/*********************************************************
 *  file: Block.java
 *  author: The Endermen
 *  class: CS4450.01-1 - Computer Graphics
 * 
 *  assignment: Semester Project Final Checkpoint
 *  date modified: 11/18/21
 * 
 *  purpose: A class that stores information of a Block
 * 
 *********************************************************/

public class Block {
    private boolean isActive;
    private BlockType Type;
    private float x,y,z;
    
    //store all types of blocks with its respective ID
    public enum BlockType {
        Grass(0),
        Sand(1),
        Water(2),
        Dirt(3),
        Stone(4),
        Bedrock(5),
        Melon(6),
        Pumpkin(7),
        Air(8),
        LongGrass(9);
        
        private int BlockID;
        
        BlockType(int i){
            BlockID = i;
        }
        
        public int getID(){ return BlockID; }
        public void setID(int i){ BlockID = i; }
    }
    
    //constructor
    public Block(BlockType type){
        Type = type;
    }
    
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean isActive(){ return isActive; }
    public void setActive(boolean active){ isActive = active; }
    public int getID(){ return Type.getID(); }
}
