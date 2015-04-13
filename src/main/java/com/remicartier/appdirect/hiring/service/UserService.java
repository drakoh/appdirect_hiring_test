package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.controller.MainController;
import com.remicartier.appdirect.hiring.model.AppDirectUser;
import com.remicartier.appdirect.hiring.model.Result;
import com.remicartier.appdirect.hiring.exception.EventException;
import org.joox.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 12:59 PM
 */
@Service
public class UserService {
    @Autowired
    private DBService dbService;

    public void subscribeUser(AppDirectUser user) throws EventException {
        AppDirectUser dbUser = dbService.getUserByOpenID(user.getOpenId());
        if (dbUser != null) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "USER_ALREADY_EXISTS", null, false), HttpStatus.OK);
        }
        try {
            dbService.addUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "INVALID_RESPONSE", "Unable to subscribe user", false), HttpStatus.OK);
        }
    }

    public void unSubscribeUser(AppDirectUser user) throws EventException {
        AppDirectUser dbUser = dbService.getUserByOpenID(user.getOpenId());
        if (dbUser == null) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to unsubscribe user", false), HttpStatus.OK);
        }
        try {
            dbService.deleteUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "INVALID_RESPONSE", "Unable to unsubscribe user", false), HttpStatus.OK);
        }
    }

    public void changeUser(AppDirectUser user) throws EventException {
        AppDirectUser dbUser = dbService.getUserByOpenID(user.getOpenId());
        if (dbUser == null) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
        try {
            dbService.updateUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "INVALID_RESPONSE", "Unable to remove user", false), HttpStatus.OK);
        }
    }

    public void assignUser(AppDirectUser user) throws EventException {
        AppDirectUser dbUser = dbService.getUserByOpenID(user.getOpenId());
        if (dbUser != null) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ALREADY_ASSIGNED", null, false), HttpStatus.OK);
        }
        try {
            dbService.addUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "INVALID_RESPONSE", null, false), HttpStatus.OK);
        }
    }

    public void unAssignUser(AppDirectUser user) throws EventException {
        AppDirectUser dbUser = dbService.getUserByOpenID(user.getOpenId());
        if (dbUser == null) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "USER_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
        if (dbUser.isAdmin()) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "UNAUTHORIZED", "Unable to remove admin user", false), HttpStatus.OK);
        }
        try {
            dbService.deleteUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "INVALID_RESPONSE", "Unable to remove user", false), HttpStatus.OK);
        }
    }

    public List<AppDirectUser> getUsers() {
        return dbService.getUsers();
    }

    public AppDirectUser extractUser(Match documentMatch, String eventType) {
        AppDirectUser user = new AppDirectUser();
        Match userMatch;
        if (MainController.TYPE_USER_ASSIGNMENT.equals(eventType) || MainController.TYPE_USER_UNASSIGNMENT.equals(eventType)) {
            userMatch = documentMatch.find("user");
        } else {
            userMatch = documentMatch.find("creator");
            user.setAdmin(true);
        }
        user.setEmail(userMatch.find("email").text());
        user.setFirstName(userMatch.find("firstName").text());
        user.setLastName(userMatch.find("lastName").text());
        user.setLanguage(userMatch.find("language").text());
        user.setOpenId(userMatch.find("openId").text());
        user.setUuid(userMatch.find("uuid").text());
        Match accountMatch = documentMatch.find("account");
        user.setAccountIdentifier(accountMatch.find("accountIdentifier").text());
        user.setCompany(documentMatch.find("company").find("name").text());
        return user;
    }
}
