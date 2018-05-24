package com.es.entity;

import io.searchbox.annotations.JestId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhai
 * @date 2018/5/24 14:58
 * 创建用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsUserInfo {

    @JestId
    private int userId;
    private String name;
    private String content;


}
