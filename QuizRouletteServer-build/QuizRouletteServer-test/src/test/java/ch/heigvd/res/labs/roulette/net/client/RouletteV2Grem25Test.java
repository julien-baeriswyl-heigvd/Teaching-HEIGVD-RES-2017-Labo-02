package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Zanone Jérémie (Grem25) & Wojciech Myszkorowski
 */
public class RouletteV2Grem25Test {

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Rule
   public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

   @Test
   @TestAuthor(githubId = "Grem25")
   public void theStudentsListFromServerMustMatch() throws IOException, Throwable {
      List<Student> students = new ArrayList<>();
      students.add(new Student("alice"));
      students.add(new Student("bob"));
      students.add(new Student("eve"));

      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      roulettePair.client.loadStudents(students);
      List<Student> test = client.listStudents();

      assertEquals(students.size(), test.size());
      for (Student s : students) {
         assertTrue(test.contains(s));
      }
   }

   @Test
   @TestAuthor(githubId = "Grem25")
   public void studentsMustBeEmptyAfterClear() throws IOException, Throwable {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      client.loadStudent("alice");
      client.loadStudent("bob");
      client.loadStudent("eve");

      client.clearDataStore();
      assertEquals(0, client.getNumberOfStudents());

   }

   @Test
   @TestAuthor(githubId = "Grem25")
   public void theResponseAfterByeIsCorrect() throws IOException, Throwable {
      Socket clientSocket = new Socket("localhost", roulettePair.server.getPort());
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
      in.readLine();

      out.println(RouletteV2Protocol.CMD_BYE);
      out.flush();

      String test = in.readLine();

      assertEquals(test, "{\"status\":\"success\",\"numberOfCommands\":1}");

   }

   @Test
   @TestAuthor(githubId = "CoolPolishGuy")
   public void correctNumberStudentsInStore() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      assertEquals(0, client.getNumberOfStudents());
      client.loadStudent("Myszkorowski");
      assertEquals(1, client.getNumberOfStudents());
      client.loadStudent("Zanone");
      assertEquals(2, client.getNumberOfStudents());

   }

   @Test
   @TestAuthor(githubId = "CoolPolishGuy")
   public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
      assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
   }

}
