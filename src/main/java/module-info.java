module com.moneywise.moneywise {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.moneywise.moneywise to javafx.fxml;
    exports com.moneywise.moneywise;
}