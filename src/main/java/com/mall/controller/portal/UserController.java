package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Cxl on 2018/12/15-14:44
 * Description: Mmall
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username, String password, HttpSession session, HttpServletResponse response,HttpServletRequest request){
        ServerResponse<User> serverResponse=iUserService.login(username,password);
        if (serverResponse.isSuccess()){
//            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
            //将key保存到Cookie中，这里session.getId()相当于一个Redis中的一个Key，也可以自己生成UUID作为key
            CookieUtil.writeLoginToken(response,session.getId());
            //将用户信息保存到Redis中，并设置过期时间
            RedisShardedPoolUtil.setEx(session.getId(),JsonUtil.obj2String(serverResponse.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return serverResponse;
    }
    @RequestMapping(value = "logout.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>logout(HttpServletRequest request,HttpServletResponse response){
        String loginToken=CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>register(User user){
         return iUserService.register(user);
    }
    @RequestMapping(value = "check_valid.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>getUserInfo(HttpServletRequest request){
//        User user=(User)session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(request);
        if (loginToken==null){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");

    }
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetGetQuestion(String username, HttpServletRequest request){
            return iUserService.selectQuestion(username);
    }
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetCheckAnser(String username,String answer,String question){
        return iUserService.checkAnser(username,answer,question);
    }
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>resetPassword(HttpServletRequest request,String passwordOld,String passwordNew){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(request);
        if (loginToken==null){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }


    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>update_information(HttpServletRequest request,User user,HttpSession session){
//        User currentUser=(User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(request);
        if (loginToken==null){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.string2Obj(userJsonStr,User.class);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        ServerResponse updateResponse=iUserService.resetPassword(currentUser.getPassword(),user.getPassword(),user);
        if (updateResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,updateResponse.getData());
        }
        return  updateResponse;
    }
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>get_information(HttpServletRequest request){
//        User currentUser=(User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(request);
        if (loginToken==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(user.getId());
    }
}
