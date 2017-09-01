package farm.bsg;

import farm.bsg.ops.Counter;
import farm.bsg.ops.CounterSource;

public class BsgCounters {

    public static final BsgCounters I = BUILD();

    private static BsgCounters BUILD() {
        final CounterSource source = new CounterSource();
        final BsgCounters counters = new BsgCounters(source);
        return counters;
    }

    // Section{FACEBOOK MESSENGER}
    public final Counter       fb_has_invalid_host;
    public final Counter       fb_subscribe_begin;
    public final Counter       fb_token_given_correct;
    public final Counter       fb_token_given_wrong;
    public final Counter       fb_message;
    public final Counter       fb_message_valid;
    public final Counter       fb_message_invalid;
    public final Counter       fb_attempt_response;
    public final Counter       fb_send_failed_no_fb_token;
    public final Counter       fb_send_out_on_wire;

    public final Counter       fb_send_ok;

    public final Counter       fb_send_failed_send;

    // Section{PAGE: DASHBOARD}
    public final Counter       dashboard_hits;
    // Section{PAGE: TASK FACTORY}
    public final Counter       task_factory_monitor_run;
    // Section{ALEXA}
    public final Counter       alexa_auth_attempt;

    public final Counter       alexa_auth_success;
    public final Counter       alexa_auth_failure;
    // Section{AUTH}
    public final Counter       auth_login_attempt;
    public final Counter       auth_login_success;
    public final Counter       auth_login_failure;
    public final Counter       auth_customer_login_attempt;
    public final Counter       auth_customer_login_success;
    public final Counter       auth_customer_login_failure;
    public final Counter       auth_attempt_cookie;
    public final Counter       auth_cache_hit;

    public final Counter       auth_cache_populate;
    public final Counter       auth_super_cookie_conversion;
    // Section{DATA: TASKS}
    public final Counter       task_transition;
    public final Counter       task_snooze;

    public final Counter       task_woke;
    public final Counter       task_close;

    // Section{DATA: WAKE INPUT FILE}
    public final Counter       compile_wake;

    public final Counter       wake_file_written_blob_cache;

    public final CounterSource source;

    public BsgCounters(final CounterSource src) {
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

        src.setSection("Alexa");
        this.alexa_auth_attempt = src.counter("alexa_auth_attempt", "an alexa auth request was made");
        this.alexa_auth_success = src.counter("alexa_auth_success", "an alexa auth passed");
        this.alexa_auth_failure = src.counter("alexa_auth_failure", "an alexa auth failed");

        src.setSection("Auth");
        this.auth_login_attempt = src.counter("auth_login_attempt", "an auth was attempted");
        this.auth_login_success = src.counter("auth_login_success", "an auth attempt was successful");
        this.auth_login_failure = src.counter("auth_login_failure", "an auth attempt failed");
        this.auth_customer_login_attempt = src.counter("auth_customer_login_attempt", "an auth was attempted");
        this.auth_customer_login_success = src.counter("auth_customer_login_success", "an auth attempt was successful");
        this.auth_customer_login_failure = src.counter("auth_customer_login_failure", "an auth attempt failed");
        this.auth_attempt_cookie = src.counter("auth_attempt_cookie", "an auth was attempted");
        this.auth_cache_hit = src.counter("auth_cache_hit", "the cookie was found in the local cache");
        this.auth_cache_populate = src.counter("auth_cache_populate", "the cookie was found in the DB and went into local cache");
        this.auth_super_cookie_conversion = src.counter("auth_super_cookie_conversion", "a super cookie was converted into a new cookie");

        src.setSection("Data: Tasks");
        this.task_transition = src.counter("task_transition", "a task was transitioned to a new state");
        this.task_snooze = src.counter("task_snooze", "a task was put to sleep");
        this.task_woke = src.counter("task_woke", "a task was brought back from sleep");
        this.task_close = src.counter("task_close", "a task was close");

        src.setSection("Data: Wake Input File");
        this.compile_wake = src.counter("compile_wake", "wake files are being compiled");
        this.wake_file_written_blob_cache = src.counter("wake_file_written_blob_cache", "a file was generated and put in the blob cache");
    }
}
