package com.remicartier.appdirect.hiring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        return jdbcTemplate.query("SELECT email,first_name,last_name,open_id,uuid,account_identifier,language FROM public.user WHERE open_id = ?", new Object[]{user.getOpenId()}, new AppDirectUserRowMapper()).size() != 0;
    }

    public void addUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("INSERT INTO public.user (email,first_name,last_name,open_id,uuid,account_identifier,language) VALUES (?,?,?,?,?,?,?) WHERE open_id = ?",
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getOpenId(), user.getUuid(), user.getAccountIdentifier(), user.getLanguage(), user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to insert, affected rows = " + affectedRows);
        }
    }

    public void updateUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("UPDATE public.user SET email=?,first_name=?,last_name=?,open_id=?,uuid=?,account_identifier=?,language=? WHERE open_id = ?",
                user.getEmail(), user.getFirstName(), user.getLastName(), user.getOpenId(), user.getUuid(), user.getAccountIdentifier(), user.getLanguage(), user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to update, affected rows = " + affectedRows);
        }
    }

    public void deleteUser(AppDirectUser user) {
        int affectedRows = jdbcTemplate.update("DELETE FROM public.user WHERE open_id = ?", user.getOpenId());
        if (affectedRows != 1) {
            throw new IllegalStateException("Unable to delete, affected rows = " + affectedRows);
        }
    }

    public List<AppDirectUser> getUsers() {
        return jdbcTemplate.query("SELECT email,first_name,last_name,open_id,uuid,account_identifier,language FROM public.user", new Object[0], new AppDirectUserRowMapper());
    }

    private static class AppDirectUserRowMapper implements RowMapper<AppDirectUser> {
        @Override
        public AppDirectUser mapRow(ResultSet resultSet, int i) throws SQLException {
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
