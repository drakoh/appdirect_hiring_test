package com.remicartier.appdirect.hiring;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class OAuthSignatureService {
    private final static Logger LOGGER = Logger.getLogger(OAuthSignatureService.class);
    public static final String HEADER_X_FORWARDED_PROTO = "x-forwarded-proto";

    public String requestURL(String consumerKey, String consumerSecret, String endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        URL url = new URL(endpointURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        consumer.sign(request);
        request.connect();

        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, request.getContentEncoding());
        return writer.toString();
    }

    public void verifyRequest(String consumerKey, String consumerSecret, HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
        String requestURL = getEndpointURL(request);

        net.oauth.OAuthMessage message = net.oauth.server.OAuthServlet.getMessage(request, requestURL);
        message.requireParameters(OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE);

        net.oauth.OAuthConsumer consumer = new net.oauth.OAuthConsumer(null, consumerKey, consumerSecret, null);
        net.oauth.OAuthAccessor accessor = new net.oauth.OAuthAccessor(consumer);
        net.oauth.signature.OAuthSignatureMethod.newSigner(message, accessor).validate(message);
    }

    protected String getEndpointURL(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        boolean isRequestBehindProxy = Collections.list(request.getHeaderNames()).contains(HEADER_X_FORWARDED_PROTO);

        String containerEndpointURLString = net.oauth.server.OAuthServlet.getRequestURL(request);
        if (isRequestBehindProxy) {
            URL endpointURL = new URL(containerEndpointURLString);
            String host = request.getHeader("host");
            String scheme = request.getHeader("x-forwarded-proto");
            URI proxyEndpointURL = new URI(scheme, host, endpointURL.getPath(), null);
            return proxyEndpointURL.toString();
        } else {
            return containerEndpointURLString;
        }
    }

}