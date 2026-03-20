package com.moneywise.moneywise.service;

import com.moneywise.moneywise.dao.TransactionDAO;
import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;

import java.util.List;

/**
 * Service pour les calculs financiers.
 * Le Controller demande les données → le Service calcule → le DAO fournit.
 */
public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }

    // Toutes les transactions d'un utilisateur
    public List<Transaction> getTransactions(int utilisateurId) {
        return transactionDAO.trouverParUtilisateur(utilisateurId);
    }

    // Calcule le total des revenus
    public double getTotalRevenus(int utilisateurId) {
        return transactionDAO.trouverParUtilisateur(utilisateurId)
                .stream()
                .filter(t -> t.getType() == TypeTransaction.ENTREE)
                .mapToDouble(Transaction::getMontant)
                .sum();
    }

    // Calcule le total des dépenses
    public double getTotalDepenses(int utilisateurId) {
        return transactionDAO.trouverParUtilisateur(utilisateurId)
                .stream()
                .filter(t -> t.getType() == TypeTransaction.SORTIE)
                .mapToDouble(Transaction::getMontant)
                .sum();
    }

    // Calcule le solde (revenus - dépenses)
    public double getSolde(int utilisateurId) {
        return getTotalRevenus(utilisateurId) - getTotalDepenses(utilisateurId);
    }
}