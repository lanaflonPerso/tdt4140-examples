package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class Tags implements Tagged, Iterable<String> {

	private Collection<String> tags = null;

	@Override
	public Iterator<String> iterator() {
		return (tags != null ? tags.iterator() : Collections.<String>emptyList().iterator());
	}

	public Tags(final String... tags) {
		setTags(tags);
	}

	public Tags(final Tagged tags) {
		setTags(tags.getTags());
	}

	public static Tags valueOf(final String tags) {
		return valueOf(tags, ",");
	}

	public static Tags valueOf(final String tags, final String separator) {
		return new Tags(tags.split(separator));
	}

	public int getTagCount() {
		return (tags == null ? 0 : tags.size());
	}

	@Override
	public boolean hasTags(final String... tags) {
		return (tags.length == 0 || (this.tags != null && this.tags.containsAll(Arrays.asList(tags))));
	}

	final static String[] EMPTY_STRINGS = {};

	@Override
	public String[] getTags() {
		return (tags != null ? tags.toArray(new String[tags.size()]) : EMPTY_STRINGS);
	}

	@Override
	public String getTags(final String prefix, final String separator, final String suffix) {
		final StringBuilder buffer = new StringBuilder();
		append(buffer, prefix);
		int tagNum = 0;
		for (final String tag : tags) {
			if (tagNum > 0 && separator != null) {
				buffer.append(separator);
			}
			buffer.append(tag);
			tagNum++;
		}
		append(buffer, suffix);
		return buffer.toString();
	}

	static StringBuilder append(final StringBuilder buffer, final String s) {
		if (s != null) {
			buffer.append(s);
		}
		return buffer;
	}

	@Override
	public void setTags(final String... tags) {
		this.tags = new ArrayList<>();
		addTags(tags);
	}

	@Override
	public void addTags(final String... tags) {
		if (this.tags == null && tags != null && tags.length > 0) {
			this.tags = new ArrayList<>();
		}
		for (int i = 0; i < tags.length; i++) {
			if (! this.tags.contains(tags[i])) {
				this.tags.add(tags[i]);
			}
		}
	}

	@Override
	public void removeTags(final String... tags) {
		if (this.tags != null) {
			this.tags.removeAll(Arrays.asList(tags));
		}
	}
}
