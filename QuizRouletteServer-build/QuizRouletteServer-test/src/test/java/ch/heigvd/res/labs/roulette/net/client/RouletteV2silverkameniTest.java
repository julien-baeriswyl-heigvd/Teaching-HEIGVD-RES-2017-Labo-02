package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author silver kameni & nguefack zacharie
 */
public class RouletteV2silverkameniTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void theServerShouldClearDataCorrectly() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("silver");
    client.loadStudent("zach");
    assertEquals(2, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void theServerShouldBeAbleToClearItsDatabaseEvenWhenEmpty() throws Exception {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.clearDataStore();
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void listStudentsShouldCountFile() throws IOException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", roulettePair.getServer().getPort());
    assertEquals(0, client.listStudents().size());
    client.loadStudent("stephan Robert");
    assertEquals(1, client.listStudents().size());
    client.loadStudent("olivier Liechti");
    assertEquals(2, client.listStudents().size());
    client.loadStudent("jean Marc");
    assertEquals(3, client.listStudents().size());
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void clearTheDataStoreShouldputTheListEmpty() throws IOException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", roulettePair.getServer().getPort());
    client.loadStudent("cedric lankeu");
    client.clearDataStore();
    assertTrue(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void StudentsListShouldBeEmptyAtStart() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertTrue(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void RandomAfterclearTheDataStoreShouldThrowsException() throws IOException, EmptyStoreException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", roulettePair.getServer().getPort());
    client.loadStudent("John snow");
    client.clearDataStore();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }

  @Test
  @TestAuthor(githubId = {"zacharienguefack", "silverkameni"})
  public void TheClientShouldConnectToGoodServer() {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    int port = roulettePair.server.getPort();
    try {
      client.connect("www.dasilva123.fr", port);
    } catch (IOException ex) {
      Logger.getLogger(RouletteV2silverkameniTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    assertFalse(client.isConnected());
  }

}
