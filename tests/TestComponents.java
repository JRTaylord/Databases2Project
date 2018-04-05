import org.junit.Before;
import org.junit.Test;
import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.file.Page;
import simpledb.server.Startup;

import static org.junit.Assert.assertEquals;

/**
 * CS4432-Project1:
 * Tests the subcomponents of the dbms
 */
public class TestComponents {

    int buffers = 100;
    BufferMgr bufferMgr = new BufferMgr(buffers);

    Block block0, block1, block2, block3, block4, block5, block6, block7, block8, block9;

    @Before
    public void startup(){
        try{
            //Startup.main(new String[]{"TestDB","LRU"});
            block0 = new Block("12 Rules for Life",0);
            block1 = new Block("The GULAG Archipelago",1);
            block2 = new Block("Demons",2);
            block3 = new Block("Beyond Good and Evil",3);
            block4 = new Block("Crime and Punishment",4);
            block5 = new Block("Old Man and the Sea",5);
            block6 = new Block("A Farewell to Arms",6);
            block7 = new Block("For Whom the Bell Tolls",7);
            block8 = new Block("The Red and Black",8);
            block9 = new Block("The Charterhouse of Parma",9);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * CS4432-Project1:
     * Tests if the bufferMgr can successfully pin
     */
    @Test
    public void testBufferMgrPin(){
        int availableBlocks = bufferMgr.available();
        assertEquals(buffers,availableBlocks);
        Buffer buffer0 = bufferMgr.pin(block0);
        availableBlocks = bufferMgr.available();
        assertEquals(buffers-1,availableBlocks);
        //CS4432-Project1: unpins block0
        bufferMgr.unpin(buffer0);
        availableBlocks = bufferMgr.available();
        assertEquals(buffers-1,availableBlocks);
        //CS4432-Project1: pins a new block
        bufferMgr.pin(block1);
        availableBlocks = bufferMgr.available();
        assertEquals(buffers-2,availableBlocks);
        //CS4432-Project1: repins block0
        bufferMgr.pin(block0);
        assertEquals(buffers-2,availableBlocks);
    }
}
