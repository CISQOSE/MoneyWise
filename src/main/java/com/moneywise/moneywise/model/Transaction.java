package com.moneywise.moneywise.model;

import java.time.LocalDate;

/**
 * MODÈLE : représente une transaction financière.
 * Type ENTREE = revenu, Type SORTIE = dépense
 */
public class Transaction {

    // ── Enum : les deux types possibles ───────────────────
    public enum TypeTransaction {
        ENTREE, SORTIE
    }

    // ── Attributs ─────────────────────────────────────────
    private int id;
    private double montant;
    private TypeTransaction type;
    private LocalDate date;
    private String description;
    private int utilisateurId;
    private int categorieId;

    // ── Constructeur vide ──────────────────────────────────
    public Transaction() {}

    // ── Constructeur complet ───────────────────────────────
    public Transaction(double montant, TypeTransaction type,
                       LocalDate date, String description,
                       int utilisateurId, int categorieId) {
        this.montant = montant;
        this.type = type;
        this.date = date;
        this.description = description;
        this.utilisateurId = utilisateurId;
        this.categorieId = categorieId;
    }

    // ── Getters & Setters ──────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public TypeTransaction getType() { return type; }
    public void setType(TypeTransaction type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }

    @Override
    public String toString() {
        return "Transaction{id=" + id +
                ", montant=" + montant +
                ", type=" + type +
                ", date=" + date + "}";
    }
}