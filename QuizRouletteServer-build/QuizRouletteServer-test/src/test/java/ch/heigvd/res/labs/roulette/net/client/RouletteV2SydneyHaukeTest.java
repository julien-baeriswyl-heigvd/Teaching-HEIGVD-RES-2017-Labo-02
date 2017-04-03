package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test class for specifying Roulette V2 Protocol
 *
 * Created by sydney and thuy-my on 26.03.17.
 */
public class RouletteV2SydneyHaukeTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void storeShouldBeCleared() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("sydney");
    client.loadStudent("hauke");
    assertEquals(client.getNumberOfStudents(), 2);
    client.clearDataStore();
    assertEquals(client.getNumberOfStudents(), 0);
  }

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void serverShouldListStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    List<Student> students = Arrays.asList(
      new Student("Sydney Hauke"),
      new Student("Thuy-My Tran")
    );
    client.loadStudents(students);
    List<Student> listedStudents = client.listStudents();
    assertEquals(listedStudents.size(), students.size());
    for (Student student : listedStudents) {
      assertTrue(students.contains(student));
    }
  }

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void serverShouldReturnCorrectProtocolVersion() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void serverShouldReturnCorrectNumberOfCommands() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.clearDataStore();
    client.listStudents();
    client.getProtocolVersion();
    client.loadStudent("Sydney Hauke");
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());

    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(socket.getOutputStream());
    input.readLine();
    
    output.println(RouletteV2Protocol.CMD_BYE);
    output.flush();
    String result = input.readLine();

    assertEquals("{\"status\":\"success\",\"numberOfCommands\":1}", result);
  }

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void serverShouldPrintTheNumberOfStudentsLoaded() throws IOException {
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());

    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(socket.getOutputStream());

    input.readLine();

    output.println(RouletteV2Protocol.CMD_LOAD);
    output.flush();
    input.readLine();
    output.println("Sydney Hauke");
    output.println("Thuy-My Tran");
    output.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();

    String result = input.readLine();

    assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":2}", result);
  }

  @Test
  @TestAuthor(githubId = "sydneyhauke")
  public void serverShouldPrintTheCorrectInfos() throws IOException {
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());

    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(socket.getOutputStream());

    input.readLine();

    output.println(RouletteV2Protocol.CMD_INFO);
    output.flush();
    String result = input.readLine();
    assertEquals("{\"protocolVersion\":\"2.0\",\"numberOfStudents\":0}", result);
  }
}
