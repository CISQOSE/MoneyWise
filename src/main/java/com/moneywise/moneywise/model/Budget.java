package com.moneywise.moneywise.model;

public class Budget {

    private int id;
    private double montantMax;
    private String mois;
    private int annee;
    private boolean estActif;
    private int utilisateurId;
    private int categorieId;

    // Constructeur vide
    public Budget() {}

    // ✅ Constructeur sans id (généré par la BDD)
    // ✅ Avec utilisateurId et categorieId
    // ✅ estActif = true par défaut
    public Budget(double montantMax, String mois, int annee,
                  int utilisateurId, int categorieId) {
        this.montantMax = montantMax;
        this.mois = mois;
        this.annee = annee;
        this.utilisateurId = utilisateurId;
        this.categorieId = categorieId;
        this.estActif = true;  // ✅ true par défaut
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getMontantMax() { return montantMax; }
    public void setMontantMax(double montantMax) { this.montantMax = montantMax; }

    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }

    @Override
    public String toString() {
        return "Budget{id=" + id +
                ", montantMax=" + montantMax +
                ", mois=" + mois +
                ", annee=" + annee + "}";
    }
}