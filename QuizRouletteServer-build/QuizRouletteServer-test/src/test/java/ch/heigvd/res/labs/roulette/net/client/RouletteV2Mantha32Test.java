package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.client.IRouletteV2Client;
import ch.heigvd.schoolpulse.TestAuthor;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * @author Julien  Baeriswyl    (julien.baeriswyl@heig-vd.ch,         julien-baeriswyl-heigvd)
 * @author Iando   Rafidimalala (iando.rafidimalalathevoz@heig-vd.ch, Mantha32)
 * @since  2017-03-27
 */
public class RouletteV2Mantha32Test
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Mantha32")
    public void clientShouldBeAbleToRetrieveProtocolVersionFromServer () throws IOException
    {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "Mantha32")
    public void clientNumberOfStudentsAndStudentsListShouldMatch () throws IOException
    {
        // BEGIN: TO REPLACE WHEN IRouletteV2Client WILL BE AVAILABLE THROUGH RESOURCES
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", roulettePair.getServer().getPort());
        // END:   TO REPLACE WHEN IRouletteV2Client WILL BE AVAILABLE THROUGH RESOURCES

        assertTrue(client.isConnected());
        assertEquals(client.listStudents().size(), client.getNumberOfStudents());

        List<Student> students;

        for (int i = 1; i <= 10; ++i)
        {
            client.loadStudent("Iando" + i);
            students = client.listStudents();
            assertTrue(students.size() > 0);
            assertEquals(students.size(), client.getNumberOfStudents());
        }
    }

    @Test
    @TestAuthor(githubId = "Mantha32")
    public void clientShouldBeAbleToListAndClearStudents () throws IOException
    {
        // BEGIN: TO REPLACE WHEN IRouletteV2Client WILL BE AVAILABLE THROUGH RESOURCES
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", roulettePair.getServer().getPort());
        // END:   TO REPLACE WHEN IRouletteV2Client WILL BE AVAILABLE THROUGH RESOURCES

        assertTrue(client.isConnected());

        List<Student> students       = client.listStudents(),
                      futureStudents = new ArrayList<Student>();

        futureStudents.add(new Student("Julien Baeriswyl"));
        futureStudents.add(new Student("Iando Rafidimalala"));

        for (Student s : futureStudents)
        {
            students.add(s);
            client.loadStudent(s.getFullname());
        }

        assertEquals(students.size(), client.getNumberOfStudents());
        assertEquals(students,        client.listStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
        assertTrue(client.listStudents().isEmpty());
    }
}
