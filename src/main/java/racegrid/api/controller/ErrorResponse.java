package racegrid.api.controller;

import racegrid.api.model.RacegridError;

public class ErrorResponse {
    private String message;
    private RacegridError error;
    private Throwable throwable;
    private String url;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RacegridError getError() {
        return error;
    }

    public void setError(RacegridError error) {
        this.error = error;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
