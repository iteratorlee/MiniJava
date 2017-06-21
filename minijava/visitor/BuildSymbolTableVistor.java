package minijava.visitor;

import java.lang.annotation.Retention;
import java.util.Enumeration;

import javax.management.monitor.MonitorNotification;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;

import minijava.symboltable.*;
import minijava.syntaxtree.*;

public class BuildSymbolTableVistor extends GJDepthFirst<BaseType, BaseType> {
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
	 * @author ly
	 * 
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> "public" f4 -> "static"
	 * f5 -> "void" f6 -> "main" f7 -> "(" f8 -> "String" f9 -> "[" f10 -> "]"
	 * f11 -> Identifier() f12 -> ")" f13 -> "{" f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )* f16 -> "}" f17 -> "}"
	 */
	public BaseType visit(MainClass n, BaseType argu) {
		BaseType _ret = null;
		String class_name = n.f1.f0.toString();
		MiniApp app = (MiniApp)argu;
		MiniClass main_class = new MiniClass(class_name, app, null, n.f1.f0.beginLine);
		//Insert main class to environment
		app.insertClass(main_class);
		app.currClass = main_class;
		app.hierarchy = 1;
		n.f14.accept(this, argu);
		n.f15.accept(this, argu);
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
	 * @author ly
	 * 
	 * f0 -> "class" f1 -> Identifier() f2 -> "{" f3 -> ( VarDeclaration() )* f4
	 * -> ( MethodDeclaration() )* f5 -> "}"
	 */
	public BaseType visit(ClassDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniClass temp_class;
		String class_name;
		String ret_str;
		
		class_name = ((TypeName)(n.f1.accept(this, argu))).name;
		MiniApp app = (MiniApp)argu;
		temp_class = new MiniClass(class_name, app, null, n.f1.f0.beginLine);
		ret_str = app.insertClass(temp_class);
		if(ret_str != null){
			app.setError(true);
			System.err.println("line : " + n.f1.f0.beginLine + " : " + ret_str);
		}else{
			app.currClass = temp_class;
			app.hierarchy = 1;
			n.f3.accept(this, argu);
			n.f4.accept(this, argu);
			app.hierarchy = 0;
			app.currClass = null;
		}
		return _ret;
	}

	/**
	 * f0 -> "class" f1 -> Identifier() f2 -> "extends" f3 -> Identifier() f4 ->
	 * "{" f5 -> ( VarDeclaration() )* f6 -> ( MethodDeclaration() )* f7 -> "}"
	 */
	public BaseType visit(ClassExtendsDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniClass temp_class;
		MiniClass fa_class;
		String class_name;
		String fa_name;
		String ret_str;
		
		class_name = ((TypeName)(n.f1.accept(this, argu))).name;
		fa_name = ((TypeName)(n.f3.accept(this, argu))).name;
		
		MiniApp app = (MiniApp)argu;
		temp_class = new MiniClass(class_name, app, fa_name, n.f1.f0.beginLine);
		ret_str = app.insertClass(temp_class);
		if(ret_str != null){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : " + ret_str);
		}else{
			app.currClass = temp_class;
			app.hierarchy = 1;
			n.f5.accept(this, argu);
			n.f6.accept(this, argu);
			app.hierarchy = 0;
			app.currClass = null;
		}
		return _ret;
	}

	/**
	 * f0 -> Type() f1 -> Identifier() f2 -> ";"
	 */
	public BaseType visit(VarDeclaration n, BaseType argu) {
		BaseType _ret = null;
		MiniClass currClass = ((MiniApp)argu).currClass;
		MiniMethod currMethod = ((MiniApp)argu).currMethod;
		MiniApp app = (MiniApp)argu;
		String var_name = n.f1.f0.toString();
		String var_type = ((TypeName)(n.f0.accept(this, argu))).name;
		String ret_str;
		int hierarchy = app.hierarchy;
		if(hierarchy == 1)
			ret_str = currClass.insertVar(new MiniVar(var_type, var_name, (MiniApp)argu));
		else 
			ret_str = currMethod.insertVar(new MiniVar(var_type, var_name, (MiniApp)argu));
		if(ret_str != null){
			app.setError(true);
			System.err.println("line " + n.f1.f0.beginLine + " : " + ret_str);
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
		String ret_type = ((TypeName)(n.f1.accept(this, argu))).name;
		String func_name = ((TypeName)(n.f2.accept(this, argu))).name;
		MiniApp app = (MiniApp)argu;
		MiniClass currClass = app.currClass;
		MiniMethod thisMethod;
		String ret_str;
		if(app.hierarchy == 2){
			app.setError(true);
			System.err.println("line " + n.f0.beginLine + " : methods is not allowed to be nested");
		}
		else{
			thisMethod = new MiniMethod(ret_type, func_name, app, n.f0.beginLine);
			currClass.insertMethod(thisMethod);
			app.currMethod = thisMethod;
			app.hierarchy = 2;
			n.f4.accept(this, argu);
			n.f7.accept(this, argu);
			app.currMethod = null;
			app.hierarchy = 1;
		}
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
		String type = ((TypeName)(n.f0.accept(this, argu))).name;
		String name = ((TypeName)(n.f1.accept(this, argu))).name;
		String ret_str;
		MiniApp app = (MiniApp)argu;
		MiniMethod currMethod = app.currMethod;
		MiniVar var = new MiniVar(type, name, app, true);//Parameters are all assigned(assigned=true)
		ret_str = currMethod.insertVar(var);
		++currMethod.paraNum;
		if(ret_str != null){
			app.setError(true);
			System.err.println("line : " + n.f1.f0.beginLine + " : " + ret_str);
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
		BaseType _ret = n.f0.accept(this, argu);
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
		//TODO
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Identifier() f1 -> "[" f2 -> Expression() f3 -> "]" f4 -> "=" f5 ->
	 * Expression() f6 -> ";"
	 */
	public BaseType visit(ArrayAssignmentStatement n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "if" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement() f5 ->
	 * "else" f6 -> Statement()
	 */
	public BaseType visit(IfStatement n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "while" f1 -> "(" f2 -> Expression() f3 -> ")" f4 -> Statement()
	 */
	public BaseType visit(WhileStatement n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "System.out.println" f1 -> "(" f2 -> Expression() f3 -> ")" f4 ->
	 * ";"
	 */
	public BaseType visit(PrintStatement n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> AndExpression() | CompareExpression() | PlusExpression() |
	 * MinusExpression() | TimesExpression() | ArrayLookup() | ArrayLength() |
	 * MessageSend() | PrimaryExpression()
	 */
	public BaseType visit(Expression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "&&" f2 -> PrimaryExpression()
	 */
	public BaseType visit(AndExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "<" f2 -> PrimaryExpression()
	 */
	public BaseType visit(CompareExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "+" f2 -> PrimaryExpression()
	 */
	public BaseType visit(PlusExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "-" f2 -> PrimaryExpression()
	 */
	public BaseType visit(MinusExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "*" f2 -> PrimaryExpression()
	 */
	public BaseType visit(TimesExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "[" f2 -> PrimaryExpression() f3 -> "]"
	 */
	public BaseType visit(ArrayLookup n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "." f2 -> "length"
	 */
	public BaseType visit(ArrayLength n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> PrimaryExpression() f1 -> "." f2 -> Identifier() f3 -> "(" f4 -> (
	 * ExpressionList() )? f5 -> ")"
	 */
	public BaseType visit(MessageSend n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> Expression() f1 -> ( ExpressionRest() )*
	 */
	public BaseType visit(ExpressionList n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "," f1 -> Expression()
	 */
	public BaseType visit(ExpressionRest n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> IntegerLiteral() | TrueLiteral() | FalseLiteral() | Identifier() |
	 * ThisExpression() | ArrayAllocationExpression() | AllocationExpression() |
	 * NotExpression() | BracketExpression()
	 */
	public BaseType visit(PrimaryExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public BaseType visit(IntegerLiteral n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "true"
	 */
	public BaseType visit(TrueLiteral n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "false"
	 */
	public BaseType visit(FalseLiteral n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public BaseType visit(Identifier n, BaseType argu) {
		//When variable declaration is a class
		BaseType _ret = new TypeName(n.f0.toString());
		return _ret;
	}

	/**
	 * f0 -> "this"
	 */
	public BaseType visit(ThisExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "new" f1 -> "int" f2 -> "[" f3 -> Expression() f4 -> "]"
	 */
	public BaseType visit(ArrayAllocationExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "new" f1 -> Identifier() f2 -> "(" f3 -> ")"
	 */
	public BaseType visit(AllocationExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "!" f1 -> Expression()
	 */
	public BaseType visit(NotExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "(" f1 -> Expression() f2 -> ")"
	 */
	public BaseType visit(BracketExpression n, BaseType argu) {
		BaseType _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}
}