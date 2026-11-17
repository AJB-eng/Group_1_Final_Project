package ui;

/**
 * Standalone Launcher class that invokes MainApp without extending javafx.application.Application.
 * Allows running the JavaFX application directly from IDEs, fat JARs, or classpath runners.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
