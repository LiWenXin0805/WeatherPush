package com.lwx.weatherpush.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author LiWenXin
 * @date 2022/12/10
 */
@Data
@ToString
@Entity
@Table(name = "t_system_user")
@DynamicInsert
@DynamicUpdate
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.lwx.weatherpush.util.EntityUtils")
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("微信号")
    private String account;

    @ApiModelProperty("微信昵称")
    private String name;

    @ApiModelProperty("corn表达式")
    private String corn;

    @ApiModelProperty("模板ID")
    private String templateId;

    @ApiModelProperty("爱称")
    private String petName;

    @ApiModelProperty("问候语，问候语中用英文问号?作为占位符代替爱称")
    private String greeting;

    @ApiModelProperty("所在城市代码")
    private String city;

    @ApiModelProperty("生日")
    private String birthday;

    private static final long serialVersionUID = 1L;

}
