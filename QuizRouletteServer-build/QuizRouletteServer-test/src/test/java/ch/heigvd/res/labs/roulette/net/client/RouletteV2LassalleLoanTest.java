package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Loan Lassalle
 * @author Tano Iannetta
 */
public class RouletteV2LassalleLoanTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldSendAnStudentRandomly() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> listStudents = new ArrayList<>();
        Collections.addAll(listStudents, new Student("Tano Iannetta"),
                new Student("Loan Lassalle"), new Student("Wojciech Myszkorowski"),
                new Student("Jérémie Zanone"));

        client.loadStudents(listStudents);
        assertNotNull(client.pickRandomStudent());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldReturnDataStoreCleared() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> listStudents = new ArrayList<>();
        Collections.addAll(listStudents, new Student("Tano Iannetta"), new Student("Loan Lassalle"),
                new Student("Wojciech Myszkorowski"), new Student("Jérémie Zanone"));

        client.loadStudents(listStudents);
        client.clearDataStore();

        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldSendTheListOfStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> listStudentExpected = new ArrayList<>();
        Collections.addAll(listStudentExpected, new Student("Tano Iannetta"),
                new Student("Loan Lassalle"), new Student("Wojciech Myszkorowski"),
                new Student("Jérémie Zanone"));

        final int sizelistStudentExpected = listStudentExpected.size();

        client.loadStudents(listStudentExpected);
        List<Student> listStudents = client.listStudents();

        for (int i = 0; i < sizelistStudentExpected; ++i) {
            assertEquals(listStudentExpected.get(i).getFullname(), listStudents.get(i).getFullname());
        }
    }
}