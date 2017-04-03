package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.QuizRouletteServer;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
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
 * @author Christpher Meier
 * @author Daniel Palumbo
 */
public class RouletteV2cmeierTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"c-meier", "danpa32"})
  public void theServerShouldHaveTheRightPort() throws IOException {
    RouletteServer server = new RouletteServer(RouletteV2Protocol.DEFAULT_PORT, RouletteV2Protocol.VERSION);
    server.startServer();
    assertEquals(2613, server.getPort());
    server.stopServer();
  }

  @Test
  @TestAuthor(githubId = {"c-meier", "danpa32"})
  public void theServerSouldHaveAnEmptyListOfStudentsAtStart() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertTrue(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = {"c-meier", "danpa32"})
  public void theServerShouldFetchTheListOfStudentsInTheStore() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

    List<Student> listStudents = new ArrayList<>();
    listStudents.add(new Student("Daniel Palumbo"));
    listStudents.add(new Student("Christopher Meier"));
    client.loadStudents(listStudents);

    assertEquals(listStudents, client.listStudents());
  }

  @Test
  @TestAuthor(githubId = {"c-meier", "danpa32"})
  public void theServerShouldHaveZeroStudentsAfterClearDataStore() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

    client.loadStudent("daniel");
    client.loadStudent("christopher");

    assertEquals(2, client.getNumberOfStudents());

    client.clearDataStore();

    assertEquals(0, client.getNumberOfStudents());
  }


  
}
