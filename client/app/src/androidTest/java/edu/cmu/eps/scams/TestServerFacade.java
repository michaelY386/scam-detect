package edu.cmu.eps.scams;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import edu.cmu.eps.scams.communication.ServerFacade;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.IncomingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.utilities.RandomUtility;
import edu.cmu.eps.scams.utilities.TimestampUtility;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/*
* Test the server API calls over HTTP.
* */
@RunWith(AndroidJUnit4.class)
public class TestServerFacade {

    private ServerFacade server;

    @Before
    public void setup() {
        this.server = new ServerFacade();
    }

    @Test
    public void testFailedInitialize() {
        try {
            String identifier = String.format("Test_%s", RandomUtility.getString(6));
            String secret = String.format("Test_%s", RandomUtility.getString(6));
            server.init(
                    true,
                    identifier,
                    secret,
                    "{}",
                    "{}"
            );
            Assert.fail("Unexpected successful login");
        } catch (Exception e) {
        }
    }

    @Test
    public void testInitialize() {
        try {
            String identifier = String.format("Test_%s", RandomUtility.getString(6));
            String secret = String.format("Test_%s", RandomUtility.getString(6));
            server.init(
                    false,
                    identifier,
                    secret,
                    "{}",
                    "{}"
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSendMessage() {
        try {
            String identifier = String.format("Test_%s", RandomUtility.getString(6));
            String secret = String.format("Test_%s", RandomUtility.getString(6));
            server.init(
                    false,
                    identifier,
                    secret,
                    "{}",
                    "{}"
            );
            OutgoingMessage message = new OutgoingMessage();
            message.setRecipient(identifier);
            message.getProperties().put("test", "Hello World");
            server.sendMessage(message);
            List<IncomingMessage> results = server.retrieveMessages();
            assertThat(results.size(), greaterThan(0));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSendTelemetry() {
        try {
            String identifier = String.format("Test_%s", RandomUtility.getString(6));
            String secret = String.format("Test_%s", RandomUtility.getString(6));
            server.init(
                    false,
                    identifier,
                    secret,
                    "{}",
                    "{}"
            );
            Telemetry toSend = new Telemetry("test.data", TimestampUtility.now());
            toSend.getProperties().put("test", "Hello World");
            server.sendTelemetry(toSend);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRetrieveClassifierParameters() {
        try {
            String identifier = String.format("Test_%s", RandomUtility.getString(6));
            String secret = String.format("Test_%s", RandomUtility.getString(6));
            server.init(
                    false,
                    identifier,
                    secret,
                    "{}",
                    "{}"
            );
            ClassifierParameters parameters = server.retrieveClassifierParameters();
            assertThat(parameters.getContent().length(), greaterThan(0));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}