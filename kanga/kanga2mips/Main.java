package kanga.kanga2mips;

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
			System.out.println("Please input the path of kanga file:");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String filePath = scanner.nextLine();
			Kanga2Mips kg2mips = new Kanga2Mips();
			kg2mips.setFilename(filePath);
			File kangafile = new File(kg2mips.filename + ".kg");
			FileInputStream kangaFileStream = new FileInputStream(kangafile);
			KangaParser kparser = new KangaParser(kangaFileStream);
			@SuppressWarnings("static-access")
			kanga.syntaxtree.Node kgoal = kparser.Goal();
			kgoal.accept(kg2mips, null);
			System.out.println("Mips file at \"" + kg2mips.filename + ".s\"");
		}catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (kanga.ParseException e) {
			e.printStackTrace();
		}
	}
}