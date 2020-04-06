package com.joshlong.cfp;

import com.amazonaws.services.lambda.runtime.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class CfpStatusApplicationTest {

	@Test
	@Ignore
	public void testRunningTheApplication() {
		Log log = LogFactory.getLog(getClass());
		CfpStatusHandler cfpStatusHandler = new CfpStatusHandler();
		Context mock = Mockito.mock(Context.class);
		Object handleRequest = cfpStatusHandler.handleRequest(new CfpStatusRequest("4544232b0efcd3a4a6e3df4c76e42430"), mock);
		log.info("response: " + handleRequest);
	}
}
