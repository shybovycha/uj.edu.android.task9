package uj.edu.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shybovycha on 24.01.15.
 */
public class ChargesDAO {
    protected SQLiteDatabase database;
    protected DBHelper dbHelper;

    public ChargesDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public ChargeEntry startCharging() {
        ChargeEntry entry = new ChargeEntry();
        ContentValues values = new ContentValues();

        values.put("start_time", entry.getStartTime());

        database = dbHelper.getWritableDatabase();

        long entryId = database.insert("charges", null, values);

        database.close();

        entry.setId(entryId);

        return entry;
    }

    public ChargeEntry stopCharging() {
        ChargeEntry entry = getLastlyStartedCharge();

        if (entry == null) {
            return null;
        }

        entry.stopCharging();

        database = dbHelper.getWritableDatabase();

        String sql = String.format("update charges set end_time = %d where _id = %d;", entry.getEndTime(), entry.getId());

        database.execSQL(sql);

        database.close();

        return entry;
    }

    public List<ChargeEntry> all() {
        List<ChargeEntry> results = new ArrayList<ChargeEntry>();

        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select _id, start_time, end_time from charges order by start_time desc", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ChargeEntry entry = cursorToChargeEntry(cursor);
            results.add(entry);
            cursor.moveToNext();
        }

        database.close();

        return results;
    }

    public void deleteAll() {
        database = dbHelper.getWritableDatabase();

        database.execSQL("delete from charges where _id is not null;");

        database.close();
    }

    public float averageDailyChargingTime() {
        // List<Long> results = new ArrayList<Long>();
        float result = 0;

        database = dbHelper.getReadableDatabase();

        // Cursor cursor = database.rawQuery("select AVG(end_time - start_time) from charges group by ROUND(start_time / (60 * 60 * 24))", null);
        Cursor cursor = database.rawQuery("select AVG(end_time - start_time) from charges", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            // results.add(cursor.getLong(0));
            result = cursor.getFloat(0);
            cursor.moveToNext();
        }

        database.close();

        return result;
    }

    public float totalDailyChargingTime() {
        // List<Long> results = new ArrayList<Long>();
        float result = 0;

        database = dbHelper.getReadableDatabase();

        long yesterdayBeginningOfDay = new Date().getTime() - (24 * 60 * 60 * 1000);

        // Cursor cursor = database.rawQuery("select SUM(end_time - start_time) from charges group by ROUND(start_time / (60 * 60 * 24))", null);
        Cursor cursor = database.rawQuery("select SUM(end_time - start_time) from charges where start_time > ?", new String [] { String.format("%d", yesterdayBeginningOfDay) });

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            // results.add(cursor.getLong(0));
            result = cursor.getFloat(0);
            cursor.moveToNext();
        }

        database.close();

        return result;
    }

    protected ChargeEntry getLastlyStartedCharge() {
        ChargeEntry result = null;

        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select _id, start_time, end_time from charges where end_time is null", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            result = cursorToChargeEntry(cursor);
            cursor.moveToNext();
        }

        database.close();

        return result;
    }

    protected ChargeEntry cursorToChargeEntry(Cursor cursor) {
        ChargeEntry entry = new ChargeEntry();

        entry.setId(cursor.getLong(0));
        entry.setStartTime(cursor.getLong(1));
        entry.setEndTime(cursor.getLong(2));

        return entry;
    }
}
