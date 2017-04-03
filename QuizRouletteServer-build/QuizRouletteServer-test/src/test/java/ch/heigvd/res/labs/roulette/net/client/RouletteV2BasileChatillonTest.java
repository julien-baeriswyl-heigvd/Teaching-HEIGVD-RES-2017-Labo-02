
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Basile Chatillon
 * @author Nicolas Rod
 */
public class RouletteV2BasileChatillonTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
  
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    @Test
    @TestAuthor(githubId = {"BasileChatillon", "Rhod3"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
        
    @Test
    @TestAuthor(githubId = {"BasileChatillon", "Rhod3"})
    public void theServerShouldBeAbleToClearHisData() throws IOException {
        // getting the client
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        // adding some new Student
        client.loadStudent("Basile Chatillon");
        client.loadStudent("Nicolas Rod");
        // Testing if the new version can still count
        assertEquals(2, client.getNumberOfStudents());
        
        // reseting the data base of the serveur
        client.clearDataStore();
        
        //verify
        assertEquals(0, client.getNumberOfStudents());
    }
    
    @Test
    @TestAuthor(githubId = {"BasileChatillon", "Rhod3"})
    public void theServerShouldBeAbleToReturnAListOfHisData() throws IOException {
        // creation of the list of student
        List<Student> tmp = new LinkedList<>();
        tmp.add(new Student("Basile Chatillon"));
        tmp.add(new Student("Nicolas Rod"));
        
        // getting the client. 
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        // passing to the client the list of Student
        client.loadStudents(tmp);
        
        // trying the new method
        List<Student> Answer = client.listStudents();
        for(int i = 0 ; i < 2 ; ++i){
            //For each strudent, we compare their full name
            assertTrue(Answer.get(i).getFullname().equals(tmp.get(i).getFullname()));
        }
    }
}
