package tdt4140.gr1800.web.server;

public class PutMethod extends HttpMethod {

	public PutMethod(RestEntity<?> rootEntity) {
		super(rootEntity);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, int num) {
		doRelated(relation, num);
		relation.updateRelatedObject(parentEntity, entity, getPayload());
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, String qualifier) {
		doRelated(relation, qualifier);
		relation.updateRelatedObject(parentEntity, entity, getPayload());
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation) {
		throw new UnsupportedOperationException();
	}
}
