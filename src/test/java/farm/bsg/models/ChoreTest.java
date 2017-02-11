package farm.bsg.models;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;

public class ChoreTest {

    // 2017-01-21T16:55Z
    private static final DateTime NOW = new DateTime(1485017736711L);

    @Test
    public void Coverage() {
        Chore chore = new Chore();
        Assert.assertNotNull(chore.toJson());
    }

    @Test
    public void VeryLateItem() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-01T11:00Z");
        chore.set("frequency", "7");
        chore.set("slack", "1");
        Assert.assertEquals("20170121", chore.dayDue());
        Assert.assertEquals("20170121", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void ItemNeverPerformed() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("frequency", "7");
        chore.set("slack", "1");
        Assert.assertEquals("20170121", chore.dayDue());
        Assert.assertEquals("20170121", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void ItemNeverPerformedAndNoSlack() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("frequency", "7");
        chore.set("slack", "");
        Assert.assertEquals("20170121", chore.dayDue());
        Assert.assertEquals("20170121", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void VeryLateItemAvailableOnlyOnFridaysInSeptember() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-01T11:00Z");
        chore.set("frequency", "7");
        chore.set("slack", "1");
        chore.set("month_filter", TypeMonthFilter.Month.encode("s"));
        chore.set("day_filter", TypeDayFilter.Day.encode("f"));
        Assert.assertEquals("20170121", chore.dayDue());
        Assert.assertEquals("20170901", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void VeryLateItemAvailableOnlyOnMondayInSeptember() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-01T11:00Z");
        chore.set("frequency", "7");
        chore.set("slack", "1");
        chore.set("month_filter", TypeMonthFilter.Month.encode("s"));
        chore.set("day_filter", TypeDayFilter.Day.encode("m"));
        Assert.assertEquals("20170121", chore.dayDue());
        Assert.assertEquals("20170904", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void YesterdayWeeklyTwoDaysSlack1() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-20T16:55Z");
        chore.set("frequency", "7");
        chore.set("slack", "1");
        Assert.assertEquals("20170127", chore.dayDue());
        Assert.assertEquals("20170126", chore.firstAvailableDay());
        Assert.assertEquals(1, chore.daysAvailable());
    }

    @Test
    public void YesterdayWeeklyTwoDaysSlack4() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-20T16:55Z");
        chore.set("frequency", "7");
        chore.set("slack", "4");
        Assert.assertEquals("20170127", chore.dayDue());
        Assert.assertEquals("20170123", chore.firstAvailableDay());
        Assert.assertEquals(4, chore.daysAvailable());
    }

    @Test
    public void YesterdayWeeklyTwoDaysSlack0() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-20T16:55Z");
        chore.set("frequency", "7");
        chore.set("slack", "0");
        Assert.assertEquals("20170127", chore.dayDue());
        Assert.assertEquals("20170127", chore.firstAvailableDay());
        Assert.assertEquals(0, chore.daysAvailable());
    }

    @Test
    public void YesterdayOnScheduleTuesdays() {
        Chore chore = new Chore();
        chore.setNow(NOW);
        chore.set("last_performed", "2017-01-20T16:55Z");
        chore.set("frequency", "7");
        chore.set("slack", "3");
        chore.set("day_filter", TypeDayFilter.Day.encode("t"));
        Assert.assertEquals("20170127", chore.dayDue());
        Assert.assertEquals("20170124", chore.firstAvailableDay());
        Assert.assertEquals(1, chore.daysAvailable());
    }
}
