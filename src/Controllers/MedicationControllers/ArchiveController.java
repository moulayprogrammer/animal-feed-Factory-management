package Controllers.MedicationControllers;

import BddPackage.MedicationOperation;
import Models.Medication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ArchiveController implements Initializable {

    @FXML
    TextField tfRecherche;
    @FXML
    TableView<Medication> table;
    @FXML
    TableColumn<Medication,String> clName,clReference;
    @FXML
    TableColumn<Medication,Integer> clId, clLimitQte;

    private final ObservableList<Medication> dataTable = FXCollections.observableArrayList();
    private final MedicationOperation operation = new MedicationOperation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        clLimitQte.setCellValueFactory(new PropertyValueFactory<>("limitQte"));

        refresh();
    }

    @FXML
    private void ActionDeleteFromArchive(){
        Medication medication = table.getSelectionModel().getSelectedItem();

        if (medication != null){
            try {

                operation.DeleteFromArchive(medication);
                ActionAnnuler();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("تحذير ");
            alertWarning.setContentText("الرجاء اختيار دواء لالغاء أرشفته");
            alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("موافق");
            alertWarning.showAndWait();
        }
    }

    @FXML
    private void ActionAnnuler(){
        ((Stage)tfRecherche.getScene().getWindow()).close();
    }

    @FXML
    private void ActionRefresh(){
        clearRecherche();
        refresh();
    }

    private void clearRecherche(){
        tfRecherche.clear();
    }

    private void refresh(){
        ArrayList<Medication> medications = operation.getAllArchive();
        dataTable.setAll(medications);
        table.setItems(dataTable);
    }

    @FXML
    void ActionSearch() {
        // filtrer les données
        ObservableList<Medication> dataMedication = table.getItems();
        FilteredList<Medication> filteredData = new FilteredList<>(dataMedication, e -> true);
        String txtRecherche = tfRecherche.getText().trim();

        filteredData.setPredicate((Predicate<? super Medication>) medication -> {
            if (txtRecherche.isEmpty()) {
                //loadDataInTable();
                return true;
            } else if (medication.getName().contains(txtRecherche)) {
                return true;
            } else if (medication.getReference().contains(txtRecherche)) {
                return true;
            } else return String.valueOf(medication.getLimitQte()).contains(txtRecherche);
        });

        SortedList<Medication> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

    }
}
