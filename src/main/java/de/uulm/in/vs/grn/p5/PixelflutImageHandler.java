package de.uulm.in.vs.grn.p5;

import java.awt.image.BufferedImage;

//seperate thread just for updating the image of the pixelflutserver
public class PixelflutImageHandler extends Thread{
    //ik this is a kinda weird way to handle the updating of the image but this should make it so that always the newst complete image is rendered i think
    public BufferedImage image;

    public PixelflutImageHandler() {
        this.image =new BufferedImage(128, 128,BufferedImage.TYPE_INT_RGB);
    }

    public void run(){
        while(true) {
            updateImage();
        }
    }

    private void updateImage(){

    }

}
