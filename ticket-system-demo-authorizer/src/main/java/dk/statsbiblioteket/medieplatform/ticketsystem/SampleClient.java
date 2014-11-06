package dk.statsbiblioteket.medieplatform.ticketsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SampleClient {
	public static void main(String[] args) {
		// mvn spring-boot:run first
		Authorization auth = new Authorization("http://localhost:8080");

		Map<String, List<String>> attributes = new TreeMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("true");
		attributes.put("attribut_store.MediestreamFullAccess", l);

		List<String> r = new ArrayList<String>();
		r.add("doms_radioTVCollection:uuid:a5390b1e-69fb-47c7-b23e-7831eb59479d");
		r.add("doms_reklamefilm:uuid:35a1aa76-97a1-4f1b-b5aa-ad2a246eeeec");

		// Currently fails with: Exception in thread "main" com.sun.jersey.api.client.UniformInterfaceException: POST http://localhost:8080/checkAccessForIds returned a response status of 400 Bad Request

		System.out.println(auth.authorizeUser(attributes, "images", r));
	}
}
