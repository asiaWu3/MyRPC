package cn.asiawu.service;

import cn.asiawu.common.User;

/**
 * @author asiawu
 * @date 2023/06/24 21:26
 * @description: UserService接口
 */
public interface UserService {
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User getUserById(String id);
}
