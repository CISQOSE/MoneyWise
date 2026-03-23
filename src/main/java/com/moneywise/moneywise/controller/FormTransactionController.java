package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;
import com.moneywise.moneywise.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FormTransactionController {

    @FXML private TextField champMontant;
    @FXML private TextField champDescription;
    @FXML private ComboBox<String> comboType;
    @FXML private DatePicker datePicker;
    @FXML private Label labelMessage;
    @FXML private Button btnSauvegarder;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private Transaction transactionAModifier;
    private Runnable onSauvegarde;

    @FXML
    private void initialize() {
        comboType.getItems().addAll("ENTREE", "SORTIE");
        comboType.setValue("SORTIE");
        datePicker.setValue(LocalDate.now());
    }

    public void setTransactionAModifier(Transaction t) {
        this.transactionAModifier = t;
        champMontant.setText(String.valueOf(t.getMontant()));
        champDescription.setText(t.getDescription());
        comboType.setValue(t.getType().name());
        datePicker.setValue(t.getDate());
        btnSauvegarder.setText("Modifier");
    }

    public void setOnSauvegarde(Runnable callback) {
        this.onSauvegarde = callback;
    }

    @FXML
    private void handleSauvegarder() {

        if (champMontant.getText().isBlank() ||
                champDescription.getText().isBlank() ||
                comboType.getValue() == null ||
                datePicker.getValue() == null) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(champMontant.getText().trim());
            if (montant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            afficherErreur("Montant invalide (ex: 150.50)");
            return;
        }

        int userId = SessionManager.getInstance().getUtilisateurConnecte().getId();
        boolean succes;

        if (transactionAModifier == null) {
            Transaction nouvelle = new Transaction(
                    montant,
                    TypeTransaction.valueOf(comboType.getValue()),
                    datePicker.getValue(),
                    champDescription.getText().trim(),
                    userId,
                    1
            );
            succes = transactionDAO.creer(nouvelle);
        } else {
            transactionAModifier.setMontant(montant);
            transactionAModifier.setDescription(champDescription.getText().trim());
            transactionAModifier.setType(TypeTransaction.valueOf(comboType.getValue()));
            transactionAModifier.setDate(datePicker.getValue());
            succes = transactionDAO.modifier(transactionAModifier);
        }

        if (succes) {
            if (onSauvegarde != null) onSauvegarde.run();
            ((Stage) btnSauvegarder.getScene().getWindow()).close();
        } else {
            afficherErreur("Erreur lors de la sauvegarde.");
        }
    }

    @FXML
    private void handleAnnuler() {
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }

    // ── Utilitaires CSS ───────────────────────────────────
    private void afficherErreur(String message) {
        labelMessage.getStyleClass().removeAll("msg-succes", "msg-info");
        if (!labelMessage.getStyleClass().contains("msg-erreur")) {
            labelMessage.getStyleClass().add("msg-erreur");
        }
        labelMessage.setText(message);
    }
}