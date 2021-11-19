/*********************************************************
 *  file: Chunk.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Final Checkpoint
 *  date modified: 11/18/21
 * 
 *  purpose: A class that stores information of a Chunk
 * 
 *********************************************************/

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    
    private int VBOTextureHandle;
    private Texture texture;
    
    //noise generation variables
    private float height;
    private int seed;
    private int heightOffset = 4;       //adjusts the offset at which the chunk starts noise height
    private int maxHeight = 100;        //controls how tall terrain can get. higher value = taller terrain
    private double persistance = 0.1;   //controls noise height variation. lower value = flatter terrain
    SimplexNoise noise;
    
    //method:  render
    //purpose: The render method responsible for rendering the program
    public void render(){
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //method:  rebuildMesh
    //purpose: determines positions of all cubes
    public void rebuildMesh(float startX, float startY, float startZ){
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
                  
        for(float x = 0; x < CHUNK_SIZE; x++){
            for(float z = 0; z < CHUNK_SIZE; z++){
                
                //noise height generation
                height = heightOffset + Math.abs(100 * (float)noise.getNoise((int)x, (int)z));
                //prevent going above chunk height
                if(height > CHUNK_SIZE)
                    height = CHUNK_SIZE;
                
                for(float y = 0; y < height; y++){
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), 
                                                      (float)(startY + y * CUBE_LENGTH + (int)(CHUNK_SIZE * 0.8)), 
                                                      (float)(startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int)x][(int)y][(int)z])));
                    VertexTextureData.put(createTexCube((float) 0, (float)0, blocks[(int)(x)][(int)(y)][(int)(z)]));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
 
    }
    
    //method:  createCubeVertexCol
    //purpose: determines and returns the color of a cube
    private float[] createCubeVertexCol(float[] CubeColorArray){
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        
        for(int i = 0; i < cubeColors.length; i++){
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        
        return cubeColors;
    }
    
    //method:  createCube
    //purpose: creates a cube with respect to the CUBE_LENGTH
    public static float[] createCube(float x, float y, float z){
        int offset = CUBE_LENGTH / 2;
        
        return new float[]{
            //TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            //FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            //BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            //LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z, 
            x - offset, y - offset, z, 
            x - offset, y - offset, z - CUBE_LENGTH,
            //RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }
    
    //method:  getCubeColor
    //purpose: returns a cube's color with respect to its block type
    private float[] getCubeColor(Block block){
        /*  unnecessary as textures are placed instead
        switch(block.getID()){
            //grass
            case 0:
                return new float[]{0, 1, 0};
            //sand
            case 1:
                return new float[]{1, 1, 0.78f};
            //water
            case 2:
                return new float[]{0, 0, 1};
            //dirt
            case 3:
                return new float[]{0.44f, 0.31f, 0.22f};
        }
        */
        return new float[]{1, 1, 1};
    }
    
    //method:  createTexCube
    //purpose: returns a cube with its respective texture
    public static float[] createTexCube(float x, float y, Block block){
        float offset = (1024f/16)/1024f;
        
        switch(block.getID()){
            case 0: //grass
                return new float[] {
                //Top
                x + offset * 3, y + offset * 10,    //bottom right of texture
                x + offset * 2, y + offset * 10,    //bottom left
                x + offset * 2, y + offset * 9,     //top left
                x + offset * 3, y + offset * 9,     //top right
                //Bottom
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                //Front
                x + offset * 3, y + offset * 0,
                x + offset * 4, y + offset * 0,
                x + offset * 4, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Back
                x + offset * 4, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 0,
                x + offset * 4, y + offset * 0,
                //Left
                x + offset * 3, y + offset * 0,
                x + offset * 4, y + offset * 0,
                x + offset * 4, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Right
                x + offset * 3, y + offset * 0,
                x + offset * 4, y + offset * 0,
                x + offset * 4, y + offset * 1,
                x + offset * 3, y + offset * 1,
                };
            case 1: //sand
                return new float[] {
                //Top
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Bottom
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Front
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Back
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Left
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                //Right
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                };
            case 2: //water
                return new float[] {
                //Top
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                //Bottom
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                //Front
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                //Back
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                //Left
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                //Right
                x + offset * 14, y + offset * 0,
                x + offset * 15, y + offset * 0,
                x + offset * 15, y + offset * 1,
                x + offset * 14, y + offset * 1,
                };
            case 3: //dirt
                return new float[] {
                //Top
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                //Bottom
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                //Front
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                //Back
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                //Left
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                //Right
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                };
            case 4: //stone
                return new float[] {
                //Top
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                //Bottom
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                //Front
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                //Back
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                //Left
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                //Right
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                };
            case 5: //bedrock
                return new float[] {
                //Top
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                //Bottom
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                //Front
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                //Back
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                //Left
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                //Right
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                };
            default:
                return new float[]{1, 1, 1};
        }
        
    }
    
    //Get chunk coordinates
    public int getX(){return startX;}
    public int getY(){return startY;}
    public int getZ(){return startZ;}
    
    //constructor
    public Chunk(int startX, int startY, int startZ){
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }catch(Exception e){
            System.out.print("Texture er-roar found");
        }
        
        r = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        seed = Math.abs(r.nextInt());
        System.out.println("Seed: " + seed);
        noise = new SimplexNoise(maxHeight, persistance, seed);
        
        for(int x = 0; x < CHUNK_SIZE; x++){
            for(int z = 0; z < CHUNK_SIZE; z++){
                
                //noise height generation
                height = heightOffset + Math.abs(100 * (float)noise.getNoise((int)x, (int)z));
                
                for(int y = 0; y < height; y++){
                    //Bottom layer
                    if(y == 0){
                        blocks[x][y][z] = new Block(Block.BlockType.Bedrock);
                    }
                    //Middle layer
                    else if(y < height - 1){
                        if(y > height - 3)
                            blocks[x][y][z] = new Block(Block.BlockType.Dirt);
                        else
                            blocks[x][y][z] = new Block(Block.BlockType.Stone);
                    }
                    //Top layer
                    else{
                        if(y < 7)           //create lakes or rivers
                            blocks[x][y][z] = new Block(Block.BlockType.Water);
                        else if(y < 8)      //sand appears next to water
                            blocks[x][y][z] = new Block(Block.BlockType.Sand);
                        else
                            blocks[x][y][z] = new Block(Block.BlockType.Grass);
                    }
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        
        rebuildMesh(startX, startY, startZ);

    }
}
