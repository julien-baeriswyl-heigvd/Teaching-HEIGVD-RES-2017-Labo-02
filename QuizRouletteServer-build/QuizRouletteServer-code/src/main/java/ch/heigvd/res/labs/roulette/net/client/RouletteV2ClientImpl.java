package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Julien Baeriswyl   [MODIFIED BY] (julien.baeriswyl@heig-vd.ch)
 * @author Iando Rafidimalala [MODIFIED BY] (iando.rafidimalala@heig-vd.ch)
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{
    @Override
    protected String[] getSupportedCommands ()
    {
        return RouletteV2Protocol.SUPPORTED_COMMANDS;
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
