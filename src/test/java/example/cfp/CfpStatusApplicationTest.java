package example.cfp;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.extern.java.Log;
import org.junit.Test;
import org.mockito.Mockito;

@Log
public class CfpStatusApplicationTest {

	@Test
	public void testRunningTheApplication() {
		CfpStatusHandler h = new CfpStatusHandler(CfpStatusApplication.class);
		Context mock = Mockito.mock(Context.class);
		Object handleRequest = h.handleRequest(new CfpStatusRequest("4544232b0efcd3a4a6e3df4c76e42430"), mock);
		log.info("handle request: " + handleRequest);
	}
}
