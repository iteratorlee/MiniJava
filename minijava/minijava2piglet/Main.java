package minijava.minijava2piglet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import kanga.KangaParser;
import kanga.visitor.Kanga2Mips;
import minijava.MiniJavaParser;
import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import piglet.visitor.CountTmp;
import piglet.visitor.Piglet2Spiglet;
import spiglet.*;
import spiglet.visitor.*;
import minijava.symboltable.MiniApp;
import minijava.visitor.BuildSymbolTableVistor;
import minijava.visitor.Minijava2piglet;
import minijava.visitor.TypeCheckVisitor;


public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException{
		try{
			System.out.println("Please input the path of java file:");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String filePath = scanner.nextLine();
			File javaFile = new File(filePath);
			FileInputStream javaFileStream = new FileInputStream(javaFile);
			MiniJavaParser jParser = new MiniJavaParser(javaFileStream);
			@SuppressWarnings("static-access")
			minijava.syntaxtree.Node jgoal = jParser.Goal();
			MiniApp app = new MiniApp();
			MiniApp typeApp = new MiniApp();
			app.setFilename(filePath);
			typeApp.setFilename(filePath);
			BuildSymbolTableVistor buildSymbolTableVistor = new BuildSymbolTableVistor();
			jgoal.accept(buildSymbolTableVistor, app);
			jgoal.accept(buildSymbolTableVistor, typeApp);
			typeApp.handle_inheritance();
			TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
			jgoal.accept(typeCheckVisitor, typeApp);
			if(!typeApp.existErr){
				System.out.println("No type error.");
				app.buildGlobalMap();
				jgoal.accept(new Minijava2piglet(), app);
				System.out.println("Piglet file at \"" + app.filename + ".pg\"");
			}
		}catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (minijava.ParseException e) {
			e.printStackTrace();
		}
	}
}