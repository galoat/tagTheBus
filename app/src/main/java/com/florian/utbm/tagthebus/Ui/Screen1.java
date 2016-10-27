/**
 * Package who contain all  the classes relevant fot the
 * Graphic User Interface (GUI)
 */
package com.florian.utbm.tagthebus.Ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.florian.utbm.tagthebus.AsyncRest.GetElements;
import com.florian.utbm.tagthebus.AsyncRest.GetElementsImplementation;
import com.florian.utbm.tagthebus.Entity.BusStation;
import com.florian.utbm.tagthebus.R;
import com.florian.utbm.tagthebus.Service.NetworkStateReceiver;
import com.florian.utbm.tagthebus.Service.NetworkStateReceiverListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.LinkedList;


/**
 * <b>Screen1 is the class responsible fot the GUI of the main class .</b>
 * <p>
 * This class call a RestApi in order to get the ligne Bus in Barcelona
 *  This class is characterise by the elements :
 * <ul>
 * <li>a list of Bus Station who contain all the BusStation</li>
 * </ul>
 * </p>
 *
 * Implements GetElementsImplementation in order to use the asyncTask GetElements.
 * @see GetElementsImplementation
 * @see GetElements
 * @author Lacour Florian
 * @version 1.0
 */

public class Screen1 extends AppCompatActivity implements GetElementsImplementation,NetworkStateReceiverListener {


    /**
     * A List who contain all the BusStation of Barcelona
     * Initialised in the methods onCreate by a AsynChrone task
     * Use a Rest API to get all the ligne
     * The URl of the Rest is :http://barcelonaapi.marcpous.com/bus/nearstation/latlon/41.3985182/2.1917991/1.json
     */
    private LinkedList<BusStation> listStation;
    private NetworkStateReceiver networkStateReceiver;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // print the waiting screen
        setContentView(R.layout.activity_screen1_waiting);

        if(isOnline()) {
            // Call a async Task to get the JSONObject inside the API
            networkAvailable();
        }else{
            Toast.makeText(Screen1.this,R.string.noConnection,Toast.LENGTH_SHORT).show();
            // Set the screen to no connection
            TextView textView=(TextView)findViewById(R.id.NoInternet);
             ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            textView.setText(getResources().getString(R.string.noConnection));
            TextView loading =(TextView)findViewById(R.id.tvLoading);
             loading.setText("Not loading");
            // set the listnner got waiting on new state
            networkStateReceiver = new NetworkStateReceiver();
            networkStateReceiver.addListener(this);
            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        }
        }

    /**
     * Call when the network was down and became up
     * Call the Rest task
     *And set all the display to inform the user that the application is charging
     */

    public void networkAvailable() {
        GetElements task = new GetElements(Screen1.this);
        task.execute("http://barcelonaapi.marcpous.com/bus/nearstation/latlon/41.3985182/2.1917991/1.json");
        ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView textView=(TextView)findViewById(R.id.NoInternet);
        textView.setText("");
        TextView loading =(TextView)findViewById(R.id.tvLoading);
        loading.setText("Loading");
    }

    /**
     * Call when the network is unavailable
     * We don't use it cause we need one connection at the begining and
     * not a full time connection
     */
    public void networkUnavailable(){

    }



    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Function call when the thread GetElement as finish.
     * This methods call a ayncTask who create and fill listStation (@see listStation)
     * @param jObj
     *      jObj is the Json contain resulting for the call of http://barcelonaapi.marcpous.com/bus/nearstation/latlon/41.3985182/2.1917991/1.json
     */
    public void threadFinsh(JSONObject jObj) {
        // we create a new Thread which is responsible of the listStation filling.
        ThreadJsonObject tJobj = new ThreadJsonObject(Screen1.this);
        tJobj.execute(jObj);

    }

    /**
     * Methods responsible to print the information when the listStation as been initialised.
     * Set a clickListnner for the second activity (@see Screen2)
     */
    private void afficher(){
        // we change the layout of the activity
        setContentView(R.layout.activity_conteneur_scroll);
        LinearLayout item = (LinearLayout) this.findViewById(R.id.scrollLayout);
        /*
         Print all the BusStation information
         For each BusStation we print a new Layout  @see activity_screen1_intent
        */
        for(final BusStation station: listStation){
            View aAjouter =getLayoutInflater().inflate(R.layout.activity_screen1_intent, null);
            TextView nomView= (TextView)aAjouter.findViewById(R.id.nomDeLaZone);
            nomView.setText(station.getStreet_name());
            item.addView(aAjouter);
            LinearLayout layoutNom = (LinearLayout) aAjouter.findViewById(R.id.conteneurBus);
            // if the user clik on this layout we launch a new activity with in intent the
            //information for the BusStation in which the user have clic
            layoutNom.setOnClickListener(new OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent(Screen1.this, Screen2.class);
                                                 intent.putExtra("BusStation",station);
                                                 startActivity(intent);
                                             }
                                         }
            );

        }
    }

    /**
     * Thread Responsible to parse the JsonObject into a LinkedList of BusStation
     * Parameter JSonOBject the jsonObject who have to be parsed
     *
     * return nothing but this is a inner class and it can modify the attribut listStation.
     * JSonOBject to be parsed example:
                         *
                         * {
                         "code": 200,
                         "data": {
                         "nearstations": [
                         {
                         "id": "1",
                         "street_name": "Almog\u00e0vers-\u00c0vila",
                         "city": "BARCELONA",
                         "utm_x": "432542,5460",
                         "utm_y": "4583524,2340",
                         "lat": "41.3985182",
                         "lon": "2.1917991",
                         "furniture": "Pal",
                         "buses": "06 - 40 - 42 - 141 - B25 - N11",
                         "distance": "0"
                         },
     *
     *
     */
    class ThreadJsonObject extends AsyncTask<JSONObject, Void, Void> {
        /*!
         reference à l'activité
             weakReference => temps que task et pas morte il y a un lien sur mActivity
            quand task est morte y a plus de lien de ce fait on peut passer l'inner classe au garbage
         */
        private WeakReference<Screen1> mActivity = null;

        /**
         * Constructor for the inner class
         * @param classScreen1
         *      the screen1, use to call the methods afficher (@see afficher() )
         *      when the parsing is finish
         */
        public ThreadJsonObject(Screen1 classScreen1){

            mActivity = new WeakReference<Screen1>(classScreen1);
        }

        /**
         * Function who is reponsable for parsing the JsonObject into LinkedList<BusStation>
         * @param params
         *          the JSONObject who as to be parsed into LinkedListStation
         * @return Void
         * (Obliged to use the Object Void and not the primitiv one)
         */
        @Override
        protected Void doInBackground(JSONObject... params) {
            JSONObject jObj = params[0];
            listStation = new LinkedList<BusStation>();
            jObj = jObj.optJSONObject("data");
            JSONArray jsonArray = jObj.optJSONArray("nearstations");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BusStation b = null;

                    b = new BusStation(jsonObject.getInt("id"),
                            jsonObject.getString("street_name"),
                            jsonObject.getString("city"),
                            BigDecimal.valueOf(jsonObject.getDouble("lat")).floatValue(),
                            BigDecimal.valueOf(jsonObject.getDouble("lon")).floatValue(),
                            jsonObject.getString("furniture"),
                            jsonObject.getString("buses"),
                            BigDecimal.valueOf(jsonObject.getDouble("distance")).floatValue());
                    if (b != null)
                        listStation.add(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        /**
         * Call when the methods doInBackground as finsh
         * call the method afficher() from the class Screen1 in order
         * to signalized that the async task have finish
         * @param aVoid
         *    * (Obliged to use the Object Void and not the primitiv one)
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            mActivity.get().afficher();
        }

    }
}



