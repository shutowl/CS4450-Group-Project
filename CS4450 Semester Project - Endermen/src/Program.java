/*********************************************************
 *  file: Program.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Checkpoint 1
 *  date modified: 10/6/21
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


public class Program{
    private FPCameraController fp = new FPCameraController(0f, 0f, 0f);
    private DisplayMode displayMode;
    
    //method:  start
    //purpose: initializes the program
    public void start(){
        try{
            createWindow();
            initGL();
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
        Display.setTitle("Semester Project Checkpoint 1 by The Endermen");
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
        glEnable(GL_DEPTH_TEST);
    }
    
    /*
    Ununsed as the camera controller renders the program instead
    
    //method:  render
    //purpose: the program loop that handles rendering, inputs, etc.
    private void render(){
        while(!Display.isCloseRequested()){
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                //Keyboard inputs
                while(Keyboard.next()){
                    //Exits the program upon pressing Escape
                    if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
                        if(Keyboard.getEventKeyState()) {
                            System.out.println("Program exited!");
                            System.exit(0);
                        }
                }
                
                createCube(100, 320, 200, 100, true);
                
                Display.update();
                Display.sync(60);   //updates at 60fps
                
            } catch (Exception e){}
        }
        Display.destroy();
    }
    */
    
        
    //method:  main
    //purpose: The main method
    public static void main(String[] args){
                
        //Create program
        Program program = new Program();
        program.start();

    }
}