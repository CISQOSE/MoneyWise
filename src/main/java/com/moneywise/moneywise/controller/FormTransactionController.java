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

    // ── Composants FXML ───────────────────────────────────
    @FXML private TextField champMontant;
    @FXML private TextField champDescription;
    @FXML private ComboBox<String> comboType;
    @FXML private DatePicker datePicker;
    @FXML private Label labelMessage;
    @FXML private Button btnSauvegarder;

    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Transaction en cours de modification (null = ajout)
    private Transaction transactionAModifier;

    // Callback : appelé après sauvegarde pour rafraîchir la liste
    private Runnable onSauvegarde;

    // ── Initialisation ────────────────────────────────────
    @FXML
    private void initialize() {
        // Remplit le ComboBox avec les types
        comboType.getItems().addAll("ENTREE", "SORTIE");
        comboType.setValue("SORTIE"); // valeur par défaut
        datePicker.setValue(LocalDate.now()); // date du jour par défaut
    }

    // ── Pré-remplir le formulaire pour la modification ────
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

    // ── Sauvegarde ────────────────────────────────────────
    @FXML
    private void handleSauvegarder() {

        // Validation
        if (champMontant.getText().isBlank() ||
                champDescription.getText().isBlank() ||
                comboType.getValue() == null ||
                datePicker.getValue() == null) {
            labelMessage.setText("Veuillez remplir tous les champs.");
            labelMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(champMontant.getText().trim());
            if (montant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            labelMessage.setText("Montant invalide (ex: 150.50)");
            labelMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();

        boolean succes;

        if (transactionAModifier == null) {
            // ── MODE AJOUT ────────────────────────────────
            Transaction nouvelle = new Transaction(
                    montant,
                    TypeTransaction.valueOf(comboType.getValue()),
                    datePicker.getValue(),
                    champDescription.getText().trim(),
                    userId,
                    1 // catégorie par défaut (sera amélioré)
            );
            succes = transactionDAO.creer(nouvelle);
        } else {
            // ── MODE MODIFICATION ─────────────────────────
            transactionAModifier.setMontant(montant);
            transactionAModifier.setDescription(champDescription.getText().trim());
            transactionAModifier.setType(TypeTransaction.valueOf(comboType.getValue()));
            transactionAModifier.setDate(datePicker.getValue());
            succes = transactionDAO.modifier(transactionAModifier);
        }

        if (succes) {
            // Appelle le callback pour rafraîchir la liste
            if (onSauvegarde != null) onSauvegarde.run();
            // Ferme la fenêtre popup
            ((Stage) btnSauvegarder.getScene().getWindow()).close();
        } else {
            labelMessage.setText("Erreur lors de la sauvegarde.");
            labelMessage.setStyle("-fx-text-fill: red;");
        }
    }

    // ── Annuler ───────────────────────────────────────────
    @FXML
    private void handleAnnuler() {
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }
}