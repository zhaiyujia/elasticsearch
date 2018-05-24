package com.es.controller;

import com.es.entity.EsUserInfo;
import com.es.service.UserService;
import io.searchbox.client.JestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhai
 * @date 2018/5/24 15:05
 */
@RestController
@RequestMapping("/EsUser")
public class UserController {

    @Autowired
    private UserService userService;



    @PostMapping("saveConfig")
    public boolean saveConfig( HttpServletRequest request){
        JestResult jestResult = null;
        try {
            userService.userSettingConfig();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/saveUser")
    public boolean savePress(HttpServletRequest request){
        List<EsUserInfo> sysCompuserArrayList = new ArrayList<EsUserInfo>();
        sysCompuserArrayList.add(new EsUserInfo(1,"作者1","123456789123"));
        sysCompuserArrayList.add(new EsUserInfo(2,"1做这","123456789124"));
        sysCompuserArrayList.add(new EsUserInfo(3,"开心就好","123456789125"));
        sysCompuserArrayList.add(new EsUserInfo(4,"简单点","123456789126"));
        boolean flag = false;
        try {
            flag = userService.insertData(sysCompuserArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    @PostMapping("/termQuery")
    public String termQuery(@RequestParam String keyWord, HttpServletRequest request){
        try {
            String [] fields = new String [] {"name","content"};
            List<EsUserInfo> list = userService.wildcardQuery(keyWord,fields);
            return list.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/termQueryPin")
    public String termQueryPin(@RequestParam String keyWord, HttpServletRequest request){
        try {
            String [] fields = new String [] {"name.pinyin","content.pinyin"};
            List<EsUserInfo> list = userService.wildcardQuery(keyWord,fields);
            return list.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
