package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{
    @Override
    public void clearDataStore() throws IOException
    {
        if (!sendCommand(RouletteV2Protocol.CMD_CLEAR))
        {
            throw new IOException("failed to send clear command");
        }

        checkAnswer(RouletteV2Protocol.CMD_CLEAR);
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

        checkAnswer(RouletteV2Protocol.CMD_LIST);
    }
}
