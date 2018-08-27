package com.joshlong.cfp;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pinboard.Bookmark;
import pinboard.PinboardClient;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	*/

  class CfpStatusService {

		private final PinboardClient pinboardClient;

		private final static String CFP_TAG = "cfp";

		CfpStatusService(PinboardClient pinboardClient) {
				this.pinboardClient = pinboardClient;
		}

		CfpStatusResponse processCfpStatusRequest(CfpStatusRequest request) {

				Assert.notNull(request, "you must provide a valid " + CfpStatusRequest.class.getName() + '.');

				String currentYearTag = Integer.toString(Instant.now().atZone(ZoneId.systemDefault()).getYear());

				Map<String, Bookmark> bookmarks = Arrays
					.stream(this.pinboardClient.getAllPosts(new String[]{CFP_TAG}, 0, 100, null, null, 0))
					.filter(it -> !Arrays.asList(it.getTags()).contains(currentYearTag))
					.collect(Collectors.toMap(Bookmark::getHash, it -> it));

				Assert.hasText(request.getId(), "the ID must be a valid ID");

				Bookmark bookmark = bookmarks.get(request.getId());

				return Optional
					.ofNullable(bookmark).map(b -> {
							String[] tags = addTags(b.getTags(), currentYearTag);
							boolean added = this.pinboardClient.addPost(b.getHref(), b.getDescription(), b.getDescription(), tags, b.getTime(), true, false, false);
							return new CfpStatusResponse(added);
					})
					.orElse(new CfpStatusResponse(false));
		}

		private String[] addTags(String[] i, String... extra) {
				List<String> tags = new ArrayList<>();
				tags.addAll(Arrays.asList(i));
				tags.addAll(Arrays.asList(extra));
				return tags.toArray(new String[0]);
		}
}
