package tdt4140.gr1800.app.core;

public class TimedTaggedImpl extends TimedImpl implements Tagged {
	
	private Tags tags = null;

	public TimedTaggedImpl() {
	}

	public TimedTaggedImpl(Tagged tags) {
		setTags(tags.getTags());
	}

	private static TimedTaggedImpl valueOf(Tags tags) {
		TimedTaggedImpl timedTags = new TimedTaggedImpl();
		timedTags.tags = tags;
		return timedTags;
	}

	public static TimedTaggedImpl valueOf(String tags) {
		return valueOf(Tags.valueOf(tags));
	}
	
	public static TimedTaggedImpl valueOf(String tags, String separator) {
		return valueOf(Tags.valueOf(tags, separator));
	}

	@Override
	public boolean hasTags(String... tags) {
		return this.tags != null && this.tags.hasTags(tags);
	}

	@Override
	public String[] getTags() {
		return (tags != null ? tags.getTags() : Tags.EMPTY_STRINGS);
	}
	
	@Override
	public String getTags(String prefix, String separator, String suffix) {
		return (tags != null ? tags.getTags(prefix, separator, suffix) : Tags.append(Tags.append(new StringBuilder(), prefix), suffix).toString());
	}

	@Override
	public void setTags(String... tags) {
		this.tags = (tags != null && tags.length > 0 ? new Tags(tags) : null);
	}

	public void addTags(String... tags) {
		if (this.tags == null) {
			this.tags = new Tags();
		}
		this.tags.addTags(tags);
	}
	
	public void removeTags(String... tags) {
		if (this.tags != null) {
			this.tags.removeTags(tags);
			if (this.tags.getTagCount() == 0) {
				this.tags = null;
			}
		}
	}
}
