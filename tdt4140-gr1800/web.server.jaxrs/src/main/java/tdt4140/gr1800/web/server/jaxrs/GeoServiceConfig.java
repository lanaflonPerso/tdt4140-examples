package tdt4140.gr1800.web.server.jaxrs;

import java.sql.SQLException;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import tdt4140.gr1800.app.db.DbAccessImpl;
import tdt4140.gr1800.app.db.IDbAccess;

public class GeoServiceConfig extends ResourceConfig {

	public GeoServiceConfig() {

		packages("tdt4140.gr1800.web.server.jaxrs");
		register(GeoService.class);
		register(GeoObjectMapperProvider.class);
		register(JacksonFeature.class);


		// https://stackoverflow.com/questions/16216759/dependency-injection-with-jersey-2-0
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(getDbAccess()).to(IDbAccess.class);
			}
		});
	}


	private IDbAccess dbAccess;

	public IDbAccess getDbAccess() {
		if (dbAccess == null) {
			String dbConnectionUrl = "jdbc:hsqldb:mem:" + getClass().getName();
			final Object dbConnectionParam = getProperty("dbConnectionURL");
			if (dbConnectionParam != null) {
				dbConnectionUrl = String.valueOf(dbConnectionParam);
			}
			try {
				final DbAccessImpl dbAccess = new DbAccessImpl(dbConnectionUrl);
				dbAccess.executeStatements("schema.sql", false);
				this.dbAccess = dbAccess;
			} catch (final SQLException e) {
				throw new RuntimeException("Error when initializing database connection (" + dbConnectionUrl + "): " + e, e);
			}
		}
		return dbAccess;
	}

	//	private ObjectMapper objectMapper;
	//
	//	public ObjectMapper getObjectMapper() {
	//		if (objectMapper == null) {
	//			final PersonSerializer personSerializer = new PersonSerializer();
	//			personSerializer.setIdProvider(getDbAccess().getPersonIdProvider());
	//			final SimpleModule module = new SimpleModule()
	//					.addSerializer(new LatLongSerializer())
	//					.addSerializer(new GeoLocationSerializer())
	//					.addSerializer(new GeoLocationsSerializer())
	//					.addSerializer(personSerializer)
	//					.addDeserializer(LatLong.class, new LatLongDeserializer())
	//					.addDeserializer(GeoLocation.class, new GeoLocationDeserializer())
	//					.addDeserializer(GeoLocations.class, new GeoLocationsDeserializer())
	//					.addDeserializer(Person.class, new PersonDeserializer());
	//			objectMapper = new ObjectMapper()
	//					.registerModule(module);
	//		}
	//		return objectMapper;
	//	}
}
