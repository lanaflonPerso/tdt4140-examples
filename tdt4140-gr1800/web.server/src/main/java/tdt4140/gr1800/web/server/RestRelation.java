package tdt4140.gr1800.web.server;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.JsonNode;

import tdt4140.gr1800.app.db.IDbAccess;

public abstract class RestRelation<OE, RE> {

	private final String name;

	private RestEntity<RE> relatedEntity;

	protected RestRelation(String name, RestEntity<RE> relatedEntity) {
		super();
		this.name = name;
		this.relatedEntity = relatedEntity;
	}
	protected RestRelation(String name) {
		this(name, null);
	}
	
	public String getName() {
		return name;
	}
	
	public RestEntity<RE> getRelatedEntity() {
		return relatedEntity;
	}

	private IDbAccess dbAccess;
	
	public IDbAccess getDbAccess() {
		return dbAccess;
	}
	
	public void setDbAccess(IDbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	public void setRelatedEntity(RestEntity<RE> relatedEntity) {
		this.relatedEntity = relatedEntity;
	}

	// GET method: /name
	protected abstract Collection<RE> getRelated(OE entity);
	
	protected Collection<RE> getRelatedObjects(Object entity) {
		return getRelated((OE) entity);
	}

	// GET method: /name/<num>
	protected RE getRelated(OE entity, int num) {
		Collection<RE> col = getRelated(entity);
		if (num < 0) {
			num = col.size() + num;
		}
		for (RE related : col) {
			if (num == 0) {
				return related;
			}
			num--;
		}
		return null;
	}
	public RE getRelatedObject(Object entity, int num) {
		return getRelated((OE) entity, num);
	}
	
	protected boolean isRelated(RE related, String qualifier) {
		return false;
	}

	// GET method: /name/<qualifier>
	protected RE getRelated(OE entity, String qualifier) {
		for (RE related : getRelated(entity)) {
			if (isRelated(related, qualifier)) {
				return related;
			}
		}
		return null;
	}
	public RE getRelatedObject(Object entity, String qualifier) {
		return getRelated((OE) entity, qualifier);
	}

	// POST method
	protected abstract RE createRelated(OE owner, JsonNode payload);

	public Object createRelatedObject(Object owner, JsonNode payload) {
		return createRelated((OE) owner, payload);
	}
	
	// PUT method
	protected abstract void updateRelated(OE owner, RE related, JsonNode payload);

	public void updateRelatedObject(Object owner, Object related, JsonNode payload) {
		updateRelated((OE) owner, (RE) related, payload);
	}
	
	// DELETE method
	protected abstract void deleteRelated(OE owner, RE related);
	
	public void deleteRelatedObject(Object owner, Object related) {
		deleteRelated((OE) owner, (RE) related);
	}

	// DELETE method: /name/<num>
	protected void deleteRelated(OE owner, int num) {
		Collection<RE> col = getRelated(owner);
		int count = (num < 0 ? col.size() + num : num);
		for (RE related : col) {
			if (count == 0) {
				deleteRelated(owner, related);
				return;
			}
			count--;
		}
		throw new NoSuchElementException("No element with '" + num + "' qualifier");
	}

	public void deleteRelatedObject(Object owner, int num) {
		deleteRelated((OE) owner, num);
	}
	
	// DELETE method: /name/<qualifier>
	protected void deleteRelated(OE owner, String qualifier) {
		for (RE related : getRelated(owner)) {
			if (isRelated(related, qualifier)) {
				deleteRelated(owner, related);
				return;
			}
		}
		throw new NoSuchElementException("No element with '" + qualifier + "' qualifier");
	}
	
	public void deleteRelatedObject(Object owner, String qualifier) {
		deleteRelated((OE) owner, qualifier);
	}
}
