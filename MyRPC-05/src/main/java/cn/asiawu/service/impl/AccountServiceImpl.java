package cn.asiawu.service.impl;

import cn.asiawu.service.AccountService;

import java.util.Random;

/**
 * @author asiawu
 * @date 2023/06/25 00:51
 * @description: AccountService实现类
 */
public class AccountServiceImpl implements AccountService {
    @Override
    public double getMoneyById(String id) {
        return new Random().nextDouble()*1000.0;
    }
}
