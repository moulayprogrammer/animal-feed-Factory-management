package Controllers.ReceiptRawMaterialControllers;

import BddPackage.*;
import Models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class AddController implements Initializable {

    @FXML
    DatePicker dpDate;
    @FXML
    Label lbFactureNbr,lbDebt,lbTransaction,lbSumWeight,lbSumTotal;
    @FXML
    ComboBox<String> cbProvider;
    @FXML
    TextField tfRechercheMaterial,tfRecherche;
    @FXML
    TableView<List<StringProperty>> tableRawMaterial, tablePurchases;
    @FXML
    TableColumn<List<StringProperty>,String> tcIdMaterial, tcNameMaterial, tcReferenceMaterial;
    @FXML
    TableColumn<List<StringProperty>,String> tcId,tcName,tcPriceU,tcQte,tcPriceTotal;
    @FXML
    Button btnInsert;

    private final ConnectBD connectBD = new ConnectBD();
    private Connection conn;
    private final ReceiptRawMaterialOperation operation = new ReceiptRawMaterialOperation();
    private final RawMaterialOperation rawMaterialOperation = new RawMaterialOperation();
    private final ProviderOperation providerOperation = new ProviderOperation();
    private final ComponentProductionRawMaterialOperation componentMaterialOperation = new ComponentProductionRawMaterialOperation();
    private final ComponentReceiptRawMaterialOperation componentReceiptRawMaterialOperation = new ComponentReceiptRawMaterialOperation();
    private final ObservableList<List<StringProperty>> dataTable = FXCollections.observableArrayList();
    private final List<Double> priceList = new ArrayList<>();
    private final ObservableList<String> comboProviderData = FXCollections.observableArrayList();
    private final List<Integer> idProviderCombo = new ArrayList<>();
    private int selectedProvider = 0;
    private double totalFacture = 0;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = connectBD.connect();

        tcIdMaterial.setCellValueFactory(data -> data.getValue().get(0));
        tcNameMaterial.setCellValueFactory(data -> data.getValue().get(1));
        tcReferenceMaterial.setCellValueFactory(data -> data.getValue().get(2));

        tcId.setCellValueFactory(data -> data.getValue().get(0));
        tcName.setCellValueFactory(data -> data.getValue().get(1));
        tcPriceU.setCellValueFactory(data -> data.getValue().get(2));
        tcQte.setCellValueFactory(data -> data.getValue().get(3));
        tcPriceTotal.setCellValueFactory(data -> data.getValue().get(4));

        refreshComponent();
        refreshComboProvider();
        setFactureNumber();

        // set Date
        dpDate.setValue(LocalDate.now());

        //set Combo Search
        cbProvider.setEditable(true);
        cbProvider.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextFields.bindAutoCompletion(cbProvider.getEditor(), cbProvider.getItems());

        tfRechercheMaterial.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.isEmpty()) {
                ObservableList<List<StringProperty>> items = tableRawMaterial.getItems();
                FilteredList<List<StringProperty>> filteredData = new FilteredList<>(items, e -> true);

                filteredData.setPredicate((Predicate<? super List<StringProperty>>) stringProperties -> {

                    if (stringProperties.get(1).toString().contains(newValue)) {
                        return true;
                    } else return stringProperties.get(2).toString().contains(newValue);
                });

                SortedList<List<StringProperty>> sortedList = new SortedList<>(filteredData);
                sortedList.comparatorProperty().bind(tableRawMaterial.comparatorProperty());
                tableRawMaterial.setItems(sortedList);
            }else {
                refreshComponent();
            }
        });
    }

    @FXML
    private void ActionComboProvider(){
        int index = cbProvider.getSelectionModel().getSelectedIndex();
        if (index >= 0 ) {
            selectedProvider = idProviderCombo.get(index);
            setProviderTransaction(selectedProvider);
        }
    }

    private void setProviderTransaction(int idProvider) {
        try {
            double debt = 0.0;
            AtomicReference<Double> trans = new AtomicReference<>(0.0);
            AtomicReference<Double> pay = new AtomicReference<>(0.0);
            ArrayList<Receipt> receipts = operation.getAllByProvider(idProvider);
            receipts.forEach(receipt -> {
                ArrayList<ComponentReceipt> componentReceipts = componentReceiptRawMaterialOperation.getAllByReceipt(receipt.getId());
                AtomicReference<Double> sumR = new AtomicReference<>(0.0);
                componentReceipts.forEach(componentReceipt -> {
                    double pr = componentReceipt.getPrice() * componentReceipt.getQte();
                    sumR.updateAndGet(v -> (double) (v + pr));
                });
                pay.updateAndGet(v -> (double) (v + receipt.getPaying()));
                trans.updateAndGet(v -> (double) (v + sumR.get()));
            });
            debt = trans.get() - pay.get();
            lbTransaction.setText(String.format(Locale.FRANCE, "%,.2f", (trans.get())));
            lbDebt.setText(String.format(Locale.FRANCE, "%,.2f", (debt)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFactureNumber() {
        int nbr = operation.getLastNumber();
        int year = LocalDate.now().getYear();
        lbFactureNbr.setText((nbr + 1) + "/" + year );
    }

    private void refreshComboProvider() {
        clearCombo();
        try {
            ArrayList<Provider> providers = providerOperation.getAll();
            providers.forEach(provider -> {
                comboProviderData.add(provider.getName());
                idProviderCombo.add(provider.getId());
            });
            cbProvider.setItems(comboProviderData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clearCombo(){
        cbProvider.getSelectionModel().clearSelection();
        comboProviderData.clear();
        idProviderCombo.clear();
        lbDebt.setText("");
        lbTransaction.setText("");
    }

    private void refreshComponent(){
        ObservableList<List<StringProperty>> componentDataTable = FXCollections.observableArrayList();

        try {
            ArrayList<RawMaterial> materials = rawMaterialOperation.getAll();

            materials.forEach(medication -> {
                List<StringProperty> data = new ArrayList<>();
                data.add( new SimpleStringProperty(String.valueOf(medication.getId())));
                data.add( new SimpleStringProperty(medication.getName()));
                data.add(new SimpleStringProperty(medication.getReference()));
                componentDataTable.add(data);
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        tableRawMaterial.setItems(componentDataTable);

    }

    @FXML
    private void ActionAddProvider(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProviderViews/AddView.fxml"));
            DialogPane temp = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(temp);
            dialog.resizableProperty().setValue(false);
            dialog.initOwner(this.tfRecherche.getScene().getWindow());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            closeButton.setVisible(false);
            dialog.showAndWait();

            refreshComboProvider();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ActionPayDebtProvider(){
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("التسديد");
        dialog.initOwner(this.tfRecherche.getScene().getWindow());
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.setHeaderText("ادخل السعر المسدد ");
        dialog.setContentText("السعر :");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(price -> {

            if (!price.isEmpty() && !lbDebt.getText().equals("0.0")) {
                double pr = Double.parseDouble(price);

                ArrayList<Receipt> receipts = operation.getAllByProvider(selectedProvider);
                for (int i = 0; i < receipts.size(); i++) {
                    Receipt receipt = receipts.get(i);
                    ArrayList<ComponentReceipt> componentReceipts = componentReceiptRawMaterialOperation.getAllByReceipt(receipt.getId());
                    AtomicReference<Double> sumR = new AtomicReference<>(0.0);
                    componentReceipts.forEach(componentReceipt -> {
                        double pre = componentReceipt.getPrice() * componentReceipt.getQte();
                        sumR.updateAndGet(v -> (double) (v + pre));
                    });
                    double debt = sumR.get() - receipt.getPaying();
                    if (debt > 0){
                        if (pr <= debt){
                            double newPaying = pr + receipt.getPaying();
                            pr = 0;
                            PayDebtReceipt(receipt.getId(),newPaying);
                            break;
                        }else {
                            double newPaying = debt + receipt.getPaying();
                            pr = pr - debt;
                            PayDebtReceipt(receipt.getId(),newPaying);
                        }
                    }
                }
                if (pr > 0 ){
                    Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                    alertInformation.setHeaderText("المبلغ المتبقي ");
                    alertInformation.initOwner(this.tfRecherche.getScene().getWindow());
                    alertInformation.setContentText("المبلغ المتبقي من التسديد هو   " + String.format(Locale.FRANCE, "%,.2f", (pr)));
                    alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("موافق");
                    alertInformation.showAndWait();
                }
                setProviderTransaction(selectedProvider);
            }
        });
    }

    private void PayDebtReceipt(int receiptId , double price) {
        try {
            Receipt receipt1 = new Receipt();
            receipt1.setPaying(price);

            Receipt receipt2 =  new Receipt();
            receipt2.setId(receiptId);

            operation.updatePaying(receipt1,receipt2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void tableMaterialClick(MouseEvent mouseEvent) {
        if ( mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY) ){

            ActionAddToCompositionDefault();
        }
    }
    @FXML
    private void ActionAddToCompositionDefault(){
        List<StringProperty> dataSelected = tableRawMaterial.getSelectionModel().getSelectedItem();
        if (dataSelected != null) {
            int ex = exist(dataSelected);
            if ( ex != -1 ){
                try {
                    int val = Integer.parseInt(dataTable.get(ex).get(3).getValue()) + 1 ;
                    double pr = priceList.get(ex);
                    dataTable.get(ex).get(3).setValue(String.valueOf(val));
                    dataTable.get(ex).get(4).setValue(String.format(Locale.FRANCE, "%,.2f", (val * pr)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                try {
                    TextInputDialog dialog = new TextInputDialog();

                    dialog.setTitle("السعر");
                    dialog.initOwner(this.tfRecherche.getScene().getWindow());
                    dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    dialog.setHeaderText("ادخل سعر الشراء ");
                    dialog.setContentText("السعر :");

                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(price -> {
                        double pr = Double.parseDouble(price);
                        List<StringProperty> data = new ArrayList<>();
                        data.add(0, new SimpleStringProperty(dataSelected.get(0).getValue()));
                        data.add(1, new SimpleStringProperty(dataSelected.get(1).getValue()));
                        data.add(2, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", pr)));
                        data.add(3, new SimpleStringProperty(String.valueOf(1)));
                        data.add(4, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", pr)));

                        priceList.add(pr);
                        dataTable.add(data);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tablePurchases.setItems(dataTable);
            sumTotalTablePorches();
        }
    }

    private int exist(List<StringProperty> dataSelected){
        AtomicInteger ex = new AtomicInteger(-1);
            for (int i = 0 ; i < dataTable.size() ; i++) {
                if (dataTable.get(i).get(0).getValue().equals(dataSelected.get(0).getValue()) ){
                    ex.set(i);
                    break;
                }
            }
        return ex.get();
    }
    @FXML
    private void tablePurchasesClick(MouseEvent mouseEvent) {
        if ( mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY) ){

            ActionModifiedQte();
        }
    }
    @FXML
    private void ActionModifiedQte(){
        int compoSelectedIndex = tablePurchases.getSelectionModel().getSelectedIndex();
        if (compoSelectedIndex != -1){
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("الكمية");
            dialog.initOwner(this.tfRecherche.getScene().getWindow());
            dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            dialog.setHeaderText("تعديل الكمية ");
            dialog.setContentText("الكمية :");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(qte -> {
                if (!qte.isEmpty()) {
                    int q = Integer.parseInt(qte);
                    double pr = priceList.get(compoSelectedIndex);
                    dataTable.get(compoSelectedIndex).get(3).setValue(qte);
                    dataTable.get(compoSelectedIndex).get(4).setValue(String.format(Locale.FRANCE, "%,.2f", (pr * q) ));
                    tablePurchases.setItems(dataTable);
                    sumTotalTablePorches();
                }
            });
        }
    }
    @FXML
    private void ActionModifiedPrice(){
        int compoSelectedIndex = tablePurchases.getSelectionModel().getSelectedIndex();
        if (compoSelectedIndex != -1){
            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("السعر");
            dialog.initOwner(this.tfRecherche.getScene().getWindow());
            dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            dialog.setHeaderText("تعديل السعر ");
            dialog.setContentText("السعر :");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(price -> {
                if (!price.isEmpty()) {
                    double pr = Double.parseDouble(price);
                    int q = Integer.parseInt(dataTable.get(compoSelectedIndex).get(3).getValue());
                    priceList.set(compoSelectedIndex, pr);
                    dataTable.get(compoSelectedIndex).get(2).setValue(String.format(Locale.FRANCE, "%,.2f", pr ));
                    dataTable.get(compoSelectedIndex).get(4).setValue(String.format(Locale.FRANCE, "%,.2f", (pr * q) ));
                    tablePurchases.setItems(dataTable);
                    sumTotalTablePorches();
                }
            });
        }
    }
    @FXML
    private void ActionDeleteFromComposition(){
        int compoSelectedIndex = tablePurchases.getSelectionModel().getSelectedIndex();
        if (compoSelectedIndex != -1){
            dataTable.remove(compoSelectedIndex);
            priceList.remove(compoSelectedIndex);
            tablePurchases.setItems(dataTable);

            sumTotalTablePorches();
        }
    }
    private void sumTotalTablePorches(){
        double totalPrice = 0.0;
        int totalling = 0;

        for (int i = 0; i < dataTable.size() ; i++) {
            int qte = Integer.parseInt(dataTable.get(i).get(3).getValue());
            totalling += qte;
            double price = priceList.get(i);
            totalPrice += (price * qte);
        }
        this.totalFacture = totalPrice;
        lbSumTotal.setText(String.format(Locale.FRANCE, "%,.2f", totalPrice));
        lbSumWeight.setText(String.valueOf(totalling));
    }
    @FXML
    private void ActionAnnulledAdd(){
        closeDialog(btnInsert);
    }

    @FXML
    void ActionInsert(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("الدفع ");
        dialog.initOwner(this.tfRecherche.getScene().getWindow());
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.setHeaderText(" السعر المدفوع ");
        dialog.setContentText("السعر :");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(price -> {
            if (!price.isEmpty()){
                LocalDate date = dpDate.getValue();
                String lbFactTxt = lbFactureNbr.getText().trim();
                int nbr = Integer.parseInt(lbFactTxt.substring(0,lbFactTxt.indexOf('/')));
                double paying = Double.parseDouble(price);

                if (date != null && selectedProvider != 0) {
                    if (paying <= totalFacture) {
                        Receipt receipt = new Receipt();
                        receipt.setNumber(nbr);
                        receipt.setIdProvider(selectedProvider);
                        receipt.setDate(date);
                        receipt.setPaying(paying);

                        int ins = insert(receipt);
                        if (ins != -1) {
                            insertComponent(ins);
                            ActionAnnulledAdd();
                        } else {
                            Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                            alertWarning.setHeaderText("تحذير ");
                            alertWarning.setContentText("خطأ غير معروف");
                            alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
                            alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                            Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                            okButton.setText("موافق");
                            alertWarning.showAndWait();
                        }
                    }else {
                        Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                        alertWarning.setHeaderText("تحذير ");
                        alertWarning.setContentText("السعر المدفوع اكبر من سعر الفاتورة");
                        alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
                        alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                        Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                        okButton.setText("موافق");
                        alertWarning.showAndWait();
                    }
                }else {
                    Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                    alertWarning.setHeaderText("تحذير ");
                    alertWarning.setContentText("الرجاء ملأ جميع الحقول");
                    alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
                    alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("موافق");
                    alertWarning.showAndWait();
                }
            }else {
                Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                alertWarning.setHeaderText("تحذير ");
                alertWarning.setContentText("الرجاء ملأ السعر المدفوع");
                alertWarning.initOwner(this.tfRecherche.getScene().getWindow());
                alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("موافق");
                alertWarning.showAndWait();
            }
        });
    }

    private void insertComponent(int idReceipt) {

        for (int i = 0; i < dataTable.size(); i++) {

            List<StringProperty> stringProperties = dataTable.get(i);

            int id = Integer.parseInt(stringProperties.get(0).getValue());
            int qte = Integer.parseInt(stringProperties.get(3).getValue());

            ComponentReceipt componentReceipt = new ComponentReceipt();
            componentReceipt.setIdReceipt(idReceipt);
            componentReceipt.setIdComponent(id);
            componentReceipt.setQte(qte);
            componentReceipt.setPrice(this.priceList.get(i));

            insertComponentMedication(componentReceipt);
        }
    }

    private int insert(Receipt receipt) {
        int insert = 0;
        try {
            insert = operation.insertId(receipt);
            return insert;
        }catch (Exception e){
            e.printStackTrace();
            return insert;
        }
    }

    private boolean insertComponentMedication(ComponentReceipt componentReceipt){
        boolean insert = false;
        try {
            insert = componentReceiptRawMaterialOperation.insert(componentReceipt);
            return insert;
        }catch (Exception e){
            e.printStackTrace();
            return insert;
        }
    }

    private boolean insertComponentRawMaterial(ComponentProduction componentProduction){
        boolean insert = false;
        try {
            insert = componentMaterialOperation.insert(componentProduction);
            return insert;
        }catch (Exception e){
            e.printStackTrace();
            return insert;
        }
    }


    private void closeDialog(Button btn) {
        ((Stage)btn.getScene().getWindow()).close();
    }

    @FXML
    void ActionSearchRawMadTable() {
        // filtrer les données
        ObservableList<List<StringProperty>> items = tableRawMaterial.getItems();
        FilteredList<List<StringProperty>> filteredData = new FilteredList<>(items, e -> true);
        String txtRecherche = tfRechercheMaterial.getText().trim();

        filteredData.setPredicate((Predicate<? super List<StringProperty>>) stringProperties -> {
            if (txtRecherche.isEmpty()) {
                //loadDataInTable();
                return true;
            } else if (stringProperties.get(1).toString().contains(txtRecherche)) {
                return true;
            } else if (stringProperties.get(2).toString().contains(txtRecherche)) {
                return true;
            }  else return stringProperties.get(3).toString().contains(txtRecherche);
        });

        SortedList<List<StringProperty>> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(tableRawMaterial.comparatorProperty());
        tableRawMaterial.setItems(sortedList);
    }

    @FXML
    private void ActionRefresh(){
        clearRecherche();
        tablePurchases.setItems(dataTable);
    }

    private void clearRecherche(){
        tfRecherche.clear();
    }

    @FXML
    void ActionSearch() {
        // filtrer les données
        ObservableList<List<StringProperty>> items = tablePurchases.getItems();
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
            }  else return stringProperties.get(4).toString().contains(txtRecherche);
        });

        SortedList<List<StringProperty>> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(tablePurchases.comparatorProperty());
        tablePurchases.setItems(sortedList);
    }
}
