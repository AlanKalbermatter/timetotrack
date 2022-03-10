package timetotrack.microservice.timer;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link timetotrack.microservice.timer.Timer}.
 * NOTE: This class has been automatically generated from the {@link timetotrack.microservice.timer.Timer} original class using Vert.x codegen.
 */
public class TimerConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Timer obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "customerId":
          if (member.getValue() instanceof Number) {
            obj.setCustomerId(((Number)member.getValue()).longValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).longValue());
          }
          break;
        case "projectId":
          if (member.getValue() instanceof Number) {
            obj.setProjectId(((Number)member.getValue()).longValue());
          }
          break;
      }
    }
  }

  public static void toJson(Timer obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Timer obj, java.util.Map<String, Object> json) {
    if (obj.getCustomerId() != null) {
      json.put("customerId", obj.getCustomerId());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getProjectId() != null) {
      json.put("projectId", obj.getProjectId());
    }
  }
}
