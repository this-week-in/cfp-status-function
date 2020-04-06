#!/usr/bin/env bash

#echo "announcing the current botocore version."
#python -c "import botocore; print('%s: %s' % (botocore.__file__, botocore.__version__))"

this_dir=`dirname $0`
mkdir -p  ${this_dir}/target
tmp_dir=${this_dir}/target

zip_name=deployment.zip
zip=${tmp_dir}/${zip_name}
template_output=${tmp_dir}/output.yaml
bucket=cfp-status-function-bucket
jar=${tmp_dir}/cfp-status-function-0.0.1-SNAPSHOT.jar

zip ${zip} ${jar}
aws s3 mb  s3://${bucket}
aws s3 cp ${zip} s3://${bucket}/${zip_name}

stack_name=CfpStatusFunction

sam package --template-file ${this_dir}/template.yaml --output-template-file ${template_output} --s3-bucket ${bucket}
aws cloudformation deploy --template-file ${template_output} --stack-name $stack_name --capabilities CAPABILITY_IAM --region $AWS_REGION

fn_name=$( aws lambda list-functions  --region $AWS_REGION | jq '.Functions[].FunctionName' -r | grep ${stack_name} | uniq )
aws lambda update-function-configuration  --region $AWS_REGION --function-name ${fn_name} --environment Variables={PINBOARD_TOKEN=${PINBOARD_TOKEN}}