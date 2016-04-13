package io.ehdev.conrad.client.java.scm;

public class CommitDetails {

    private final String message;
    private final String id;

    public CommitDetails(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }
}
