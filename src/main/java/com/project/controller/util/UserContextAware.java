package com.project.controller.util;

import com.project.model.User;

public interface UserContextAware {
    void setUserContext(User user);
}