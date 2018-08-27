#!/usr/bin/env bash

should_build=${1:-echo "we assume you\'ve already built the artifact to be deployed."}
eval $should_build
zip myfile.zip deployment.zip

jar=`pwd`/target/cfp-status-function-0.0.1-SNAPSHOT-aws.jar
ls -la $jar
zip_name=deployment.zip
zip=`pwd`/$zip_name

zip  $zip $jar


aws s3 cp $zip s3://cfp-status-function/$zip_name

stack_name=greetings-stack

aws cloudformation delete-stack --stack-name $stack_name --region us-east-1

sleep 10

aws cloudformation create-stack --stack-name $stack_name  --template-body file://./cloudformation.template  --capabilities CAPABILITY_IAM --region us-east-1
aws cloudformation list-stacks
echo "S3: $s3_put "
echo "Cloud Formation:  $result "