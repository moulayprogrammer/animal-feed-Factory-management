package Controllers.RawMaterialControllers;

import BddPackage.RawMaterialOperation;
import Models.RawMaterial;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MainController implements Initializable {

    @FXML
    TextField tfRecherche;
    @FXML
    TableView<RawMaterial> table;
    @FXML
    TableColumn<RawMaterial,String> clName,clReference;
    @FXML
    TableColumn<RawMaterial,Integer> clId,clLimiteQte,clQte;


    private final ObservableList<RawMaterial> dataTable = FXCollections.observableArrayList();
    private final RawMaterialOperation operation = new RawMaterialOperation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        clLimiteQte.setCellValueFactory(new PropertyValueFactory<>("limitQte"));
        clQte.setCellValueFactory(new PropertyValueFactory<>("qte"));

        refresh();
    }

    @FXML
    private void ActionAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/RawMaterialViews/AddView.fxml"));
            DialogPane temp = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(temp);
            dialog.resizableProperty().setValue(false);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            closeButton.setVisible(false);
            dialog.showAndWait();

            refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ActionUpdate(){

        RawMaterial rawMaterial = table.getSelectionModel().getSelectedItem();
        if (rawMaterial != null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/RawMaterialViews/UpdateView.fxml"));
                DialogPane temp = loader.load();
                UpdateController controller = loader.getController();
                controller.InitUpdate(rawMaterial);
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(temp);
                dialog.resizableProperty().setValue(false);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                closeButton.setVisible(false);
                dialog.showAndWait();
                refresh();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("??????????");
            alertWarning.setContentText("???????????? ???????????? ???????? ?????? ???? ?????? ??????????????");
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("??????????");
            alertWarning.showAndWait();
        }
    }

    @FXML
    private void ActionAddToArchive(){
        RawMaterial rawMaterial = table.getSelectionModel().getSelectedItem();

        if (rawMaterial != null){
            if (rawMaterial.getQte() == 0) {
                try {

                    Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    alertConfirmation.setHeaderText("?????????? ??????????????");
                    alertConfirmation.setContentText("???? ?????? ?????????? ???? ?????????? ???????????? ??????????");
                    alertConfirmation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertConfirmation.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("??????????");

                    Button cancel = (Button) alertConfirmation.getDialogPane().lookupButton(ButtonType.CANCEL);
                    cancel.setText("??????????");

                    alertConfirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.CANCEL) {
                            alertConfirmation.close();
                        } else if (response == ButtonType.OK) {
                            operation.AddToArchive(rawMaterial);
                            refresh();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setHeaderText("???? ???????? ?????????????? ");
                alertInformation.setContentText("???? ???????????? ?????????? ???????????? ?????????????? ?????????? ???????????? ???? ????????????");
                alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("??????????");
                alertInformation.showAndWait();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("?????????? ");
            alertWarning.setContentText("???????????? ???????????? ???????? ?????? ????????????????");
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("??????????");
            alertWarning.showAndWait();
        }
    }


    @FXML
    private void ActionDeleteFromArchive(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/RawMaterialViews/ArchiveView.fxml"));
            DialogPane temp = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(temp);
            dialog.resizableProperty().setValue(false);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            closeButton.setVisible(false);
            dialog.showAndWait();

            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refresh(){
        ArrayList<RawMaterial> rawMaterials = operation.getAll();
        dataTable.setAll(rawMaterials);
        table.setItems(dataTable);
    }

    @FXML
    private void ActionRefresh(){
        clearRecherche();
        refresh();
    }

    private void clearRecherche(){
        tfRecherche.clear();
    }

    @FXML
    void ActionSearch() {
        // filtrer les donn??es
        ObservableList<RawMaterial> dataRaw = table.getItems();
        FilteredList<RawMaterial> filteredData = new FilteredList<>(dataRaw, e -> true);
        String txtRecherche = tfRecherche.getText().trim();

        filteredData.setPredicate((Predicate<? super RawMaterial>) rawMaterial -> {
            if (txtRecherche.isEmpty()) {
                //loadDataInTable();
                return true;
            } else if (rawMaterial.getName().contains(txtRecherche)) {
                return true;
            } else if (rawMaterial.getReference().contains(txtRecherche)) {
                return true;
            } else return String.valueOf(rawMaterial.getLimitQte()).contains(txtRecherche);
        });

        SortedList<RawMaterial> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }
}
