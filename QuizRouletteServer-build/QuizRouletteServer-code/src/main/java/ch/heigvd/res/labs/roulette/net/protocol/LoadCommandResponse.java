package ch.heigvd.res.labs.roulette.net.protocol;


/**
 This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Julien  Baeriswyl    [MODIFIED BY] (julien.baeriswyl@heig-vd.ch)
 * @author Iando   Rafidimalala [MODIFIED BY] (iando.rafidimalalathevoz@heig-vd.ch)
 */


public class LoadCommandResponse {
    public static final String SUCCESS = "success",
                               FAILURE = "failure";

    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse() {
    }

    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status  ;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public String getStatus() {
        return status  ;
    }

    public void setStatus  (String status ) {
        this.status = status  ;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
