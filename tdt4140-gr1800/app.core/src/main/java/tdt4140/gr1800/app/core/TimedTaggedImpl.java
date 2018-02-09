package tdt4140.gr1800.app.core;

public class TimedTaggedImpl extends TimedImpl implements Tagged {
	
	private Tags tags = null;

	@Override
	public boolean hasTags(String... tags) {
		return this.tags != null && this.tags.hasTags(tags);
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
