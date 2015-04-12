package com.remicartier.appdirect.hiring.service;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class OAuthSignatureService {
    public static final String HEADER_X_FORWARDED_PROTO = "x-forwarded-proto";
    public static final String HEADER_HOST = "host";

    public String requestURL(String consumerKey, String consumerSecret, URL endpointURL) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        HttpURLConnection request = (HttpURLConnection) endpointURL.openConnection();
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
            String host = request.getHeader(HEADER_HOST);
            String scheme = request.getHeader(HEADER_X_FORWARDED_PROTO);
            URI proxyEndpointURL = new URI(scheme, host, endpointURL.getPath(), null);
            return proxyEndpointURL.toString();
        } else {
            return containerEndpointURLString;
        }
    }

}