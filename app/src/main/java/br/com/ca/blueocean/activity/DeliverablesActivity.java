package br.com.ca.blueocean.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.ca.blueocean.adapter.DeliverablesAdapter;
import br.com.ca.blueocean.database.DeliverableDAO;
import br.com.ca.blueocean.email.DeliverableTextReporter;
import br.com.ca.blueocean.email.EmailChannel;
import br.com.ca.blueocean.hiveservices.HiveInviteUser;
import br.com.ca.blueocean.vo.DeliverableVo;
import br.com.ca.shareview.R;

/**
 * Deliverables Activity
 *
 * Show list of deliverables associated with an identified Initiative.
 *
 * @author Rodrigo Carvalho
 */
public class DeliverablesActivity extends AppCompatActivity {

    public final static String EXTRA_INITIATIVE_ID = "INITIATIVE_ID"; //expected value to the activity initialization
    public final static String EXTRA_INITIATIVE_TITLE = "INITIATIVE_TITLE"; //expected value to the activity initialization

    String initiativeTitle = null;
    String initiativeId = null;

    DeliverablesAdapter adapter = null;

    ArrayList<String> deliverableCodeArrayList = new ArrayList<String>();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverables);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //action bar title
        setTitle(getString(R.string.deliverables));

        //
        //Get parameters from previous activity
        //
        Intent myIntent = getIntent(); // gets the previously created intent
        Bundle extras = myIntent.getExtras();
        initiativeId = extras.getString(EXTRA_INITIATIVE_ID);
        initiativeTitle = extras.getString(EXTRA_INITIATIVE_TITLE);

        //set initiative name
        //TextView initiativeTextView = (TextView) findViewById(R.id.initiativeTextView);
        //initiativeTextView.setText(initiativeTitle);

        //Initialize List View
        //
        // 1. pass context and data to the custom adapter
        adapter = new DeliverablesAdapter(this, getDeliverableArrayList(initiativeId));
        // 2. Get ListView from activity xml
        ListView listView = (ListView) findViewById(R.id.deliverables_listView);
        // 3. setListAdapter
        listView.setAdapter(adapter);

        //Register context menu associated with the deliverables list view
        //
        //registerForContextMenu(listView); TODO: review application of context menu for deliverable itens


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Intent
                Intent intent = new Intent(DeliverablesActivity.this, DeliverableUpdateActivity.class);

                //Intent Parameter
                TextView deliverableId = (TextView) view.findViewById(R.id.deliverableIdTextView); //view list item is received as a parameter

                Bundle extras = new Bundle();
                extras.putString(DeliverableUpdateActivity.EXTRA_DELIVERABLE_ID, deliverableId.getText().toString());
                intent.putExtras(extras);

                //Start Intent
                startActivity(intent);
            }
        });
    }

    /**
     * onCreateContextMenu
     *
     * @param menu
     * @param v
     * @param menuInfo
     *
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        //menu inflater
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_deliverable, menu);

    }
    */

    /**
     * onContextItemSelected
     *
     * @param item
     * @return
     *
    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        Intent intent = null;

        View view = null;

        view = getViewByPosition(position);

        switch(item.getItemId()){
            case (R.id.action_show_details):
                Log.d("tag", deliverableCodeArrayList.get(position));
                //Intent
                //Intent intent = new Intent(InitiativesActivity.this, SendMessageActivity.class);
                intent = new Intent(DeliverablesActivity.this, DeliverableUpdateActivity.class);
                //Start Intent
                startActivity(intent);

            case (R.id.action_prioritize):
                Log.d("tag", deliverableCodeArrayList.get(position));
        }

        return true; //super.onContextItemSelected(item);
    }
    */

    /**
     * getViewByPosition
     *
     * @return
     *
    private View getViewByPosition(int position){
        View view = null;

        ListView listView = (ListView) findViewById(R.id.deliverables_listView);
        view = listView.getAdapter().getView(position, null, listView);

        return view;
    }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Set activity menu
        //
        getMenuInflater().inflate(R.menu.menu_deliverables, menu); // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        //
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.action_invite:

                //TODO: INVITE: use intent or dialog to get email
                //TODO: ...and call async task to HiveInviteUser passing context, email and initiativeId

                return true;

            case R.id.action_share:
                String to = null;
                String cc = null;
                String subject = null;
                String emailText = null;

                //prepare share parameters
                //
                to = getString(R.string.emailTo);
                cc = "";
                subject = getString(R.string.emailSubject);
                DeliverableTextReporter deliverableTextReporter = new DeliverableTextReporter(getApplicationContext());
                emailText = deliverableTextReporter.getDeliverablesTextReport(this.initiativeId, this.initiativeTitle);

                EmailChannel emailChannel= new EmailChannel();
                emailChannel.callEmailApp(this, to, cc, subject, emailText);

                return true;

            case R.id.initiative_report:

                Intent intent = new Intent(DeliverablesActivity.this, DeliverablesReportActivity.class);

                Bundle extras = new Bundle();
                extras.putString(DeliverablesActivity.EXTRA_INITIATIVE_ID, this.initiativeId);
                extras.putString(DeliverablesActivity.EXTRA_INITIATIVE_TITLE, this.initiativeTitle);
                intent.putExtras(extras);

                startActivity(intent);

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * getDeliverableArrayList
     *
     * Generate DeliverableVo ArrayList for use with ListView Adapter
     *
     * @param initiativeId
     * @return
     */
    private ArrayList<DeliverableVo> getDeliverableArrayList(String initiativeId){

        ArrayList<DeliverableVo> deliverableVoArrayList = new ArrayList<>();
        List<DeliverableVo> deliverableVoList;

        Context context = getApplicationContext();

        DeliverableDAO deliverableDAO = new DeliverableDAO(context);
        deliverableVoList = deliverableDAO.selectDeliverablesByInitiativeId(initiativeId);

        //access initiative list via Iterator
        Iterator iterator = deliverableVoList.iterator();
        while(iterator.hasNext()){
            DeliverableVo deliverableVo = (DeliverableVo) iterator.next();
            //add into ArrayList
            deliverableVoArrayList.add(deliverableVo);

            //
            //ADD INTO DATA STRUCTURE (CODE ARRAY LIST) FOR FUTURE CONTEXT MENU EVENT REFERENCE
            //
            deliverableCodeArrayList.add(deliverableVo.getCode());
        }

        return deliverableVoArrayList;
    }



    /**
     * DoAsyncSignIn
     *
     * Uses AsyncTask to create a task away from the main UI thread, and check login.
     * If login is valid, call the enter activity
     */
    private class DoAsyncSignIn extends AsyncTask<String, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        final ProgressDialog progressDialog = new ProgressDialog(DeliverablesActivity.this,
                R.style.AppTheme_Dark_Dialog);


        /**
         * onPreExecute
         *
         * Prepare environment before initiate the background execution.
         * Can be used to change a view element (as a progress bar) indicating that is running a background task.
         *
         */
        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(res.getString(R.string.synchronizing));
            progressDialog.show();
        }

        /**
         * doInBackgroud
         * <p/>
         * AsyncExecution. Executes in its own thread in background.
         * <p/>
         *
         * If it is not Guest user, query a REST service to check the state of the user. Returns true if it is a valid user.
         *
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(String... params) {

            //initialize user validation state
            Integer returnState = HiveInviteUser.SUCCESS;

            //hive service parameters
            String email = params[0];
            String initiativeId = params[1];


            //prepare hive service
            Context context = getApplicationContext();
            HiveInviteUser hiveInviteUser = new HiveInviteUser(context);

            //call hive service
            returnState = hiveInviteUser.hiveInviteUser(email, initiativeId); //TODO: Create and check exceptions for every hive service

            //TODO: check hive status return
            //check hive status return
            //SUCCESS
            //POSTPONED
            //INITIATIVE NOT KNOWN
            //and add exception treatment for network or server errors

            //return status of user signin
            return returnState;

        }

        /**
         * onPostExecute
         * <p/>
         * Executes in the original thread and receives the result of the background execution.
         * <p/>
         * Displays the results of the AsyncTask.
         *
         * @param result
         */
        //
        @Override
        protected void onPostExecute(Integer result) {
            Context context = getApplicationContext();

            progressDialog.dismiss();

            //CHECK FOR RETURN INVITE STATE AND INFORM THE USER WITH SNACK BAR MESSAGE

                String text = res.getString(R.string.synchronizing); //TODO: reference the right string
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.deliverableLayout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, text, Snackbar.LENGTH_LONG);
                snackbar.show();

        }
    }
}