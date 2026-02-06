package rs.oris.back.exceptions;

public class KilometersDoesNotExistException extends RuntimeException{
    public KilometersDoesNotExistException(String message) {
        super(message);
    }
}
