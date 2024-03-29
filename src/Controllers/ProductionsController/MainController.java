package Controllers.ProductionsController;

import BddPackage.*;
import Models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MainController implements Initializable {

    @FXML
    TextField tfRecherche;
    @FXML
    TableView<List<StringProperty>> table;
    @FXML
    TableColumn<List<StringProperty>,String> clId,clProduct,clDate,clPriceUnit,clQte,clPrice,clConfirmation;

    private final ObservableList<List<StringProperty>> dataTable = FXCollections.observableArrayList();
    private final ProductionOperation operation = new ProductionOperation();

    private final ComponentStoreMedicationOperation componentStoreMedicationOperation = new ComponentStoreMedicationOperation();
    private final ComponentStoreRawMaterialOperation componentStoreMaterialOperation = new ComponentStoreRawMaterialOperation();
    private final ArrayList<ComponentStore> componentStoreMedication = new ArrayList<>();
    private final ArrayList<ComponentStore> componentStoreMaterial = new ArrayList<>();

    private final ConnectBD connectBD = new ConnectBD();
    private Connection conn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        conn = connectBD.connect();

        clId.setCellValueFactory(data -> data.getValue().get(0));
        clProduct.setCellValueFactory(data -> data.getValue().get(1));
        clDate.setCellValueFactory(data -> data.getValue().get(2));
        clPriceUnit.setCellValueFactory(data -> data.getValue().get(3));
        clQte.setCellValueFactory(data -> data.getValue().get(4));
        clPrice.setCellValueFactory(data -> data.getValue().get(5));
        clConfirmation.setCellValueFactory(data -> data.getValue().get(6));

        refresh();
    }

    @FXML
    private void ActionAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductionsViews/AddView.fxml"));
            DialogPane temp = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(temp);
            dialog.resizableProperty().setValue(false);
            dialog.initOwner(this.tfRecherche.getScene().getWindow());
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
    private void tableClick(MouseEvent mouseEvent) {
        if ( mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY) ){

            ActionUpdate();
        }
    }
    @FXML
    private void ActionUpdate(){
        List<StringProperty> dataSelected = table.getSelectionModel().getSelectedItem();

        if (dataSelected != null){
            if (dataSelected.get(6).getValue().equals("غير ماكد")) {
                try {
                    Production production = operation.get(Integer.parseInt(dataSelected.get(0).getValue()));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductionsViews/UpdateView.fxml"));
                    DialogPane temp = loader.load();
                    UpdateController controller = loader.getController();
                    controller.Init(production);
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(temp);
                    dialog.resizableProperty().setValue(false);
                    dialog.initOwner(this.tfRecherche.getScene().getWindow());
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                    closeButton.setVisible(false);
                    dialog.showAndWait();

                    refresh();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setHeaderText("لا يمكن التعديل");
                alertInformation.setContentText("تم تـأكبد الانتاج بالفعل فلا يمكن تعديله" );
                alertInformation.initOwner(this.tfRecherche.getScene().getWindow());
                alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("موافق");
                alertInformation.showAndWait();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("تحذير");
            alertWarning.setContentText("الرجاء اختيار منتج من اجل التعديل");
            alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("موافق");
            alertWarning.showAndWait();
        }
    }

    @FXML
    private void ActionConfirm(){
        List<StringProperty> dataSelected = table.getSelectionModel().getSelectedItem();

        if (dataSelected != null){
            if (dataSelected.get(6).getValue().equals("غير ماكد")) {
                try {
                    Production production = operation.get(Integer.parseInt(dataSelected.get(0).getValue()));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductionsViews/ConfirmationView.fxml"));
                    DialogPane temp = loader.load();
                    ConfirmationController controller = loader.getController();
                    controller.Init(production);
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(temp);
                    dialog.resizableProperty().setValue(false);
                    dialog.initOwner(this.tfRecherche.getScene().getWindow());
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                    closeButton.setVisible(false);
                    dialog.showAndWait();

                    refresh();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setHeaderText("التاكيد");
                alertInformation.setContentText("تم تـأكبد الانتاج بالفعل" );
                alertInformation.initOwner(this.tfRecherche.getScene().getWindow());
                alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("موافق");
                alertInformation.showAndWait();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("تحذير");
            alertWarning.setContentText("الرجاء اختيار إنتاج من اجل التأكيد");
            alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("موافق");
            alertWarning.showAndWait();
        }
    }

    @FXML
    private void ActionDelete(){
        List<StringProperty> dataSelected = table.getSelectionModel().getSelectedItem();

        if (dataSelected != null){
            if (dataSelected.get(6).getValue().equals("غير ماكد")) {
                try {
                    Production production = operation.get(Integer.parseInt(dataSelected.get(0).getValue()));

                    Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    alertConfirmation.setHeaderText("تاكيد الحذف");
                    alertConfirmation.setContentText("هل انت متاكد من حذف هذا الانتاج");
                    alertConfirmation.initOwner(this.tfRecherche.getScene().getWindow());
                    alertConfirmation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertConfirmation.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("موافق");

                    Button cancel = (Button) alertConfirmation.getDialogPane().lookupButton(ButtonType.CANCEL);
                    cancel.setText("الغاء");

                    alertConfirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.CANCEL) {
                            alertConfirmation.close();
                        } else if (response == ButtonType.OK) {

                            // update store medication comsomation
                            try {
                                if (conn.isClosed()) conn = connectBD.connect();
                                componentStoreMedication.clear();

                                String query = "SELECT تخزين_الادوية.معرف_الدواء, تخزين_الادوية.معرف_وصل_التوصيل, الكمية, كمية_مستهلكة FROM  تخزين_الادوية_مؤقت_للانتاج, تخزين_الادوية" +
                                        " WHERE معرف_الانتاج = ? AND تخزين_الادوية.معرف_الدواء = تخزين_الادوية_مؤقت_للانتاج.معرف_الدواء AND  تخزين_الادوية.معرف_وصل_التوصيل = تخزين_الادوية_مؤقت_للانتاج.معرف_وصل_التوصيل";
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setInt(1, production.getId());
                                ResultSet resultSet = preparedStmt.executeQuery();
                                while (resultSet.next()) {

                                    ComponentStore componentStore = new ComponentStore();
                                    componentStore.setIdComponent(resultSet.getInt("معرف_الدواء"));
                                    componentStore.setIdDeliveryArrival(resultSet.getInt("معرف_وصل_التوصيل"));
                                    componentStore.setQteConsumed(resultSet.getInt("كمية_مستهلكة") - resultSet.getInt("الكمية"));

                                    componentStoreMedication.add(componentStore);
//                                updateQteComponentStoreMedication(componentStore);

                                }
                                conn.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // update store material comsomation
                            try {
                                if (conn.isClosed()) conn = connectBD.connect();
                                componentStoreMaterial.clear();

                                String query = "SELECT  تخزين_المواد_الخام.معرف_المادة_الخام, تخزين_المواد_الخام.معرف_وصل_التوصيل, الكمية, كمية_مستهلكة FROM  تخزين_مواد_خام_مؤقت_للانتاج, تخزين_المواد_الخام\n" +
                                        "WHERE تخزين_المواد_الخام.معرف_المادة_الخام = تخزين_مواد_خام_مؤقت_للانتاج.معرف_المادة_الخام AND  تخزين_المواد_الخام.معرف_وصل_التوصيل = تخزين_مواد_خام_مؤقت_للانتاج.معرف_وصل_التوصيل\n" +
                                        "AND معرف_الانتاج = ?;";
                                PreparedStatement preparedStmt = conn.prepareStatement(query);
                                preparedStmt.setInt(1, production.getId());
                                ResultSet resultSet = preparedStmt.executeQuery();
                                while (resultSet.next()) {

                                    ComponentStore componentStore = new ComponentStore();
                                    componentStore.setIdComponent(resultSet.getInt("معرف_المادة_الخام"));
                                    componentStore.setIdDeliveryArrival(resultSet.getInt("معرف_وصل_التوصيل"));
                                    componentStore.setQteConsumed(resultSet.getInt("كمية_مستهلكة") - resultSet.getInt("الكمية"));

                                    componentStoreMaterial.add(componentStore);
//                                updateQteComponentStoreMaterial(componentStore);

                                }
                                conn.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                updateComponent();
                                operation.delete(production);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            refresh();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setHeaderText("لا يمكن الحذف");
                alertInformation.setContentText("تم تـأكبد الانتاج بالفعل فلا يمكن حذفه" );
                alertInformation.initOwner(this.tfRecherche.getScene().getWindow());
                alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("موافق");
                alertInformation.showAndWait();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("تحذير");
            alertWarning.setContentText("الرجاء اختيار إنتاج من اجل الحذف");
            alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("موافق");
            alertWarning.showAndWait();
        }
    }

    private void updateComponent() {

        for (ComponentStore componentStore : componentStoreMedication) {
            updateQteComponentStoreMedication(componentStore);
        }
        for (ComponentStore componentStore : componentStoreMaterial) {
            updateQteComponentStoreMaterial(componentStore);
        }
    }

    private boolean updateQteComponentStoreMedication(ComponentStore store){
        boolean update = false;
        try {
            update = componentStoreMedicationOperation.updateQte(store);
            return update;
        }catch (Exception e){
            e.printStackTrace();
            return update;
        }
    }

    private boolean updateQteComponentStoreMaterial( ComponentStore store){
        boolean update = false;
        try {
            update = componentStoreMaterialOperation.updateQte(store);
            return update;
        }catch (Exception e){
            e.printStackTrace();
            return update;
        }
    }

    private void refresh(){
        try {
            if (conn.isClosed()) conn = connectBD.connect();
            dataTable.clear();

            String query = "SELECT الانتاج.المعرف, المنتجات.الاسم, الانتاج.التاريخ, الانتاج.الكمية_المنتجة, الانتاج.التكلفة, \n" +
                    "(SELECT معرف_الانتاج FROM تخزين_منتج WHERE معرف_الانتاج = الانتاج.المعرف ) AS التاكيد FROM المنتجات, الانتاج WHERE المنتجات.المعرف = الانتاج.معرف_المنتج";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {

                List<StringProperty> data = new ArrayList<>();
                data.add( new SimpleStringProperty(String.valueOf(resultSet.getInt("المعرف"))));//0
                data.add( new SimpleStringProperty(resultSet.getString("الاسم")));//1
                data.add( new SimpleStringProperty(String.valueOf(resultSet.getDate("التاريخ").toLocalDate())));//2
                double cost = resultSet.getDouble("التكلفة");
                data.add( new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", cost )));//3
                data.add( new SimpleStringProperty(String.valueOf(resultSet.getInt("الكمية_المنتجة"))));//4
                data.add( new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", cost * resultSet.getInt("الكمية_المنتجة") )));//5
                if (resultSet.getInt("التاكيد") != 0) data.add( new SimpleStringProperty("ماكد"));//6
                else data.add( new SimpleStringProperty("غير ماكد"));//6

                dataTable.add(data);
            }
            conn.close();
            table.setItems(dataTable);
        }catch (Exception e){
            e.printStackTrace();
        }
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
        // filtrer les données
        ObservableList<List<StringProperty>> items = table.getItems();
        FilteredList<List<StringProperty>> filteredData = new FilteredList<>(items, e -> true);
        String txtRecherche = tfRecherche.getText().trim();

        filteredData.setPredicate((Predicate<? super List<StringProperty>>) stringProperties -> {
            if (txtRecherche.isEmpty()) {
                //loadDataInTable();
                return true;
            } else if (stringProperties.get(1).toString().contains(txtRecherche)) {
                return true;
            } else if (stringProperties.get(2).toString().contains(txtRecherche)) {
                return true;
            }else if (stringProperties.get(3).toString().contains(txtRecherche)) {
                return true;
            } else if (stringProperties.get(4).toString().contains(txtRecherche)) {
                return true;
            }  else return stringProperties.get(6).toString().contains(txtRecherche);
        });

        SortedList<List<StringProperty>> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }
}
