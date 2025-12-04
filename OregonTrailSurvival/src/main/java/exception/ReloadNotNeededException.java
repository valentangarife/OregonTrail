package exception;


public class ReloadNotNeededException extends GameException {

    public ReloadNotNeededException() {
        super("No es necesario recargar, aún tienes balas disponibles.");
    }

    public ReloadNotNeededException(String message) {
        super(message);
    }
}
