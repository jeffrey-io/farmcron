package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class TaskTest {
    @Test
    public void dueDate() {
        final Task task = new Task();
        task.created();
        task.set("created", "2017-06-22T02:40Z");
        final long now = 1498099238370L;
        task.setDue(now, 4);
        Assert.assertEquals("2017-06-26T07:00Z", task.get("due_date"));
        task.setDue(now, 1);
        Assert.assertEquals("2017-06-23T07:00Z", task.get("due_date"));
    }

    @Test
    public void stateMachineAndTimeline() {
        final Task task = new Task();
        task.created();
        task.set("created", "2017-06-22T02:40Z");
        final long now = 1498099238370L;

        Assert.assertTrue(task.canTransition());
        for (int delta = -100; delta < 100; delta++) {
            Assert.assertFalse(task.isClosedAndReadyForTransition(now + delta * 1000 * 60 * 60 * 24, 1));
        }

        task.close();
        task.set("closed", "2017-06-22T02:40Z");
        Assert.assertFalse(task.canTransition());
        Assert.assertFalse(task.isClosedAndReadyForTransition(now, 1));

        for (long delta = -100; delta < 0; delta++) {
            Assert.assertFalse(task.isClosedAndReadyForTransition(now + delta * 1000 * 60 * 60 * 24, 1));
        }
        Assert.assertTrue(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 24, 1));
        Assert.assertTrue(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 12, 1));
        Assert.assertTrue(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 5, 1));

        Assert.assertFalse(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 24, 2));
        Assert.assertFalse(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 12, 2));
        Assert.assertFalse(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 5, 2));

        Assert.assertTrue(task.isClosedAndReadyForTransition(now + 1000 * 60 * 60 * 30, 2));
    }
}
