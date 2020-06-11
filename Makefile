# Default to the read only token - the read/write token will be present on Travis CI.
# It's set as a secure environment variable in the .travis.yml file

PACTICIPANT := "pactflow-example-provider-java-kafka"
WEBHOOK_UUID := "9GS-Z8nSAbUzvJW4xmhdsg"
TRIGGER_PROVIDER_BUILD_URL := "https://api.travis-ci.com/repo/pactflow%2Fexample-provider-springboot/requests"

# Only deploy from master
ifeq ($(TRAVIS_BRANCH),master)
	DEPLOY_TARGET=deploy
else
	DEPLOY_TARGET=no_deploy
endif

all: test

## ====================
## CI tasks
## ====================

ci: test $(DEPLOY_TARGET)

# Run the ci target from a developer machine with the environment variables
# set as if it was on Travis CI.
# Use this for quick feedback when playing around with your workflows.
fake_ci: .env
	CI=true \
	TRAVIS_COMMIT=`git rev-parse --short HEAD`+`date +%s` \
	TRAVIS_BRANCH=`git rev-parse --abbrev-ref HEAD` \
	PACT_BROKER_PUBLISH_VERIFICATION_RESULTS=true \
	make ci

ci_webhook: .env
	./gradlew clean test -i

fake_ci_webhook:
	CI=true \
	TRAVIS_COMMIT=`git rev-parse --short HEAD`+`date +%s` \
	TRAVIS_BRANCH=`git rev-parse --abbrev-ref HEAD` \
	PACT_BROKER_PUBLISH_VERIFICATION_RESULTS=true \
	make ci_webhook

## =====================
## Build/test tasks
## =====================

test: .env
	./gradlew clean test -i

## =====================
## Deploy tasks
## =====================

deploy: can_i_deploy deploy_app tag_as_prod

no_deploy:
	@echo "Not deploying as not on master branch"

can_i_deploy: .env
	@echo docker run --rm \
	 --env-file .env \
	 -e PACT_BROKER_BASE_URL \
	 -e PACT_BROKER_TOKEN \
	  pactfoundation/pact-cli:latest \
	  broker can-i-deploy \
	  --pacticipant ${PACTICIPANT} \
	  --version ${TRAVIS_COMMIT} \
	  --to prod

deploy_app:
	@echo "Deploying to prod"

tag_as_prod:
	@docker run --rm \
	 --env-file .env \
	 -e PACT_BROKER_BASE_URL \
	 -e PACT_BROKER_TOKEN \
	  pactfoundation/pact-cli:latest \
	  broker create-version-tag \
	  --pacticipant ${PACTICIPANT} \
	  --version ${TRAVIS_COMMIT} \
	  --tag prod

## =====================
## Pactflow set up tasks
## =====================

# export the TRAVIS_TOKEN environment variable before running this
# You can get your token from the Settings tab of https://travis-ci.com/account/preferences
create_travis_token_secret:
	@curl -v -X POST ${PACT_BROKER_BASE_URL}/secrets \
	-H "Authorization: Bearer ${PACT_BROKER_TOKEN}" \
	-H "Content-Type: application/json" \
	-H "Accept: application/hal+json" \
	-d  "{\"name\":\"travisToken\",\"description\":\"Travis CI Provider Build Token\",\"value\":\"${TRAVIS_TOKEN}\"}"

# NOTE: the travis token secret must be created (either through the UI or using the
# `create_travis_token_secret` target) before the webhook is invoked.
create_or_update_travis_webhook:
	@docker run --rm \
	 -e PACT_BROKER_BASE_URL \
	 -e PACT_BROKER_TOKEN \
	 -v ${PWD}:${PWD} \
	  pactfoundation/pact-cli:latest \
	  broker create-or-update-webhook \
	  "${TRIGGER_PROVIDER_BUILD_URL}" \
	  --header "Content-Type: application/json" "Accept: application/json" "Travis-API-Version: 3" 'Authorization: token $${user.travisToken}' \
	  --request POST \
	  --data @${PWD}/pactflow/travis-ci-webhook.json \
	  --uuid ${WEBHOOK_UUID} \
	  --provider ${PACTICIPANT} \
	  --contract-content-changed \
	  --description "Travis CI webhook for ${PACTICIPANT}"

test_travis_webhook:
	@docker run --rm \
	 -e PACT_BROKER_BASE_URL \
	 -e PACT_BROKER_TOKEN \
	 -v ${PWD}:${PWD} \
	  pactfoundation/pact-cli:latest \
	  broker test-webhook \
	  --uuid ${WEBHOOK_UUID}

## ======================
## Travis CI set up tasks
## ======================

travis_login:
	@docker run --rm -v ${HOME}/.travis:/root/.travis -it lirantal/travis-cli login --pro

travis_encrypt_pact_broker_token:
	@docker run --rm -v ${HOME}/.travis:/root/.travis -v ${PWD}:${PWD} --workdir ${PWD} lirantal/travis-cli encrypt --pro PACT_BROKER_TOKEN="${PACT_BROKER_TOKEN}"

## ======================
## Misc
## ======================

.env:
	touch .env

docker-logs:
	@docker-compose -f kafka-cluster.yml logs -f

docker-rm:
	@docker-compose -f kafka-cluster.yml rm -vfs

docker-stop:
	@docker-compose -f kafka-cluster.yml stop

docker:
	@docker-compose -f kafka-cluster.yml up -d --no-recreate

start: docker
	@echo "starting kafka cluster and producer"
	./gradlew bootRun
