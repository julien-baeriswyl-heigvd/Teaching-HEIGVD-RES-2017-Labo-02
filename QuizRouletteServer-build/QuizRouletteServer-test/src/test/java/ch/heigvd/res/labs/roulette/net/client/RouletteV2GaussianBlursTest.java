package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Arnold von Bauer Gauss
 */
public class RouletteV2GaussianBlursTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "gaussianblurs")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "gaussianblurs")
    public void theServerShouldListenToTheCorrectPort() {
        RouletteServer server = new RouletteServer(RouletteV2Protocol.DEFAULT_PORT, RouletteV2Protocol.VERSION);
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, server.getPort());
    }

    @Test
    @TestAuthor(githubId = "gaussianblurs")
    public void theServerShouldHaveZeroStudentsAfterClear() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("Albert");
        client.loadStudent("Gerard");
        client.loadStudent("Roger");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "gaussianblurs")
    public void theServerShouldListStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        List<Student> addStudents = new ArrayList<>();
        addStudents.add(new Student("Albert"));
        addStudents.add(new Student("Gerard"));
        addStudents.add(new Student("Roger"));
        client.loadStudents(addStudents);
        List<Student> students = client.listStudents();
        assertEquals(addStudents, students);
    }

}
