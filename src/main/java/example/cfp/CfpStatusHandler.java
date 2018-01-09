package example.cfp;

import lombok.extern.java.Log;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class CfpStatusHandler extends SpringBootRequestHandler<CfpStatusRequest, CfpStatusResponse> {

	public CfpStatusHandler(Class<?> configurationClass) {
		super(configurationClass);
	}

	public CfpStatusHandler() {
		super();
	}
}
