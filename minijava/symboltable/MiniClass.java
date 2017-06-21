package minijava.symboltable;

import java.util.Vector;

public class MiniClass extends BaseType{
	public String name;
	public MiniApp app;
	public Vector class_vars = new Vector(); //Variables defined in this class
	public Vector class_methods = new Vector(); //Functions defined in this class
	
	public String fa_name; //Father class name
	public MiniClass fa_class; //Father class
	public int location;
	
	//Constructor
	public MiniClass(String _name, MiniApp _app, String _fa_name, int _location) {
		name = _name;
		app = _app;
		fa_name = _fa_name;
		location = _location;
	}
	
	//Insert a variable to current class
	public String insertVar(MiniVar var) {
		String var_name = var.name;
		if(existed(var_name, 0))
			return "Variable defination conflict(redefination): \"" + var_name + "\" in class \"" + name + "\"";
		var.assigned = true;
		class_vars.addElement(var);
		return null;
	}
	
	//Insert a method to current class
	public String insertMethod(MiniMethod method){
		String method_name = method.name;
		if(existed(method_name, 1))
			return "Method overloading is not allowed: \"" + method_name + "\" in class \"" + name + "\"";
		class_methods.addElement(method);
		return null;
	}
	
	//Check if a variable(0) or a method(1) is already existed
	public boolean existed(String _var_name, int flag) {
		if(flag == 0){
			int size = class_vars.size();
			for(int i = 0; i < size; ++i){
				String temp_name = ((MiniVar)class_vars.elementAt(i)).name;
				if(_var_name.equals(temp_name))
					return true;
			}
			return false;
		}
		else{
			int size = class_methods.size();
			for(int i = 0; i < size; ++i){
				String temp_name = ((MiniMethod)class_methods.elementAt(i)).name;
				if(_var_name.equals(temp_name))
					return true;
			}
			return false;
		}
	}
	
	public MiniVar getVarByName(String var_name){
		int size = class_vars.size();
		for(int i = 0; i < size; ++i){
			MiniVar temp_var = (MiniVar)class_vars.elementAt(i);
			if(temp_var.name.equals(var_name))
				return temp_var;
		}
		return null;
	}
	
	public MiniMethod getMethodByName(String func_name){
		int size = class_methods.size();
		for(int i = 0; i < size; ++i){
			MiniMethod temp_method = (MiniMethod)class_methods.elementAt(i);
			if(temp_method.name.equals(func_name))
				return temp_method;
		}
		return null;
	}
	
	public MiniClass getFather(){
		int size = app.classes.size();
		for(int i = 0; i < size; ++i){
			MiniClass temp_class = (MiniClass)app.classes.elementAt(i);
			if(temp_class.name.equals(fa_name)){
				fa_class = temp_class;
				return fa_class;
			}
		}
		return null;
	}
	
	public void printVars(){
		System.out.println("var number "  + class_vars.size());
		for(int i = 0; i < class_vars.size(); ++i){
			MiniVar temp_var = (MiniVar)class_vars.elementAt(i);
			System.out.println("#" + i + ": type --> " + temp_var.type + ", name --> " + temp_var.name);
		}
	}
	
	public void printMethods(){
		System.out.println("method number "  + class_methods.size());
		for(int i = 0; i < class_methods.size(); ++i){
			MiniMethod temp_method = (MiniMethod)class_methods.elementAt(i);
			System.out.println("#" + i + ": ret_type --> " + temp_method.ret_type + ", name --> " + temp_method.name);
			for(int j = 0; j < temp_method.vars.size(); ++j){
				System.out.println(temp_method.name + "'s var#" + j + " " + ((MiniVar)temp_method.vars.elementAt(j)).name);
			}
		}
	}
}