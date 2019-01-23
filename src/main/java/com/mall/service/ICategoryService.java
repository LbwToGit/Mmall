package com.mall.service;

import com.mall.common.ServerResponse;

import java.util.List;

/**
 * Created by Cxl on 2018/12/17-15:52
 * Description: Mmall
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parendId);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse getChildrenParalleCategory(Integer categoryId);

    ServerResponse <List<Integer>>selectCategoryAndChildrenById(Integer categoryId);
}
