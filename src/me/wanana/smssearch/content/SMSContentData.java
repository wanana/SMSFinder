package me.wanana.smssearch.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SMSContentData {

    public static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    
    private static final String TAG = "MessageDatabase";

    //The columns we'll include in the dictionary table
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

    
    
    public static  interface MessageColumns {

        public static String ID = "_id";

        public static String THREAD_ID = "thread_id";

        public static String ADDRESS = "address";

        public static String PERSON = "person";

        public static String DATE = "date";

        public static String DATE_SENT = "date_sent";

        public static String PROTOCOL = "protocol";

        public static String READ = "read";

        public static String STATUS = "cstatus";

        public static String TYPE = "type";

        public static String REPLY_PATH_PRSENT = "reply_path_present";

        public static String SUBJECT = "subject";

        public static String BODY = "body";

        public static String SERVICE_SENTER = "service_center";

        public static String LOCKED = "locked";

        public static String ERROR_CODE = "error_code";

        public static String SEEN = "seen";
    }

    
    /* TODO */
    private ContentResolver resolver;

 
    
    public SMSContentData(ContentResolver ConResolver) {
        resolver = ConResolver;
    }

    // TODO
    public String[] getMessagesByDateRange(Date from, Date to) {
        return null;
    }

    public String[] getMessagesSinceDate(Date from) {
        return getMessagesByDateRange(from, new Date());
    }

    public Cursor getMessage(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] { rowId };

        return query(selection, selectionArgs, columns);

    }

    public String[] getMessageByKeyword(String keyword) {
        String whereSql = "cperson like ? or cbody  like ?";
        String word1 = "%" + keyword + "%";
        Cursor cur = query(whereSql, new String[] { word1, word1 }, null);
        ArrayList<String> msgList = new ArrayList<String>();

        int hitung = 0;
        while (cur.moveToNext()) {
            String sms = "";
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)
                    + "\n";
            msgList.add(sms);
            if (hitung == getMessageCountUnread())
                break;
            hitung++;
        }
        return (String[]) msgList.toArray();

    }

    public Cursor getCursor(String keyword) {
        String whereSql = "person like ? or body  like ?";
        String word1 = "%" + keyword + "%";
        Cursor cur = resolver.query(SMS_INBOX, null, whereSql, new String[] {
                word1, word1 }, null);

        return cur;

    }

    //FIXME could not get result
    public Cursor getCursorColumns(String query, String[] columns) {
        String selection = "person like ? or body  like ?";
        String word1 = "%" + selection + "%";

        return resolver.query(SMS_INBOX, null, selection, new String[] { word1,
                word1 }, null);

    }

    public int getMessageCountUnread() {
        Cursor c = resolver.query(SMS_INBOX, null, "read = 0", null, null);
        int unreadMessagesCount = c.getCount();
        c.deactivate();
        return unreadMessagesCount;
    }

    public String getMessageAll() {
        // TODO
        return "";
    }

    public String getMessageUnread() {
        // TODO
        return "";
    }

    public void setMessageStatusRead() {
        ContentValues values = new ContentValues();
        values.put("read", true);
        // TODO resolver.update(SMS_INBOX, values, "_id=" + SmsMessageId, null);
    }

    private Cursor query(String selection, String[] selectionArgs,
            String[] columns) {
        Cursor cursor = resolver.query(SMS_INBOX, columns, selection,
                selectionArgs, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}