package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * @author Yosra Harbaoui
 * @author Arthur Passuello
 */
public class RouletteV2X4l1b1Test {

    private PrintWriter writer = null;
    private BufferedReader reader = null;


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"yosra-harbaoui", "X4l1b1"})
    public void theServerDefaultPortShouldBeRight() throws IOException {
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, 2613);
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "yosra-harbaoui", "X4l1b1"})
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "yosra-harbaoui", "X4l1b1"})
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }


    @Test
    @TestAuthor(githubId = {"yosra-harbaoui", "X4l1b1"})
    public void theServerListSouldBeEmptyAtSetup() throws IOException {

        IRouletteV2Client c2 = (IRouletteV2Client) roulettePair.getClient();
        assertTrue(c2.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = {"yosra-harbaoui", "X4l1b1"})
    public void theServerShouldGetTheNumberOfStudentInTheStore() throws IOException {

        IRouletteV2Client c2 = (IRouletteV2Client) roulettePair.getClient();

        c2.loadStudent("Yosra");
        c2.loadStudent("Arthur");

        assertEquals(2  , c2.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"yosra-harbaoui", "X4l1b1"})
    public void theServerListShouldBeEmptyAfterClearDataStore() throws IOException {

        IRouletteV2Client c2 = (IRouletteV2Client) roulettePair.getClient();

        c2.loadStudent("Yosra");
        c2.loadStudent("Arthur");

        assertEquals(2, c2.getNumberOfStudents());

        c2.clearDataStore();

        assertEquals(0, c2.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"yosra-harbaoui", "X4l1b1"})
    public void theServerShouldFetchAList() throws IOException {

        IRouletteV2Client c2 = (IRouletteV2Client) roulettePair.getClient();

        List<Student> students = new ArrayList<>();

        students.add(new Student("Yosra"));
        students.add(new Student("Arthur"));

        c2.loadStudents(students);

        assertEquals(2, c2.getNumberOfStudents());
    }

}
