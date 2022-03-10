package timetotrack.microservice.timer;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.sql.Timestamp;

/**
 * A timer that represent the time recorded for a determined job.
 */

@DataObject(generateConverter = true)
public class Timer {

    private Long id;
    private Timestamp fromTime;
    private Timestamp toTime;

    private Long projectId;
    private Long customerId;

    public Timer() {
    }

    public Timer(Long id) {
        this.id = id;
    }

    public Timer(JsonObject timerJson) {
      TimerConverter.fromJson(timerJson, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        return json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getFromTime() {
        return fromTime;
    }

    public void setFromTime(Timestamp fromTime) {
        this.fromTime = fromTime;
    }

    public Timestamp getToTime() {
        return toTime;
    }

    public void setToTime(Timestamp toTime) {
        this.toTime = toTime;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
