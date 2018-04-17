package tdt4140.gr18nn.web.server.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr18nn.app.core.LatLong;
import tdt4140.gr18nn.app.json.LatLongDeserializer;
import tdt4140.gr18nn.app.json.LatLongSerializer;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LatLongObjectMapperProvider implements ContextResolver<ObjectMapper> {

	private final ObjectMapper objectMapper;

	public LatLongObjectMapperProvider() {
		final SimpleModule module = new SimpleModule()
				.addSerializer(LatLong.class, new LatLongSerializer())
				.addDeserializer(LatLong.class, new LatLongDeserializer());
		objectMapper = new ObjectMapper()
				.registerModule(module);
	}

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return objectMapper;
	}
}
