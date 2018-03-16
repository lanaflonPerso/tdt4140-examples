package tdt4140.gr1800.web.server;

public class PostMethod extends HttpMethod {

	public PostMethod(RestEntity<?> rootEntity) {
		super(rootEntity);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, int num) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, String qualifier) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation) {
		entity = relation.createRelatedObject(parentEntity, getPayload());
	}
}
