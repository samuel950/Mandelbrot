import java.awt.image.BufferedImage;
import javax.swing.*;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.event.*;

public class Main  extends Application {

    PixelWriter pw;
    float rlow = 0;
    float glow = 0;
    float blow = 0;
    float rmed = 1;
    float gmed = 0;
    float bmed = 0;
    float rhigh = 1;
    float ghigh = 200/255f;
    float bhigh = 0;
    int maxiter = 500;
    double height = 600;
    double width = 600;
    int delta = 10; //for fps

    double zoom = 1;
    double shifty = 0;//positive goes down
    double shiftx = 0;//positive goes left
    double shiftmod = .1;
    boolean released = true;
    KeyCode ckey = null;

    public static void main(String[] args) {

        launch(args);

    }

    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene s = new Scene(root, width, height, Color.BLACK);
        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {

                    case W:
                        if(released){
                            ckey = KeyCode.W;
                            released = false;
                            shifty -= shiftmod;
                            draw();
                        }
                        break;
                    case S:
                        if(released){
                            ckey = KeyCode.S;
                            released = false;
                            shifty += shiftmod;
                            draw();
                        }
                        break;
                    case A:
                        if(released){
                            ckey = KeyCode.A;
                            released = false;
                            shiftx += shiftmod;
                            draw();
                        }
                        break;
                    case D:
                        if(released){
                            ckey = KeyCode.D;
                            released = false;
                            shiftx -= shiftmod;
                            draw();
                        }
                        break;
                    case Q:
                        if(released){
                            ckey = KeyCode.Q;
                            released = false;
                            zoom /= 2;
                            shiftmod /= .5;
                            draw();
                        }
                        break;
                    case E:
                        if(released){
                            ckey = KeyCode.E;
                            released = false;
                            zoom *= 2;
                            shiftmod *= .5;
                            draw();
                        }
                        break;
                }
            }
        });
        s.setOnKeyReleased(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent event){
               switch (event.getCode()) {

                   case W:
                       if(!released && ckey == KeyCode.W){
                           released = true;
                       }
                       break;
                   case S:
                       if(!released && ckey == KeyCode.S){
                           released = true;
                       }
                       break;
                   case A:
                       if(!released && ckey == KeyCode.A){
                           released = true;
                       }
                       break;
                   case D:
                       if(!released && ckey == KeyCode.D){
                           released = true;
                       }
                       break;
                   case Q:
                       if(!released && ckey == KeyCode.Q){
                           released = true;
                       }
                       break;
                   case E:
                       if(!released && ckey == KeyCode.E){
                           released = true;
                       }
                       break;
               }
           }
        });
        final Canvas canvas = new Canvas(width, height);
        pw = canvas.getGraphicsContext2D().getPixelWriter();
        root.getChildren().add(canvas);
        primaryStage.setScene(s);
        primaryStage.show();
        draw();
        //display

    }

    private void draw() {
        //double zoom = 1;
        //double shift = 0;
        double minreal = -2.0;
        double maxreal = 1.0;
        maxreal /= zoom;
        minreal /= zoom;
        minreal -= shiftx;
        maxreal -= shiftx;
        double miniq = -1.2;
        double maxiq = (miniq+(maxreal-minreal)*height/width);
        //miniq /= zoom;
        //maxiq /= zoom;
        miniq -= shifty;
        maxiq -= shifty;
        double realVelocity = (maxreal-minreal)/(width-1);
        double iqVelocity = (maxiq-miniq)/(height-1);
        for (int y = 0; y < height; y++){
            double ciq = maxiq - y*iqVelocity;
            for(int x = 0; x < width; x++) {
                double creal =  minreal + (x*realVelocity);
                double zreal = creal;
                double ziq =  ciq;
                boolean inside = true;
                for(int n = 0; n < maxiter;  n++)  {
                    double zreal2 = zreal*zreal;
                    double ziq2 =  ziq*ziq;
                    if(zreal2 + ziq2 > 4){
                        inside = false;
                        putpixel(x,y,n,false);
                        break;
                    }
                    ziq = (2*zreal*ziq) + ciq;
                    zreal = zreal2 - ziq2 + creal;
                }//end for n

                if(inside){
                    putpixel(x, y, 0, inside);
                }
                //draw
            }//end for x
        }//end for y
    }

    private void putpixel(int x, int y, int n, boolean inside){
        if(inside){
            pw.setColor(x, y, javafx.scene.paint.Color.BLACK);
        } else {
            float prop;
            float rval;
            float gval;
            float bval;
            float t = (float)maxiter/2;
            if(n < maxiter/2){
                prop = n/(t-1);
                rval = rlow  + prop * (rmed - rlow);
                gval = glow + prop * (gmed - glow);
                bval = blow +  prop * (bmed - blow);
                Color col = Color.color(rval,gval,bval);
                pw.setColor(x,y,col);
            } else {
                prop = ((float)n-t) / ((float)(maxiter-1) - t);
                rval = rmed  + prop * (rhigh - rmed);
                gval = gmed + prop * (ghigh - gmed);
                bval = bmed +  prop * (bhigh - bmed);
                Color col = Color.color(rval,gval,bval);
                pw.setColor(x,y,col);
            }
        }

    }

}