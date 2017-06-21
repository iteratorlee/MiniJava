//
// Generated by JTB 1.3.2
//

package kanga.visitor;
import kanga.syntaxtree.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;


/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  YouString visitors may extend this class.
 */
public class Kanga2Mips extends GJDepthFirst<String,String> {
   
   public int paraNum, stackEleNum, maxParaNum;
   public boolean printTab = false;
   public boolean println = true;
   public String filename;
	
   //Set file path
   public void setFilename(String filepath){
	   filename = filepath.substring(0, filepath.indexOf('.'));
   }
   
   
   public void print_mips(String code){	   
	   BufferedWriter out = null;   
	    try {   
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(filename + ".s", true)));
	         if(printTab && println)
	        	 out.write("\t\t\t");
	         out.write(code);
	         if(println)
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
   
   public void print_beg(String methodName){
	   printTab = true;
	   print_mips(".text");
	   print_mips(".globl " + methodName);
	   printTab = false;
	   print_mips(methodName + ":");
	   printTab = true;
   }
   
   public void print_end(){
	   printTab = true;
	   print_mips("");
   }
   
   public boolean is_num(String str) {
	   for(int i = 0; i < str.length(); ++i){
		   if(!Character.isDigit(str.charAt(i)))
			   return false;
	   }
	   return true;
   }
   
   public String visit(NodeList n, String argu) {
      String _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeListOptional n, String argu) {
      if ( n.present() ) {
         String _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public String visit(NodeOptional n, String argu) {
	   println = false;
	   if ( n.present() )
           print_mips(n.node.accept(this,argu) + ":");
	   println = true;
       return null;
   }

   public String visit(NodeSequence n, String argu) {
      String _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeToken n, String argu) { return null; }

   //
   // User-generated visitoString methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( Procedure() )*
    * f13 -> <EOF>
    */
   public String visit(Goal n, String argu) {
      String _ret=null;
      print_beg("main");
      paraNum = Integer.parseInt(n.f2.f0.toString());
      stackEleNum = Integer.parseInt(n.f5.f0.toString());
      maxParaNum = Integer.parseInt(n.f8.f0.toString());
      
      paraNum = Integer.max(0, paraNum - 4);
      maxParaNum = Integer.max(0, maxParaNum - 4);
      stackEleNum -= (paraNum - maxParaNum - 2);
      stackEleNum *= 4;
      
      print_mips("sw $fp, -8($sp)");
      print_mips("sw $ra, -4($sp)");
      print_mips("move $fp, $sp");
      print_mips("subu $sp, $sp, " + stackEleNum);
      n.f10.accept(this, argu);
      print_mips("lw $ra, -4($fp)");
      print_mips("lw $fp, -8($fp)");
      print_mips("addu $sp, $sp, " + stackEleNum);
      print_mips("j $ra");
      print_end();
      
      n.f12.accept(this, argu);
      
      print_beg("_halloc");
      print_mips("li $v0, 9");
      print_mips("syscall");
      print_mips("j $ra");
      print_end();
      
      print_beg("_print");
      print_mips("li $v0, 1");
      print_mips("syscall");
      print_mips("la $a0, label_n");
      print_mips("li $v0, 4");
      print_mips("syscall");
      print_mips("j $ra");
      print_end();
      
      printTab = false;
      print_mips("\t\t\t.data");
      print_mips("\t\t\t.align\t0");
      print_mips("label_n:\t.asciiz\t\"\\n\"");
      print_mips("\t\t\t.data");
      print_mips("\t\t\t.align\t0");
      printTab = true;
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public String visit(StmtList n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    */
   public String visit(Procedure n, String argu) {
      String _ret=null;
      String fucn_name = n.f0.f0.toString();
      print_beg(fucn_name);
      paraNum = Integer.parseInt(n.f2.f0.toString());
      stackEleNum = Integer.parseInt(n.f5.f0.toString());
      maxParaNum = Integer.parseInt(n.f8.f0.toString());
      
      paraNum = Integer.max(0, paraNum - 4);
      maxParaNum = Integer.max(0, maxParaNum - 4);
      stackEleNum -= (paraNum - maxParaNum - 2);
      stackEleNum *= 4;
      
      print_mips("sw $fp, -8($sp)");
      print_mips("move $fp, $sp");
      print_mips("subu $sp, $sp, " + stackEleNum);
      print_mips("sw $ra, -4($fp)");
      
      n.f10.accept(this, argu);
      
      print_mips("lw $ra, -4($fp)");
      print_mips("lw $fp, " + (stackEleNum - 8) + "($sp)");
      print_mips("addu $sp, $sp, " + stackEleNum);
      print_mips("j $ra");
      print_end();
      
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    *       | ALoadStmt()
    *       | AStoreStmt()
    *       | PassArgStmt()
    *       | CallStmt()
    */
   public String visit(Stmt n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public String visit(NoOpStmt n, String argu) {
      String _ret=null;
      print_mips("nop");
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public String visit(ErrorStmt n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Reg()
    * f2 -> Label()
    */
   public String visit(CJumpStmt n, String argu) {
      String _ret=null;
      print_mips("beqz $" + n.f1.accept(this, argu) + ", " + n.f2.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public String visit(JumpStmt n, String argu) {
      String _ret=null;
      print_mips("b " + n.f1.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Reg()
    * f2 -> IntegerLiteral()
    * f3 -> Reg()
    */
   public String visit(HStoreStmt n, String argu) {
      String _ret=null;
      print_mips("sw $" + n.f3.accept(this, argu) + ", " + n.f2.f0.toString() + "($" + n.f1.accept(this, argu) + ")");
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Reg()
    * f2 -> Reg()
    * f3 -> IntegerLiteral()
    */
   public String visit(HLoadStmt n, String argu) {
      String _ret=null;
      print_mips("lw $" + n.f1.accept(this, argu) + ", " + n.f3.f0.toString() + "($" + n.f2.accept(this, argu) + ")");
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Reg()
    * f2 -> Exp()
    */
   public String visit(MoveStmt n, String argu) {
      String _ret=null;
      //TODO
      n.f2.accept(this, n.f1.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public String visit(PrintStmt n, String argu) {
      String _ret=null;
      String to_print = n.f1.accept(this, argu);
      if(is_num(to_print)) print_mips("li $a0, " + to_print);
      else print_mips("move $a0, $" + to_print);
      print_mips("jal _print");
      return _ret;
   }

   /**
    * f0 -> "ALOAD"
    * f1 -> Reg()
    * f2 -> SpilledArg()
    */
   public String visit(ALoadStmt n, String argu) {
      String _ret=null;
      print_mips("lw $" + n.f1.accept(this, argu) + ", " + n.f2.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> "ASTORE"
    * f1 -> SpilledArg()
    * f2 -> Reg()
    */
   public String visit(AStoreStmt n, String argu) {
      String _ret=null;
      print_mips("sw $" + n.f2.accept(this, argu) + ", " + n.f1.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> "PASSARG"
    * f1 -> IntegerLiteral()
    * f2 -> Reg()
    */
   public String visit(PassArgStmt n, String argu) {
      String _ret=null;
      print_mips("sw $" + n.f2.accept(this, argu) + ", " + ((Integer.parseInt(n.f1.accept(this, argu)) - 1) * 4) + "($sp)");
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    */
   public String visit(CallStmt n, String argu) {
      String _ret=null;
      print_mips("jalr $" + n.f1.accept(this, argu));
      return _ret;
   }

   /**
    * f0 -> HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public String visit(Exp n, String argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public String visit(HAllocate n, String argu) {
      String _ret=null;
      String to_allc = n.f1.accept(this, null);
      if(is_num(to_allc)) print_mips("li $a0, " + to_allc);
      else print_mips("move $a0, $" + to_allc);
      print_mips("jal _halloc");
      print_mips("move $" + argu + ", $v0");
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Reg()
    * f2 -> SimpleExp()
    */
   public String visit(BinOp n, String argu) {
      String _ret=null;
      String to_op = n.f2.accept(this, null);
      String op = n.f0.accept(this, null);
      
      if(is_num(to_op)) {
    	  //only slt and add can operate with a number
    	  print_mips(op + "i $" + argu + ", $" + n.f1.accept(this, argu) + ", " + to_op);
    	  /*if(op.equals("slt") || op.equals("add"))
    		  print_mips(op + "i $" + argu + ", $" + n.f1.accept(this, argu) + ", " + to_op);
    	  else{
    		  print_mips("li $t8, " + to_op);
    		  print_mips(op + " $" + argu + ", $" + n.f1.accept(this, argu) + ", $t8");
    	  }*/
      }
      else print_mips(op + " $" + argu + ", $" + n.f1.accept(this, argu) + ", $" + to_op);
      
      return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public String visit(Operator n, String argu) {
      String[] _ret = {"slt", "add", "sub", "mul"};
      return _ret[n.f0.which];
   }

   /**
    * f0 -> "SPILLEDARG"
    * f1 -> IntegerLiteral()
    */
   public String visit(SpilledArg n, String argu) {
      int tmp_pos = Integer.parseInt(n.f1.accept(this, argu));

      if(tmp_pos < paraNum) {  
          return (tmp_pos * 4) + "($fp)";
      }
      else {  
    	  //TODO
          return (-((tmp_pos - paraNum + 3) * 4)) + "($fp)";
      }
   }

   /**
    * f0 -> Reg()
    *       | IntegerLiteral()
    *       | Label()
    */
   public String visit(SimpleExp n, String argu) {
      if(argu != null) {
    	  switch (n.f0.which) {
    	  	case 0:
    	  		print_mips("move $" + argu + ", $" + n.f0.accept(this, argu));
			break;
    	  	case 1:
    	  		print_mips("li $" + argu + ", " + n.f0.accept(this, argu));
			break;
    	  	default:
    	  		print_mips("la $" + argu + ", " + n.f0.accept(this, argu));
			break;
    	  }
      }
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "a0"
    *       | "a1"
    *       | "a2"
    *       | "a3"
    *       | "t0"
    *       | "t1"
    *       | "t2"
    *       | "t3"
    *       | "t4"
    *       | "t5"
    *       | "t6"
    *       | "t7"
    *       | "s0"
    *       | "s1"
    *       | "s2"
    *       | "s3"
    *       | "s4"
    *       | "s5"
    *       | "s6"
    *       | "s7"
    *       | "t8"
    *       | "t9"
    *       | "v0"
    *       | "v1"
    */
   public String visit(Reg n, String argu) {
	   String[] _ret = {"a0", "a1", "a2", "a3", "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
			   "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "v0", "v1"};
       return _ret[n.f0.which];
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, String argu) {
      return n.f0.toString();
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Label n, String argu) {
      return n.f0.toString();
   }

}