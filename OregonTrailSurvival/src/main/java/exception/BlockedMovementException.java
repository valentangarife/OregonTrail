package exception;


public class BlockedMovementException extends GameException {

    public BlockedMovementException() {
        super("No puedes moverte en esa dirección, el camino está bloqueado.");
    }

    public BlockedMovementException(String message) {
        super(message);
    }
}