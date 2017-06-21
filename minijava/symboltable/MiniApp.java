package minijava.symboltable;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import javax.imageio.stream.IIOByteBuffer;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;
import javax.swing.text.AsyncBoxView.ChildLocator;

import minijava.syntaxtree.TrueLiteral;

public class MiniApp extends BaseType{
	/**
	 * File meta data
	 */
	public String filename;
	public boolean existErr = false;
	
	/**
	 * Fields used to build symbol table
	 */
	public Vector classes = new Vector();
	public MiniClass currClass; //Current class
	public MiniMethod currMethod; //Current method
	public int hierarchy; //Current position, in class(1) or in method(2)
	public Vector currPara; //Tempt array to store passing parameters
	public boolean inCheck; //If is under-check for parameter passing
	public boolean idType = false; //If a method or a variable is defined, this is true.(such as public 'Tree' function(){})
	
	/**
	 * Fields used to translate to piglet
	 */
	public int tabcnt = 0; //Current tab count
	public Vector funcmap = new Vector(); //Methods table
	public Vector varmap = new Vector(); //Variables table
	public Vector funclenmap = new Vector(); //Method parameters length table
	public int temp_offset = 0; //Current field(variable or function)'s address offset 
	public int global_flag = 0; //Current field is from inner function(1) or outside environment(0)
	public int max_temp_id = 0; //Max TEMP index, initially 0(or 21?)TODO
	public int max_label_id = 0; //MAX label index, initially 0
	public int curr_temp = 0;
	public int curr_temp_cnt = 0;
	
	public void setFilename(String path){
		filename = path.substring(0, path.indexOf('.'));
	}
	
	public void setError(boolean flag){
		existErr = flag;
	}
	
	public String insertClass(MiniClass _class){
		String class_name = _class.name;
		String fa_name = _class.fa_name;
		
		//fa_name can not be checked before inheritance handling
		//if(fa_name != null && !existed(fa_name))
			//return "Base class \"" + fa_name + "\" does not exist";
		if(existed(class_name))
			return "Class defination conflict(redefination): \"" + class_name + " \"";
		classes.addElement(_class);
		return null;
	}
	
	public boolean existed(String class_name){
		int size = classes.size();
		for(int i = 0; i < size; ++i){
			String temp_name = ((MiniClass)classes.elementAt(i)).name;
			if(class_name.equals(temp_name))
				return true;
		}
		return false;
	}
	
	public MiniClass getClassByName(String name) {
		int size = classes.size();
		for(int i = 0; i < size; ++i){
			MiniClass temp_class = (MiniClass)classes.elementAt(i);
			if(temp_class.name.equals(name))
				return temp_class;
		}
		return null;
	}
	
	public MiniVar getVarByName(String var_name) {
		if(hierarchy == 1)
			return currClass.getVarByName(var_name);
		else if(hierarchy == 2)
			return currMethod.getVarByName(var_name);
		else 
			return null;
	}
	
	public boolean hasVarType(MiniVar var){
		String var_type = var.type;
		if(var_type.equals("int") || var_type.equals("int[]") || var_type.equals("boolean"))
			return true;
		int size = classes.size();
		for(int i = 0; i < size; ++i){
			MiniClass temp_var = (MiniClass)classes.elementAt(i);
			if(temp_var.name.equals(var_type))
				return true;
		}
		return false;
	}
	
	public void copy_recourses(MiniClass father, MiniClass child){
		if(father.fa_class != null)
			copy_recourses(father.fa_class, father);
		
		/**
		 * Step 1 --> copy variables
		 * Step 2 --> copy methods
		 */
		
		//Step 1
		Vector father_vars = father.class_vars;
		int var_num = father_vars.size();
		for(int i = 0; i < var_num; ++i){
			MiniVar temp_var = (MiniVar)father_vars.elementAt(i);
			if(!child.existed(temp_var.name, 0))
				child.insertVar(new MiniVar(temp_var));
		}
		
		//Step 2
		for(int i = 0; i < father.class_methods.size(); ++i){
			MiniMethod father_method = (MiniMethod)father.class_methods.elementAt(i);
			boolean existed = false;
			for(int j = 0; j < child.class_methods.size(); ++j){
				MiniMethod child_method = (MiniMethod)child.class_methods.elementAt(j);
				if(father_method.name.equals(child_method.name)){
					existed = true;
					if(!father_method.ret_type.equals(child_method .ret_type)){
						setError(true);
						System.err.println("line " + child_method.location + " : return type has conflict with base class");
					}
					else{
						if(father_method.paraNum != child_method.paraNum){
							setError(true);
							System.err.println("line " + child_method.location + " : parameter number not equal to base class");
						}
						else{
							for(int k = 0; k < father_method.paraNum; ++k){
								MiniVar father_para = (MiniVar)father_method.vars.elementAt(k);
								MiniVar child_para = (MiniVar)child_method.vars.elementAt(k);
								if(!father_para.type.equals(child_para.type)){
									setError(true);
									System.err.println("line " + child_method.location + " : parameter type not match with base class");
								}
							}
						}
					}
					break;
				}
			}
		}
	}

	//When symbol table is built up, call this method
	public boolean handle_inheritance(){
		//TODO: find father for every class that extends another class
		int size = classes.size();
		for(int i = 0; i < size; ++i){
			MiniClass temp_class = (MiniClass)classes.elementAt(i);
			if(temp_class.fa_name != null){
				/**
				 * Handle inheritance
				 * Step 1 --> find father for every class
				 * Step 2 --> check inheritance loop
				 * Step 3 --> copy father's variables and methods to children
				 */
				if(temp_class.getFather() == null){
					setError(true);
					System.err.println("line " + temp_class.location + " : cannot find base class named \""
							+ temp_class.fa_name + "\"");
				}
				else{
					//check inheritance loop
					MiniClass temp = temp_class;
					boolean flag = false; //If has inheritance loop
					while(temp.getFather() != null){
						temp = temp.fa_class;
						if(temp.name.equals(temp_class.name)){
							setError(true);
							System.err.println("line : " + temp.location + " : inheritance loop with class");
							flag = true;
							break;
						}
					}
					if(!flag && temp_class.fa_class != null){
						//Copy recourses from father to child recursively
						copy_recourses(temp_class.fa_class, temp_class);
					}
				}
			}
		}
		//Add variables in class to every class method
		for(int i = 0; i < size; ++i){
			MiniClass temp_class = (MiniClass)classes.elementAt(i);
			Vector class_vars = temp_class.class_vars;
			int var_num = class_vars.size();
			int func_num = temp_class.class_methods.size();
			for(int j = 0; j < func_num; ++j){
				MiniMethod temp_method = (MiniMethod)temp_class.class_methods.elementAt(j);
				for(int k = 0; k < var_num; ++k){
					MiniVar temp_var =(MiniVar)class_vars.elementAt(k);
					if(!temp_method.existed(temp_var.name))
						temp_method.insertVar(temp_var);
				}
			}
		}
		return true;
	}
	
	public boolean isFather(String father, String child){
		if(child.equals("int") || child.equals("int[]") || child.equals("boolean"))
			return father.equals(child);
		MiniClass child_class = getClassByName(child);
		while(child_class != null){
			if(child_class.name.equals(father))
				return true;
			child_class = child_class.fa_class;
		}
		return false;
	}
	
	public void print_classes(){
		for(int i = 0; i < classes.size(); ++i){
			MiniClass temp_class = (MiniClass)classes.elementAt(i);
			System.out.println("##" + i + ": class name --> " + temp_class.name);
			System.out.println("##########################");
			temp_class.printVars();
			System.out.println("##########################");
			temp_class.printMethods();
			System.out.println();
		}
	}
	
	//Print tabs and code segment
	public void gen(String message){
		/*for(int i = 0; i < tabcnt; ++i)
				System.out.print("    ");
		System.out.println(message);*/
		BufferedWriter out = null;   
	    try {   
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(filename + ".pg", true)));
	         for(int i = 0; i < tabcnt; ++i)
	        	 out.write("    ");
	         out.write(message);
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
	
	//Insert a variable into variable map
	public void varMapInsert(String var_name){
		for(int i = 0; i < varmap.size(); ++i){
			String temp_name = (String)varmap.elementAt(i);
			if(temp_name.equals(var_name))
				return;
		}
		varmap.addElement(var_name);
	}
	
	//Insert a function name into function map
	public void funcMapInsert(String func_name, int len){
		for(int i = 0; i < funcmap.size(); ++i){
			String temp_name = (String)funcmap.elementAt(i);
			if(temp_name.equals(func_name))
				return;
		}
		funcmap.addElement(func_name);
		funclenmap.addElement(len);
	}
	
	//Build funcmap and varmap
	public void buildGlobalMap() {
		for(int i = 0; i < classes.size(); ++i){
			MiniClass temp_class = (MiniClass)classes.elementAt(i);
			
			Vector var_vector = temp_class.class_vars;
			for(int j = 0; j < var_vector.size(); ++j){
				MiniVar temp_var = (MiniVar)var_vector.elementAt(j);
				String temp_var_name = temp_var.name;
				varMapInsert(temp_var_name);
			}
			
			Vector func_vector = temp_class.class_methods;
			for(int j = 0; j < func_vector.size(); ++j){
				MiniMethod temp_method = (MiniMethod)func_vector.elementAt(j);
				String temp_func_name = temp_method.name;
				int para_number = temp_method.vars.size();
				funcMapInsert(temp_func_name, para_number);
			}
		}
	}
	
	public void printGlobalMap(){
		System.out.println("varmap");
		for(int i = 0; i < varmap.size(); ++i)
			System.out.println(varmap.elementAt(i));
		System.out.println("funcmap");
		for(int i = 0; i < funcmap.size(); ++i)
			System.out.println(funcmap.elementAt(i));
		System.out.println("funclenmap");
		for(int i = 0; i < funclenmap.size(); ++i)
			System.out.println(funclenmap.elementAt(i));
	}
	
	//Get the address of current field
	public void getAddr(String field) {
		Vector innerVec = currMethod.vars;
		for(int i = 0; i < innerVec.size(); ++i){
			MiniVar temp_var = (MiniVar)innerVec.elementAt(i);
			String temp_var_name = temp_var.name;
			if(temp_var_name.equals(field)){
				temp_offset = i * 4;
				global_flag = 1;
				return;
			}
		}
		
		Vector outerVec = varmap;
		for(int i = 0; i < outerVec.size(); ++i){
			if(outerVec.elementAt(i).equals(field)){
				temp_offset = (i + 1) * 4;
				global_flag = 0;
				return;
			}
		}
	}
	
	//Get the offset of a method
	public int getFuncOffset(String func_name){
		for(int i = 0; i < funcmap.size(); ++i){
			if(((String)funcmap.elementAt(i)).equals(func_name))
				return i*4;
		}
		//Not found
		return -1;
	}
	
	//Get the variable number of a method
	public int getFuncVarNumber(String func_name) {
		for(int i = 0; i < funcmap.size(); ++i){
			if(((String)funcmap.elementAt(i)).equals(func_name)){
				return (int)funclenmap.elementAt(i);
			}
		}
		//Not found
		return -1;
	}
}
