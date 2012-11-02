package au.org.intersect.samifier.reporter;

import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

public class DatabaseHelperTest {

    @Test
    public void testDatabaseConnection()
    {
        try
        {
        	DatabaseHelper db = new DatabaseHelper();
        	db.connect();
    		db.generateTables();
    		
    		Collection<String> result = db.filterResult("SELECT * FROM Result;");
    		System.out.println(result.toString());
    		db.shutdown();
        }
        catch (Exception e)
        {
        	System.out.println("HSQLDB error:" + e);
        	fail("Unexpected exception: " + e.getMessage());
        }
    }       
}

