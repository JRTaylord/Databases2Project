
import org.junit.After;
import org.junit.Before;
import simpledb.buffer.BufferMgr;
import simpledb.server.SimpleDB;
import simpledb.remote.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDB {

    Connection conn;
    Statement stmt;

    @Before
    public void setup() {
        conn = null;
        try {
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);
            stmt = conn.createStatement();

            String s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
            stmt.executeUpdate(s);
            System.out.println("Table STUDENT created.");

            s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
            String[] studvals = {"(1, 'joe', 10, 2004)",
                    "(2, 'amy', 20, 2004)",
                    "(3, 'max', 10, 2005)",
                    "(4, 'sue', 20, 2005)",
                    "(5, 'bob', 30, 2003)",
                    "(6, 'kim', 20, 2001)",
                    "(7, 'art', 30, 2004)",
                    "(8, 'pat', 20, 2001)",
                    "(9, 'lee', 10, 2004)"};
            for (int i = 0; i < studvals.length; i++)
                stmt.executeUpdate(s + studvals[i]);
            System.out.println("STUDENT records inserted.");


            s = "create table DEPT(DId int, DName varchar(8))";
            stmt.executeUpdate(s);
            System.out.println("Table DEPT created.");

            s = "insert into DEPT(DId, DName) values ";
            String[] deptvals = {"(10, 'compsci')",
                    "(20, 'math')",
                    "(30, 'drama')"};
            for (int i = 0; i < deptvals.length; i++)
                stmt.executeUpdate(s + deptvals[i]);
            System.out.println("DEPT records inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @After
    public void cleanup(){
        try{
            String s = "DELETE * FROM DEPT";
            stmt.executeUpdate(s);

            s = "DROP TABLE DEPT";
            stmt.executeUpdate(s);

            s = "DELETE * FROM STUDENT";
            stmt.executeUpdate(s);

            s = "DROP TABLE STUDENT";
            stmt.executeUpdate(s);
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
