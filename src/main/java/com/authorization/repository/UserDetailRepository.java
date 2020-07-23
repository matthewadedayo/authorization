package com.authorization.repository;

import com.food.FoodModel.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String name);

}
