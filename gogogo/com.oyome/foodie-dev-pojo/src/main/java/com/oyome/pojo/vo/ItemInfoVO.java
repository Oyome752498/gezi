package com.oyome.pojo.vo;

import com.oyome.pojo.Items;
import com.oyome.pojo.ItemsImg;
import com.oyome.pojo.ItemsParam;
import com.oyome.pojo.ItemsSpec;

import java.util.Date;
import java.util.List;

/**
 * 商品详情VO
 */
public class ItemInfoVO {
  private Items item;
  private  List<ItemsImg> itemImgList;
  private List<ItemsSpec> itemSpecList;
  private ItemsParam itemParams;

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public List<ItemsImg> getItemImgList() {
        return itemImgList;
    }

    public void setItemImgList(List<ItemsImg> itemImgList) {
        this.itemImgList = itemImgList;
    }

    public List<ItemsSpec> getItemSpecList() {
        return itemSpecList;
    }

    public void setItemSpecList(List<ItemsSpec> itemSpecList) {
        this.itemSpecList = itemSpecList;
    }

    public ItemsParam getItemParams() {
        return itemParams;
    }

    public void setItemParams(ItemsParam itemParams) {
        this.itemParams = itemParams;
    }
}
