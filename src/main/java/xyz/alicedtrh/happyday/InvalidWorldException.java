package xyz.alicedtrh.happyday;

public class InvalidWorldException extends RuntimeException {
    public InvalidWorldException(String msg, NullPointerException e) {
        super(msg, e);
    }
}
