package minijava.symboltable;

import java.util.NavigableMap;

public class MiniVar extends BaseType{
	public String type;
	public String name;
	public MiniClass PClass;
	public MiniMethod PMethod;
	public int hierarchy; // Defined in a class(1) or a method(2)
	public boolean assigned; // Assigned? Default False;
	
	public MiniVar(String _type, String _name, MiniApp _app){
		type = _type;
		name = _name;
		assigned = false;
		PClass = _app.currClass;
		PMethod = _app.currMethod;
	}
	
	public MiniVar(String _type, String _name, MiniApp _app, boolean _assigned) {
		type = _type;
		name = _name;
		assigned = _assigned;
		PClass = _app.currClass;
		PMethod = _app.currMethod;
	}
	
	public MiniVar(MiniVar anothor) {
		type = anothor.type;
		name = anothor.name;
		PClass = anothor.PClass;
		PMethod = anothor.PMethod;
		hierarchy = anothor.hierarchy;
		assigned = anothor.assigned;
	}
}
