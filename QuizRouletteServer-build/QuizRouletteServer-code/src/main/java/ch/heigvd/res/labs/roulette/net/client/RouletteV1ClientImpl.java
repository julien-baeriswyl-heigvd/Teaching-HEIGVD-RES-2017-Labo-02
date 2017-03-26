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
    private enum CommandNumber
    {
        HELP,
        RANDOM,
        LOAD,
        INFO,
        BYE
    }

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket         clientSocket;
    private PrintWriter    pw;
    private BufferedReader br;
    private String         answer;

    /**
     *
     * @param nr
     * @return
     * @throws IOException
     */
    private boolean sendCommand (CommandNumber nr) throws IOException
    {
        // JBL: ?
        if (clientSocket != null)
        {
            pw.println(RouletteV1Protocol.SUPPORTED_COMMANDS[nr.ordinal()]);

            if (pw.checkError())
            {
                throw new IOException("failed to send command - `" + RouletteV1Protocol.SUPPORTED_COMMANDS[nr.ordinal()] + "`");
            }

            switch(nr)
            {
                case HELP:
                case INFO:
                case RANDOM:
                case LOAD:
                    answer = br.readLine();
                    return !answer.isEmpty();
                case BYE:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     *
     * @param data
     * @return
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
        // JBL: ?
        clientSocket = new Socket(server, port);
        if (isConnected())
        {
            pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            br.readLine();
        }
    }

    @Override
    public void disconnect() throws IOException
    {
        // JBL: ?
        if (sendCommand(CommandNumber.BYE))
        {
            clientSocket.close();
            clientSocket = null;
            pw = null;
            br = null;
        }
    }

    @Override
    public boolean isConnected()
    {
        // JBL: ?
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException
    {
        // JBL: ?
        if (!sendCommand(CommandNumber.LOAD))
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
        final String SEPARATOR = String.format("%n");

        // JBL: ?
        if (!sendCommand(CommandNumber.LOAD))
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
        // JBL: ?
        if (!sendCommand(CommandNumber.RANDOM))
        {
            throw new IOException("failed to retrieve student");
        }

        RandomCommandResponse rcr = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);

        if (!rcr.getError().isEmpty())
        {
            throw new EmptyStoreException();
        }

        return Student.fromJson(answer);
    }

    @Override
    public int getNumberOfStudents() throws IOException
    {
        // JBL: ?
        if(!sendCommand(CommandNumber.INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException
    {
        // JBL: ?
        if(!sendCommand(CommandNumber.INFO))
        {
            throw new IOException("failed to retrieve global information");
        }

        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getProtocolVersion();
    }
}
