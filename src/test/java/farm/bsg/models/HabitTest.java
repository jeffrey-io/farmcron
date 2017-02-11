package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class HabitTest {
    @Test
    public void Coverage() {
        Habit habit = new Habit();
        Assert.assertNotNull(habit.toJson());
    }

}
