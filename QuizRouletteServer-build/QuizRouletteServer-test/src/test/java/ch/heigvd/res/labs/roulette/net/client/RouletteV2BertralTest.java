package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Friant Antoine, Stalder Lawrence
 */
public class RouletteV2BertralTest {
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Bertral")
    public void clearDataStoreShouldSetNumberOfStudentsToZero() throws Exception {
        RouletteV2ClientImpl client = new RouletteV2ClientImpl();
        client.connect("localhost", roulettePair.getServer().getPort());
        client.loadStudent("Gérard");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "Bertral")
    public void listStudentsShouldListAllStudentsStoredOnTheServer() throws Exception {
        RouletteV2ClientImpl client = new RouletteV2ClientImpl();
        client.connect("localhost", roulettePair.getServer().getPort());
        client.loadStudent("Gérard");
        client.loadStudent("Gertrude");
        client.loadStudent("Robert");

        assertEquals(3, client.listStudents().size());
        assertEquals("Gérard", client.listStudents().get(0).getFullname());
        assertEquals("Gertrude", client.listStudents().get(1).getFullname());
        assertEquals("Robert", client.listStudents().get(2).getFullname());
        client.clearDataStore();
    }

    @Test
    @TestAuthor(githubId = "Bertral")
    public void protocolVersionShouldBeUpdated() throws Exception {
        assertEquals("2.0", roulettePair.getClient().getProtocolVersion());
    }
}
