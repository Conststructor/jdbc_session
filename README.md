# Описание проекта
Проект пердставляет собой программу проверки 13 заданий по SQL БД.

**Для запуска необходимо** создать MySQL БД (с помощью XAMPP Control Panel) запустить SQL скрипт из файла Create_and_fill_DB.txt для создания и заполнения таблиц и данных.

После запуска в окне программы введите логин и пароль для подключения к БД (если пароля нет просто нажмите enter) затем вводите номер задания для проверки и исполнится заранее подготовленный скрипт решения (скрипты решения представленны ниже). 


# Задание №1 (1.1)
Найдите производителей (maker) и модели всех мотоциклов, которые имеют мощность более 150 лошадиных сил, стоят менее 20 тысяч долларов и являются спортивными (тип Sport). Также отсортируйте результаты по мощности в порядке убывания.

***Решение задачи должно представлять из себя один SQL-запрос.***

### ***Решение***

<pre> SELECT v.maker, m.model
 FROM Motorcycle m
 JOIN Vehicle v ON m.model = v.model
 WHERE m.horsepower > 150
  AND m.price < 20000
  AND m.type = 'Sport'
 ORDER BY m.horsepower DESC;</pre>

---

# Задание №2 (1.2)
Найти информацию о производителях и моделях различных типов транспортных средств (автомобили, мотоциклы и велосипеды), которые соответствуют заданным критериям.

1. Автомобили:

    - Извлечь данные о всех автомобилях, которые имеют:
        Мощность двигателя более 150 лошадиных сил.
        Объем двигателя менее 3 литров.
        Цену менее 35 тысяч долларов.

    - В выводе должны быть указаны производитель (maker), номер модели (model), мощность (horsepower), объем двигателя (engine_capacity) и тип транспортного средства, который будет обозначен как Car.
2. Мотоциклы:

    - Извлечь данные о всех мотоциклах, которые имеют:
        Мощность двигателя более 150 лошадиных сил.
        Объем двигателя менее 1,5 литров.
        Цену менее 20 тысяч долларов.

    - В выводе должны быть указаны производитель (maker), номер модели (model), мощность (horsepower), объем двигателя (engine_capacity) и тип транспортного средства, который будет обозначен как Motorcycle.
3. Велосипеды:

    - Извлечь данные обо всех велосипедах, которые имеют:
        Количество передач больше 18.
        Цену менее 4 тысяч долларов.

    - В выводе должны быть указаны производитель (maker), номер модели (model), а также NULL для мощности и объема двигателя, так как эти характеристики не применимы для велосипедов. Тип транспортного средства будет обозначен как Bicycle.
4. Сортировка:

    - Результаты должны быть объединены в один набор данных и отсортированы по мощности в порядке убывания. Для велосипедов, у которых нет значения мощности, они будут располагаться внизу списка.

___Решение задачи должно представлять из себя один SQL-запрос.___

### ***Решение***

<pre> 
SELECT v.maker,
       c.model,
       c.horsepower,
       c.engine_capacity,
       'Car' AS vehicle_type
FROM Car c
JOIN Vehicle v ON c.model = v.model
WHERE c.horsepower > 150
  AND c.engine_capacity < 3.0
  AND c.price < 35000
UNION ALL
SELECT v.maker,
       m.model,
       m.horsepower,
       m.engine_capacity,
       'Motorcycle' AS vehicle_type
FROM Motorcycle m
JOIN Vehicle v ON m.model = v.model
WHERE m.horsepower > 150
  AND m.engine_capacity < 1.5
  AND m.price < 20000
UNION ALL
SELECT v.maker,
       b.model,
       NULL AS horsepower,
       NULL AS engine_capacity,
       'Bicycle' AS vehicle_type
FROM Bicycle b
JOIN Vehicle v ON b.model = v.model
WHERE b.gear_count > 18
  AND b.price < 4000
ORDER BY horsepower DESC;</pre>
---

# Задание №3 (2.1)
Определить, какие автомобили из каждого класса имеют наименьшую среднюю позицию в гонках, и вывести информацию о каждом таком автомобиле для данного класса, включая его класс, среднюю позицию и количество гонок, в которых он участвовал. Также отсортировать результаты по средней позиции.

***Решение задачи должно представлять из себя один SQL-запрос.***

### ***Решение***

<pre>
WITH car_stats AS (
  SELECT
    c.class,
    res.car,
    AVG(res.position) AS avg_pos,
    COUNT(*) AS races_count
  FROM Results res
  JOIN Cars r ON res.car = r.name
  JOIN Classes c ON r.class = c.class
  GROUP BY c.class, res.car
),
best_per_class AS (
  SELECT
    class,
    MIN(avg_pos) AS best_avg
  FROM car_stats
  GROUP BY class
)
SELECT
  cs.car AS car_name,
  cs.class,
  cs.avg_pos,
  cs.races_count
FROM car_stats cs
JOIN best_per_class b
  ON cs.class = b.class
 AND cs.avg_pos = b.best_avg
ORDER BY cs.avg_pos;
</pre>

---

# Задание №4 (2.2)
Определить автомобиль, который имеет наименьшую среднюю позицию в гонках среди всех автомобилей, и вывести информацию об этом автомобиле, включая его класс, среднюю позицию, количество гонок, в которых он участвовал, и страну производства класса автомобиля. Если несколько автомобилей имеют одинаковую наименьшую среднюю позицию, выбрать один из них по алфавиту (по имени автомобиля).

***Решение задачи должно представлять из себя один SQL-запрос.***

### ***Решение***

<pre>
SELECT
  cs.car AS car_name,
  cs.class,
  cs.avg_pos,
  cs.races_count,
  cl.country
FROM (
  SELECT
    res.car,
    c.class,
    AVG(res.position) AS avg_pos,
    COUNT(*) AS races_count
  FROM Results res
  JOIN Cars r ON res.car = r.name
  JOIN Classes c ON r.class = c.class
  GROUP BY res.car, c.class
) cs
JOIN Classes cl ON cs.class = cl.class
ORDER BY cs.avg_pos ASC, cs.car ASC
LIMIT 1;
</pre>

---

# Задание №5 (2.3)
Определить классы автомобилей, которые имеют наименьшую среднюю позицию в гонках, и вывести информацию о каждом автомобиле из этих классов, включая его имя, среднюю позицию, количество гонок, в которых он участвовал, страну производства класса автомобиля, а также общее количество гонок, в которых участвовали автомобили этих классов. Если несколько классов имеют одинаковую среднюю позицию, выбрать все из них.

***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***

<pre>
WITH car_stats AS (
  SELECT
    c.class,
    r.name AS car_name,
    AVG(res.position) AS avg_pos,
    COUNT(*) AS races_count
  FROM Results res
  JOIN Cars r ON res.car = r.name
  JOIN Classes c ON r.class = c.class
  GROUP BY c.class, r.name
),
class_avg AS (
  SELECT
    class,
    AVG(avg_pos) AS class_avg_pos
  FROM car_stats
  GROUP BY class
),
best_classes AS (
  SELECT class
  FROM class_avg
  WHERE class_avg_pos = (
    SELECT MIN(class_avg_pos) FROM class_avg
  )
)
SELECT
  cs.car_name,
  cs.class as car_class,
  cs.avg_pos as average_position,
  cs.races_count,
  cl.country,
  total.total_races_for_classes as total_races
FROM car_stats cs
JOIN Classes cl ON cs.class = cl.class
JOIN (
  SELECT
    c.class,
    SUM(cs2.races_count) AS total_races_for_classes
  FROM car_stats cs2
  JOIN Classes c ON cs2.class = c.class
  WHERE cs2.class IN (SELECT class FROM best_classes)
  GROUP BY c.class
) total ON cs.class = total.class
WHERE cs.class IN (SELECT class FROM best_classes)
ORDER BY cs.class, cs.car_name;
</pre>
---

# Задание № 6 (2.4)
Определить, какие автомобили имеют среднюю позицию лучше (меньше) средней позиции всех автомобилей в своем классе (то есть автомобилей в классе должно быть минимум два, чтобы выбрать один из них). Вывести информацию об этих автомобилях, включая их имя, класс, среднюю позицию, количество гонок, в которых они участвовали, и страну производства класса автомобиля. Также отсортировать результаты по классу и затем по средней позиции в порядке возрастания.

***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***

<pre>
WITH car_stats AS (
  SELECT
    r.name AS car_name,
    r.class,
    AVG(res.position) AS avg_pos,
    COUNT(*) AS races_count
  FROM Results res
  JOIN Cars r ON res.car = r.name
  GROUP BY r.name, r.class
),
class_stats AS (
  SELECT
    class,
    AVG(avg_pos) AS class_avg_pos,
    COUNT(*) AS cars_in_class
  FROM car_stats
  GROUP BY class
)
SELECT
  cs.car_name,
  cs.class as car_class,
  cs.avg_pos as average_position,
  cs.races_count as race_count,
  cl.country as car_country
FROM car_stats cs
JOIN class_stats cls ON cs.class = cls.class
JOIN Classes cl ON cs.class = cl.class
WHERE cls.cars_in_class >= 2
  AND cs.avg_pos < cls.class_avg_pos
ORDER BY cs.class, cs.avg_pos;
</pre>
---

# Задание №7 (2.5)
Определить, какие классы автомобилей имеют наибольшее количество автомобилей с низкой средней позицией (больше 3.0) и вывести информацию о каждом автомобиле из этих классов, включая его имя, класс, среднюю позицию, количество гонок, в которых он участвовал, страну производства класса автомобиля, а также общее количество гонок для каждого класса. Отсортировать результаты по количеству автомобилей с низкой средней позицией.

***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***
<pre>
WITH car_stats AS (
  SELECT
    r.name AS car_name,
    r.class,
    AVG(res.position) AS average_position,
    COUNT(*) AS races_count
  FROM Results res
  JOIN Cars r ON res.car = r.name
  GROUP BY r.name, r.class
),
low_cars AS (
  SELECT
    class,
    car_name,
    average_position,
    races_count
  FROM car_stats
  WHERE average_position > 3.0
),
class_low_counts AS (
  SELECT
    lc.class,
    COUNT(*) AS low_position_count
  FROM low_cars lc
  GROUP BY lc.class
),
class_total_races AS (
  SELECT
    cs.class,
    SUM(cs.races_count) AS total_races
  FROM car_stats cs
  GROUP BY cs.class
)
SELECT
  lc.car_name,
  lc.class as car_class,
  lc.average_position,
  lc.races_count,
  cl.country as car_coutnry,
  ctr.total_races,
  clc.low_position_count
FROM low_cars lc
JOIN Classes cl ON lc.class = cl.class
JOIN class_low_counts clc ON lc.class = clc.class
JOIN class_total_races ctr ON lc.class = ctr.class
ORDER BY clc.low_position_count DESC, lc.class, lc.average_position;
</pre>
---
# Задание №8 (3.1)
Определить, какие клиенты сделали более двух бронирований в разных отелях, и вывести информацию о каждом таком клиенте, включая его имя, электронную почту, телефон, общее количество бронирований, а также список отелей, в которых они бронировали номера (объединенные в одно поле через запятую). Также подсчитать среднюю длительность их пребывания (в днях) по всем бронированиям. Отсортировать результаты по количеству бронирований в порядке убывания.

***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***
<pre>
SELECT
  c.name,
  c.email,
  c.phone,
  COUNT(b.id_booking) AS total_bookings,
  GROUP_CONCAT(DISTINCT h.name ORDER BY h.name SEPARATOR ', ') AS hotels_list,
  ROUND(AVG(DATEDIFF(b.check_out_date, b.check_in_date)), 2) AS avg_stay_days
FROM Customer c
JOIN Booking b ON b.id_customer = c.id_customer
JOIN Room r ON r.id_room = b.id_room
JOIN Hotel h ON h.id_hotel = r.id_hotel
GROUP BY  c.name, c.email, c.phone
HAVING COUNT(DISTINCT r.id_hotel) > 1
   AND COUNT(b.id_booking) > 2
ORDER BY total_bookings DESC;
</pre>
---

# Задание №9 (3.2)
Необходимо провести анализ клиентов, которые сделали более двух бронирований в разных отелях и потратили более 500 долларов на свои бронирования. Для этого:

- Определить клиентов, которые сделали более двух бронирований и забронировали номера в более чем одном отеле. Вывести для каждого такого клиента следующие данные: ID_customer, имя, общее количество бронирований, общее количество уникальных отелей, в которых они бронировали номера, и общую сумму, потраченную на бронирования.
- Также определить клиентов, которые потратили более 500 долларов на бронирования, и вывести для них ID_customer, имя, общую сумму, потраченную на бронирования, и общее количество бронирований.
- В результате объединить данные из первых двух пунктов, чтобы получить список клиентов, которые соответствуют условиям обоих запросов. Отобразить поля: ID_customer, имя, общее количество бронирований, общую сумму, потраченную на бронирования, и общее количество уникальных отелей.
- Результаты отсортировать по общей сумме, потраченной клиентами, в порядке возрастания.

***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***

<pre>
SELECT
  c.id_customer,
  c.name,
  totals.total_bookings,
  ROUND(totals.total_spent, 2) AS total_spent,
  totals.unique_hotels
FROM Customer c
JOIN (
  SELECT
    b.id_customer,
    COUNT(*) AS total_bookings,
    COUNT(DISTINCT r.id_hotel) AS unique_hotels,
    SUM(r.price * GREATEST(1, DATEDIFF(b.check_out_date, b.check_in_date))) AS total_spent
  FROM Booking b
  JOIN Room r ON r.id_room = b.id_room
  WHERE b.check_out_date > b.check_in_date
  GROUP BY b.id_customer
  HAVING COUNT(*) > 2
     AND COUNT(DISTINCT r.id_hotel) > 1
     AND SUM(r.price * GREATEST(1, DATEDIFF(b.check_out_date, b.check_in_date))) > 500
) AS totals ON totals.id_customer = c.id_customer
ORDER BY totals.total_spent ASC;
</pre>
---

# Задание №10 (3.3)
Вам необходимо провести анализ данных о бронированиях в отелях и определить предпочтения клиентов по типу отелей. Для этого выполните следующие шаги:

1. Категоризация отелей.
    
    Определите категорию каждого отеля на основе средней стоимости номера:

   - «Дешевый»: средняя стоимость менее 175 долларов.
   - «Средний»: средняя стоимость от 175 до 300 долларов.
   - «Дорогой»: средняя стоимость более 300 долларов.

2. Анализ предпочтений клиентов.

    Для каждого клиента определите предпочитаемый тип отеля на основании условия ниже:
   - Если у клиента есть хотя бы один «дорогой» отель, присвойте ему категорию «дорогой».
   - Если у клиента нет «дорогих» отелей, но есть хотя бы один «средний», присвойте ему категорию «средний».
   - Если у клиента нет «дорогих» и «средних» отелей, но есть «дешевые», присвойте ему категорию предпочитаемых отелей «дешевый».

3. Вывод информации.
    
    Выведите для каждого клиента следующую информацию:
   - ID_customer: уникальный идентификатор клиента.
   - name: имя клиента.
   - preferred_hotel_type: предпочитаемый тип отеля.
   - visited_hotels: список уникальных отелей, которые посетил клиент.
   
4. Сортировка результатов.

    Отсортируйте клиентов так, чтобы сначала шли клиенты с «дешевыми» отелями, затем со «средними» и в конце — с «дорогими».


***Решение задачи должно представлять из себя один SQL-запрос.***
  
### ***Решение***
<pre>
SELECT
  c.id_customer,
  c.name,
  CASE
    WHEN MAX(h_cat.cat = 'Дорогой') = 1 THEN 'Дорогой'
    WHEN MAX(h_cat.cat = 'Средний') = 1 THEN 'Средний'
    WHEN MAX(h_cat.cat = 'Дешевый') = 1 THEN 'Дешевый'
    ELSE NULL
  END AS preferred_hotel_type,
  GROUP_CONCAT(DISTINCT h.name ORDER BY h.name SEPARATOR ', ') AS visited_hotels
FROM Customer c
JOIN Booking b ON b.id_customer = c.id_customer
JOIN Room r ON r.id_room = b.id_room
JOIN Hotel h ON h.id_hotel = r.id_hotel
-- подзапрос вычисляет категорию каждого отеля по средней цене номера
JOIN (
  SELECT
    r.id_hotel,
    CASE
      WHEN AVG(r.price) < 175 THEN 'Дешевый'
      WHEN AVG(r.price) BETWEEN 175 AND 300 THEN 'Средний'
      ELSE 'Дорогой'
    END AS cat
  FROM Room r
  GROUP BY r.id_hotel
) AS h_cat ON h_cat.id_hotel = h.id_hotel
GROUP BY c.id_customer, c.name
HAVING preferred_hotel_type IS NOT NULL
ORDER BY
  FIELD(
    CASE
      WHEN MAX(h_cat.cat = 'Дешевый') = 1 AND MAX(h_cat.cat = 'Средний') = 0 AND MAX(h_cat.cat = 'Дорогой') = 0 THEN 'Дешевый'
      WHEN MAX(h_cat.cat = 'Средний') = 1 AND MAX(h_cat.cat = 'Дорогой') = 0 THEN 'Средний'
      WHEN MAX(h_cat.cat = 'Дорогой') = 1 THEN 'Дорогой'
      WHEN MAX(h_cat.cat = 'Средний') = 1 THEN 'Средний'
      WHEN MAX(h_cat.cat = 'Дешевый') = 1 THEN 'Дешевый'
      ELSE NULL
    END,
  'Дешевый','Средний','Дорогой');
  </pre>

  ---

# Задание №11 (4.1)
Найти всех сотрудников, подчиняющихся Ивану Иванову (с EmployeeID = 1), включая их подчиненных и подчиненных подчиненных, а также самого Ивана Иванова. Для каждого сотрудника вывести следующую информацию:


1. EmployeeID: идентификатор сотрудника.
2. Имя сотрудника.
3. ManagerID: Идентификатор менеджера.
4. Название отдела, к которому он принадлежит.
5. Название роли, которую он занимает.
6. Название проектов, к которым он относится (если есть, конкатенированные в одном столбце через запятую).
7. Название задач, назначенных этому сотруднику (если есть, конкатенированные в одном столбце через запятую).
8. Если у сотрудника нет назначенных проектов или задач, отобразить NULL.

Требования:

- Рекурсивно извлечь всех подчиненных сотрудников Ивана Иванова и их подчиненных.
- Для каждого сотрудника отобразить информацию из всех таблиц.
- Результаты должны быть отсортированы по имени сотрудника.
- Решение задачи должно представлять из себя один sql-запрос и задействовать ключевое слово RECURSIVE.

  
### ***Решение***
<pre>
WITH RECURSIVE subordinates AS (
  SELECT e.EmployeeID, e.Name, e.ManagerID, e.DepartmentID, e.RoleID
  FROM Employees e
  WHERE e.EmployeeID = 1

  UNION ALL

  SELECT e2.EmployeeID, e2.Name, e2.ManagerID, e2.DepartmentID, e2.RoleID
  FROM Employees e2
  JOIN subordinates s ON e2.ManagerID = s.EmployeeID
)
SELECT
  s.EmployeeID,
  s.Name AS EmployeeName,
  s.ManagerID,
  d.DepartmentName,
  r.RoleName,
  p.ProjectName,
  NULLIF(TRIM(BOTH ',' FROM GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ',')), '') AS Tasks
FROM subordinates s
LEFT JOIN Departments d ON s.DepartmentID = d.DepartmentID
LEFT JOIN Roles r ON s.RoleID = r.RoleID 
LEFT JOIN Tasks t ON s.EmployeeID = t.AssignedTo
LEFT JOIN Projects p ON t.ProjectID = p.ProjectID
GROUP BY s.EmployeeID, s.Name, s.ManagerID, d.DepartmentName, r.RoleName
ORDER BY s.Name;
</pre>

---

# Задание №12 (4.2)
Найти всех сотрудников, подчиняющихся Ивану Иванову с EmployeeID = 1, включая их подчиненных и подчиненных подчиненных, а также самого Ивана Иванова. Для каждого сотрудника вывести следующую информацию:

1. EmployeeID: идентификатор сотрудника.
2. Имя сотрудника.
3. Идентификатор менеджера.
4. Название отдела, к которому он принадлежит.
5. Название роли, которую он занимает.
6. Название проектов, к которым он относится (если есть, конкатенированные в одном столбце).
7. Название задач, назначенных этому сотруднику (если есть, конкатенированные в одном столбце).
8. Общее количество задач, назначенных этому сотруднику.
9. Общее количество подчиненных у каждого сотрудника (не включая подчиненных их подчиненных).
10. Если у сотрудника нет назначенных проектов или задач, отобразить NULL.


***Решение задачи должно представлять из себя один sql-запрос и задействовать ключевое слово RECURSIVE.***
  
### ***Решение***
<pre>
WITH RECURSIVE subordinates AS (
  SELECT e.EmployeeID, e.Name, e.ManagerID, e.DepartmentID, e.RoleID
  FROM Employees e
  WHERE e.EmployeeID = 1
  UNION ALL
  SELECT c.EmployeeID, c.Name, c.ManagerID, c.DepartmentID, c.RoleID
  FROM Employees c
  JOIN subordinates s ON c.ManagerID = s.EmployeeID
)
SELECT
  s.EmployeeID,
  s.Name AS EmployeeName,
  s.ManagerID,
  d.DepartmentName,
  r.RoleName,
  NULLIF(GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ', '), '') AS Tasks,
  COUNT(t.TaskID) AS TasksCount,
  COALESCE(direct.SubCount, 0) AS DirectReportsCount
FROM subordinates s
LEFT JOIN Departments d ON s.DepartmentID = d.DepartmentID
LEFT JOIN Roles r ON s.RoleID = r.RoleID
LEFT JOIN Tasks t ON s.EmployeeID = t.AssignedTo
LEFT JOIN (
  SELECT ManagerID, COUNT(*) AS SubCount
  FROM Employees
  GROUP BY ManagerID
) direct ON s.EmployeeID = direct.ManagerID
GROUP BY
  s.EmployeeID,
  s.Name,
  s.ManagerID,
  d.DepartmentName,
  r.RoleName,
  direct.SubCount
ORDER BY s.Name;
</pre>

---

# Задание №13 (4.3)
Найти всех сотрудников, которые занимают роль менеджера и имеют подчиненных (то есть число подчиненных больше 0). Для каждого такого сотрудника вывести следующую информацию:

1. EmployeeID: идентификатор сотрудника.
2. Имя сотрудника.
3. Идентификатор менеджера.
4. Название отдела, к которому он принадлежит.
5. Название роли, которую он занимает.
6. Название проектов, к которым он относится (если есть, конкатенированные в одном столбце).
7. Название задач, назначенных этому сотруднику (если есть, конкатенированные в одном столбце).
8. Общее количество подчиненных у каждого сотрудника (включая их подчиненных).
9. Если у сотрудника нет назначенных проектов или задач, отобразить NULL.

***Решение задачи должно представлять из себя один sql-запрос и задействовать ключевое слово RECURSIVE.***

### ***Решение***

<pre>
WITH RECURSIVE
manager_tree AS (
  SELECT
    e.EmployeeID AS root_manager,
    e.EmployeeID AS subordinate
  FROM Employees e
  UNION ALL
  SELECT
    mt.root_manager,
    c.EmployeeID AS subordinate
  FROM manager_tree mt
  JOIN Employees c ON c.ManagerID = mt.subordinate
),
all_subordinates AS (
  SELECT
    root_manager AS ManagerID,
    COUNT(DISTINCT subordinate) - 1 AS TotalReportsCount  -- вычитаем 1, чтобы не считать сам root_manager
  FROM manager_tree
  GROUP BY root_manager
),
managers_with_reports AS (
  SELECT
    e.EmployeeID,
    e.Name,
    e.ManagerID AS ParentManagerID,
    e.DepartmentID,
    e.RoleID,
    COALESCE(a.TotalReportsCount, 0) AS TotalReportsCount
  FROM Employees e
  LEFT JOIN all_subordinates a ON e.EmployeeID = a.ManagerID
  LEFT JOIN Roles rl ON e.RoleID = rl.RoleID
  WHERE COALESCE(a.TotalReportsCount, 0) > 0
    AND rl.RoleName = 'Manager'   -- заменить на нужное название роли, если в БД другое
)
SELECT
  m.EmployeeID,
  m.Name AS EmployeeName,
  m.ParentManagerID AS ManagerID,
  dep.DepartmentName,
  rl.RoleName,
  NULLIF(GROUP_CONCAT(DISTINCT p.ProjectName ORDER BY p.ProjectName SEPARATOR ', '), '') AS Projects,
  NULLIF(GROUP_CONCAT(DISTINCT t.TaskName ORDER BY t.TaskName SEPARATOR ', '), '') AS Tasks,
  m.TotalReportsCount
FROM managers_with_reports m
LEFT JOIN Departments dep ON m.DepartmentID = dep.DepartmentID
LEFT JOIN Roles rl ON m.RoleID = rl.RoleID
LEFT JOIN EmployeeProjects ep ON m.EmployeeID = ep.EmployeeID
LEFT JOIN Projects p ON ep.ProjectID = p.ProjectID
LEFT JOIN Tasks t ON m.EmployeeID = t.AssignedTo
GROUP BY
  m.EmployeeID,
  m.Name,
  m.ParentManagerID,
  dep.DepartmentName,
  rl.RoleName,
  m.TotalReportsCount
ORDER BY m.Name;

</pre>

