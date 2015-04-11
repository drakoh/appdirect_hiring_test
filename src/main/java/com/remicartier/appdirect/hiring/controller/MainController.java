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
import java.util.HashMap;
import java.util.Map;

import static org.joox.JOOX.$;

@Controller
public class MainController {
    public static final String TYPE_USER_UNASSIGNMENT = "USER_UNASSIGNMENT";
    private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /");
        Map<String,Object> model = new HashMap<>();
        model.put("auth", authentication);
        return new ModelAndView("index", model);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /profile");
        Map<String,Object> model = new HashMap<>();
        model.put("auth", authentication);
        return new ModelAndView("profile", model);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView users(OpenIDAuthenticationToken authentication) {
        LOGGER.info("GET /users");
        Map<String,Object> model = new HashMap<>();
        model.put("users", userService.getUsers());
        return new ModelAndView("users", model);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "openid", required = false) String openid, HttpServletResponse response, HttpServletRequest request) throws Exception {
        LOGGER.info("GET /login?openid={}", openid);
//        try {
//            oAuthSignatureService.verifyRequest(oauthConsumerKey, oauthConsumerSecret, request);
//        } catch (Exception x) {
//            LOGGER.warn("Invalid signature", x);
//            response.sendError(HttpStatus.FORBIDDEN.value());
//        }
        Map<String,Object> model = new HashMap<>();
        model.put("openId", openid);
        return new ModelAndView("login",model);
    }


    @RequestMapping(value = "/events", method = RequestMethod.GET)
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
        String endpointUrlResponse = oAuthSignatureService.requestURL(oauthConsumerKey, oauthConsumerSecret, eventUrl);
        try {
            processEvent(endpointUrlResponse);
            LOGGER.info("processEvent() Succeeded");
            return new ResponseEntity<>(new Result(), HttpStatus.OK);//return XML response
        } catch (EventException x) {
            LOGGER.error("processEvent() failed", x);
            return new ResponseEntity<>(x.getResult(), x.getHttpStatus());
        } catch (Exception x) {
            LOGGER.error("processEvent() failed", x);
            return new ResponseEntity<>(new Result(), HttpStatus.SERVICE_UNAVAILABLE);//return XML response
        }
    }

    protected void processEvent(String xmlResponse) throws IOException, SAXException, EventException {
        LOGGER.info("XMLResponse = {}", xmlResponse);
        Document document = $(new StringReader(xmlResponse)).document();
        Match match = $(document);
        String eventType = match.find("type").text();
        AppDirectUser user = extractUser(match, eventType);
        if (TYPE_SUBSCRIPTION_CHANGE.equals(eventType)) {
            userService.changeUser(user);
        } else if (TYPE_SUBSCRIPTION_ORDER.equals(eventType)) {
            userService.subscribeUser(user);
        } else if (TYPE_SUBSCRIPTION_CANCEL.equals(eventType)) {
            userService.unSubscribeUser(user);
        } else if (TYPE_USER_ASSIGNMENT.equals(eventType)) {
            userService.assignUser(user);
        } else if (TYPE_USER_UNASSIGNMENT.equals(eventType)) {
            userService.unAssignUser(user);
        } else {
            throw new IllegalStateException("Unknown event type : "+eventType);
        }
    }

    protected static AppDirectUser extractUser(Match documentMatch, String eventType) {
        AppDirectUser user = new AppDirectUser();
        Match userMatch;
        if (TYPE_USER_ASSIGNMENT.equals(eventType) || TYPE_USER_UNASSIGNMENT.equals(eventType)) {
            userMatch = documentMatch.find("user");
        } else {
            userMatch = documentMatch.find("creator");
        }
        user.setEmail(userMatch.find("email").text());
        user.setFirstName(userMatch.find("firstName").text());
        user.setLastName(userMatch.find("lastName").text());
        user.setLanguage(userMatch.find("language").text());
        user.setOpenId(userMatch.find("openId").text());
        user.setUuid(userMatch.find("uuid").text());
        Match accountMatch = documentMatch.find("account");
        user.setAccountIdentifier(accountMatch.find("accountIdentifier").text());
        return user;
    }
}