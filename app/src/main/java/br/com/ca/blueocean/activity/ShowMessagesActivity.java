package br.com.ca.blueocean.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.ca.blueocean.adapter.MessagesAdapter;
import br.com.ca.blueocean.hiveservices.HiveGetMessages;
import br.com.ca.blueocean.vo.MessageVo;
import br.com.ca.shareview.R;

/**
 * ShowMessagesActivity
 *
 * @author Rodrigo Carvalho
 */
public class ShowMessagesActivity extends AppCompatActivity {
    public final int SEND_MESSAGE_INTENT_CALL = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_showmessages, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //refresh messages reading from hive server and actualize list view
        new DoAsyncMessagesSynchronize().execute("demo");
    }

    // Call Back method  after sending message activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==SEND_MESSAGE_INTENT_CALL)
        {
            //refresh messages reading from hive server and actualize list view
            new DoAsyncMessagesSynchronize().execute("demo"); //TODO: change demo word for final version
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.action_refresh_messsages:
                //refresh messages reading from hive server and actualize list view
                new DoAsyncMessagesSynchronize().execute("demo"); //TODO: demo depricated - remove
                return true;

            case R.id.action_send_messsage:
                //send message activity
                Intent intent = new Intent(ShowMessagesActivity.this, SendMessageActivity.class);
                //Start Intent for result
                startActivityForResult(intent, SEND_MESSAGE_INTENT_CALL);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * refreshMessageListView
     *
     * Actualizes the activity List View that shows messages from users.
     *
     * @param messageVoList
     */
    private void refreshMessageListView(ArrayList<MessageVo> messageVoList){
        //
        // 1. pass context and data (ArrayList<MessageVo>) to the custom adapter
        //
        MessagesAdapter adapter = new MessagesAdapter(this, messageVoList);
        //
        // 2. Get ListView from activity_show_messages.xml layout
        //
        ListView listView = (ListView) findViewById(R.id.messages_listView);
        //
        // 3. setListAdapter
        //
        listView.setAdapter(adapter);
    }

    /**
     * DoAsyncMessagesSynchronize
     *
     * Uses AsyncTask to create a task away from the main UI thread, and synchronize the data with CA Server.
     */
    private class DoAsyncMessagesSynchronize extends AsyncTask<String, Void, ArrayList<MessageVo>> {
        Resources res = getResources();
        Context context = getApplicationContext();

        final ProgressDialog progressDialog = new ProgressDialog(ShowMessagesActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            //show progress dialog
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(res.getString(R.string.synchronizing));
            progressDialog.show();
        }

        @Override
        protected ArrayList<MessageVo> doInBackground(String... params) {

            ArrayList<MessageVo> items = new ArrayList<>();
            Context context = getApplicationContext();

            HiveGetMessages hiveGetMessages;

            //
            // uses hive services to get all messages from server
            //
            List<MessageVo> messageVoList;

            Log.d("ShowMessagesActivity", "call HiveGetMessages");
            hiveGetMessages = new HiveGetMessages(context);

            messageVoList = hiveGetMessages.getMessages();

            //access initiative list via Iterator
            Iterator iterator = messageVoList.iterator();
            while (iterator.hasNext()) {
                //get InitiativeVo
                MessageVo messageVo = (MessageVo) iterator.next();
                //add into ArrayList
                items.add(messageVo);
            }

            return items;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<MessageVo> result) {
            //close progress dialog
            progressDialog.dismiss();

            //parameter result is sent as a ArrayList<MessageVo> by doInBackGround,
            //refresh ListView
            refreshMessageListView(result);
        }

    }
}
