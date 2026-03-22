package com.moneywise.moneywise.controller;

import com.moneywise.moneywise.dao.UtilisateurDAO;
import com.moneywise.moneywise.model.Utilisateur;
import com.moneywise.moneywise.util.SceneManager;
import com.moneywise.moneywise.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class AdminController {

    // ── Composants FXML ───────────────────────────────────
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colId;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colStatut;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colActions;
    @FXML private Label labelStats;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    // ── Initialisation ────────────────────────────────────
    @FXML
    private void initialize() {
        // Vérifie que l'utilisateur est bien admin
        if (!SessionManager.getInstance().estAdmin()) {
            SceneManager.getInstance().allerVersDashboard();
            return;
        }
        configurerColonnes();
        chargerUtilisateurs();
    }

    // ── Configuration des colonnes ─────────────────────────
    private void configurerColonnes() {

        colId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(data.getValue().getId())
                )
        );

        colNom.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom())
        );

        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        // Colonne Statut avec couleur
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Utilisateur u = getTableRow().getItem();
                    if (u.isEstActif()) {
                        setText("✅ Actif");
                        setStyle("-fx-text-fill: #2dc653; -fx-font-weight: bold;");
                    } else {
                        setText("❌ Inactif");
                        setStyle("-fx-text-fill: #e63946; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isEstActif() ? "Actif" : "Inactif"
                )
        );

        // Colonne Rôle
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Utilisateur u = getTableRow().getItem();
                    if (u.isEstAdmin()) {
                        setText("👑 Admin");
                        setStyle("-fx-text-fill: #f4a261; -fx-font-weight: bold;");
                    } else {
                        setText("👤 User");
                        setStyle("-fx-text-fill: #4361ee;");
                    }
                }
            }
        });
        colRole.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isEstAdmin() ? "Admin" : "User"
                )
        );

        // Colonne Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle   = new Button();
            private final Button btnAdmin    = new Button("👑");
            private final Button btnSupprimer = new Button("🗑️");

            {
                btnToggle.setStyle(
                        "-fx-background-color: #4361ee; -fx-text-fill: white;" +
                                "-fx-background-radius: 4px; -fx-cursor: hand;");
                btnAdmin.setStyle(
                        "-fx-background-color: #f4a261; -fx-text-fill: white;" +
                                "-fx-background-radius: 4px; -fx-cursor: hand;");
                btnSupprimer.setStyle(
                        "-fx-background-color: #e63946; -fx-text-fill: white;" +
                                "-fx-background-radius: 4px; -fx-cursor: hand;");

                // Toggle actif/inactif
                btnToggle.setOnAction(e -> {
                    Utilisateur u = getTableRow().getItem();
                    if (u != null) toggleStatut(u);
                });

                // Toggle admin/user
                btnAdmin.setOnAction(e -> {
                    Utilisateur u = getTableRow().getItem();
                    if (u != null) toggleAdmin(u);
                });

                // Supprimer
                btnSupprimer.setOnAction(e -> {
                    Utilisateur u = getTableRow().getItem();
                    if (u != null) confirmerSuppression(u);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Utilisateur u = getTableRow().getItem();
                    btnToggle.setText(u.isEstActif() ? "⏸️" : "▶️");

                    // Empêche de modifier son propre compte
                    int monId = SessionManager.getInstance()
                            .getUtilisateurConnecte().getId();
                    boolean estMoi = u.getId() == monId;
                    btnToggle.setDisable(estMoi);
                    btnAdmin.setDisable(estMoi);
                    btnSupprimer.setDisable(estMoi);

                    HBox boutons = new HBox(5,
                            btnToggle, btnAdmin, btnSupprimer);
                    setGraphic(boutons);
                }
            }
        });
        colActions.setCellValueFactory(data ->
                new SimpleStringProperty("")
        );
    }

    // ── Charger les utilisateurs ───────────────────────────
    private void chargerUtilisateurs() {
        List<Utilisateur> liste = utilisateurDAO.trouverTous();
        tableUtilisateurs.setItems(FXCollections.observableArrayList(liste));
        labelStats.setText(liste.size() + " utilisateur(s) enregistré(s)");
    }

    // ── Activer / Désactiver un compte ─────────────────────
    private void toggleStatut(Utilisateur u) {
        u.setEstActif(!u.isEstActif());
        if (utilisateurDAO.modifier(u)) {
            chargerUtilisateurs();
        }
    }

    // ── Promouvoir / Rétrograder Admin ─────────────────────
    private void toggleAdmin(Utilisateur u) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Changer le rôle");
        confirm.setHeaderText(u.isEstAdmin() ?
                "Retirer les droits Admin à " + u.getNom() + " ?" :
                "Donner les droits Admin à " + u.getNom() + " ?"
        );
        confirm.showAndWait().ifPresent(rep -> {
            if (rep == ButtonType.OK) {
                u.setEstAdmin(!u.isEstAdmin());
                utilisateurDAO.modifier(u);
                chargerUtilisateurs();
            }
        });
    }

    // ── Supprimer un utilisateur ───────────────────────────
    private void confirmerSuppression(Utilisateur u) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer " + u.getNom() + " ?");
        alert.setContentText(
                "⚠️ Cette action supprimera aussi toutes ses transactions !"
        );
        alert.showAndWait().ifPresent(rep -> {
            if (rep == ButtonType.OK) {
                utilisateurDAO.supprimer(u.getId());
                chargerUtilisateurs();
            }
        });
    }

    // ── Navigation ────────────────────────────────────────
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