package com.moneywise.moneywise.service;

import com.moneywise.moneywise.dao.UtilisateurDAO;
import com.moneywise.moneywise.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

/**
 * AuthService : gère l'inscription et la connexion.
 *
 * La logique métier est ici — pas dans le Controller.
 * Le Controller appelle le Service, le Service appelle le DAO.
 *
 * Controller → Service → DAO → MySQL
 */
public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    // ══════════════════════════════════════════════
    //  INSCRIPTION
    // ══════════════════════════════════════════════
    public ResultatAuth inscrire(String nom, String email, String motDePasse) {

        // Validation : champs vides
        if (nom.isBlank() || email.isBlank() || motDePasse.isBlank()) {
            return ResultatAuth.erreur("Tous les champs sont obligatoires.");
        }

        // Validation : format email simple
        if (!email.contains("@") || !email.contains(".")) {
            return ResultatAuth.erreur("Format d'email invalide.");
        }

        // Validation : mot de passe trop court
        if (motDePasse.length() < 6) {
            return ResultatAuth.erreur("Le mot de passe doit contenir au moins 6 caractères.");
        }

        // Vérifier si l'email est déjà utilisé
        if (utilisateurDAO.trouverParEmail(email) != null) {
            return ResultatAuth.erreur("Cet email est déjà utilisé.");
        }

        // Hasher le mot de passe avant de stocker
        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());

        // Créer l'utilisateur
        Utilisateur nouvelUtilisateur = new Utilisateur(nom, email, hash);
        boolean succes = utilisateurDAO.creer(nouvelUtilisateur);

        if (succes) {
            return ResultatAuth.succes("Compte créé avec succès !", nouvelUtilisateur);
        } else {
            return ResultatAuth.erreur("Erreur lors de la création du compte.");
        }
    }

    // ══════════════════════════════════════════════
    //  CONNEXION
    // ══════════════════════════════════════════════
    public ResultatAuth connecter(String email, String motDePasse) {

        // Validation : champs vides
        if (email.isBlank() || motDePasse.isBlank()) {
            return ResultatAuth.erreur("Veuillez remplir tous les champs.");
        }

        // Chercher l'utilisateur par email
        Utilisateur utilisateur = utilisateurDAO.trouverParEmail(email);

        // Utilisateur non trouvé
        if (utilisateur == null) {
            return ResultatAuth.erreur("Email ou mot de passe incorrect.");
        }

        // Vérifier si le compte est actif
        if (!utilisateur.isEstActif()) {
            return ResultatAuth.erreur("Ce compte a été désactivé.");
        }

        // Vérifier le mot de passe avec BCrypt
        boolean motDePasseCorrect = BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse());

        if (motDePasseCorrect) {
            return ResultatAuth.succes("Connexion réussie !", utilisateur);
        } else {
            return ResultatAuth.erreur("Email ou mot de passe incorrect.");
        }
    }

    // ══════════════════════════════════════════════
    //  Classe interne : résultat d'une opération Auth
    // ══════════════════════════════════════════════
    public static class ResultatAuth {

        private final boolean succes;
        private final String message;
        private final Utilisateur utilisateur;

        private ResultatAuth(boolean succes, String message, Utilisateur utilisateur) {
            this.succes = succes;
            this.message = message;
            this.utilisateur = utilisateur;
        }

        // Fabrique un résultat succès
        public static ResultatAuth succes(String message, Utilisateur u) {
            return new ResultatAuth(true, message, u);
        }

        // Fabrique un résultat erreur
        public static ResultatAuth erreur(String message) {
            return new ResultatAuth(false, message, null);
        }

        public boolean isSucces()           { return succes; }
        public String getMessage()          { return message; }
        public Utilisateur getUtilisateur() { return utilisateur; }
    }
}