package com.es.service;

import com.es.config.ElsearchConfig;
import com.es.entity.EsUserInfo;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhai
 * @date 2018/5/24 15:00
 */
@Service
@Slf4j
public class UserService {

    private static String indexName = "user";//定义索引 相当于数据库的库
    private static String indexType = "usertype";//定义类型 相当于数据库的表 table


    @Autowired
    private JestClient jestClient;//注入es客户端
    @Autowired
    private ElsearchConfig elsearchConfig;//注入es配置类

    //创建索引和类型
    public void userSettingConfig() throws Exception{
        elsearchConfig.settingsConfig(this.jestClient,indexName,indexType);
    }

    //插入数据
    public boolean insertData(List<EsUserInfo> list) throws Exception {
        List<EsUserInfo> objects = new ArrayList<>();
        System.out.println(list.get(0));
        for (int i=0;i<list.size();i++){
            objects.add(list.get(i));
        }
        JestResult jestResult = elsearchConfig.bulkIndex(this.jestClient,indexName,indexType,objects);
        return jestResult.isSucceeded();
    }


    //搜索
    public List<EsUserInfo> wildcardQuery (String keyWord,String... fields) throws  Exception{
        List<String> indexNames = new ArrayList<String>();
        indexNames.add(indexName);

        List<String> indexTypes = new ArrayList<String>();
        indexTypes.add(indexType);

        List<SearchResult.Hit<EsUserInfo, Void>> list = ElsearchConfig.createSearch(this.jestClient,indexNames,indexTypes,keyWord,new EsUserInfo(),fields);
        List<EsUserInfo> sysPressList = new ArrayList<EsUserInfo>();
        if(list.size()>0){
            for (SearchResult.Hit<EsUserInfo, Void> hit : list) {
                EsUserInfo SysUser = hit.source;
                //获取高亮后的内容
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight.containsKey("name")){
                    List<String> names = highlight.get("name");
                    if(names.size()>0){
                        SysUser.setName(names.get(0));
                    }
                }

                if(highlight.containsKey("content")){
                    List<String> contents = highlight.get("content");
                    if(contents.size()>0){
                        SysUser.setContent(contents.get(0));
                    }
                }
                SysUser.setContent(SysUser.getContent().replaceAll("-",""));
                sysPressList.add(SysUser);
            }
        }
        return sysPressList;
    }

}
