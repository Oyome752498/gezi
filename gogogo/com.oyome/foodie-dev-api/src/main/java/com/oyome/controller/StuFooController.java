package com.oyome.controller;

import com.oyome.pojo.Stu;
import com.oyome.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class StuFooController {
    @Autowired
    private StuService stuService;
    @GetMapping("/stu")
    public Stu getStuInfo(@RequestParam("id") int id){
        return stuService.getStuInfo(id);
    }
    @PostMapping("/savestu")
    public void saveStu(){
         stuService.saveStu();
    }

    @PostMapping("/updatestu")
    public void updateStu(@RequestParam("id")int id){
        stuService.updateStu(id);
    }

    @PostMapping("/deletestu")
    public void deleteStu(@RequestParam("id")int id){
        stuService.deleteStu(id);
    }
}
