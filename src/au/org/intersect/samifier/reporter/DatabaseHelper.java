package au.org.intersect.samifier.reporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseHelper {
	
	private static final String USER = "SA";
	private static final String PASSWORD = "";
	private static final String DELIMITER = "\t";
	
	private static Connection connection;
	
	public void connect()
	{	
		String url = "jdbc:hsqldb:mem:ap11";
		try
		{
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection(url, USER, PASSWORD);
		}
		catch (Exception e)
		{
			System.out.println("Could not connect to SQL database");
			e.printStackTrace();
		}
	}
	
	public void shutdown() throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.execute("SHUTDOWN;");
	}
	 
	public synchronized void generateTables() throws SQLException
	{
		Statement statement = connection.createStatement();
		StringBuffer query = new StringBuffer();
		query.append("CREATE TABLE Result (");
		query.append("proteinId varchar(255),");
		query.append("locusName varchar(255),");
		query.append("geneId varchar(255),");
		query.append("score varchar(255),");
		query.append("startPosition varchar(255),");
		query.append("stopPosition varchar(255),");
		query.append("lengthInAminoacids varchar(255),");
		query.append("chromosomeId varchar(255),");
		query.append("geneStart varchar(255),");
		query.append("geneEnd varchar(255),");
		query.append("frame varchar(255),");
		query.append("exons varchar(255),");
		query.append("exonString varchar(255)");
		query.append(");");
		statement.execute(query.toString());
	}
	
	public synchronized Collection<String> filterResult(String expression) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(expression);
		Collection<String> collection = new ArrayList<String>();
		while (resultSet.next())
		{
			String proteinId = resultSet.getString("proteinId");
			String locusName = resultSet.getString("locusName");
			String geneId = resultSet.getString("geneId");
			String score = resultSet.getString("score");
			String startPosition = resultSet.getString("startPosition");
			String stopPosition = resultSet.getString("stopPosition");
			String lengthInAminoacids = resultSet.getString("lengthInAminoacids");
			String chromosomeId = resultSet.getString("chromosomeId");
			String geneStart = resultSet.getString("geneStart");
			String geneEnd = resultSet.getString("geneEnd");
			String frame = resultSet.getString("frame");
			String exons = resultSet.getString("exons");
			String exonString = resultSet.getString("exonString");
			String result = proteinId + DELIMITER + locusName + DELIMITER + geneId + DELIMITER
					+ score + DELIMITER + startPosition + DELIMITER + stopPosition + DELIMITER + lengthInAminoacids + DELIMITER
					+ chromosomeId + DELIMITER + geneStart + DELIMITER + geneEnd + DELIMITER + frame + DELIMITER
					+ exons + DELIMITER + exonString;
			collection.add(result);
		}
		return collection;
	}
	
	public synchronized void executeQuery(String expression) throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.execute(expression);
	}
}
