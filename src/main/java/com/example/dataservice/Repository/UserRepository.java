package com.example.dataservice.Repository;

import com.example.dataservice.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByPasswordAndUsername(@Param("password") String password, @Param("username") String username);

    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    List<User> findByCode(@Param("username") String username);

    Optional<User> deleteByUsername(@Param("username") String username);
}