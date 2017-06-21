package piglet.piglet2spiglet;

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
			System.out.println("Please input the path of piglet file:");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String filePath = scanner.nextLine();
			CountTmp countTmp = new CountTmp();
			countTmp.setFilename(filePath);
			Piglet2Spiglet pg2spg = new Piglet2Spiglet();
			pg2spg.setFilename(filePath);
			File pigletFile = new File(pg2spg.filename + ".pg");
			FileInputStream pigletFileStream = new FileInputStream(pigletFile);
			PigletParser parser = new PigletParser(pigletFileStream);
			@SuppressWarnings("static-access")
			piglet.syntaxtree.Node goal = parser.Goal();
			goal.accept(countTmp, null);
			pg2spg.currTemp = countTmp.currTemp;
			goal.accept(pg2spg, null);
			System.out.println("Spiglet file at \"" + pg2spg.filename + ".spg\"");
		}catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (ParseException e) {
			e.printStackTrace();
		}
	}
}