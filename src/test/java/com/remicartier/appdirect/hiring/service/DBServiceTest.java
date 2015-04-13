package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.Application;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 8:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DBServiceTest {
    @InjectMocks
    private DBService dbService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAppDirectUserRowMapper() throws SQLException {
        DBService.AppDirectUserRowMapper mapper = new DBService.AppDirectUserRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyInt())).thenReturn("email").thenReturn("firstName").thenReturn("lastName").thenReturn("openid").thenReturn("accoundId").thenReturn("en").thenReturn("company");
        when(resultSet.getBoolean(anyInt())).thenReturn(true);

        AppDirectUser appDirectUser = mapper.mapRow(resultSet, 0);

        assertEquals("AppDirectUser{accountIdentifier='accoundId', email='email', firstName='firstName', lastName='lastName', language='en', openId='openid', uuid='null', company='company', admin=true}", appDirectUser.toString());
    }

    @Test
    public void testGetUserByOpenID() {
        //noinspection unchecked
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class))).thenAnswer(invocationOnMock -> {
            assertEquals("openid", ((Object[]) invocationOnMock.getArguments()[1])[0]);
            return Collections.singletonList(new AppDirectUser());
        });
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        assertTrue(dbService.getUserByOpenID(appDirectUser.getOpenId()) != null);
    }

    @Test
    public void testAddUserException() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(0);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        try {
            dbService.addUser(appDirectUser);
        } catch (Exception x) {
            assertEquals("Unable to insert, affected rows = 0", x.getMessage());
        }
    }

    @Test
    public void testAddUser() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(1);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        dbService.addUser(appDirectUser);
    }

    @Test
    public void testUpdateUserException() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(0);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        try {
            dbService.updateUser(appDirectUser);
        } catch (Exception x) {
            assertEquals("Unable to update, affected rows = 0", x.getMessage());
        }
    }

    @Test
    public void testUpdateUser() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(1);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        dbService.updateUser(appDirectUser);
    }

    @Test
    public void testDeleteUserException() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(0);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        try {
            dbService.deleteUser(appDirectUser);
        } catch (Exception x) {
            assertEquals("Unable to delete, affected rows = 0", x.getMessage());
        }
    }

    @Test
    public void testDeleteUser() {
        //noinspection RedundantTypeArguments
        when(jdbcTemplate.update(anyString(), Matchers.<Object>anyVararg())).thenReturn(1);
        AppDirectUser appDirectUser = new AppDirectUser();
        appDirectUser.setOpenId("openid");
        dbService.deleteUser(appDirectUser);
    }

    @Test
    public void testGetUsers() {
        //noinspection unchecked
        when(jdbcTemplate.query(anyString(), any(Object[].class), (RowMapper<AppDirectUser>)any(RowMapper.class))).thenReturn(Collections.singletonList(new AppDirectUser()));
        assertNotNull(dbService.getUsers());
        assertEquals(1, dbService.getUsers().size());
    }
}