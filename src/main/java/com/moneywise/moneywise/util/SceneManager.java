package com.moneywise.moneywise.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * SceneManager : gère la navigation entre les pages de MoneyWise.
 *
 * Pattern Singleton : une seule instance existe dans toute l'application.
 * C'est lui qui connaît le Stage principal et change les Scenes.
 */
public class SceneManager {

    // ── Singleton ──────────────────────────────────────────
    private static SceneManager instance;

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    private SceneManager() {} // constructeur privé

    // ── Le Stage principal (la fenêtre) ────────────────────
    private Stage stagePrincipal;

    // ── Initialisation (appelée une seule fois dans MainApp) ─
    public void initialiser(Stage stage) {
        this.stagePrincipal = stage;
    }

    // ── Navigation ─────────────────────────────────────────

    public void allerVersLogin() {
        chargerScene("/com/moneywise/moneywise/fxml/login.fxml", "MoneyWise — Connexion");
    }

    public void allerVersDashboard() {
        chargerScene("/com/moneywise/moneywise/fxml/dashboard.fxml", "MoneyWise — Tableau de bord");
    }

    public void allerVersInscription() {
        chargerScene("/com/moneywise/moneywise/fxml/inscription.fxml", "MoneyWise — Inscription");
    }

    public void allerVersTransactions() {
        chargerScene("/com/moneywise/moneywise/fxml/transactions.fxml", "MoneyWise — Transactions");
    }

    public void allerVersExport() {
        chargerScene("/com/moneywise/moneywise/fxml/export.fxml", "MoneyWise — Export");
    }

    public void allerVersAdmin() {
        chargerScene("/com/moneywise/moneywise/fxml/admin.fxml", "MoneyWise — Administration");
    }

    // ── Méthode centrale de chargement ─────────────────────
    private void chargerScene(String cheminFxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(cheminFxml)
            );

            Scene scene = new Scene(loader.load(), 900, 600);

            // Applique le CSS
            scene.getStylesheets().add(
                    getClass().getResource(
                            "/com/moneywise/moneywise/css/style.css"
                    ).toExternalForm()
            );

            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle(titre);
            stagePrincipal.centerOnScreen();

        } catch (IOException e) {
            System.err.println("❌ Erreur chargement FXML : " + cheminFxml);
            e.printStackTrace();
        }
    }


}