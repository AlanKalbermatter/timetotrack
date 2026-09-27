package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.ProjectSQL;
import com.timetotrack.timetotrack.database.Rows;
import com.timetotrack.timetotrack.model.Project;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Optional;

/**
 * Data access for projects; reads join the customer so responses carry {@code customerName}.
 */
public class ProjectDao {

    private final Pool pool;

    public ProjectDao(Pool pool) {
        this.pool = pool;
    }

    public Future<List<Project>> findAll() {
        return pool.query(ProjectSQL.SELECT_ALL).execute().map(rows -> Rows.map(rows, ProjectDao::toProject));
    }

    public Future<Optional<Project>> findById(int id) {
        return pool.preparedQuery(ProjectSQL.SELECT_BY_ID)
                .execute(Tuple.of(id))
                .map(rows -> Rows.first(rows).map(ProjectDao::toProject));
    }

    /** @return the new project id */
    public Future<Integer> create(String name, int customerId) {
        return pool.preparedQuery(ProjectSQL.INSERT_ONE)
                .execute(Tuple.of(name, customerId))
                .map(rows -> rows.iterator().next().getInteger("project_id"));
    }

    /** @return whether a row was updated */
    public Future<Boolean> update(int id, String name, int customerId) {
        return pool.preparedQuery(ProjectSQL.UPDATE_ONE)
                .execute(Tuple.of(name, customerId, id))
                .map(rows -> rows.rowCount() > 0);
    }

    /** @return whether a row was deleted */
    public Future<Boolean> delete(int id) {
        return pool.preparedQuery(ProjectSQL.DELETE_BY_ID).execute(Tuple.of(id)).map(rows -> rows.rowCount() > 0);
    }

    private static Project toProject(Row row) {
        return new Project(
                row.getInteger("project_id"),
                row.getString("project_name"),
                row.getInteger("customer_id"),
                row.getString("customer_name"));
    }
}
