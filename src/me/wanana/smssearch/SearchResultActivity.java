package me.wanana.smssearch;

import me.wanana.smssearch.content.SMSContentSearcher;
import me.wanana.smssearch.content.SMSMessage;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultActivity extends Activity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system
        // calls this method
        // to deliver the intent if this activity is currently the foreground
        // activity when
        // invoked again (when the user executes a search from this activity, we
        // don't create
        // a new instance of this activity, so the system delivers the search
        // intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SMSContentSearcher testMsg = new SMSContentSearcher(
                    getContentResolver());
            Cursor cursor = testMsg.getCursorByKeyword(query);

            // Specify the columns we want to display in the result
            String[] from = new String[] { SMSMessage.PERSON, SMSMessage.DATE,
                    SMSMessage.BODY };

            // Specify the corresponding layout elements where we want the
            // columns to go
            int[] to = new int[] { R.id.person, R.id.date, R.id.body };

            // Create a simple cursor adapter for the definitions and apply them
            // to the ListView

            SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                    R.layout.result, cursor, from, to);
            mListView.setAdapter(words);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget;
                                                 // expand it by default

        return true;

    }
}