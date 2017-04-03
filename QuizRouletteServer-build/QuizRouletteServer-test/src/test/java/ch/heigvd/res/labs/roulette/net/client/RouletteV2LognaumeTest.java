package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Guillaume Milani
 */
public class RouletteV2LognaumeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Ignore
    @Test
    @TestAuthor(githubId = "lognaume")
    public void theRouletteServerShouldUsePort2613() throws IOException {
        assertEquals(roulettePair.server.getPort(), 2613);
    }

    @Test
    @TestAuthor(githubId = "lognaume")
    public void theServerShouldUseTheCorrectVersion() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        String version = client.getProtocolVersion();
        assertEquals(version, "2.0");
    }

    @Test
    @TestAuthor(githubId = "lognaume")
    public void theClearCommandShouldClearData() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();

        client.loadStudent("John Doe");
        client.loadStudent("Jane Doe");
        assertEquals(client.getNumberOfStudents(), 2);

        client.clearDataStore();

        assertEquals(client.getNumberOfStudents(), 0);
    }

    @Test
    @TestAuthor(githubId = "lognaume")
    public void errorShouldBeThrownWhenPickingAfterClear() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        client.loadStudent("John Doe");
        client.loadStudent("Jane Doe");

        client.clearDataStore();

        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    @Test
    @TestAuthor(githubId = "lognaume")
    public void theServerShouldReturnTheGoodJSonList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        // Add students using the client
        client.loadStudent("John Doe");
        client.loadStudent("Jane Doe");

        // Read directly from the server
        Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        // Get HELLO message
        in.readLine();

        // Send LIST command
        out.append(RouletteV2Protocol.CMD_LIST + System.lineSeparator());
        out.flush();

        String response = in.readLine();
        String expectedResponse = "{\"students\":[{\"fullname\":\"John Doe\"},{\"fullname\":\"Jane Doe\"}]}";

        assertEquals(response, expectedResponse);
    }

    @Test
    @TestAuthor(githubId = "lognaume")
    public void theServerShouldRespondWhenLoaded() throws IOException {
        RouletteServer server = new RouletteServer(RouletteV2Protocol.DEFAULT_PORT, RouletteV2Protocol.VERSION);
        server.startServer();

        // Connect directly to the server        
        Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        // Get HELLO message
        in.readLine();

        // Send LOAD command
        out.append(RouletteV2Protocol.CMD_LOAD + System.lineSeparator());
        out.flush();

        // Get instructions message
        in.readLine();

        // Load 2 students
        out.append("John Doe" + System.lineSeparator());
        out.append("Jane Doe" + System.lineSeparator());

        out.append(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + System.lineSeparator());
        out.flush();

        String response = in.readLine();
        String expectedResponse = "{\"status\":\"success\",\"numberOfNewStudents\":2}";

        assertEquals(response, expectedResponse);
        server.stopServer();
    }
    
    @Test
    @TestAuthor(githubId = "lognaume")
    public void theServerShouldGiveNumberOfCommandsWhenBye() throws IOException {
        // Connect directly to the server
        Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        // Get HELLO message
        in.readLine();

        // Send INFO command
        out.append(RouletteV2Protocol.CMD_INFO + System.lineSeparator());
        out.flush();

        // Get response
        in.readLine();

         // Send HELP command
        out.append(RouletteV2Protocol.CMD_HELP + System.lineSeparator());
        out.flush();
        
        in.readLine();
        
        out.append(RouletteV2Protocol.CMD_BYE + System.lineSeparator());
        out.flush();
        
        String response = in.readLine();
        String expectedResponse = "{\"status\":\"success\",\"numberOfCommands\":3}";

        assertEquals(response, expectedResponse);
    }
}
