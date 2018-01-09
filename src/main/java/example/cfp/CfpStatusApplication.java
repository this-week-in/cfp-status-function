package example.cfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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

	@Bean
	Function<CfpStatusRequest, CfpStatusResponse> function(CfpStatusService service) {
		return service::processCfpStatusRequest;
	}
}

@Log
@Service
class CfpStatusService {

	private final PinboardClient client;

	private static final String CFP_TAG = "cfp";

	public CfpStatusService(PinboardClient client) {
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

		if (null == incoming) incoming = new String[0];

		Set<String> x = new HashSet<>();
		x.addAll(Arrays.asList(incoming));
		x.addAll(Arrays.asList(extra));
		return x.toArray(new String[x.size()]);
	}

	CfpStatusResponse processCfpStatusRequest(CfpStatusRequest request) {
		try {
			Assert.notNull(request, "you must provide a valid " + CfpStatusRequest.class.getName() + ".");
			Map<String, Bookmark> bookmarks = this.bookmarks();
			bookmarks.forEach((k, v) -> log.info(k + '=' + v.getHref()));
			Assert.hasText(request.getId(), "the ID must be a valid ID");
			Bookmark bookmark = bookmarks.get(request.getId());
			Assert.notNull(bookmark, "couldn't find the `Bookmark` with ID " + request.getId());
			log.info("updating CFP (" + bookmark.getHref() + ") having name " + bookmark.getDescription());
			String[] tags = addTags(bookmark.getTags(), yearTag());
			this.client.addPost(bookmark.getHref(), bookmark.getDescription(), bookmark.getDescription(),
					tags, bookmark.getTime(), true, false, false);
			return new CfpStatusResponse(true);
		}
		catch (Exception e) {
			String msg = NestedExceptionUtils.buildMessage("couldn't process the CFP status update", e);
			log.info(msg);
			return new CfpStatusResponse(false);
		}
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

