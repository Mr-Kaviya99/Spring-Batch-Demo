package com.springboot.batch.batchprocessing.config;

import com.springboot.batch.batchprocessing.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {
    @Override
    public Customer process(Customer item) throws Exception {

        //Handle logic here
        if (item.getGender().equals("Male")){
            return item;
        }

        return null;
    }
}
