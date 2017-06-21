package minijava.visitor;

import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.Attributes.Name;

import javax.print.attribute.Size2DSyntax;
import javax.sound.sampled.Line;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;
import javax.swing.plaf.synth.SynthStyle;

import org.omg.PortableServer.AdapterActivatorOperations;

import minijava.syntaxtree.*;
import minijava.symboltable.*;

public class TypeCheckVisitor extends GJDepthFirst<BaseType, BaseType> {
	//
	// Auto class visitors--probably don't need to be overridden.
	//
	public BaseType visit(NodeList n, BaseType argu) {
		BaseType _ret = null;
		int _count = 0;
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public BaseType visit(NodeListOptional n, BaseType argu) {
		if (n.present()) {
			BaseType _ret = null;
			int _count = 0;
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
				e.nextElement().accept(this, argu);
				_count++;
			}
			return _ret;
		} else
			return null;
	}

	public BaseType visit(NodeOptional n, BaseType argu) {
		if (n.present())
			return n.node.accept(this, argu);
		else
			return null;
	}

	public BaseType visit(NodeSequence n, BaseType argu) {
		BaseType _ret = null;
		int _count = 0;
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
			e.nextElement().accept(this, argu);
			_count++;
		}
		return _ret;
	}

	public BaseType visit(NodeToken n, BaseType argu) {
		return null;
	}

	//
	// User-generated visitor methods below
	//

	/**
	 * f0 -> MainClass() f1 -> ( TypeDeclaration() )* f2 -> <EOF>
	 */
	public BaseType visit(Goal n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> "public" f4 -> "static"
	 * f5 -> "void" f6 -> "main" f7 -> "(" f8 -> "String" f9 -> "[" f10 -> "]"
	 * f11 -> Identifier() f12 -> ")" f13 -> "{" f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )* f16 -> "}" f17 -> "}"
	 */
	public BaseType visit(MainClass n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String class_name = n.f1.f0.toString();
		MiniClass main_class = app.getClassByName(class_name);
		app.currClass = main_class;
		app.hierarchy = 1;
		n.f14.accept(this, argu);
		n.f15.accept(this, argu);
		app.currClass = null;
		app.hierarchy = 0;
		return _ret;
	}

	/**
	 * f0 -> ClassDeclaration() | ClassExtendsDeclaration()
	 */
	public BaseType visit(TypeDeclaration n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> ( VarDeclaration() )* f4
	 * -> ( MethodDeclaration() )* f5 -> "}"
	 */
	public BaseType visit(ClassDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		MiniClass _class;
		String class_name = n.f1.f0.toString();
		
		_class = app.getClassByName(class_name);
		app.currClass = _class;
		app.hierarchy = 1;
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		app.hierarchy = 0;
		app.currClass = null;
		return _ret;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "extends" f3 -> Identifier() f4 ->
	 * "{" f5 -> ( VarDeclaration() )* f6 -> ( MethodDeclaration() )* f7 -> "}"
	 */
	public BaseType visit(ClassExtendsDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String class_name = n.f1.f0.toString();
		MiniClass _class = app.getClassByName(class_name);
		
		app.currClass = _class;
		app.hierarchy = 1;
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		app.hierarchy = 0;
		app.currClass = null;
		
		return _ret;
	}

	/**
	 * f0 -> Type() f1 -> Identifier() f2 -> ";"
	 */
	public BaseType visit(VarDeclaration n, BaseType argu) {
		BaseType _ret = null;
		String var_name = n.f1.f0.toString();
		MiniApp app = (MiniApp)argu;
		MiniVar temp_var = app.getVarByName(var_name);
		if(temp_var == null){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : variable \"" + n.f1.f0.toString() + "\" is not defined.");
		}
		else if(!app.hasVarType(temp_var)){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : type \"" + n.f0.toString() + "\" does not exsit.");
		}
		return _ret;
	}

	/**
	 * f0 -> "public" f1 -> Type() f2 -> Identifier() f3 -> "(" f4 -> (
	 * FormalParameterList() )? f5 -> ")" f6 -> "{" f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )* f9 -> "return" f10 -> Expression() f11 -> ";" f12
	 * -> "}"
	 */
	public BaseType visit(MethodDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		MiniClass _class = app.currClass;
		String func_name = n.f2.f0.toString();
		MiniMethod _method = _class.getMethodByName(func_name);
		app.idType = true;
		String ret_type_beg = ((TypeName)n.f1.accept(this, argu)).name, ret_type_end;
		app.idType = false;
		
		app.currMethod = _method;
		app.hierarchy = 2;
		n.f4.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		ret_type_end = ((TypeName)n.f10.accept(this,argu)).name;
		if(!app.isFather(ret_type_beg, ret_type_end) && ret_type_beg != "" && ret_type_end != ""){
			app.setError(true);
			System.err.println("line " + n.f9.beginLine + " : return type \"" + ret_type_beg + "\" has conflict with type \""
					+ ret_type_end + "\" at line " + n.f0.beginLine);
		}
		app.hierarchy = 1;
		app.currMethod = null;
		return _ret;
	}

	/**
	 * f0 -> FormalParameter() f1 -> ( FormalParameterRest() )*
	 */
	public BaseType visit(FormalParameterList n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Type() f1 -> Identifier()
	 */
	public BaseType visit(FormalParameter n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String para_name = n.f1.f0.toString();
		MiniVar _var = app.getVarByName(para_name);
		//TODO: _var == null ?
		if(_var == null){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : so such parameter :\"" + n.f1.f0.toString() + "\"");
		}
		else if(!app.hasVarType(_var)){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : so such type :\"" + n.f0.toString() + "\"");
		}
		return _ret;
	}

	/**
	 * f0 -> "," f1 -> FormalParameter()
	 */
	public BaseType visit(FormalParameterRest n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> ArrayType() | BooleanType() | IntegerType() | Identifier()
	 */
	public BaseType visit(Type n, BaseType argu) {
		BaseType _ret = (TypeName)n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "int" f1 -> "[" f2 -> "]"
	 */
	public BaseType visit(ArrayType n, BaseType argu) {
		BaseType _ret = new TypeName("int[]");
		return _ret;
	}

	/**
	 * f0 -> "boolean"
	 */
	public BaseType visit(BooleanType n, BaseType argu) {
		BaseType _ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> "int"
	 */
	public BaseType visit(IntegerType n, BaseType argu) {
		BaseType _ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> Block() | AssignmentStatement() | ArrayAssignmentStatement() |
	 * IfStatement() | WhileStatement() | PrintStatement()
	 */
	public BaseType visit(Statement n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "{" f1 -> ( Statement() )* f2 -> "}"
	 */
	public BaseType visit(Block n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Identifier() f1 -> "=" f2 -> Expression() f3 -> ";"
	 */
	public BaseType visit(AssignmentStatement n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		MiniClass _class = app.currClass;
		String var_name = n.f0.f0.toString();
		MiniVar _var = app.getVarByName(var_name);
		if(_var == null){
			app.setError(true);
			System.err.println("line " + n.f0.f0.beginLine + " : variable \"" + n.f0.f0.toString()
			+ "\" is not defined");
		}else{
			TypeName temp_type = (TypeName)n.f2.accept(this, argu);
			String exp_name = temp_type.name;
			//TODO: expr_name == "" ?
			if(!app.isFather(_var.type, exp_name)){
				app.setError(true);
				System.err.println("line " + n.f0.f0.beginLine + " : assignment type don't match");
			}
			_var.assigned = true;
		}
		return _ret;
	}

	/**
	 * f0 -> Identifier() f1 -> "[" f2 -> Expression() f3 -> "]" f4 -> "=" f5 ->
	 * Expression() f6 -> ";"
	 */
	public BaseType visit(ArrayAssignmentStatement n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String array_name = n.f0.f0.toString();
		MiniVar array_var = app.getVarByName(array_name);
		String index_type = ((TypeName)n.f2.accept(this, argu)).name;
		String value_type = ((TypeName)n.f5.accept(this, argu)).name;
		if(array_var == null){
			app.setError(true);
			System.err.println("line " + n.f0.f0.beginLine + " : \"" + n.f0.f0.toString()
			+ "\" does not exist");
		}
		else if(!array_var.type.equals("int[]")){
			app.setError(true);
			System.err.println("line " + n.f0.f0.beginLine + " : \"" + n.f0.f0.toString()
			+ "\" is not an integer array");
		}
		else {
			if(!index_type.equals("int")){
				app.setError(true);
				System.err.println("line " + n.f1.beginLine + " : array index should be an integer");
			}
			else if(!value_type.equals("int")){
				app.setError(true);
				System.err.println("line " + n.f4.beginLine + " : object to be assigned should be an integer");
			}
		}
		
		return _ret;
	}

	/**
	 * f0 -> "if" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement() f5 ->
	 * "else" f6 -> Statement()
	 */
	public BaseType visit(IfStatement n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String judge_type = ((TypeName)n.f2.accept(this, argu)).name;
		if(!judge_type.equals("boolean")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : the if condition type should be \"boolean\"");
		}
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "while" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement()
	 */
	public BaseType visit(WhileStatement n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String judge_type = ((TypeName)n.f2.accept(this, argu)).name;
		if(!judge_type.equals("boolean")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : the while condition type shold be \"boolean\"");
		}
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "System.out.println" f1 -> "(" f2 -> Expression() f3 -> ")" f4 ->
	 * ";"
	 */
	public BaseType visit(PrintStatement n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String print_type = ((TypeName)n.f2.accept(this, argu)).name;
		if(!print_type.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : print expression should return a integer type");
		}
		return _ret;
	}

	/**
	 * f0 -> AndExpression() | CompareExpression() | PlusExpression() |
	 * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
	 * MessageSend() | PrimaryExpression()
	 */
	public BaseType visit(Expression n, BaseType argu) {
		BaseType _ret = (TypeName)n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "&&" f2 -> PrimaryExpression()
	 */
	public BaseType visit(AndExpression n, BaseType argu) {
		BaseType _ret = null; 
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("boolean")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine+ " : not a boolean type");
		}
		if(!type_2.equals("boolean")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine+ " : not a boolean type");
		}
		_ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "<" f2 -> PrimaryExpression()
	 */
	public BaseType visit(CompareExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		if(!type_2.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "+" f2 -> PrimaryExpression()
	 */
	public BaseType visit(PlusExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		if(!type_2.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "-" f2 -> PrimaryExpression()
	 */
	public BaseType visit(MinusExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		if(!type_2.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "*" f2 -> PrimaryExpression()
	 */
	public BaseType visit(TimesExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		if(!type_2.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "[" f2 -> PrimaryExpression() f3 -> "]"
	 */
	public BaseType visit(ArrayLookup n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		String type_2 = ((TypeName)n.f2.accept(this, argu)).name;
		if(!type_1.equals("int[]")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer array type");
		}
		if(!type_2.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "." f2 -> "length"
	 */
	public BaseType visit(ArrayLength n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String type_1 = ((TypeName)n.f0.accept(this, argu)).name;
		if(!type_1.equals("int[]")){
			app.setError(true);
			System.err.println("line " + n.f1.beginLine + " : not a integer type");
		}
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "." f2 -> Identifier() f3 -> "(" f4 -> (
	 * ExpressionList() )? f5 -> ")"
	 */
	public BaseType visit(MessageSend n, BaseType argu) {
		BaseType _ret = new TypeName("");
		//TODO: message sending
		MiniApp app = (MiniApp)argu;
		TypeName prim_type = (TypeName)n.f0.accept(this, argu);
		String func_name = n.f2.f0.toString();
		MiniClass _class = app.getClassByName(prim_type.name);
		if(_class != null){
			MiniMethod _func = _class.getMethodByName(func_name);
			if(_func == null){
				app.setError(true);
				System.err.println("line " + n.f1.toString() + " : class \""
						+ _class.name + "\" has no method named \"" + func_name + "\"");
			}
			else {
				_ret = new TypeName(_func.ret_type); //Return the type that _func return
				Vector formerPara = app.currPara;
				Vector currPara = new Vector();
				app.currPara = currPara;
				n.f4.accept(this, argu); //Initialize parameter list
				app.currPara = formerPara;
				
				int size = currPara.size();
				if(size != _func.paraNum){
					app.setError(true);
					System.err.println("line " + n.f3.beginLine + " : parameter number error");
				}
				else{
					for(int i = 0; i < size; ++i){
						//currPara and _func.vars
						//TODO: get parameter types passed in
						String type_1 = ((TypeName)currPara.elementAt(i)).name;
						String type_2 = ((MiniVar)_func.vars.elementAt(i)).type;
						if(!type_1.equals(type_2) && !app.isFather(type_2, type_1)){
							app.setError(true);
							System.err.println("line " + n.f3.beginLine + " : parameter type not match");
						}
					}
				}
			}
		}
		
		return _ret;
	}

	/**
	 * f0 -> Expression() f1 -> ( ExpressionRest() )*
	 */
	@SuppressWarnings("unchecked")
	public BaseType visit(ExpressionList n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		app.currPara.addElement((TypeName)n.f0.accept(this, argu));
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "," f1 -> Expression()
	 */
	@SuppressWarnings("unchecked")
	public BaseType visit(ExpressionRest n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		app.currPara.addElement((TypeName)n.f1.accept(this, argu));
		return _ret;
	}

	/**
	 * f0 -> IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() |
	 * ThisExpression() | ArrayAllocationExpression() | AllocationExpression() |
	 * NotExpression() | BracketExpression()
	 */
	public BaseType visit(PrimaryExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		app.inCheck = true;
		_ret = (TypeName)n.f0.accept(this,argu);
		app.inCheck = false;
		return _ret;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public BaseType visit(IntegerLiteral n, BaseType argu) {
		BaseType _ret = null;
		_ret = new TypeName("int");
		return _ret;
	}

	/**
	 * f0 -> "true"
	 */
	public BaseType visit(TrueLiteral n, BaseType argu) {
		BaseType _ret = null;
		_ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> "false"
	 */
	public BaseType visit(FalseLiteral n, BaseType argu) {
		BaseType _ret = null;
		_ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public BaseType visit(Identifier n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		if(!app.idType){
			String var_name = n.f0.toString();
			MiniVar _var = app.getVarByName(var_name);
			if(_var == null){
				_ret = new TypeName("");
				app.setError(true);
				System.err.println("line " +n.f0.beginLine + " : variable \"" + var_name
						+ "\" does not exist");
			}
			else{
				_ret = new TypeName(_var.type);
				if(app.inCheck && !_var.assigned){
					app.setError(true);
					System.err.println("line " + n.f0.beginLine + " : parameter \"" + var_name
						+ "\" is not initialized");
				}
			}
		}
		else{
			String class_name = n.f0.toString();
			MiniClass _cClass = app.getClassByName(class_name);
			if(_cClass != null)
				_ret = new TypeName(class_name);
			else{
				app.setError(true);
				_ret = new TypeName("");
				System.err.println("line " + n.f0.beginLine + " : class \"" + class_name + "\" not defined");
			}
		}
		return _ret;
	}

	/**
	 * f0 -> "this"
	 */
	public BaseType visit(ThisExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		_ret = new TypeName(app.currClass.name);
		return _ret;
	}

	/**
	 * f0 -> "new" f1 -> "int" f2 -> "[" f3 -> Expression() f4 -> "]"
	 */
	public BaseType visit(ArrayAllocationExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String exp_type = ((TypeName)n.f3.accept(this, argu)).name;
		if(!exp_type.equals("int")){
			app.setError(true);
			System.err.println("line " + n.f2.beginLine + " : array index should be an integer");
		}
		//TODO: return an array type?
		_ret = new TypeName("int[]");
		return _ret;
	}

	/**
	 * f0 -> "new" f1 -> Identifier() f2 -> "(" f3 -> ")"
	 */
	public BaseType visit(AllocationExpression n, BaseType argu) {
		BaseType _ret = null;
		String class_name = n.f1.f0.toString();
		MiniApp app = (MiniApp)argu;
		MiniClass _class = app.getClassByName(class_name);
		if(_class == null){
			app.setError(true);
			System.err.println("line : " + n.f0.beginLine + " : no such class \""
					+ class_name + "\"");
		}
		else
			_ret = new TypeName(class_name);
		return _ret;
	}

	/**
	 * f0 -> "!" f1 -> Expression()
	 */
	public BaseType visit(NotExpression n, BaseType argu) {
		BaseType _ret = null;
		MiniApp app = (MiniApp)argu;
		String exp_type = ((TypeName)n.f1.accept(this, argu)).name;
		if(!exp_type.equals("boolean")){
			app.setError(true);
			System.err.println("line " + n.f0.beginLine + " : not a boolean type");
		}
		_ret = new TypeName("boolean");
		return _ret;
	}

	/**
	 * f0 -> "(" f1 -> Expression() f2 -> ")"
	 */
	public BaseType visit(BracketExpression n, BaseType argu) {
		BaseType _ret = n.f1.accept(this,argu);
		return _ret;
	}

}
