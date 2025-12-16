package com.zjsu.yyd.userservice.service;

import com.zjsu.yyd.userservice.model.User;
import com.zjsu.yyd.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserService {

    private final UserRepository userRepository;

    /** 密码盐值 */
    private static final String SALT = "com.acg";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 用户注册
     */
    public boolean register(String username, String password) {

        if (userRepository.findByUserNameAndIsDeletedFalse(username).isPresent()) {
            return false;
        }

        String encryptedPassword =
                DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user = new User();
        user.setUserName(username);
        user.setUserPassword(encryptedPassword);

        userRepository.save(user);
        return true;
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {

        String encryptedPassword =
                DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        return userRepository.findByUserNameAndIsDeletedFalse(username)
                .map(user -> user.getUserPassword().equals(encryptedPassword))
                .orElse(false);
    }

    /**
     * 软删除用户
     */
    public boolean delete(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsDeleted(true);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
    /**
     * 根据Id查找用户
     */
    public User getById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsDeleted())
                .orElse(null);
    }
}
