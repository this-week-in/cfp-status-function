package com.joshlong.cfp;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/
public class CfpStatusHandler //extends SpringBootApiGatewayRequestHandler {
	extends SpringBootRequestHandler<CfpStatusRequest, CfpStatusResponse> {

		public CfpStatusHandler() {
				super(CfpStatusApplication.class);
		}
}