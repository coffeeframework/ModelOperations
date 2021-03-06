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
import com.github.coffeeframework.hlvlExtended.MergeMode;

class MergeTest {

	public final static String INPUT_PATH = "./hlvlModels/testModelsMerge/";
	public final static String OUTPUT_PATH = "./outputTest/testModelsMerge/";

	private String readFile(String path) {
		String content = "";
		FileReader fr;
		try {
			fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				content += line + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
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
			e.printStackTrace();
			fail();
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
			fail();
		} catch (ParseFormatException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (ContradictionException e) {
			System.out.println("Unsatisfiable (trivial)!");
			fail();
		} catch (TimeoutException e) {
			System.out.println("Timeout, sorry!");
			fail();
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
			fail();
		}
		return solutions;
	}

	@Test
	public void mergeToyModel() {
		String A = readFile(INPUT_PATH + "ToyModel/ToyModelA.hlvl");
		String B = readFile(INPUT_PATH + "ToyModel/ToyModelB.hlvl");

		String[] modelUris = { A, B };
		String outputPath = OUTPUT_PATH + "mergeToyModel.txt";

		try {
			Model[] models = HLVLParser.getInstance().generateModels(modelUris);
			String mergedDimacs = Merge.mergeToString(models, MergeMode.UNION);
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

			Model[] models = HLVLParser.getInstance().generateModels(modelUris);
			String mergedDimacs = Merge.mergeToString(models, MergeMode.UNION);
			writeFile(outputPath, mergedDimacs);

			assertTrue(solutions(outputPath) == 54);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void mergeCarSystem() {
		try {
			String A = readFile(INPUT_PATH + "CarSystem/carLightingSystem.hlvl");
			String B = readFile(INPUT_PATH + "CarSystem/carPeripherySupervisionSystem.hlvl");
			String[] modelUris = { A, B };
			String outputPath = OUTPUT_PATH + "mergeCarSystem.txt";

			Model[] models = HLVLParser.getInstance().generateModels(modelUris);
			String mergedDimacs = Merge.mergeToString(models, MergeMode.UNION);
			writeFile(outputPath, mergedDimacs);

			assertTrue(solutions(outputPath) == 2400);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
