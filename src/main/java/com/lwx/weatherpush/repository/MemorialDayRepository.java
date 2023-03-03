package com.lwx.weatherpush.repository;

import com.lwx.weatherpush.entity.MemorialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LiWenXin
 * @date 2023/3/3
 */
@Repository
public interface MemorialDayRepository extends JpaRepository<MemorialDay, String> {
}
