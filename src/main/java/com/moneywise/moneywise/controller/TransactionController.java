package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class TransactionController {

    // ── Composants FXML ───────────────────────────────────
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colMontant;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colActions;

    private final TransactionDAO transactionDAO = new TransactionDAO();

    // ── Initialisation ────────────────────────────────────
    @FXML
    private void initialize() {
        configurerColonnes();
        chargerTransactions();
    }

    // ── Configuration des colonnes du tableau ─────────────
    private void configurerColonnes() {

        // Colonne Date
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDate().toString()
                )
        );

        // Colonne Description
        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription())
        );

        // Colonne Montant (avec couleur selon le type)
        colMontant.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format("%.2f €", data.getValue().getMontant())
                )
        );

        // Colonne Type avec couleur
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction t = getTableRow().getItem();
                    if (t.getType() == Transaction.TypeTransaction.ENTREE) {
                        setText("↑ ENTRÉE");
                        setStyle("-fx-text-fill: #2dc653; -fx-font-weight: bold;");
                    } else {
                        setText("↓ SORTIE");
                        setStyle("-fx-text-fill: #e63946; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().name())
        );

        // Colonne Actions (boutons Modifier / Supprimer)
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnModifier   = new Button("✏️");
            private final Button btnSupprimer  = new Button("🗑️");

            {
                // Style des boutons
                btnModifier.setStyle(
                        "-fx-background-color: #4361ee; -fx-text-fill: white;" +
                                "-fx-background-radius: 4px; -fx-cursor: hand;");
                btnSupprimer.setStyle(
                        "-fx-background-color: #e63946; -fx-text-fill: white;" +
                                "-fx-background-radius: 4px; -fx-cursor: hand;");

                // Clic Modifier
                btnModifier.setOnAction(e -> {
                    Transaction t = getTableRow().getItem();
                    if (t != null) ouvrirFormulaire(t);
                });

                // Clic Supprimer
                btnSupprimer.setOnAction(e -> {
                    Transaction t = getTableRow().getItem();
                    if (t != null) confirmerSuppression(t);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox boutons = new HBox(5, btnModifier, btnSupprimer);
                    setGraphic(boutons);
                }
            }
        });
    }

    // ── Charger les transactions depuis la BDD ─────────────
    private void chargerTransactions() {
        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();
        List<Transaction> liste = transactionDAO.trouverParUtilisateur(userId);
        tableTransactions.setItems(FXCollections.observableArrayList(liste));
    }

    // ── Ouvrir le formulaire (ajout ou modification) ───────
    private void ouvrirFormulaire(Transaction transactionAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/moneywise/moneywise/fxml/form_transaction.fxml"
                    )
            );

            // ✅ Après : taille définie
            Stage popup = new Stage();
            popup.setTitle(transactionAModifier == null ?
                    "Nouvelle Transaction" : "Modifier la Transaction");

            Scene popupScene = new Scene(loader.load(), 450, 550); // ← largeur x hauteur

            // Applique le même CSS que l'app principale
            popupScene.getStylesheets().add(
                    getClass().getResource(
                            "/com/moneywise/moneywise/css/style.css"
                    ).toExternalForm()
            );

            popup.setScene(popupScene);
            popup.setMinWidth(450);
            popup.setMinHeight(550);
            popup.centerOnScreen();  // ← centre la popup à l'écran

            popup.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale

            FormTransactionController formCtrl = loader.getController();

            // Si modification, pré-rempli le formulaire
            if (transactionAModifier != null) {
                formCtrl.setTransactionAModifier(transactionAModifier);
            }

            // Rafraîchit la liste après sauvegarde
            formCtrl.setOnSauvegarde(this::chargerTransactions);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Confirmer la suppression ───────────────────────────
    private void confirmerSuppression(Transaction t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer cette transaction ?");
        alert.setContentText(t.getDescription() + " — " +
                String.format("%.2f €", t.getMontant()));

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                transactionDAO.supprimer(t.getId());
                chargerTransactions(); // rafraîchit la liste
            }
        });
    }

    // ── Navigation ────────────────────────────────────────
    @FXML
    private void handleAjouter() {
        ouvrirFormulaire(null); // null = mode ajout
    }

    @FXML
    private void handleDashboard() {
        SceneManager.getInstance().allerVersDashboard();
    }

    @FXML
    private void handleDeconnexion() {
        SessionManager.getInstance().fermerSession();
        SceneManager.getInstance().allerVersLogin();
    }
}