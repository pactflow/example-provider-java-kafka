# Default to the read only token - the read/write token will be present on GitHub Actions.
# It's set as a secure environment variable in the build.yml file

PACTICIPANT := "pactflow-example-provider-java-kafka"
WEBHOOK_UUID := "9GS-Z8nSAbUzvJW4xmhdsg"
PACT_CHANGED_WEBHOOK_UUID := "c76b601e-d66a-4eb1-88a4-6ebc50c0df8b"
PACT_CLI="docker run --rm -v ${PWD}:${PWD} -e PACT_BROKER_BASE_URL -e PACT_BROKER_TOKEN pactfoundation/pact-cli"

# Only deploy from master
ifeq ($(GIT_BRANCH),master)
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
# set as if it was on GitHub Actions.
# Use this for quick feedback when playing around with your workflows.
fake_ci: .env
	CI=true \
	GIT_COMMIT=`git rev-parse --short HEAD`+`date +%s` \
	GIT_BRANCH=`git rev-parse --abbrev-ref HEAD` \
	PACT_BROKER_PUBLISH_VERIFICATION_RESULTS=true \
	make ci

ci_webhook: .env
	./gradlew clean test -i

fake_ci_webhook:
	CI=true \
	GIT_COMMIT=`git rev-parse --short HEAD`+`date +%s` \
	GIT_BRANCH=`git rev-parse --abbrev-ref HEAD` \
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

deploy: deploy_app tag record_deployment

no_deploy:
	@echo "Not deploying as not on master branch"

can_i_deploy: .env
	echo "can_i_deploy"
	@"${PACT_CLI}" broker can-i-deploy \
	  --pacticipant ${PACTICIPANT} \
	  --version ${GIT_COMMIT} \
	  --to-environment production \
	  --retry-while-unknown 0 \
	  --retry-interval 10

deploy_app:
	@echo "Deploying to prod"

tag: .env
	@"${PACT_CLI}" broker create-version-tag \
	  --pacticipant ${PACTICIPANT} \
	  --version ${GIT_COMMIT} \
		--auto-create-version \
	  --tag ${GIT_BRANCH}

record_deployment: .env
	@"${PACT_CLI}" broker record-deployment --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --environment production

## =====================
## Pactflow set up tasks
## =====================

# export the GITHUB_TOKEN environment variable before running this
create_github_token_secret:
	curl -v -X POST ${PACT_BROKER_BASE_URL}/secrets \
	-H "Authorization: Bearer ${PACT_BROKER_TOKEN}" \
	-H "Content-Type: application/json" \
	-H "Accept: application/hal+json" \
	-d  "{\"name\":\"githubToken\",\"description\":\"Github token\",\"value\":\"${GITHUB_TOKEN}\"}"

# NOTE: the github token secret must be created (either through the UI or using the
# `create_github_token_secret` target) before the webhook is invoked.
create_or_update_pact_changed_webhook:
	"${PACT_CLI}" \
	  broker create-or-update-webhook \
	  "https://api.github.com/repos/${GITHUB_REPO}/dispatches" \
	  --header 'Content-Type: application/json' 'Accept: application/vnd.github.everest-preview+json' 'Authorization: Bearer $${user.githubToken}' \
	  --request POST \
	  --data '{ "event_type": "pact_changed", "client_payload": { "pact_url": "$${pactbroker.pactUrl}" } }' \
	  --uuid ${PACT_CHANGED_WEBHOOK_UUID} \
	  --consumer ${PACTICIPANT} \
	  --contract-content-changed \
	  --description "Pact content changed for ${PACTICIPANT}"

test_pact_changed_webhook:
	@curl -v -X POST ${PACT_BROKER_BASE_URL}/webhooks/${PACT_CHANGED_WEBHOOK_UUID}/execute -H "Authorization: Bearer ${PACT_BROKER_TOKEN}"


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
