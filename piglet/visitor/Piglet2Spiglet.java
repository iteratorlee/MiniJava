package piglet.visitor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import piglet.syntaxtree.BinOp;
import piglet.syntaxtree.CJumpStmt;
import piglet.syntaxtree.Call;
import piglet.syntaxtree.ErrorStmt;
import piglet.syntaxtree.Exp;
import piglet.syntaxtree.Goal;
import piglet.syntaxtree.HAllocate;
import piglet.syntaxtree.HLoadStmt;
import piglet.syntaxtree.HStoreStmt;
import piglet.syntaxtree.IntegerLiteral;
import piglet.syntaxtree.JumpStmt;
import piglet.syntaxtree.Label;
import piglet.syntaxtree.MoveStmt;
import piglet.syntaxtree.NoOpStmt;
import piglet.syntaxtree.Node;
import piglet.syntaxtree.NodeList;
import piglet.syntaxtree.NodeListOptional;
import piglet.syntaxtree.NodeOptional;
import piglet.syntaxtree.NodeSequence;
import piglet.syntaxtree.NodeToken;
import piglet.syntaxtree.Operator;
import piglet.syntaxtree.PrintStmt;
import piglet.syntaxtree.Procedure;
import piglet.syntaxtree.Stmt;
import piglet.syntaxtree.StmtExp;
import piglet.syntaxtree.StmtList;
import piglet.syntaxtree.Temp;

public class Piglet2Spiglet extends GJDepthFirst<String, String>{
	//
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public boolean moving = false, int_allowed = false;
	   public int tab_cnt = 0;
	   public String filename;
	   public Hashtable<String, Integer> currTemp = new Hashtable<String, Integer>();
	   
	   public void setFilename(String filepath){
		   filename = filepath.substring(0, filepath.indexOf('.'));
	   }
	   
	   public void gen(String mString){
		   BufferedWriter out = null;   
		    try {   
		         out = new BufferedWriter(new OutputStreamWriter(   
		                  new FileOutputStream(filename + ".spg", true)));
		         for(int i = 0; i < tab_cnt; ++i)
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
	   
	   /*public String visit(NodeList n, String argu) {
	      String _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }*/

	   public String visit(NodeListOptional n, String argu) {
		   String _ret="";
		   if ( n.present() ) {
		      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
		          _ret += e.nextElement().accept(this,argu);
		   }
		   return _ret;
	   }

	   public String visit(NodeOptional n, String argu) {
		   if ( n.present() )
		         gen(n.node.accept(this,argu));
		      return null;
	   }

	   /*public String visit(NodeSequence n, String argu) {
	      String _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public String visit(NodeToken n, String argu) { return null; }*/

	   //
	   // User-generated visitoString methods below
	   //

	   /**
	    * f0 -> "MAIN"
	    * f1 -> StmtList()
	    * f2 -> "END"
	    * f3 -> ( Procedure() )*
	    * f4 -> <EOF>
	    */
	   public String visit(Goal n, String argu) {
	      String _ret=null;
	      gen("MAIN");
	      ++tab_cnt;
	      n.f1.accept(this, "MAIN");
	      --tab_cnt;
	      gen("END");
	      n.f3.accept(this, argu);
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
	    * f4 -> StmtExp()
	    */
	   public String visit(Procedure n, String argu) {
	      String func_name = n.f0.f0.toString();
	      String int_val = n.f2.f0.toString();
	      gen(func_name + " [" + int_val + "]");
	      gen("BEGIN");
	      ++tab_cnt;
	      String _ret = n.f4.accept(this, func_name);
	      gen("RETURN");
	      gen(_ret);
	      --tab_cnt;
	      gen("END");
	      return null;
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
	    */
	   public String visit(Stmt n, String argu) {
	      String _ret = n.f0.accept(this, argu);
	      gen(_ret);
	      return null;
	   }

	   /**
	    * f0 -> "NOOP"
	    */
	   public String visit(NoOpStmt n, String argu) {
	      String _ret = "NOOP";
	      return _ret;
	   }

	   /**
	    * f0 -> "ERROR"
	    */
	   public String visit(ErrorStmt n, String argu) {
	      String _ret = "ERROR";
	      return _ret;
	   }

	   /**
	    * f0 -> "CJUMP"
	    * f1 -> Exp()
	    * f2 -> Label()
	    */
	   public String visit(CJumpStmt n, String argu) {
	      String _ret = "CJUMP " + n.f1.accept(this, argu) + n.f2.f0.toString();
	      return _ret;
	   }

	   /**
	    * f0 -> "JUMP"
	    * f1 -> Label()
	    */
	   public String visit(JumpStmt n, String argu) {
	      String _ret = "JUMP " + n.f1.f0.toString();
	      return _ret;
	   }

	   /**
	    * f0 -> "HSTORE"
	    * f1 -> Exp()
	    * f2 -> IntegerLiteral()
	    * f3 -> Exp()
	    */
	   public String visit(HStoreStmt n, String argu) {
	      String _ret = "HSTORE " + n.f1.accept(this, argu) + n.f2.accept(this, argu) + n.f3.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "HLOAD"
	    * f1 -> Temp()
	    * f2 -> Exp()
	    * f3 -> IntegerLiteral()
	    */
	   public String visit(HLoadStmt n, String argu) {
	      String _ret = "HLOAD " + n.f1.accept(this, argu) + n.f2.accept(this, argu) + n.f3.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "MOVE"
	    * f1 -> Temp()
	    * f2 -> Exp()
	    */
	   public String visit(MoveStmt n, String argu) {
		  moving = true;
	      String _ret = "MOVE " + n.f1.accept(this, argu) + n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "PRINT"
	    * f1 -> Exp()
	    */
	   public String visit(PrintStmt n, String argu) {
		  int_allowed = true;
	      String _ret = n.f1.accept(this, argu);
	      return "PRINT " + _ret;
	   }

	   /**
	    * f0 -> StmtExp()
	    *       | Call()
	    *       | HAllocate()
	    *       | BinOp()
	    *       | Temp()
	    *       | IntegerLiteral()
	    *       | Label()
	    */
	   public String visit(Exp n, String argu) {
		   if(moving) {
			   moving = false;
			   return n.f0.accept(this, argu);
		   }
		   if(n.f0.which == 5 && int_allowed) {
		       int_allowed = false;
			   return n.f0.accept(this, argu);
		   }
		   int_allowed = false;
		   if(n.f0.which == 4) return n.f0.accept(this, argu);
			  
		   int curNum = currTemp.get(argu);
		   currTemp.put(argu, curNum + 1);
		   gen("MOVE TEMP " + curNum + " " + n.f0.accept(this, argu));
		   return "TEMP " + curNum + " ";
	   }

	   /**
	    * f0 -> "BEGIN"
	    * f1 -> StmtList()
	    * f2 -> "RETURN"
	    * f3 -> Exp()
	    * f4 -> "END"
	    */
	   public String visit(StmtExp n, String argu) {
	      n.f1.accept(this, argu);
	      int_allowed = true;
	      return n.f3.accept(this, argu);
	      //return _ret;
	   }

	   /**
	    * f0 -> "CALL"
	    * f1 -> Exp()
	    * f2 -> "("
	    * f3 -> ( Exp() )*
	    * f4 -> ")"
	    */
	   public String visit(Call n, String argu) {
		  int_allowed = true;
	      String _ret = "CALL " + n.f1.accept(this, argu);
	      int_allowed = true;
	      return _ret + "( " + n.f3.accept(this, argu) + " )";
	   }

	   /**
	    * f0 -> "HALLOCATE"
	    * f1 -> Exp()
	    */
	   public String visit(HAllocate n, String argu) {
		  int_allowed = true;
	      String _ret = "HALLOCATE " + n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Operator()
	    * f1 -> Exp()
	    * f2 -> Exp()
	    */
	   public String visit(BinOp n, String argu) {
	      /*String curr_op = n.f0.accept(this, argu);
	      int curr_temp_num = currTemp.get(argu);
	      currTemp.put(argu, curr_temp_num + 1);
	      gen("MOVE TEMP " + curr_temp_num + " " + n.f1.accept(this, argu));
	      String _ret = curr_op + "TEMP " + curr_temp_num + " ";
	      int_allowed = true;
	      curr_temp_num = currTemp.get(argu);
	      currTemp.put(argu, curr_temp_num + 1);
	      gen("MOVE TEMP " + curr_temp_num + " " + n.f2.accept(this, argu));
	      _ret += ("TEMP " + curr_temp_num);
	      return _ret;*/
		  String curOp = n.f0.accept(this, argu);
		  String _ret = curOp + n.f1.accept(this, argu);
		  if(curOp.equals("LT ") || curOp.equals("PLUS ")) int_allowed = true;
		  return _ret + n.f2.accept(this, argu);
	   }

	   /**
	    * f0 -> "LT"
	    *       | "PLUS"
	    *       | "MINUS"
	    *       | "TIMES"
	    */
	   public String visit(Operator n, String argu) {
	      String[] _ret = {"LT ", "PLUS ", "MINUS ", "TIMES "};
	      int which = n.f0.which;
	      return _ret[which];
	   }

	   /**
	    * f0 -> "TEMP"
	    * f1 -> IntegerLiteral()
	    */
	   public String visit(Temp n, String argu) {
	      String _ret = "TEMP " + n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public String visit(IntegerLiteral n, String argu) {
	      String _ret = n.f0.toString() + " ";
	      return _ret;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public String visit(Label n, String argu) {
	      String _ret = n.f0.toString() + " ";
	      return _ret;
	   }
}
