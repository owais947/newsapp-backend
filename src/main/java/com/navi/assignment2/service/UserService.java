package com.navi.assignment2.service;

import com.navi.assignment2.Exception.EmailAlreadyExistsException;
import com.navi.assignment2.contract.request.UserRequest;
import com.navi.assignment2.entity.User;

public interface UserService {
    User save(UserRequest userRequest);

    void validateUser(UserRequest userRequest) throws Exception;

    void validateSources(UserRequest userRequest) throws Exception;

    User fetchUser(Long id);
}
