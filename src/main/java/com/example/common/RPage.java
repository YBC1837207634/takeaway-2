package com.example.common;


import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * 分页查询返回的结果
 */
@Data
@NoArgsConstructor
public class RPage {

    private Long total;
    private Object data;

}
