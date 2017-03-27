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
    public void clientShouldBeAbleToListAndClearStudents () throws IOException
    {
        List<Student> students = new ArrayList<Student>();
        students.add(new Student("Julien Baeriswyl"));
        students.add(new Student("Iando Rafidimalala"));

        IRouletteV2Client client = roulettePair.getClient();

        for (Student s : students)
        {
            client.loadStudent(s.getFullname());
        }

        assertEquals(students.size(), client.getNumberOfStudents());
        assertEquals(students,        client.listStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
        assertTrue(client.listStudents().isEmpty());
    }
}
