package exception;

public class OutOfAmmoException extends GameException {

    public OutOfAmmoException() {
        super("No tienes munición para disparar.");
    }

    public OutOfAmmoException(String message) {
        super(message);
    }
}