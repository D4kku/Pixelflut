package de.uulm.in.vs.grn.p5;

import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.nio.ByteBuffer;

//Seperate PixelfluteData class to better utilise polymorphism
public class PixelflutData {

    //since we dont want to change it ever so we can parse it on to thread without everything exploding
    private final byte[] request;

    //doing it this way makes it thread saver(since we dont allow it to change) and maybe save on performance
    //since its reused instead of getting regenerated every time we call
    public PixelflutData(byte x, byte y, Color color){
        // Diagramm of buffer:
        //  1 Byte
        //  <----->
        // +---------------------------------------+
        // | xpos  | ypos  |  red  | green | blue  |
        // +---------------------------------------+
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        byteBuffer.put(x); // x pos
        byteBuffer.put(y); // y pos
        byteBuffer.put((byte) color.getRed());
        byteBuffer.put((byte) color.getGreen());//Green
        byteBuffer.put((byte) color.getBlue());//Blue
        this.request = byteBuffer.array();
    }
    //overloading in case we already have a byte array in the right format but want to use the other helper functions ie for the receiveid dat
    public PixelflutData(byte[] array){
        this.request = array;
    }
    public byte[] getFormatedData(){
        return request;
    }
    //Debug function to print the Pixelflut data for a given
    public void print(String moreInfo){
        StringBuilder stringBuilder = new StringBuilder(moreInfo + ":" + request[0] + "," + request[1]);
        for(int i = 2; i < 5; i++){
            stringBuilder.append("," + (this.request[i] & 0xFF));//this looks weird but it only makes it so that the byte is printed so as if its range would be 0-255 instead of -128-127
        }
        System.out.println(stringBuilder);
    }
    //TODO: Test if the memory overhead globals would makes it performe worse or this abomination
    //maybe make this global again this feels like it would be really fucking slow
    //actually maybe i should do a custom color type since the java thing is fucking weird
    public int getLength() {
        return this.request.length;
    }
    public int getX(){
        return this.request[0];
    }
    public int getY(){
        return this.request[1];
    }
    public int getRGB(){
        int rgb = new Color(request[2]& 0xFF,request[3]& 0xFF,request[4]& 0xFF).getRGB();
        System.out.println(rgb);
        return rgb; //idk how to get the weird rgb thing that the image buffer wants any other way
    }
    public int getRed(){
        return this.request[2];
    }
    public int getGreen(){
        return this.request[3];
    }
    public int getBlue(){
        return this.request[4];
    }
}
