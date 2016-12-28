package org.openshift.quickstarts.todolist.dao;

import org.openshift.quickstarts.todolist.model.TodoEntry;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface TodoListDAO {

    long save(TodoEntry entry);
    
    TodoEntry get(Serializable id);

    List<TodoEntry> list();
    
    int delete(Serializable id); // for testing usage only
}
