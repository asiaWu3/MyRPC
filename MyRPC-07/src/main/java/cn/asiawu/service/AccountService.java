package cn.asiawu.service;

/**
 * @author asiawu
 * @date 2023/06/25 00:50
 * @description:
 */
public interface AccountService {
    /**
     * 根据userId获取用户的余额
     * @param id
     * @return
     */
    double getMoneyById(String id);
}
