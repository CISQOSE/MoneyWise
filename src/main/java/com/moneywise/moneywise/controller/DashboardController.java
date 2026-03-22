package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;
import com.moneywise.moneywise.service.AlerteService;
import com.moneywise.moneywise.service.TransactionService;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    // ── Composants FXML ───────────────────────────────────
    @FXML private Label labelBienvenue;
    @FXML private Label labelSolde;
    @FXML private Label labelRevenus;
    @FXML private Label labelDepenses;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;

    private final TransactionService transactionService = new TransactionService();

    // ── Initialisation ────────────────────────────────────
    @FXML
    private void initialize() {
        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte()
                .getId();
        String nom = SessionManager.getInstance()
                .getUtilisateurConnecte()
                .getNom();

        // Bienvenue
        labelBienvenue.setText("👋 Bonjour, " + nom + " !");

        // Calculs financiers
        double solde    = transactionService.getSolde(userId);
        double revenus  = transactionService.getTotalRevenus(userId);
        double depenses = transactionService.getTotalDepenses(userId);

        // Affiche les montants
        labelSolde.setText(String.format("%.2f €", solde));
        labelRevenus.setText(String.format("%.2f €", revenus));
        labelDepenses.setText(String.format("%.2f €", depenses));

        // Charge les graphiques
        chargerBarChart(userId);
        chargerPieChart(userId);

        // ✅ Ajoute cet appel à la fin
        chargerAlertes(userId);
    }

    // ── BarChart : Revenus vs Dépenses ────────────────────
    private void chargerBarChart(int userId) {
        List<Transaction> transactions = transactionService.getTransactions(userId);

        // Série Revenus
        XYChart.Series<String, Number> serieRevenus = new XYChart.Series<>();
        serieRevenus.setName("Revenus");

        // Série Dépenses
        XYChart.Series<String, Number> serieDepenses = new XYChart.Series<>();
        serieDepenses.setName("Dépenses");

        // Regroupe par mois
        Map<String, Double> revenus = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonth().toString().substring(0, 3),
                        Collectors.summingDouble(Transaction::getMontant)
                ));

        Map<String, Double> depenses = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonth().toString().substring(0, 3),
                        Collectors.summingDouble(Transaction::getMontant)
                ));

        revenus.forEach((mois, montant) ->
                serieRevenus.getData().add(new XYChart.Data<>(mois, montant)));

        depenses.forEach((mois, montant) ->
                serieDepenses.getData().add(new XYChart.Data<>(mois, montant)));

        barChart.getData().addAll(serieRevenus, serieDepenses);
        barChart.setTitle("Revenus vs Dépenses");
    }

    // ── PieChart : Dépenses par catégorie ─────────────────
    private void chargerPieChart(int userId) {
        List<Transaction> transactions = transactionService.getTransactions(userId);

        // Pour l'instant on regroupe par type (sera amélioré avec les catégories)
        double totalDepenses = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        double totalRevenus = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        if (totalDepenses > 0 || totalRevenus > 0) {
            pieChart.setData(FXCollections.observableArrayList(
                    new PieChart.Data("Dépenses", totalDepenses),
                    new PieChart.Data("Revenus", totalRevenus)
            ));
        } else {
            pieChart.setData(FXCollections.observableArrayList(
                    new PieChart.Data("Aucune donnée", 1)
            ));
        }
    }

    // ── Navigation ────────────────────────────────────────
    @FXML
    private void handleTransactions() {
        SceneManager.getInstance().allerVersTransactions();
    }

    @FXML
    private void handleDeconnexion() {
        SessionManager.getInstance().fermerSession();
        SceneManager.getInstance().allerVersLogin();
    }

    @FXML
    private void handleExport() {
        SceneManager.getInstance().allerVersExport();
    }

    @FXML
    private void handleAdmin() {
        // Vérifie que l'utilisateur est admin avant de naviguer
        if (SessionManager.getInstance().estAdmin()) {
            SceneManager.getInstance().allerVersAdmin();
        }
    }

    // ── Nouveau composant FXML ────────────────────────────
    @FXML private VBox zoneAlertes;

    // ── Nouveau service ───────────────────────────────────
    private final AlerteService alerteService = new AlerteService();

    // ── Charge et affiche les alertes ─────────────────────
    private void chargerAlertes(int userId) {
        List<AlerteService.Alerte> alertes =
                alerteService.verifierAlertes(userId);

        zoneAlertes.getChildren().clear();

        if (!alertes.isEmpty()) {
            // Rend la zone visible
            zoneAlertes.setVisible(true);
            zoneAlertes.setManaged(true);

            for (AlerteService.Alerte alerte : alertes) {

                // Couleur selon le niveau
                String couleur = alerte.getNiveau() ==
                        AlerteService.Alerte.NiveauAlerte.DEPASSE
                        ? "#e63946" : "#f4a261";

                // Crée le label d'alerte
                Label labelAlerte = new Label(alerte.getMessage());
                labelAlerte.setMaxWidth(Double.MAX_VALUE);
                labelAlerte.setStyle(
                        "-fx-background-color: " + couleur + "22;" +
                                "-fx-border-color: " + couleur + ";" +
                                "-fx-border-radius: 6px;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-padding: 10px;" +
                                "-fx-text-fill: " + couleur + ";" +
                                "-fx-font-weight: bold;"
                );

                zoneAlertes.getChildren().add(labelAlerte);
            }
        } else {
            zoneAlertes.setVisible(false);
            zoneAlertes.setManaged(false);
        }
    }

}