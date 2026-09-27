package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.error.ConflictException;
import com.timetotrack.timetotrack.error.NotFoundException;
import com.timetotrack.timetotrack.error.PgErrors;
import com.timetotrack.timetotrack.error.ValidationException;
import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.Future;

import java.util.List;

public class CustomerService {

    static final int MAX_NAME_LENGTH = 120;

    private final CustomerDao customers;

    public CustomerService(CustomerDao customers) {
        this.customers = customers;
    }

    public Future<List<Customer>> findAll() {
        return customers.findAll();
    }

    public Future<Customer> findById(int id) {
        return customers.findById(id).compose(found -> NotFoundException.require(found, notFound(id)));
    }

    public Future<Customer> create(String name) {
        return validName(name).compose(valid -> customers.create(valid)
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION, () -> duplicate(valid))));
    }

    public Future<Customer> update(int id, String name) {
        return validName(name).compose(valid -> customers.update(id, valid)
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION, () -> duplicate(valid)))
                .compose(updated -> updated
                        ? Future.succeededFuture(new Customer(id, valid))
                        : Future.failedFuture(new NotFoundException(notFound(id)))));
    }

    public Future<Void> delete(int id) {
        return customers.delete(id)
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION,
                        () -> new ConflictException("Customer " + id + " still has projects")))
                .compose(deleted -> deleted
                        ? Future.<Void>succeededFuture()
                        : Future.failedFuture(new NotFoundException(notFound(id))));
    }

    private static Future<String> validName(String name) {
        if (name == null) {
            return Future.failedFuture(new ValidationException("name is required"));
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return Future.failedFuture(new ValidationException("name must be at most " + MAX_NAME_LENGTH + " characters"));
        }
        return Future.succeededFuture(name);
    }

    private static ConflictException duplicate(String name) {
        return new ConflictException("A customer named '" + name + "' already exists");
    }

    private static String notFound(int id) {
        return "Customer " + id + " not found";
    }
}
