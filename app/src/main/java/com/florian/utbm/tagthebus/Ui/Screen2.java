package com.florian.utbm.tagthebus.Ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.florian.utbm.tagthebus.Entity.BusStation;
import com.florian.utbm.tagthebus.Entity.Image;
import com.florian.utbm.tagthebus.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

/**
 * <b>Screen2 is the class responsible fot the GUI of the class wh contain all the photos.</b>
 * <p>
 *  This class is characterise by the elements :
 * <ul>
 * <li>REQUEST_IMAGE_CAPTURE : the ID for identify the photoActivity for the function startActivityForResult </li>
 * <li>REQUEST_SET_TITLE : the ID for identify the the class SetTitle for the function startActivityForResult </li>
 * <li>ligne: the BusStation for this activity  </li>
 * </ul>
 * </p>
 *
 *
 * @author Lacour Florian
 * @version 1.0
 */
public class Screen2 extends AppCompatActivity {

    /**
     * Use for identifying the activity responsable of taking a photo
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * Use for identifying the activity SetTitel
     * @see SetTitle
     */
    static  final int REQUEST_SET_TITLE=2;
    /**
     * The BusStation for this Activity
     * Given by the class Screen1 when it launch Screen2
     */
    private BusStation ligne;


    public void onCreate(Bundle savedInstanceState) {
        ligne=(BusStation) getIntent().getSerializableExtra("BusStation");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteneur_scroll);
        // change the title in the menus to know on what BusLigne we are
        this.setTitle(ligne.getStreet_name());
        // responsible for find the photo and print it
        setView();
    }


    /**
     * call when an activity is lauch with startActivityForResult
     * if the activityWhoCall is the camera then
     *          if the return call is OK
     *               we launch another activity to set the title of the photo
     *               and to save the photo.
     *          if the return call is Canceked
     *               We print a popup to inform the user that he have not take a photo
     * if the activity who call this function is SetTitle
     *          if the code is OK
     *              we add the photo and title on the activity
     *           if the code is notOK
     *               we print the message in a popup saying that the image as not been save
     * @param requestCode
     *          The code given when the activity is launch
     *           request_image_capture
     *          REQUEST_SET_TITLE
     * @param resultCode
     *          the result given by the application
     * @param data
     *      The intent given by the activity on the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LinearLayout item = (LinearLayout) this.findViewById(R.id.scrollLayout);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
             Intent intent = new Intent(this, SetTitle.class);
            intent.putExtra("BusStation",ligne);
            intent.putExtra("photo",imageBitmap);

            startActivityForResult(intent, REQUEST_SET_TITLE);
        } else if (requestCode==REQUEST_IMAGE_CAPTURE&& resultCode==RESULT_CANCELED){
            Toast.makeText(Screen2.this, getString(R.string.photoNotTake), Toast.LENGTH_LONG).show();

        } else if (requestCode==REQUEST_SET_TITLE&&resultCode==RESULT_OK) {
            Bitmap bImage =data.getParcelableExtra("result");
            String titre = (String) data.getSerializableExtra("titre");
            ajouterImage(bImage, item,titre);
        }else if(requestCode==REQUEST_SET_TITLE&&resultCode==RESULT_CANCELED){
            Toast.makeText(Screen2.this, getString(R.string.photoNotSave), Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Function responsible of inisialised the LinkedList<Image> with all the image inside
     * {apllicationContext}/BusLigne.streetName
     * @see Image
     * @param RueName
     *          The name of the street use to go inside the subFolder
     * @return a List who containt alll Images inside the folder {apllicationContext}/BusLigne.streetName
     */
    private LinkedList<Image> loadImageFromStorage(String RueName)
    {
            LinkedList<Image> retour = new LinkedList<>();
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File f=cw.getDir(RueName,Context.MODE_PRIVATE);
            if(f.exists()) {
                if (f.isDirectory()) {
                    for (File sub : f.listFiles()) {
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(sub));
                        String titre= new String(sub.getName());
                        retour.add(new Image(titre,b));
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return retour;
    }

    /**
     * Function use to create the view.
     */
    private void  setView(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        LinkedList<Image> images =loadImageFromStorage(ligne.getStreet_name());
        LinearLayout item = (LinearLayout) this.findViewById(R.id.scrollLayout);
        if(images.size()!=0) {
            for (Image bImage : images) {
                ajouterImage(bImage.getB(), item,bImage.getTitre());
            }
        }
    }


    /**
     * Function use to add the icon take photo to the menus
     * @param menu
     *      menu we want to modify
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_screen2, menu);
        return true;

    }

    /**
     * Function use when the user clic on take a photo
     * launch the methode takePhoto()
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_takePhoto:
                takePhoto();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method use when the user clic on take on phto
     * launch the camera
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Function use when we want to add a photo inside the view.
     *  Handle the case to destroy
     *  When the user long click on  view menus appear and he can select the delete option
     * @param img
     *          the image we want to add inside the layout
     * @param layout
     *          the layout in which will be added the photo
     * @param titre
     *         the title of the photo
     */
    private void ajouterImage(final Bitmap img,final LinearLayout layout,final String titre){
        final View aAjouter = getLayoutInflater().inflate(R.layout.activity_screen2_intent, null);

        final ImageView mImageView = (ImageView) aAjouter.findViewById(R.id.photo);
        TextView tView =(TextView)aAjouter.findViewById(R.id.titrePhoto);
        // suppression de l'extenssion
        tView.setText(titre.substring(0, titre.lastIndexOf('.')));
        mImageView.setImageBitmap(img);

       aAjouter.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(Screen2.this, aAjouter);
                popup.getMenuInflater().inflate(R.menu.menu_screen2_layout, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals(getResources().getString(R.string.delete))){
                            layout.removeView(aAjouter);
                            deleteFile(ligne.getStreet_name(),titre);
                            Toast.makeText(Screen2.this,R.string.deletePhoto,Toast.LENGTH_SHORT).show();

                        }else
                        {
                            Toast.makeText(Screen2.this,R.string.photoNotDelete,Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
                return true;
            }
        });
        layout.addView(aAjouter);
    }

    /**
     * Function who delete a image

     * @param rue
     *  The file were we want to delete
     *  In this case the image is identify by the street name
     * @param title
     *  The titleof the photo we want to delete
     */
    private void deleteFile( String rue, String title){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(rue, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,title);
        mypath.delete();

        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_screen2_layout, menu);
    }
    }


