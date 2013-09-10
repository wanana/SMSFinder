package me.wanana.smssearch;

import me.wanana.smssearch.search.SMSContentSearcher;
import me.wanana.smssearch.search.IMessageColumns;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
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
import android.widget.TextView;

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
        // if (Intent.ACTION_VIEW.equals(intent.getAction())){

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);

            // Intent smsIntent = new Intent();
            //
            // ComponentName comp2 = new ComponentName("com.android.mms",
            // "com.android.mms.ui.SearchActivity");
            //
            // smsIntent.setAction("android.intent.action.SEARCH");
            // smsIntent.setComponent(comp2);
            //
            // intent.putExtra(SearchManager.QUERY, query);
            //
            // startActivity(smsIntent);

            SMSContentSearcher testMsg = new SMSContentSearcher(
                    getContentResolver());
            Cursor cursor = testMsg.getCursorByKeyword(query);

            // Specify the columns we want to display in the result
            String[] from = new String[] { IMessageColumns.THREAD_ID,
                    IMessageColumns.ID, IMessageColumns.ADDRESS,
                    IMessageColumns.DATE, IMessageColumns.BODY };

            int[] to = new int[] { R.id.thread_id, R.id.id, R.id.person,
                    R.id.date, R.id.body };

            SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                    R.layout.result, cursor, from, to);
            mListView.setAdapter(words);
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    TextView addressView = (TextView) view
                            .findViewById(R.id.person);
                    TextView threadIdView = (TextView) view
                            .findViewById(R.id.thread_id);
                    TextView idView = (TextView) view.findViewById(R.id.id);
                    Intent smsIntent = new Intent();
                    // smsIntent.setType("vnd.android-dir/mms-sms");

                    ComponentName comp = new ComponentName("com.android.mms",
                            "com.android.mms.ui.ComposeMessageActivity");
                    smsIntent.setComponent(comp);
                    smsIntent.setAction("android.intent.action.VIEW");

                    // smsIntent.putExtra("address", addressView.getText());
                    smsIntent.putExtra("thread_id",
                            Long.valueOf(threadIdView.getText().toString()));
                    smsIntent.putExtra("select_id",
                            Long.valueOf(idView.getText().toString())); //
                    smsIntent.putExtra("highlight", query);
                    // startActivity(Intent.createChooser(smsIntent, "SMS:"));
                    startActivity(smsIntent);
                }
            });

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
