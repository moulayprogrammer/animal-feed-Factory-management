package Controllers.InvoiceController;

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
    ComboBox<String> cbClient;
    @FXML
    TextField tfRechercheProduct,tfRecherche;
    @FXML
    TableView<List<StringProperty>> tableProduct, tableSales;

    @FXML
    TableColumn<List<StringProperty>,String> tcIdProd, tcNameProd, tcQteProd, tcReferenceProd;
    @FXML
    TableColumn<List<StringProperty>,String> tcId,tcName,tcPriceU,tcQte,tcPriceTotal;
    @FXML
    Button btnInsert;

    private final InvoiceOperation operation = new InvoiceOperation();
    private final ProductOperation productOperation = new ProductOperation();
    private final ClientOperation clientOperation = new ClientOperation();
    private final ComponentInvoiceOperation componentInvoiceOperation = new ComponentInvoiceOperation();
    private final ObservableList<List<StringProperty>> dataTable = FXCollections.observableArrayList();
    private final List<Double> priceList = new ArrayList<>();
    private final ObservableList<String> comboClientData = FXCollections.observableArrayList();
    private final List<Integer> idClientCombo = new ArrayList<>();
    private int selectedClient = 0;
    private double totalFacture = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tcIdProd.setCellValueFactory(data -> data.getValue().get(0));
        tcNameProd.setCellValueFactory(data -> data.getValue().get(1));
        tcQteProd.setCellValueFactory(data -> data.getValue().get(2));
        tcReferenceProd.setCellValueFactory(data -> data.getValue().get(3));

        tcId.setCellValueFactory(data -> data.getValue().get(0));
        tcName.setCellValueFactory(data -> data.getValue().get(1));
        tcPriceU.setCellValueFactory(data -> data.getValue().get(2));
        tcQte.setCellValueFactory(data -> data.getValue().get(3));
        tcPriceTotal.setCellValueFactory(data -> data.getValue().get(4));

        refreshProduct();
        refreshComboClient();
        setFactureNumber();

        // set Date
        dpDate.setValue(LocalDate.now());

        //set Combo Search
        cbClient.setEditable(true);
        cbClient.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextFields.bindAutoCompletion(cbClient.getEditor(), cbClient.getItems());

        tfRechercheProduct.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.isEmpty()) {
                ObservableList<List<StringProperty>> items = tableProduct.getItems();
                FilteredList<List<StringProperty>> filteredData = new FilteredList<>(items, e -> true);

                filteredData.setPredicate((Predicate<? super List<StringProperty>>) stringProperties -> {

                    if (stringProperties.get(1).toString().contains(newValue)) {
                        return true;
                    } else return stringProperties.get(2).toString().contains(newValue);
                });

                SortedList<List<StringProperty>> sortedList = new SortedList<>(filteredData);
                sortedList.comparatorProperty().bind(tableProduct.comparatorProperty());
                tableProduct.setItems(sortedList);
            }else {
                refreshProduct();
            }
        });

    }

    @FXML
    private void ActionComboProvider(){
        int index = cbClient.getSelectionModel().getSelectedIndex();
        if (index >= 0 ) {
            selectedClient = idClientCombo.get(index);
            setClientTransaction(selectedClient);
        }
    }

    private void setClientTransaction(int idClient) {
        try {
            double debt;
            AtomicReference<Double> trans = new AtomicReference<>(0.0);
            AtomicReference<Double> pay = new AtomicReference<>(0.0);
            // Medication
            ArrayList<Invoice> invoices = operation.getAllByClient(idClient);
            invoices.forEach(invoice -> {
                ArrayList<ComponentInvoice> componentInvoices = componentInvoiceOperation.getAllByInvoice(invoice.getId());
                AtomicReference<Double> sumR = new AtomicReference<>(0.0);
                componentInvoices.forEach(componentInvoice -> {
                    double pr = componentInvoice.getPrice() * componentInvoice.getQte();
                    sumR.updateAndGet(v -> v + pr);
                });
                pay.updateAndGet(v -> v + invoice.getPaying());
                trans.updateAndGet(v -> v + sumR.get());
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

    private void refreshComboClient() {
        clearCombo();
        try {
            ArrayList<Client> clients = clientOperation.getAll();
            clients.forEach(client -> {
                comboClientData.add(client.getName());
                idClientCombo.add(client.getId());
            });
            cbClient.setItems(comboClientData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clearCombo(){
        cbClient.getSelectionModel().clearSelection();
        comboClientData.clear();
        idClientCombo.clear();
        lbDebt.setText("");
        lbTransaction.setText("");
    }

    private void refreshProduct(){
        ObservableList<List<StringProperty>> componentDataTable = FXCollections.observableArrayList();

        try {
            ArrayList<Product> products = productOperation.getAllWithQteNotNull();

            products.forEach(product -> {
                List<StringProperty> data = new ArrayList<>();
                data.add( new SimpleStringProperty(String.valueOf(product.getId())));
                data.add( new SimpleStringProperty(product.getName()));
                data.add( new SimpleStringProperty(String.valueOf(product.getQte())));
                data.add(new SimpleStringProperty(product.getReference()));
                componentDataTable.add(data);
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        tableProduct.setItems(componentDataTable);

    }

    @FXML
    private void ActionAddClient(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ClientViews/AddView.fxml"));
            DialogPane temp = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(temp);
            dialog.resizableProperty().setValue(false);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            closeButton.setVisible(false);
            dialog.showAndWait();

            refreshComboClient();
            cbClient.getSelectionModel().select(comboClientData.size() - 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ActionPayDebtClient(){
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("??????????????");
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.setHeaderText("???????? ?????????? ???????????? ");
        dialog.setContentText("?????????? :");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(price -> {

            if (!price.isEmpty() && !lbDebt.getText().equals("0.0")) {
                double pr = Double.parseDouble(price);

                ArrayList<Invoice> invoices = operation.getAllByClient(selectedClient);
                for (Invoice invoice : invoices) {
                    ArrayList<ComponentInvoice> componentInvoices = componentInvoiceOperation.getAllByInvoice(invoice.getId());
                    AtomicReference<Double> sumR = new AtomicReference<>(0.0);
                    componentInvoices.forEach(componentInvoice -> {
                        double pre = componentInvoice.getPrice() * componentInvoice.getQte();
                        sumR.updateAndGet(v -> v + pre);
                    });
                    double debt = sumR.get() - invoice.getPaying();
                    if (debt > 0) {
                        if (pr <= debt) {
                            double newPaying = pr + invoice.getPaying();
                            pr = 0;
                            PayDebtInvoice(invoice.getId(), newPaying);
                            break;
                        } else {
                            double newPaying = debt + invoice.getPaying();
                            pr = pr - debt;
                            PayDebtInvoice(invoice.getId(), newPaying);
                        }
                    }
                }
                if (pr > 0 ){
                    Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                    alertInformation.setHeaderText("???????????? ?????????????? ");
                    alertInformation.setContentText("???????????? ?????????????? ???? ?????????????? ????   " + String.format(Locale.FRANCE, "%,.2f", (pr)));
                    alertInformation.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Button okButton = (Button) alertInformation.getDialogPane().lookupButton(ButtonType.OK);
                    okButton.setText("??????????");
                    alertInformation.showAndWait();
                }
                setClientTransaction(selectedClient);
            }
        });
    }

    private void PayDebtInvoice(int invoiceId , double price) {
        try {
            Invoice invoice1 = new Invoice();
            invoice1.setPaying(price);

            Invoice invoice2 =  new Invoice();
            invoice2.setId(invoiceId);

            operation.updatePaying(invoice1,invoice2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void tableProductClick(MouseEvent mouseEvent) {
        if ( mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY) ){

            ActionAddSales();
        }
    }
    @FXML
    private void ActionAddSales(){
        List<StringProperty> dataSelected = tableProduct.getSelectionModel().getSelectedItem();
        if (dataSelected != null) {
            int ex = exist(dataSelected);
            if ( ex == -1 ){
               try {
                   Product product = productOperation.get(Integer.parseInt(tableProduct.getSelectionModel().getSelectedItem().get(0).getValue()));
                   ComponentInvoice componentInvoice = new ComponentInvoice();

                   FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/InvoiceViews/AddSaleView.fxml"));
                   DialogPane temp = loader.load();
                   AddSaleController controller = loader.getController();
                   controller.Init(product,componentInvoice);
                   Dialog<ButtonType> dialog = new Dialog<>();
                   dialog.setDialogPane(temp);
                   dialog.resizableProperty().setValue(false);
                   dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                   Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                   closeButton.setVisible(false);
                   dialog.showAndWait();

                   if (componentInvoice.getPrice() != 0) {

                       List<StringProperty> data = new ArrayList<>();
                       data.add(0, new SimpleStringProperty(dataSelected.get(0).getValue()));
                       data.add(1, new SimpleStringProperty(dataSelected.get(1).getValue()));
                       data.add(2, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", componentInvoice.getPrice())));
                       data.add(3, new SimpleStringProperty(String.valueOf(componentInvoice.getQte())));
                       data.add(4, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", componentInvoice.getPrice() * componentInvoice.getQte())));

                       priceList.add(componentInvoice.getPrice());
                       dataTable.add(data);
                   }

               }catch (Exception e){
                   e.printStackTrace();
               }
            }
            tableSales.setItems(dataTable);
            sumTotalTableSales();
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
    private void tableSalesClick(MouseEvent mouseEvent) {
        if ( mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY) ){

            ActionUpdateSales();
        }
    }

    @FXML
    private void ActionUpdateSales(){
        int compoSelectedIndex = tableSales.getSelectionModel().getSelectedIndex();
        if (compoSelectedIndex != -1){
            try {
                Product product = productOperation.get(Integer.parseInt(tableSales.getSelectionModel().getSelectedItem().get(0).getValue()));
                ComponentInvoice componentInvoice = new ComponentInvoice();
                componentInvoice.setQte(Integer.parseInt(tableSales.getSelectionModel().getSelectedItem().get(3).getValue()));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/InvoiceViews/UpdateSaleView.fxml"));
                DialogPane temp = loader.load();
                UpdateSaleController controller = loader.getController();
                controller.Init(product,componentInvoice);
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(temp);
                dialog.resizableProperty().setValue(false);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                closeButton.setVisible(false);
                dialog.showAndWait();

                if (componentInvoice.getPrice() != 0) {

                    List<StringProperty> data = new ArrayList<>();
                    data.add(0, new SimpleStringProperty(String.valueOf(product.getId())));
                    data.add(1, new SimpleStringProperty(product.getName()));
                    data.add(2, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", componentInvoice.getPrice())));
                    data.add(3, new SimpleStringProperty(String.valueOf(componentInvoice.getQte())));
                    data.add(4, new SimpleStringProperty(String.format(Locale.FRANCE, "%,.2f", componentInvoice.getPrice() * componentInvoice.getQte())));

                    priceList.set(compoSelectedIndex,componentInvoice.getPrice());
                    dataTable.set(compoSelectedIndex,data);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            tableSales.setItems(dataTable);
            sumTotalTableSales();
        }
    }

    @FXML
    private void ActionDeleteSales(){
        int compoSelectedIndex = tableSales.getSelectionModel().getSelectedIndex();
        if (compoSelectedIndex != -1){
            dataTable.remove(compoSelectedIndex);
            priceList.remove(compoSelectedIndex);
            tableSales.setItems(dataTable);

            sumTotalTableSales();
        }
    }
    private void sumTotalTableSales(){
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

        dialog.setTitle("?????????? ");
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.setHeaderText(" ?????????? ?????????????? ");
        dialog.setContentText("?????????? :");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(price -> {
            if (!price.isEmpty()){
                LocalDate date = dpDate.getValue();
                String lbFactTxt = lbFactureNbr.getText().trim();
                int nbr = Integer.parseInt(lbFactTxt.substring(0,lbFactTxt.indexOf('/')));
                double paying = Double.parseDouble(price);

                if (date != null && selectedClient != 0) {
                    if (paying <= totalFacture) {

                        Invoice invoice = new Invoice();
                        invoice.setNumber(nbr);
                        System.out.println("id = " + selectedClient);
                        invoice.setIdClient(selectedClient);
                        invoice.setDate(date);
                        invoice.setPaying(paying);


                        int ins = insert(invoice);
                        if (ins != -1) {
                            insertComponent(ins);
                            ActionAnnulledAdd();
                        } else {
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
                        alertWarning.setContentText("?????????? ?????????????? ???????? ???? ?????? ????????????????");
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
            }else {
                Alert alertWarning = new Alert(Alert.AlertType.WARNING);
                alertWarning.setHeaderText("?????????? ");
                alertWarning.setContentText("???????????? ?????? ?????????? ??????????????");
                alertWarning.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                Button okButton = (Button) alertWarning.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("??????????");
                alertWarning.showAndWait();
            }
        });
    }

    private void insertComponent(int idInvoice) {

        for (int i = 0; i < dataTable.size(); i++) {

            List<StringProperty> stringProperties = dataTable.get(i);

            int id = Integer.parseInt(stringProperties.get(0).getValue());
            int qte = Integer.parseInt(stringProperties.get(3).getValue());

            ComponentInvoice  componentInvoice = new ComponentInvoice();
            componentInvoice.setIdInvoice(idInvoice);
            componentInvoice.setIdComponent(id);
            componentInvoice.setQte(qte);
            componentInvoice.setPrice(this.priceList.get(i));

            System.out.println("price 01  = " + componentInvoice.getPrice());

            insertComponentInvoice(componentInvoice);
        }
    }

    private int insert(Invoice receipt) {
        int insert = 0;
        try {
            insert = operation.insertId(receipt);
            return insert;
        }catch (Exception e){
            e.printStackTrace();
            return insert;
        }
    }

    private boolean insertComponentInvoice(ComponentInvoice componentInvoice){
        boolean insert = false;
        try {
            insert = componentInvoiceOperation.insert(componentInvoice);
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
        // filtrer les donn??es
        ObservableList<List<StringProperty>> items = tableProduct.getItems();
        FilteredList<List<StringProperty>> filteredData = new FilteredList<>(items, e -> true);
        String txtRecherche = tfRechercheProduct.getText().trim();

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
        sortedList.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedList);
    }

    @FXML
    private void ActionRefresh(){
        clearRecherche();
        tableSales.setItems(dataTable);
    }

    private void clearRecherche(){
        tfRecherche.clear();
    }

    @FXML
    void ActionSearch() {
        // filtrer les donn??es
        ObservableList<List<StringProperty>> items = tableSales.getItems();
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
        sortedList.comparatorProperty().bind(tableSales.comparatorProperty());
        tableSales.setItems(sortedList);
    }


}
