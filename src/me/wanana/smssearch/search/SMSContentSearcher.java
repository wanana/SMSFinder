package me.wanana.smssearch.search;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMSContentSearcher {

    public static final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    private ContentResolver resolver;

    public SMSContentSearcher(ContentResolver ConResolver) {
        resolver = ConResolver;
    }

    // TODO
    public String[] getMessagesByDateRange(Date from, Date to) {
        return null;
    }

    public String[] getMessagesSinceDate(Date from) {
        return getMessagesByDateRange(from, new Date());
    }

    public String getMessage(int limit) {
        Cursor cur = resolver.query(SMS_INBOX, null, null, null, null);
        String sms = "Message >> \n";
        int conunt = 0;
        /*
         * for (String col : cur.getColumnNames()) {
         * Log.d(SMSMessage.class.getName(), "c" + col + "  ");
         * 
         * }
         */
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(12)
                    + "\n";
            if (conunt == limit)
                break;
            conunt++;
        }
        return sms;
    }

    public String[] getMessageByKeyword(String keyword) {
        String whereSql = "cperson like ? or cbody  like ?";
        String word1 = "%" + keyword + "%";
        Cursor cur = resolver.query(SMS_INBOX, null, whereSql, new String[] {
                word1, word1 }, null);
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

    public Cursor getCursorByKeyword(String keyword) {
        String whereSql = "person like ? or body  like ?";
        String word1 = "%" + keyword + "%";
        Cursor cur = resolver.query(SMS_INBOX, null, whereSql, new String[] {
                word1, word1 }, null);

        return cur;

    }

    public int getMessageCountUnread() {
        Cursor c = resolver.query(SMS_INBOX, null, "read = 0", null, null);
        int unreadMessagesCount = c.getCount();
        c.deactivate();
        return unreadMessagesCount;
    }

    public String getMessageAll() {
        Cursor cur = resolver.query(SMS_INBOX, null, null, null, null);
        String sms = "Message >> \n";
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)
                    + "\n";
        }
        return sms;
    }

    public String getMessageUnread() {
        Cursor cur = resolver.query(SMS_INBOX, null, null, null, null);
        String sms = "Message >> \n";
        int count = 0;
        while (cur.moveToNext()) {
            sms += "From :" + cur.getString(2) + " : " + cur.getString(11)
                    + "\n";
            if (count == getMessageCountUnread())
                break;
            count++;
        }
        return sms;
    }

    public void setMessageStatusRead() {
        ContentValues values = new ContentValues();
        values.put("read", true);
        // TODO resolver.update(SMS_INBOX, values, "_id=" + SmsMessageId, null);
    }

}