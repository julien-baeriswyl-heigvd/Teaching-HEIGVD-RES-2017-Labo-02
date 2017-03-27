package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{
    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket         clientSocket;
    private BufferedReader br;
    private PrintWriter    pw;
    private String         answer;

    /**
     * Verify command is available in protocol
     *
     * @param cmd  protocol command to verify
     * @return <code>true</code> if command is valid, else <code>false</code>
     */
    protected boolean isValidCommand (String cmd)
    {
        for (String supported : RouletteV1Protocol.SUPPORTED_COMMANDS)
        {
            if (cmd.equals(supported))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve and control answer.
     *
     * @param cmd  last command sent to server
     * @return <code>true</code> if answer is fine, else <code>false</code>
     * @throws IOException if reading answer failed
     */
    protected boolean checkAnswer (String cmd) throws IOException
    {
        // JBL: get and check server answer if command is not last (BYE)
        if (!cmd.equals(RouletteV1Protocol.CMD_BYE))
        {
            answer = br.readLine();
            return !answer.isEmpty();
        }
        return true;
    }

    /**
     * Send command to server through client socket.
     *
     * @param cmd  command token send to server
     * @return <code>true</code> if send operation succeed, else <code>false</code>
     * @throws IOException if write operation to server failed
     */
    protected boolean sendCommand (String cmd) throws IOException
    {
        // JBL: control socket connexion
        if (!isConnected())
        {
            throw new IOException("client is not connected");
        }

        // JBL: check validity of command
        if (!isValidCommand(cmd))
        {
            throw new IOException("command is not available - `" + cmd + "`");
        }

        // JBL: send command
        pw.println(cmd);
        if (pw.checkError())
        {
            throw new IOException("failed to send command - `" + cmd + "`");
        }

        // JBL: check server answered properly
        return checkAnswer(cmd);
    }

    /**
     * Send data formatted to string.
     * Each data item is sent on one line.
     *
     * @param data array containing data to send
     * @return <code>true</code> if operation succeed, else <code>false</code>
     * @throws IOException if writing or reading into streams failed
     */
    protected boolean sendData (Object... data) throws IOException
    {
        // JBL: control socket connexion
        if (!isConnected())
        {
            throw new IOException("client is not connected");
        }

        // JBL: send items line by line
        for (Object item : data)
        {
            pw.println(item.toString());
            if (pw.checkError())
            {
                throw new IOException("failed to send data");
            }
        }

        // JBL: indicate end of transmission to server
        pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        if (pw.checkError())
        {
            throw new IOException("failed to send EOT");
        }

        // JBL: check server answered successful loading
        return br.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE);
    }

    @Override
    public void connect(String server, int port) throws IOException
    {
        // JBL: create a new client socket and open input and output streams when connected
        clientSocket = new Socket(server, port);
        if (isConnected())
        {
            // JBL: open input and output stream
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // JBL: destroy initial text message send by server
            br.readLine();
        }
    }

    @Override
    public void disconnect() throws IOException
    {
        // JBL: send BYE command and clear resources (socket included)
        if (sendCommand(RouletteV1Protocol.CMD_BYE) && !clientSocket.isConnected())
        {
            pw.close();
            br.close();
            clientSocket.close();

            pw = null;
            br = null;
            clientSocket = null;
        }
    }

    @Override
    public boolean isConnected()
    {
        // JBL: test if client socket exists and is connected
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException
    {
        // JBL: first send command LOAD
        if (!sendCommand(RouletteV1Protocol.CMD_LOAD))
        {
            throw new IOException("failed to launch LOAD operation");
        }

        // JBL: send name of student
        if (!sendData(fullname))
        {
            throw new IOException("failed to load student");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException
    {
        // JBL: first send command LOAD
        if (!sendCommand(RouletteV1Protocol.CMD_LOAD))
        {
            throw new IOException("failed to launch LOAD operation");
        }

        // JBL: send Student names line by line
        if (!sendData(students.toArray()))
        {
            throw new IOException("failed to load students list");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException
    {
        // JBL: send command RANDOM, then test answer value
        if (!sendCommand(RouletteV1Protocol.CMD_RANDOM))
        {
            throw new IOException("failed to retrieve student");
        }

        // JBL: test if error occurred, for instance: no student available
        RandomCommandResponse rcr = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);
        if (!rcr.getError().isEmpty())
        {
            throw new EmptyStoreException();
        }

        // JBL: if no error, then convert answer to Student.
        return Student.fromJson(answer);
    }

    @Override
    public int getNumberOfStudents() throws IOException
    {
        // JBL: send INFO command
        if(!sendCommand(RouletteV1Protocol.CMD_INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        // JBL: convert answer to InfoCommandResponse and get number of students
        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException
    {
        // JBL: send INFO command
        if(!sendCommand(RouletteV1Protocol.CMD_INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        // JBL: convert answer to InfoCommandResponse and get protocol version
        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getProtocolVersion();
    }
}
