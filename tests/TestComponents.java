import org.junit.Test;
import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.file.Page;

public class TestComponents {
    @Test
    public void testBufferMgrPin(){
        BufferMgr bufferMgr = new BufferMgr(5);
        Page page = new Page();
        int availableBlocks = bufferMgr.available();
        Block block = new Block("test.txt", 1);
        System.out.println("Available Blocks: "+availableBlocks+"/5");
        Buffer buffer = bufferMgr.pin(block);
        availableBlocks = bufferMgr.available();
        System.out.println("Available Blocks: "+availableBlocks+"/5");
        bufferMgr.unpin(buffer);
        availableBlocks = bufferMgr.available();
        System.out.println("Available Blocks: "+availableBlocks+"/5");
    }
}
