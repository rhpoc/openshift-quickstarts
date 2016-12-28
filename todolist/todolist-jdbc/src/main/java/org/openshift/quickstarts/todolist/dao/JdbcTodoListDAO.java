package org.openshift.quickstarts.todolist.dao;

import org.openshift.quickstarts.todolist.model.TodoEntry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  TODO: proper exception handling
 *  TODO: initialize schema whenever necessary (what if db is not persistent and is restarted while app is running)
 */
public class JdbcTodoListDAO implements TodoListDAO {

    private final DataSource dataSource;

    public JdbcTodoListDAO() {
        dataSource = lookupDataSource();
        initializeSchemaIfNeeded();
    }

    private DataSource lookupDataSource() {
        try {
            Context initialContext = new InitialContext();
            String mydb = null;
            try {
            	mydb = "java:jboss/jdbc/TodoListDS"; // System.getenv("MYDB");
            	System.out.println( "mydb: " +  mydb);
                return (DataSource) initialContext.lookup(mydb);
            } catch (NameNotFoundException e) {
            	System.out.println( "mydb: " +  mydb);
                Context envContext = (Context) initialContext.lookup("java:comp/env");  // Tomcat places datasources inside java:comp/env
                return (DataSource) envContext.lookup(System.getenv("MYDB"));
            }
        } catch (NamingException e) {
            throw new DataAccessException("Could not look up datasource", e);
        }
    }

    private void initializeSchemaIfNeeded() {
        try {
            Connection connection = getConnection();
            try {
                if (!isSchemaInitialized(connection)) {
                    connection.setAutoCommit(false);
                    Statement statement = connection.createStatement();
                    try {
                        statement.executeUpdate("CREATE TABLE todo_entries (id bigint, summary VARCHAR(255), description TEXT)");
                        connection.commit();
                    } finally {
                        statement.close();
                    }
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("could not initialize database schema", e);
        }
    }

    private boolean isSchemaInitialized(Connection connection) throws SQLException {
        ResultSet rset = connection.getMetaData().getTables(null, null, "todo_entries", null);
        try {
            return rset.next();
        } finally {
            rset.close();
        }
    }

    @Override
    public long save(TodoEntry entry) {
    	Long id = (Long) entry.getId();
        try {
            Connection connection = getConnection();
            try {
                connection.setAutoCommit(false);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO todo_entries (id, summary, description) VALUES (?, ?, ?)");
                try {
                	if (null == id) {
                		id = getNextId();
                	}
                    statement.setLong(1, id);
                    statement.setString(2, entry.getSummary());
                    statement.setString(3, entry.getDescription());
                    statement.executeUpdate();
                    connection.commit();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save entry " + entry, e);
        }
        
        
        return id;
    }

    private long getNextId() {
        return new Random().nextLong();
    }

    @Override
    public List<TodoEntry> list() {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet rset = statement.executeQuery("SELECT id, summary, description FROM todo_entries");
                    try {
                        List<TodoEntry> list = new ArrayList<TodoEntry>();
                        while (rset.next()) {
                            Long id = rset.getLong(1);
                            String summary = rset.getString(2);
                            String description = rset.getString(3);
                            list.add(new TodoEntry(id, summary, description));
                        }
                        return list;
                    } finally {
                        rset.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not list entries", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataSource getDataSource() {
        return dataSource;
    }

	@Override
	public TodoEntry get(final Serializable idIn) {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet rset = statement.executeQuery("SELECT * from todo_entries where id = " + idIn);
                    try {
                        TodoEntry te = null;
                        if (rset.next()) {
                            Long id = rset.getLong(1);
                            String summary = rset.getString(2);
                            String description = rset.getString(3);
                            te = new TodoEntry(id, summary, description);
                        }
                        return te;
                    } finally {
                        rset.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not get entry by idIn (" + idIn + "): ", e);
        }
        
       
	}

	@Override
	public int delete(Serializable idIn) {
		try {
			
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    return statement.executeUpdate("DELETE from todo_entries where id = " + idIn);
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
            
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete entry by idIn (" + idIn + "): ", e);
        }
        		
	}
}
