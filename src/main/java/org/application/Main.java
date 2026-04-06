package org.application;
import java.sql.*;

public class Main {

//    private static final String URL = "jdbc:mysql://localhost/session_task_bd?user=root&password=";
    private static final String URL = "jdbc:mysql://localhost/session_task_bd";
    private static final String NAME = "root";
    private static final String PASS = "";
    private static String conok="Соединение с бд установлено";
    private static String conerr="Произошла ошибка подключения к бд";

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(URL, NAME, PASS)){
            System.out.printf("%s%n",conok);


            DaoJdbc jdbc= new DaoJdbc(connection);
            jdbc.tasks();

        } catch (SQLException e) {
            System.out.printf("%s%n",conerr);
            e.printStackTrace();
        }
    }
}