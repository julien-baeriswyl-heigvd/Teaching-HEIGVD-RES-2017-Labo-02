
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;
import java.util.ArrayList;
import java.util.List;

/**
 * we add some test with method which is not defined in client class RouletteV2Impl, so we have to
 * add them if we want to test them correctly.  
 * 
 * @author annie Dongmo, doriane Kaffo
 */
public class RouletteV2AnnieSandraTest {
     @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sandra");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("annie");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }
  
  
  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldCountTheCorrectNumberOfNewStudent() throws IOException, EmptyStoreException {
    
    Socket client = new Socket("localhost",roulettePair.getServer().getPort());
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
    
    reader.readLine();
    writer.printf(RouletteV2Protocol.CMD_LOAD + "\n");
    writer.flush();
    reader.readLine();
    writer.printf("anne \n");
    writer.flush();
    writer.printf("rose \n");
    writer.flush();
    writer.printf("dongmo \n");
    writer.flush();
    writer.printf("sandra \n");
    writer.flush();
    writer.printf(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
    writer.flush();
    String line = reader.readLine();
    writer.printf(RouletteV2Protocol.CMD_BYE + "\n");
    writer.flush();
    reader.readLine();
    
    String correct = "false";
    if(line.contains("4")){
       correct = "true";
    }
    assertEquals("true", correct);
  }
  
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendStatusOfCommand() throws IOException, EmptyStoreException{
     Socket client = new Socket ("localhost",roulettePair.getServer().getPort());
     PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
     BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
     
     reader.readLine();
     writer.printf(RouletteV2Protocol.CMD_BYE + "\n");
     writer.flush();
     String line = reader.readLine();
     boolean correct = false;
     if(line.contains("success")){
        correct = true;
     }

     assertEquals(true,correct);
  }
  
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendTheCorrectNumberOfCommand() throws IOException, EmptyStoreException {
    Socket client = new Socket ("localhost",roulettePair.getServer().getPort());
     PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
     BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
     
     reader.readLine();
     writer.printf(RouletteV2Protocol.CMD_INFO + "\n");
     writer.flush();
     reader.readLine();
     writer.printf(RouletteV2Protocol.CMD_LOAD + "\n");
     writer.flush();
     reader.readLine();
     writer.printf("biphaga" + "\n");
     writer.flush();
     writer.printf(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
     writer.flush();
     reader.readLine();
     writer.printf(RouletteV2Protocol.CMD_BYE + "\n");
     writer.flush();
     String line = reader.readLine();
     boolean correct = false;
     if(line.contains("3")){
        correct = true;
     }
    assertEquals(true, correct);
  }
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldHaveZeroStudentAfterClearCommand() throws IOException, EmptyStoreException{
     IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
     client.loadStudent("terri");
     client.clearDataStore();
     assertEquals(0, client.getNumberOfStudents());
	
  }
  
  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldReturnTheCorrectStudentList() throws IOException, EmptyStoreException{
     IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
     client.clearDataStore();
     List<Student> students = new ArrayList<>();
     students.add(new Student("erica"));
     students.add(new Student ("alexandra"));
     students.add(new Student("alehandro"));
     client.loadStudents(students);
     List<Student> returnStudents = client.listStudents();
     assertEquals(students, returnStudents);
	
  }
  
    
}
