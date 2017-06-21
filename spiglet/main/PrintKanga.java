package spiglet.main;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PrintKanga {

    private static boolean pTab = false;
    private static String filename;
    
    public static void setFilename(String filepath){
    	filename = filepath.substring(0, filepath.indexOf('.'));
    }
    
    public static void gen(Boolean label, String mString){
		   BufferedWriter out = null;   
		    try {   
		         out = new BufferedWriter(new OutputStreamWriter(   
		                  new FileOutputStream(filename + ".kg", true)));
		         if(!label && pTab)
		        	 out.write("    ");
		         out.write(mString);
		         out.newLine();
		    } catch (Exception e) {   
		            e.printStackTrace();   
		    } finally {   
		        try {   
		            out.close();   
		        } catch (IOException e) {   
		            e.printStackTrace();   
		        }   
		    }
	   }

    public static void print(String s){
    	gen(true, s);
    }
    public static void println(String s) {
    	gen(false, s);
    }


    public static void pBegin(String procName, int argNum, int stackUse, int maxCallArg) {
        println(procName + " [" + argNum + "][" + stackUse + "][" + maxCallArg + "]");
        pTab = true;
    }


    public static void pEnd() {
        pTab = false;
        println("END");
    }
}
