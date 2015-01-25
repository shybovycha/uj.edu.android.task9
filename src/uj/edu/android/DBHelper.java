package uj.edu.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by shybovycha on 24.01.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    protected static final String DBNAME = "charges.db";
    protected static final int DBVERSION = 1;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    protected static String dropDatabaseQuery() {
        StringBuilder sql = new StringBuilder();
        ArrayList<String> queries = new ArrayList<String>();

        queries.add("drop table if exists charges;");

        for (String query : queries) {
            sql.append(query);
        }

        return sql.toString();
    }

    protected static String createDatabaseQuery() {
        StringBuilder sql = new StringBuilder();
        ArrayList<String> queries = new ArrayList<String>();

        queries.add("create table charges (_id integer primary key autoincrement, start_time integer, end_time integer);");

        for (String query : queries) {
            sql.append(query);
        }

        return sql.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createDatabaseQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(dropDatabaseQuery());
        sqLiteDatabase.execSQL(createDatabaseQuery());
    }
}
