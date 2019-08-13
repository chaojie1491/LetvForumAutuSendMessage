package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接 Sqlite
 */
public class SqliteUtil {

    private static Connection connection;

    public synchronized static Connection getConnection() throws SQLException {
        //如果 当前练
        if (connection == null) {
            try {
                String driverClass = "org.sqlite.JDBC";
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = "jdbc:sqlite:src/main/resources/db/letv.db";
            return connection = DriverManager.getConnection(url);
        } else {
            return connection;
        }
    }
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("连接未开启！");
        }
    }

//    private static final String BBS_DB = "create table bbs(" +
//            "  bbs_id integer not null primary key autoincrement," +
//            "  bbs_name text(40)," +
//            "  bbs_domian text(40)," +
//            "  bbs_auth_key text(40)," +
//            "  bbs_auth_value text(40)," +
//            "  bbs_saltkey text(40)," +
//            "  bbs_saltvalue text(40)," +
//            "  bbs_send_content text(50)" +
//            ");";
//
    private static final String DB_CONFIG = "create table letv_config (" +
            "  config_id integer(11) not null," +
            "  config_message_text text(50) not null," +
            "  primary key (config_id)" +
            ");";

    private static final String DB_COOKIE = "create table letv_cookie (" +
            "  cookie_id integer(11) not null," +
            "  cookie_key text(20)," +
            "  cookie_value text(20)," +
            "  primary key (cookie_id)" +
            ");";

    private static final String DB_LINK = "create table letv_link (" +
            "  letv_link_id integer(11) not null," +
            "  letv_link_name text(40) not null," +
            "  letv_link text(50) not null," +
            "  letv_last_time text," +
            "  primary key (letv_link_id)" +
            ");";
    public static final String DB_USER = "create table letv_user (" +
            "  letv_user_id integer(11) not null," +
            "  letv_user_uid text(11)," +
            "  letv_user_link text(40)," +
            "  primary key (letv_user_id)" +
            ");";

    public static void createDatabases() throws SQLException, IOException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute(DB_CONFIG);
        statement.execute(DB_COOKIE);
        statement.execute(DB_LINK);
        statement.execute(DB_USER);
        close();
    }

}
