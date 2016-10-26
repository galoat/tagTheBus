package com.florian.utbm.tagthebus.AsyncRest;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * GetElements is the class Responsable to go to a RestURl
 *  and return the JSOn
 * param: the String ( enter
 *
 *
 * Created by Florian on 23/10/2016.
 */

public final class GetElements extends AsyncTask<String, Void, JSONObject> {

    // reference à l'activité
    // weakReference => temps que task et pas morte il y a un lien sur mActivity
    //quand task est morste y a plus de lien de ce fait on peut passer l'inner classe au garbage
    private WeakReference<GetElementsImplementation> mActivity = null;
    /**
     * Create the link with a GetElemntsImplementatio
     * @param Activity
     */
    public GetElements(GetElementsImplementation Activity) {
        link(Activity);
    }


    /**
     * Fonction in background responsable to find the RestApi
     * @param str
     *  The url in which find the JSON
     * @return
     *  The JSON in the URL
     */
    @Override
    protected JSONObject doInBackground(String... str) {
        String sortie = null;
        JSONObject jObj = null;
        String urlString = str[0];
        if (urlString == null) {
            return null;
        } else {
            InputStream stream = null;
            URL url = null;
            try {
                url = new URL(urlString);

            URLConnection connection = url.openConnection();
            StringBuffer output = new StringBuffer("");


                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                    String s = "";
                    while ((s = buffer.readLine()) != null)
                        output.append(s);

                    sortie=output.toString();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }

        if (sortie.equals("")) {

        } else {
            try {
                jObj = new JSONObject(sortie);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // clonage pour qu'il y est plus rien pointant sur la classe
        // de ce fait GetElement pour être garbage collécter
        return jObj;
    }

    public void link(GetElementsImplementation pActivity) {
        mActivity = new WeakReference<GetElementsImplementation>(pActivity);
    }

    /**
     * when it's finsh call alll the class who is waiting for result.
     * @param retour
     */
    @Override
    protected void onPostExecute(JSONObject retour) {
        mActivity.get().threadFinsh(retour);

    }

}