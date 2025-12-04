package ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class FXMLTest {

    @BeforeAll
    public static void initJFX() {
        // Initialize JavaFX environment
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }

    @Test
    public void testLoadProfesionView() {
        loadFXML("profesion-view.fxml");
    }

    @Test
    public void testLoadTutorialView() {
        loadFXML("tutorial-view.fxml");
    }

    private void loadFXML(String fxmlName) {
        try {
            URL url = getClass().getResource("/ui/" + fxmlName);
            if (url == null) {
                fail("Could not find resource: /ui/" + fxmlName);
            }
            System.out.println("Found resource: " + url);
            FXMLLoader loader = new FXMLLoader(url);
            loader.load();
            System.out.println("Successfully loaded: " + fxmlName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to load " + fxmlName + ": " + e.getMessage());
        }
    }
}
