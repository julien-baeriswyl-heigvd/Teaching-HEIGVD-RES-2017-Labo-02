package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * @author Yann Mahmoudi
 * @author Marie Lemdjo
 */
public class RouletteV2McMoudiTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  private static int STUDENT_NUMBER = 10;

  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theServerShouldListenToTheProperPort() throws IOException {
    RouletteServer rs = new RouletteServer(2613,RouletteV2Protocol.VERSION);
    rs.startServer();
    assertEquals(rs.getPort(), 2613); //FIXME no static port for v2 yet in RouletteV2Protocol
    rs.stopServer();
  }

  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theClientShouldRunTheRightProtocolVersion() throws IOException {
    assertEquals(roulettePair.getClient().getProtocolVersion(),RouletteV2Protocol.VERSION);
  }


  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theClientShouldBeAbleToConnect() throws IOException {
    IRouletteV2Client c = new RouletteV2ClientImpl();
    assertFalse(c.isConnected());
    c.connect("localhost",roulettePair.getServer().getPort());
    assertTrue(c.isConnected());
  }

  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theClientCanLoadAndRetrieveAStudentList() throws IOException{
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    List<Student> studentList = populate(client);
    assertEquals(client.listStudents(),studentList);

  }

  @Test
  @TestAuthor(githubId = "McMoudi")
    public void theClientCanPopulateAndResetServerData() throws IOException{
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      populate(client);
      assertEquals(client.getNumberOfStudents(),STUDENT_NUMBER); //check if students are well inserted
      client.clearDataStore();
      assertEquals(client.getNumberOfStudents(),0); //check if well cleared
  }


  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theClientShouldBeConnectedWhenTestStarts() {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theClientDisconnectsProperly() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      client.disconnect();
      assertFalse(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = "McMoudi")
  public void theServerShouldReturnAnEmptyListWhenTestStart() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      assertTrue(client.listStudents().isEmpty());
  }

  private List<Student> populate(IRouletteV2Client client) throws IOException {
    List<Student> studentList = new ArrayList<>();

    for (char c = 0; c < STUDENT_NUMBER;++c){
      studentList.add(new Student("stud-"+c));
    }

    client.loadStudents(studentList);

    return studentList;
  }
}
