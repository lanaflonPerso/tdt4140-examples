package tdt4140.gr1800.web.server;

import java.util.ArrayList;
import java.util.Collection;

public class RestEntity<T> {

	private final Collection<RestRelation<T, ?>> relations = new ArrayList<RestRelation<T,?>>();
	
	public RestRelation<T, ?> getRelation(String relationName) {
		for (RestRelation<T, ?> relation : relations) {
			if (relationName.equals(relation.getName())) {
				return relation;
			}
		}
		return null;
	}
	
	public void addRelation(RestRelation<T, ?> relation) {
		this.relations.add(relation);
	}
}
