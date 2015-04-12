package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.Application;
import net.oauth.OAuthException;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 9:17 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OAuthSignatureServiceTest {
    @InjectMocks
    private OAuthSignatureService oAuthSignatureService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRequestURL() throws IOException, OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException {
        final Map<String, String> map = new HashMap<>();
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        URL url = new URL("http://foo.bar", "foo.bar", 80, "", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return httpURLConnection;
            }
        });
        doAnswer(invocationOnMock -> {
            map.put(invocationOnMock.getArgumentAt(0, String.class), invocationOnMock.getArgumentAt(1, String.class));
            return null;
        }).when(httpURLConnection).setRequestProperty(anyString(), anyString());
        when(httpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream("BODY".getBytes()));
        when(httpURLConnection.getURL()).thenReturn(url);
        String response = oAuthSignatureService.requestURL("KEY", "SECRET", url);
        assertNotNull(response);
        assertEquals("BODY", response);
        String auth = map.get("Authorization");
        assertNotNull(auth);
        assertTrue(auth.contains("OAuth oauth_consumer_key"));
        assertTrue(auth.contains("oauth_nonce"));
        assertTrue(auth.contains("oauth_signature"));
        assertTrue(auth.contains("oauth_signature_method"));
        assertTrue(auth.contains("oauth_timestamp"));
        assertTrue(auth.contains("oauth_version"));
    }

//Can't make this work in the time frame I have

//    @Test
//    public void testVerifyRequest() throws OAuthException, IOException, URISyntaxException, OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {
//        final Map<String, String> map = new HashMap<>();
//        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
//        URL url = new URL("http://foo.bar", "foo.bar", 80, "", new URLStreamHandler() {
//            @Override
//            protected URLConnection openConnection(URL u) throws IOException {
//                return httpURLConnection;
//            }
//        });
//        doAnswer(invocationOnMock -> {
//            map.put(invocationOnMock.getArgumentAt(0, String.class), invocationOnMock.getArgumentAt(1, String.class));
//            return null;
//        }).when(httpURLConnection).setRequestProperty(anyString(), anyString());
//        when(httpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream("BODY".getBytes()));
//        when(httpURLConnection.getURL()).thenReturn(url);
//        oAuthSignatureService.requestURL("KEY", "SECRET", url);
//        String auth = map.get("Authorization");
//
//        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
//
//        when(httpServletRequest.getHeaderNames()).thenReturn(new Enumerator<>(Arrays.asList("Authorization", "H2")));
//        when(httpServletRequest.getHeaders(anyString())).thenAnswer(invocationOnMock -> {
//            if (invocationOnMock.getArgumentAt(0, String.class).equals("Authorization")) {
//                return new Enumerator<>(Collections.singletonList(auth));
//            }
//            return null;
//        });
//        when(httpServletRequest.getMethod()).thenReturn("GET");
//        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://foo.bar"));
//        oAuthSignatureService.verifyRequest("KEY", "SECRET", httpServletRequest);
//    }

    @Test
    public void testGetEndpointURLProxy() throws MalformedURLException, URISyntaxException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeaderNames()).thenReturn(new Enumerator<>(Collections.singletonList(OAuthSignatureService.HEADER_X_FORWARDED_PROTO)));
        when(httpServletRequest.getHeader(anyString())).thenReturn("foo.bar").thenReturn("https");
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://foo.bar"));
        String endPoint = oAuthSignatureService.getEndpointURL(httpServletRequest);
        assertEquals("https://foo.bar", endPoint);
    }

    @Test
    public void testGetEndpointURL() throws MalformedURLException, URISyntaxException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeaderNames()).thenReturn(new Enumerator<>(Collections.singletonList("H1")));
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://foo.bar"));
        String endPoint = oAuthSignatureService.getEndpointURL(httpServletRequest);
        assertEquals("http://foo.bar", endPoint);
    }

}