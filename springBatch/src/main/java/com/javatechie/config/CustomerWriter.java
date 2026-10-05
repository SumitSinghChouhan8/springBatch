package com.javatechie.config;

import com.javatechie.entity.Customer;
import com.javatechie.repository.CustomerRepo;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class CustomerWriter implements ItemWriter<Customer> {

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {

        customerRepo.saveAll(chunk);


    }
}
