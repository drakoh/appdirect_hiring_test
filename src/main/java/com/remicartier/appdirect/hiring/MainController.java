package com.remicartier.appdirect.hiring;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class MainController {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final static String OAUTH_CONSUMER_KEY = "hiring-test-21055";
    private final static String OAUTH_CONSUMER_SECRET = "1u1vBleXa5DNtsXf";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(@ModelAttribute("model") ModelMap model) {
        LOGGER.info("GET /");
        model.addAttribute("userList", null);
        return "index";
    }

    @RequestMapping(value = "/login/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<String> login(@ModelAttribute("model") ModelMap model, @PathVariable("id") String id) throws Exception {
        LOGGER.info("GET /login/{}", id);
        OAuthConsumer consumer = new DefaultOAuthConsumer(OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
        URL url = new URL(id);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        consumer.sign(request);
        request.connect();
        if (request.getResponseCode() != HttpStatus.OK.value()) {
            LOGGER.warn("Login failed, response code = {}", request.getResponseCode());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Login was successful");
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/events/{eventUrl}", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<Result> events(@PathVariable("eventUrl") String eventUrl, @RequestHeader(value = "Authorization", required = false) String oAuthSignature) {
        LOGGER.info("GET /events/{} (Auth:{})", eventUrl, oAuthSignature);
        //TODO:must validate it actually comes from AppDirect
        if (StringUtils.isEmpty(oAuthSignature)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String[] parts = oAuthSignature.split(",");
        return new ResponseEntity<>(new Result(), HttpStatus.OK);//return XML response
    }
}