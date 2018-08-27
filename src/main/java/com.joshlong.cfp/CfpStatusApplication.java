package com.joshlong.cfp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pinboard.PinboardClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@SpringBootApplication
public class CfpStatusApplication {

		private final Log log = LogFactory.getLog(getClass());

		//	@Bean
		CfpStatusService cfpStatusService(PinboardClient client) {
				return new CfpStatusService(client);
		}

		@Data
		public static class LambdaProxyWrapper<T> {

				private final String body;
				private final int statusCode;
				private final boolean isBase64Encoded;
				private final Map<String, Object> headers = new ConcurrentHashMap<>();

				public static <T> LambdaProxyWrapper<T> from(T response) {
						return new LambdaProxyWrapper<T>(response, HttpStatus.OK, new HashMap<>(), false);
				}

				public static <T> LambdaProxyWrapper<T> from(ResponseEntity<T> response) {
						return new LambdaProxyWrapper<T>(
							response.getBody(), response.getStatusCode().value(), response.getHeaders().toSingleValueMap(), false);
				}

				public boolean getIsBase64Encoded() {
						return this.isBase64Encoded;
				}

				LambdaProxyWrapper(T body, int status, Map<String, String> headers, boolean b64) {
						try {
								this.body = new ObjectMapper().writeValueAsString(body);
						}
						catch (JsonProcessingException e) {
								throw new RuntimeException(e);
						}
						this.statusCode = status;
						this.isBase64Encoded = b64;
						this.headers.putAll(headers);
				}

				LambdaProxyWrapper(T body, HttpStatus status, Map<String, String> headers, boolean b64) {
						this(body, status.value(), headers, b64);
				}
		}

		@Bean
		Function<LinkedHashMap<String, Object>, LambdaProxyWrapper<CfpStatusResponse>> function() {
				return request -> {
						request.forEach((k, v) -> this.log.info(k + "=" + v));
						return LambdaProxyWrapper.from(new CfpStatusResponse(true));
				};
		}

		public static void main(String args[]) throws JsonProcessingException {
				SpringApplication.run(CfpStatusApplication.class, args);

/*				ObjectMapper objectMapper = new ObjectMapper();

				LambdaProxyWrapper<Map<String, Object>> value = new LambdaProxyWrapper<>(
					Collections.singletonMap("a", "value"), HttpStatus.OK, Collections.singletonMap("my_header", "my_value"), false);

				String s = objectMapper.writeValueAsString(value);

				System.out.println("JSON: " + s);*/

		}
}
