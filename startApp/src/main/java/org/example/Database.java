package org.example;

import org.postgresql.jdbc.PgConnection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private PgConnection connection;

    public PgConnection getConnection() {
        return connection;
    }

    public Database() {
        try {
            //connection = DriverManager.getConnection("jdbc:postgresql://db/prac2?user=postgres&password=123").unwrap(PgConnection.class);
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/prac2?user=p_user&password=123").unwrap(PgConnection.class);
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
