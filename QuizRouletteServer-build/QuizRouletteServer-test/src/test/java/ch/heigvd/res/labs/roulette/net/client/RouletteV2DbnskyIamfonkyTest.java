package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2DbnskyIamfonkyTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "dbnsky")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "dbnsky")
  public void theServerShouldHaveNoStudentsAfterClear() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Iamfonky");
    client.loadStudent("Dbnsky");
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "dbnsky")
  public void theServerShouldReturnSpecificStudentList () throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    List<Student> studentList = new ArrayList<>();
    studentList.add(new Student("Iamfonky"));
    studentList.add(new Student("Dbnksy"));
    client.loadStudents(studentList);
    List<Student> studentsListStore = client.listStudents();
    assertEquals(studentList, studentsListStore);
  }

}