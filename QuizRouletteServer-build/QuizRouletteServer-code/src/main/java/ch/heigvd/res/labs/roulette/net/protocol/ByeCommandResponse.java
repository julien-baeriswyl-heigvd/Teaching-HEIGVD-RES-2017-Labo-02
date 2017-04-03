package ch.heigvd.res.labs.roulette.net.protocol;

/**
 This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Julien  Baeriswyl    [MODIFIED BY] (julien.baeriswyl@heig-vd.ch)
 * @author Iando   Rafidimalala [MODIFIED BY] (iando.rafidimalalathevoz@heig-vd.ch)
 */

public class ByeCommandResponse {
    public static final String SUCCESS = "success",
                               FAILURE = "failure";

    private String status;
    private int numberOfCommands;

    public ByeCommandResponse() {
    }

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
}
