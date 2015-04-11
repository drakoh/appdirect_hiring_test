package com.remicartier.appdirect.hiring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 11:54 AM
 */
@Service
public class DBService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean doesUserExist(AppDirectUser user) {
        return jdbcTemplate.query("SELECT email,first_name,last_name,open_id,uuid,account_identifier,language FROM user WHERE open_id = ?", new Object[]{user.getOpenId()}, new AppDirectUserResultSetExtractor()) != null;
    }

    public void addUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("INSERT INTO user (email,first_name,last_name,open_id,uuid,account_identifier,language) VALUES (?,?,?,?,?,?,?) WHERE open_id = ?",
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getOpenId(), user.getUuid(), user.getAccountIdentifier(), user.getLanguage(), user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to insert, affected rows = " + affectedRows);
        }
    }

    public void updateUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("UPDATE user SET email=?,first_name=?,last_name=?,open_id=?,uuid=?,account_identifier=?,language=? WHERE open_id = ?",
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getOpenId(), user.getUuid(), user.getAccountIdentifier(), user.getLanguage(), user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to update, affected rows = " + affectedRows);
        }
    }

    public void deleteUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("DELETE FROM user WHERE open_id = ?", user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to delete, affected rows = " + affectedRows);
        }
    }

    private static class AppDirectUserResultSetExtractor implements ResultSetExtractor<AppDirectUser> {
        @Override
        public AppDirectUser extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            AppDirectUser appDirectUser = new AppDirectUser();
            appDirectUser.setEmail(resultSet.getString(1));
            appDirectUser.setFirstName(resultSet.getString(2));
            appDirectUser.setLastName(resultSet.getString(3));
            appDirectUser.setOpenId(resultSet.getString(4));
            appDirectUser.setAccountIdentifier(resultSet.getString(5));
            appDirectUser.setLanguage(resultSet.getString(6));
            return appDirectUser;
        }
    }
}
