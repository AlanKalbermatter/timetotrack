package timetotrack.microservice.timer;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.sql.Timestamp;

@DataObject(generateConverter = true)
public class Timer {

  private Long id;
  private Timestamp fromTime;
  private Timestamp toTime;

  private Long projectId;
  private Long customerId;

  public Timer(){
  }

  public Timer(Long id){
    this.id = id;
  }

  public Timer(JsonObject timerJson) {

  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    return json;
  }
}
