/*
 * Lab : 02
 * Author : Nathalie Mégevand
 * Date : 24 mars 2017
 * File : RouletteV2nathalie_megevandTest.java
 * 
 *  Test class that verifies the implementation of the Roulette protocol v2 
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RouletteV2nathalie_megevandTest {

  private final String serverName = "loclhost";
  private final int portV2 = 2613;
  private final int portV1 = RouletteV1Protocol.DEFAULT_PORT;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "nathalie-megevand")
  public void INFOShouldReturnCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "nathalie-megevand")
  public void ConnectionOnV1PortShouldNotReturnV2VersionNumber() throws IOException {
    // Creating our own client to intercept the answer of sevrer
    Socket communicationSocket = new Socket("localhost", roulettePair.getServer().getPort());
    // Openning an input and an ouput stream
    PrintWriter printer = new PrintWriter(
      new OutputStreamWriter(communicationSocket.getOutputStream()));
    BufferedReader buffReader = new BufferedReader(
      new InputStreamReader(communicationSocket.getInputStream()));

    // Reading the welcomme message on the serveur
    buffReader.readLine();

    printer.println(RouletteV1Protocol.CMD_INFO);
    printer.flush();

    // Parsing the answer for the version number of the protocol
    InfoCommandResponse info = JsonObjectMapper.parseJson(buffReader.readLine(),
      InfoCommandResponse.class);
    assertNotEquals(RouletteV1Protocol.VERSION, info.getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "nathalie-megevand")
  public void CLEARShouldLeaveServerWithAnEmptyList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Frodo Baggins");
    client.loadStudent("Bilbo Baggins");
    // Asking to clear the list
    client.clearDataStore();
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "nathalie-megevand")
  public void LOADShouldReturnCorrectNUmberOfStudents() throws IOException {
    // Creating our own client to intercept the answer of sevrer
    Socket communicationSocket = new Socket("localhost", roulettePair.getServer().getPort());
    // Openning an input and an ouput stream
    PrintWriter printer = new PrintWriter(
      new OutputStreamWriter(communicationSocket.getOutputStream()));
    BufferedReader buffReader = new BufferedReader(
      new InputStreamReader(communicationSocket.getInputStream()));

    // Reading the welcomme message on the serveur
    buffReader.readLine();

    // Data to be send
    ArrayList<Student> listOfStudents = new ArrayList<Student>();
    listOfStudents.add(new Student("Radagast the Brown"));
    listOfStudents.add(new Student("Gandalf the Grey"));
    listOfStudents.add(new Student("Saruman the White"));

    // Sends the signal for the beginning of the loading process
    printer.println(RouletteV2Protocol.CMD_LOAD);
    printer.flush();

    // Tests if the server is ready and waiting for the data
    if (!buffReader.readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_LOAD_START)) {
      throw new IOException("Error during the loading !");
    }

    // Sends the students name by name
    for (Student currentStudent : listOfStudents) {
      // Do not sends empty strings
      if (currentStudent.getFullname().length() >= 1) {
        // Sends full name
        printer.println(currentStudent.getFullname());
      }
    }

    // Signifies the end of the transfert with ENDOFDATA
    printer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    printer.flush();

    // Check the confirmation line
    assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":3}",
      buffReader.readLine());
  }

  @Test
  @TestAuthor(githubId = "nathalie-megevand")
  public void BYEShouldReturnCorrectNumberOfCommend() throws IOException {
    // Creating our own client to intercept the answer of sevrer
    Socket communicationSocket = new Socket("localhost", roulettePair.getServer().getPort());
    // Openning an input and an ouput stream
    PrintWriter printer = new PrintWriter(
      new OutputStreamWriter(communicationSocket.getOutputStream()));
    BufferedReader buffReader = new BufferedReader(
      new InputStreamReader(communicationSocket.getInputStream()));

    // Reading the welcomme message on the serveur
    buffReader.readLine();

    // Sends the signal for the beginning of the loading process
    printer.println(RouletteV2Protocol.CMD_LOAD);
    printer.flush();

    // Tests if the server is ready and waiting for the data
    if (!buffReader.readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_LOAD_START)) {
      throw new IOException("Error during the loading !");
    }

    printer.println("Fingolfin");
    printer.flush();

    // Signifies the end of the transfert with ENDOFDATA
    printer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    printer.flush();
    // Reading confirmation
    buffReader.readLine();

    printer.println(RouletteV2Protocol.CMD_CLEAR);
    printer.flush();
    buffReader.readLine();

    printer.println(RouletteV2Protocol.CMD_BYE);
    printer.flush();

    // Check the confirmation line
    assertEquals("{\"status\":\"success\",\"numberOfCommands\":3}",
      buffReader.readLine());

  }

}
