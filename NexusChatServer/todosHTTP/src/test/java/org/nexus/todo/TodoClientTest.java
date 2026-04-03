package org.nexus.todo;

import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class TodoClientTest extends TestCase {

    TodoClient client = new TodoClient();

    @Test
    public void testFindAll() throws IOException, InterruptedException {
        List<Todo> todos = client.findAll();
        assertEquals(200, todos.size());
    }

    @Test
    void shouldReturnTodosGivenValidId() throws IOException, InterruptedException {
        Todo todo = client.findbyId(1);
        assertEquals(1, todo.userId());
        assertEquals(1, todo.id());
        assertEquals("delectus aut autem", todo.title());
        assertFalse(todo.completed());
    }
}