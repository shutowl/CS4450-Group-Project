/*********************************************************
 *  file: Chunk.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Checkpoint 2
 *  date modified: 10/24/21
 * 
 *  purpose: A class that stores information of a Chunk
 * 
 *********************************************************/

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    
    
    //method:  render
    //purpose: The render method responsible for rendering the program
    public void render(){
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
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        
        for(float x = 0; x < CHUNK_SIZE; x += 1){
            for(float z = 0; z < CHUNK_SIZE; z += 1){
                for(float y = 0; y < CHUNK_SIZE; y++){
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), 
                                                    (float)(startY + y * CUBE_LENGTH + (int)(CHUNK_SIZE * 0.8)), 
                                                    (float)(startZ + z * CUBE_LENGTH)));
                
                    VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int)x][(int)y][(int)z])));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
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
        return new float[]{1, 1, 1};
    }
    
    //constructor
    public Chunk(int startX, int startY, int startZ){
        r = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for(int x = 0; x < CHUNK_SIZE; x++){
            for(int y = 0; y < CHUNK_SIZE; y++){
                for(int z = 0; z < CHUNK_SIZE; z++){
                    if(r.nextFloat() > 0.7f){
                        blocks[x][y][z] = new Block(Block.BlockType.Grass);
                    }
                    else if(r.nextFloat () > 0.5f){
                        blocks[x][y][z] = new Block(Block.BlockType.Dirt);
                    }
                    else if(r.nextFloat () > 0.3f){
                        blocks[x][y][z] = new Block(Block.BlockType.Water);
                    }
                    else if(r.nextFloat() > 0.2f){
                        blocks[x][y][z] = new Block(Block.BlockType.Sand);
                    }
                    else{
                        blocks[x][y][z] = new Block(Block.BlockType.Bedrock);
                    }
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        
        rebuildMesh(startX, startY, startZ);

    }
}
