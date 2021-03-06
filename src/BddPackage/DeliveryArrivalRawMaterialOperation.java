package BddPackage;

import Models.ComponentStore;
import Models.DeliveryArrival;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DeliveryArrivalRawMaterialOperation extends BDD<DeliveryArrival>{

    private final ComponentStoreRawMaterialOperation componentStoreRawMaterialOperation = new ComponentStoreRawMaterialOperation();

    @Override
    public boolean insert(DeliveryArrival o) {
        return false;
    }

    public int insertId(DeliveryArrival deliveryArrival) {
        connectDatabase();
        int ins = 0;
        String query = "INSERT INTO وصل_توصيل_مواد_خام ( معرف_الفاتورة , معرف_الموصل , التاريخ , السعر ) VALUES (?,?,?,?);";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,deliveryArrival.getIdReceipt());
            preparedStmt.setInt(2,deliveryArrival.getIdDelivery());
            preparedStmt.setDate(3,Date.valueOf(deliveryArrival.getDate()));
            preparedStmt.setDouble(4,deliveryArrival.getPrice());
            int insert = preparedStmt.executeUpdate();
            if(insert != -1) {
                ins = preparedStmt.getGeneratedKeys().getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return ins;
    }

    @Override
    public boolean update(DeliveryArrival o1, DeliveryArrival o2) {
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE وصل_توصيل_مواد_خام SET معرف_الموصل = ?, التاريخ = ?, السعر = ? WHERE المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,o1.getIdDelivery());
            preparedStmt.setDate(2,Date.valueOf(o1.getDate()));
            preparedStmt.setDouble(3,o1.getPrice());
            preparedStmt.setInt(4,o2.getId());
            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    @Override
    public boolean delete(DeliveryArrival o) {
        connectDatabase();
        boolean del = false;
        String query = "DELETE FROM وصل_توصيل_مواد_خام WHERE المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,o.getId());

            int delete = preparedStmt.executeUpdate();
            if(delete != -1) del = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return del;
    }

    @Override
    public boolean isExist(DeliveryArrival o) {
        return false;
    }

    @Override
    public ArrayList<DeliveryArrival> getAll() {
        connectDatabase();
        ArrayList<DeliveryArrival> list = new ArrayList<>();
        String query = "SELECT * FROM وصل_توصيل_مواد_خام WHERE ارشيف = 0";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                DeliveryArrival deliveryArrival = new DeliveryArrival();
                deliveryArrival.setId(resultSet.getInt("المعرف"));
                deliveryArrival.setIdDelivery(resultSet.getInt("معرف_الموصل"));
                deliveryArrival.setIdReceipt(resultSet.getInt("معرف_الفاتورة"));
                deliveryArrival.setDate(resultSet.getDate("التاريخ").toLocalDate());
                deliveryArrival.setPrice(resultSet.getDouble("السعر"));

                list.add(deliveryArrival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }

    public ArrayList<DeliveryArrival> getAllByReceipt(int idReceipt) {
        connectDatabase();
        ArrayList<DeliveryArrival> list = new ArrayList<>();
        String query = "SELECT * FROM وصل_توصيل_مواد_خام WHERE ارشيف = 0 AND معرف_الفاتورة = ? ;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,idReceipt);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                DeliveryArrival deliveryArrival = new DeliveryArrival();
                deliveryArrival.setId(resultSet.getInt("المعرف"));
                deliveryArrival.setIdDelivery(resultSet.getInt("معرف_الموصل"));
                deliveryArrival.setIdReceipt(resultSet.getInt("معرف_الفاتورة"));
                deliveryArrival.setDate(resultSet.getDate("التاريخ").toLocalDate());
                deliveryArrival.setPrice(resultSet.getDouble("السعر"));

                list.add(deliveryArrival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }

    public DeliveryArrival get(int id) {
        connectDatabase();
        ArrayList<DeliveryArrival> list = new ArrayList<>();
        String query = "SELECT * FROM وصل_توصيل_مواد_خام WHERE ارشيف = 0 AND المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,id);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                DeliveryArrival deliveryArrival = new DeliveryArrival();
                deliveryArrival.setId(resultSet.getInt("المعرف"));
                deliveryArrival.setIdDelivery(resultSet.getInt("معرف_الموصل"));
                deliveryArrival.setIdReceipt(resultSet.getInt("معرف_الفاتورة"));
                deliveryArrival.setDate(resultSet.getDate("التاريخ").toLocalDate());
                deliveryArrival.setPrice(resultSet.getDouble("السعر"));

                list.add(deliveryArrival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list.get(0);
    }

    public ArrayList<DeliveryArrival> getAllArchive() {
        connectDatabase();
        ArrayList<DeliveryArrival> list = new ArrayList<>();
        String query = "SELECT * FROM وصل_توصيل_مواد_خام WHERE ارشيف = 1";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                DeliveryArrival deliveryArrival = new DeliveryArrival();
                deliveryArrival.setId(resultSet.getInt("المعرف"));
                deliveryArrival.setIdDelivery(resultSet.getInt("معرف_الموصل"));
                deliveryArrival.setIdReceipt(resultSet.getInt("معرف_الفاتورة"));
                deliveryArrival.setDate(resultSet.getDate("التاريخ").toLocalDate());
                deliveryArrival.setPrice(resultSet.getDouble("السعر"));

                list.add(deliveryArrival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }

    public DeliveryArrival getArchive(int id) {
        connectDatabase();
        ArrayList<DeliveryArrival> list = new ArrayList<>();
        String query = "SELECT * FROM وصل_توصيل_مواد_خام WHERE ارشيف = 1 AND المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,id);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                DeliveryArrival deliveryArrival = new DeliveryArrival();
                deliveryArrival.setId(resultSet.getInt("المعرف"));
                deliveryArrival.setIdDelivery(resultSet.getInt("معرف_الموصل"));
                deliveryArrival.setIdReceipt(resultSet.getInt("معرف_الفاتورة"));
                deliveryArrival.setDate(resultSet.getDate("التاريخ").toLocalDate());
                deliveryArrival.setPrice(resultSet.getDouble("السعر"));

                list.add(deliveryArrival);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list.get(0);
    }

    public boolean StoreDeliveryArrival(DeliveryArrival deliveryArrival){
        connectDatabase();
        boolean upd = false;
        String query = "SELECT * FROM توصيل_مواد_خام, وصل_توصيل_مواد_خام, مشتريات_مواد_خام WHERE وصل_توصيل_مواد_خام.المعرف = ? AND  معرف_الوصل = المعرف \n" +
                "AND مشتريات_مواد_خام.معرف_المادة_الخام = توصيل_مواد_خام.معرف_المادة AND مشتريات_مواد_خام.معرف_الفاتورة = وصل_توصيل_مواد_خام.معرف_الفاتورة";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,deliveryArrival.getId());
            ResultSet resultSet = preparedStmt.executeQuery();
            ArrayList<ComponentStore> componentStores = new ArrayList<>();
            int totQte = 0;
            double priceDelivery = 0;
            while (resultSet.next()){
                priceDelivery = resultSet.getDouble("السعر");
                ComponentStore componentStore = new ComponentStore();//
                componentStore.setIdComponent(resultSet.getInt("معرف_المادة_الخام"));
                componentStore.setIdDeliveryArrival(resultSet.getInt("معرف_الوصل"));
                componentStore.setPrice(resultSet.getDouble("سعر_الوحدة"));
                int qte = resultSet.getInt("الكمية_الموصلة");
                totQte += qte;
                componentStore.setQteStored(qte);
                componentStore.setQteConsumed(0);
                componentStores.add(componentStore);
            }
            double priceUnitDelivery = priceDelivery / totQte ;
            for (int i = 0; i < componentStores.size(); i++) {
                ComponentStore componentStore = componentStores.get(i);
                componentStore.setPrice(componentStore.getPrice() + priceUnitDelivery);

                upd = componentStoreRawMaterialOperation.insert(componentStore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    public boolean DeleteFromArchive(DeliveryArrival deliveryArrival){
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE وصل_توصيل_مواد_خام SET ارشيف = 0 WHERE المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,deliveryArrival.getId());
            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

}
