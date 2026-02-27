package service;

public class ServiceException extends Exception {
    public final int status;
    public ServiceException(int status, String message) { super(message); this.status = status; }
    public ServiceException(int status, String message, Throwable cause) { super(message, cause); this.status = status; }
}
