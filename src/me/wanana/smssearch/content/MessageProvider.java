//MessageProvider.java

package me.wanana.smssearch.content;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 
 * @author wanana
 * @date Aug 20, 2013 5:16:32 PM
 */
public class MessageProvider extends ContentProvider {

    String TAG = "MessageFinderProvider";

    public static String AUTHORITY = MessageProvider.class.getName();

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/message");

    public static final String TYPE_STRING = "wanana.smssearch";

    // MIME types used for searching words or looking up a single definition
    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd." + TYPE_STRING;

    public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd." + TYPE_STRING;

    private SMSContentData mSearcher;

    // UriMatcher stuff
    private static final int SEARCH_MSGS = 0;

    private static final int GET_MSG = 1;

    private static final int SEARCH_SUGGEST = 2;

    private static final UriMatcher sURIMatcher = buildUriMatcher();

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */
    private static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "message", SEARCH_MSGS);
        matcher.addURI(AUTHORITY, "message/#", GET_MSG);

        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,
                SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
                SEARCH_SUGGEST);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mSearcher = new SMSContentData(getContext().getContentResolver());
        return true;
    }

    /**
     * Handles all the dictionary searches and suggestion queries from the
     * Search Manager. When requesting a specific word, the uri alone is
     * required. When searching all of the dictionary for matches, the
     * selectionArgs argument must carry the search query as the first element.
     * All other arguments are ignored.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        // Use the UriMatcher to see what kind of query we have and format the
        // db query accordingly
        switch (sURIMatcher.match(uri)) {
        case SEARCH_SUGGEST:
            if (selectionArgs == null) {
                throw new IllegalArgumentException(
                        "selectionArgs must be provided for the Uri: " + uri);
            }
            return getSuggestions(selectionArgs[0]);
        case SEARCH_MSGS:
            if (selectionArgs == null) {
                throw new IllegalArgumentException(
                        "selectionArgs must be provided for the Uri: " + uri);
            }
            return search(selectionArgs[0]);
        case GET_MSG:
            Log.d("GET_MSG", "1");
            return getWord(uri);
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private Cursor getSuggestions(String query) {

        Cursor cur = mSearcher.getCursor(query);

        MatrixCursor maCur = new MatrixCursor(new String[] { BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1 });
        // FIXME display the begining 10 characters and text since query
        // FIXME hight the text
        while (cur.moveToNext()) {
            String sms = cur.getString(12);
            sms = sms.substring(sms.indexOf(query), sms.length());
            maCur.addRow(new String[] { cur.getString(0), sms });

        }

        return maCur;

        // return mSearcher.getCursorByKeyword(query);
    }

    private Cursor search(String query) {
        return mSearcher.getCursor(query);
    }

    private Cursor getWord(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] { SMSContentData.MessageColumns.ID,
                SMSContentData.MessageColumns.BODY };

        return mSearcher.getMessage(rowId, columns);
    }

    /**
     * This method is required in order to query the supported types. It's also
     * useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
        case SEARCH_MSGS:
            return WORDS_MIME_TYPE;
        case GET_MSG:
            return DEFINITION_MIME_TYPE;
        case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    // Other required implementations...

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

}
