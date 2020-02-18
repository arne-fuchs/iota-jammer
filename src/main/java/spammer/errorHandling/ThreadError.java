package spammer.errorHandling;

import java.sql.Timestamp;

public class ThreadError {

    private ErrorType errorType;
    private Timestamp timestamp;

    public ThreadError(ErrorType errorType) {
        this.timestamp = getCurrentSystemTime();
        this.errorType = errorType;
    }

    /**
     * Get current Time of System
     *
     * @return Timestamp in milliseconds
     */
    private Timestamp getCurrentSystemTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
