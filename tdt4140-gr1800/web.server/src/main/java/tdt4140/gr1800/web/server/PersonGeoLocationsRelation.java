package tdt4140.gr1800.web.server;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class PersonGeoLocationsRelation extends RestRelation<Person, GeoLocations> {

	public PersonGeoLocationsRelation(String name, RestEntity<GeoLocations> relatedEntity) {
		super(name, relatedEntity);
	}

	@Override
	public Collection<GeoLocations> getRelated(Person entity) {
		return getDbAccess().getGeoLocations(entity, false);
	}

	@Override
	public GeoLocations createRelated(Person owner, JsonNode payload) {
		GeoLocations geoLocations = getDbAccess().createGeoLocations(owner);
		updateRelated(owner, geoLocations, payload);
		return geoLocations;
	}
	
	private final static Collection<String> NULL_STRINGS = Arrays.asList("", "\0");
	
	protected boolean isNull(String s) {
		return NULL_STRINGS.contains(s);
	}
	
	protected <T, V> void set(T t, ObjectNode jsonNode, String fieldName, Consumer<String> setter) {
		if (jsonNode.has(fieldName)) {
			String s = jsonNode.get(fieldName).asText();
			setter.accept(isNull(s) ? null : s);
		}
	}
	protected <T, V> void set(T t, ObjectNode jsonNode, String fieldName, Consumer<V> setter, Function<String, V> parser, Class<? extends Exception> exClass) {
		if (jsonNode.has(fieldName)) {
			String s = jsonNode.get(fieldName).asText();
			try {
				setter.accept(isNull(s) ? null : parser.apply(s));
			} catch (RuntimeException e) {
				if (exClass != null && (! exClass.isAssignableFrom(e.getClass()))) {
					throw e;
				}
			}
		}
	}
	
	@Override
	public void updateRelated(Person owner, GeoLocations related, JsonNode payload) {
		if (payload instanceof ObjectNode) {
			ObjectNode objectNode = (ObjectNode) payload;
			set(related, objectNode, "name", related::setName);
			set(related, objectNode, "description", related::setDescription);
			set(related, objectNode, "date", related::setDate, LocalDate::parse, DateTimeParseException.class);
			set(related, objectNode, "time", related::setTime, LocalTime::parse, DateTimeParseException.class);
			set(related, objectNode, "zone", related::setZone, ZoneId::of, DateTimeException.class);
			if (objectNode.has("tags")) {
				JsonNode tagsNode = objectNode.get("tags");
				String[] tags = null;
				if (tagsNode instanceof ArrayNode) {
					ArrayNode tagsArray = (ArrayNode) tagsNode;
					tags = new String[tagsArray.size()];
					for (int i = 0; i < tags.length; i++) {
						tags[i] = tagsArray.get(i).asText();
					}
				} else if (tagsNode instanceof TextNode) {
					tags = ((TextNode) tagsNode).asText().split("[ ,;]");
				}
				if (tags != null) {
					related.setTags(tags);
				}
			}
		}
		getDbAccess().updateGeoLocationsData(related);
	}

	@Override
	public void deleteRelated(Person owner, GeoLocations related) {
		getDbAccess().deleteGeoLocations(related);
	}
}
