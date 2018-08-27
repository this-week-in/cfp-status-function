#!/usr/bin/env bash

function_role=arn:aws:iam::960598786046:role/service-role/cfp-status-function-role


aws lambda create-function \
 --function-name cfp-status-function-test \
 --role ${function_role} \
 --zip-file fileb://./target/cfp-status-function-0.0.1-SNAPSHOT-aws.jar \
 --handler com.joshlong.cfp.CfpStatusHandler  \
 --description "Spring Cloud Function Adapter Example" --runtime java8 --region us-east-1 --timeout 30 --memory-size 1024 \
 --publish

