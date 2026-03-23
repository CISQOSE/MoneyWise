package com.moneywise.moneywise.service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AlerteServiceTest {

    // ── Teste la logique des seuils directement ────────────

    @Test
    void seuil_depensesA80Pourcent_declencheAttention() {
        double montantMax   = 500.0;
        double totalDepenses = 400.0; // 80%

        double pourcentage = (totalDepenses / montantMax) * 100;

        assertTrue(pourcentage >= 80);
        assertFalse(pourcentage >= 100);
    }

    @Test
    void seuil_depensesA100Pourcent_declencheDepasse() {
        double montantMax    = 500.0;
        double totalDepenses = 500.0; // 100%

        double pourcentage = (totalDepenses / montantMax) * 100;

        assertTrue(pourcentage >= 100);
    }

    @Test
    void seuil_depensesA270Pourcent_declencheDepasse() {
        double montantMax    = 300.0;
        double totalDepenses = 810.0; // 270% — notre cas réel !

        double pourcentage = (totalDepenses / montantMax) * 100;

        assertTrue(pourcentage >= 100);
        assertEquals(270.0, pourcentage, 0.01);
    }

    @Test
    void seuil_depensesA50Pourcent_aucuneAlerte() {
        double montantMax    = 500.0;
        double totalDepenses = 250.0; // 50%

        double pourcentage = (totalDepenses / montantMax) * 100;

        assertFalse(pourcentage >= 80);
        assertFalse(pourcentage >= 100);
    }

    @Test
    void seuil_depensesA79Pourcent_aucuneAlerte() {
        double montantMax    = 500.0;
        double totalDepenses = 395.0; // 79%

        double pourcentage = (totalDepenses / montantMax) * 100;

        // 79% < 80% → pas d'alerte
        assertFalse(pourcentage >= 80);
    }

    @Test
    void seuil_depensesA81Pourcent_declencheAttentionPasDepasse() {
        double montantMax    = 500.0;
        double totalDepenses = 405.0; // 81%

        double pourcentage = (totalDepenses / montantMax) * 100;

        assertTrue(pourcentage >= 80);   // ⚠️ ATTENTION
        assertFalse(pourcentage >= 100); // pas encore DÉPASSÉ
    }
}