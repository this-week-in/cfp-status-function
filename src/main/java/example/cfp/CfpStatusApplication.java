package example.cfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import pinboard.Bookmark;
import pinboard.PinboardClient;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

	private final PinboardClient client;
	private static final String CFP_TAG = "cfp";

	public CfpStatusApplication(PinboardClient client) {
		this.client = client;
	}

	private String yearTag() {
		return Integer.toString(
				Instant.now().atZone(ZoneId.systemDefault()).getYear());
	}

	private Map<String, Bookmark> bookmarks() {
		Bookmark[] allPosts = client.getAllPosts(
				new String[]{CFP_TAG}, 0, 100, null, null, 0);
		return Stream
				.of(allPosts)
				.filter(bookmark -> Stream
						.of(bookmark.getTags())
						.filter(t -> t.equalsIgnoreCase(yearTag()))
						.count() == 0
				)
				.collect(Collectors.toMap(Bookmark::getHash, x -> x));
	}

	private String[] addTags(String[] incoming, String... extra) {
		Set<String> x = new HashSet<>();
		x.addAll(Arrays.asList(incoming));
		x.addAll(Arrays.asList(extra));
		return x.toArray(new String[x.size()]);
	}

	private CfpStatusResponse process(CfpStatusRequest request) {
		Map<String, Bookmark> bookmarks = this.bookmarks();
		bookmarks.forEach((k, v) -> log.info(k + '=' + v.getHref()));
		if (request != null && StringUtils.hasText(request.getId())) {
			Bookmark bookmark = bookmarks.get(request.getId());
			log.info("updating CFP (" + bookmark.getHref() + ")" +
					" having name " + bookmark.getDescription());
			String[] tags = addTags(bookmark.getTags(), yearTag());
			this.client.addPost(bookmark.getHref(), bookmark.getDescription(), bookmark.getDescription(),
					tags, bookmark.getTime(), true, false, false);
		}
		return new CfpStatusResponse(true);
	}

	/*
	@Bean
	ApplicationRunner run() {
		return args -> this.process(new CfpStatusRequest("4544232b0efcd3a4a6e3df4c76e42430"));
	}
	*/

	@Bean
	Function<CfpStatusRequest, CfpStatusResponse> function() {
		return this::process;
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

	private String id;
}

