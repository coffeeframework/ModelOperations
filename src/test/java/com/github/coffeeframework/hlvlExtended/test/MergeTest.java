package com.github.coffeeframework.hlvlExtended.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolutionCounter;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;
import com.github.coffeeframework.hlvlExtended.DIMACS;
import com.github.coffeeframework.hlvlExtended.Merge;

class MergeTest {

	public final static String INPUT_PATH = "./hlvlModels/testModelsMerge/";
	public final static String OUTPUT_PATH = "./outputTest/testModelsMerge/";

	@BeforeEach
	private void initialization() {

	}

	private String readFile(String path) throws FileNotFoundException, IOException {
		String content = "";
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			content += line + "\n";
		}
		br.close();
		fr.close();
		return content;
	}

	private void writeFile(String path, String content) {
		try {

			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.close();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private int solutions(String dimacsFilePath) {
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout

		Reader reader = new DimacsReader(solver);
		PrintWriter out = new PrintWriter(System.out, true);
		try {
			IProblem problem = reader.parseInstance(dimacsFilePath);

			if (problem.isSatisfiable()) {
				System.out.println("Satisfiable !");
				reader.decode(problem.model(), out);
			} else {
				System.out.println("Unsatisfiable !");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ContradictionException e) {
			System.out.println("Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
		}
		int solutions = -1;
		SolutionCounter counter = new SolutionCounter(solver);
		try {
			solutions = (int) counter.countSolutions();
			// the exact number of solutions is nbSol
			System.out.println("Number of Solutions: " + solutions);
		} catch (TimeoutException te) {
			solutions = counter.lowerBound() * -1;
			// the solver found lowerBound solutions so far.
			System.out.println("LowerBound" + solutions);
		}
		return solutions;
	}

	@Test
	public void merge_A_B() {
		String A = "model  A\n" + "elements: \n" + "	boolean a\n" + "	boolean b\n" + "   boolean c\n"
				+ "   boolean e\n" + "   boolean f\n" + "relations:\n" + "	r0: common(a)\n"
				+ "   r1: decomposition(a, [b], [1,1])" + "   r2: decomposition(a, [c], [0,1])"
				+ "   r3: group(c, [e,f], [1,1])";

		String B = "model  B\n" + "elements: \n" + "	boolean a\n" + "	boolean c\n" + "	boolean d\n"
				+ "relations:\n" + "	r0: common(a)\n" + "   r1: decomposition(a, [c], [1,1])"
				+ "   r2: decomposition(c, [d], [0,1])";

		String[] modelUris = { A, B };
		String outputPath = OUTPUT_PATH + "mergeAB.txt";

		try {
			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);
			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));

//			System.out.println(DIMACS.toString(currentDimacs.get(0)));
//			System.out.println(DIMACS.toString(currentDimacs.get(1)));
//			System.out.println(mergedDimacs);
			System.out.println(solutions(outputPath));

			writeFile(outputPath, mergedDimacs);
			assertTrue(solutions(outputPath) == 5);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void mergeFameDB() {
		try {
			String A = readFile(INPUT_PATH + "FameDB/FameDB-A.hlvl");
			String B = readFile(INPUT_PATH + "FameDB/FameDB-B.hlvl");
			String[] modelUris = { A, B };
			String outputPath = OUTPUT_PATH + "mergeFameDB.txt";

			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);

			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));
			writeFile(outputPath, mergedDimacs);

			System.out.println(DIMACS.toString(currentDimacs.get(0)));
			System.out.println(DIMACS.toString(currentDimacs.get(1)));
			System.out.println(mergedDimacs);
			System.out.println(solutions(outputPath));

			assertTrue(solutions(outputPath) == 54);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void mergeBerkeleyDB() {
		try {
			String A = readFile(INPUT_PATH + "BerkeleyDB/BerkeleyDB-A.hlvl");
			String B = readFile(INPUT_PATH + "BerkeleyDB/BerkeleyDB-B.hlvl");
			String[] modelUris = { A, B };
			String outputPath = OUTPUT_PATH + "mergeBerkeleyDB.txt";

			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);

			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));
			writeFile(outputPath, mergedDimacs);

			System.out.println(DIMACS.toString(currentDimacs.get(0)));
			System.out.println(DIMACS.toString(currentDimacs.get(1)));
			System.out.println(mergedDimacs);
			System.out.println(solutions(outputPath));

			assertTrue((long) solutions(outputPath) == 3699634944L);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void mergeCarSystem() {
		try {
			String A = readFile(INPUT_PATH + "CarSystem/carLightingSystem.hlvl");
			String B = readFile(INPUT_PATH + "CarSystem/carPeripherySupervisionSystem.hlvl");
			String[] modelUris = { A, B };
			String outputPath = OUTPUT_PATH + "mergeCarSystem.txt";

			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);

			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));
			writeFile(outputPath, mergedDimacs);

			System.out.println(DIMACS.toString(currentDimacs.get(0)));
			System.out.println(DIMACS.toString(currentDimacs.get(1)));
			System.out.println(mergedDimacs);
			System.out.println(solutions(outputPath));

			assertTrue(solutions(outputPath) == 2400); 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void mergeGPL() {
		try {
			String A = readFile(INPUT_PATH + "GPL/GPL-A.hlvl");
			String B = readFile(INPUT_PATH + "GPL/GPL-B.hlvl");
			String[] modelUris = { A, B };
			String outputPath = OUTPUT_PATH + "mergeGPL.txt";

			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);

			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));
			// String mergedDimacs = DIMACS.toString(currentDimacs.get(0));
			writeFile("./outputTest/testModelsMerge/mergeGPLA.txt", DIMACS.toString(currentDimacs.get(0)));
			writeFile("./outputTest/testModelsMerge/mergeGPLB.txt", DIMACS.toString(currentDimacs.get(1)));


			System.out.println(DIMACS.toString(currentDimacs.get(0)));
			System.out.println(DIMACS.toString(currentDimacs.get(1)));
			System.out.println(mergedDimacs);
			System.out.println(solutions(outputPath));

			assertTrue(solutions(outputPath) == 18432); //36864

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
