package racegrid.api.model;

public class RacegridException extends RuntimeException{

    private final RacegridError error;

    public RacegridException(RacegridError error, String message) {
        super(message);
        this.error = error;
    }

    public RacegridError getError() {
        return error;
    }
}
