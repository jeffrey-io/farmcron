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

public class AlexaCommands implements Speechlet {

    private final ProductEngine                            engine;
    private transient final SpeechletToSpeechletV2Adapter  adapter;
    private transient final ServletSpeechletRequestHandler handler;

    public AlexaCommands(ProductEngine engine) {
        this.adapter = new SpeechletToSpeechletV2Adapter(this);
        this.handler = new ServletSpeechletRequestHandler();
        this.engine = engine;
    }

    public boolean auth(byte[] serializedSpeechletRequest, String signature, String chain) {
        try {
            SpeechletRequestSignatureVerifier.checkRequestSignature(serializedSpeechletRequest, signature, chain);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    public byte[] handle(byte[] serializedSpeechletRequest) throws Exception {
        return handler.handleSpeechletCall(adapter, serializedSpeechletRequest);
    }

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        SpeechletResponse response = new SpeechletResponse();
        PlainTextOutputSpeech output = new PlainTextOutputSpeech();
        output.setText("I AM THE GOAT, and you are not the goat. My name is roslin, and I am your baby goat.");
        response.setOutputSpeech(output);
        return response;
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        SpeechletResponse response = new SpeechletResponse();
        if ("chores".equalsIgnoreCase(request.getIntent().getName())) {
            PlainTextOutputSpeech output = new PlainTextOutputSpeech();
            output.setText("this needs to be linked to tasks");
            response.setOutputSpeech(output);
        }
        if ("habits".equalsIgnoreCase(request.getIntent().getName())) {
            PlainTextOutputSpeech output = new PlainTextOutputSpeech();
            output.setText("Habits are not yet integrated");
            response.setOutputSpeech(output);
        }
        if ("stretch".equalsIgnoreCase(request.getIntent().getName())) {
            SsmlOutputSpeech output = new SsmlOutputSpeech();
            try {
                int input = Integer.parseInt(request.getIntent().getSlot("Count").getValue());
                StringBuilder sb = new StringBuilder();
                sb.append("<speak>");
                sb.append("Going to begin ").append(input).append(" reps. You have five seconds to get into position. <break time=\"5s\"/>");
                for (int k = 0; k < input; k++) {
                    sb.append("Breathe and hold, and stretch. <break time=\"1s\"/> Focus. <break time=\"2500ms\"/>. Release. <break time=\"500ms\"/>");
                }
                sb.append(". You are finished.");
                sb.append("</speak>");
                output.setSsml(sb.toString());
            } catch (NumberFormatException nfe) {
                output.setSsml("<speak>failed to understand the intent, sorry</speak>");
            }
            response.setOutputSpeech(output);
        }
        if ("whatup".equalsIgnoreCase(request.getIntent().getName())) {
            SsmlOutputSpeech output = new SsmlOutputSpeech();
            output.setSsml("<speak>Nothing much mother <phoneme alphabet=\"ipa\" ph=\"fʌkər\">forka</phoneme></speak>");
            response.setOutputSpeech(output);
        }
        return response;
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    }
}
