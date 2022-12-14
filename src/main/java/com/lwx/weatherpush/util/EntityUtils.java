package com.lwx.weatherpush.util;

import com.lwx.weatherpush.repository.EntitySaveRepository;
import lombok.SneakyThrows;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 为实体类提供自生成相关信息
 *
 * @author JinXu
 * @date 2022/7/4
 */
@Component
public class EntityUtils implements IdentifierGenerator {

    /**
     * 实现IdentifierGenerator接口
     * 为jpa自生成id提供一种生成方式
     * 使用uuid的形式 清除其中的分隔符
     *
     * @param sessionContract jpaSession构建器
     * @param o               entity
     * @return uuid without line
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor sessionContract, Object o) throws HibernateException {
        Serializable id = sessionContract.getEntityPersister(null, o).getClassMetadata().getIdentifier(o, sessionContract);
        return id == null ? UUID.randomUUID().toString().replace("-", "") : id;
    }

    /**
     * 生成UUID
     *
     * @return 生成的UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 为实体类 in/out dto之间的转换提供方法
     * 复制同名属性
     *
     * @param clazz 目标class
     * @param obj   来源实体
     * @param <T>   type
     * @return 目标实体
     */
    @SneakyThrows
    public static <T> T generate(Class<T> clazz, Object obj) {
        T target = clazz.newInstance();
        BeanUtils.copyProperties(obj, target);
        return target;
    }

    /**
     * 由于jpa的saveAll适用于新增和更新
     * 所以每次save都会先查询一次
     * 此方法直接插入，不进行查询操作
     * 要求list必须都为新增的数据
     *
     * @param list 实体集合
     * @param <T>  必须为entity
     * @return entity list
     */
    @SneakyThrows
    public static <T> List<T> saveAll(List<T> list) {
        EntitySaveRepository entitySaveRepository = SpringContextUtils.getBean(EntitySaveRepository.class);
        return entitySaveRepository.saveAll(list);
    }

    public static <T> T save(T entity) {
        EntitySaveRepository entitySaveRepository = SpringContextUtils.getBean(EntitySaveRepository.class);
        return entitySaveRepository.save(entity);
    }

}
