package tdt4140.gr1800.app.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IdMap<T> implements IdProvider<T> {

	protected Map<Integer, T> id2o = new HashMap<Integer, T>();
	protected Map<T, Integer> o2id = new HashMap<T, Integer>();

	public int size() {
		return o2id.size();
	}

	public T get(final int id) {
		return id2o.get(id);
	}

	@Override
	public boolean hasId(final T t) {
		return o2id.containsKey(t);
	}

	@Override
	public int getId(final T o) {
		final Integer id = o2id.get(o);
		return (id != null ? id : -1);
	}

	public Collection<T> get() {
		return o2id.keySet();
	}

	public Collection<Integer> getIds() {
		return id2o.keySet();
	}

	//

	public void set(final T o, final int id) {
		if (o2id.containsKey(o)) {
			throw new IllegalStateException(o + " already has the id " + o2id.get(o));
		}
		if (id2o.containsKey(id)) {
			throw new IllegalStateException(id + " already used by " + id2o.get(id));
		}
		id2o.put(id, o);
		o2id.put(o, id);
	}

	public void clear() {
		o2id.clear();
		id2o.clear();
	}

	private void remove(final T o, final int id) {
		o2id.remove(o);
		id2o.remove(id);
	}

	public void remove(final T o) {
		if (o2id.containsKey(o)) {
			remove(o, o2id.get(o));
		}
	}

	public void removeAll(final Iterable<T> os) {
		for (final T o : os) {
			remove(o);
		}
	}

	public void remove(final int id) {
		if (id2o.containsKey(id)) {
			remove(id2o.get(id), id);
		}
	}
}
