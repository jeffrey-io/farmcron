package farm.bsg;

import java.util.List;

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

import farm.bsg.models.Chore;

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
            output.setText(TOP_CHORES(engine));
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

    public static String TOP_CHORES(ProductEngine engine) {
        StringBuilder sb = new StringBuilder();
        List<Chore> chores = engine.select_chore().to_list().inline_filter((chore) -> !chore.ready()).inline_order_by(new Chore.Ranking()).done();
        if (chores.size() == 0) {
            return "You have no chores to do in the near future. You are awesome, loved, and I worship you.";
        }
        int at = 0;
        int limit = Math.min(3, chores.size());

        if (chores.size() == 1) {
            sb.append("You have one thing to do today, which is " + chores.get(0).get("name"));
            return sb.toString();
        }
        sb.append("Your have " + limit + " chores to do which are ");
        for (Chore chore : chores) {
            if (at >= limit) {
                break;
            }
            at++;
            if (at > 1) {
                sb.append(", ");
                if (at == limit) {
                    sb.append("and ");
                }
            }
            sb.append(chore.get("name"));
        }

        return sb.toString();
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    }
}
