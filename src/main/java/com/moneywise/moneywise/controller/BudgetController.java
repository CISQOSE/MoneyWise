package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.BudgetDAO;
import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Budget;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BudgetController {

    @FXML private TableView<Budget> tableBudgets;
    @FXML private TableColumn<Budget, String> colMois;
    @FXML private TableColumn<Budget, String> colAnnee;
    @FXML private TableColumn<Budget, String> colMontantMax;
    @FXML private TableColumn<Budget, String> colDepenses;
    @FXML private TableColumn<Budget, String> colPourcentage;
    @FXML private TableColumn<Budget, String> colActions;

    private final BudgetDAO budgetDAO           = new BudgetDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    private void initialize() {
        configurerColonnes();
        chargerBudgets();
    }

    private void configurerColonnes() {

        colMois.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMois()));

        colAnnee.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAnnee())));

        colMontantMax.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format("%.2f €", data.getValue().getMontantMax())));

        // Colonne Dépenses
        colDepenses.setCellValueFactory(data -> {
            Budget b = data.getValue();
            int userId = SessionManager.getInstance().getUtilisateurConnecte().getId();
            double depenses = calculerDepenses(b, userId);
            return new SimpleStringProperty(String.format("%.2f €", depenses));
        });

        // Colonne Pourcentage avec couleur
        colPourcentage.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Budget b = getTableRow().getItem();
                    int userId = SessionManager.getInstance()
                            .getUtilisateurConnecte().getId();
                    double depenses    = calculerDepenses(b, userId);
                    double pourcentage = (depenses / b.getMontantMax()) * 100;

                    setText(String.format("%.0f%%", pourcentage));

                    if (pourcentage >= 100) {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    } else if (pourcentage >= 80) {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colPourcentage.setCellValueFactory(data -> new SimpleStringProperty(""));

        // Colonne Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("🗑️");
            {
                btnSupprimer.getStyleClass().add("btn-icon-delete");
                btnSupprimer.setOnAction(e -> {
                    Budget b = getTableRow().getItem();
                    if (b != null) confirmerSuppression(b);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSupprimer);
            }
        });
        colActions.setCellValueFactory(data -> new SimpleStringProperty(""));
    }

    private double calculerDepenses(Budget b, int userId) {
        List<Transaction> transactions = transactionDAO.trouverParUtilisateur(userId);
        return transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .filter(t -> t.getCategorieId() == b.getCategorieId())
                .mapToDouble(Transaction::getMontant)
                .sum();
    }

    private void chargerBudgets() {
        int userId = SessionManager.getInstance().getUtilisateurConnecte().getId();
        List<Budget> liste = budgetDAO.trouverParUtilisateur(userId);
        tableBudgets.setItems(FXCollections.observableArrayList(liste));
    }

    private void confirmerSuppression(Budget b) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer ce budget ?");
        alert.setContentText(b.getMois() + " " + b.getAnnee() +
                " — " + String.format("%.2f €", b.getMontantMax()));
        alert.showAndWait().ifPresent(rep -> {
            if (rep == ButtonType.OK) {
                b.setEstActif(false);
                chargerBudgets();
            }
        });
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/moneywise/moneywise/fxml/form_budget.fxml")
            );

            // ✅ Configuration unique — pas de duplication
            Stage popup = new Stage();
            popup.setTitle("Nouveau Budget");

            Scene scene = new Scene(loader.load(), 450, 475);
            scene.getStylesheets().add(
                    getClass().getResource(
                            "/com/moneywise/moneywise/css/style.css"
                    ).toExternalForm()
            );

            popup.setScene(scene);
            popup.setMinWidth(450);
            popup.setMinHeight(475);
            popup.centerOnScreen();
            popup.initModality(Modality.APPLICATION_MODAL);

            FormBudgetController ctrl = loader.getController();
            ctrl.setOnSauvegarde(this::chargerBudgets);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleDashboard() {
        SceneManager.getInstance().allerVersDashboard();
    }

    @FXML private void handleDeconnexion() {
        SessionManager.getInstance().fermerSession();
        SceneManager.getInstance().allerVersLogin();
    }
}