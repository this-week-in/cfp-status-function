package com.joshlong.cfp

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.NestedExceptionUtils
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import pinboard.Bookmark
import pinboard.PinboardClient
import java.time.Instant
import java.time.ZoneId
import java.util.*

@SpringBootApplication
class CfpStatusApplication {

	@Bean
	fun function(service: CfpStatusService) =
			java.util.function.Function<CfpStatusRequest, CfpStatusResponse> {
				service.processCfpStatusRequest(it)
			}
}

@Service
class CfpStatusService(private val client: PinboardClient) {

	private val CFP_TAG = "cfp"

	private val log = LogFactory.getLog(javaClass)

	private fun log(msg: String) {
		println(msg)
		log.info(msg)
	}

	fun processCfpStatusRequest(request: CfpStatusRequest): CfpStatusResponse {
		try {
			Assert.notNull(request, "you must provide a valid ${CfpStatusRequest::class.java.name}.")
			val currentYearTag: String = Integer.toString(Instant.now().atZone(ZoneId.systemDefault()).year)
			val bookmarks: Map<String, Bookmark> = this.client
					.getAllPosts(arrayOf(CFP_TAG), 0, 100, null, null, 0)
					.filter { !it.tags.contains(currentYearTag) }
					.map { Pair(it.hash!!, it) }
					.toMap()
			log("there are ${bookmarks.size} bookmarks returned.")
			Assert.hasText(request.id, "the ID must be a valid ID")
			log("the incoming ID is: ${request.id}")
			val bookmark = bookmarks[request.id]
			if (bookmark != null) {
				Assert.notNull(bookmark, "couldn't find the `Bookmark` with ID " + request.id!!)
				log("the incoming ID maps to an actual bookmark.")
				val tags = addTags(bookmark.tags, currentYearTag)
				this.client.addPost(bookmark.href!!, bookmark.description!!, bookmark.description!!,
						tags, bookmark.time!!, true, false, false)
				log("updated the post with ID ${request.id}.")
				return CfpStatusResponse(true)
			}
		}
		catch (e: Exception) {
			val message = NestedExceptionUtils.buildMessage("couldn't process the CFP status update", e)
			log(message)
			log.error(message, e)
		}
		return CfpStatusResponse(false)
	}

	private fun addTags(i: Array<String>?, vararg extra: String) =
			(i ?: arrayOf()).toList().plus(Arrays.asList(*extra)).toTypedArray()

}

class CfpStatusResponse(var processed: Boolean? = false)
class CfpStatusRequest(var id: String? = null)