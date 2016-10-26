package com.florian.utbm.tagthebus.Ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.florian.utbm.tagthebus.Entity.BusStation;
import com.florian.utbm.tagthebus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <b>SetTile is the class responsible fot the GUI of the class whio can modify the title of a photo</b>
 * <p>

 *
 * @author Lacour Florian
 * @version 1.0
 */
public class SetTitle extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_title);
        // to display the return on the menus
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button bouton = (Button) findViewById(R.id.valider);
        // we get all the intent given by Screen2.onActivityResult
        final Bitmap imageBitmap=  getIntent().getParcelableExtra("photo");
        final BusStation ligne= (BusStation) getIntent().getSerializableExtra("BusStation");
        final TextView titre = (TextView)findViewById(R.id.titre);
        ImageView viewImage = (ImageView)findViewById(R.id.photo);
        // we print the image
        viewImage.setImageBitmap(imageBitmap);
        // when validate is clic we save photo and return to Screen2 activity.
        //if a title as not been enter we say that "a title as not been set"
        bouton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!titre.getText().toString().equals("")) {
                    saveToInternalStorage(imageBitmap,ligne.getStreet_name(), titre.getText().toString());
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",imageBitmap);
                    returnIntent.putExtra("titre",titre.getText()+".png");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(SetTitle.this, getString(R.string.titleNotEnter), Toast.LENGTH_LONG).show();
                 }
            }
        });
    }

    /**
     * if the user clic on the return boutton we get back to the activity Screen2
     * with the result Canceled
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                setResult(Activity.RESULT_CANCELED,null);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Function who save the intent in the internal storage
     * @param bitmapImage
     *  The image we want to save
     * @param rue
     *      the path we ant to save given by streetName
     * @param title
     *      The title of the photo
     * @return
     */
    private void saveToInternalStorage(Bitmap bitmapImage, String rue, String title){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(rue, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,title+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
