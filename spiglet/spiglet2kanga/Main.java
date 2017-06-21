package spiglet.spiglet2kanga;

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
			System.out.println("Please input the path of spiglet file:");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String filePath = scanner.nextLine();
			Spiglet2Kanga spg2kg = new Spiglet2Kanga();
			spg2kg.setFilename(filePath);
			spiglet.main.PrintKanga.setFilename(filePath);
			File spigletfile = new File(spg2kg.filename + ".spg");
			FileInputStream spigletFileStream = new FileInputStream(spigletfile);
			SpigletParser sparser = new SpigletParser(spigletFileStream);
			@SuppressWarnings("static-access")
			spiglet.syntaxtree.Node sgoal = sparser.Goal();
			sgoal.accept(new BasicBlock(), null);
			sgoal.accept(new DataFlow(), null);
			spiglet.main.AllProc.assignReg();
			sgoal.accept(new Spiglet2Kanga(), null);
			System.out.println("Kanga file at \"" + spg2kg.filename + ".kg\"");
		}catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (spiglet.ParseException e) {
			e.printStackTrace();
		}
	}
}