package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Tags implements Tagged {

	private Collection<String> tags = null;

	public Tags(String... tags) {
		addTags(tags);
	}
	
	public int getTagCount() {
		return (tags == null ? 0 : tags.size());
	}
	
	@Override
	public boolean hasTags(String... tags) {
		return this.tags != null && this.tags.containsAll(Arrays.asList(tags));
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
