package com.es.config;

import com.es.utils.SettingsAndMapping;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.Health;
import io.searchbox.cluster.NodesInfo;
import io.searchbox.cluster.NodesStats;
import io.searchbox.indices.settings.UpdateSettings;
import io.searchbox.core.*;
import io.searchbox.indices.*;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author zhai
 * @date 2018/5/24 14:50
 * 创建es配置类
 */
@Component
public class ElsearchConfig {

    //配置实体映射
    public void settingsConfig(JestClient jestClient, String indexName, String indexType) throws Exception{
        createIndex(jestClient,indexName);
        closeIndex(jestClient,indexName);
        updateIndexSettings(jestClient,indexName, SettingsAndMapping.settings());
        openIndex(jestClient,indexName);
        putIndexMapping(jestClient,indexName,indexType,SettingsAndMapping.commonMapping(indexType));
    }



    //创建索引
    public <T> JestResult createIndex(JestClient jestClient, String indexName){
        CreateIndex createIndex = new CreateIndex.Builder(indexName).build();
        JestResult jestResult = null ;
        try {
            jestResult = jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jestResult;
    }

    //关闭索引
    public JestResult closeIndex(JestClient jestClient,String indexName) {
        CloseIndex closeIndex = new CloseIndex.Builder(indexName).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(closeIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }


    //打开索引
    public JestResult openIndex(JestClient jestClient,String indexName) {
        OpenIndex openIndex = new OpenIndex.Builder(indexName).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(openIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //更新索引设置
    public JestResult updateIndexSettings(JestClient jestClient,String indexName,String source) {
        UpdateSettings settings = new UpdateSettings.Builder(source).addIndex(indexName).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //更新映射设置
    public JestResult putIndexMapping(JestClient jestClient,String indexName,String indexType,String source) {
        PutMapping putMapping = new PutMapping.Builder(indexName, indexType, source).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(putMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }




    public <T> JestResult bulkIndex(JestClient jestClient,String indexName,String indexType,List<T> tList) {
        Bulk.Builder bulkBuilder = new Bulk.Builder().defaultIndex(indexName).defaultType(indexType);
        for(int i=0;i<tList.size();i++){
            Index index = new Index.Builder(tList.get(i)).build();
            bulkBuilder.addAction(index);
        }
        JestResult result = null ;
        try{
            result = jestClient.execute(bulkBuilder.build());
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }


    //搜索
    public static <T> List<SearchResult.Hit<T, Void>> createSearch(JestClient jestClient, List<String> indexNames, List<String> indexTypes, String keyWord, T o, String... fields) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyWord,fields);
        searchSourceBuilder.query(queryBuilder);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String fiel : fields){
            highlightBuilder.field(fiel);
        }
        highlightBuilder.preTags("<em>").postTags("</em>");
        highlightBuilder.fragmentSize(200);
        searchSourceBuilder.highlighter(highlightBuilder);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexNames).addType(indexTypes).build();
        SearchResult result = null ;
        List<?> hits = null ;
        try {
            result = jestClient.execute(search);
            hits = result.getHits(o.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (List<SearchResult.Hit<T, Void>>) hits ;
    }

    //搜索
    public <T> List<SearchResult.Hit<T, Void>> createSearchCard(JestClient jestClient,List<String> indexNames,List<String> indexTypes,String keyWord,T o, String fields) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name","*考试*");
        searchSourceBuilder.query(queryBuilder);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexNames).addType(indexTypes).build();
        SearchResult result = null ;
        List<?> hits = null ;
        try {
            result = jestClient.execute(search);
            hits = result.getHits(o.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (List<SearchResult.Hit<T, Void>>) hits ;
    }




    //删除索引
    public JestResult deleteIndex(JestClient jestClient,String indexName) {
        DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).build();
        JestResult result = null;
        try {
            result = jestClient.execute(deleteIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //清除缓存
    public JestResult clearCache(JestClient jestClient) {
        ClearCache clearCache = new ClearCache.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(clearCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //优化索引
    public JestResult optimizeIndex(JestClient jestClient) {
        Optimize optimize = new Optimize.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(optimize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //刷新索引
    public JestResult flushIndex(JestClient jestClient){
        Flush flush = new Flush.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(flush);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //判断索引是否存在
    public JestResult indicesExists(JestClient jestClient,String indexName){
        IndicesExists indicesExists = new IndicesExists.Builder(indexName).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(indicesExists);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //节点信息
    public JestResult nodesInfo(JestClient jestClient){
        NodesInfo nodesInfo = new NodesInfo.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(nodesInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //健康减查
    public JestResult health(JestClient jestClient){
        Health health = new Health.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(health);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //节点状态
    public JestResult nodesStats(JestClient jestClient){
        NodesStats nodesStats = new NodesStats.Builder().build();
        JestResult result = null ;
        try {
            result = jestClient.execute(nodesStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    //删除元素
    public JestResult deleteDocument(JestClient jestClient,String indexName,String type,String id) {
        Delete delete = new Delete.Builder(id).index(indexName).type(type).build();
        JestResult result = null ;
        try {
            result = jestClient.execute(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
