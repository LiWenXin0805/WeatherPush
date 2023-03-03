package com.lwx.weatherpush.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author LiWenXin
 * @date 2023/3/3
 */
@Data
@ToString
@Entity
@Table(name = "t_memorial_day")
@DynamicInsert
@DynamicUpdate
public class MemorialDay implements Serializable {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.lwx.weatherpush.util.EntityUtils")
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("微信号")
    private String account;

    @ApiModelProperty("纪念日名称")
    private String name;

    @ApiModelProperty("纪念日日期")
    private String date;

    private static final long serialVersionUID = 1L;

}
