/*********************************************************
 *  file: FPCameraController.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Final Checkpoint
 *  date modified: 11/18/21
 * 
 *  purpose: A class that controls the camera (and renders the program
 *  through the Chunk class)
 * 
 *********************************************************/

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class FPCameraController {
    
    private Chunk chunk = new Chunk(20, -50, -70);
    
    //store position of the camera's position
    private Vector3f position = null;
    private Vector3f lastPosition = null;
    
    //y-axis rotation
    private float yaw = 0.0f;
    //x-axis rotation
    private float pitch = 0.0f;
    
    //Daylight cycle variables
    private float sunSpeed = 2.0f;
    private float sunRadius = 600f;
    Vector3Float sunStartPos = new Vector3Float(chunk.getX()-(int)sunRadius, chunk.getY()+60, chunk.getZ()+100);    //sun spawn position
    Vector3Float sunPos = new Vector3Float((int)sunStartPos.x, (int)sunStartPos.y, (int)sunStartPos.z);             //current position
    
    private Vector3Float me;
    
    //constructor
    public FPCameraController(float x, float y, float z){
        //instantiate position
        position = new Vector3f(x, y, z);
        lastPosition = new Vector3f(x, y, z);
        //lastPosition.x = 0f;
        //lastPosition.y = 15f;
        //lastPosition.z = 0f;
    }
    
    //method:  yaw
    //purpose: increment yaw (y-axis) rotation
    public void yaw(float amount){
        yaw += amount;
    }
    
    //method:  pitch
    //purpose: increment pitch (x-axis) rotation
    public void pitch(float amount){
        pitch -= amount;
    }
    
    //method:  walkForward
    //purpose: moves the camera forward with respect to the x-axis
    public void walkForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lastPosition.x -= xOffset).put(lastPosition.y).put(lastPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method:  walkBackwards
    //purpose: moves the camera backwards with respect to the x-axis
    public void walkBackwards(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lastPosition.x += xOffset).put(lastPosition.y).put(lastPosition.z -= zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method:  strafeLeft
    //purpose: strafe the camera left with respect to the x-axis
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lastPosition.x -= xOffset).put(lastPosition.y).put(lastPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method:  strafeRight
    //purpose: strafe the camera right with respect to the x-axis
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lastPosition.x -= xOffset).put(lastPosition.y).put(lastPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method:  moveUp
    //purpose: move the camera up
    public void moveUp(float distance){
        position.y -= distance;
    }
    
    //method:  moveDown
    //purpose: move the camera down
    public void moveDown(float distance){
        position.y += distance;
    }
    
    //method:  lookThrough
    //purpose: translates and rotate the matrix so that it looks through the camera
    public void lookThrough(){
        //rotate the pitch around the x-axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the y-axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector
        glTranslatef(position.x, position.y, position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lastPosition.x).put(lastPosition.y).put(lastPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: gameLoop
    //purpose: the game loop responsible for moving the camera, controls, and rendering the program
    public void gameLoop(){
        FPCameraController camera = new FPCameraController(position.x, position.y, position.z);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;        //time between frames
        float lastTime = 0.0f;  //time of last frame
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        
        //hide the mouse
        Mouse.setGrabbed(true);
        
        //keep program running until window is closed or esc if pressed
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            
            time = Sys.getTime();
            lastTime = time;
            
            //distance in mouse movement
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            //control camera with mouse
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
            
            // -- CONTROLS -- //
            //W = forward
            if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
                camera.walkForward(movementSpeed);
            }
            //S = backwards
            if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                camera.walkBackwards(movementSpeed);
            }
            //A = left
            if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                camera.strafeLeft(movementSpeed);
            }
            //D = left
            if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                camera.strafeRight(movementSpeed);
            }
            //SPACE = up
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(movementSpeed);
            }
            //LSHIFT = down
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(movementSpeed);
            }
            while(Keyboard.next()){
                //R = refresh chunk (randomizes the noise generation seed)
                if(Keyboard.getEventKey() == (Keyboard.KEY_R)){
                    if(!Keyboard.getEventKeyState()){
                        System.out.print("Chunk reloaded. ");
                        chunk = new Chunk(chunk.getX(), chunk.getY(), chunk.getZ());
                    }
                }
                //Q = decrease sun speed
                if(Keyboard.getEventKey() == (Keyboard.KEY_Q)){
                    if(!Keyboard.getEventKeyState()){
                        sunSpeed -= 0.5f;
                        System.out.println("Decreased sun speed. Current speed: " + sunSpeed);
                    }
                }
                //E = increase sun speed
                if(Keyboard.getEventKey() == (Keyboard.KEY_E)){
                    if(!Keyboard.getEventKeyState()){
                        sunSpeed += 0.5f;
                        System.out.println("Increased sun speed. Current speed: " + sunSpeed);
                    }
                }
            }
            // -- end of CONTROLS -- //
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //Daylight cycle
            FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
            lightPosition.put(sunPos.x += sunSpeed).put(sunPos.y).put(sunPos.z).put(1.0f).flip();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);

            if(sunPos.x > sunRadius + chunk.getX()){
                sunPos.x = sunStartPos.x;
            }
            
            //System.out.println("Sun position: (" + sunPos.x + ", " + sunPos.y + ", " + sunPos.z + ")");   //Print the "sun's" location to consol
            createCube(4, (int)sunPos.x, (int)sunPos.y, (int)sunPos.z, false, new Vector3Float(1,1,0));     //Visually tracks where the "sun" is
            
            
            //Use the Chunk class's render method
            chunk.render();
            
            Display.update();
            Display.sync(60);
        }
        System.out.println("Program exited!");
        Display.destroy();
    }
    
    //method:  createCube
    //purpose: Creates a cube in the program
    private void createCube(int size, int posX, int posY, int posZ, boolean lines, Vector3Float color){
        glPushMatrix();
        glTranslatef(posX, posY, posZ);
        
        if(!lines){
            glBegin(GL_QUADS);
                //Quad 1 (Top)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, -size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(-size, size, size);
                    glVertex3f(size, size, size);
                //Quad 2 (Bottom)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, -size, size);
                    glVertex3f(-size, -size, size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(size, -size, -size);
                //Quad 3 (Front)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, size);
                    glVertex3f(-size, size, size);
                    glVertex3f(-size, -size, size);
                    glVertex3f(size, -size, size);
                //Quad 4 (Back)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, -size, -size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(size, size, -size);
                //Quad 5 (Left)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(-size, size, size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(-size, -size, size);
                //Quad 6 (Right)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, -size);
                    glVertex3f(size, size, size);
                    glVertex3f(size, -size, size);
                    glVertex3f(size, -size, -size);    
            glEnd();
        }
        else{   //only show outer lines (the mesh)
            glBegin(GL_LINE_LOOP);
                //Quad 1 (Top)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, -size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(-size, size, size);
                    glVertex3f(size, size, size);
            glEnd();
            glBegin(GL_LINE_LOOP);
                //Quad 2 (Bottom)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, -size, size);
                    glVertex3f(-size, -size, size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(size, -size, -size);
            glEnd();
            glBegin(GL_LINE_LOOP);
                //Quad 3 (Front)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, size);
                    glVertex3f(-size, size, size);
                    glVertex3f(-size, -size, size);
                    glVertex3f(size, -size, size);
            glEnd();
            glBegin(GL_LINE_LOOP);
                //Quad 4 (Back)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, -size, -size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(size, size, -size);
            glEnd();
            glBegin(GL_LINE_LOOP);
                //Quad 5 (Left)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(-size, size, size);
                    glVertex3f(-size, size, -size);
                    glVertex3f(-size, -size, -size);
                    glVertex3f(-size, -size, size);
            glEnd();
            glBegin(GL_LINE_LOOP);
                //Quad 6 (Right)
                glColor3f(color.x, color.y, color.z);
                    glVertex3f(size, size, -size);
                    glVertex3f(size, size, size);
                    glVertex3f(size, -size, size);
                    glVertex3f(size, -size, -size);    
            glEnd();
        }
        glPopMatrix();
        
    }
   
    /*
    private void render(){
        try{
           createCube(3, 0, -5, -10, false);
        }catch(Exception e){}
    }
    */
    
    
    
    
}
