package example.cfp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import java.util.function.Function

@SpringBootApplication
class CfpStatusApplication

data class CfpStatusRequest(val url: String)

data class CfpStatusResponse(val processed: Boolean)

fun main(args: Array<String>) {
	SpringApplicationBuilder()
			.initializers(beans {
				bean(name = "function") {
					Function<CfpStatusRequest, CfpStatusResponse> { request ->
						println("incoming URL is ${request.url}")
						CfpStatusResponse(true)
					}
				}
			})
			.sources(CfpStatusApplication::class.java)
			.run(*args)
}