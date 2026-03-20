package com.moneywise.moneywise.controller;

import javafx.fxml.FXML;
import com.moneywise.moneywise.util.SceneManager;

public class TransactionController {

    @FXML
    private void handleRetourDashboard() {
        SceneManager.getInstance().allerVersDashboard();
    }
}