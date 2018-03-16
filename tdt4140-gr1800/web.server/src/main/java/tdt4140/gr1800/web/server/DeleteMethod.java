package tdt4140.gr1800.web.server;

public class DeleteMethod extends HttpMethod {

	public DeleteMethod(RestEntity<?> rootEntity) {
		super(rootEntity);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, int num) {
		relation.deleteRelatedObject(parentEntity, num);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, String qualifier) {
		relation.deleteRelatedObject(parentEntity, qualifier);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation) {
		relation.deleteRelatedObject(parentEntity, getPayload());
	}
}
