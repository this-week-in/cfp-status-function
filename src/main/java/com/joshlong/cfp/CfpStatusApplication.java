package com.joshlong.cfp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import pinboard.PinboardClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@SpringBootApplication
public class CfpStatusApplication {

		private final ObjectMapper objectMapper;

		public CfpStatusApplication(ObjectMapper objectMapper) {
				this.objectMapper = objectMapper;
		}

		@Bean
		CfpStatusService cfpStatusService(PinboardClient client) {
				return new CfpStatusService(client);
		}

		@Bean
		Function<Map<String, Object>, Map<String, Object>> function(CfpStatusService svc) {
				return request -> {
						Map<String, Object> queryStringParameters = (Map<String, Object>) request.get("queryStringParameters");
						String id = (String) queryStringParameters.get("id");
						CfpStatusResponse cfpStatusResponse = svc.processCfpStatusRequest(new CfpStatusRequest(id));
						return this.buildAwsLambdaProxyResponse(cfpStatusResponse);
				};
		}

		private Map<String, Object> buildAwsLambdaProxyResponse(Object response) {
				try {
						Map<String, Object> gatewayResponse = new HashMap<>();
						gatewayResponse.put("statusCode", HttpStatus.OK.value());
						gatewayResponse.put("isBase64Encoded", false);
						gatewayResponse.put("body", this.objectMapper.writeValueAsString(response));
						gatewayResponse.put("headers", new HashMap<>());
						return gatewayResponse;
				}
				catch (JsonProcessingException e) {
						throw new RuntimeException(e);
				}
		}

		public static void main(String[] args) {
				SpringApplication.run(CfpStatusApplication.class, args);
		}
}
