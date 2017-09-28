package stupidapp.com.smsapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    EditText input;
    SmsManager smsManager = SmsManager.getDefault();
    private static MainActivity inst;
    static boolean active = false;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public static MainActivity instance() {
        return inst;
    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
        inst = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = (ListView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = smsInboxCursor.getColumnIndex("body");

        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS de la part de " + smsInboxCursor.getString(indexAddress) + "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());

        smsInboxCursor.close();
    }

    public void updateInbox(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void OnSendClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            smsManager.sendTextMessage("0782972827", null, input.getText().toString(), null, null);
            Toast.makeText(this, "Message envoyé", Toast.LENGTH_SHORT).show();
        }

    }


    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Veuillez autoriser l'application a accéder aux SMS", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission (SMS) accordée", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Permission (SMS) non accordée", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
