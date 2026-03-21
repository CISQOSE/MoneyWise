package com.moneywise.moneywise.service;

import com.moneywise.moneywise.dao.BudgetDAO;
import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Budget;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * AlerteService : vérifie les budgets et génère des alertes.
 */
public class AlerteService {

    // ── Classe interne : représente une alerte ─────────────
    public static class Alerte {

        public enum NiveauAlerte {
            ATTENTION,   // 80% atteint
            DEPASSE      // 100% dépassé
        }

        private final String message;
        private final NiveauAlerte niveau;
        private final double pourcentage;

        public Alerte(String message, NiveauAlerte niveau, double pourcentage) {
            this.message = message;
            this.niveau = niveau;
            this.pourcentage = pourcentage;
        }

        public String getMessage()        { return message; }
        public NiveauAlerte getNiveau()   { return niveau; }
        public double getPourcentage()    { return pourcentage; }
    }

    private final BudgetDAO budgetDAO           = new BudgetDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // ══════════════════════════════════════════════
    //  Vérifie tous les budgets d'un utilisateur
    //  et retourne la liste des alertes actives
    // ══════════════════════════════════════════════
    public List<Alerte> verifierAlertes(int utilisateurId) {
        List<Alerte> alertes = new ArrayList<>();

        // Récupère tous les budgets actifs
        List<Budget> budgets = budgetDAO.trouverParUtilisateur(utilisateurId);

        // Récupère toutes les transactions
        List<Transaction> transactions =
                transactionDAO.trouverParUtilisateur(utilisateurId);

        for (Budget budget : budgets) {

            // Calcule le total des dépenses pour ce budget (mois + année)
            double totalDepenses = transactions.stream()
                    .filter(t -> t.getType() == TypeTransaction.SORTIE)
                    .filter(t -> t.getCategorieId() == budget.getCategorieId())
                    .filter(t -> t.getDate().getMonthValue() ==
                            moisEnNombre(budget.getMois()))
                    .filter(t -> t.getDate().getYear() == budget.getAnnee())
                    .mapToDouble(Transaction::getMontant)
                    .sum();

            // Calcule le pourcentage utilisé
            double pourcentage = (totalDepenses / budget.getMontantMax()) * 100;

            // Génère une alerte selon le seuil
            if (pourcentage >= 100) {
                alertes.add(new Alerte(
                        String.format("🔴 Budget dépassé ! %.1f€ / %.1f€ (%.0f%%)",
                                totalDepenses, budget.getMontantMax(), pourcentage),
                        Alerte.NiveauAlerte.DEPASSE,
                        pourcentage
                ));
            } else if (pourcentage >= 80) {
                alertes.add(new Alerte(
                        String.format("⚠️ Attention ! %.1f€ / %.1f€ (%.0f%%)",
                                totalDepenses, budget.getMontantMax(), pourcentage),
                        Alerte.NiveauAlerte.ATTENTION,
                        pourcentage
                ));
            }
        }

        return alertes;
    }

    // ── Convertit un nom de mois en numéro ────────────────
    private int moisEnNombre(String mois) {
        return switch (mois.toUpperCase()) {
            case "JANVIER"   -> 1;
            case "FEVRIER"   -> 2;
            case "MARS"      -> 3;
            case "AVRIL"     -> 4;
            case "MAI"       -> 5;
            case "JUIN"      -> 6;
            case "JUILLET"   -> 7;
            case "AOUT"      -> 8;
            case "SEPTEMBRE" -> 9;
            case "OCTOBRE"   -> 10;
            case "NOVEMBRE"  -> 11;
            case "DECEMBRE"  -> 12;
            default          -> -1;
        };
    }
}