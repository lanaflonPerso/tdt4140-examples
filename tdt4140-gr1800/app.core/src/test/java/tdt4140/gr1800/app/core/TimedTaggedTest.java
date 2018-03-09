package tdt4140.gr1800.app.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TimedTaggedTest {

	private TimedTaggedImpl timedTagged;
	
	protected void setUp(TimedTaggedImpl timedTagged) {
		this.timedTagged = timedTagged;
	}
	
	@Before
	public void setUp() {
		setUp(new TimedTaggedImpl());
	}

	@Test
	public void testTaggedSetTime() {
		LocalTime now = LocalTime.now();
		timedTagged.setTime(now);
		Assert.assertEquals(now, timedTagged.getTime());
	}
	
	@Test
	public void testTaggedSetDate() {
		LocalDate now = LocalDate.now();
		timedTagged.setDate(now);
		Assert.assertEquals(now, timedTagged.getDate());
	}
	
	@Test
	public void testTaggedSetDateTime() {
		LocalDateTime now = LocalDateTime.now();
		timedTagged.setDateTime(now);
		Assert.assertEquals(now.toLocalDate(), timedTagged.getDate());
		Assert.assertEquals(now.toLocalTime(), timedTagged.getTime());
	}

	@Test
	public void testTaggedValueOf() {		
		Assert.assertEquals("<en, to>", TimedTaggedImpl.valueOf("en; to", "; ").getTags("<", ", ", ">"));
		Assert.assertEquals("<en, to>", TimedTaggedImpl.valueOf("en,to").getTags("<", ", ", ">"));
	}

	@Test
	public void testTaggedSet() {
		timedTagged.setTags("en", "to");
		Assert.assertTrue(timedTagged.hasTags("en"));
		Assert.assertTrue(timedTagged.hasTags("to"));
		timedTagged.setTags("tre");
		Assert.assertFalse(timedTagged.hasTags("en"));
		Assert.assertFalse(timedTagged.hasTags("to"));
		Assert.assertTrue(timedTagged.hasTags("tre"));
	}

	@Test
	public void testTaggedGet() {
		Assert.assertEquals(0, timedTagged.getTags().length);
		timedTagged.setTags("en", "to", "to");
		String[] tags = timedTagged.getTags();
		Assert.assertEquals(2, tags.length);
		Arrays.sort(tags);
		Arrays.equals(new String[] {"en",  "to"}, tags);
	}
	
	@Test
	public void testTaggedGet2() {
		Assert.assertEquals("", timedTagged.getTags(null, null, null));
		Assert.assertEquals("<>", timedTagged.getTags("<", null, ">"));
		timedTagged.setTags("en", "to", "to");
		Assert.assertEquals("ento", timedTagged.getTags(null, null, null));
		Assert.assertEquals("<en, to>", timedTagged.getTags("<", ", ", ">"));
	}

	@Test
	public void testTaggedAdd() {
		timedTagged.addTags("en", "to");
		Assert.assertTrue(timedTagged.hasTags("en"));
		Assert.assertTrue(timedTagged.hasTags("to"));
		Assert.assertTrue(timedTagged.hasTags("en", "to"));
		timedTagged.addTags("tre");
		Assert.assertTrue(timedTagged.hasTags("en", "to"));
		Assert.assertTrue(timedTagged.hasTags("tre"));
	}
	
	@Test
	public void testTaggedRemove() {
		timedTagged.addTags("en", "en", "to");
		timedTagged.removeTags("en");
		Assert.assertFalse(timedTagged.hasTags("en"));
		Assert.assertTrue(timedTagged.hasTags("to"));
		timedTagged.removeTags("to");
		Assert.assertFalse(timedTagged.hasTags("to"));
		timedTagged.removeTags("tre");
		Assert.assertFalse(timedTagged.hasTags("tre"));
	}
}
