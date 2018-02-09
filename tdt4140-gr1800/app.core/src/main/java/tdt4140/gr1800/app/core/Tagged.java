package tdt4140.gr1800.app.core;

public interface Tagged {
	public boolean hasTags(String... tags);
	public void addTags(String... tags);	
	public void removeTags(String... tags);
}
