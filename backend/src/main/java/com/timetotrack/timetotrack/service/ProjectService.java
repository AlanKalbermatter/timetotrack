package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.error.ConflictException;
import com.timetotrack.timetotrack.error.NotFoundException;
import com.timetotrack.timetotrack.error.PgErrors;
import com.timetotrack.timetotrack.error.ValidationException;
import com.timetotrack.timetotrack.model.Project;
import io.vertx.core.Future;

import java.util.List;

public class ProjectService {

    static final int MAX_NAME_LENGTH = 120;

    private final ProjectDao projects;

    public ProjectService(ProjectDao projects) {
        this.projects = projects;
    }

    public Future<List<Project>> findAll() {
        return projects.findAll();
    }

    public Future<Project> findById(int id) {
        return projects.findById(id).compose(found -> NotFoundException.require(found, notFound(id)));
    }

    public Future<Project> create(String name, int customerId) {
        return validName(name).compose(valid -> projects.create(valid, customerId)
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION, () -> unknownCustomer(customerId)))
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION, () -> duplicate(valid)))
                .compose(this::findById));
    }

    public Future<Project> update(int id, String name, int customerId) {
        return validName(name).compose(valid -> projects.update(id, valid, customerId)
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION, () -> unknownCustomer(customerId)))
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION, () -> duplicate(valid)))
                .compose(updated -> updated
                        ? findById(id)
                        : Future.failedFuture(new NotFoundException(notFound(id)))));
    }

    public Future<Void> delete(int id) {
        return projects.delete(id)
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION,
                        () -> new ConflictException("Project " + id + " has time entries")))
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

    private static ValidationException unknownCustomer(int customerId) {
        return new ValidationException("Customer " + customerId + " does not exist");
    }

    private static ConflictException duplicate(String name) {
        return new ConflictException("Project '" + name + "' already exists for this customer");
    }

    private static String notFound(int id) {
        return "Project " + id + " not found";
    }
}
