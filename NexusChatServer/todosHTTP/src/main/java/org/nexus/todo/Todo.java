package org.nexus.todo;

public record Todo(int userId, int id, String title, Boolean completed) {
}
