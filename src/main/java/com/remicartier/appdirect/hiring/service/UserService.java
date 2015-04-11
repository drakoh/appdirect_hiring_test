package com.remicartier.appdirect.hiring.service;

import com.remicartier.appdirect.hiring.model.AppDirectUser;
import com.remicartier.appdirect.hiring.model.Result;
import com.remicartier.appdirect.hiring.exception.EventException;
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
        if (dbService.doesUserExist(user)) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ALREADY_SUBSCRIBED", null, false), HttpStatus.OK);
        }
        try {
            dbService.addUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "ALREADY_SUBSCRIBED", null, false), HttpStatus.OK);
        }
    }

    public void unSubscribeUser(AppDirectUser user) throws EventException {
        if (!dbService.doesUserExist(user)) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
        try {
            dbService.deleteUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
    }

    public void changeUser(AppDirectUser user) throws EventException {
        if (!dbService.doesUserExist(user)) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
        try {
            dbService.updateUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "ACCOUNT_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
    }

    public void assignUser(AppDirectUser user) throws EventException {
        if (dbService.doesUserExist(user)) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "ALREADY_ASSIGNED", null, false), HttpStatus.OK);
        }
        try {
            dbService.addUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "ALREADY_ASSIGNED", null, false), HttpStatus.OK);
        }
    }

    public void unAssignUser(AppDirectUser user) throws EventException {
        if (!dbService.doesUserExist(user)) {
            throw new EventException(null, new Result(user.getAccountIdentifier(), "USER_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
        try {
            dbService.deleteUser(user);
        } catch (Exception x) {
            throw new EventException(x, new Result(user.getAccountIdentifier(), "USER_NOT_FOUND", "Unable to remove user", false), HttpStatus.OK);
        }
    }

    public List<AppDirectUser> getUsers() {
        return dbService.getUsers();
    }
}
