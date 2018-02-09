package tdt4140.gr1800.app.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
