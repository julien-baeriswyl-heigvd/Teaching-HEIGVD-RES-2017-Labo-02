package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Max Caduff
 */
public class RouletteV2maxcaduffTest {


    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    @Test
    @TestAuthor(githubId = "maxcaduff")
    public void theClientShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "maxcaduff")
    public void theServerShouldListAndClearCorrectly() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("Jaques");
        client.loadStudent("Georges");
        client.loadStudent("Robert");
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(3, numberOfStudents);
        String listOfStudents = client.listStudents().toString();
        assertTrue( listOfStudents.contains("Jaques") && listOfStudents.contains("Georges") && listOfStudents.contains("Robert"));
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "maxcaduff")
    public void theServerAndClientShouldWorkWithTheDefaultPort() throws Exception {
        RouletteServer server = new RouletteServer(RouletteV2Protocol.DEFAULT_PORT, RouletteV2Protocol.VERSION);
        server.startServer();
        RouletteV2ClientImpl client = new RouletteV2ClientImpl();
        client.connect("localhost", RouletteV2Protocol.DEFAULT_PORT );
        assertTrue(client.isConnected());
        server.stopServer();
    }



}
