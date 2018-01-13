#!/bin/bash

clean(){
    region=${AWS_REGION}
    aws lambda list-functions --region $region | jq -r '.Functions[].FunctionName' | while read functionName ; do
        aws lambda delete-function --function-name $functionName --region $region
    done
    aws apigateway get-rest-apis --region $region | jq -r '.items[].id' | while read rest_api_id ; do
        aws apigateway delete-rest-api --region $region --rest-api-id $rest_api_id || echo "can't delete $rest_api_id ";
    done
}

deploy(){

    function_name=${1:-demo-function}
    method=ANY
    jar_name=`find . -iname "*aws.jar"`
    handler_name=example.DemoHandler
    endpoint_path_part=${function_name}
    region=${AWS_REGION}
    rest_api_name=${function_name}
    function_role=arn:aws:iam::${AWS_ACCOUNT_ID}:role/lambda-role
    function_arn=$(
        aws lambda create-function \
            --region ${region} \
            --timeout 300 \
            --function-name ${function_name} \
            --zip-file fileb://${jar_name} \
            --memory-size 512 \
            --environment Variables="{FOO=BAR}" \
            --role  ${function_role} \
            --handler ${handler_name}  \
            --runtime java8 |  jq -r '.FunctionArn'
    )
    rest_api_id=$( aws apigateway create-rest-api --name ${rest_api_name} --region ${region} | jq -r '.id' )
    resource_id=$( aws apigateway get-resources --rest-api-id ${rest_api_id} --region ${region} | jq -r '.items[].id' )
    create_resource_result=` aws apigateway create-resource --rest-api-id ${rest_api_id} --region ${region} --parent-id ${resource_id} --path-part ${function_name}  `
    path_part=$( echo ${create_resource_result}  | jq -r '.path' )
    resource_id=$( echo  ${create_resource_result} | jq -r '.id' )
    method_result=$( aws apigateway put-method --rest-api-id ${rest_api_id}  --region ${region}  --resource-id ${resource_id} --http-method ${method} --authorization-type "NONE" )
    method_response_result=$( aws apigateway put-method-response --rest-api-id ${rest_api_id} --region ${region} --resource-id ${resource_id}  --http-method ${method} --status-code 200 )
    integration_uri=arn:aws:apigateway:${region}:lambda:path/2015-03-31/functions/arn:aws:lambda:${region}:${AWS_ACCOUNT_ID}:function:${function_name}/invocations
    role_id=arn:aws:iam::960598786046:role/lambda-role
    put_integration_result=$(
        aws apigateway put-integration \
            --region ${region} \
            --rest-api-id ${rest_api_id} \
            --resource-id ${resource_id} \
            --http-method ${method} \
            --type AWS \
            --integration-http-method POST \
            --uri ${integration_uri} \
            --request-templates file://`pwd`/request-template.json \
            --credentials $role_id
    )
    put_integration_response_result=$(
        aws apigateway put-integration-response \
            --region ${region} \
            --rest-api-id ${rest_api_id} \
            --resource-id ${resource_id} \
            --http-method ANY \
            --status-code 200 \
            --selection-pattern ""
    )
    deploy=$( aws apigateway create-deployment --rest-api-id ${rest_api_id} --stage-name prod --region ${region} )
    echo https://${rest_api_id}.execute-api.${region}.amazonaws.com/prod${path_part}
}

clean
deploy cfp-stats-fn
