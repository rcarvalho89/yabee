package br.com.ca.asap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import br.com.ca.asap.hiveservices.HiveSendMessage;
import br.com.ca.asap.user.CurrentUser;
import br.com.ca.asap.user.SignManager;
import br.com.ca.asap.vo.MessageVo;
import br.com.ca.asap.vo.UserVo;
import br.com.ca.shareview.R;

/**
 * SendMessageActivity
 *
 * This activity allows editing and sending a text message
 *
 * @author Rodrigo Carvalho
 */
public class SendMessageActivity extends AppCompatActivity {

    /**
     * onCreate
     *
     * Create activity, set the layout and associate a click listener for the button present in the layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check for empty message
                EditText messageEditText = (EditText) findViewById(R.id.msgEditText);

                if (messageEditText.getText().toString().equals("")){
                    Resources res = getResources();
                    Snackbar.make(view, res.getString(R.string.empty_message_not_permitted), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    //send message
                    String msgText = (String) (((EditText) findViewById(R.id.msgEditText)).getText()).toString();
                    new AsyncSendMessage().execute(String.valueOf(msgText));

                    setResult(Activity.RESULT_OK);
                    finish();
                }

            }
        });
    }

    /**
     * AsyncSendMessage
     *
     * <p/>
     * Uses AsyncTask to create a task away from the main UI thread, and send message to rest server.
     */
    private class AsyncSendMessage extends AsyncTask<String, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        //possible returned states of network query login
        public final int NOT_CONNECTED = 0;
        public final int SEND_MESSAGE_OK = 1;
        public final int SEND_MESSAGE_ERROR = 2;

        /**
         * onPreExecute
         * <p/>
         * Prepare environment before initiate the background execution.
         * Can be used to change a view element (as a progress bar) indicating that is running a background task.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * doInBackgroud
         * <p/>
         * AsyncExecution. Executes in its own thread in background.
         * <p/>
         * If it is not demo user, query a REST service to check the state of the user. Returns true if it is a valid user.
         *
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(String... params) { //TODO: check if is appropriate receive initiative and deliverable as parameter as message context

            // variable thats maintains return status for original thread
            int sendMessageStatus = SEND_MESSAGE_OK;

            //SignManager - used to get current user sending the message
            SignManager signManager = new SignManager(getApplicationContext());

            //message sent as parameter for the async class
            String msgText = (String) params[0];

            //prepare message vo
            MessageVo messageVo = new MessageVo();
            messageVo.setText(msgText);
            messageVo.setIdFromUser(signManager.getCurrentUser().getUserId());
            messageVo.setUser_idUser(1); //TODO: read from current message
            messageVo.setInitiative_idInitiative(2); //TODO: read from current initiative
            messageVo.setDeliverable_idDeliverable(1); //TODO: read from current deliverable

            //prepare hive service
            Context context = getApplicationContext();
            HiveSendMessage hiveSendMessage = new HiveSendMessage(context);

            //call give service to send message
            hiveSendMessage.sendMessage(messageVo);

            //return result of background thread execution
            return new Integer(sendMessageStatus);
        }

        /**
         * onPostExecute
         * <p/>
         * Executes in the original thread and receives the result of the background execution.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Integer result) {
            Context context = getApplicationContext();

            if(result == SEND_MESSAGE_ERROR) {
                String text = res.getString(R.string.send_message_error);
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else if (result == NOT_CONNECTED) { //TODO: actualize according the hive service return or exceptions thrown
                String text = res.getString(R.string.device_not_connect);
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else { //...otherwise just shows message informing that the user is not valid
                //TODO: review error treatment
                /*
                SignManager signManager = new SignManager(getApplicationContext());  //retrieve current user
                UserVo userVo = signManager.getCurrentUser();
                String text = res.getString(R.string.send_message_ok);
                text = text + " -> " + userVo.getName();
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                */
            }
        }
    }
}
