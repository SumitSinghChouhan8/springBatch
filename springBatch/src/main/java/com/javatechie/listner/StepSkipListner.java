package com.javatechie.listner;

import com.javatechie.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.step.StepExecution;
import tools.jackson.databind.ObjectMapper;

public class StepSkipListner implements SkipListener<Customer,Number> {

   // If you want to more customization use this class  StepExecution

    Logger logger=LoggerFactory.getLogger(StepSkipListner.class);

    public void onSkipInRead(Throwable t) {
        logger.info("A failure on read {}",t.getMessage());
    }

    public void onSkipInWrite(Number item, Throwable t) {
        logger.info("A failure on write {},{}",  t.getMessage(), item);
    }

    public void onSkipInProcess(Customer item, Throwable t) {
        logger.info("Item {} was skipped due to the exception {}",new ObjectMapper().writeValueAsString(item)
                ,t.getMessage());
        // here item or customer item means one row of csv file i.e each row in csv file act as object i.e in db every row is object
    }

}
