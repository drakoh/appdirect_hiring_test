package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.Application;
import com.remicartier.appdirect.hiring.exception.EventException;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 7:51 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private DBService dbService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubscribeUserAlreadyDefined() {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        try {
            userService.subscribeUser(appDirectUser);
            fail(); //should trigger exception
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='null', errorCode='ALREADY_SUBSCRIBED'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testSubscribeUserException() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        doThrow(new IllegalStateException("?!")).when(dbService).addUser(any(AppDirectUser.class));
        try {
            userService.subscribeUser(appDirectUser);
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException: java.lang.IllegalStateException: ?!", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='null', errorCode='ALREADY_SUBSCRIBED'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testSubscribeUser() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        userService.subscribeUser(appDirectUser);
        verify(dbService).addUser(any(AppDirectUser.class));
    }

    @Test
    public void testUnSubscribeUserNotDefined() {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        try {
            userService.unSubscribeUser(appDirectUser);
            fail(); //should trigger exception
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='ACCOUNT_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testUnSubscribeUserException() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        doThrow(new IllegalStateException("?!")).when(dbService).deleteUser(any(AppDirectUser.class));
        try {
            userService.unSubscribeUser(appDirectUser);
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException: java.lang.IllegalStateException: ?!", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='ACCOUNT_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testUnSubscribeUser() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        userService.unSubscribeUser(appDirectUser);
        verify(dbService).deleteUser(any(AppDirectUser.class));
    }

    @Test
    public void testAssignUserAlreadyDefined() {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        try {
            userService.assignUser(appDirectUser);
            fail(); //should trigger exception
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='null', errorCode='ALREADY_ASSIGNED'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testAssignUserException() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        doThrow(new IllegalStateException("?!")).when(dbService).addUser(any(AppDirectUser.class));
        try {
            userService.assignUser(appDirectUser);
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException: java.lang.IllegalStateException: ?!", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='null', errorCode='ALREADY_ASSIGNED'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testAssignUser() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        userService.assignUser(appDirectUser);
        verify(dbService).addUser(any(AppDirectUser.class));
    }

    @Test
    public void testUnAssignUserNotDefined() {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        try {
            userService.unAssignUser(appDirectUser);
            fail(); //should trigger exception
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='USER_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testUnAssignUserException() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        doThrow(new IllegalStateException("?!")).when(dbService).deleteUser(any(AppDirectUser.class));
        try {
            userService.unAssignUser(appDirectUser);
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException: java.lang.IllegalStateException: ?!", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='USER_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testUnAssignUser() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        userService.unAssignUser(appDirectUser);
        verify(dbService).deleteUser(any(AppDirectUser.class));
    }

    @Test
    public void testChangeUserNotDefined() {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(false);
        try {
            userService.changeUser(appDirectUser);
            fail(); //should trigger exception
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='ACCOUNT_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testChangeUserException() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        doThrow(new IllegalStateException("?!")).when(dbService).updateUser(any(AppDirectUser.class));
        try {
            userService.changeUser(appDirectUser);
        } catch (EventException x) {
            assertEquals("com.remicartier.appdirect.hiring.exception.EventException: java.lang.IllegalStateException: ?!", x.toString());
            assertEquals("Result{accountIdentifier='null', success=false, message='Unable to remove user', errorCode='ACCOUNT_NOT_FOUND'}", x.getResult().toString());
            assertEquals(HttpStatus.OK, x.getHttpStatus());
        }
    }

    @Test
    public void testChangeUser() throws EventException {
        AppDirectUser appDirectUser = new AppDirectUser();
        when(dbService.doesUserExist(any(AppDirectUser.class))).thenReturn(true);
        userService.changeUser(appDirectUser);
        verify(dbService).updateUser(any(AppDirectUser.class));
    }

    @Test
    public void testGetUsersException() {
        when(dbService.getUsers()).thenThrow(new UncategorizedScriptException("?!"));
        try {
            userService.getUsers();
            fail();
        } catch (UncategorizedScriptException x) {
            assertEquals("?!", x.getMessage());
        }
    }

    @Test
    public void testGetUsers() {
        when(dbService.getUsers()).thenReturn(Arrays.asList(new AppDirectUser(), new AppDirectUser(), new AppDirectUser()));
        List<AppDirectUser> appDirectUserList = userService.getUsers();
        assertNotNull(appDirectUserList);
        assertEquals(3, appDirectUserList.size());
    }
}