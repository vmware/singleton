/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.pseudo;


import java.io.InputStream;

import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;

@Component
public class Pseudo {
      
	
      
      private static String pyParm="ops=create_pesudo";
	
   
     public void exePython(String pyPath, String localPath) {
    	 
    	 PySystemState state= null;
    	 try {
    	 state= new PySystemState();
    	 state.argv.clear(); 
    	 state.argv.append(new PyString("pseudodb")); 
    	 state.argv.append(new PyString("bundle_path="+localPath)); 
    	 state.argv.append(new PyString(pyParm)); 
    	 
    	 PythonInterpreter interpreter = new PythonInterpreter(null,state); 
         interpreter.execfile(pyPath);
    	 }finally {
    		 if(state != null) {
    			 state.close();
    			 
    		 }
    		 
    	 }
         
         

     }
     
     
     
    public void exePythonStream(String localPath) {
   	 PySystemState state= null;
	 try {
    	 state = new PySystemState();
    	 state.argv.clear(); 
    	 state.argv.append(new PyString("pseudodb")); 
    	 state.argv.append(new PyString("bundle_path="+localPath)); 
    	 state.argv.append(new PyString(pyParm)); 
    	 
    	 PythonInterpreter interpreter = new PythonInterpreter(null,state); 
    	 InputStream inputStm = this.getClass().getClassLoader().getResourceAsStream("scripts/pseudodb.py");
    	 
         interpreter.execfile(inputStm);
	   }finally {
		 if(state != null) {
			 state.close();
			 
		 }
		 
	 }
    }
    
	 
	
}
