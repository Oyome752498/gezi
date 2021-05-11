package com.oyome.pojo.vo;

import com.oyome.pojo.bo.ShopcartBO;

import java.util.List;

public class OrderVO {
    private String orderId;         // 商户订单号
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopCartBo;//创建订单后需要删除的购物车信息


    public List<ShopcartBO> getToBeRemovedShopCartBo() {
        return toBeRemovedShopCartBo;
    }

    public void setToBeRemovedShopCartBo(List<ShopcartBO> toBeRemovedShopCartBo) {
        this.toBeRemovedShopCartBo = toBeRemovedShopCartBo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}
