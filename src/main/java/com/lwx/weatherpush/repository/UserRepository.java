package com.lwx.weatherpush.repository;

import com.lwx.weatherpush.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author LiWenXin
 * @date 2022/12/12
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByAccount(String account);

    List<User> findAllByAccountIn(List<String> account);

}
