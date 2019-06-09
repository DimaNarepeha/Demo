package com.softserve.demo.repository;

import com.softserve.demo.model.Customer;
import com.softserve.demo.model.CustomerStatus;
import com.softserve.demo.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    Customer getCustomerByOffers(Offer offer);

    Customer findCustomerByUserId(Integer id);
}
