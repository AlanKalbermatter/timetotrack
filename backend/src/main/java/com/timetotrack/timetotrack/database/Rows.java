package com.timetotrack.timetotrack.database;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class Rows {

    private Rows() {
    }

    public static <T> List<T> map(RowSet<Row> rows, Function<Row, T> mapper) {
        List<T> result = new ArrayList<>(rows.size());
        for (Row row : rows) {
            result.add(mapper.apply(row));
        }
        return result;
    }

    public static Optional<Row> first(RowSet<Row> rows) {
        RowIterator<Row> iterator = rows.iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }
}
