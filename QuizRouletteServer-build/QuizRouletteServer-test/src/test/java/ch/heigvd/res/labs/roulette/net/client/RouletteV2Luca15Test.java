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
 * @author Luca Sivillica
 * @author Dany Tchente
 */
public class RouletteV2Luca15Test {


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "luca15")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "luca15")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "luca15")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "luca15")
    public void theRouletteClientShouldClearData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        client.loadStudent("Luca Sivillica");
        client.loadStudent("James Bond");
        assertEquals(2, client.getNumberOfStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "luca15")
    public void theRouletteClientShouldGetListOfStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        List<Student> students = new ArrayList<>();
        students.add(new Student("Luca Sivillica"));
        students.add(new Student("Indiana Jones"));
        students.add(new Student("James Bond"));

        client.loadStudents(students);

        List<Student> serverResponse = client.listStudents();

        assertEquals(students.size(), serverResponse.size());

        String fullNameStudent1;
        String fullNameStudent2;

        for (int i = 0; i < serverResponse.size(); ++i) {
            fullNameStudent1 = serverResponse.get(i).getFullname();
            fullNameStudent2 = students.get(i).getFullname();

            assertEquals(fullNameStudent1, fullNameStudent2);
        }
    }

    @Test
    @TestAuthor(githubId = "luca15")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

}
