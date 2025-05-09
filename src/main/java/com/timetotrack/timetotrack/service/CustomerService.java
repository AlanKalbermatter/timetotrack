package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.*;

import java.util.List;

public class CustomerService {

    private final CustomerDao dao;

    public CustomerService(CustomerDao dao) {
        this.dao = dao;
    }

    public void fetchAll(Handler<AsyncResult<List<Customer>>> handler) {
        dao.fetchAll(handler);
    }

    public void fetchById(int id, Handler<AsyncResult<Customer>> handler) {
        dao.fetchById(id, handler);
    }

    public void create(Customer customer, Handler<AsyncResult<Customer>> handler) {
        dao.create(customer, handler);
    }

    public void update(Customer customer, Handler<AsyncResult<Customer>> handler) {
        dao.update(customer, handler);
    }

    public void delete(int id, Handler<AsyncResult<Void>> handler) {
        dao.delete(id, handler);
    }
}
