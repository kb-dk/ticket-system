package dk.statsbiblioteket.medieplatform.ticketsystem;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationRequest;
import dk.statsbiblioteket.medieplatform.ticketsystem.authorization.AuthorizationResponse;

@RequestMapping("/checkAccessForIds/**")
@RestController

public class DemoAuthorizerController {

	@RequestMapping(method=RequestMethod.GET)
	public AuthorizationResponse authorize(@RequestParam(value="request") AuthorizationRequest request) {
		throw new RuntimeException("not implemented");
	}
}
