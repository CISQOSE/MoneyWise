package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.service.AuthService;
import com.moneywise.moneywise.service.AuthService.ResultatAuth;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InscriptionController {

    @FXML private TextField champNom;
    @FXML private TextField champEmail;
    @FXML private PasswordField champMdp;
    @FXML private Label labelMessage;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleInscription() {
        String nom   = champNom.getText().trim();
        String email = champEmail.getText().trim();
        String mdp   = champMdp.getText().trim();

        ResultatAuth resultat = authService.inscrire(nom, email, mdp);

        if (resultat.isSucces()) {
            // Connecte automatiquement après inscription
            SessionManager.getInstance().ouvrirSession(resultat.getUtilisateur());
            SceneManager.getInstance().allerVersDashboard();
        } else {
            labelMessage.setStyle("-fx-text-fill: red;");
            labelMessage.setText(resultat.getMessage());
        }
    }

    @FXML
    private void handleRetourLogin() {
        SceneManager.getInstance().allerVersLogin();
    }
}