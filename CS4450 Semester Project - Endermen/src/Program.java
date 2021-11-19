/*********************************************************
 *  file: Program.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Final Checkpoint
 *  date modified: 11/18/21
 * 
 *  purpose: Demonstrate usage of a controllable camera
 *  in a 3D environment
 * 
 *********************************************************/

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import java.io.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;


public class Program{
    private FPCameraController fp;
    private DisplayMode displayMode;
    
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    //method:  start
    //purpose: initializes the program
    public void start(){
        try{
            createWindow();
            initGL();
            fp = new FPCameraController(0, 0, 0);
            fp.gameLoop(); //render();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //method:  createWindow
    //purpose: initializes the program window
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        
        DisplayMode d[] = Display.getAvailableDisplayModes();
            for (int i= 0; i < d.length; i++) {
                if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                    displayMode = d[i];
                    break;
                }
            }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Semester Project Final Checkpoint by The Endermen");
        Display.create();
    }
    
    //method:  initGL
    //purpose: initializes program values upon program start
    private void initGL(){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);   //background color
        
        glMatrixMode(GL_PROJECTION);            //camera
        glLoadIdentity();
        
        //glOrtho(0, 640, 0, 480, 1, -1);       //orthographic matrix
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float) displayMode.getHeight(), 0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        //lighting
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //light position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);    //specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);     //diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);     //ambient light
        
        glEnable(GL_LIGHTING);  //enable lighting
        glEnable(GL_LIGHT0);    //enable light0
    }
            
    //method:  main
    //purpose: The main method
    public static void main(String[] args){
                
        //Create program
        Program program = new Program();
        program.start();

    }
    
    //method:  initLightArrays
    //purpose: initialize lighting values
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
}