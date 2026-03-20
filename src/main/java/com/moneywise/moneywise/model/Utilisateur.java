package com.moneywise.moneywise.model;

import java.time.LocalDate;

/**
 * MODÈLE : représente un utilisateur de MoneyWise.
 * Cette classe correspond à la table UTILISATEUR en base de données.
 *
 * C'est un simple "conteneur de données" (POJO)
 * POJO = Plain Old Java Object = classe Java simple sans logique complexe
 */
public class Utilisateur {

    // ── Attributs (correspondent aux colonnes en BDD) ──────
    private int id;
    private String nom;
    private String email;
    private String motDePasse;
    private LocalDate dateInscription;
    private boolean estActif;
    private boolean estAdmin;

    // ── Constructeur vide (requis pour JDBC) ───────────────
    public Utilisateur() {}

    // ── Constructeur complet ───────────────────────────────
    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateInscription = LocalDate.now();
        this.estActif = true;
        this.estAdmin = false;
    }

    // ── Getters & Setters ──────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    public boolean isEstAdmin() { return estAdmin; }
    public void setEstAdmin(boolean estAdmin) { this.estAdmin = estAdmin; }

    // ── toString : utile pour déboguer ─────────────────────
    @Override
    public String toString() {
        return "Utilisateur{id=" + id +
                ", nom='" + nom + "'" +
                ", email='" + email + "'" +
                ", estAdmin=" + estAdmin + "}";
    }
}