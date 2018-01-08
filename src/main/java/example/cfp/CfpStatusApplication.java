package example.cfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pinboard.Bookmark;
import pinboard.PinboardClient;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This marks certain CFP links as processed.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log
@SpringBootApplication
public class CfpStatusApplication {

	private static final String CFP_TAG = "cfp";

	private final Map<String, Bookmark> bookmarks = new ConcurrentHashMap<>();

	@Bean
	ApplicationRunner run(PinboardClient client) {
		return args -> {

			Bookmark[] allPosts = client.getAllPosts(new String[]{CFP_TAG}, 0, 100, null, null, 0);

			String year = Integer.toString(
					Instant.now().atZone(ZoneId.systemDefault()).getYear());

			List<Bookmark> stream = Stream
					.of(allPosts)
					.filter(bookmark -> Stream
							.of(bookmark.getTags())
							.filter(t -> t.equalsIgnoreCase(year))
							.count() == 0
					)
					.collect(Collectors.toList());

			stream
					.forEach(bookmark -> this.bookmarks.putIfAbsent(bookmark.getHash(), bookmark));

			this.bookmarks.forEach((k, bookmark) ->
					log.info(bookmark.getMeta() + ' ' + bookmark.getHash() + ' ' +
							bookmark.getExtended() + ' ' + bookmark.getTime()));

		};
	}


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

