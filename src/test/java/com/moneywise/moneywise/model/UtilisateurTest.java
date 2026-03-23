package com.moneywise.moneywise.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        // Crée un utilisateur avant chaque test
        utilisateur = new Utilisateur("Alice", "alice@test.com", "hash123");
    }

    @Test
    void constructeur_donneesValides_attributsCorrects() {
        assertEquals("Alice", utilisateur.getNom());
        assertEquals("alice@test.com", utilisateur.getEmail());
        assertEquals("hash123", utilisateur.getMotDePasse());
    }

    @Test
    void constructeur_nouvelUtilisateur_estActifParDefaut() {
        assertTrue(utilisateur.isEstActif());
    }

    @Test
    void constructeur_nouvelUtilisateur_pasAdminParDefaut() {
        assertFalse(utilisateur.isEstAdmin());
    }

    @Test
    void constructeur_nouvelUtilisateur_dateInscriptionNonNull() {
        assertNotNull(utilisateur.getDateInscription());
    }

    @Test
    void setEstAdmin_true_utilisateurDevientAdmin() {
        utilisateur.setEstAdmin(true);
        assertTrue(utilisateur.isEstAdmin());
    }

    @Test
    void setEstActif_false_utilisateurDesactive() {
        utilisateur.setEstActif(false);
        assertFalse(utilisateur.isEstActif());
    }

    @Test
    void toString_contientNomEtEmail() {
        String resultat = utilisateur.toString();
        assertTrue(resultat.contains("Alice"));
        assertTrue(resultat.contains("alice@test.com"));
    }

    @Test
    void setNom_nouveauNom_nomMisAJour() {
        utilisateur.setNom("Bob");
        assertEquals("Bob", utilisateur.getNom());
    }

    @Test
    void toString_utilisateurValide_retourneNonNull() {
        assertNotNull(utilisateur.toString());
    }
}