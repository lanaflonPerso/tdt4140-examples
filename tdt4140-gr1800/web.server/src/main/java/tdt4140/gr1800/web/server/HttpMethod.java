package tdt4140.gr1800.web.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class HttpMethod {

	private final RestEntity<?> rootEntity;

	public HttpMethod(final RestEntity<?> rootEntity) {
		this.rootEntity = rootEntity;
	}

	protected Object parentEntity = null, entity = null;
	protected Collection<?> entities = null;

	private Map<String, Object> params;
	private JsonNode payload;

	public Map<String, Object> getParameters() {
		return params;
	}

	public void setParameters(final Map<String, ? extends Object> params) {
		this.params = new HashMap<String, Object>(params);
	}

	public void setParameters(final Object... params) {
		this.params = new HashMap<String, Object>();
		for (int i = 0; i < params.length; i += 2) {
			this.params.put(String.valueOf(params[i]), params[i + 1]);
		}
	}

	protected Object getParameter(final String param) {
		if (params.containsKey(param)) {
			return params.get(param);
		}
		if (payload instanceof ObjectNode) {
			return ((ObjectNode) payload).get(param);
		}
		return null;
	}

	private final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);

	public void setPayload(final JsonNode payload) {
		this.payload = payload;
	}

	public void setPayload(final Object... params) {
		final ObjectNode objectNode = jsonNodeFactory.objectNode();
		for (int i = 0; i < params.length; i += 2) {
			setPayloadValue(objectNode, String.valueOf(params[i]), params[i + 1]);
		}
		this.payload = objectNode;
	}

	public void setPayload(final Map<String, ? extends Object> params) {
		final ObjectNode objectNode = jsonNodeFactory.objectNode();
		for (final Map.Entry<String, ? extends Object> entry : params.entrySet()) {
			setPayloadValue(objectNode, entry.getKey(), entry.getValue());
		}
		this.payload = objectNode;
	}

	protected void setPayloadValue(final ObjectNode objectNode, final String key, final Object value) {
		if (value instanceof String) {
			objectNode.put(key, (String) value);
		} else if (value instanceof Number) {
			objectNode.put(key, ((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			objectNode.put(key, (Boolean) value);
		}
	}

	public JsonNode getPayload() {
		return payload;
	}

	public Object doMethod(final String... segments) {
		return doMethod(Arrays.asList(segments));
	}

	public Object doMethod(final Iterable<String> segments) {
		return doMethod(segments.iterator());
	}

	public Object doMethod(final Iterator<String> segments) {
		RestEntity<?> restEntity = rootEntity;
		entity = null;
		while (segments.hasNext()) {
			final String segment = segments.next();
			if (segment.length() == 0) {
				continue;
			}
			final RestRelation<?, ?> relation = restEntity.getRelation(segment);
			parentEntity = entity;
			entity = null;
			entities = null;
			if (segments.hasNext()) {
				final String qualifier = segments.next();
				try {
					final int num = Integer.valueOf(qualifier);
					if (segments.hasNext()) {
						doRelated(relation, num);
					} else {
						doMethod(relation, num);
					}
				} catch (final NumberFormatException e) {
					if (segments.hasNext()) {
						doRelated(relation, qualifier);
					} else {
						doMethod(relation, qualifier);
					}
				}
				restEntity = relation.getRelatedEntity();
			} else {
				if (segments.hasNext()) {
					doRelated(relation);
				} else {
					doMethod(relation);
				}
			}
			if (entity == null) {
				break;
			}
		}
		if (entities != null) {
			return entities;
		}
		return entity;
	}

	protected abstract void doMethod(RestRelation<?, ?> relation, int num);

	protected abstract void doMethod(RestRelation<?, ?> relation, String qualifier);

	protected abstract void doMethod(RestRelation<?, ?> relation);

	//

	protected void doRelated(final RestRelation<?, ?> relation) {
		entities = relation.getRelatedObjects(parentEntity);
	}

	protected void doRelated(final RestRelation<?, ?> relation, final String qualifier) {
		entity = relation.getRelatedObject(parentEntity, qualifier);
	}

	protected void doRelated(final RestRelation<?, ?> relation, final int num) {
		entity = relation.getRelatedObject(parentEntity, num);
	}
}
