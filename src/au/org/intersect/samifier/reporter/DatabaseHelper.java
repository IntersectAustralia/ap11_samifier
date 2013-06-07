package au.org.intersect.samifier.reporter;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.hsqldb.server.Server;

import au.org.intersect.samifier.domain.DebuggingFlag;

public class DatabaseHelper {

    private static final String USER = "SA";
    private static final String PASSWORD = "";
    private static final String DELIMITER = "\t";

    private static Connection connection;

    public DatabaseHelper() {
        Server hsqlServer = null;
        try {
            hsqlServer = new Server();
            hsqlServer.setDatabaseName(0, "ap11");
            hsqlServer.setDatabasePath(0, "mem:ap11");

            hsqlServer.setSilent(true);
            hsqlServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hsqlServer.shutdown();
        }
    }

    public void connect() {
        String url = "jdbc:hsqldb:mem:ap11";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            connection = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Could not connect to SQL database");
            e.printStackTrace();
        }
    }

    public void shutdown() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SHUTDOWN;");
    }

    
    public synchronized void generateTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE Result IF EXISTS");
        StringBuffer query = new StringBuffer();
        query.append("CREATE TABLE Result (");
        query.append("proteinId varchar(255),");
        query.append("locusName varchar(255),");
        query.append("geneId varchar(255),");
        query.append("score numeric(255, 2),"); /// Change by Aidan
        query.append("startPosition integer,"); /// Change by Aidan
        query.append("stopPosition integer,"); /// Change by Aidan
        query.append("lengthInAminoacids integer,"); /// Change by Aidan
        query.append("chromosomeId varchar(255),");
        query.append("geneStart integer,"); /// Change by Aidan
        query.append("geneEnd integer,"); /// Change by Aidan
        query.append("strand varchar(255),");
        query.append("frame integer,");
        query.append("exons integer,"); /// Change by Aidan
        query.append("exonString varchar(255),"); /// Change by Ignatius Pang  *%*%*%
        query.append("queryId varchar(255), ");
        query.append("peptide_sequence varchar(255), ");
        query.append("filename varchar(255), ");
        if (DebuggingFlag.get_sbi_debug_flag() == 1) {// / Change by Ignatius
                                                      // Pang *%*%*%
            
            query.append("comments varchar(255), ");
            query.append("validatedSequence varchar(255), ");
        } else {
            query.append("comments varchar(255)");
        }

        query.append(");");
        statement.execute(query.toString());
    }

    public synchronized Collection<String> filterResult(String expression)
            throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(expression);
        Collection<String> collection = new ArrayList<String>();
        int cols = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            StringBuilder sb = new StringBuilder();
            for (int c = 1; c <= cols; c++) {
                if (c > 1) {
                    sb.append(DELIMITER);
                }
                sb.append(resultSet.getString(c));
            }
            collection.add(sb.toString());
        }
        return collection;
    }

    public synchronized void execute(String expression) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(expression);
    }

    public synchronized ResultSet executeQuery(String expression)
            throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(expression);
    }

    public void printMetadata(PrintStream ps) throws SQLException {
        if (ps == null) {
            ps = System.out;
        }

        StringBuilder sb = new StringBuilder();

        ResultSet rs = executeQuery("SELECT * FROM information_schema.system_columns WHERE table_name='RESULT'");

        boolean first = true;

        while (rs.next()) {
            if (first) {
                sb.append("\nTABLE NAME: " + rs.getString("TABLE_NAME"));
                sb.append("\n-----------------------");
                first = false;
            }
            sb.append('\n');
            sb.append(rs.getString("COLUMN_NAME"));
        }
        rs.close();

        ps.println(sb.toString());
    }

    public void printTableDetails(PrintStream ps) throws SQLException {
        connect();
        generateTables();
        printMetadata(ps);
    }
}
