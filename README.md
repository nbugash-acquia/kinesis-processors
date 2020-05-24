# Processor POC
## Overview
This is a prototype project for improving the kinesis processors. The POC shows how to use
reactive programming to improve scalability by introducing a few concepts that reactive programming
solves over the traditional imperative programming.

## Setting up the project
### Requirement
1. Terraform
2. AWS account
3. Maven
4. Java 14
5. Spring 5.x
6. Spring Boot 2.3.x
7. Spring Cloud Stream 2.0.1.RELEASE
8. Docker (Local development)


## AWS Resources
The terraform scripts generates the following aws resources:
* kinesis streams (dynamodb, s3, redshift)
* S3 bucket 
* dynamodb table

### Infrastructure
In order to run the application, there are certain AWS resources that need to run / spin up.
    
    >> cd $PROJECT_ROOT/infra
    >> terraform apply

### Api
The api application generates captures and push them down to the `dynamodb` kinesis stream.
It produces a simple Capture object with the following attributes:
* `id` of type `Long`
* `data` of type `String`
* `accountId` of type `String`

## Processors
The processors are written in Java 14 using the Spring 5.x framework.

### Dynamodb processors
It reads from the `dynamodb` stream, saves each capture to dynamodb, and pushes the capture to the `s3` stream

To run the application: 

    >> cd $PROJECT_ROOT/dynamodb-processor ;\
    >> AWS_ACCESS_KEY="<accesskey>"; AWS_SECRET_KEY="<secretkey>"; AWS_PROFILE="<aws profile>"; mvn clean spring-boot:run ;\

### S3 processors
The S3 application reads capture from the `s3` stream. It saves each capture to a bucket in s3 called `titan-s3-bucket`.
Before uploading the object to s3, it tags each key to `STATUS=UNPROCESSED`. Then it pushes the s3key to the `redshift` stream

To run the application:

    >> cd $PROJECT_ROOT/s3-processor ;\
    >> AWS_ACCESS_KEY="<accesskey>"; AWS_SECRET_KEY="<secretkey>"; AWS_PROFILE="<aws profile>"; mvn clean spring-boot:run ;\

### Redshift processor
The Redshift processors reads the s3key from the `redshift` stream. Once it receives the s3key, it'll perform a sleep to simulate
a long running process. Afterwards, it changes the tag from `STATUS=UNPROCESSED` to `STATUS=PROCESSED`.

    >> cd $PROJECT_ROOT/redshift-processor ;\
    >> AWS_ACCESS_KEY="<accesskey>"; AWS_SECRET_KEY="<secretkey>"; AWS_PROFILE="<aws profile>"; mvn clean spring-boot:run ;\
