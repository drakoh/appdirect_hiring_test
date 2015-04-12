package com.remicartier.appdirect.hiring.controller;

import com.remicartier.appdirect.hiring.Application;
import com.remicartier.appdirect.hiring.exception.EventException;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import com.remicartier.appdirect.hiring.model.Result;
import com.remicartier.appdirect.hiring.service.DBService;
import com.remicartier.appdirect.hiring.service.OAuthSignatureService;
import com.remicartier.appdirect.hiring.service.UserService;
import net.oauth.OAuthException;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 10:10 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class MainControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @Autowired
    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        //seems the @Mock/#InjectMocks would not bind my mock UserService
        mainController.setUserService(new UserService(){
            @Override
            public List<AppDirectUser> getUsers() {
                return Arrays.asList(new AppDirectUser(), new AppDirectUser());
            }
            @Override
            public void assignUser(AppDirectUser user) throws EventException {
            }

            @Override
            public void changeUser(AppDirectUser user) throws EventException {
            }

            @Override
            public void subscribeUser(AppDirectUser user) throws EventException {
            }

            @Override
            public void unAssignUser(AppDirectUser user) throws EventException {
            }

            @Override
            public void unSubscribeUser(AppDirectUser user) throws EventException {
            }
        });
    }

    @Test
    public void testIndexLoggedOut() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")))
                .andExpect(content().string(not(containsString("Logout"))));
    }

    @Test
    public void testIndexLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new MockSecurityContext(new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2", "MSG", Collections.singletonList(new OpenIDAttribute("attr", "string", Collections.singletonList("Value"))))));

        mvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")))
                .andExpect(content().string(containsString("Logout")));
    }

    @Test
    public void testProfileLoggedOut() throws Exception {
        mvc.perform(get("/profile"))
                .andExpect(status().isFound())//forwarded to login page
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @Test
    public void testProfileLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new MockSecurityContext(new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2", "MSG", Collections.singletonList(new OpenIDAttribute("attr", "string", Collections.singletonList("Value"))))));

        mvc.perform(get("/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Profile</h1>")));
    }

    @Test
    public void testUsersLoggedOut() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isFound())//forwarded to login page
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @Test
    public void testUsersLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new MockSecurityContext(new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2", "MSG", Collections.singletonList(new OpenIDAttribute("attr", "string", Collections.singletonList("Value"))))));

        mvc.perform(get("/users").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Users</h1>")));
    }

    @Test
    public void testLogoutLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new MockSecurityContext(new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS, "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2", "MSG", Collections.singletonList(new OpenIDAttribute("attr", "string", Collections.singletonList("Value"))))));

        mvc.perform(get("/logout").session(session))
                .andExpect(status().isFound())//forwarded to login page
                .andExpect(header().string("Location", "https://www.appdirect.com/applogout?openid=https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2"));
    }

    @Test
    public void testEventsDummy() throws Exception {
        mvc.perform(get("/events").param("eventUrl", "http:/dummy"))
                .andExpect(status().isOk())
                .andExpect(content().string("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><message>OK</message><success>true</success></result>"));
    }

    @Test
    public void testEventsBadSource() throws Exception {
        mainController.setoAuthSignatureService(new OAuthSignatureService(){
            @Override
            public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
                throw new OAuthException("Nope");
            }
        });
        mvc.perform(get("/events").param("eventUrl", "http:/event"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testEventsErrorWhileFetchingURL() throws Exception {
        mainController.setoAuthSignatureService(new OAuthSignatureService() {
            @Override
            public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
                //all good
            }

            @Override
            public String requestURL(String consumerKey, String consumerSecret, URL endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
                throw new IOException("Nope");
            }
        });
        mvc.perform(get("/events").param("eventUrl","http:/event"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><errorCode>Nope</errorCode><success>false</success></result>"));
    }

    @Test
    public void testEventsProcessEventException() throws Exception {
        mainController.setoAuthSignatureService(new OAuthSignatureService() {
            @Override
            public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
                //all good
            }

            @Override
            public String requestURL(String consumerKey, String consumerSecret, URL endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
                return "";
            }
        });
        mvc.perform(get("/events").param("eventUrl","http:/event"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><errorCode>Premature end of file.</errorCode><success>false</success></result>"));
    }

    @Test
    public void testEventsProcessEventEventException() throws Exception {
        mainController.setoAuthSignatureService(new OAuthSignatureService() {
            @Override
            public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
                //all good
            }

            @Override
            public String requestURL(String consumerKey, String consumerSecret, URL endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
                return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyOrder.xml"));
            }
        });
        mainController.setUserService(new UserService() {
            @Override
            public void subscribeUser(AppDirectUser user) throws EventException {
                throw new EventException(null, new Result(user.getAccountIdentifier(), "ALREADY_SUBSCRIBED", null, false), HttpStatus.OK);
            }
        });
        mvc.perform(get("/events").param("eventUrl","http:/event"))
                .andExpect(status().isOk())
                .andExpect(content().string("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><errorCode>ALREADY_SUBSCRIBED</errorCode><success>false</success></result>"));
    }

    @Test
    public void testEvents() throws Exception {
        mainController.setoAuthSignatureService(new OAuthSignatureService() {
            @Override
            public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
                //all good
            }

            @Override
            public String requestURL(String consumerKey, String consumerSecret, URL endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
                return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyOrder.xml"));
            }
        });
        mvc.perform(get("/events").param("eventUrl","http:/event"))
                .andExpect(status().isOk())
                .andExpect(content().string("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><message>OK</message><success>true</success></result>"));
    }

    @Test
    public void testProcessEventWrongType() throws IOException {
        String xml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyOrder.xml"));
        xml = xml.replaceAll("SUBSCRIPTION_ORDER","WRONG_TYPE");
        try {
            mainController.processEvent(xml);
        } catch (Exception x) {
            assertEquals("java.lang.IllegalStateException: Unknown event type : WRONG_TYPE", x.toString());
        }
    }

    @Test
    public void testProcessEventWrongFormat() {
        try {
            mainController.processEvent("");
        } catch (Exception x) {
            assertEquals("org.xml.sax.SAXParseException; Premature end of file.", x.toString());
        }
    }

    @Test
    public void testProcessEventKnownType() throws IOException, EventException, SAXException {
        UserService userService = mock(UserService.class);
        mainController.setUserService(userService);
        mainController.processEvent(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyOrder.xml")));
        verify(userService).subscribeUser(any(AppDirectUser.class));
        Mockito.reset(userService);
        mainController.processEvent(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyChange.xml")));
        verify(userService).changeUser(any(AppDirectUser.class));
        Mockito.reset(userService);
        mainController.processEvent(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyCancel.xml")));
        verify(userService).unSubscribeUser(any(AppDirectUser.class));
        Mockito.reset(userService);
        mainController.processEvent(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyAssign.xml")));
        verify(userService).assignUser(any(AppDirectUser.class));
        Mockito.reset(userService);
        mainController.processEvent(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("dummyUnassign.xml")));
        verify(userService).unAssignUser(any(AppDirectUser.class));
    }

    public static class MockSecurityContext implements SecurityContext {

        private Authentication authentication;

        public MockSecurityContext(Authentication authentication) {
            this.authentication = authentication;
        }

        @Override
        public Authentication getAuthentication() {
            return this.authentication;
        }

        @Override
        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }
    }
}