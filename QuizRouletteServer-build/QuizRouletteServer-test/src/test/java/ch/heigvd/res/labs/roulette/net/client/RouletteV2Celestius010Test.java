package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Benoit Gianinetti
 */
public class RouletteV2Celestius010Test {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "bgianinetti")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "bgianinetti")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "bgianinetti")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "bgianinetti")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

   @Test
   @TestAuthor(githubId = "bgianinetti")
   public void theServerShouldResetTheDataAfterAClearCommand() throws IOException {
       IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
       client.loadStudent("John Doe");
       client.clearDataStore();
       assertEquals(0, client.getNumberOfStudents());
   }

   @Test
   @TestAuthor(githubId = "bgianinetti")
   public void theServerShouldReturnAListOfStudents() throws IOException {
       IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
       List<Student> students = new ArrayList<Student>();

       students.add(new Student("John doe"));
       students.add(new Student("Foo bar"));

       client.loadStudents(students);
       assertEquals(students, client.listStudents());
   }

}
