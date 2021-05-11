package com.oyome.service.center;

import com.github.pagehelper.PageInfo;
import com.oyome.utils.PagedGridResult;

import java.util.List;

public class BaseService {
    public PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
