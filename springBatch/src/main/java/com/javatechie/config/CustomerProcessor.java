package com.javatechie.config;

import com.javatechie.entity.Customer;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {
    @Override
    public @Nullable Customer process(Customer item) throws Exception {
        if (item.getLastname().equalsIgnoreCase("Sharma")) {
            return item;
        } else {
            return null;
        }
    }
}