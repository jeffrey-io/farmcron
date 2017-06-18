package farm.bsg;

import farm.bsg.ops.Counter;
import farm.bsg.ops.CounterSource;

public class BsgCounters {

  // Section{FACEBOOK MESSENGER}
  public final Counter fb_has_invalid_host;
  public final Counter fb_subscribe_begin;
  public final Counter fb_token_given_correct;
  public final Counter fb_token_given_wrong;
  public final Counter fb_message;
  public final Counter fb_message_valid;
  public final Counter fb_message_invalid;
  public final Counter fb_attempt_response;
  public final Counter fb_send_failed_no_fb_token;
  public final Counter fb_send_out_on_wire;
  public final Counter fb_send_ok;
  public final Counter fb_send_failed_send;

  // Section{PAGE: DASHBOARD}
  public final Counter dashboard_hits;

  // Section{PAGE: TASK FACTORY}
  public final Counter task_factory_monitor_run;

  // Section{DATA: HABIT}
  public final Counter habit_bad_history;

  public final CounterSource source;

  public static final BsgCounters I = BUILD();

  private static BsgCounters BUILD() {
    CounterSource source = new CounterSource();
    BsgCounters counters = new BsgCounters(source);
    source.lockDown();
    return counters;
  }

  public BsgCounters(CounterSource src) {
    this.source = src;

    src.setSection("Facebook Messenger");
    this.fb_has_invalid_host = src.counter("fb_has_invalid_host", "Facebook is setting a host header that is wrong");
    this.fb_subscribe_begin = src.counter("fb_subscribe_begin", "Facebook is attempting to subscribe to the page");
    this.fb_token_given_correct = src.counter("fb_token_given_correct", "Facebook gave us the right token");
    this.fb_token_given_wrong = src.counter("fb_token_given_wrong", "Facebook gave us the wrong token");
    this.fb_message = src.counter("fb_message", "Facebook is sending us a message");
    this.fb_message_valid = src.counter("fb_message_valid", "We were able to parse the message");
    this.fb_message_invalid = src.counter("fb_message_invalid", "We failed to parse the message");
    this.fb_attempt_response = src.counter("fb_attempt_response", "The engine gave a response back from a given message");
    this.fb_send_failed_no_fb_token = src.counter("fb_send_failed_no_fb_token", "We are unable to send messages since the site lacks a token");
    this.fb_send_out_on_wire = src.counter("fb_send_out_on_wire", "We attempted to actuall send the message");
    this.fb_send_ok = src.counter("fb_send_ok", "We sent a message well enough");
    this.fb_send_failed_send = src.counter("fb_send_failed_send", "We were unable to send the message via HTTP");

    src.setSection("Page: Dashboard");
    this.dashboard_hits = src.counter("dashboard_hits", "How many times a dashboard was viewed");

    src.setSection("Page: Task Factory");
    this.task_factory_monitor_run = src.counter("task_factory_monitor_run", "How many runs of the task factory have there been");

    src.setSection("Data: Habit");
    this.habit_bad_history = src.counter("habit_bad_history", "Contained poorly formated history");
  }
}
