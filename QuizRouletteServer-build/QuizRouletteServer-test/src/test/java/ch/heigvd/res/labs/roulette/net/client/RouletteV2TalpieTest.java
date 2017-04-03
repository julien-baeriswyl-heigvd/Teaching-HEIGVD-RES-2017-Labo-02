package ch.heigvd.res.labs.roulette.net.client;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 * 
 * @author Tony Clavien
 */
public class RouletteV2TalpieTest {
	
	  @Rule
	  public ExpectedException exception = ExpectedException.none();

	  @Rule
	  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
	  	  	  
	  //Info
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
	    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
	  }
	  
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void theServerShouldCountStudents() throws IOException {
	    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
	    assertEquals(0, client.getNumberOfStudents());
	    client.loadStudent("sacha");
	    assertEquals(1, client.getNumberOfStudents());
	    client.loadStudent("olivier");
	    assertEquals(2, client.getNumberOfStudents());
	    client.loadStudent("fabienne");
	    assertEquals(3, client.getNumberOfStudents());
	  }
	  
	  // CLear
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void theServerShouldClearStudents() throws IOException {
	    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
	    assertEquals(0, client.getNumberOfStudents());
	    client.loadStudent("sacha");
	    assertEquals(1, client.getNumberOfStudents());
	    client.loadStudent("olivier");
	    assertEquals(2, client.getNumberOfStudents());
	    client.loadStudent("fabienne");
	    assertEquals(3, client.getNumberOfStudents());
	    client.clearDataStore();
	    assertEquals(0, client.getNumberOfStudents());
	  }
	  
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void ClearOnEmptySetShouldNotthrowsException() throws IOException {
	    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
	    client.clearDataStore();
	  }
	  
	  // List
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void theServerShouldGiveListStudents() throws IOException {
	    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
	    client.loadStudent("sacha");
	    client.loadStudent("olivier");
	    client.loadStudent("fabienne");
	    List<String> names = new ArrayList<>();
	    names.add("sacha");
	    names.add("olivier");
	    names.add("fabienne");
	    List<Student> ls = client.listStudents();
	    List<String> lsName = new ArrayList<>();
	    for(Student s : ls)
	    {
	    	lsName.add(s.getFullname());
	    }
	    
	    assertEquals(true, lsName.containsAll(names));
	  }
	  
	  @Test
	  @TestAuthor(githubId = "Talpie")
	  public void theServerShouldGiveEmptyListStudents() throws IOException {
	    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
	    List<Student> ls = client.listStudents();
	    
	    assertEquals(true, ls.isEmpty());
	  }

}
