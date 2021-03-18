package com.COMP3004.CMS;

public class UserCreateFactory {
    protected User createUser() {
        return null;
    }

    public class StudentCreate extends UserCreateFactory {
        protected User createUser() {
            return null;
        }
    }

    public class ProfessorCreate extends UserCreateFactory {
        protected User createUser() {
            return null;
        }
    }

    public class AdminCreate extends UserCreateFactory {
        protected User createUser() {
            return null;
        }
    }

}
