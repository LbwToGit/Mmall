package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import com.mall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Cxl on 2018/12/15-14:49
 * Description: Mmall
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse login(String username, String password) {
        int resultCount=userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        // 密码登陆MD5,数据库中保存的密码是被MD5加密过的
        //所以要转换成MD5进行比较
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5Password);
        if (user==null){
            return ServerResponse.createByErrorMessage("密码输入不正确");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user,"登陆成功");
    }
    @Override
    public ServerResponse<String> register(User user){
        //调用校验方法，校验是否已经存在该username以及email
        ServerResponse validResponse=this.checkValid(user.getEmail(),"email");
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse=this.checkValid(user.getUsername(),"username");
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //校验结束
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5密码加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //保存
        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }
    public ServerResponse<String>checkValid(String str,String type){ if (StringUtils.isNotBlank(type)){   //判断参数是否为空
            //开始校验
            if (Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("Email已存在");
                }
            }
            if (Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
    @Override
    public ServerResponse<String>selectQuestion(String username)
    {
        ServerResponse validResponse=this.checkValid(username,"username");
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.selectQuestionByName(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("用户没有设置找回密码的问题");
    }
    //使用本地缓存检查问题答案
    @Override
    public ServerResponse<String>checkAnser(String username,String answer,String question){
        int resultCount=userMapper.checkAnser(username,answer,question);
        if (resultCount>0){
            String forgetToekn=UUID.randomUUID().toString();
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX +username,forgetToekn);
            //缓存迁移到Redis缓存
            //Redis缓存有效时间为12小时60*60*12
            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToekn,60*60*12);
            return ServerResponse.createBySuccess(forgetToekn);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }
    @Override
    public ServerResponse<String>forgetResetPassword(String username,String passwordNew,String forgetToken){
        if (StringUtils.isNotBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
        if (StringUtils.isNotBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        //如果比较的2个字符串有一个为null,使用StringUtils.equals不会抛出空指针异常
        if (StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount=userMapper.updatePasswordByUsername(username,md5Password);
            if (resultCount>0){
                return ServerResponse.createBySuccessMessage("修改成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }
    @Override
    public ServerResponse<String>resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，校验用户旧密码，必须指定id
        int resultCount=userMapper.checkPassword(user.getId(),passwordOld);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if (updateCount==0){
            return ServerResponse.createByErrorMessage("密码更新失败");
        }
        return ServerResponse.createBySuccessMessage("密码更新成功");
    }
    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //检查更新的邮箱是否重复
        int resultCount=userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if (resultCount==0){
            return  ServerResponse.createByErrorMessage("email已经存在,请更换email在尝试更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount==0){
            return ServerResponse.createByErrorMessage("更新个人信息失败");
        }
        return ServerResponse.createBySuccess(updateUser,"更新个人信息成功");
    }
    public ServerResponse<User>getInformation(int id){
        User user=userMapper.selectByPrimaryKey(id);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}



