package input;

import javafx.scene.input.KeyCode;

public class KeyHandler {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean spacePressed;
    public boolean rPressed;

    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.UP || code == KeyCode.W) {
            upPressed = true;
        }
        if (code == KeyCode.DOWN || code == KeyCode.S) {
            downPressed = true;
        }
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            leftPressed = true;
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            rightPressed = true;
        }
        if (code == KeyCode.SPACE) {
            spacePressed = true;
        }
        if (code == KeyCode.R) {
            rPressed = true;
        }
    }

    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.UP || code == KeyCode.W) {
            upPressed = false;
        }
        if (code == KeyCode.DOWN || code == KeyCode.S) {
            downPressed = false;
        }
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            leftPressed = false;
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            rightPressed = false;
        }
        if (code == KeyCode.SPACE) {
            spacePressed = false;
        }
        if (code == KeyCode.R) {
            rPressed = false;
        }
    }
}
