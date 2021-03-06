package com.oyome.pojo.vo;

import java.util.List;

/**
 * 二级分类VO
 */
public class CategoryVO {
    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;
    //三级分类vo list
    private List<SubCategoryVo> SubCatList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVo> getSubCatList() {
        return SubCatList;
    }

    public void setSubCatList(List<SubCategoryVo> subCatList) {
        SubCatList = subCatList;
    }
}
