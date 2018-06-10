package com.dyndyn.demo.service;

import com.dyndyn.demo.model.User;
import com.dyndyn.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByChatId(Long chatId){
        User user = userRepository.getByChatId(chatId);
        logger.info("User {}", user);
        return user;
    }

    public void addOrUpdate(User user){
        User dbUser = userRepository.getByChatId(user.getChatId());
        if (dbUser == null){
            userRepository.insert(user);
        } else {
            dbUser.setLatLng(user.getLatLng());
            dbUser.setLastPlaces(user.getLastPlaces());
            userRepository.update(dbUser);
        }
    }

    public void insert(User user){
        userRepository.insert(user);
    }

}
