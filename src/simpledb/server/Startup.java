package simpledb.server;

import simpledb.remote.*;
import java.rmi.registry.*;


public class Startup {
    //CS4432-Project1: Global flag to set which replacement policy is used. 0 is LRU, 1 is clock, 2 is longest since unpinning
    static int policyFlag = 2;

   public static void main(String args[]) throws Exception {
      // configure and initialize the database
      SimpleDB.init(args[0]);

      if(args[1].equalsIgnoreCase("LRU"))
          policyFlag = 0;
      else if(args[1].equalsIgnoreCase("Clock"))
          policyFlag = 1;
      else
          policyFlag = 2;
      
      // create a registry specific for the server on the default port
      Registry reg = LocateRegistry.createRegistry(1099);
      
      // and post the server entry in it
      RemoteDriver d = new RemoteDriverImpl();
      reg.rebind("simpledb", d);
      
      System.out.println("database server ready");
   }

   public static int getPolicy() {
       return policyFlag;
   }
}
