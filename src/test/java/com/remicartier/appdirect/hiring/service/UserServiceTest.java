package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.Application;
import com.remicartier.appdirect.hiring.exception.EventException;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import org.apache.commons.io.IOUtils;
import org.joox.Match;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.joox.JOOX.$;
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

    @Test
    public void testExtractUser() throws IOException, SAXException {
        AppDirectUser user;
        user = userService.extractUser(getMatch("dummyOrder.xml"), "SUBSCRIPTION_ORDER");
        assertEquals("AppDirectUser{accountIdentifier='null', email='test-email+creator@appdirect.com', firstName='DummyCreatorFirst', lastName='DummyCreatorLast', language='fr', openId='\n" +
                "            https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
                "        ', uuid='ec5d8eda-5cec-444d-9e30-125b6e4b67e2'}", user.toString());
        user = userService.extractUser(getMatch("dummyChange.xml"), "SUBSCRIPTION_CHANGE");
        assertEquals("AppDirectUser{accountIdentifier='dummy-account', email='test-email+creator@appdirect.com', firstName='DummyCreatorFirst', lastName='DummyCreatorLast', language='fr', openId='\n" +
                "            https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
                "        ', uuid='ec5d8eda-5cec-444d-9e30-125b6e4b67e2'}", user.toString());
        user = userService.extractUser(getMatch("dummyCancel.xml"), "SUBSCRIPTION_CANCEL");
        assertEquals("AppDirectUser{accountIdentifier='dummy-account', email='test-email+creator@appdirect.com', firstName='DummyCreatorFirst', lastName='DummyCreatorLast', language='fr', openId='\n" +
                "            https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
                "        ', uuid='ec5d8eda-5cec-444d-9e30-125b6e4b67e2'}", user.toString());
        user = userService.extractUser(getMatch("dummyAssign.xml"), "USER_ASSIGNMENT");
        assertEquals("AppDirectUser{accountIdentifier='dummy-account', email='test-email@appdirect.com', firstName='DummyFirst', lastName='DummyLast', language='fr', openId='\n" +
                "                https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
                "            ', uuid='ec5d8eda-5cec-444d-9e30-125b6e4b67e2'}", user.toString());
        user = userService.extractUser(getMatch("dummyUnassign.xml"), "USER_UNASSIGNMENT");
        assertEquals("AppDirectUser{accountIdentifier='dummy-account', email='test-email@appdirect.com', firstName='DummyFirst', lastName='DummyLast', language='fr', openId='\n" +
                "                https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
                "            ', uuid='ec5d8eda-5cec-444d-9e30-125b6e4b67e2'}", user.toString());
    }

    private Match getMatch(String resource) throws IOException, SAXException {
        Document document = $(new StringReader(IOUtils.toString(getClass().getClassLoader().getResourceAsStream(resource)))).document();
        return $(document);
    }
}