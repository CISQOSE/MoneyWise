package com.moneywise.moneywise.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection : gère la connexion à MySQL.
 *
 * Pattern Singleton : une seule connexion partagée
 * dans toute l'application.
 */
public class DatabaseConnection {

    // ── Configuration MySQL ────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/moneywise"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";       // utilisateur XAMPP
    private static final String PASSWORD = "";           // mot de passe XAMPP (vide par défaut)

    // ── Singleton ──────────────────────────────────────────
    private static Connection instance;

    // Constructeur privé
    private DatabaseConnection() {}

    /**
     * Retourne la connexion active.
     * La crée si elle n'existe pas encore.
     */
    public static Connection getInstance() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion MySQL établie !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL : " + e.getMessage());
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Ferme la connexion proprement.
     * À appeler quand l'application se ferme.
     */
    public static void fermer() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("🔒 Connexion MySQL fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}