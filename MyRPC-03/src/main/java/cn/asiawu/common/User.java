package cn.asiawu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author asiawu
 * @date 2023/06/24 21:24
 * @description: User实体类
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID=1L;
    private String id;
    private String username;
    private Integer age;
}
