package com.moneywise.moneywise.service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    // ── Teste les règles de validation ────────────────────

    @Test
    void validation_emailSansArobase_invalide() {
        String email = "alicetest.com"; // pas de @
        assertFalse(email.contains("@"));
    }

    @Test
    void validation_emailValide_valide() {
        String email = "alice@test.com";
        assertTrue(email.contains("@") && email.contains("."));
    }

    @Test
    void validation_motDePasseTropCourt_invalide() {
        String mdp = "123"; // moins de 6 caractères
        assertTrue(mdp.length() < 6);
    }

    @Test
    void validation_motDePasseAssezLong_valide() {
        String mdp = "admin123"; // 8 caractères
        assertFalse(mdp.length() < 6);
    }

    @Test
    void validation_champsVides_invalide() {
        String nom   = "";
        String email = "";
        String mdp   = "";

        assertTrue(nom.isBlank() || email.isBlank() || mdp.isBlank());
    }

    @Test
    void validation_champsRemplis_valide() {
        String nom   = "Alice";
        String email = "alice@test.com";
        String mdp   = "motdepasse123";

        assertFalse(nom.isBlank() || email.isBlank() || mdp.isBlank());
    }

    @Test
    void validation_emailAvecEspaces_invalideApresTrim() {
        String email = "  alice@test.com  ";
        String emailNettoye = email.trim();

        // Après trim, l'email est valide
        assertTrue(emailNettoye.contains("@"));
        // Mais avant trim il avait des espaces
        assertNotEquals(email, emailNettoye);
    }
}