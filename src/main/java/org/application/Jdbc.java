package org.application;

import java.sql.*;
import java.util.Scanner;

public class Jdbc {
    private final Connection connection;
    private String sql;

    public Jdbc(Connection connection) {
        this.connection = connection;
    }

    public void printData(String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t | \t");
            }
            System.out.println("\n-------------------------------------------------");
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t | \t");
                }
                System.out.println();
            }
            System.out.println("-------------------------------------------------\n");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void tasks() {
        Scanner scanner = new Scanner(System.in);
        int key;
        do {

            System.out.println("Введите номер задания от 1 до 13, для выхода - 0");
            key = scanner.nextInt();
            switch (key) {
                case 1:
                    sql = "SELECT v.maker, m.model FROM Motorcycle m JOIN Vehicle v ON m.model = v.model WHERE m.horsepower > 150 AND m.price < 20000 AND m.type = 'Sport' ORDER BY m.horsepower DESC;";
                    printData(sql);
                    break;
                case 2:
                    sql = "SELECT v.maker, c.model, c.horsepower, c.engine_capacity, 'Car' AS vehicle_type FROM Car c JOIN Vehicle v ON c.model = v.model WHERE c.horsepower > 150 AND c.engine_capacity < 3.0 AND c.price < 35000 UNION ALL SELECT v.maker, m.model, m.horsepower, m.engine_capacity, 'Motorcycle' AS vehicle_type FROM Motorcycle m JOIN Vehicle v ON m.model = v.model WHERE m.horsepower > 150 AND m.engine_capacity < 1.5 AND m.price < 20000 UNION ALL SELECT v.maker, b.model, NULL AS horsepower, NULL AS engine_capacity, 'Bicycle' AS vehicle_type FROM Bicycle b JOIN Vehicle v ON b.model = v.model WHERE b.gear_count > 18 AND b.price < 4000 ORDER BY horsepower DESC;";
                    printData(sql);
                    break;
                case 3:
                    sql = "WITH car_stats AS (SELECT c.class, res.car, AVG(res.position) AS avg_pos, COUNT(*) AS races_count FROM Results res JOIN Cars r ON res.car = r.name JOIN Classes c ON r.class = c.class GROUP BY c.class, res.car), best_per_class AS (SELECT class, MIN(avg_pos) AS best_avg FROM car_stats GROUP BY class) SELECT cs.car AS car_name, cs.class, cs.avg_pos, cs.races_count FROM car_stats cs JOIN best_per_class b ON cs.class = b.class AND cs.avg_pos = b.best_avg ORDER BY cs.avg_pos;";
                    printData(sql);
                    break;
                case 4:
                    sql = "SELECT cs.car AS car_name, cs.class, cs.avg_pos, cs.races_count, cl.country FROM ( SELECT res.car, c.class, AVG(res.position) AS avg_pos, COUNT(*) AS races_count FROM Results res JOIN Cars r ON res.car = r.name JOIN Classes c ON r.class = c.class GROUP BY res.car, c.class) cs JOIN Classes cl ON cs.class = cl.class ORDER BY cs.avg_pos ASC, cs.car ASC LIMIT 1;";
                    printData(sql);
                    break;
                case 5:
                    sql = "WITH car_stats AS (SELECT c.class, r.name AS car_name, AVG(res.position) AS avg_pos, COUNT(*) AS races_count FROM Results res JOIN Cars r ON res.car = r.name JOIN Classes c ON r.class = c.class GROUP BY c.class, r.name), class_avg AS ( SELECT class, AVG(avg_pos) AS class_avg_pos FROM car_stats GROUP BY class), best_classes AS ( SELECT class FROM class_avg WHERE class_avg_pos = ( SELECT MIN(class_avg_pos) FROM class_avg )) SELECT cs.car_name, cs.class as car_class, cs.avg_pos as average_position, cs.races_count, cl.country, total.total_races_for_classes as total_races FROM car_stats cs JOIN Classes cl ON cs.class = cl.class JOIN ( SELECT c.class, SUM(cs2.races_count) AS total_races_for_classes FROM car_stats cs2 JOIN Classes c ON cs2.class = c.class WHERE cs2.class IN (SELECT class FROM best_classes) GROUP BY c.class) total ON cs.class = total.class WHERE cs.class IN (SELECT class FROM best_classes) ORDER BY cs.class, cs.car_name;";
                    printData(sql);
                    break;
                case 6:
                    sql = "WITH car_stats AS (SELECT r.name AS car_name, r.class, AVG(res.position) AS avg_pos, COUNT(*) AS races_count FROM Results res JOIN Cars r ON res.car = r.name GROUP BY r.name, r.class),class_stats AS ( SELECT class, AVG(avg_pos) AS class_avg_pos, COUNT(*) AS cars_in_class FROM car_stats GROUP BY class) SELECT cs.car_name, cs.class as car_class, cs.avg_pos as average_position, cs.races_count as race_count, cl.country as car_country FROM car_stats cs JOIN class_stats cls ON cs.class = cls.class JOIN Classes cl ON cs.class = cl.class WHERE cls.cars_in_class >= 2 AND cs.avg_pos < cls.class_avg_pos ORDER BY cs.class, cs.avg_pos;";
                    printData(sql);
                    break;
                case 7:
                    sql = "WITH car_stats AS (SELECT r.name AS car_name, r.class, AVG(res.position) AS average_position, COUNT(*) AS races_count FROM Results res JOIN Cars r ON res.car = r.name GROUP BY r.name, r.class),low_cars AS ( SELECT class, car_name, average_position, races_count FROM car_stats WHERE average_position > 3.0 ), class_low_counts AS ( SELECT lc.class, COUNT(*) AS low_position_count FROM low_cars lc GROUP BY lc.class), class_total_races AS ( SELECT cs.class, SUM(cs.races_count) AS total_races FROM car_stats cs GROUP BY cs.class) SELECT lc.car_name, lc.class as car_class, lc.average_position, lc.races_count, cl.country as car_coutnry, ctr.total_races, clc.low_position_count FROM low_cars lc JOIN Classes cl ON lc.class = cl.class JOIN class_low_counts clc ON lc.class = clc.class JOIN class_total_races ctr ON lc.class = ctr.class ORDER BY clc.low_position_count DESC, lc.class, lc.average_position;";
                    printData(sql);
                    break;
                case 8:
                    sql = "SELECT c.name, c.email, c.phone, COUNT(b.id_booking) AS total_bookings, GROUP_CONCAT(DISTINCT h.name ORDER BY h.name SEPARATOR ', ') AS hotels_list, ROUND(AVG(DATEDIFF(b.check_out_date, b.check_in_date)), 2) AS avg_stay_days FROM Customer c JOIN Booking b ON b.id_customer = c.id_customer JOIN Room r ON r.id_room = b.id_room JOIN Hotel h ON h.id_hotel = r.id_hotel GROUP BY  c.name, c.email, c.phone HAVING COUNT(DISTINCT r.id_hotel) > 1 AND COUNT(b.id_booking) > 2 ORDER BY total_bookings DESC;";
                    printData(sql);
                    break;
                case 9:
                    sql = "SELECT c.id_customer, c.name, totals.total_bookings, ROUND(totals.total_spent, 2) AS total_spent, totals.unique_hotels FROM Customer c JOIN ( SELECT b.id_customer, COUNT(*) AS total_bookings, COUNT(DISTINCT r.id_hotel) AS unique_hotels, SUM(r.price * GREATEST(1, DATEDIFF(b.check_out_date, b.check_in_date))) AS total_spent FROM Booking b JOIN Room r ON r.id_room = b.id_room WHERE b.check_out_date > b.check_in_date GROUP BY b.id_customer HAVING COUNT(*) > 2 AND COUNT(DISTINCT r.id_hotel) > 1 AND SUM(r.price * GREATEST(1, DATEDIFF(b.check_out_date, b.check_in_date))) > 500) AS totals ON totals.id_customer = c.id_customer ORDER BY totals.total_spent ASC;";
                    printData(sql);
                    break;
                case 10:
                    sql = "SELECT c.id_customer, c.name, CASE WHEN MAX(h_cat.cat = 'Дорогой') = 1 THEN 'Дорогой' WHEN MAX(h_cat.cat = 'Средний') = 1 THEN 'Средний' WHEN MAX(h_cat.cat = 'Дешевый') = 1 THEN 'Дешевый' ELSE NULL END AS preferred_hotel_type, GROUP_CONCAT(DISTINCT h.name ORDER BY h.name SEPARATOR ', ') AS visited_hotels FROM Customer c JOIN Booking b ON b.id_customer = c.id_customer JOIN Room r ON r.id_room = b.id_room JOIN Hotel h ON h.id_hotel = r.id_hotel JOIN (SELECT r.id_hotel, CASE WHEN AVG(r.price) < 175 THEN 'Дешевый' WHEN AVG(r.price) BETWEEN 175 AND 300 THEN 'Средний' ELSE 'Дорогой' END AS cat FROM Room r GROUP BY r.id_hotel) AS h_cat ON h_cat.id_hotel = h.id_hotel GROUP BY c.id_customer, c.name HAVING preferred_hotel_type IS NOT NULL ORDER BY FIELD(CASE WHEN MAX(h_cat.cat = 'Дешевый') = 1 AND MAX(h_cat.cat = 'Средний') = 0 AND MAX(h_cat.cat = 'Дорогой') = 0 THEN 'Дешевый' WHEN MAX(h_cat.cat = 'Средний') = 1 AND MAX(h_cat.cat = 'Дорогой') = 0 THEN 'Средний' WHEN MAX(h_cat.cat = 'Дорогой') = 1 THEN 'Дорогой' WHEN MAX(h_cat.cat = 'Средний') = 1 THEN 'Средний' WHEN MAX(h_cat.cat = 'Дешевый') = 1 THEN 'Дешевый' ELSE NULL END, 'Дешевый','Средний','Дорогой');";
                    printData(sql);
                    break;
                case 11:
                    sql = "WITH RECURSIVE subordinates AS (SELECT e.EmployeeID, e.Name, e.ManagerID, e.DepartmentID, e.RoleID FROM Employees e WHERE e.EmployeeID = 1 UNION ALL SELECT e2.EmployeeID, e2.Name, e2.ManagerID, e2.DepartmentID, e2.RoleID FROM Employees e2 JOIN subordinates s ON e2.ManagerID = s.EmployeeID) SELECT s.EmployeeID, s.Name AS EmployeeName, s.ManagerID, d.DepartmentName, r.RoleName, p.ProjectName, NULLIF(TRIM(BOTH ',' FROM GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ',')), '') AS Tasks FROM subordinates s LEFT JOIN Departments d ON s.DepartmentID = d.DepartmentID LEFT JOIN Roles r ON s.RoleID = r.RoleID LEFT JOIN Tasks t ON s.EmployeeID = t.AssignedTo LEFT JOIN Projects p ON t.ProjectID = p.ProjectID GROUP BY s.EmployeeID, s.Name, s.ManagerID, d.DepartmentName, r.RoleName ORDER BY s.Name;";
                    printData(sql);
                    break;
                case 12:
                    sql = "WITH RECURSIVE subordinates AS (SELECT e.EmployeeID, e.Name, e.ManagerID, e.DepartmentID, e.RoleID FROM Employees e WHERE e.EmployeeID = 1 UNION ALL SELECT c.EmployeeID, c.Name, c.ManagerID, c.DepartmentID, c.RoleID FROM Employees c JOIN subordinates s ON c.ManagerID = s.EmployeeID) SELECT s.EmployeeID, s.Name AS EmployeeName, s.ManagerID, d.DepartmentName, r.RoleName, NULLIF(GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ', '), '') AS Tasks, COUNT(t.TaskID) AS TasksCount, COALESCE(direct.SubCount, 0) AS DirectReportsCount FROM subordinates s LEFT JOIN Departments d ON s.DepartmentID = d.DepartmentID LEFT JOIN Roles r ON s.RoleID = r.RoleID LEFT JOIN Tasks t ON s.EmployeeID = t.AssignedTo LEFT JOIN (SELECT ManagerID, COUNT(*) AS SubCount FROM Employees GROUP BY ManagerID) direct ON s.EmployeeID = direct.ManagerID GROUP BY s.EmployeeID, s.Name, s.ManagerID, d.DepartmentName, r.RoleName, direct.SubCount ORDER BY s.Name;";
                    printData(sql);
                    break;
                case 13:
                    sql = "WITH RECURSIVE manager_tree AS ( SELECT e.EmployeeID AS root_manager, e.EmployeeID AS subordinate FROM Employees e UNION ALL SELECT mt.root_manager, c.EmployeeID AS subordinate FROM manager_tree mt JOIN Employees c ON c.ManagerID = mt.subordinate), all_subordinates AS ( SELECT root_manager AS ManagerID, COUNT(DISTINCT subordinate) - 1 AS TotalReportsCount FROM manager_tree GROUP BY root_manager),managers_with_reports AS ( SELECT e.EmployeeID, e.Name, e.ManagerID AS ParentManagerID, e.DepartmentID, e.RoleID, COALESCE(a.TotalReportsCount, 0) AS TotalReportsCount FROM Employees e LEFT JOIN all_subordinates a ON e.EmployeeID = a.ManagerID LEFT JOIN Roles rl ON e.RoleID = rl.RoleID WHERE COALESCE(a.TotalReportsCount, 0) > 0 AND rl.RoleName = 'Manager') SELECT m.EmployeeID, m.Name AS EmployeeName, m.ParentManagerID AS ManagerID, dep.DepartmentName, rl.RoleName, NULLIF(GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ', '), '') AS Tasks, m.TotalReportsCount FROM managers_with_reports m LEFT JOIN Departments dep ON m.DepartmentID = dep.DepartmentID LEFT JOIN Roles rl ON m.RoleID = rl.RoleID LEFT JOIN Tasks t ON m.EmployeeID = t.AssignedTo GROUP BY m.EmployeeID, m.Name, m.ParentManagerID, dep.DepartmentName, rl.RoleName, m.TotalReportsCount ORDER BY m.Name;";
                    printData(sql);
                    break;
            }
        }
        while (key != 0);
    }
}
