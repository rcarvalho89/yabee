package br.com.ca.blueocean.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.ca.blueocean.hiveservices.HiveDeleteInitiativeById;
import br.com.ca.blueocean.hiveservices.HiveInviteUser;
import br.com.ca.blueocean.hiveservices.HiveUnexpectedReturnException;
import br.com.ca.blueocean.network.DeviceNotConnectedException;
import br.com.ca.blueocean.share.DeliverablesShareTextFormater;
import br.com.ca.blueocean.share.EmailChannel;
import br.com.ca.blueocean.users.UserManager;
import br.com.ca.blueocean.vo.UserVo;
import br.com.ca.shareview.R;

/**
 * ShowDeliverablesActivity
 *
 * This activity uses TabLayout and fragments to show list of deliverables that are opened and list of closed activities.
 * Receives as initialization parameters the id and title of an initiative.
 *
 * @author: Rodrigo Carvalho
 */
public class ShowDeliverablesActivity extends AppCompatActivity implements ShowOpenedDeliverablesFragment.OnFragmentInteractionListener {

    //identify expected parameters from previous activity
    public final static String EXTRA_INITIATIVE_ID = "INITIATIVE_ID"; //expected value to the activity initialization
    public final static String EXTRA_INITIATIVE_TITLE = "INITIATIVE_TITLE"; //expected value to the activity initialization

    //used for identification of StartActivityForResults request
    public final int CREATE_DELIVERABLE_INTENT_CALL = 0;
    public final int SHOW_DELIVERABLE_DETAILS_FOR_ACTION_INTENT_CALL = 1;

    //to read initialization parameters passed by previous actvity
    String initiativeTitle = null;
    String initiativeId = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_deliverables);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        //Get parameters from previous activity
        //
        Intent myIntent = getIntent(); // gets the previously created intent
        Bundle extras = myIntent.getExtras();
        initiativeId = extras.getString(EXTRA_INITIATIVE_ID);
        initiativeTitle = extras.getString(EXTRA_INITIATIVE_TITLE);

        //action bar title
        //setTitle(getString(R.string.deliverables));
        setTitle(initiativeTitle);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Associates the ViewPagerAdpater with the Tabs in the TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.f_fab_new_deliverable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = getApplicationContext();

                Intent intent;
                Bundle extras;

                //get userId
                //BEWARE: Use getActivity().getApplicationContext() to get application context from a fragment
                UserManager um = new UserManager(getApplicationContext());
                UserVo userVo = um.getCurrentUser();

                //create initiative activity
                //BEWARE: Use getActivity() as a parameter to create an Intent from a Fragment
                intent = new Intent(ShowDeliverablesActivity.this, CreateDeliverableActivity.class);

                extras = new Bundle();
                extras.putString(CreateDeliverableActivity.EXTRA_INITIATIVE_ID, initiativeId);
                extras.putString(CreateDeliverableActivity.EXTRA_INITIATIVE_ID, initiativeId);
                extras.putString(CreateDeliverableActivity.EXTRA_USER_ID, String.valueOf(userVo.getUserId()));
                intent.putExtras(extras);

                //Start Intent for result
                startActivityForResult(intent, CREATE_DELIVERABLE_INTENT_CALL);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_deliverables, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle extras;

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
            /**
             case R.id.action_invite:

             //TODO: INVITE: use intent or dialog to get email
             //TODO: ...and call async task to HiveInviteUser passing context, email and initiativeId

             return true;
             */

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
                DeliverablesShareTextFormater deliverableTextReporter = new DeliverablesShareTextFormater(getApplicationContext());
                emailText = deliverableTextReporter.getDeliverablesTextReport(this.initiativeId, this.initiativeTitle);

                EmailChannel emailChannel= new EmailChannel();
                emailChannel.callEmailApp(this, to, cc, subject, emailText);

                return true;

            case R.id.initiative_report:

                intent = new Intent(ShowDeliverablesActivity.this, DeliverablesReportActivity.class);

                extras = new Bundle();
                extras.putString(DeliverablesActivity.EXTRA_INITIATIVE_ID, this.initiativeId);
                extras.putString(DeliverablesActivity.EXTRA_INITIATIVE_TITLE, this.initiativeTitle);
                intent.putExtras(extras);

                startActivity(intent);

                return true;

            case R.id.action_delete_initiative:
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_delete_initiative)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //call delete initiative handler
                                onConfirmDeleteInitiativeDialog();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing
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
     * OnConfirmDeleteInitiativeDialog
     *
     * BEWARE: This method should be called from Delete Initiative Dialog
     *
     */
    public void onConfirmDeleteInitiativeDialog(){
        //delete the current initiative
        new ShowDeliverablesActivity.AsyncDeleteInitiative().execute(initiativeId);
    }

    /**
     * onFragmentSelectedDeliverable
     *
     * This method is the implementation of the Interface OnFragmentInteractionListener
     * that is defined by the Fragment Class ShowOpenedDeliverablesFragment
     *
     * This callback method must be called by the fragment to comunicate with the activity
     *
     * @param param
     *
     * @author Rodrigo Carvalho
     */
    public void onFragmentInteraction(String param) {

        Context context = getApplicationContext();
        CharSequence text = "ShowOpenedDeliverableFragment selected delivery is " + param;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        /*
        //Intent
        Intent intent = new Intent(ShowDeliverablesActivity.this, DeliverableDetailsActivity.class);

        //Prepare DelvierbaleDetatilsActivity Intent with deliverableId as parameter
        Bundle extras = new Bundle();
        extras.putString(DeliverableDetailsActivity.EXTRA_DELIVERABLE_ID, deliverableId);
        intent.putExtras(extras);

        //Start Intent
        startActivityForResult(intent, SHOW_DELIVERABLE_DETAILS_FOR_ACTION_INTENT_CALL);
        */

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_deliverables, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.content_activities_empty));
            return rootView;
        }
    }

    /**
     * SectionsPagerAdapter
     *
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return ShowOpenedDeliverablesFragment.newInstance(initiativeId, initiativeTitle);
                    //return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return ShowFinishedDeliverablesFragment.newInstance(initiativeId, initiativeTitle);
                    //return PlaceholderFragment.newInstance(position + 1);
                default:
                    return null; //PlaceholderFragment.newInstance(1);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Resources res = getResources();
            switch (position) {
                case 0:
                    return res.getString(R.string.tab_show_opened_deliverables);
                case 1:
                    return res.getString(R.string.tab_show_finished_deliverables);
            }
            return null;
        }
    }


    ////////////////////////////////////////////////////////////////
    // ASync Private Classes
    ////////////////////////////////////////////////////////////////

    /********************************************************************/
    /** ASYNC DELETE INITIATIVE                                        **/
    /********************************************************************/

    /**
     * AsyncDeleteInitiative
     *
     * <p/>
     * Uses AsyncTask to delete current Initiative in a task away from the main UI thread,
     * and call methods that send message to rest server.
     *
     */
    private class AsyncDeleteInitiative extends AsyncTask<String, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();

        public static final int SUCCESS = 0;
        public static final int ERROR = 1;
        public static final int DEVICE_NOT_CONNECTED = 2;

        final ProgressDialog progressDialog = new ProgressDialog(ShowDeliverablesActivity.this,
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
            String initiativeId = params[0];

            //prepare hive service
            Context context = getApplicationContext();
            HiveDeleteInitiativeById hiveDeleteInitiativeById = new HiveDeleteInitiativeById(context);

            try {

                //call hive service
                hiveDeleteInitiativeById.deleteInitiativeById(initiativeId);

                //actualize local database

                //InitiativeDAO initiativeDAO = new InitiativeDAO(context);
                //initiativeDAO.deleteInitiativeById(initiativeId); //TODO: catch exception for inert exception - repeated value

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

                //Intent intent = new Intent();
                //setResult(Activity.RESULT_OK, intent);
                //Intent
                Intent intent = new Intent(ShowDeliverablesActivity.this, SynchronizeInitiativesActivity.class);
                //clear activity stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //Start Intent
                startActivity(intent);

                //finish();

            } else if (result == DEVICE_NOT_CONNECTED) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.initiativesGraphLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.device_not_connect), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else if (result == ERROR) {

                Resources res = getResources();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.initiativesGraphLinearLayout);
                Snackbar.make(linearLayout, res.getString(R.string.unexpected_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }



    /********************************************************************/
    /** ASYNC INVITE USER                                              **/
    /********************************************************************/


    /**
     * AsyncInviteUser
     *
     *
     */
    private class AsyncInviteUser extends AsyncTask<String, Void, Integer> {
        Resources res = getResources();
        Context context = getApplicationContext();

        final ProgressDialog progressDialog = new ProgressDialog(ShowDeliverablesActivity.this,
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
            RelativeLayout coordinatorLayout = (RelativeLayout) findViewById(R.id.deliverablesLayout);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, text, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    //
    //TODO: Needs evaluation. Below is the example code for context menu creation and events handlers.
    //

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
     intent = new Intent(DeliverablesActivity.this, DeliverableDetailsActivity.class);
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

}
