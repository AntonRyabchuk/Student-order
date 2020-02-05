package edu.javacourse.studentorder.dao;

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.*;
import edu.javacourse.studentorder.exception.DaoException;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class StudentDaoImpl implements IStudentOrderDao {

    private static final String INSERT_ORDER = "INSERT INTO jc_student_order(" +
            "student_order_status, student_order_date, h_sur_name, h_given_name, h_patronymic, " +
            "h_date_of_birth, h_passport_seria, h_passport_number, h_passport_date, h_passport_office_id, h_post_index, " +
            "h_streetcode, h_building, h_extension, h_apartment, h_university_id, h_student_number, " +
            "w_sur_name, w_given_name, w_patronymic, w_date_of_birth, w_passport_seria, w_passport_number, " +
            "w_passport_date, w_passport_office_id, w_post_index, w_streetcode, w_building, w_extension, w_apartment, " +
            "w_university_id, w_student_number, " +
            "certificate_id, register_office_id, marriage_date)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String INSERT_CHILD = "INSERT INTO jc_student_child(" +
            "student_order_id, c_sur_name, c_given_name, c_patronymic, c_date_of_birth, c_certificate_number, " +
            "c_certificate_date, c_register_office_id, c_post_index, c_streetcode, c_building, c_extension, c_apartment)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_ORDERS = "SELECT so.*, ro.r_office_area_id, ro.r_office_name, " +
        "po_h.p_office_area_id as h_p_office_area_id, po_h.p_office_name as h_p_office_name, " +
        "po_w.p_office_area_id as w_p_office_area_id, po_w.p_office_name as w_p_office_name " +
        "FROM jc_student_order so " +
        "INNER JOIN jc_register_office ro ON so.register_office_id = ro.r_office_id " +
        "INNER JOIN jc_passport_office po_h ON so.h_passport_office_id = po_h.p_office_id " +
        "INNER JOIN jc_passport_office po_w ON so.w_passport_office_id = po_w.p_office_id " +
        "WHERE student_order_status = ? ORDER BY student_order_date LIMIT ?";

    private static final String SELECT_CHILDREN = "SELECT soc.*, ro.r_office_area_id, ro.r_office_name " +
            "FROM jc_student_child soc " +
            "INNER JOIN jc_register_office ro ON ro.r_office_id = soc.c_register_office_id " +
            "WHERE soc.student_order_id IN ";

    private static final String SELECT_ORDERS_FULL = "SELECT so.*, ro.r_office_area_id, ro.r_office_name, " +
            "po_h.p_office_area_id as h_p_office_area_id, po_h.p_office_name as h_p_office_name, " +
            "po_w.p_office_area_id as w_p_office_area_id, po_w.p_office_name as w_p_office_name, " +
            "soc.*, ro_c.r_office_area_id, ro_c.r_office_name " +
            "FROM jc_student_order so " +
            "INNER JOIN jc_register_office ro ON so.register_office_id = ro.r_office_id " +
            "INNER JOIN jc_passport_office po_h ON so.h_passport_office_id = po_h.p_office_id " +
            "INNER JOIN jc_passport_office po_w ON so.w_passport_office_id = po_w.p_office_id " +
            "INNER JOIN jc_student_child soc ON soc.student_order_id = so.student_order_id " +
            "INNER JOIN jc_register_office ro_c ON ro_c.r_office_id = soc.c_register_office_id " +
            "WHERE student_order_status = ? ORDER BY so.student_order_id LIMIT ?";

    // TODO make one method
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(
                Config.getProperty(Config.DB_URL),
                Config.getProperty(Config.DB_LOGIN), Config.getProperty(Config.DB_PASSWORD));

        return connection;
    }

    @Override
    public Long saveStudentOrder(StudentOrder studentOrder) throws DaoException {
        Long resultId = -1L;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ORDER, new String[]{"student_order_id"})) {

            connection.setAutoCommit(false);
            try {
                // Header
                statement.setInt(1, StudentOrderStatus.START.ordinal());
                statement.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));

                // Husband and wife
                setParamsForAdult(statement, 3, studentOrder.getHusband());
                setParamsForAdult(statement, 18, studentOrder.getWife());

                // Marriage
                statement.setString(33, studentOrder.getMarriageCertificateId());
                statement.setLong(34, studentOrder.getMarriageOffice().getOfficeId());
                statement.setDate(35, java.sql.Date.valueOf(studentOrder.getMarriageDate()));

                statement.executeUpdate();

                ResultSet genKeysResSet = statement.getGeneratedKeys();

                if (genKeysResSet.next()) {
                    resultId = genKeysResSet.getLong(1);
                }

                saveChildren(connection, studentOrder, resultId);

                connection.commit();
            } catch (SQLException e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return resultId;
    }

    private void saveChildren(Connection connection, StudentOrder studentOrder, Long resultId) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CHILD)){

            for (Child child : studentOrder.getChildren()){
                statement.setLong(1, resultId);
                setParamsForChild(statement, child);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new DaoException(e);
        }
    }

    private void setParamsForAdult(PreparedStatement statement, int start, Adult adult) throws SQLException {
        setParamsForPerson(statement, start, adult);
        statement.setString(start + 4, adult.getPassportSeria());
        statement.setString(start + 5, adult.getPassportNumber());
        statement.setDate(start + 6, Date.valueOf(adult.getIssueDate()));
        statement.setLong(start + 7, adult.getIssueDepartment().getOfficeId());
        setParamsForAddress(statement, start + 8, adult);
        statement.setLong(start + 13, adult.getUniversity().getUniversityId());
        statement.setString(start + 14, adult.getStudentId());
    }

    private void setParamsForChild(PreparedStatement statement, Child child) throws SQLException {
        setParamsForPerson(statement, 2, child);
        statement.setString(6, child.getCertificateNumber());
        statement.setDate(7, java.sql.Date.valueOf(child.getIssueDate()));
        statement.setLong(8, child.getIssueDepartment().getOfficeId());
        setParamsForAddress(statement, 9, child);
    }

    private void setParamsForPerson(PreparedStatement statement, int start, Person person) throws SQLException {
        statement.setString(start, person.getSurName());
        statement.setString(start + 1, person.getGivenName());
        statement.setString(start + 2, person.getPatronymic());
        statement.setDate(start + 3, Date.valueOf(person.getDateOfBirth()));
    }

    private void setParamsForAddress(PreparedStatement statement, int start, Person person) throws SQLException {
        Address address = person.getAddress();
        statement.setString(start, address.getPostCode());
        statement.setLong(start + 1, address.getStreet().getStreetCode());
        statement.setString(start + 2, address.getBuilding());
        statement.setString(start + 3, address.getExtension());
        statement.setString(start + 4, address.getApartment());
    }

    @Override
    public List<StudentOrder> getStudentOrders() throws DaoException {
//        return getStudentOrdersTwoSelect();
        return getStudentOrdersOneSelect();
    }

    private List<StudentOrder> getStudentOrdersOneSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS_FULL)){

            Map<Long, StudentOrder> maps = new HashMap<>();
            int limit = Integer.parseInt(Config.getProperty(Config.DB_LIMIT));
            statement.setInt(1, StudentOrderStatus.START.ordinal());
            statement.setInt(2, limit);
            ResultSet resultSet = statement.executeQuery();
            int counter = 0;
            while (resultSet.next()){
                Long soId = resultSet.getLong("student_order_id");
                if(!maps.containsKey(soId)) {
                    StudentOrder studentOrder = getFullStudentOrder(resultSet);

                    result.add(studentOrder);
                    maps.put(soId, studentOrder);
                    counter++;
                }
                //тут мы знаем что заявка уже есть
                StudentOrder so = maps.get(soId);
                Child child = fillChild(resultSet);
                so.addChild(child);
            }

            // отбрасываем запись последней семьи, чтобы не получить только часть детей
            if(counter >= limit){
                result.remove(result.size() - 1);
            }

            resultSet.close();

        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return result;
    }

    private List<StudentOrder> getStudentOrdersTwoSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS)){

            statement.setInt(1, StudentOrderStatus.START.ordinal());
            // установка лимита исключительно для попробовать
            statement.setInt(2, Integer.parseInt(Config.getProperty(Config.DB_LIMIT)));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                StudentOrder studentOrder = getFullStudentOrder(resultSet);

                result.add(studentOrder);
            }
            findChildren(connection, result);

        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return result;
    }

    private StudentOrder getFullStudentOrder(ResultSet resultSet) throws SQLException {
        StudentOrder studentOrder = new StudentOrder();

        fillStudentOrder(resultSet, studentOrder);
        fillMarriage(resultSet, studentOrder);

        Adult husband = fillAdult(resultSet, "h_");
        Adult wife = fillAdult(resultSet, "w_");
        studentOrder.setHusband(husband);
        studentOrder.setWife(wife);
        return studentOrder;
    }

    private void findChildren(Connection connection, List<StudentOrder> result) throws DaoException {
        // TODO изучить потоки
        String cl = "(" + result.stream().map( so -> String.valueOf(so.getStudentOrderId()))
                .collect(Collectors.joining(",")) + ")";

        // для более быстрого поиска
        Map<Long, StudentOrder> maps = result.stream()
                .collect(Collectors.toMap(so -> so.getStudentOrderId(), so -> so));

        try (PreparedStatement statement = connection.prepareStatement(SELECT_CHILDREN + cl)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Child child = fillChild(resultSet);
                StudentOrder so = maps.get(resultSet.getLong("student_order_id"));
                so.addChild(child);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException(e);
        }
    }

    private Adult fillAdult(ResultSet resultSet, String prefix) throws SQLException {
        Adult adult = new Adult();
        adult.setSurName(resultSet.getString(prefix + "sur_name"));
        adult.setGivenName(resultSet.getString(prefix + "given_name"));
        adult.setPatronymic(resultSet.getString(prefix + "patronymic"));
        adult.setDateOfBirth(resultSet.getDate(prefix + "date_of_birth").toLocalDate());
        adult.setPassportSeria(resultSet.getString(prefix + "passport_seria"));
        adult.setPassportNumber(resultSet.getString(prefix + "passport_number"));
        adult.setIssueDate(resultSet.getDate(prefix + "passport_date").toLocalDate());

        Long officeId = resultSet.getLong(prefix + "passport_office_id");
        String officeArea = resultSet.getString(prefix + "p_office_area_id");
        String officeName = resultSet.getString(prefix + "p_office_name");
        PassportOffice passportOffice = new PassportOffice(officeId, officeArea, officeName);
        adult.setIssueDepartment(passportOffice);

        Address address = new Address();
        Street street = new Street(resultSet.getLong(prefix + "streetcode"), "");
        address.setStreet(street);
        address.setPostCode(resultSet.getString(prefix + "post_index"));
        address.setBuilding(resultSet.getString(prefix + "building"));
        address.setExtension(resultSet.getString(prefix + "extension"));
        address.setApartment(resultSet.getString(prefix + "apartment"));
        adult.setAddress(address);

        University university = new University(resultSet.getLong(prefix + "university_id"), "");
        adult.setUniversity(university);
        adult.setStudentId(resultSet.getString(prefix + "student_number"));

        return adult;
    }

    private void fillStudentOrder(ResultSet resultSet, StudentOrder studentOrder) throws SQLException {
        studentOrder.setStudentOrderId(resultSet.getLong("student_order_id"));
        studentOrder.setStudentOrderDate(resultSet.getTimestamp("student_order_date").toLocalDateTime());
        studentOrder.setStatus(StudentOrderStatus.fromValue(resultSet.getInt("student_order_status")));

    }

    private void fillMarriage(ResultSet resultSet, StudentOrder studentOrder) throws SQLException {
        studentOrder.setMarriageCertificateId(resultSet.getString("certificate_id"));
        studentOrder.setMarriageDate(resultSet.getDate("marriage_date").toLocalDate());

        Long registerOfficeId = resultSet.getLong("register_office_id");
        String areaId = resultSet.getString("r_office_area_id");
        String rOfficeName = resultSet.getString("r_office_name");
        RegisterOffice registerOffice = new RegisterOffice(registerOfficeId, areaId, rOfficeName);
        studentOrder.setMarriageOffice(registerOffice);
    }

    private Child fillChild(ResultSet resultSet) throws SQLException {
        String surName = resultSet.getString("c_sur_name");
        String givenName = resultSet.getString("c_given_name");
        String patronymic = resultSet.getString("c_patronymic");
        LocalDate dateOfBirth = resultSet.getDate("c_date_of_birth").toLocalDate();

        Child child = new Child(surName, givenName, patronymic, dateOfBirth);

        child.setCertificateNumber(resultSet.getString("c_certificate_number"));
        child.setIssueDate(resultSet.getDate("c_certificate_date").toLocalDate());

        Long roId = resultSet.getLong("c_register_office_id");
        String roArea = resultSet.getString("r_office_area_id");
        String roName = resultSet.getString("r_office_name");
        RegisterOffice registerOffice = new RegisterOffice(roId, roArea, roName);
        child.setIssueDepartment(registerOffice);

        Address address = new Address();
        Street street = new Street(resultSet.getLong("c_streetcode"), "");
        address.setStreet(street);
        address.setPostCode(resultSet.getString("c_post_index"));
        address.setBuilding(resultSet.getString("c_building"));
        address.setExtension(resultSet.getString("c_extension"));
        address.setApartment(resultSet.getString("c_apartment"));
        child.setAddress(address);
        return child;
    }
}
