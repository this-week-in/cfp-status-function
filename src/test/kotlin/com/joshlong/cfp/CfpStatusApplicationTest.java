package com.joshlong.cfp;

import com.amazonaws.services.lambda.runtime.Context;
import com.joshlong.cfp.CfpStatusApplication;
import com.joshlong.cfp.CfpStatusHandler;
import com.joshlong.cfp.CfpStatusRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class CfpStatusApplicationTest {

	private final Log log = LogFactory.getLog(getClass());

	@Test
	public void testRunningTheApplication() {
		CfpStatusHandler h = new CfpStatusHandler(CfpStatusApplication.class);
		Context mock = Mockito.mock(Context.class);
		Object handleRequest = h.handleRequest(new CfpStatusRequest("4544232b0efcd3a4a6e3df4c76e42430"), mock);
		log.info("response: " + handleRequest);
	}
}
