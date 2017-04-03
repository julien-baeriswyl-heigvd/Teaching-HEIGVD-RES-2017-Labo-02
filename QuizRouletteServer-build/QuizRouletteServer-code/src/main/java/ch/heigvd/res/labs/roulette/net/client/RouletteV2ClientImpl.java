package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Julien  Baeriswyl    [MODIFIED BY] (julien.baeriswyl@heig-vd.ch)
 * @author Iando   Rafidimalala [MODIFIED BY] (iando.rafidimalalathevoz@heig-vd.ch)
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{
    @Override
    protected String[] getSupportedCommands ()
    {
        return RouletteV2Protocol.SUPPORTED_COMMANDS;
    }

    @Override
    protected boolean hasSendDataSucceed (Object... data) throws IOException
    {
        LoadCommandResponse response = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
        return response.getStatus().equals(LoadCommandResponse.SUCCESS) && response.getNumberOfNewStudents() == data.length;
    }

    @Override
    protected boolean retrieveAnswer (String cmd) throws IOException
    {
        // JBL: get and check server answer if command is not last (BYE)
        answer = br.readLine();
        return !answer.isEmpty();
    }

    @Override
    public void disconnect() throws IOException
    {
        // JBL: send BYE command and clear resources (socket included)
        if (sendCommand(RouletteV2Protocol.CMD_BYE)
                && JsonObjectMapper.parseJson(getAnswer(), ByeCommandResponse.class).getStatus().equals(ByeCommandResponse.SUCCESS))
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
    public void clearDataStore() throws IOException
    {
        if (!sendCommand(RouletteV2Protocol.CMD_CLEAR))
        {
            throw new IOException("failed to send clear command");
        }

        retrieveAnswer(RouletteV2Protocol.CMD_CLEAR);
        if (!getAnswer().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
        {
            throw new IOException("failed to clear students list");
        }
    }

    @Override
    public List<Student> listStudents() throws IOException
    {
        if (!sendCommand(RouletteV2Protocol.CMD_LIST))
        {
            throw new IOException("failed to ask students list");
        }

        retrieveAnswer(RouletteV2Protocol.CMD_LIST);

        return JsonObjectMapper.parseJson(getAnswer(), StudentsList.class).getStudents();
    }
}
