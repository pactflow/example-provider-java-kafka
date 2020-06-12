# Example Java Kafka Producer

[![Build Status](https://travis-ci.com/pactflow/example-provider-java-kafka.svg?token=Kys1s4kjp8EWzwqKWwhy&branch=master)](https://travis-ci.com/pactflow/example-provider-java-kafka)

This is an example of a Java Spring Boot Kafka Producer that uses Pact, [Pactflow](https://pactflow.io) and Travis CI to ensure that it is compatible with the expectations its consumers have of it.

See the canonical provider example here: https://github.com/pactflow/example-provider
See also the full [Pactflow CI/CD Workshop](https://github.com/pactflow/ci-cd-workshop) for which this can be substituted in as the "provider".

In the following diagram, we'll be testing the "Event API", a service that publishes product events to a Kafka stream on the `product` topic.

![Kafka Architecture](docs/kafka.png "Kafka Architecture")

## Pre-requisites

**Software**:

* Tools listed at: https://docs.pactflow.io/docs/workshops/ci-cd/set-up-ci/prerequisites/
* Java (11+)
* A pactflow.io account with an valid [API token](https://docs.pactflow.io/docs/getting-started/#configuring-your-api-token)

### Environment variables

To be able to run some of the commands locally, you will need to export the following environment variables into your shell:

* `PACT_BROKER_TOKEN`: a valid [API token](https://docs.pactflow.io/docs/getting-started/#configuring-your-api-token) for Pactflow
* `PACT_BROKER_BASE_URL`: a fully qualified domain name with protocol to your pact broker e.g. https://dius.pactflow.io
* `PACT_BROKER_HOST`: a fully qualified domain name _without_ protocol to your pact broker e.g. dius.pactflow.io

## Usage

* Running tests: `./gradlew clean test`
* Start a Kafka cluster, and publish events from the producer into the `product` topic: `make start`