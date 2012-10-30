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
	private Connection connection = null;
	
	public void connect()
	{	
		String url = "jdbc:hsqldb:mem:ap11";
		try
		{
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection(url, USER, PASSWORD);
		}
		catch (Exception e)
		{
			System.out.println("Could not connect to SQL database");
			e.printStackTrace();
		}
	}
	
	public void disconnect() throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.execute("SHUTDOWN");
		connection.close();
	}
	 
	public synchronized void generateTables() throws SQLException
	{
		Statement statement = connection.createStatement();
		StringBuffer query = new StringBuffer();
		query.append("CREATE TABLE result (");
		query.append("proteinId int,");
		query.append("locusName varchar(255),");
		query.append("geneId int,");
		query.append("score varchar(255),");
		query.append("startPosition varchar(255),");
		query.append("stopPosition varchar(255),");
		query.append("lengthInAminoacids varchar(255),");
		query.append("chromosomeId int,");
		query.append("geneStart varchar(255),");
		query.append("geneEnd varchar(255),");
		query.append("frame varchar(255),");
		query.append("exons varchar(255),");
		query.append("exonString varchar(255)");
		query.append(")");
		statement.execute(query.toString());
	}
	
	public synchronized Collection<String> filterResult(String expression) throws SQLException
	{
		PreparedStatement statement = connection.prepareCall(expression);
		ResultSet resultSet = statement.executeQuery();
		Collection<String> collection = new ArrayList<String>();
		while (resultSet.next())
		{
			Integer proteinId = resultSet.getInt("proteinId");
			String locusName = resultSet.getString("locusName");
			Integer geneId = resultSet.getInt("geneId");
			String score = resultSet.getString("score");
			String startPosition = resultSet.getString("startPosition");
			String stopPosition = resultSet.getString("stopPosition");
			String lengthInAminoacids = resultSet.getString("lengthInAminoacids");
			Integer chromosomeId = resultSet.getInt("chromosomeId");
			String geneStart = resultSet.getString("geneStart");
			String geneEnd = resultSet.getString("geneEnd");
			String frame = resultSet.getString("frame");
			String exons = resultSet.getString("exons");
			String exonString = resultSet.getString("exonString");
			String result = proteinId + DELIMITER + locusName + DELIMITER + geneId + DELIMITER
					+ score + DELIMITER + startPosition + DELIMITER + stopPosition + lengthInAminoacids + DELIMITER
					+ chromosomeId + DELIMITER + geneStart + DELIMITER + geneEnd + DELIMITER + frame + DELIMITER
					+ exons + DELIMITER + exonString;
			collection.add(result);
		}
		
		return collection;
	}
	
	public synchronized void query(String expression) throws SQLException
	{
		PreparedStatement statement = connection.prepareCall(expression);
		statement.executeQuery();
	}
}
