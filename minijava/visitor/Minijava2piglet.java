package minijava.visitor;

import java.util.Enumeration;
import java.util.Vector;

import minijava.symboltable.*;
import minijava.syntaxtree.*;

public class Minijava2piglet extends GJDepthFirst<BaseType, BaseType>{
	//
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public BaseType visit(NodeList n, BaseType argu) {
	      BaseType _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public BaseType visit(NodeListOptional n, BaseType argu) {
	      if ( n.present() ) {
	         BaseType _ret=null;
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

	   public BaseType visit(NodeOptional n, BaseType argu) {
	      if ( n.present() )
	         return n.node.accept(this,argu);
	      else
	         return null;
	   }

	   public BaseType visit(NodeSequence n, BaseType argu) {
	      BaseType _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public BaseType visit(NodeToken n, BaseType argu) { return null; }

	   //
	   // User-generated visitoBaseType methods below
	   //

	   /**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
	   public BaseType visit(Goal n, BaseType argu) {
	      BaseType _ret=null;
	     
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	     
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> "public"
	    * f4 -> "static"
	    * f5 -> "void"
	    * f6 -> "main"
	    * f7 -> "("
	    * f8 -> "String"
	    * f9 -> "["
	    * f10 -> "]"
	    * f11 -> Identifier()
	    * f12 -> ")"
	    * f13 -> "{"
	    * f14 -> ( VarDeclaration() )*
	    * f15 -> ( Statement() )*
	    * f16 -> "}"
	    * f17 -> "}"
	    */
	   public BaseType visit(MainClass n, BaseType argu) {
	      BaseType _ret=null;
	      //TODO:
	      MiniApp app = (MiniApp)argu;
	      app.gen("MAIN");
	      ++app.tabcnt;
	      n.f14.accept(this, argu);
	      n.f15.accept(this, argu);
	      --app.tabcnt;
	      app.gen("END");
	      return _ret;
	   }

	   /**
	    * f0 -> ClassDeclaration()
	    *       | ClassExtendsDeclaration()
	    */
	   public BaseType visit(TypeDeclaration n, BaseType argu) {
	      BaseType _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	   public BaseType visit(ClassDeclaration n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      String name = n.f1.f0.toString();
	      MiniClass _class = app.getClassByName(name);
	      
	      app.hierarchy = 1;
	      app.currClass = _class;
	      //n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      app.currClass = null;
	      app.hierarchy = 0;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "extends"
	    * f3 -> Identifier()
	    * f4 -> "{"
	    * f5 -> ( VarDeclaration() )*
	    * f6 -> ( MethodDeclaration() )*
	    * f7 -> "}"
	    */
	   public BaseType visit(ClassExtendsDeclaration n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      String name = n.f1.f0.toString();
	      MiniClass _cLass = app.getClassByName(name);
	      
	      app.hierarchy = 1;
	      app.currClass = _cLass;
	      //n.f5.accept(this, argu);
	      n.f6.accept(this, argu);
	      app.currClass = null;
	      app.hierarchy = 0;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	   public BaseType visit(VarDeclaration n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      //n.f1.accept(this, argu);
	      //n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "public"
	    * f1 -> Type()
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( FormalParameterList() )?
	    * f5 -> ")"
	    * f6 -> "{"
	    * f7 -> ( VarDeclaration() )*
	    * f8 -> ( Statement() )*
	    * f9 -> "return"
	    * f10 -> Expression()
	    * f11 -> ";"
	    * f12 -> "}"
	    */
	   public BaseType visit(MethodDeclaration n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      MiniClass temp_class = app.currClass;
	      String func_name = n.f2.f0.toString();
	      String class_name = temp_class.name;
	      String pig_func_name = class_name + "_" + func_name;
	      MiniMethod _func = temp_class.getMethodByName(func_name);
	      
	      app.hierarchy = 2;
	      app.max_temp_id = 1;
	      app.currMethod = _func;
	      
	      app.gen(pig_func_name + " [2]");
	      ++app.tabcnt;
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      
	      //n.f4.accept(this, argu);
	      //n.f7.accept(this, argu);
	      n.f8.accept(this, argu);
	      
	      --app.tabcnt;
	      app.gen("RETURN");
	      ++app.tabcnt;
	      n.f10.accept(this, argu);
	      --app.tabcnt;
	      app.gen("END");
	      --app.tabcnt;
	      app.gen("");
	      
	      app.hierarchy = 1;
	      app.currMethod = null;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	   public BaseType visit(FormalParameterList n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      //n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    */
	   public BaseType visit(FormalParameter n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      //n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> FormalParameter()
	    */
	   public BaseType visit(FormalParameterRest n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      //n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ArrayType()
	    *       | BooleanType()
	    *       | IntegerType()
	    *       | Identifier()
	    */
	   public BaseType visit(Type n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "int"
	    * f1 -> "["
	    * f2 -> "]"
	    */
	   public BaseType visit(ArrayType n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      //n.f1.accept(this, argu);
	      //n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "boolean"
	    */
	   public BaseType visit(BooleanType n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "int"
	    */
	   public BaseType visit(IntegerType n, BaseType argu) {
	      BaseType _ret=null;
	      //n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Block()
	    *       | AssignmentStatement()
	    *       | ArrayAssignmentStatement()
	    *       | IfStatement()
	    *       | WhileStatement()
	    *       | PrintStatement()
	    */
	   public BaseType visit(Statement n, BaseType argu) {
	      BaseType _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "{"
	    * f1 -> ( Statement() )*
	    * f2 -> "}"
	    */
	   public BaseType visit(Block n, BaseType argu) {
	      BaseType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "="
	    * f2 -> Expression()
	    * f3 -> ";"
	    */
	   public BaseType visit(AssignmentStatement n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      //TODO: get_address
	      String var_name = n.f0.f0.toString();
	      app.getAddr(var_name);
	      
	      app.gen("HSTORE TEMP " + app.global_flag + " " + app.temp_offset);
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "["
	    * f2 -> Expression()
	    * f3 -> "]"
	    * f4 -> "="
	    * f5 -> Expression()
	    * f6 -> ";"
	    */
	   public BaseType visit(ArrayAssignmentStatement n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      String var_name = n.f0.f0.toString();
	      
	      app.getAddr(var_name);
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      
	      app.gen("HLOAD TEMP " + t0 + " TEMP " + app.global_flag + " " + app.temp_offset);
	      app.gen("MOVE TEMP " + t1 + " PLUS TEMP " + t0);
	      ++app.tabcnt;
	      app.gen("TIMES 4 ");
	      ++app.tabcnt;
	      app.gen("PLUS 1 ");
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      --app.tabcnt;
	      app.gen("HSTORE TEMP " + t1 + " 0 ");
	      ++app.tabcnt;
	      n.f5.accept(this, argu);
	      --app.tabcnt;
	      return _ret;
	   }

	   /**
	    * f0 -> "if"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    * f5 -> "else"
	    * f6 -> Statement()
	    */
	   public BaseType visit(IfStatement n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      int t0 = ++app.max_temp_id;
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      int label_0 = ++app.max_label_id;
	      int label_1 = ++app.max_label_id;
	      app.gen("CJUMP TEMP " + t0 + " L" + label_0);
	      ++app.tabcnt;
	      n.f4.accept(this, argu);
	      --app.tabcnt;
	      app.gen("JUMP L" + label_1);
	      
	      app.gen("L" + label_0 + " NOOP");
	      ++app.tabcnt;
	      n.f6.accept(this, argu);
	      --app.tabcnt;
	      app.gen("L" + label_1 + " NOOP");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "while"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    */
	   public BaseType visit(WhileStatement n, BaseType argu) {
	      BaseType _ret=null;
	      //TODO
	      MiniApp app = (MiniApp)argu;
	      int label_0 = ++app.max_label_id;
	      int label_1 = ++app.max_label_id;
	      
	      app.gen("L" + label_0 + " NOOP");
	      ++app.tabcnt;
	      
	      int t0 = ++app.max_temp_id;
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      --app.tabcnt;
	      app.gen("CJUMP TEMP " + t0 + " L" + label_1);
	      ++app.tabcnt;
	      n.f4.accept(this, argu);
	      --app.tabcnt;
	      app.gen("JUMP L" + label_0);
	      app.gen("L" + label_1 + " NOOP");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "System.out.println"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> ";"
	    */
	   public BaseType visit(PrintStatement n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("PRINT");
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> AndExpression()
	    *       | CompareExpression()
	    *       | PlusExpression()
	    *       | MinusExpression()
	    *       | TimesExpression()
	    *       | ArrayLookup()
	    *       | ArrayLength()
	    *       | MessageSend()
	    *       | PrimaryExpression()
	    */
	   public BaseType visit(Expression n, BaseType argu) {
	      BaseType _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "&&"
	    * f2 -> PrimaryExpression()
	    */
	   public BaseType visit(AndExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      int t2 = ++app.max_temp_id;
	      int label_0 = ++app.max_label_id;
	      
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      app.gen("MOVE TEMP " + t2 + " 0");
	      
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      --app.tabcnt;
	      app.gen("CJUMP TEMP " + t0 + " L" + label_0);
	      
	      app.gen("MOVE TEMP " + t1);
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      app.gen("CJUMP TEMP " + t1 + " L" + label_0);
	      
	      app.gen("MOVE TEMP " + t2 + " 1");
	      
	      --app.tabcnt;
	      app.gen("L" + label_0 + " NOOP");
	      app.gen("RETURN TEMP " + t2);
	      app.gen("END");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	   public BaseType visit(CompareExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("LT ");
	      --app.tabcnt;
	      n.f0.accept(this, argu);
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	   public BaseType visit(PlusExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("PLUS");
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	   public BaseType visit(MinusExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("MINUS ");
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	   public BaseType visit(TimesExpression n, BaseType argu) {
		   BaseType _ret=null;
		   MiniApp app = (MiniApp)argu;
		      
		   app.gen("TIMES ");
		   ++app.tabcnt;
		   n.f0.accept(this, argu);
		   n.f2.accept(this, argu);
		   --app.tabcnt;
		   
		   return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "["
	    * f2 -> PrimaryExpression()
	    * f3 -> "]"
	    */
	   public BaseType visit(ArrayLookup n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      int t2 = ++app.max_temp_id;
	      int t3 = ++app.max_temp_id;
	      
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      --app.tabcnt;
	      
	      app.gen("MOVE TEMP " + t1);
	      ++app.tabcnt;
	      app.gen("TIMES 4 ");
	      app.gen("PLUS 1");
	      ++app.tabcnt;
	      n.f2.accept(this, argu);
	      --app.tabcnt;
	      --app.tabcnt;
	      
	      app.gen("MOVE TEMP " + t2 + " PLUS TEMP " + t1 + " TEMP " + t0);
	      app.gen("HLOAD TEMP " + t3 + " TEMP " + t2 + " 0");
	      --app.tabcnt;
	      app.gen("RETURN TEMP " + t3);
	      app.gen("END");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> "length"
	    */
	   public BaseType visit(ArrayLength n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      
	      app.gen("BEGIN ");
	      ++app.tabcnt;
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      --app.tabcnt;
	      
	      app.gen("HLOAD TEMP " + t1 + " TEMP " + t0 + " 0");
	      --app.tabcnt;
	      app.gen("RETURN TEMP " + t1);
	      app.gen("END");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( ExpressionList() )?
	    * f5 -> ")"
	    */
	   public BaseType visit(MessageSend n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      String func_name = n.f2.f0.toString();
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      int t2 = ++app.max_temp_id;
	      int t3 = ++app.max_temp_id;
	      
	      app.gen("BEGIN");
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      --app.tabcnt;
	      app.gen("HLOAD TEMP " + t1 + " TEMP " + t0 + " 0");
	      app.gen("HLOAD TEMP " + t2 + " TEMP " + t1 + " "
	    		  +app.getFuncOffset(func_name));
	      
	      int para_num = app.getFuncVarNumber(func_name) + 1;
	      app.gen("MOVE TEMP " + t3 + " HALLOCATE " + para_num * 4);
	      
	      int former_temp = app.curr_temp;
	      int former_temp_cnt = app.curr_temp_cnt;
	      app.curr_temp = t3;
	      app.curr_temp_cnt = 0;
	      n.f4.accept(this, argu);
	      
	      --app.tabcnt;
	      app.gen("RETURN CALL TEMP " + t2 + " (TEMP " + t0 + " TEMP " + t3 + ")");
	      app.gen("END");
	      
	      app.curr_temp = former_temp;
	      app.curr_temp_cnt = former_temp_cnt;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Expression()
	    * f1 -> ( ExpressionRest() )*
	    */
	   public BaseType visit(ExpressionList n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("HSTORE TEMP " + app.curr_temp + " " + app.curr_temp_cnt);
	      ++app.tabcnt;
	      n.f0.accept(this, argu);
	      --app.tabcnt;
	      app.curr_temp_cnt += 4;
	      n.f1.accept(this, argu);
	      
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> Expression()
	    */
	   public BaseType visit(ExpressionRest n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("HSTORE TEMP " + app.curr_temp + " " + app.curr_temp_cnt);
	      ++app.tabcnt;
	      n.f1.accept(this, argu);
	      --app.tabcnt;
	      app.curr_temp_cnt += 4;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> IntegerLiteral()
	    *       | TrueLiteral()
	    *       | FalseLiteral()
	    *       | Identifier()
	    *       | ThisExpression()
	    *       | ArrayAllocationExpression()
	    *       | AllocationExpression()
	    *       | NotExpression()
	    *       | BracketExpression()
	    */
	   public BaseType visit(PrimaryExpression n, BaseType argu) {
	      BaseType _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public BaseType visit(IntegerLiteral n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      String int_val = n.f0.toString();
	      app.gen(int_val);
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "true"
	    */
	   public BaseType visit(TrueLiteral n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("1");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "false"
	    */
	   public BaseType visit(FalseLiteral n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("0");
	      return _ret;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public BaseType visit(Identifier n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      int t0 = ++app.max_temp_id;
	      
	      String name = n.f0.toString();
	      //System.out.println(name + " line : " + n.f0.beginLine);
	      app.getAddr(name);
	      
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      app.gen("HLOAD TEMP " + t0 + " TEMP " + app.global_flag + " " + app.temp_offset);
	      --app.tabcnt;
	      app.gen("RETURN TEMP " + t0);
	      app.gen("END");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "this"
	    */
	   public BaseType visit(ThisExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      app.gen("TEMP 0");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> "int"
	    * f2 -> "["
	    * f3 -> Expression()
	    * f4 -> "]"
	    */
	   public BaseType visit(ArrayAllocationExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      
	      app.gen("MOVE TEMP " + t0);
	      ++app.tabcnt;
	      n.f3.accept(this, argu);
	      --app.tabcnt;
	      
	      app.gen("MOVE TEMP " + t1 + " HALLOCATE TIMES 4 PLUS 1 TEMP " + t0);
	      //Store the length of the array and the beginning of the address
	      app.gen("HSTORE TEMP " + t1 + " 0 TEMP " + t0);
	      --app.tabcnt;
	      app.gen("RETURN TEMP " + t1);
	      app.gen("END");
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> Identifier()
	    * f2 -> "("
	    * f3 -> ")"
	    */
	   public BaseType visit(AllocationExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      
	      String class_name = n.f1.f0.toString();
	      MiniClass _class = app.getClassByName(class_name);
	      int t0 = ++app.max_temp_id;
	      int t1 = ++app.max_temp_id;
	      
	      app.gen("BEGIN");
	      ++app.tabcnt;
	      app.gen("MOVE TEMP " + t0 + " HALLOCATE " + 4 * (app.varmap.size() + 1));
	      app.gen("MOVE TEMP " + t1 + " HALLOCATE " + 4 * app.funcmap.size());
	      app.gen("HSTORE TEMP " + t0 + " 0 TEMP " + t1);
	      
	      boolean used[] = new boolean[app.funcmap.size()];
	      for(int i = 0; i < used.length; ++i)
	    	  used[i] = false;
	      
	      while(_class != null){
	    	  Vector temp_methods = _class.class_methods;
	    	  for(int i = 0; i < temp_methods.size(); ++i){
	    		  MiniMethod temp_func = (MiniMethod)temp_methods.elementAt(i);
	    		  String temp_func_name = temp_func.name;
	    		  int func_cnt = app.getFuncOffset(temp_func_name)/4;
	    		  if(!used[func_cnt]){
	    			  used[i] = true;
	    			  app.gen("HSTORE TEMP " + t1 + " " + 4*func_cnt + " " + class_name + "_" + temp_func_name);
	    		  }
	    	  }
	    	  _class = _class.getFather();
	      }
	      
	      --app.tabcnt;
	      app.gen("RETURN TEMP " + t0);
	      app.gen("END");
	      return _ret;
	   }

	   /**
	    * f0 -> "!"
	    * f1 -> Expression()
	    */
	   public BaseType visit(NotExpression n, BaseType argu) {
	      BaseType _ret=null;
	      MiniApp app = (MiniApp)argu;
	      app.gen("LT");
	      ++app.tabcnt;
	      n.f1.accept(this, argu);
	      --app.tabcnt;
	      app.gen("1");
	      return _ret;
	   }

	   /**
	    * f0 -> "("
	    * f1 -> Expression()
	    * f2 -> ")"
	    */
	   public BaseType visit(BracketExpression n, BaseType argu) {
	      BaseType _ret=null;
	      n.f1.accept(this, argu);
	      return _ret;
	   }
}
