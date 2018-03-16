package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import tdt4140.gr1800.app.db.IdProvider;

public abstract class IdSerializer<T> extends StdSerializer<T> {

	public static final String ID_FIELD_NAME = "id";

	public IdSerializer(final Class<T> clazz) {
		super(clazz);
	}

	private IdProvider<T> idProvider = null;

	public void setIdProvider(final IdProvider<T> idProvider) {
		this.idProvider = idProvider;
	}

	protected void serializeId(final T t, final JsonGenerator jsonGen) throws IOException {
		if (idProvider != null && idProvider.hasId(t)) {
			jsonGen.writeFieldName(ID_FIELD_NAME);
			jsonGen.writeNumber(idProvider.getId(t));
		}
	}
}
