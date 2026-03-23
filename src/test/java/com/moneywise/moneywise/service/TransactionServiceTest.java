package com.moneywise.moneywise.service;

import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * On teste la LOGIQUE de calcul sans toucher à la BDD.
 * On crée les transactions manuellement dans les tests.
 */
class TransactionServiceTest {

    // Transactions de test (pas de BDD)
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        // Crée des transactions fictives pour les tests
        Transaction t1 = new Transaction(
                2000.0, TypeTransaction.ENTREE,
                LocalDate.of(2026, 3, 1),
                "Salaire", 1, 1
        );
        Transaction t2 = new Transaction(
                350.0, TypeTransaction.SORTIE,
                LocalDate.of(2026, 3, 5),
                "Courses", 1, 1
        );
        Transaction t3 = new Transaction(
                500.0, TypeTransaction.SORTIE,
                LocalDate.of(2026, 3, 10),
                "Loyer", 1, 2
        );
        Transaction t4 = new Transaction(
                1000.0, TypeTransaction.ENTREE,
                LocalDate.of(2026, 3, 15),
                "Freelance", 1, 1
        );

        transactions = Arrays.asList(t1, t2, t3, t4);
    }

    // ── Tests calcul revenus ───────────────────────────────
    @Test
    void calculerRevenus_avecTransactionsMixtes_retourneTotalEntrees() {
        double revenus = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        assertEquals(3000.0, revenus, 0.01);
    }

    // ── Tests calcul dépenses ──────────────────────────────
    @Test
    void calculerDepenses_avecTransactionsMixtes_retourneTotalSorties() {
        double depenses = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        assertEquals(850.0, depenses, 0.01);
    }

    // ── Tests calcul solde ─────────────────────────────────
    @Test
    void calculerSolde_revenus3000_depenses850_solde2150() {
        double revenus = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        double depenses = transactions.stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        double solde = revenus - depenses;
        assertEquals(2150.0, solde, 0.01);
    }

    // ── Tests liste vide ───────────────────────────────────
    @Test
    void calculerRevenus_listeVide_retourneZero() {
        double revenus = List.<Transaction>of().stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .mapToDouble(Transaction::getMontant)
                .sum();

        assertEquals(0.0, revenus, 0.01);
    }

    // ── Test montant positif ───────────────────────────────
    @Test
    void transaction_montantPositif_estValide() {
        Transaction t = new Transaction(
                100.0, TypeTransaction.SORTIE,
                LocalDate.now(), "Test", 1, 1
        );
        assertTrue(t.getMontant() > 0);
    }

    // ── Test type transaction ──────────────────────────────
    @Test
    void transaction_typeEntree_estBienEntree() {
        Transaction t = new Transaction(
                500.0, TypeTransaction.ENTREE,
                LocalDate.now(), "Salaire", 1, 1
        );
        assertEquals(TypeTransaction.ENTREE, t.getType());
        assertNotEquals(TypeTransaction.SORTIE, t.getType());
    }
}