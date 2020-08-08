package com.alan.dev.timetotrack;

import io.vertx.core.json.JsonObject;

import java.sql.Timestamp;

public class Time_entry {

	private int time_entry_id;

	private Timestamp from_time;

	private Timestamp to_time;

	private int project_id;

	private int cap_user_id;

	String id = String.valueOf(Integer.parseInt(""));

	public Time_entry(Timestamp from_time, Timestamp to_time, int project_id, int cap_user_id) {
		this.time_entry_id = Integer.parseInt("");
		this.from_time = from_time;
		this.to_time = to_time;
		this.project_id = project_id;
		this.cap_user_id = cap_user_id;
	}

	public Time_entry(JsonObject json) {
		this.time_entry_id = json.getInteger("time_entry_id");

	}
}
