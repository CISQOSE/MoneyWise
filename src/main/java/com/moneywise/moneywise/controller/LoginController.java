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

        ResultatAuth resultat = authService.connecter(email, mdp);

        if (resultat.isSucces()) {
            SessionManager.getInstance().ouvrirSession(resultat.getUtilisateur());
            SceneManager.getInstance().allerVersDashboard();
        } else {
            labelMessage.getStyleClass().removeAll("msg-succes", "msg-info");
            labelMessage.getStyleClass().add("msg-erreur");
            labelMessage.setText(resultat.getMessage());
        }
    }

    @FXML
    private void handleInscription() {
        SceneManager.getInstance().allerVersInscription();
    }
}