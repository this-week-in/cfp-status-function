#!/usr/bin/env bash


region=${AWS_REGION:-us-east-1}


function real_deploy (){

    stack_name=greetings-stack
    zip_name=deployment.zip
    region=${AWS_REGION:-us-east-1}

    jar=${this_dir}/target/cfp-status-function-0.0.1-SNAPSHOT-aws.jar
    zip=${this_dir}/$zip_name

    zip ${zip} ${jar}

    aws s3 cp ${zip} s3://cfp-status-function/${zip_name}
    aws cloudformation delete-stack --stack-name $stack_name --region ${region}

    sleep 10

    aws cloudformation create-stack --stack-name $stack_name  --template-body file://./cloudformation.template  --capabilities CAPABILITY_IAM --region ${region}
    aws cloudformation list-stacks

}


function deploy (){
    stack_name=greetings-stack
    cmd_to_run=""
    complete=$( aws cloudformation list-stacks | grep CREATE_COMPLETE )
    create_complete=CREATE_COMPLETE
    list_stacks=$(aws cloudformation list-stacks  |  jq '.[] | .[]  | select(.StackStatus=="CREATE_COMPLETE")  | .StackStatus')
    if [ "1${list_stacks}" == '1"CREATE_COMPLETE"' ]; then
     cmd_to_run="update"
    else
     cmd_to_run="create"
    fi
    cmd=${cmd_to_run}-stack
    echo "aws cloudformation $cmd --stack-name $stack_name .. "
    echo "Region: $region"
    echo "Stack Name: $stack_name"
    aws cloudformation $cmd --stack-name $stack_name  --template-body file://./cloudformation.template --capabilities CAPABILITY_IAM --region ${region}
}

cd `dirname $0`
this_dir=`pwd`

if [ "$1x" != "x" ]; then
    eval $1
fi

deploy