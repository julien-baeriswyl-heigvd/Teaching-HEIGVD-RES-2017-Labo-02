package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 * @author Julien Baeriswyl   [MODIFIED BY] (julien.baeriswyl@heig-vd.ch)
 * @author Iando Rafidimalala [MODIFIED BY] (iando.rafidimalalathevoz@heig-vd.ch)
 */
public class RouletteV2ClientHandler implements IClientHandler
{
    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store)
    {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter    writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        String successStatus = LoadCommandResponse.SUCCESS;
        String command;
        boolean done = false;
        int nbCommand = 0;

        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            // When we reach this state, we have a wellformed command
            nbCommand++;

            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student, you cannot pick a random one");
                    }
                    writer.println(JsonObjectMapper.toJson(rcResponse));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_HELP:
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    int oldNumberOfStudent = store.getNumberOfStudents();
                    store.importData(reader);

                    // retrieve the number of the new students and check the difference after storage action
                    int numberOfNewStudents = store.getNumberOfStudents() - oldNumberOfStudent;
                    writer.println(JsonObjectMapper.toJson(new LoadCommandResponse(successStatus, numberOfNewStudents)));

                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_DONE);
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    writer.println(JsonObjectMapper.toJson(store.listStudents()));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_BYE:
                    writer.println(JsonObjectMapper.toJson(new ByeCommandResponse(ByeCommandResponse.SUCCESS, nbCommand)));
                    writer.flush();
                    done = true;
                    break;
                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            writer.flush();
        }

    }
}
