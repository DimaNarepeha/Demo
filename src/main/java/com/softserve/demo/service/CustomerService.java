package com.softserve.demo.service;


import com.softserve.demo.dto.CustomerDTO;
import com.softserve.demo.dto.ProviderDTO;
import com.softserve.demo.model.Customer;
import com.softserve.demo.model.CustomerStatus;
import com.softserve.demo.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    void createCustomer(CustomerDTO customer);

    CustomerDTO updateCustomer(Integer id, CustomerDTO customer);

    List<CustomerDTO> getAllCustomers();

    CustomerDTO deleteCustomer(Integer id);

    CustomerDTO getCustomerById(Integer id);

    Page<CustomerDTO> getCustomersByPage(Pageable pageable);

    void addImageToCustomer(Integer id, String fileName);

    Customer getCustomerByOffer(Offer offer);

    CustomerDTO findCustomerByUserId (Integer id);

    Page<CustomerDTO> getCustomersByStatus(Pageable pageable, CustomerStatus status);

    CustomerDTO updateStatus(Integer id, String status);

}
