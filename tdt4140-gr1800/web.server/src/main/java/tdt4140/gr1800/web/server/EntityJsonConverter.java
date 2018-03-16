package tdt4140.gr1800.web.server;

import com.fasterxml.jackson.databind.JsonNode;

public interface EntityJsonConverter<T> {
	
	public static enum JsonDepth {
		ID, CONTENTS, CONTAINED
	}
	
	public boolean supportsEntity(Object entity, JsonDepth depth);
	public JsonNode convert2Json(T entity, JsonDepth depth);
}
