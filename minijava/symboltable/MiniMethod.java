package minijava.symboltable;

import java.awt.geom.AffineTransform;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;

public class MiniMethod extends BaseType{
	public String ret_type; //Return type
	public String name;
	public String fa_name; //Father class name
	public MiniClass fa_class; //Father class
	public int location; //Method location(line number)
	public int paraNum; //Number of parameters, initially 0
	public Vector vars = new Vector();
	
	public MiniMethod(String _ret_type, String _name, MiniApp _app, int _location) {
		ret_type = _ret_type;
		name = _name;
		fa_class = _app.currClass;
		fa_name = fa_class.name;
		location = _location;
	}
	
	public MiniMethod(MiniMethod another) {
		vars.clear();
		ret_type = another.ret_type;
		name = another.name;
		fa_class = another.fa_class;
		fa_name = another.fa_name;
		int size = vars.size();
		paraNum = another.paraNum;
		Vector temp_vec = another.vars;
		for(int i = 0; i < size; ++i){
			MiniVar temp_var = (MiniVar)temp_vec.elementAt(i);
			vars.addElement(new MiniVar(temp_var));
		}
	}
	
	public String insertVar(MiniVar _var){
		String var_name = _var.name;
		if(existed(var_name))
			return "Variable defination conflict(redefination): \"" + var_name + "\" in method \"" + name + "\"";
		vars.addElement(_var);
		return null;
	}
	
	public boolean existed(String var_name){
		int size = vars.size();
		for(int i = 0; i < size; ++i){
			MiniVar temp_var = (MiniVar)(vars.elementAt(i));
			if(var_name.equals(temp_var.name))
				return true;
		}
		return false;
	}
	
	public MiniVar getVarByName(String var_name){
		int size = vars.size();
		for(int i = 0; i < size; ++i){
			MiniVar temp_var = (MiniVar)vars.elementAt(i);
			if(temp_var.name.equals(var_name))
				return temp_var;
		}
		return null;
	}
	
	public boolean copyVarsFromClass(){
		//TODO: copy variables from the the method's father class
		return true;
	}
}
