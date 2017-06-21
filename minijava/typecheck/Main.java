package minijava.typecheck;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import minijava.MiniJavaParser;
import minijava.symboltable.MiniApp;
import minijava.syntaxtree.Node;
import minijava.visitor.BuildSymbolTableVistor;
import minijava.visitor.TypeCheckVisitor;

public class Main {
	public static void main(String[] args) {
		try {
			System.out.println("Please input the path of java file:");
			Scanner scanner = new Scanner(System.in);
			String filePath = scanner.nextLine();
			File javaFile = new File(filePath);
			FileInputStream javaFileStream = new FileInputStream(javaFile);
			MiniJavaParser parser = new MiniJavaParser(javaFileStream);
			Node goal = parser.Goal();
			MiniApp app = new MiniApp();
			app.setFilename(filePath);
			System.out.println(app.filename);
			goal.accept(new BuildSymbolTableVistor(), app);
			app.handle_inheritance();
			//app.print_classes();
			goal.accept(new TypeCheckVisitor(), app);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
