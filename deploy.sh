#!/usr/bin/env bash

this_dir=`dirname $0`
mkdir -p  ${this_dir}/target
tmp_dir=${this_dir}/target
mvn -DskipTests=true clean package

zip_name=deployment.zip
zip=${tmp_dir}/${zip_name}
template_output=${tmp_dir}/output.yaml
bucket=cfp-status-function
jar=${tmp_dir}/cfp-status-function-0.0.1-SNAPSHOT.jar

zip ${zip} ${jar}
aws s3 cp ${zip} s3://${bucket}/${zip_name}

stack_name=cfp-status-function

sam package --template-file ${this_dir}/template.yaml  --output-template-file ${template_output} --s3-bucket ${bucket}
#sam deploy --template-file ${template_output} --stack-name new-stack-name --capabilities CAPABILITY_IAM
aws cloudformation deploy --template-file ${template_output} --stack-name $stack_name --capabilities CAPABILITY_IAM
