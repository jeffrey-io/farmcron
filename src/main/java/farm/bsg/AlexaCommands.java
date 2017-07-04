package farm.bsg;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletToSpeechletV2Adapter;
import com.amazon.speech.speechlet.authentication.SpeechletRequestSignatureVerifier;
import com.amazon.speech.speechlet.servlet.ServletSpeechletRequestHandler;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;

import farm.bsg.ops.CounterCodeGen;

public class AlexaCommands implements Speechlet {

    public static void link(final CounterCodeGen c) {
        c.section("Alexa");
        c.counter("alexa_auth_attempt", "an alexa auth request was made");
        c.counter("alexa_auth_success", "an alexa auth passed");
        c.counter("alexa_auth_failure", "an alexa auth failed");
    }

    private transient final SpeechletToSpeechletV2Adapter  adapter;

    private transient final ServletSpeechletRequestHandler handler;

    public AlexaCommands(final ProductEngine engine) {
        this.adapter = new SpeechletToSpeechletV2Adapter(this);
        this.handler = new ServletSpeechletRequestHandler();
    }

    public boolean auth(final byte[] serializedSpeechletRequest, final String signature, final String chain) {
        BsgCounters.I.alexa_auth_attempt.bump();
        try {
            SpeechletRequestSignatureVerifier.checkRequestSignature(serializedSpeechletRequest, signature, chain);
            BsgCounters.I.alexa_auth_success.bump();
            return true;
        } catch (final Exception err) {
            BsgCounters.I.alexa_auth_failure.bump();
            return false;
        }
    }

    public byte[] handle(final byte[] serializedSpeechletRequest) throws Exception {
        return this.handler.handleSpeechletCall(this.adapter, serializedSpeechletRequest);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        final SpeechletResponse response = new SpeechletResponse();
        if ("chores".equalsIgnoreCase(request.getIntent().getName())) {
            final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
            output.setText("this needs to be linked to tasks");
            response.setOutputSpeech(output);
        }
        if ("habits".equalsIgnoreCase(request.getIntent().getName())) {
            final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
            output.setText("Habits are not yet integrated");
            response.setOutputSpeech(output);
        }
        if ("stretch".equalsIgnoreCase(request.getIntent().getName())) {
            final SsmlOutputSpeech output = new SsmlOutputSpeech();
            try {
                final int input = Integer.parseInt(request.getIntent().getSlot("Count").getValue());
                final StringBuilder sb = new StringBuilder();
                sb.append("<speak>");
                sb.append("Going to begin ").append(input).append(" reps. You have five seconds to get into position. <break time=\"5s\"/>");
                for (int k = 0; k < input; k++) {
                    sb.append("Breathe and hold, and stretch. <break time=\"1s\"/> Focus. <break time=\"2500ms\"/>. Release. <break time=\"500ms\"/>");
                }
                sb.append(". You are finished.");
                sb.append("</speak>");
                output.setSsml(sb.toString());
            } catch (final NumberFormatException nfe) {
                output.setSsml("<speak>failed to understand the intent, sorry</speak>");
            }
            response.setOutputSpeech(output);
        }
        if ("whatup".equalsIgnoreCase(request.getIntent().getName())) {
            final SsmlOutputSpeech output = new SsmlOutputSpeech();
            output.setSsml("<speak>Nothing much mother <phoneme alphabet=\"ipa\" ph=\"fʌkər\">forka</phoneme></speak>");
            response.setOutputSpeech(output);
        }
        return response;
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        final SpeechletResponse response = new SpeechletResponse();
        final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
        output.setText("I AM THE GOAT, and you are not the goat. My name is roslin, and I am your baby goat.");
        response.setOutputSpeech(output);
        return response;
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
    }
}
