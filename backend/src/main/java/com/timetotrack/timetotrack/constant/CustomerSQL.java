package com.timetotrack.timetotrack.constant;

public class CustomerSQL {
    public static final String SELECT_ALL = "SELECT customer_id, customer_name FROM customer";
    public static final String SELECT_BY_ID = "SELECT customer_id, customer_name FROM customer WHERE customer_id = $1";
    public static final String INSERT_ONE = "INSERT INTO customer (customer_name) VALUES ($1) RETURNING customer_id";
    public static final String UPDATE_ONE = "UPDATE customer SET customer_name = $1 WHERE customer_id = $2";
    public static final String DELETE_BY_ID = "DELETE FROM customer WHERE customer_id = $1";
}
