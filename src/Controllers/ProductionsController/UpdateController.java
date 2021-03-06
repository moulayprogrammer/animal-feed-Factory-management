package Controllers.ProductionsController;

import BddPackage.*;
import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class UpdateController implements Initializable {

    @FXML
    TextField tfQte,tfPrice,tfQteMaterial,tfQteNew,tfPriceNew;
    @FXML
    ComboBox<String> cbProduct,cbMaterial;
    @FXML
    Button btnInsert;

    private final ConnectBD connectBD = new ConnectBD();
    private Connection conn;

    private final ProductionOperation operation = new ProductionOperation();
    private final ProductOperation productOperation = new ProductOperation();
    private final ComponentStoreRawMaterialOperation componentStoreMaterialOperation = new ComponentStoreRawMaterialOperation();
    private final ComponentStoreProductTempMaterialOperation componentStoreProductTempMaterialOperation = new ComponentStoreProductTempMaterialOperation();

    private final ObservableList<String> dataComboProduct = FXCollections.observableArrayList();
    private final ObservableList<String> dataComboMaterial = FXCollections.observableArrayList();
    private final ArrayList<Integer> listIdProduct = new ArrayList<>();
    private final ArrayList<Integer> listIdMaterial = new ArrayList<>();
    private final ArrayList<ComponentStoreProductTemp> componentStoreMaterialTemp = new ArrayList<>();
    private final ArrayList<ComponentStore> componentStoreMaterial = new ArrayList<>();

    private Production productionSelected;
    private double priceProduction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = connectBD.connect();

        refreshComboProduct();


//        refreshComponent();

        cbProduct.setEditable(true);
        cbProduct.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextFields.bindAutoCompletion(cbProduct.getEditor(), cbProduct.getItems());

        cbMaterial.setEditable(true);
        cbMaterial.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextFields.bindAutoCompletion(cbMaterial.getEditor(), cbMaterial.getItems());
    }

    public void Init(Production production){
        this.productionSelected = production;

        cbProduct.getSelectionModel().select(listIdProduct.indexOf(productionSelected.getIdProduct()));
        tfQte.setText(String.valueOf(productionSelected.getQteProduct()));
        tfPrice.setText(String.format(Locale.FRANCE, "%,.2f", productionSelected.getPrice() * productionSelected.getQteProduct() ));

//        this.priceProduction = productionSelected.getPrice() * productionSelected.getQteProduct();

        refreshComboMaterial();
//        getComponentProduction();
    }

    private void refreshComboProduct(){
        dataComboProduct.clear();
        listIdProduct.clear();
        try {
            ArrayList<Product> products = productOperation.getAll();

            products.forEach(product -> {
                dataComboProduct.add(product.getName());
                listIdProduct.add(product.getId());
            });

            cbProduct.setItems(dataComboProduct);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void refreshComboMaterial(){
        dataComboMaterial.clear();
        listIdMaterial.clear();
        try {
            if (conn.isClosed()) conn = connectBD.connect();

            String query = "SELECT ????????_????????????_??????????.????????_????????????_??????????, ????????????_??????????.??????????  FROM ????????_????????????_??????????, ????????????_??????????  WHERE ????????_???????????? = ? AND ????????_????????????_?????????? = ????????????";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, this.productionSelected.getIdProduct());
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){
                dataComboMaterial.add(resultSet.getString("??????????"));
                listIdMaterial.add(resultSet.getInt("????????_????????????_??????????"));
            }

            cbMaterial.setItems(dataComboMaterial);
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void countPrice(){
        try {
            String stQte = tfQteMaterial.getText().trim();
            int indexMat = cbMaterial.getSelectionModel().getSelectedIndex();
            String stNewQte = tfQteNew.getText().trim();

            if (indexMat != -1 && !stQte.isEmpty() && !stNewQte.isEmpty()){
                int idMat = listIdMaterial.get(indexMat);
                int qte = Integer.parseInt(stQte);
                if (checkQte(idMat,qte)) {
                    priceProduction = 0;
                    componentStoreMaterialTemp.clear();
                    componentStoreMaterial.clear();

                    ArrayList<ComponentStore> CSM = componentStoreMaterialOperation.getAllByMaterialOrderByDate(listIdMaterial.get(indexMat));
                    int qteNeed = qte;

                    for (ComponentStore componentStore : CSM) {

                        int qteStored = componentStore.getQteStored();
                        int qteConsumed = componentStore.getQteConsumed();
                        double price = componentStore.getPrice();

                        ComponentStoreProductTemp componentStoreProductTemp = new ComponentStoreProductTemp();
                        componentStoreProductTemp.setIdComponent(componentStore.getIdComponent());
                        componentStoreProductTemp.setIdDeliveryArrival(componentStore.getIdDeliveryArrival());

                        if ((qteStored - qteConsumed) >= qteNeed) {

                            priceProduction += qteNeed * price;
                            componentStoreProductTemp.setQte(qteNeed);
                            componentStoreMaterialTemp.add(componentStoreProductTemp);
                            componentStore.setQteConsumed(qteConsumed + qteNeed);
                            componentStoreMaterial.add(componentStore);
                            break;

                        } else {

                            int qteRest = qteStored - qteConsumed;
                            qteNeed -= qteRest;
                            priceProduction += qteRest * price;
                            componentStoreProductTemp.setQte(qteRest);
                            componentStoreMaterialTemp.add(componentStoreProductTemp);
                            componentStore.setQteConsumed(qteConsumed + qteRest);
                            componentStoreMaterial.add(componentStore);
                        }
                    }

                    priceProduction += productionSelected.getPrice() * productionSelected.getQteProduct() ;

                    tfPriceNew.setText(String.format(Locale.FRANCE, "%,.2f", priceProduction));
                }else {
                    Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                    alertWarning.setHeaderText("???????????? ?????????? ");
                    alertWarning.setContentText("???????? ???????????? ?????????????? ???????????????? ???? ???????? ???????????? ?????? ????????????");
                    alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("??????????");
                    alertWarning.showAndWait();
                }
            }else {
                Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                alertWarning.setHeaderText("?????????? ");
                alertWarning.setContentText("???????????? ?????? ???????? ?????????????? ?? ???????????? ???????????? ?????????? ?????? ??????????????");
                alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("??????????");
                alertWarning.showAndWait();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkQte(int idMat, int qte){
        boolean ex = true;
        try {
            if (conn.isClosed()) conn = connectBD.connect();

            try {
                String query = "SELECT sum(????????_?????????? - ????????_??????????????) as ???????? FROM ??????????_????????????_?????????? WHERE ????????_????????????_?????????? = ? ;";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setInt(1,idMat);
                ResultSet resultSet = preparedStmt.executeQuery();
                int qteComponent = 0 ;
                if (resultSet.next()){
                    qteComponent = resultSet.getInt("????????");
                }
                if (qteComponent < qte) {
                    ex = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ex;
    }

    @FXML
    private void ActionAnnulledAdd(){
        closeDialog(btnInsert);
    }

    @FXML
    void ActionUpdate() {

        String stQte = tfQteNew.getText().trim();
        String stPrice = tfPriceNew.getText().trim();

        if ( !stQte.isEmpty() && !stPrice.isEmpty() ){

            Production production = new Production();

            int qte =  Integer.parseInt(stQte);
            production.setQteProduct(qte);
            production.setPrice(priceProduction / qte);

            boolean update = update(production);
            if (update) {
                insertComponent();
                ActionAnnulledAdd();
            }else {
                Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                alertWarning.setHeaderText("?????????? ");
                alertWarning.setContentText("?????? ?????? ??????????");
                alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("??????????");
                alertWarning.showAndWait();
            }
        }else {
            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
            alertWarning.setHeaderText("?????????? ");
            alertWarning.setContentText("???????????? ?????? ???????? ????????????");
            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("??????????");
            alertWarning.showAndWait();
        }
    }

    private void insertComponent() {


        for (int i = 0; i < componentStoreMaterialTemp.size(); i++) {
            ComponentStoreProductTemp componentStoreProductTemp = componentStoreMaterialTemp.get(i);

            componentStoreProductTemp.setIdProduction(productionSelected.getId());
            insertComponentStoreTempMaterial(componentStoreProductTemp);
            updateQteComponentStoreMaterial(componentStoreMaterial.get(i));
        }
    }

    private boolean update(Production production)  {
        boolean update = false;
        try {
            update = operation.update(production,productionSelected);
            return update;
        }catch (Exception e){
            e.printStackTrace();
            return update;
        }
    }

    private void insertComponentStoreTempMaterial(ComponentStoreProductTemp storeProductTemp){
        boolean insert = false;
        try {
            insert = componentStoreProductTempMaterialOperation.insert(storeProductTemp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateQteComponentStoreMaterial(ComponentStore store){
        boolean update = false;
        try {
            update = componentStoreMaterialOperation.updateQte(store);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void closeDialog(Button btn) {
        ((Stage)btn.getScene().getWindow()).close();
    }

}
