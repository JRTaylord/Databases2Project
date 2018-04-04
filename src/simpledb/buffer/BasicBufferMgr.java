package simpledb.buffer;

import simpledb.file.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
   private Buffer[] bufferpool;
   private int numAvailable;

   //CS4432-Project1: Added list of empty buffer frames
   private LinkedList<Buffer> unpinnedBuffers = new LinkedList<>();
   //CS4432-Project1: Added hashmap of blocks to buffer frames
   private HashMap<Block, Buffer> bufferMap = new HashMap<>();
   //CS4432-Project1: Clock replacement pointer
   private int clockPtr = 0;

   /*
    CS4432-Project1: Added indices of empty frames to list as they are created
    */
   /**
    * Creates a buffer manager having the specified number
    * of buffer slots.
    * This constructor depends on both the {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} objects
    * that it gets from the class
    * {@link simpledb.server.SimpleDB}.
    * Those objects are created during system initialization.
    * Thus this constructor cannot be called until
    * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
    * is called first.
    * @param numbuffs the number of buffer slots to allocate
    */
   BasicBufferMgr(int numbuffs) {
      bufferpool = new Buffer[numbuffs];
      numAvailable = numbuffs;
      for (int i=0; i<numbuffs; i++) {
         bufferpool[i] = new Buffer();
         unpinnedBuffers.add(bufferpool[i]);
      }
   }

   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.isModifiedBy(txnum))
            buff.flush();
   }

   /*
    CS4432-Project1: Adds buffers to hashmap as they are assigned to blocks
    */
   /**
    * Pins a buffer to the specified block.
    * If there is already a buffer assigned to that block
    * then that buffer is used;
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
      }
      if(bufferMap.putIfAbsent(buff.block(), buff) != null) {
         bufferMap.replace(buff.block(), buff);
      }
      if (!buff.isPinned()) {
         numAvailable--;
      }
      buff.pin();
      return buff;
   }

   /*
    CS4432-Project1: Adds buffers to hashmap as they are assigned to blocks
    */
   /**
    * Allocates a new block in the specified file, and
    * pins a buffer to it.
    * Returns null (without allocating the block) if
    * there are no available buffers.
    * @param filename the name of the file
    * @param fmtr a pageformatter object, used to format the new block
    * @return the pinned buffer
    */
   synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      if(bufferMap.putIfAbsent(buff.block(), buff) != null) {
         bufferMap.replace(buff.block(), buff);
      }
      numAvailable--;
      buff.pin();
      return buff;
   }

   /*
   CS4432-Project1: Added functionality to add unpinned buffers to unpinned list
    */
   /**
    * Unpins the specified buffer.
    * @param buff the buffer to be unpinned
    */
   synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned()) {
         numAvailable++;
         unpinnedBuffers.add(buff);
      }
   }

   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   int available() {
      return numAvailable;
   }

   /*
   CS4432-Project1: Uses hashmap to find buffer containing blk
    */
   private Buffer findExistingBuffer(Block blk) {
//      for (Buffer buff : bufferpool) {
//         Block b = buff.block();
//         if (b != null && b.equals(blk))
//            return buff;
//      }
//      return null;
      Buffer buff = bufferMap.get(blk);
      Block b = buff.block();
      if(b != null && b.equals(blk))
         return buff;
      return null;
   }

   /*
   CS4432-Project1: This function removes and returns the first empty or unpinned buffer from the list
    */
   private Buffer chooseUnpinnedBuffer() {
//      for (Buffer buff : bufferpool)
//         if (!buff.isPinned()) {
//            return buff;
//         }
//      return null;
      try {
         Buffer buff = unpinnedBuffers.pop();
         return buff;
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   /*
   CS4432-Project1: This function selects which frame to replace using a least recently used policy
    */
   synchronized Buffer LRU() {
      long currentTime = System.currentTimeMillis();
      Buffer oldestBuff = null;
      long oldestBuffTime = 0;
      for(Buffer buff: bufferpool) {
         long tempTime;
         if(!buff.isPinned()) {
            if ((tempTime = currentTime - buff.getLastUsed()) > oldestBuffTime) {
               oldestBuff = buff;
               oldestBuffTime = tempTime;
            }
         }
      }
      return oldestBuff;
   }

   /*
   CS4432-Project1: This function selects which frame to replace using a clock replacement policy
    */
   synchronized Buffer Clock() {
      Buffer buff;
      int localPtr = clockPtr;
      for(; localPtr < bufferpool.length; localPtr++) {
         buff = bufferpool[localPtr];
         if(!buff.isPinned()) {
            if(buff.getRefbit() == 1)
               buff.setRefbit();
            else {
               //Free and reuse buffer
               clockPtr = localPtr + 1;
               return buff;
            }
         }
      }
      for(localPtr = 0; localPtr < clockPtr; localPtr++) {
         buff = bufferpool[localPtr];
         if(!buff.isPinned()) {
            if(buff.getRefbit() == 1)
               buff.setRefbit();
            else {
               //Free and reuse buffer
               clockPtr = localPtr + 1;
               return buff;
            }
         }
      }
      return null;
   }

   /**
    * CS4432-Project1:
    * runs through each buffer in its bufferpool and calls to string on each buffer
    * @return
    */
   public String toString(){
      String out = "";
      for (Buffer b :
              bufferpool) {
         out = out + b.toString() + "\n";
      }
      return out;
   }
}
