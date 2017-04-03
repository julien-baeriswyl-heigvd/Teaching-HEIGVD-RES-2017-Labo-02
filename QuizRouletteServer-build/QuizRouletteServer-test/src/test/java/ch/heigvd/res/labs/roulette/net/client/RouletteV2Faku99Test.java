package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Lucas ELISEI (faku99)
 * @author David TRUAN  (Daxidz)
 */
public class RouletteV2Faku99Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void serverShouldRunDuringTests() {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void clientShoudBeConnectedDuringTests() {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void itShouldBePossibleForAClientToConnectToV2Server() throws Exception {
        int port = roulettePair.getServer().getPort();

        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());

        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void serverShouldReturnCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void serverShouldClearDatastore() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("David");
        client.loadStudent("Lucas");
        assertNotEquals(0, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = { "Daxidz", "faku99" })
    public void serverShouldListStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        students.add(new Student("David TRUAN"));
        students.add(new Student("Lucas ELISEI"));

        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudents(students);
        assertTrue(client.listStudents().equals(students));
    }
}
