package com.moneywise.moneywise.util;

import com.moneywise.moneywise.model.Utilisateur;

/**
 * SessionManager : garde en mémoire l'utilisateur connecté.
 *
 * Singleton — une seule session active à la fois.
 * Comme les variables de session en PHP/web.
 */
public class SessionManager {

    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Ouvrir la session
    public void ouvrirSession(Utilisateur u) {
        this.utilisateurConnecte = u;
        System.out.println("✅ Session ouverte pour : " + u.getNom());
    }

    // Fermer la session (déconnexion)
    public void fermerSession() {
        System.out.println("👋 Session fermée pour : " + utilisateurConnecte.getNom());
        this.utilisateurConnecte = null;
    }

    // Récupérer l'utilisateur connecté
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    // Vérifier si quelqu'un est connecté
    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    // Vérifier si l'utilisateur connecté est admin
    public boolean estAdmin() {
        return estConnecte() && utilisateurConnecte.isEstAdmin();
    }
}