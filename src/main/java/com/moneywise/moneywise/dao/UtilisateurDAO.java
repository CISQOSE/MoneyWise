package com.moneywise.moneywise.dao;

import com.moneywise.moneywise.model.Utilisateur;
import com.moneywise.moneywise.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table UTILISATEUR.
 * Toutes les opérations BDD liées aux utilisateurs sont ici.
 */
public class UtilisateurDAO {

    private final Connection conn;

    public UtilisateurDAO() {
        this.conn = DatabaseConnection.getInstance();
    }

    // ══════════════════════════════════════════════
    //  CREATE — Insérer un nouvel utilisateur
    // ══════════════════════════════════════════════
    public boolean creer(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, email, mot_de_passe, est_admin, est_actif) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getMotDePasse());
            stmt.setBoolean(4, u.isEstAdmin());
            stmt.setBoolean(5, u.isEstActif());

            return stmt.executeUpdate() > 0; // true si insertion réussie

        } catch (SQLException e) {
            System.err.println("❌ Erreur création utilisateur : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  READ — Trouver par email (pour le login)
    // ══════════════════════════════════════════════
    public Utilisateur trouverParEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapperUtilisateur(rs); // convertit la ligne en objet
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche utilisateur : " + e.getMessage());
        }
        return null; // utilisateur non trouvé
    }

    // ══════════════════════════════════════════════
    //  READ — Trouver par ID
    // ══════════════════════════════════════════════
    public Utilisateur trouverParId(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapperUtilisateur(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche utilisateur : " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════════
    //  READ — Trouver tous les utilisateurs
    // ══════════════════════════════════════════════
    public List<Utilisateur> trouverTous() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(mapperUtilisateur(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur liste utilisateurs : " + e.getMessage());
        }
        return liste;
    }

    // ══════════════════════════════════════════════
    //  UPDATE — Modifier un utilisateur
    // ══════════════════════════════════════════════
    public boolean modifier(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom=?, email=?, est_actif=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getEmail());
            stmt.setBoolean(3, u.isEstActif());
            stmt.setInt(4, u.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur modification utilisateur : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  DELETE — Supprimer un utilisateur
    // ══════════════════════════════════════════════
    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression utilisateur : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  UTILITAIRE — Convertir une ligne SQL en objet
    // ══════════════════════════════════════════════
    private Utilisateur mapperUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setEstAdmin(rs.getBoolean("est_admin"));
        u.setEstActif(rs.getBoolean("est_actif"));
        u.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        return u;
    }
}