package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Ludovic Richard, Luana Martelli
 */
public class RouletteV2LuanaMartelliTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "LuanaMartelli")
    public void theServerShouldBeEmptyAfterAClean() throws IOException {
        final int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();

        client.connect("localhost", port);
        client.loadStudent("John Smith");
        client.clearDataStore();
        assertEquals(client.getNumberOfStudents(), 0);
        client.disconnect();
    }

    @Test
    @TestAuthor(githubId = "LuanaMartelli")
    public void theServerShouldSendTheListOfStudentsCorrectly() throws IOException {
        final int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        List<Student> newStudents = new ArrayList();
        List<Student> returnedList;

        newStudents.add(new Student("Ludovic Richard"));
        newStudents.add(new Student("Luana Martelli"));

        client.connect("localhost", port);
        client.loadStudent("John Smith");
        client.loadStudents(newStudents);
        assertEquals(client.getNumberOfStudents(), 3);

        newStudents.add(new Student("John Smith"));
        returnedList = client.listStudents();

        for(Student s : newStudents){
            assertTrue(returnedList.contains(s));
        }
        client.disconnect();
    }

    @Test
    @TestAuthor(githubId = "LuanaMartelli")
    public void theServerShouldGiveCorrectlyTheVersion() throws IOException {
        final int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();

        client.connect("localhost", port);
        assertEquals(client.getProtocolVersion(), RouletteV2Protocol.VERSION);

        client.disconnect();
    }
}
