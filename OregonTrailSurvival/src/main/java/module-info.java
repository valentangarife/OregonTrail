module OregonTrailSurvival {

    // JavaFX
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;
    requires com.google.gson;
    requires okhttp3;

    exports ui;
    exports structures;
    exports model;
    exports exception;
    exports enums;
    exports controller;

    opens ui to javafx.fxml;
}

