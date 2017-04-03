package ch.heigvd.res.labs.roulette.net.client;

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

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 * Tests for V2 protocol
 * @author Camilo Pineda Serna
 * @author Antoine Nourazar
 */
public class RouletteV2kkoPSTest {
    // creates an excpected exception to test the exceptions handling
    @Rule
    public ExpectedException exception = ExpectedException.none();

    // wizard with the client and the server.
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    /**
     * tests the correct version Number
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = {"kkoPS", "antoineNourZaf"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    /**
     * creates a client, loads 2 students, checks if number of students is 2, then clears and check if number of students is 0
     * @throws IOException
     */
    @Ignore
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void noMoreStudentsInServerAfterCallingMethodClearDataStore() throws IOException {
        // other client to intercept the server answers
        IRouletteV2Client clientV2 = new RouletteV2ClientImpl();
        clientV2.loadStudent("cesar");
        clientV2.loadStudent("cleopatra");

        clientV2.listStudents();

    }

    /**
     * creates a client, loads 2 students, checks if number of students is 2, then clears and check if number of students is 0
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void theListCommandShouldReturnTheCorrectAnswer() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // clearing students
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();
        fromServer.readLine();

        // loading
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        if (! fromServer.readLine().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
        {
            throw new IOException("error at LOAD command start : unexpected server Answer");
        }
        toServer.println("cesar");
        toServer.flush();
        toServer.println("cleopatra");
        toServer.flush();
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        toServer.flush();
        fromServer.readLine();

        // check
        toServer.println(RouletteV2Protocol.CMD_LIST);
        toServer.flush();
        String serverAnswer = fromServer.readLine();
        assertEquals("{\"students\":[{\"fullname\":\"cesar\"},{\"fullname\":\"cleopatra\"}]}", serverAnswer);


        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();
    }

    /**
     * clears and checks the server's answer
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void theClearCommandShouldReturnTheCorrectAnswer() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // command no 1
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();

        // checking the server answer
        String serverResponse = fromServer.readLine();
        assertEquals(RouletteV2Protocol.RESPONSE_CLEAR_DONE, serverResponse);


        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();
    }

    /**
     * connects and loads 2 students, then checks the answer's status and number of new students
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void theLoadCommandShouldReturnTheCorrectStatusAndNumberOfNewStudents() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // loading two students
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        // getting the response message with instructions
        if (! fromServer.readLine().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
        {
            throw new IOException("error at LOAD command start : unexpected server Answer");
        }

        // actual loading of students "names"
        toServer.println("La Traviata");
        toServer.flush();
        toServer.println("Sarastro");
        toServer.flush();
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        toServer.flush();

        // checking the server answer
        String serverResponse = fromServer.readLine();
        assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":2}", serverResponse);



        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();
    }

    /**
     * clears students, adds 2 students in 1 Load command, lists the students, clears the students.
     * Finally closes the connexion with BYE and checks the number of commands sent int the session :
     * 2 clears, 1 load, 1 list and 1 bye : 5 commands
      * @throws IOException
     */
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void theByeCommandShouldReturnTheCorrectStatusAndNumberOfCommandsInTheSession() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // command no 1
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();
        fromServer.readLine();

        // command no 2
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        if (! fromServer.readLine().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
        {
            throw new IOException("error at LOAD command start : unexpected server Answer");
        }
        toServer.println("Harry Cot");
        toServer.flush();
        toServer.println("Susage Dog");
        toServer.flush();
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        toServer.flush();
        fromServer.readLine();

        // command no 3
        toServer.println(RouletteV2Protocol.CMD_LIST);
        toServer.flush();
        fromServer.readLine();

        // command no 4
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();
        fromServer.readLine();

        // command no 5
        toServer.println(RouletteV2Protocol.CMD_BYE);
        toServer.flush();
        String serverResponse = fromServer.readLine();

        // checks the answer
        assertEquals("{\"status\":\"success\",\"numberOfCommands\":5}", serverResponse);



        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();

    }


    /**
     * clears students, adds 3 students in 1 Load command and asks for INFO.
     *
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = {"kkoPS", "antoineNourZaf"})
    public void theInfoCommandShouldReturnTheCorrectProtocolVersionAndNumberOfStudents() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // clearing the server content
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();
        fromServer.readLine();

        // loading 3 students
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        if (! fromServer.readLine().equals(RouletteV2Protocol.RESPONSE_LOAD_START))
        {
            throw new IOException("error at LOAD command start : unexpected server Answer");
        }
        toServer.println("Provençal le Gaulois");
        toServer.flush();
        toServer.println("Arthur Cuillère");
        toServer.flush();
        toServer.println("Joe LeClodo");
        toServer.flush();
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        toServer.flush();
        // fetching server's response
        fromServer.readLine();



        // command Info
        toServer.println(RouletteV2Protocol.CMD_INFO);
        toServer.flush();
        String serverResponse = fromServer.readLine();

        // checks the answer
        assertEquals("{\"protocolVersion\":\"2.0\",\"numberOfStudents\":3}", serverResponse);


        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();

    }

}
