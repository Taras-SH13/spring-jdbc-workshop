package org.shop.db;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.shop.db.entity.OrderDetailEntity;
import org.shop.db.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
public class OrderRepositoryImpl implements OrdersRepository {

//    private static final String URL = "jdbc:mysql://localhost:3306/shopdb?useSSL=false";
//    private static final String USER = "root";
//    private static final String PASSWORD = "root";


    OrderDetailRepositoryImpl orderDetailRepository;
    Connection connection;

    public OrderRepositoryImpl(OrderDetailRepositoryImpl orderDetailRepository,Connection connection) {
        this.orderDetailRepository = orderDetailRepository;
        this.connection=connection;
    }

    @Override
    public List<OrderEntity> findAllOrdersInDB() {
     List<OrderEntity> orders = new ArrayList<>();
        String selectquery = "SELECT *FROM `order`";
//        try {
//            Driver driver = new FabricMySQLDriver();
//            DriverManager.registerDriver(driver);
//        } catch (SQLException e) {
//            System.out.println("Не удалось загрузить драйвер!!!");
//
//        }
        try (Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(selectquery);
            while (result.next()) {
                long id = result.getLong("id");
                List<OrderDetailEntity> orderDetailEntittes = orderDetailRepository.findAllDetailInCurrentOrder(id);
                String name = result.getString("name");
                String client = result.getString("client");
                orders.add(new OrderEntity(id, name, client, orderDetailEntittes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;


    }

    @Override
    public OrderEntity findOrderByID(long id) {
        OrderEntity order = null;
        String selectquery = "SELECT *FROM `order`WHERE id=" + id;
//        try {
//            Driver driver = new FabricMySQLDriver();
//            DriverManager.registerDriver(driver);
//        } catch (SQLException e) {
//            System.out.println("Не удалось загрузить драйвер!!!");
//
//        }
        try (Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(selectquery);
            while (result.next()) {
                String name = result.getString("name");
                String client = result.getString("client");
                order = new OrderEntity(id, name, client, new OrderDetailRepositoryImpl().findAllDetailInCurrentOrder(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;

    }

    @Override
    public void saveOrderInDB(OrderEntity orderEntity) {
        long id = orderEntity.getId();
        String name = orderEntity.getName();
        String client = orderEntity.getClient();
        String savequery = "INSERT INTO shopdb.`order`(id,name, client) VALUES " + "(" + id + "," + "'" + name + "'" + "," + "'" + client + "'" + ")";

//        try {
//            Driver driver = new FabricMySQLDriver();
//            DriverManager.registerDriver(driver);
//        } catch (SQLException e) {
//            System.out.println("Не удалось загрузить драйвер!!!");
//
//        }
        try (Statement statement = connection.createStatement()) {

            statement.execute(savequery);
            orderDetailRepository.saveOrderDetailInDB(orderEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrderFromDB(long orderId) {
        orderDetailRepository.deleteOllDetailFromDB(orderId);
//        try {
//            Driver driver = new FabricMySQLDriver();
//            DriverManager.registerDriver(driver);
//        } catch (SQLException e) {
//            System.out.println("Не удалось загрузить драйвер!!!");
//
//        }

        String deletequery = "DELETE FROM shopdb.`order`WHERE id=" + orderId;
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(deletequery);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}

