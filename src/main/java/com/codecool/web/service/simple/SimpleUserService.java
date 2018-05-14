package com.codecool.web.service.simple;

import com.codecool.web.dao.UserDao;
import com.codecool.web.exceptions.EmptyFieldException;
import com.codecool.web.exceptions.NotFoundException;
import com.codecool.web.exceptions.ServiceException;
import com.codecool.web.model.User;
import com.codecool.web.service.UserService;

import java.sql.SQLException;

public class SimpleUserService implements UserService {

    private final UserDao uD;

    public SimpleUserService(UserDao uD) {
        this.uD = uD;
    }

    @Override
    public User getByName(String name) throws NotFoundException, SQLException, ServiceException {
        if (name.equals("")) {
            throw new ServiceException("You didn't enter a name");
        }
        if (uD.findByName(name) == null) {
            throw new ServiceException("User not found");
        }
        return uD.findByName(name);
    }

    @Override
    public User addNewUser(String name, String password, String role) throws SQLException, ServiceException {
        if (name.equals("") || password.equals("") || role.equals("") ) {
            throw new ServiceException(new EmptyFieldException("Fill all fields"));
        }

        return uD.insertNewUser(name,password,role);
    }
}
