package example.cfp

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler

class CfpStatusHandler : SpringBootRequestHandler<CfpStatusRequest, CfpStatusResponse> {

	constructor(configurationClass: Class<*>) : super(configurationClass)

	constructor() : super()
}
