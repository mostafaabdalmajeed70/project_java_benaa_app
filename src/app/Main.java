package app;

import app.database.DatabaseInitializer;
import app.gui.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}