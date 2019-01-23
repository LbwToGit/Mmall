package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.User;

/**
 * Created by Cxl on 2018/12/15-14:43
 * Description: Mmall
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String>checkValid(String str,String type);
    ServerResponse<String>selectQuestion(String username);
    ServerResponse<String>checkAnser(String username,String answer,String question);
    ServerResponse<String>forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String>resetPassword(String passwordOld,String passwordNew,User user);
    ServerResponse<User>getInformation(int id);
    ServerResponse checkAdminRole(User user);
}
