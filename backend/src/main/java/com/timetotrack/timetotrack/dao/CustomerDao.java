package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.CustomerSQL;
import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    private final Pool client;

    public CustomerDao(Pool client) {
        this.client = client;
    }

    public void fetchAll(Handler<AsyncResult<List<Customer>>> handler) {
        client.query(CustomerSQL.SELECT_ALL).execute(ar -> {
            if (ar.succeeded()) {
                List<Customer> list = new ArrayList<>();
                for (Row row : ar.result()) {
                    list.add(map(row));
                }
                handler.handle(Future.succeededFuture(list));
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    public void fetchById(int id, Handler<AsyncResult<Customer>> handler) {
        client.preparedQuery(CustomerSQL.SELECT_BY_ID).execute(Tuple.of(id), ar -> {
            if (ar.succeeded() && ar.result().rowCount() > 0) {
                handler.handle(Future.succeededFuture(map(ar.result().iterator().next())));
            } else {
                handler.handle(Future.failedFuture("Customer not found"));
            }
        });
    }

    public void create(Customer customer, Handler<AsyncResult<Customer>> handler) {
        client.preparedQuery(CustomerSQL.INSERT_ONE)
                .execute(Tuple.of(customer.getName()), ar -> {
                    if (ar.succeeded()) {
                        customer.setId(ar.result().iterator().next().getInteger("customer_id"));
                        handler.handle(Future.succeededFuture(customer));
                    } else {
                        handler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void update(Customer customer, Handler<AsyncResult<Customer>> handler) {
        client.preparedQuery(CustomerSQL.UPDATE_ONE)
                .execute(Tuple.of(customer.getName(), customer.getId()), ar -> {
                    if (ar.succeeded()) {
                        handler.handle(Future.succeededFuture(customer));
                    } else {
                        handler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void delete(int id, Handler<AsyncResult<Void>> handler) {
        client.preparedQuery(CustomerSQL.DELETE_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    private Customer map(Row row) {
        return new Customer(row.getInteger("customer_id"), row.getString("customer_name"));
    }
}
