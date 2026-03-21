package com.moneywise.moneywise.dao;

import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final Connection conn;

    public TransactionDAO() {
        this.conn = DatabaseConnection.getInstance();
    }

    // ══════════════════════════════════════════════
    //  CREATE — Insérer une transaction
    // ══════════════════════════════════════════════
    public boolean creer(Transaction t) {
        // ✅ Bonne table + bons noms de colonnes SQL
        String sql = "INSERT INTO transaction "
                + "(montant, type, date, description, utilisateur_id, categorie_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, t.getMontant());
            stmt.setString(2, t.getType().name());
            stmt.setDate(3, Date.valueOf(t.getDate()));
            stmt.setString(4, t.getDescription());
            stmt.setInt(5, t.getUtilisateurId());
            stmt.setInt(6, t.getCategorieId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur création transaction : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  READ — Toutes les transactions d'un utilisateur
    // ══════════════════════════════════════════════
    public List<Transaction> trouverParUtilisateur(int utilisateurId) {
        // ✅ Bon nom de colonne SQL
        String sql = "SELECT * FROM transaction WHERE utilisateur_id = ?";
        List<Transaction> liste = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            // ✅ while pour lire TOUTES les lignes
            while (rs.next()) {
                liste.add(mapperTransaction(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche transactions : " + e.getMessage());
        }

        return liste; // ✅ liste vide si aucun résultat (jamais null)
    }

    // ══════════════════════════════════════════════
    //  UTILITAIRE — Convertir une ligne SQL en objet
    // ══════════════════════════════════════════════
    private Transaction mapperTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setMontant(rs.getDouble("montant"));
        // ✅ Affecter le résultat de valueOf()
        t.setType(Transaction.TypeTransaction.valueOf(rs.getString("type")));
        t.setDate(rs.getDate("date").toLocalDate());
        t.setDescription(rs.getString("description"));
        t.setUtilisateurId(rs.getInt("utilisateur_id"));
        t.setCategorieId(rs.getInt("categorie_id"));
        return t;
    }

    // ══════════════════════════════════════════════
    //  UPDATE — Modifier une transaction
    // ══════════════════════════════════════════════
    public boolean modifier(Transaction t) {
        String sql = "UPDATE transaction SET montant=?, type=?, date=?, "
                + "description=?, categorie_id=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, t.getMontant());
            stmt.setString(2, t.getType().name());
            stmt.setDate(3, Date.valueOf(t.getDate()));
            stmt.setString(4, t.getDescription());
            stmt.setInt(5, t.getCategorieId());
            stmt.setInt(6, t.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur modification transaction : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  DELETE — Supprimer une transaction
    // ══════════════════════════════════════════════
    public boolean supprimer(int id) {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression transaction : " + e.getMessage());
            return false;
        }
    }
}