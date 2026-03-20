package com.moneywise.moneywise.model;

/**
 * MODÈLE : représente une catégorie de dépenses.
 * estSysteme = true  → catégorie prédéfinie (Alimentation, Transport...)
 * estSysteme = false → catégorie personnalisée par l'utilisateur
 */
public class Categorie {

    private int id;
    private String nom;
    private String icone;
    private boolean estSysteme;
    private int utilisateurId;

    public Categorie() {}

    public Categorie(String nom, String icone, boolean estSysteme) {
        this.nom = nom;
        this.icone = icone;
        this.estSysteme = estSysteme;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public boolean isEstSysteme() { return estSysteme; }
    public void setEstSysteme(boolean estSysteme) { this.estSysteme = estSysteme; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "'}";
    }
}