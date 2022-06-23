package BddPackage;

import Models.ComponentStore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ComponentStoreRawMaterialOperation extends BDD<ComponentStore> {

    @Override
    public boolean insert(ComponentStore o) {
        connectDatabase();
        boolean ins = false;
        String query = "INSERT INTO تخزين_المواد_الخام ( معرف_المادة_الخام, معرف_وصل_التوصيل , سعر_الوحدة, كمية_مخزنة, كمية_مستهلكة ) VALUES (?,?,?,?,?) ; ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,o.getIdComponent());
            preparedStmt.setInt(2,o.getIdDeliveryArrival());
            preparedStmt.setDouble(3,o.getPrice());
            preparedStmt.setInt(4,o.getQteStored());
            preparedStmt.setInt(5,o.getQteConsumed());
            int insert = preparedStmt.executeUpdate();
            if(insert != -1) ins = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return ins;
    }

    @Override
    public boolean update(ComponentStore o1, ComponentStore o2) {
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE تخزين_المواد_الخام SET   سعر_الوحدة = ? , كمية_مخزنة = ? , كمية_مستهلكة = ? WHERE معرف_المادة_الخام = ? AND معرف_وصل_التوصيل = ?";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setDouble(1,o1.getPrice());
            preparedStmt.setInt(2,o2.getQteStored());
            preparedStmt.setInt(3,o2.getQteConsumed());
            preparedStmt.setInt(4,o2.getIdComponent());
            preparedStmt.setInt(5,o2.getIdDeliveryArrival());

            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    @Override
    public boolean delete(ComponentStore o) {
        connectDatabase();
        boolean del = false;
        String query = "DELETE FROM تخزين_المواد_الخام WHERE معرف_وصل_التوصيل = ? AND معرف_المادة_الخام = ? ;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,o.getIdDeliveryArrival());
            preparedStmt.setInt(2,o.getIdComponent());

            int update = preparedStmt.executeUpdate();
            if(update != -1) del = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return del;
    }

    @Override
    public boolean isExist(ComponentStore o) {
        return false;
    }

    @Override
    public ArrayList<ComponentStore> getAll() {
        connectDatabase();
        ArrayList<ComponentStore> list = new ArrayList<>();
        String query = "SELECT * FROM تخزين_المواد_الخام ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                ComponentStore componentStore = new ComponentStore();
                componentStore.setIdComponent(resultSet.getInt("معرف_المادة_الخام"));
                componentStore.setIdDeliveryArrival(resultSet.getInt("معرف_وصل_التوصيل"));
                componentStore.setPrice(resultSet.getDouble("سعر_الوحدة"));
                componentStore.setQteStored(resultSet.getInt("كمية_مخزنة"));
                componentStore.setQteConsumed(resultSet.getInt("كمية_مستهلكة"));

                list.add(componentStore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }
}
