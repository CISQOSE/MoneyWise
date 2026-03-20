package com.moneywise.moneywise;

import javafx.application.Application;
import javafx.stage.Stage;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.DatabaseConnection;
import java.sql.Connection;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // ── Test connexion BDD (temporaire) ───────────────────
        Connection conn = DatabaseConnection.getInstance();
        if (conn != null) {
            System.out.println("✅ Base de données connectée !");
        } else {
            System.out.println("❌ Impossible de se connecter à la BDD !");
        }

        // ── Navigation ────────────────────────────────────────
        SceneManager.getInstance().initialiser(stage);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        SceneManager.getInstance().allerVersLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}