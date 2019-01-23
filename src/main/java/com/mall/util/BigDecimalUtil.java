package com.mall.util;

import java.math.BigDecimal;

/**
 * Created by Cxl on 2018/12/20-16:32
 * Description: Mmall
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){ }//防止被实例化
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }
    //减
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    //乘
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    //除
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留2位
    }
}
