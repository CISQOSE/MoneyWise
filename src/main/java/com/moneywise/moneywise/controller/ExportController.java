package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.service.ExportService;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ExportController {

    @FXML private Label labelInfo;

    private final ExportService exportService     = new ExportService();
    private final TransactionDAO transactionDAO   = new TransactionDAO();

    @FXML
    private void initialize() {
        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();
        int nbTransactions = transactionDAO
                .trouverParUtilisateur(userId).size();
        labelInfo.setText(nbTransactions + " transaction(s) à exporter");
    }

    // ── Export PDF ────────────────────────────────────────
    @FXML
    private void handleExportPDF() {
        File fichier = choisirFichier("PDF", "*.pdf");
        if (fichier == null) return;

        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();
        String nom = SessionManager.getInstance()
                .getUtilisateurConnecte().getNom();
        List<Transaction> transactions =
                transactionDAO.trouverParUtilisateur(userId);

        boolean succes = exportService.exporterPDF(
                transactions, nom, fichier.getAbsolutePath()
        );

        afficherResultat(succes, fichier.getName(), "PDF");
    }

    // ── Export Excel ──────────────────────────────────────
    @FXML
    private void handleExportExcel() {
        File fichier = choisirFichier("Excel", "*.xlsx");
        if (fichier == null) return;

        int userId = SessionManager.getInstance()
                .getUtilisateurConnecte().getId();
        String nom = SessionManager.getInstance()
                .getUtilisateurConnecte().getNom();
        List<Transaction> transactions =
                transactionDAO.trouverParUtilisateur(userId);

        boolean succes = exportService.exporterExcel(
                transactions, nom, fichier.getAbsolutePath()
        );

        afficherResultat(succes, fichier.getName(), "Excel");
    }

    // ── Ouvre le sélecteur de fichier ─────────────────────
    private File choisirFichier(String description, String extension) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le fichier");

        // ✅ Force l'extension selon le type
        if (extension.equals("*.xlsx")) {
            chooser.setInitialFileName("moneywise_export.xlsx");
        } else {
            chooser.setInitialFileName("moneywise_export.pdf");
        }

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, extension)
        );
        Stage stage = (Stage) labelInfo.getScene().getWindow();
        File fichier = chooser.showSaveDialog(stage);

        // ✅ Ajoute l'extension si l'utilisateur l'a oubliée
        if (fichier != null && !fichier.getName().endsWith(".xlsx")
                && extension.equals("*.xlsx")) {
            fichier = new File(fichier.getAbsolutePath() + ".xlsx");
        }
        if (fichier != null && !fichier.getName().endsWith(".pdf")
                && extension.equals("*.pdf")) {
            fichier = new File(fichier.getAbsolutePath() + ".pdf");
        }

        return fichier;
    }

    // ── Affiche le résultat ───────────────────────────────
    private void afficherResultat(boolean succes, String nom, String type) {
        Alert alert = new Alert(succes ?
                Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(succes ? "Export réussi" : "Erreur d'export");
        alert.setHeaderText(succes ?
                "✅ " + type + " exporté avec succès !" :
                "❌ Erreur lors de l'export");
        alert.setContentText(succes ?
                "Fichier : " + nom : "Vérifiez les logs pour plus de détails.");
        alert.showAndWait();
    }

    // ── Navigation ────────────────────────────────────────
    @FXML
    private void handleDashboard() {
        SceneManager.getInstance().allerVersDashboard();
    }
}