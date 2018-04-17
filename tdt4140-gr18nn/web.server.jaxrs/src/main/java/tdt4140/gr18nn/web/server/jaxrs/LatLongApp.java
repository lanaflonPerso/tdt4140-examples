package tdt4140.gr18nn.web.server.jaxrs;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import tdt4140.gr18nn.app.core.LatLong;

public class LatLongApp extends ResourceConfig {

	public LatLongApp() {
		packages("tdt4140.gr18nn.web.server.jaxrs");
		register(LatLongService.class);
		register(LatLongObjectMapperProvider.class);
		register(JacksonFeature.class);

		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(LatLongApp.this);
			}
		});
	}

	final List<LatLong> geoLocations = new ArrayList<>();
}
