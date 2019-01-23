package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by Cxl on 2018/12/17-15:52
 * Description: Mmall
 */
@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse addCategory(String categoryName,Integer parendId){
        if (parendId==null||StringUtils.isNotBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category=new Category();
        category.setParentId(parendId);
        category.setName(categoryName);
        category.setStatus(true);//标记状态为可用

        int resultCount=categoryMapper.insert(category);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if (categoryId==null||StringUtils.isNotBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount==0) {
            return ServerResponse.createByErrorMessage("更新品类名称失败");
        }
        return ServerResponse.createBySuccessMessage("更新品类名称成功");
    }

    public ServerResponse getChildrenParalleCategory(Integer categoryId){
        List<Category>categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id以及子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse <List<Integer>>selectCategoryAndChildrenById(Integer categoryId){
        Set<Category>categorySet=Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer>categoryIdList=Lists.newArrayList();
        if (categoryId!=null){
            for (Category category:categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    /**
     *
     * @param categorySet
     * @param categoryId  子节点的categoryId
     * @return
     */
    //递归算法，算出子节点
    private Set<Category>findChildCategory(Set<Category>categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){  //不为空则添加到set中
            categorySet.add(category);
        }
        List<Category>categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //如果当前子节点没有子节点(即categoryList为null)，则不进入for循环，直接返回categorySet并结束递归
        for (Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}
