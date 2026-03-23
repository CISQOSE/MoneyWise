package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.BudgetDAO;
import com.moneywise.moneywise.model.Budget;
import com.moneywise.moneywise.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormBudgetController {

    @FXML private TextField champMontant;
    @FXML private ComboBox<String> comboMois;
    @FXML private TextField champAnnee;
    @FXML private Label labelMessage;
    @FXML private Button btnSauvegarder;

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private Runnable onSauvegarde;

    @FXML
    private void initialize() {
        comboMois.getItems().addAll(
                "JANVIER", "FEVRIER", "MARS", "AVRIL",
                "MAI", "JUIN", "JUILLET", "AOUT",
                "SEPTEMBRE", "OCTOBRE", "NOVEMBRE", "DECEMBRE"
        );
        comboMois.setValue("MARS");
        champAnnee.setText("2026");
    }

    public void setOnSauvegarde(Runnable callback) {
        this.onSauvegarde = callback;
    }

    @FXML
    private void handleSauvegarder() {

        // Validation
        if (champMontant.getText().isBlank() ||
                comboMois.getValue() == null ||
                champAnnee.getText().isBlank()) {
            labelMessage.setStyle("-fx-text-fill: red;");
            labelMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        double montant;
        int annee;
        try {
            montant = Double.parseDouble(champMontant.getText().trim());
            annee   = Integer.parseInt(champAnnee.getText().trim());
            if (montant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            labelMessage.setStyle("-fx-text-fill: red;");
            labelMessage.setText("Montant ou année invalide.");
            return;
        }

        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();

        Budget budget = new Budget(
                montant,
                comboMois.getValue(),
                annee,
                userId,
                1 // catégorie par défaut
        );

        boolean succes = budgetDAO.creer(budget);

        if (succes) {
            if (onSauvegarde != null) onSauvegarde.run();
            ((Stage) btnSauvegarder.getScene().getWindow()).close();
        } else {
            labelMessage.setStyle("-fx-text-fill: red;");
            labelMessage.setText("Erreur lors de la sauvegarde.");
        }
    }

    @FXML
    private void handleAnnuler() {
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }
}