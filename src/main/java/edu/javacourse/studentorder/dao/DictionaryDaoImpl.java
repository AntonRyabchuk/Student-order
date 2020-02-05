package edu.javacourse.studentorder.dao;

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.CountryArea;
import edu.javacourse.studentorder.domain.PassportOffice;
import edu.javacourse.studentorder.domain.RegisterOffice;
import edu.javacourse.studentorder.domain.Street;
import edu.javacourse.studentorder.exception.DaoException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DictionaryDaoImpl implements IDictionaryDao {

    private static final String GET_STREET = "SELECT street_code, UPPER(street_name) FROM jc_street WHERE UPPER(street_name) LIKE UPPER(?)";

    private static final String GET_PASSPORT = "SELECT * FROM jc_passport_office WHERE p_office_area_id = ?";

    private static final String GET_REGISTER = "SELECT * FROM jc_register_office WHERE r_office_area_id = ?";

    private static final String GET_AREA = "SELECT * FROM jc_country_struct WHERE area_id like ? and area_id <> ?";

    public static void main(String[] args) throws Exception {

    }

    // TODO make one method
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(
                Config.getProperty(Config.DB_URL),
                Config.getProperty(Config.DB_LOGIN), Config.getProperty(Config.DB_PASSWORD));

        return connection;
    }

    @Override
    public List<Street> findStreets(String pattern) throws DaoException {
        List<Street> resultStreets = new LinkedList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_STREET)) {

            statement.setString(1, "%" + pattern + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long streetCode = (long) resultSet.getInt(1);
                String streetName = resultSet.getString(2);
                resultStreets.add(new Street(streetCode, streetName));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return resultStreets;
    }

    @Override
    public List<PassportOffice> findPassportOffices(String areaId) throws DaoException {
        List<PassportOffice> result = new LinkedList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PASSPORT)) {

            statement.setString(1, areaId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PassportOffice passportOffice = new PassportOffice(
                        resultSet.getLong("p_office_id"),
                        resultSet.getString("p_office_area_id"),
                        resultSet.getString("p_office_name"));
                result.add(passportOffice);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return result;
    }

    @Override
    public List<RegisterOffice> findRegisterOffices(String areaId) throws DaoException {
        List<RegisterOffice> result = new LinkedList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_REGISTER)) {

            statement.setString(1, areaId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RegisterOffice registerOffice = new RegisterOffice(
                        resultSet.getLong("r_office_id"),
                        resultSet.getString("r_office_area_id"),
                        resultSet.getString("r_office_name"));
                result.add(registerOffice);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return result;
    }

    @Override
    public List<CountryArea> findArea(String areaId) throws DaoException {
        List<CountryArea> result = new LinkedList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_AREA)) {

            String param1 = buildParam(areaId);
            String param2 = areaId;
            statement.setString(1, param1);
            statement.setString(2, param2);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CountryArea countryArea = new CountryArea(
                        resultSet.getString("area_id"),
                        resultSet.getString("area_name"));
                result.add(countryArea);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DaoException(ex);
        }
        return result;
    }

    private String buildParam(String areaId) throws SQLException {
        if(areaId == null || areaId.trim().isEmpty()){
            return "__0000000000";
        } else if (areaId.endsWith("0000000000")){
            return areaId.substring(0,2) + "___0000000";
        } else if (areaId.endsWith("0000000")){
            return areaId.substring(0,5) + "___0000";
        } else if(areaId.endsWith("0000")){
            return areaId.substring(0,8) + "____";
        } else {
            throw new SQLException("Invalid parameter 'areaId':" + areaId);
        }
    }
}
