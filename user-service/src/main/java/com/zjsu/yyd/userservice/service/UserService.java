package com.zjsu.yyd.userservice.service;

import com.zjsu.yyd.userservice.model.User;
import com.zjsu.yyd.userservice.repository.UserRepository;
import com.zjsu.yyd.userservice.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String SALT = "com.acg";

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /** 注册 */
    public boolean register(String username, String password) {
        if (userRepository.findByUserNameAndIsDeletedFalse(username).isPresent()) {
            return false;
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(encryptedPassword);

        userRepository.save(user);
        return true;
    }

    /** 登录并返回 JWT token，失败返回 null */
//    public String loginAndGetToken(String username, String password) {
//        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
//
//        boolean valid = userRepository.findByUserNameAndIsDeletedFalse(username)
//                .map(user -> user.getUserPassword().equals(encryptedPassword))
//                .orElse(false);
//
//        if (!valid) return null;
//
//        // 登录成功，生成 token
//        return jwtUtil.generateToken(username);
//    }
    public String loginAndGetToken(String username, String password) {
        String encryptedPassword =
                DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        return userRepository.findByUserNameAndIsDeletedFalse(username)
                .filter(user -> user.getUserPassword().equals(encryptedPassword))
                .map(user -> jwtUtil.generateToken(user.getId(), user.getUserName()))
                .orElse(null);
    }


    /** 软删除 */
    public boolean delete(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsDeleted(true);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    /** 根据 ID 查询用户 */
    public User getById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsDeleted())
                .orElse(null);
    }
}
