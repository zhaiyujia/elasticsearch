package com.es.utils;

/**
 * @author zhai
 * @date 2018/5/24 14:49
 * 配置实体映射关系
 */
public class SettingsAndMapping {

    public static String settings(){

        String settings ="{\"index\":{\"analysis\":{\"analyzer\":{\"default\":{\"tokenizer\":\"ik_max_word\"},\"pinyin_analyzer\":{\"tokenizer\":\"my_pinyin\"}}," +
                "\"tokenizer\":{\"my_pinyin\":{\"keep_separate_first_letter\":\"false\",\"lowercase\":\"true\",\"type\":\"pinyin\",\"limit_first_letter_length\":\"16\"," +
                "\"keep_original\":\"true\",\"keep_full_pinyin\":\"true\",\"first_letter\":\"none\",\"padding_char\":\"\"}}}}}";


        return settings;
    }

    public static String commonMapping(String indexType){


        String mapping = "{\""+indexType+"\":{\"properties\":{\"name\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"," +
                "\"include_in_all\":true,\"fields\":{\"pinyin\":{\"type\":\"text\",\"term_vector\":\"with_positions_offsets\"," +
                "\"analyzer\":\"pinyin_analyzer\",\"boost\":10}}}}}}";

        return mapping;
    }

}
