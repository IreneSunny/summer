package ruc.summer.storage;

/**
 * User: xiatian
 * Date: 2/23/13 6:56 PM
 */
public class Result {
    public static final int STATUS_OK = 0;
    public static final int STATUS_EXISTED = 1;
    public static final int STATUS_ERROR = 2;

    private int status = 0;
    private String message;

    public Result(){}

    public Result(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
