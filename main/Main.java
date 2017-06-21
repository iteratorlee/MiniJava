package main;

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
			
				Kanga2Mips kg2mips = new Kanga2Mips();
				kg2mips.setFilename(filePath);
				File kangafile = new File(kg2mips.filename + ".kg");
				FileInputStream kangaFileStream = new FileInputStream(kangafile);
				KangaParser kparser = new KangaParser(kangaFileStream);
				@SuppressWarnings("static-access")
				kanga.syntaxtree.Node kgoal = kparser.Goal();
				kgoal.accept(kg2mips, null);
				System.out.println("Mips file at \"" + kg2mips.filename + ".s\"");
			}
		}catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (ParseException e) {
			e.printStackTrace();
		} catch (minijava.ParseException e) {
			e.printStackTrace();
		} catch (spiglet.ParseException e) {
			e.printStackTrace();
		} catch (kanga.ParseException e) {
			e.printStackTrace();
		}
	}
}