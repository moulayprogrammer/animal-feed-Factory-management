package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class  MainController implements Initializable {

    @FXML
    BorderPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setPadding(new Insets(0,10,5,10));

        ShowProductScreen();
    }

    @FXML
    private void close(){
        ((Stage)mainPane.getScene().getWindow()).close();
    }

    @FXML
    private void ShowRawMaterialScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/RawMaterialViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowMedicationScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MedicationViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowProductScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowClientScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ClientViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowProviderScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProviderViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowDeliveryScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/DeliveryViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowStoreScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/StoreViews/MainView.fxml"));
            BorderPane temp = loader.load();
            mainPane.setCenter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}