package cn.asiawu.service.impl;

import cn.asiawu.common.User;
import cn.asiawu.service.UserService;

/**
 * @author asiawu
 * @date 2023/06/24 21:27
 * @description:
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(String id) {
        User user=new User();
        user.setId(id);
        user.setAge(18);
        user.setUsername("asiaWu3");
        return user;
    }
}
