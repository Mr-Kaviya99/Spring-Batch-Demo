package com.springboot.batch.batchprocessing.repository;

import com.springboot.batch.batchprocessing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface CustomerRepository extends JpaRepository<Customer, Serializable> {
}
