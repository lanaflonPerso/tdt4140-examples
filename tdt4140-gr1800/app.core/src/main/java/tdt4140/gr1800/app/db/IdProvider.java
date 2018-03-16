package tdt4140.gr1800.app.db;

public interface IdProvider<T> {
	public boolean hasId(T t);
	public int getId(T t);
}
