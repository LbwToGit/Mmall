package com.mall.controller.backend;

import com.mall.common.ServerResponse;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Cxl on 2018/12/17-15:01
 * Description: Mmall
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId",defaultValue ="0")int parendId){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String loginToken=CookieUtil.readLoginToken(request);
//        if (loginToken==null){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
//        }
//        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.string2Obj(userJsonStr,User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
//        }
//        //校验是否是管理员
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //是管理员
//            //增加处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parendId);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
//        }
    }
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest request,Integer categoryId,String categoryName){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String loginToken=CookieUtil.readLoginToken(request);
//        if (loginToken==null){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
//        }
//        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.string2Obj(userJsonStr,User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
//        }
    }
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParalleCategory(HttpServletRequest request, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String loginToken=CookieUtil.readLoginToken(request);
//        if (loginToken==null){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
//        }
//        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.string2Obj(userJsonStr,User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParalleCategory(categoryId);
//        }else {
//        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
//        }
    }
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
//           User user=(User) session.getAttribute(Const.CURRENT_USER);
//        String loginToken=CookieUtil.readLoginToken(request);
//        if (loginToken==null){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
//        }
//        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.string2Obj(userJsonStr,User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //查询当前节点的ID,和递归子节点的ID
//            //0->10000->100000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
//        }
    }
}
