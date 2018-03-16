package tdt4140.gr1800.web.server;

public class GetMethod extends HttpMethod {

	public GetMethod(RestEntity<?> rootEntity) {
		super(rootEntity);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, int num) {
		doRelated(relation, num);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation, String qualifier) {
		doRelated(relation, qualifier);
	}

	@Override
	protected void doMethod(RestRelation<?, ?> relation) {
		doRelated(relation);
	}
}
