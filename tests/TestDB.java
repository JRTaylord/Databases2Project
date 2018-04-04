
import org.junit.After;
import org.junit.Before;
import simpledb.buffer.BufferMgr;
import simpledb.server.SimpleDB;
import simpledb.remote.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.sql.*;

/**
 * CS4432-Project1:
 * For testing the overall functionality of the db
 * Must have the database running in the background to run general tests
 */
public class TestDB {

    Connection conn;
    Statement stmt;

    /**
     * CS4432-Project1:
     * Creates the tables for testing the overall db functionality
     */
    @Before
    public void setup() {
        conn = null;
        try {
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);
            stmt = conn.createStatement();

            String s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
            stmt.executeUpdate(s);

            s = "create table DEPT(DId int, DName varchar(8))";
            stmt.executeUpdate(s);

            s = "insert into DEPT(DId, DName) values ";
            String[] deptvals = {"(10, 'compsci')",
                    "(20, 'math')",
                    "(30, 'drama')"};
            for (int i = 0; i < deptvals.length; i++)
                stmt.executeUpdate(s + deptvals[i]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * CS4432-Project1:
     * Tests selection from the database after inserting one entry into the student table
     */
    @Test
    public void testSelect(){
        try {
            String s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
            String[] studvals = {"(1, 'joe', 10, 2004)"};
            for (int i = 0; i < studvals.length; i++)
                stmt.executeUpdate(s + studvals[i]);

            s = "select SName, MajorID, GradYear from STUDENT where SId = 1";
            ResultSet rst = stmt.executeQuery(s);
            rst.next();
            assertEquals("joe",rst.getString("SName"));
            assertEquals(10,rst.getInt("MajorID"));
            assertEquals(2004,rst.getInt("GradYear"));
            rst.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * CS4432-Project1:
     * Cleans up the tables after each test
     */
    @After
    public void cleanup(){
        try{
            String s = "DELETE FROM DEPT";
            stmt.executeUpdate(s);

            s = "DELETE FROM STUDENT";
            stmt.executeUpdate(s);

            //s = "DROP TABLE DEPT";
            //stmt.executeUpdate(s);

            //s = "DROP TABLE STUDENT";
            //stmt.executeUpdate(s);
        } catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
