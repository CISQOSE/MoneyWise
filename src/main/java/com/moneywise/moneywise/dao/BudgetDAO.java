package com.moneywise.moneywise.dao;

import com.moneywise.moneywise.model.Budget;
import com.moneywise.moneywise.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    private final Connection conn;

    public BudgetDAO() {
        this.conn = DatabaseConnection.getInstance();
    }

    // ══════════════════════════════════════════════
    //  CREATE
    // ══════════════════════════════════════════════
    public boolean creer(Budget b) {
        String sql = "INSERT INTO budget "
                + "(montant_max, mois, annee, est_actif, utilisateur_id, categorie_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, b.getMontantMax());
            stmt.setString(2, b.getMois());
            stmt.setInt(3, b.getAnnee());
            stmt.setBoolean(4, b.isEstActif());
            stmt.setInt(5, b.getUtilisateurId());
            stmt.setInt(6, b.getCategorieId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur création budget : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  READ — Budgets actifs d'un utilisateur
    // ══════════════════════════════════════════════
    public List<Budget> trouverParUtilisateur(int utilisateurId) {
        List<Budget> liste = new ArrayList<>();
        String sql = "SELECT * FROM budget WHERE utilisateur_id = ? AND est_actif = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(mapperBudget(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste budgets : " + e.getMessage());
        }
        return liste;
    }

    // ══════════════════════════════════════════════
    //  UTILITAIRE
    // ══════════════════════════════════════════════
    private Budget mapperBudget(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setMontantMax(rs.getDouble("montant_max"));
        b.setMois(rs.getString("mois"));
        b.setAnnee(rs.getInt("annee"));
        b.setEstActif(rs.getBoolean("est_actif"));
        b.setUtilisateurId(rs.getInt("utilisateur_id"));
        b.setCategorieId(rs.getInt("categorie_id"));
        return b;
    }
}