package exception;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * @return integer containing the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return super.getMessage();
    }
}
