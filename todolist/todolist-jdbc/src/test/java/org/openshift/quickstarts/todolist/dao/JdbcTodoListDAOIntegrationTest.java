package org.openshift.quickstarts.todolist.dao;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.filter.ExcludeRegExpPaths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openshift.quickstarts.todolist.model.TodoEntry;

/**
 * Integration test -- saves a TodoEntry and verifies it was saved.
 */
@RunWith(Arquillian.class)
public class JdbcTodoListDAOIntegrationTest {
	
//	@Inject
	JdbcTodoListDAO dao;
	
//	@Deployment 
//	public static WebArchive createDeployment() {
//	    return ShrinkWrap.create(WebArchive.class) 
//            .addClasses( JdbcTodoListDAO.class, TodoListDAO.class, TodoEntry.class)
//            .addAsWebResource(EmptyAsset.INSTANCE, "beans.xml");
//	}
	
	@Deployment 
	public static WebArchive createDeployment() {
		//Package p = TodoListDAO.class.getPackage();
	    return ShrinkWrap.create(WebArchive.class ) //"todolist-jdbc.war") 
            //.addClasses( JdbcTodoListDAO.class, TodoListDAO.class, TodoEntry.class);
	        .addPackages( true, "org.openshift.quickstarts.todolist");
    		//.addPackages(true, new ExcludeRegExpPaths(".*Test.class$"), p)
//	    	.addPackage(JdbcTodoListDAO.class.getPackage())
//	    	.addPackage(TodoListDAO.class.getPackage())
//	    	.addPackage(TodoEntry.class.getPackage())
//            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

	}
	
	@Test
	public void save () {
		
		//final String id = "4000";
		long id = 4000l;
		dao = new JdbcTodoListDAO();
		
		try {
			
			TodoEntry te = new TodoEntry();
			te.setDescription("mydescription");
			te.setId(id);
			te.setSummary("summary here");
			
			id = dao.save(te);
			
			TodoEntry teFound = dao.get(id);
			Assert.assertNotNull(teFound);
			Assert.assertEquals(id, teFound.getId());
			
		} finally {
			
			// cleanup
			if ( null != dao ) {
				int deleted = dao.delete(id);
				//Assert.assertEquals( 1,  deleted);
			}
		}
		
	}

}
