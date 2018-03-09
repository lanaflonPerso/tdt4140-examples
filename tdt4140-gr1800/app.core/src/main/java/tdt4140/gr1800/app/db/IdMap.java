package tdt4140.gr1800.app.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IdMap<T> {

	protected Map<Integer, T> id2o = new HashMap<Integer, T>();
	protected Map<T, Integer> o2id = new HashMap<T, Integer>();

	public T get(int id) {
		return id2o.get(id);
	}

	public int getId(T o) {
		Integer id = o2id.get(o);
		return (id != null ? id : -1);
	}
	
	public Collection<T> get() {
		return o2id.keySet();
	}
	
	public Collection<Integer> getIds() {
		return id2o.keySet();
	}

	//
	
	void set(T o, int id) {
		if (o2id.containsKey(o)) {
			throw new IllegalStateException(o + " already has the id " + o2id.get(o));
		}
		if (id2o.containsKey(id)) {
			throw new IllegalStateException(id + " already used by " + id2o.get(id));
		}
		id2o.put(id, o);
		o2id.put(o, id);
	}
	
	void clear() {
		o2id.clear();
		id2o.clear();		
	}
	
	private void remove(T o, int id) {
		o2id.remove(o);
		id2o.remove(id);		
	}

	void remove(T o) {
		if (o2id.containsKey(o)) {
			remove(o, o2id.get(o));
		}
	}

	void removeAll(Iterable<T> os) {
		for (T o : os) {
			remove(o);
		}
	}

	void remove(int id) {
		if (id2o.containsKey(id)) {
			remove(id2o.get(id), id);
		}		
	}
}
