package com.smile.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class EmployeeContentProvider extends ContentProvider {

    public static final String providerName = new String("com.smile.contentprovidertest.provider01");
    public static final String providerURL = new String("content://"+providerName+"/employees");
    public static final Uri contentURI = Uri.parse(providerURL);

    public static final String employeeId = new String("_id");
    public static final String employeeName = new String("name");
    public static final String employeePhone = new String("phone");

    private final static int EMPLOYEES = 1;
    private final static int EMPLOYEE_ID = 2;

    private final static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(providerName,"employees",EMPLOYEES);
        uriMatcher.addURI(providerName,"employees/#",EMPLOYEE_ID);
    }

    private static HashMap<String,String> employeesMap = new HashMap<String,String>();

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase employeeDb;
    private static final String databaseName = "SmileCompany";
    private static final String tableName = "employees";
    private static final int databaseVersion = 1;
    private static final String CREATE_DB_TABLE =
            " CREATE TABLE " + tableName +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " phone TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,databaseName,null,databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);

        }
    }
    // end of SQLiteDatabase creating


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        employeeDb = dbHelper.getWritableDatabase();
        if (employeeDb == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case EMPLOYEES:
                count = employeeDb.delete(tableName, selection, selectionArgs);
                break;

            case EMPLOYEE_ID:
                String id = uri.getPathSegments().get(1);
                count = employeeDb.delete( tableName,employeeId +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all employees  records
             */
            case EMPLOYEES:
                return "vnd.android.cursor.dir/vnd.smile.employees";

            /**
             * Get a particular employee
             */
            case EMPLOYEE_ID:
                return "vnd.android.cursor.item/vnd.smile.employees";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         *  Add a new employee record
         */
        long rowId = employeeDb.insert(tableName, "", values);
        /**
         * If record is added successfully
         */
        if (rowId > 0) {
            // successfully
            Uri _uri = ContentUris.withAppendedId(contentURI, rowId);
            // getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        } else {
            throw new SQLException("Failed to add a record into " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);

        employeesMap.clear();
        employeesMap.put("emId", employeeId);
        employeesMap.put("emName", employeeName);
        employeesMap.put("emPhone", employeePhone);
        qb.setProjectionMap(employeesMap);
        // qb.setStrict(true);

        switch (uriMatcher.match(uri)) {
            case EMPLOYEES:
                break;
            case EMPLOYEE_ID:
                qb.appendWhere( employeeId + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on employees  names
             */
            sortOrder = employeeName;
        }

        Cursor c = qb.query(employeeDb,	projection,	selection, selectionArgs,null, null, sortOrder);
        
        /**
         * register to watch a content URI for changes
         */
        // c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case EMPLOYEES:
                count = employeeDb.update(tableName, values, selection, selectionArgs);
                break;

            case EMPLOYEE_ID:
                count = employeeDb.update(tableName, values,employeeId + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        // getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

}
