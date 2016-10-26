package com.florian.utbm.tagthebus.Entity;

import android.graphics.Bitmap;

/**
 * Image represent an photo
 *
 * This case is characterise by the elements :
 * <ul>
 *     <p>Bitmap b: Contain the image</p>
 *     <p>Titre contain the title of the image</p>
 * </ul>
 */

public class Image {
    /**
     * The image
     */
   private Bitmap b;
    /**
     * The title of the image
     */
    private String titre;

    public Image(String titre, Bitmap b) {
        this.titre = titre;
        this.b = b;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
