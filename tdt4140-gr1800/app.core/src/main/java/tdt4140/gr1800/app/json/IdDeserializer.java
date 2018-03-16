package tdt4140.gr1800.app.json;

import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class IdDeserializer<T> extends StdDeserializer<T> {

	public IdDeserializer(final Class<T> clazz) {
		super(clazz);
	}

	void deserialize(final ObjectNode objectNode, final String fieldName, final Consumer<String> setter) throws JsonProcessingException {
		if (objectNode.has(fieldName)) {
			final String s = objectNode.get(fieldName).asText();
			setter.accept(s);
		}
	}

	<U> void deserialize(final ObjectNode objectNode, final String fieldName, final Function<String, U> parser, final Consumer<U> setter) throws JsonProcessingException {
		if (objectNode.has(fieldName)) {
			final String s = objectNode.get(fieldName).asText();
			final U value = parser.apply(s);
			setter.accept(value);
		}
	}
}
