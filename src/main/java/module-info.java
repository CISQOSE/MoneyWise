module com.moneywise.moneywise {

    // ── Dépendances JavaFX ──────────────────────────
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // ── Base de données ─────────────────────────────
    requires java.sql;

    // ── Sécurité ────────────────────────────────────
    requires jbcrypt;

    // ── Export ──────────────────────────────────────
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    // ── driver MySQL ──────────────────────────────────────
    requires mysql.connector.j;
    requires org.testng;

    // ── Accès par réflexion (JavaFX en a besoin) ────
    opens com.moneywise.moneywise to javafx.fxml, javafx.graphics;
    opens com.moneywise.moneywise.controller to javafx.fxml;

    // ── Export du package principal ─────────────────
    exports com.moneywise.moneywise;
}
