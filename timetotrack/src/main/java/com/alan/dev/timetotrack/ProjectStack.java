package com.alan.dev.timetotrack;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProjectStack {

	private Map<Long, Project> projects = new HashMap<>();

	public ProjectStack() {
		/*
		 * projects.put(1L, new Project(1L, "Vert.x in Action")); projects.put(2L, new
		 * Project(2L, "Building Microservices"));
		 */
	}

	public JsonArray getAll() {
		JsonArray all = new JsonArray();
		projects.values().forEach(project -> {
			all.add(JsonObject.mapFrom(project));
		});
		return all;
	}

	public void add(final Project entry) {
		/*
		 * projects.put(entry.getIsbn(), entry);
		 */
	}

	public Project update(final String isbn, final Project entry) {
		/*
		 * Long key = Long.parseLong(isbn); if (key != entry.getIsbn()) { throw new
		 * IllegalArgumentException("ISBN does not match!"); } projects.put(key, entry);
		 * return entry;
		 */
		return null;
	}

	public Project get(final String isbn) {
		Long key = Long.parseLong(isbn);
		return projects.get(key);
	}

	public Project delete(final String isbn) {
		Long key = Long.parseLong(isbn);
		return projects.remove(key);
	}
}
