package com.lwx.weatherpush.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 由于jpa的saveAll适用于新增和更新
 * 所以每次save都会先查询一次
 * 此方法直接插入，不进行查询操作
 * 要求list必须都为新增的数据
 *
 * @author JinXu
 * @date 2022/7/20
 */
@Component
@EnableTransactionManagement
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EntitySaveRepository {

    private final EntityManager entityManager;

    @Transactional
    @SuppressWarnings("all")
    public <T> List<T> saveAll(List<T> list) throws NoSuchFieldException, IllegalAccessException {
        for (T elem : list) {
            Class<?> clazz = elem.getClass();
            Field field = clazz.getDeclaredField("id");
            field.setAccessible(true);
            Object id = field.get(elem);
            if (id == null) {
                entityManager.persist(elem);
            } else {
                entityManager.merge(elem);
            }
        }
        return list;
    }

    @Transactional
    public <T> T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }
}
