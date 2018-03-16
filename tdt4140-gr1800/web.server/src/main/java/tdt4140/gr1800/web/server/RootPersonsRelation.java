package tdt4140.gr1800.web.server;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.Person;

public class RootPersonsRelation extends RestRelation<Void, Person> {

	public RootPersonsRelation(String name, RestEntity<Person> relatedEntity) {
		super(name, relatedEntity);
	}

	@Override
	public Collection<Person> getRelated(Void entity) {
		return getDbAccess().getAllPersons(false);
	}

	@Override
	protected Person getRelated(Void entity, int num) {
		return getDbAccess().getPerson(num, false);
	}
	
	@Override
	protected boolean isRelated(Person related, String qualifier) {
		String propertyValue = qualifier.contains("@") ? related.getEmail() : related.getName();
		return qualifier.equals(propertyValue);
	}
	
	@Override
	public Person createRelated(Void owner, JsonNode payload) {
		String name = null, email = null;
		if (payload instanceof ObjectNode) {
			ObjectNode objectNode = (ObjectNode) payload;
			name = objectNode.get("name").asText();
			email = objectNode.get("email").asText();
		}
		if (name == null || email == null) {
			throw new IllegalArgumentException("name and email must be set: " + payload);
		}
		return getDbAccess().createPerson(name, email);
	}

	@Override
	public void updateRelated(Void owner, Person related, JsonNode payload) {
		if (payload instanceof ObjectNode) {
			ObjectNode objectNode = (ObjectNode) payload;
			if (objectNode.has("name")) {
				related.setName(objectNode.get("name").asText());
			}
			if (objectNode.has("email")) {
				related.setEmail(objectNode.get("email").asText());
			}
		}
		getDbAccess().updatePersonData(related);
	}

	@Override
	public void deleteRelated(Void owner, Person related) {
		getDbAccess().deletePerson(related);
	}
}
