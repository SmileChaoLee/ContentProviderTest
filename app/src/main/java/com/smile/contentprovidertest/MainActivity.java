package com.smile.contentprovidertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickAddName(View view) {
        // Add a new employee record
        // String URL = EmployeeContentProvider.providerURL;
        // Uri URI = Uri.parse(URL);

        ContentValues values = new ContentValues();
        values.put(EmployeeContentProvider.employeeName,
                ((EditText)findViewById(R.id.editText1)).getText().toString());

        values.put(EmployeeContentProvider.employeePhone,
                ((EditText)findViewById(R.id.editText2)).getText().toString());

        Uri uri = getContentResolver().insert(EmployeeContentProvider.contentURI, values);

        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

    public void onClickRetrieveEmployees(View view) {

        // Retrieve  employee records
        // String URL = "content://com.smile.provider.SmileCompany/employees";
        // String URL = EmployeeContentProvider.providerURL();
        // Uri URI = Uri.parse(URL);

        // String selection = EmployeeContentProvider.employeeId + " = ? ";
        // String[] selectionArgs = {"2"};

        String selection = null;
        String[] selectionArgs = null;
        String[] projection = {"emId", "emName", "emPhone"};
        Cursor c = getContentResolver().query(EmployeeContentProvider.contentURI, projection, selection, selectionArgs, "name");
        if (c.moveToFirst()) {
            do{
                /*
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(EmployeeContentProvider.employeeId)) +
                                ", " +  c.getString(c.getColumnIndex( EmployeeContentProvider.employeeName)) +
                                ", " + c.getString(c.getColumnIndex( EmployeeContentProvider.employeePhone)),
                        Toast.LENGTH_SHORT).show();
                        */


                Toast.makeText(this,
                        c.getString(0) +
                                ", " +  c.getString(1) +
                                ", " + c.getString(2),
                        Toast.LENGTH_SHORT).show();

            } while (c.moveToNext());
        }

        c.close();
    }
}
