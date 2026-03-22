package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.service.AuthService;
import com.moneywise.moneywise.service.AuthService.ResultatAuth;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField champEmail;
    @FXML private PasswordField champMdp;
    @FXML private Label labelMessage;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleConnexion() {
        String email = champEmail.getText().trim();
        String mdp   = champMdp.getText().trim();

        // Appelle le service d'authentification
        ResultatAuth resultat = authService.connecter(email, mdp);

        if (resultat.isSucces()) {
            // Ouvre la session avec l'utilisateur connecté
            SessionManager.getInstance().ouvrirSession(resultat.getUtilisateur());

            // ← Ajoute cette ligne temporairement
            System.out.println("Est admin : " +
                    SessionManager.getInstance().estAdmin());

            // Navigue vers le dashboard
            SceneManager.getInstance().allerVersDashboard();
        } else {
            afficherErreur(resultat.getMessage());
        }
    }

    @FXML
    private void handleInscription() {
        SceneManager.getInstance().allerVersInscription();
    }

    private void afficherErreur(String message) {
        labelMessage.setStyle("-fx-text-fill: red;");
        labelMessage.setText(message);
    }

}