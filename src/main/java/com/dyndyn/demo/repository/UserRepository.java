package com.dyndyn.demo.repository;


import com.dyndyn.demo.model.User;

public interface UserRepository {

    User getByChatId(Long chatId);
    void update(User user);
    void insert(User user);
    void delete(User user);

}
