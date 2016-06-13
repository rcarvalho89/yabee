package br.com.ca.blueocean.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.ca.blueocean.database.DeliverableDAO;
import br.com.ca.blueocean.hiveservices.HiveDeleteDeliverableById;
import br.com.ca.blueocean.hiveservices.HiveResetDeliverablePriority;
import br.com.ca.blueocean.hiveservices.HiveSetDeliverablePriority;
import br.com.ca.blueocean.hiveservices.HiveUnexpectedReturnException;
import br.com.ca.blueocean.network.DeviceNotConnectedException;
import br.com.ca.blueocean.users.UserManager;
import br.com.ca.blueocean.vo.DeliverableVo;
import br.com.ca.blueocean.vo.UserVo;
import br.com.ca.shareview.R;

/**
 * DeliverableDetailsActivity
 *
 * TODO:
 *
 * Show and permits edition and update of deliverable status and rating.
 *
 * @author Rodrigo Carvalho
 */
public class DeliverableDetailsActivity extends AppCompatActivity {

    public final static String EXTRA_DELIVERABLE_ID = "DELIVERABLE_ID"; //expected value to the activity initialization
    private String deliverableId = null;
    private DeliverableVo thisDeliverableVo = null;
    private String prioritizeComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverable_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        //Get parameters from previous activity
        //
        Intent myIntent = getIntent(); // gets the previously created intent
        Bundle extras = myIntent.getExtras();
        deliverableId = extras.getString(EXTRA_DELIVERABLE_ID);

        //use DAO to get deliverable by id
        DeliverableDAO deliverableDAO = new DeliverableDAO(getApplicationContext());
        thisDeliverableVo = deliverableDAO.getDeliverableById(deliverableId);

        //set details view with deliverableVo values
        updateActivityDetailsView(thisDeliverableVo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_change_priority);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrioritizeDialog();
            }
        });

    }

    /**
     * showPrioritizeDialog
     *
     */
    public void showPrioritizeDialog(){

         //show dialog
         LayoutInflater li = LayoutInflater.from(getApplicationContext()); // get dialog layout view
         View dialogView = li.inflate(R.layout.change_priority_dialog, null);

         AlertDialog.Builder builder = new AlertDialog.Builder(this);

         // set view in alertDialogBuilder
         builder.setView(dialogView);

         final EditText userInput = (EditText) dialogView
         .findViewById(R.id.commentsEditText);

         // set dialog message
         builder.setMessage(R.string.dialog_prioritize_deliverable);

         builder.setPositiveButton(R.string.dialog_confirm,
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         //get current user
                         UserVo userVo = null;
                         UserManager userManager = new UserManager(getApplicationContext());
                         userVo = userManager.getCurrentUser();

                         // get user input
                         prioritizeComment = userInput.getText().toString();

                         //update thisDeliverableVo
                         thisDeliverableVo.setIsPriority("YES");
                         thisDeliverableVo.setPriorityComment(prioritizeComment);
                         thisDeliverableVo.setPrioritizedBy(String.valueOf(userVo.getUserId()));

                         //call set priority cloud service
                         new AsyncUpdateDeliverablePriority().execute(thisDeliverableVo);

                     }
                 });

         builder.setNegativeButton(R.string.dialog_cancel,
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         //dialog.cancel();
                     }
                 });

        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Set activity menu
        getMenuInflater().inflate(R.menu.menu_deliverable_details, menu); // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     * onOptionsItemSelected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.action_delete_deliverable:
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_delete_deliverable)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //call delete deliverable handler
                                onConfirmDeleteDeliverableDialog();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * OnConfirmDeleteDeliverableDialog
     *
     * BEWARE: This method should be called from Delete Dialog
     *
     */
    public void onConfirmDeleteDeliverableDialog(){
        //delete the deliverable
        new AsyncDeleteDeliverable().execute(deliverableId);
    }

    /**
     * updateActivityDetailsView
     *
     */
    private void updateActivityDetailsView(DeliverableVo deliverableVo){

        //get text views
        TextView deliverableTextView = (TextView) findViewById(R.id.titleTextView);
        TextView deliverableDescriptionView = (TextView) findViewById(R.id.descriptionTextView);
        TextView deliverableDueDateView = (TextView) findViewById(R.id.dueDateTextView);
        TextView deliverableCurrentUserView = (TextView) findViewById(R.id.currentUserTextView);
        TextView deliverablePrioritizedByView = (TextView) findViewById(R.id.prioritizedByTextView);
        TextView deliverablePriorityCommentsView = (TextView) findViewById(R.id.priorityCommentsTextView);

        //set text views values
        deliverableTextView.setText(deliverableVo.getTitle());
        deliverableDescriptionView.setText(deliverableVo.getDescription());
        deliverableDueDateView.setText(deliverableVo.getDuedate());
        deliverableCurrentUserView.setText(deliverableVo.getCurrentusername());
        deliverablePrioritizedByView.setText(deliverableVo.getPrioritizedBy());
        deliverablePriorityCommentsView.setText(deliverableVo.getPriorityComment());

        //set fields visibility
        if ((deliverableVo.getCurrentusername() == null) || (deliverableVo.getCurrentusername().equals(""))){
            TextView currentUserLabelTextView = (TextView) findViewById(R.id.currentUserLabelTextView);
            currentUserLabelTextView.setVisibility(View.INVISIBLE);
        }

        if ((deliverableVo.getDescription() == null) || (deliverableVo.getDescription().equals(""))){
            TextView descriptionLabelTextView = (TextView) findViewById(R.id.descriptionLabelTextView);
            descriptionLabelTextView.setVisibility(View.INVISIBLE);
        }

        if((deliverableVo.getIsPriority() == null) || (deliverableVo.getIsPriority().equals("NO"))) {
            TextView prioritizedByTextView = (TextView) findViewById(R.id.prioritizedByTextView);
            prioritizedByTextView.setVisibility(View.INVISIBLE); //TODO: check hive service returning "0" instead of null
            TextView priorityCommentsLabelTextView = (TextView) findViewById(R.id.priorityCommentsLabelTextView);
            priorityCommentsLabelTextView.setVisibility(View.INVISIBLE);
        }
    }


    /********************************************************************/
    /** ASYNC DELETE DELIVERABLE                                       **/
    /********************************************************************/

    /**
     * AsyncDeleteDeliverable
     *
     * <p/>
     * Uses AsyncTask to create a task away from the main UI thread, and call methods that send message to rest server.
     *
     */
    private class AsyncDeleteDeliverable extends AsyncTask<String, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();

        public static final int SUCCESS = 0;
        public static final int ERROR = 1;
        public static final int DEVICE_NOT_CONNECTED = 2;

        final ProgressDialog progressDialog = new ProgressDialog(DeliverableDetailsActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            //show progress dialog
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(res.getString(R.string.synchronizing));
            progressDialog.show();
        }

        /**
         * doInBackgroud
         *
         * <p/>
         * AsyncExecution. Executes in its own thread in background.
         *
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(String... params) {

            int result = SUCCESS;

            //prepare hive service parameters
            String deliverableId = params[0];

            //prepare hive service
            Context context = getApplicationContext();
            HiveDeleteDeliverableById hiveDeleteDeliverableById = new HiveDeleteDeliverableById(context);

            try {

                //call hive service
                hiveDeleteDeliverableById.deleteDeliverableById(deliverableId);

                //actualize local database
                DeliverableDAO deliverableDAO = new DeliverableDAO(context);
                deliverableDAO.deleteDeliverableById(deliverableId); //TODO: catch exception for inert exception - repeated value

                result = SUCCESS;

            } catch (DeviceNotConnectedException e){
                result = DEVICE_NOT_CONNECTED;

            } catch(HiveUnexpectedReturnException e){
                result = ERROR;

            } catch(Exception e){
                result = ERROR;
                //TODO: Unexpected error. Should log to enable analysis of the error
            }

            //return result of background thread execution
            return result;
        }

        /**
         * onPostExecute
         *
         * <p/>
         * Executes in the original thread and receives the result of the background execution.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Integer result) {
            Context context = getApplicationContext();
            progressDialog.dismiss();

            if(result == SUCCESS) {

                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);

                finish();

            } else if (result == DEVICE_NOT_CONNECTED) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.deliverableDetailsLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.device_not_connect), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else if (result == ERROR) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.deliverableDetailsLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.unexpected_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }


    /********************************************************************/
    /** ASYNC UPDATE DELIVERABLE PRIORITY                              **/
    /********************************************************************/

    /**
     * AsyncUpdateDeliverablePriority
     *
     * <p/>
     * Uses AsyncTask to create a task away from the main UI thread, and call methods that send message to rest server.
     *
     */
    private class AsyncUpdateDeliverablePriority extends AsyncTask<DeliverableVo, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();

        public static final int SUCCESS = 0;
        public static final int ERROR = 1;
        public static final int DEVICE_NOT_CONNECTED = 2;

        final ProgressDialog progressDialog = new ProgressDialog(DeliverableDetailsActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            //show progress dialog
            //progressDialog.setIndeterminate(true);
            //progressDialog.setMessage(res.getString(R.string.synchronizing));
            //progressDialog.show();
        }

        /**
         * doInBackgroud
         *
         * <p/>
         * AsyncExecution. Executes in its own thread in background.
         *
         * @param params
         * @return
         */
        @Override
        protected Integer doInBackground(DeliverableVo... params) {

            int result = SUCCESS;

            //prepare hive service parameters
            DeliverableVo deliverableVo = params[0];

            //prepare hive service
            Context context = getApplicationContext();

            try {

                if (deliverableVo.getIsPriority().equals("YES")){
                    //call hive service
                    HiveSetDeliverablePriority hiveSetDeliverablePriority = new HiveSetDeliverablePriority(context);
                    hiveSetDeliverablePriority.setDeliverablePriority(deliverableVo);

                    //actualize local database
                    //DeliverableDAO deliverableDAO = new DeliverableDAO(context);
                    //deliverableDAO.deleteDeliverableById(deliverableId); //TODO: catch exception for inert exception - repeated value

                } else {

                    //call hive service
                    HiveResetDeliverablePriority hiveResetDeliverablePriority = new HiveResetDeliverablePriority(context);
                    hiveResetDeliverablePriority.resetDeliverablePriority(deliverableVo);

                    //actualize local database
                    //DeliverableDAO deliverableDAO = new DeliverableDAO(context);
                    //deliverableDAO.deleteDeliverableById(deliverableId); //TODO: catch exception for inert exception - repeated value
                }

                result = SUCCESS;

            } catch (DeviceNotConnectedException e){
                result = DEVICE_NOT_CONNECTED;

            } catch(HiveUnexpectedReturnException e){
                result = ERROR;

            } catch(Exception e){
                result = ERROR;
                //TODO: Unexpected error. Should log to enable analysis of the error
            }

            //return result of background thread execution
            return result;
        }

        /**
         * onPostExecute
         *
         * <p/>
         * Executes in the original thread and receives the result of the background execution.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Integer result) {
            Context context = getApplicationContext();
            //progressDialog.dismiss();

            if(result == SUCCESS) {
                //update view
                updateActivityDetailsView(thisDeliverableVo);

            } else if (result == DEVICE_NOT_CONNECTED) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.deliverableDetailsLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.device_not_connect), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else if (result == ERROR) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.deliverableDetailsLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.unexpected_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }
}
