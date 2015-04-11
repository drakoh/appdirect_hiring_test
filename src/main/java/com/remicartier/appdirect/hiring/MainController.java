package com.remicartier.appdirect.hiring;

import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

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
        LOGGER.info("GET /events?eventUrl=", eventUrl);
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
        Document document = $(new StringReader(xmlResponse)).document();
        Match match = $(document);
        AppDirectUser user = extractUser(match);
        String eventType = match.find("type").text();
        switch (eventType) {
            case "SUBSCRIPTION_CHANGE":
                userService.changeUser(user);
                break;
            case "SUBSCRIPTION_ORDER":
                userService.subscribeUser(user);
                break;
            case "SUBSCRIPTION_CANCEL":
                userService.unSubscribeUser(user);
                break;
            case "USER_ASSIGNMENT":
                userService.assignUser(user);
                break;
            case "USER_UNASSIGNMENT":
                userService.unAssignUser(user);
                break;
        }
    }

    protected static AppDirectUser extractUser(Match documentMatch) {
        AppDirectUser user = new AppDirectUser();
        Match userMatch = documentMatch.find("user");
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

//    public static void main(String[] args) throws IOException, SAXException {
//        String xml = "<event xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
//                "<type>USER_UNASSIGNMENT</type>\n" +
//                "<marketplace>\n" +
//                "<baseUrl>https://acme.appdirect.com</baseUrl>\n" +
//                "<partner>ACME</partner>\n" +
//                "</marketplace>\n" +
//                "<flag>STATELESS</flag>\n" +
//                "<creator>\n" +
//                "<email>test-email+creator@appdirect.com</email>\n" +
//                "<firstName>DummyCreatorFirst</firstName>\n" +
//                "<language>fr</language>\n" +
//                "<lastName>DummyCreatorLast</lastName>\n" +
//                "<openId>\n" +
//                "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
//                "</openId>\n" +
//                "<uuid>ec5d8eda-5cec-444d-9e30-125b6e4b67e2</uuid>\n" +
//                "</creator>\n" +
//                "<payload>\n" +
//                "<account>\n" +
//                "<accountIdentifier>dummy-account</accountIdentifier>\n" +
//                "<status>ACTIVE</status>\n" +
//                "</account>\n" +
//                "<configuration/>\n" +
//                "<user>\n" +
//                "<email>test-email@appdirect.com</email>\n" +
//                "<firstName>DummyFirst</firstName>\n" +
//                "<language>fr</language>\n" +
//                "<lastName>DummyLast</lastName>\n" +
//                "<openId>\n" +
//                "https://www.appdirect.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2\n" +
//                "</openId>\n" +
//                "<uuid>ec5d8eda-5cec-444d-9e30-125b6e4b67e2</uuid>\n" +
//                "</user>\n" +
//                "</payload>\n" +
//                "</event>";
//        Document document = $(new StringReader(xml)).document();
//        Match match = $(document);
//        AppDirectUser user = extractUser(match);
//        System.out.println(user);
//    }
}