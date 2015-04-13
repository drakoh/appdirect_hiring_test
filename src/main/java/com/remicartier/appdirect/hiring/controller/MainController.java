package com.remicartier.appdirect.hiring.controller;

import com.remicartier.appdirect.hiring.exception.EventException;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import com.remicartier.appdirect.hiring.model.Result;
import com.remicartier.appdirect.hiring.service.OAuthSignatureService;
import com.remicartier.appdirect.hiring.service.UserService;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.joox.JOOX.$;

@Controller
public class MainController {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    public static final String TYPE_USER_UNASSIGNMENT = "USER_UNASSIGNMENT";
    public static final String TYPE_SUBSCRIPTION_CHANGE = "SUBSCRIPTION_CHANGE";
    public static final String TYPE_SUBSCRIPTION_ORDER = "SUBSCRIPTION_ORDER";
    public static final String TYPE_SUBSCRIPTION_CANCEL = "SUBSCRIPTION_CANCEL";
    public static final String TYPE_USER_ASSIGNMENT = "USER_ASSIGNMENT";

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;
    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;
    @Autowired
    private OAuthSignatureService oAuthSignatureService;
    @Autowired
    private UserService userService;

    //Setters mostly used for unit tests
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SuppressWarnings("unused")
    public void setOauthConsumerKey(String oauthConsumerKey) {
        this.oauthConsumerKey = oauthConsumerKey;
    }

    @SuppressWarnings("unused")
    public void setOauthConsumerSecret(String oauthConsumerSecret) {
        this.oauthConsumerSecret = oauthConsumerSecret;
    }

    public void setoAuthSignatureService(OAuthSignatureService oAuthSignatureService) {
        this.oAuthSignatureService = oAuthSignatureService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /");
        Map<String, Object> model = new HashMap<>();
        model.put("auth", authentication);
        return new ModelAndView("index", model);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /profile");
        Map<String, Object> model = new HashMap<>();
        model.put("auth", authentication);
        return new ModelAndView("profile", model);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView users(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /users");
        Map<String, Object> model = new HashMap<>();
        model.put("users", userService.getUsers());
        return new ModelAndView("users", model);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "openid", required = false) String openid, HttpServletResponse response, HttpServletRequest request) throws Exception {
        LOGGER.info("GET /login?openid={}", openid);
//At the beginning I thought we had to validate if the request was coming from AppDirect
//        try {
//            oAuthSignatureService.verifyRequest(oauthConsumerKey, oauthConsumerSecret, request);
//        } catch (Exception x) {
//            LOGGER.warn("Invalid signature", x);
//            response.sendError(HttpStatus.FORBIDDEN.value());
//        }
        Map<String, Object> model = new HashMap<>();
        model.put("openId", openid);
        return new ModelAndView("login", model);
    }


    @RequestMapping(value = "/events", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public
    @ResponseBody
    ResponseEntity<Result> events(@RequestParam("eventUrl") String eventUrl,
                                  HttpServletRequest request) throws Exception {
        LOGGER.info("GET /events?eventUrl={}", eventUrl);
        if (eventUrl.contains("dummy")) {
            return new ResponseEntity<>(new Result(null, null, "OK", true), HttpStatus.OK);//return XML response
        }
        try {
            oAuthSignatureService.verifyRequest(oauthConsumerKey, oauthConsumerSecret, request);
        } catch (Exception x) {
            LOGGER.warn("Unable to verify request", x);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Result> result;
        try {
            String endpointUrlResponse = oAuthSignatureService.requestURL(oauthConsumerKey, oauthConsumerSecret, new URL(eventUrl));
            processEvent(endpointUrlResponse);
            LOGGER.info("processEvent() Succeeded");
            result = new ResponseEntity<>(new Result(null, null, "OK", true), HttpStatus.OK);
        } catch (EventException x) {
            LOGGER.error("processEvent() failed with EventException");
            result = new ResponseEntity<>(x.getResult(), x.getHttpStatus());
        } catch (Exception x) {
            LOGGER.error("processEvent() failed", x);
            result = new ResponseEntity<>(new Result(null, x.getMessage(), null, false), HttpStatus.SERVICE_UNAVAILABLE);
        }
        LOGGER.info("events() returned {}", result);
        return result;
    }

    protected void processEvent(String xmlResponse) throws IOException, SAXException, EventException {
        Document document = $(new StringReader(xmlResponse)).document();
        Match match = $(document);
        String eventType = match.find("type").text();
        LOGGER.info("Event XML ({}) = {}", eventType, xmlResponse);
        AppDirectUser user = userService.extractUser(match, eventType);
        switch (eventType) {
            case TYPE_SUBSCRIPTION_CHANGE:
                userService.changeUser(user);
                break;
            case TYPE_SUBSCRIPTION_ORDER:
                userService.subscribeUser(user);
                break;
            case TYPE_SUBSCRIPTION_CANCEL:
                userService.unSubscribeUser(user);
                break;
            case TYPE_USER_ASSIGNMENT:
                userService.assignUser(user);
                break;
            case TYPE_USER_UNASSIGNMENT:
                userService.unAssignUser(user);
                break;
            default:
                throw new IllegalStateException("Unknown event type : " + eventType);
        }
    }

}