package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.error.ConflictException;
import com.timetotrack.timetotrack.error.NotFoundException;
import com.timetotrack.timetotrack.error.PgErrors;
import com.timetotrack.timetotrack.error.ValidationException;
import com.timetotrack.timetotrack.model.Summary.DayTotal;
import com.timetotrack.timetotrack.model.Summary.ProjectTotal;
import com.timetotrack.timetotrack.model.Summary;
import com.timetotrack.timetotrack.model.TimeEntry;
import io.vertx.core.Future;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class TimeEntryService {

    static final int MAX_DESCRIPTION_LENGTH = 500;
    static final Duration DEFAULT_LOOKBACK = Duration.ofDays(30);

    private final TimeEntryDao entries;
    private final Clock clock;

    public TimeEntryService(TimeEntryDao entries, Clock clock) {
        this.entries = entries;
        this.clock = clock;
    }

    /** Entries overlapping [from, to); defaults to the last 30 days. */
    public Future<List<TimeEntry>> list(int userId, Instant from, Instant to) {
        Instant now = clock.instant();
        Instant start = from != null ? from : now.minus(DEFAULT_LOOKBACK);
        Instant end = to != null ? to : now.plus(Duration.ofDays(1));
        if (!start.isBefore(end)) {
            return Future.failedFuture(new ValidationException("from must be before to"));
        }
        return entries.findForUser(userId, start, end, now);
    }

    public Future<TimeEntry> createManual(int userId, int projectId, String description, Instant from, Instant to) {
        if (!from.isBefore(to)) {
            return Future.failedFuture(new ValidationException("to must be after from"));
        }
        return validDescription(description)
                .compose(valid -> entries.insert(userId, projectId, valid, from, to))
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION, () -> unknownProject(projectId)))
                .compose(id -> load(id, userId));
    }

    public Future<Void> delete(int userId, long id) {
        return entries.deleteForUser(id, userId).compose(deleted -> deleted
                ? Future.<Void>succeededFuture()
                : Future.failedFuture(new NotFoundException("Time entry " + id + " not found")));
    }

    public Future<Optional<TimeEntry>> current(int userId) {
        return entries.findRunning(userId);
    }

    public Future<TimeEntry> start(int userId, int projectId, String description) {
        Instant now = clock.instant();
        return validDescription(description)
                .compose(valid -> entries.insert(userId, projectId, valid, now, null))
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION, () -> new ConflictException("A timer is already running")))
                .recover(PgErrors.translate(PgErrors.FOREIGN_KEY_VIOLATION, () -> unknownProject(projectId)))
                .compose(id -> load(id, userId));
    }

    public Future<TimeEntry> stop(int userId) {
        return entries.stopRunning(userId, clock.instant())
                .compose(stopped -> stopped
                        .map(id -> load(id, userId))
                        .orElseGet(() -> Future.failedFuture(new ConflictException("No timer is running"))));
    }

    static final Duration MAX_SUMMARY_RANGE = Duration.ofDays(366);

    public Future<Summary> summary(int userId, Instant from, Instant to, String tz) {
        if (from == null || to == null) {
            return Future.failedFuture(new ValidationException("from and to are required"));
        }
        if (!from.isBefore(to)) {
            return Future.failedFuture(new ValidationException("from must be before to"));
        }
        if (Duration.between(from, to).compareTo(MAX_SUMMARY_RANGE) > 0) {
            return Future.failedFuture(new ValidationException("The range must be at most 366 days"));
        }
        String zoneId;
        try {
            zoneId = postgresZone(tz);
        } catch (ValidationException e) {
            return Future.failedFuture(e);
        }
        Instant now = clock.instant();
        Future<List<ProjectTotal>> byProject = entries.summaryByProject(userId, from, to, now);
        Future<List<DayTotal>> byDay = entries.summaryByDay(userId, from, to, now, zoneId);
        return Future.all(byProject, byDay).map(done -> new Summary(
                byProject.result().stream().mapToLong(ProjectTotal::seconds).sum(),
                byProject.result(),
                byDay.result()));
    }

    /**
     * Postgres interprets numeric offsets like "+03:00" with the POSIX (inverted) sign, so only
     * region ids and UTC are accepted.
     */
    private static String postgresZone(String tz) {
        if (tz == null) {
            return "UTC";
        }
        try {
            ZoneId zone = ZoneId.of(tz);
            if (zone instanceof ZoneOffset offset) {
                if (offset.equals(ZoneOffset.UTC)) {
                    return "UTC";
                }
                throw new ValidationException("tz must be an IANA time zone such as America/Argentina/Buenos_Aires");
            }
            return zone.getId();
        } catch (DateTimeException e) {
            throw new ValidationException("tz must be an IANA time zone such as America/Argentina/Buenos_Aires");
        }
    }

    private Future<TimeEntry> load(long id, int userId) {
        return entries.findByIdForUser(id, userId)
                .compose(found -> NotFoundException.require(found, "Time entry " + id + " not found"));
    }

    private static Future<String> validDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            return Future.failedFuture(new ValidationException(
                    "description must be at most " + MAX_DESCRIPTION_LENGTH + " characters"));
        }
        return Future.succeededFuture(description);
    }

    private static ValidationException unknownProject(int projectId) {
        return new ValidationException("Project " + projectId + " does not exist");
    }
}
