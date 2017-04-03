package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the behaviour of the RouletteV2
 * protocol.
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public class RouletteV2EdwardransomeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void clientShouldBeAbleToConnectToRouletteServer() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }


    @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void clearShouldRemoveAllLoadedStudents() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("bob");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("michel");
        assertEquals(2, client.getNumberOfStudents());  
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }
    
     @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void clearShouldMakeStudentListEmpty() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("bob");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("michel");
        assertEquals(2, client.getNumberOfStudents());  
        client.clearDataStore();
        assertTrue(client.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void listShouldReturnAllStudents() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("bob");
        client.loadStudent("michel");
        assertEquals(2, client.getNumberOfStudents());
        List<Student> students = client.listStudents();
        assertEquals(students.get(0).getFullname(), "bob");
        assertEquals(students.get(1).getFullname(), "michel");
    }
    
    @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void listShouldReturnEmptyListIfNoStudentsLoaded() throws IOException{
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        List<Student> students = client.listStudents();
        assertEquals(0, students.size());
    }
    
    @Test
    @TestAuthor(githubId = {"edwardransome", "oceanos1"})
    public void serverShouldReturnCorrectVersionInfo() throws IOException{
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        assertEquals(roulettePair.getClient().getProtocolVersion(), RouletteV2Protocol.VERSION);
    }
    
}
