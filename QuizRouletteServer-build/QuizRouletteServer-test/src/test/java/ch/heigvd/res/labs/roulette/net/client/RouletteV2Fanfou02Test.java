package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Francois Quellec, Pierre Rochat
 */
public class RouletteV2Fanfou02Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Fanfou02")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "psrochat")
    public void itShouldBePossibleToClearDataStore() throws IOException {

        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("anakin");
        client.loadStudent("han");
        client.loadStudent("jarjar");
        assertEquals(3, client.getNumberOfStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "psrochat")
    public void theServerShouldFetchTheListOfStudentsInTheStore() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        List<Student> students = new ArrayList<>();
        assertEquals(0, client.getNumberOfStudents());
        students.add(new Student("anakin"));
        students.add(new Student("han"));
        students.add(new Student("jarjar"));
        client.loadStudents(students);

        assertEquals(3, client.getNumberOfStudents());

        List<Student> answer;
        answer = client.listStudents();

        for(int i = 0; i < students.size(); i++) {
            assertTrue(students.get(i).equals(answer.get(i)));
        }

    }
}