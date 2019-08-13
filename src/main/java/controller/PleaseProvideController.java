package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PleaseProvideController implements Initializable {

    @FXML
    private Text info;


    public void setInfo(String info) {
        this.info.setText(info);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
