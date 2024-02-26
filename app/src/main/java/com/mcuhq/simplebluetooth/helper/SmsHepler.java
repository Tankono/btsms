package com.mcuhq.simplebluetooth.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;

import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.bluetooth.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class SmsHepler {
    private static SmsHepler instance;
    private ContentResolver contentResolver;
    private SmsHepler(Context context){
        contentResolver = context.getContentResolver();
    }
    public static SmsHepler Instance(){
        return instance;
    }
    public static void init(Context context){
        instance = new SmsHepler(context);
    }
    private static void printColumn(Cursor cursor){
        Logger.log("Column Count:"+cursor.getColumnCount());
        for (String s : cursor.getColumnNames()){
            Logger.log("COL:"+s);
        }
        Logger.log("Count:"+cursor.getCount());
    }
    public ArrayList<MessagEntity> getMMS(String threadId) {
        ArrayList<MessagEntity> sms = new ArrayList<>();
        final String[] projection = new String[]{"*"};
        Uri uri = Uri.parse("content://mms-sms/conversations/");
        uri = Telephony.Mms.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, "thread_id = "+threadId, null, "date ASC");

        printColumn(cursor);
        while(cursor.moveToNext()) {
            String body = null;
            Bitmap bitmap = null;
            String address = getANumber(cursor.getInt(cursor.getColumnIndexOrThrow ("_id")));

            int int_Type = cursor.getInt(cursor.getColumnIndexOrThrow("m_type"));
            String selectionPart = new String ("mid = '" + cursor.getString(0) + "'");
            Cursor curPart = contentResolver. query (Uri.parse ("content://mms/part"), null, selectionPart, null, null);
            while(curPart.moveToNext()) {
                if(curPart.getString(3).equals("image/jpeg")) {
                    bitmap = getMmsImage(curPart.getString(0));
                }
                else if ("text/plain".equals(curPart.getString(3))) {
                    String data = curPart.getString(curPart.getColumnIndexOrThrow("_data"));
                    if (data != null) {
                        body = getMmsText(curPart.getString(0));
                    } else {
                        body = curPart.getString(curPart.getColumnIndexOrThrow("text"));
                    }
                }
            }
            curPart.close();

            MessagEntity obj = new MessagEntity();
            obj.id = ""+cursor.getInt(cursor.getColumnIndexOrThrow ("_id"));
            obj.isSMS = false;
            obj.bitmap = bitmap;
            obj.body = body;
            if(address.equalsIgnoreCase("insert-address-token")){
                address = "";
            }
            obj.sender = address;
            obj.dateTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date"))*1000);

            if(int_Type == 128){
                obj.type = "sent";
            }

            sms.add(obj);

        }
        cursor.close();
        return sms;
    }
    private Bitmap getMmsImage(String _id) {
        Uri partURI = Uri.parse("content://mms/part/" + _id);
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = contentResolver.openInputStream(partURI);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return bitmap;
    }

    private String[] getContactByNumber(final String number) {
        String[] data = new String[2];
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            Cursor cur = contentResolver.query(uri,
                    new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID },
                    null, null, null);
            if (cur.moveToFirst()) {
                int nameIdx = cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                data[0] = cur.getString(nameIdx);
                String contactId = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                String photoUri = getContactPhotoUri(Long.parseLong(contactId));
                if (photoUri != null)
                    data[1] = photoUri;
                else
                    data[1] = null;
                cur.close();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Get contact photo URI from contact Id
     * @param contactId
     * @return imagePath
     */
    private String getContactPhotoUri(long contactId) {
        Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
                contactId);
        String imagePath = null ;
        if(photoUri != null)
        {
            imagePath = photoUri.toString() ;
        }
        return imagePath;
    }

    private String getANumber(int id) {
        String addr = "";
        final String[] projection = new String[] {"address","contact_id","charset","type"};
        Uri.Builder builder = Uri.parse("content://mms").buildUpon();
        builder.appendPath(String.valueOf(id)).appendPath("addr");
        Cursor cursor = contentResolver.query(
                builder.build(),
                projection,
                null,
                null, null);
        if (cursor.moveToFirst()) {
            addr = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        }
        return addr;
    }

    private String getMmsText(String id) {
        Uri partURI = Uri.parse("content://mms/part/" + id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = contentResolver.openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String temp = reader.readLine();
                while (temp != null) {
                    sb.append(temp);
                    temp = reader.readLine();
                }
            }
        } catch (IOException e) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return sb.toString();
    }


    public ArrayList<MessagEntity> getSmsByThread() {
        ArrayList<MessagEntity> sms = new ArrayList<>();
        Cursor c = contentResolver.query(Telephony.Sms.CONTENT_URI, null, "thread_id IS NOT NULL) GROUP BY (thread_id", null, "date DESC");
        if(c == null) return sms;
        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int j = 0; j < totalSMS; j++) {
                MessagEntity obj = getSMSObject(c);
                obj.type = "short";
                sms.add(obj);
                c.moveToNext();
            }
        }
        c.close();

        return sms;
    }
    private static MessagEntity getSMSObject(Cursor cursor){
        MessagEntity obj = new MessagEntity();
        obj.id          = getStringCols(cursor, Telephony.Sms._ID);
        obj.sender      = getStringCols(cursor, Telephony.Sms.ADDRESS);
        obj.body        = getStringCols(cursor, Telephony.Sms.BODY);
        obj.threadId    = getStringCols(cursor,Telephony.Sms.THREAD_ID);
        String smsDate  = getStringCols(cursor,Telephony.Sms.DATE);
        Date dateFormat = new Date(Long.valueOf(smsDate));
        obj.dateTime    = dateFormat;
        String type     = "na";
        switch (Integer.parseInt(getStringCols(cursor,Telephony.Sms.TYPE))) {
            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                type = "inbox";
                break;
            case Telephony.Sms.MESSAGE_TYPE_SENT:
                type = "sent";
                break;
            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                type = "outbox";
                break;
            default:
                break;
        }
        obj.type = type;
        return obj;
    }
    private static String getStringCols(Cursor cursor, String name){
        return  cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    public ArrayList<MessagEntity> getSmsByThread(String threadId) {
        ArrayList<MessagEntity> sms = new ArrayList<>();
        Cursor c = contentResolver.query(Telephony.Sms.CONTENT_URI,
                null, "thread_id = "+threadId,
                null, "date ASC");

        int totalSMS = 0;
        if (c == null) return sms;
        totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int j = 0; j < totalSMS; j++) {
                MessagEntity obj = getSMSObject(c);
                sms.add(obj);
                c.moveToNext();

            }
        }
        c.close();
        return sms;
    }
    public void sendSms(MessagEntity sms){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sms.sender, null, sms.body, null, null);
    }

    public void synSms(MessagEntity sms){

    }
}
