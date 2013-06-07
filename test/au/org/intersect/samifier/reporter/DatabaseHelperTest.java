package au.org.intersect.samifier.reporter;

import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import au.org.intersect.samifier.reporter.DatabaseHelper;

import static org.mockito.Mockito.*;

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
    
    @Test
    public void testPrintMetadata() throws SQLException {
    	PrintStream ps = mock(PrintStream.class);
    	
    	String expectedString = "\nTABLE NAME: Result"
    			+ "\n-----------------------"
    			+ "\nproteinId"
    			+ "\nlocusName"
    			+ "\ngeneId"
    			+ "\nscore"
    			+ "\nstartPosition"
    			+ "\nstopPosition"
    			+ "\nlengthInAminoacids"
    			+ "\nchromosomeId"
    			+ "\ngeneStart"
    			+ "\ngeneEnd"
                + "\nstrand"
    			+ "\nframe"
    			+ "\nexons"
    			+ "\nexonString"
                + "\nqueryid"
                + "\nPEPTIDE_SEQUENCE"
                + "\nFILENAME"
                + "\nCOMMENTS";
    	expectedString = expectedString.toUpperCase();
    	DatabaseHelper db = new DatabaseHelper();
    	db.printTableDetails(ps);
    	
    	verify(ps).println(expectedString);
    	
    }
}

