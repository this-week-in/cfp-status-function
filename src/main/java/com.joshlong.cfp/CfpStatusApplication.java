package com.joshlong.cfp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
@SpringBootApplication
public class CfpStatusApplication {

		@Bean
		Function<CfpStatusRequest, CfpStatusResponse> function(CfpStatusService statusService) {
				return statusService::processCfpStatusRequest;
		}

		public static void main(String args[]) {
				SpringApplication.run(CfpStatusApplication.class, args);
		}
}
