# Example Java Kafka Producer

![Build](https://github.com/pactflow/example-provider-java-kafka/workflows/Build/badge.svg)

[![Can I deploy Status](https://testdemo.pactflow.io/pacticipants/pactflow-example-provider-java-kafka/branches/master/latest-version/can-i-deploy/to-environment/production/badge.svg)](https://testdemo.pactflow.io/overview/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-js-kafka)

[![Pact Status](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-java-kafka/latest/badge.svg)](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-java-kafka/latest) (latest pact)

[![Pact Status](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-js-kafka/latest/badge)](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-js-kafka/latest) (latest pact)

[![Pact Status](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-java-kafka/latest/master/badge.svg)](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-java-kafka/latest/master) (master/master pact)

[![Pact Status](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-js-kafka/latest/master/badge.svg)](https://testdemo.pactflow.io/pacts/provider/pactflow-example-provider-java-kafka/consumer/pactflow-example-consumer-js-kafka/latest/master) (master/master pact)

This is an example of a Java Spring Boot Kafka Producer that uses Pact, [Pactflow](https://pactflow.io) and GitHub Actions to ensure that it is compatible with the expectations its consumers have of it.

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
* Start a Kafka cluster, and setup the producer endpoint (enables `POST` to `localhost:8081/products`): `make start`
* Create a new event (manually):

    ```
    curl -X POST -H"Content-Type: application/json" localhost:8081/products -d '{
      "id": "7e54c13c-e28e-41fc-b34d-99de62db4666",
      "name": "Unbranded Plastic Tuna",
      "type": "BACON",
      "event": "UPDATED",
      "version": "v1"
    }'
    ```

To disable test data generation that puts random events onto `product` topic, prefix with `SEND_TEST_EVENTS=false`. e.g. `SEND_TEST_EVENTS=false make start`
