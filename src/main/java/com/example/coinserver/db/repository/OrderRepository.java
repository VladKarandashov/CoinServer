package com.example.coinserver.db.repository;

import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> findAllByUser(UserEntity user);

    List<OrderEntity> findAllByUserAndAssetsSymbol(UserEntity user, String symbol);
}
