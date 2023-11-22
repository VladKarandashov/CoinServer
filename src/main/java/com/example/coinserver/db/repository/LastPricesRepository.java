package com.example.coinserver.db.repository;

import com.example.coinserver.db.entity.LastPricesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastPricesRepository extends JpaRepository<LastPricesEntity, String> {
}
