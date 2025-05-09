package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerDao mockDao;
    private CustomerService service;

    @BeforeEach
    void setup() {
        mockDao = mock(CustomerDao.class);
        service = new CustomerService(mockDao);
    }

    @Test
    void createDelegatesToDao() {
        Customer c = new Customer(null, "Fake");
        doAnswer(invocation -> {
            Handler<AsyncResult<Customer>> handler = invocation.getArgument(1);
            c.id = 123;
            handler.handle(Future.succeededFuture(c));
            return null;
        }).when(mockDao).create(eq(c), any());

        service.create(c, ar -> {
            assertTrue(ar.succeeded());
            assertEquals(123, ar.result().id);
        });
    }
}
