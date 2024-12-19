package de.uulm.in.vs.grn.p5;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

//seperate thread just for updating the image of the pixelflutserver
public class PixelflutImageHandler{
    //ik this is a kinda weird way to handle the updating of the image but this should make it so that always the newst complete image is rendered i think
    public BufferedImage image;

    private ConcurrentLinkedQueue<PixelflutData> drawQueue;

    public PixelflutImageHandler(ConcurrentLinkedQueue<PixelflutData> drawQueue) {
        this.drawQueue = drawQueue;
        this.image =new BufferedImage(128, 128,BufferedImage.TYPE_INT_RGB);
    }

    public void renderNewImage(){
        //Basically a render loop where we render the last image change this could be way faster but since we dont get so many changes it should be fine
        while(!drawQueue.isEmpty()) {
            PixelflutData pixelflutData = drawQueue.remove();
            try {//i have to do this weird thing since for some reason the server somtimes returns weird data idk if its a me or a server problem
                pixelflutData.print("Current drawQueue:");
                image.setRGB(pixelflutData.getX(), pixelflutData.getY(), pixelflutData.getRGB());
            } catch (Exception e){
                System.out.println("Server returned boggus");
                pixelflutData.print("this draw crashed:");
            }
        }
    }
}
