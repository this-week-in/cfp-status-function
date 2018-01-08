package example.cfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

/**
 * This marks certain CFP links as processed.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log
@SpringBootApplication
public class CfpStatusApplication {

	@Bean
	Function<CfpStatusRequest, CfpStatusResponse> function() {
		return request -> {
			log.info("processing " + request.getUrl() + ".");
			return new CfpStatusResponse(true);
		};
	}

	public static void main(String args[]) {
		SpringApplication.run(CfpStatusApplication.class, args);
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CfpStatusResponse {

	private boolean processed;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CfpStatusRequest {

	private String url;
}

