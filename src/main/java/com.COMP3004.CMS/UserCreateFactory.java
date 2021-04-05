package com.COMP3004.CMS;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public abstract class UserCreateFactory {

    public abstract User createUser(String username, String password, String role, int id, String birthdate, String gender, String firstname, String lastname);


}
