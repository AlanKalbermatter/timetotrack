package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.CustomerSQL;
import com.timetotrack.timetotrack.database.Rows;
import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Optional;

/**
 * Data access for customers. Constraint violations are left to {@code CustomerService} to translate.
 */
public class CustomerDao {

    private final Pool pool;

    public CustomerDao(Pool pool) {
        this.pool = pool;
    }

    public Future<List<Customer>> findAll() {
        return pool.query(CustomerSQL.SELECT_ALL).execute().map(rows -> Rows.map(rows, CustomerDao::toCustomer));
    }

    public Future<Optional<Customer>> findById(int id) {
        return pool.preparedQuery(CustomerSQL.SELECT_BY_ID)
                .execute(Tuple.of(id))
                .map(rows -> Rows.first(rows).map(CustomerDao::toCustomer));
    }

    public Future<Customer> create(String name) {
        return pool.preparedQuery(CustomerSQL.INSERT_ONE)
                .execute(Tuple.of(name))
                .map(rows -> new Customer(rows.iterator().next().getInteger("customer_id"), name));
    }

    /** @return whether a row was updated */
    public Future<Boolean> update(int id, String name) {
        return pool.preparedQuery(CustomerSQL.UPDATE_ONE).execute(Tuple.of(name, id)).map(rows -> rows.rowCount() > 0);
    }

    /** @return whether a row was deleted */
    public Future<Boolean> delete(int id) {
        return pool.preparedQuery(CustomerSQL.DELETE_BY_ID).execute(Tuple.of(id)).map(rows -> rows.rowCount() > 0);
    }

    private static Customer toCustomer(Row row) {
        return new Customer(row.getInteger("customer_id"), row.getString("customer_name"));
    }
}
