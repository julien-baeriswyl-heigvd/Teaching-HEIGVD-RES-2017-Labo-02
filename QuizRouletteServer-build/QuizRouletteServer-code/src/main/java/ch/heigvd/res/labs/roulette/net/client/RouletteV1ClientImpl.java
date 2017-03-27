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
    private PrintWriter    pw;
    private BufferedReader br;
    private String         answer;

    /**
     * Send command to server through client socket
     *
     * @remark no control over command string is needed, because of private internal use
     *
     * @param cmd  command token send to server
     * @return <code>true</code> if send operation succeed, else <code>false</code>
     * @throws IOException if write operation to server failed
     */
    private boolean sendCommand (String cmd) throws IOException
    {
        // JBL: ?
        if (clientSocket != null)
        {
            pw.println(cmd);

            if (pw.checkError())
            {
                throw new IOException("failed to send command - `" + cmd + "`");
            }

            switch(cmd)
            {
                case RouletteV1Protocol.CMD_HELP:
                case RouletteV1Protocol.CMD_INFO:
                case RouletteV1Protocol.CMD_RANDOM:
                case RouletteV1Protocol.CMD_LOAD:
                    answer = br.readLine();
                    return !answer.isEmpty();
                case RouletteV1Protocol.CMD_BYE:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * Send data formatted to string. Each data item is sent on one line.
     *
     * @param data array containing data to send
     * @return <code>true</code> if operation succeed, else <code>false</code>
     * @throws IOException if writing or reading into streams failed
     */
    private boolean sendData (Object... data) throws IOException
    {
        if (clientSocket != null)
        {
            for (Object item : data)
            {
                pw.println(item.toString());
                if (pw.checkError())
                {
                    throw new IOException("failed to send data");
                }
            }

            pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            if (pw.checkError())
            {
                throw new IOException("failed to send EOT");
            }

            return br.readLine().equals(RouletteV1Protocol.RESPONSE_LOAD_DONE);
        }

        return false;
    }

    @Override
    public void connect(String server, int port) throws IOException
    {
        // JBL: create a new client socket and open input and output streams when connected
        clientSocket = new Socket(server, port);
        if (isConnected())
        {
            pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // JBL: destroy initial text message send by server
            br.readLine();
        }
    }

    @Override
    public void disconnect() throws IOException
    {
        // JBL: send BYE command and clear resources (socket included)
        if (sendCommand(RouletteV1Protocol.CMD_BYE))
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
        // JBL: first send command LOAD and send name of student
        if (!sendCommand(RouletteV1Protocol.CMD_LOAD))
        {
            throw new IOException("failed to launch LOAD operation");
        }

        if (!sendData(fullname))
        {
            throw new IOException("failed to load student");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException
    {
        // JBL: first send command LOAD and send names line by line
        if (!sendCommand(RouletteV1Protocol.CMD_LOAD))
        {
            throw new IOException("failed to launch LOAD operation");
        }

        if (!sendData(students.toArray()))
        {
            throw new IOException("failed to load all students");
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
        // JBL: send INFO command and convert answer to InfoCommandResponse
        if(!sendCommand(RouletteV1Protocol.CMD_INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException
    {
        // JBL: send INFO command and convert answer to InfoCommandResponse
        if(!sendCommand(RouletteV1Protocol.CMD_INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getProtocolVersion();
    }
}
