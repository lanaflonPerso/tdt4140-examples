package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Tags implements Tagged {

	private Collection<String> tags = null;

	public Tags(String... tags) {
		setTags(tags);
	}

	public Tags(Tagged tags) {
		setTags(tags.getTags());
	}

	public static Tags valueOf(String tags) {
		return valueOf(tags, ",");
	}

	public static Tags valueOf(String tags, String separator) {
		return new Tags(tags.split(separator));
	}

	public int getTagCount() {
		return (tags == null ? 0 : tags.size());
	}
	
	@Override
	public boolean hasTags(String... tags) {
		return (tags.length == 0 || (this.tags != null && this.tags.containsAll(Arrays.asList(tags))));
	}

	final static String[] EMPTY_STRINGS = {};

	@Override
	public String[] getTags() {
		return (tags != null ? tags.toArray(new String[tags.size()]) : EMPTY_STRINGS);
	}
	
	@Override
	public String getTags(String prefix, String separator, String suffix) {
		StringBuilder buffer = new StringBuilder();
		append(buffer, prefix);
		int tagNum = 0;
		for (String tag : tags) {
			if (tagNum > 0 && separator != null) {
				buffer.append(separator);				
			}
			buffer.append(tag);
			tagNum++;
		}
		append(buffer, suffix);
		return buffer.toString();
	}

	static StringBuilder append(StringBuilder buffer, String s) {
		if (s != null) {
			buffer.append(s);
		}
		return buffer;
	}
	
	@Override
	public void setTags(String... tags) {
		this.tags = new ArrayList<>();
		addTags(tags);
	}

	public void addTags(String... tags) {
		if (this.tags == null && tags != null && tags.length > 0) {
			this.tags = new ArrayList<>();
		}
		for (int i = 0; i < tags.length; i++) {
			if (! this.tags.contains(tags[i])) {
				this.tags.add(tags[i]);
			}
		}
	}
	
	public void removeTags(String... tags) {
		if (this.tags != null) {
			this.tags.removeAll(Arrays.asList(tags));
		}
	}
}
