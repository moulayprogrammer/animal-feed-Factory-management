package BddPackage;

import Models.Client;
import Models.Medication;
import Models.RawMaterial;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientOperation extends BDD<Client> {

    @Override
    public boolean insert(Client o) {
        connectDatabase();
        boolean ins = false;
        String query = "INSERT INTO زبون (المرجع, الاسم, العنوان, النشاط, الرقم_الوطني) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);

            preparedStmt.setString(1,o.getReference());
            preparedStmt.setString(2,o.getName());
            preparedStmt.setString(3,o.getAddress());
            preparedStmt.setString(4,o.getActivity());
            preparedStmt.setString(5,o.getNationalNumber());
            int insert = preparedStmt.executeUpdate();
            if(insert != -1) ins = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return ins;
    }

    @Override //
    public boolean update(Client o1, Client o2) {
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE زبون SET المرجع = ?, الاسم = ?, العنوان = ?, النشاط = ?, الرقم_الوطني = ? WHERE المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1,o1.getReference());
            preparedStmt.setString(2,o1.getName());
            preparedStmt.setString(3,o1.getAddress());
            preparedStmt.setString(4,o1.getActivity());
            preparedStmt.setString(5,o1.getNationalNumber());
            preparedStmt.setInt(6,o2.getId());
            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    @Override
    public boolean delete(Client o) {
        return false;
    }

    @Override
    public boolean isExist(Client o) {
        return false;
    }

    @Override
    public ArrayList<Client> getAll() {
        connectDatabase();
        ArrayList<Client> list = new ArrayList<>();
        String query = "SELECT * FROM  زبون WHERE ارشيف = 0;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                Client client = new Client();
                client.setId(resultSet.getInt("المعرف"));
                client.setReference(resultSet.getString("المرجع"));
                client.setName(resultSet.getString("الاسم"));
                client.setAddress(resultSet.getString("العنوان"));
                client.setActivity(resultSet.getString("النشاط"));
                client.setNationalNumber(resultSet.getString("الرقم_الوطني"));

                list.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }

    public Client get(int idClient) {
        connectDatabase();
        Client client = new Client();
        String query = "SELECT * FROM  زبون WHERE ارشيف = 0 AND المعرف = ?;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,idClient);
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()){

                client.setId(resultSet.getInt("المعرف"));
                client.setReference(resultSet.getString("المرجع"));
                client.setName(resultSet.getString("الاسم"));
                client.setAddress(resultSet.getString("العنوان"));
                client.setActivity(resultSet.getString("النشاط"));
                client.setNationalNumber(resultSet.getString("الرقم_الوطني"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return client;
    }

    public boolean AddToArchive(Client client){
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE زبون SET ارشيف = 1 WHERE المعرف = ?; ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,client.getId());
            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    public boolean DeleteFromArchive(Client client){
        connectDatabase();
        boolean upd = false;
        String query = "UPDATE زبون SET ارشيف = 0 WHERE المعرف = ?; ";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1,client.getId());
            int update = preparedStmt.executeUpdate();
            if(update != -1) upd = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return upd;
    }

    public ArrayList<Client> getAllArchive() {
        connectDatabase();
        ArrayList<Client> list = new ArrayList<>();
        String query = "SELECT * FROM  زبون WHERE ارشيف = 1;";
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){

                Client client = new Client();
                client.setId(resultSet.getInt("المعرف"));
                client.setReference(resultSet.getString("المرجع"));
                client.setName(resultSet.getString("الاسم"));
                client.setAddress(resultSet.getString("العنوان"));
                client.setActivity(resultSet.getString("النشاط"));
                client.setNationalNumber(resultSet.getString("الرقم_الوطني"));


                list.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
        return list;
    }
}
