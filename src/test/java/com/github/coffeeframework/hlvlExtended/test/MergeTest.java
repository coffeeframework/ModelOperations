package com.github.coffeeframework.hlvlExtended.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@BeforeEach
	private void initialization() {
		
	}
	
	private String readFile(String path) throws FileNotFoundException, IOException {
	      String content = "";
	      FileReader fr = new FileReader(path);
	      BufferedReader br = new BufferedReader(fr);
	      String line;
	      while((line = br.readLine())!=null) {
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
	
	
	private int evaluate(String dimacsFilePath) {
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
		String A = "model  A\n" + 
				"elements: \n" + 
				"	boolean a\n" + 
				"	boolean b\n" + 
				"relations:\n" + 
				"	r0: common(a)\n" +
				"   r1: decomposition(a, [b], [1,1])";
		
		String B = "model  B\n" + 
				"elements: \n" + 
				"	boolean a\n" +
				"	boolean c\n" +
				"relations:\n" + 
				"	r0: common(a)\n" + 
				"   r1: decomposition(a, [c], [1,1])";
		
		String[] modelUris = { A, B };
		
		try {
			
			HLVLParser.getInstance();
			Model[] models = HLVLParser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = HLVLParser.getDIMACSs(models);
			
			String mergedDimacs = DIMACS.toString(Merge.union(currentDimacs));
			writeFile("./test/merge_A_B.txt", mergedDimacs);
			
			System.out.println(DIMACS.toString(currentDimacs.get(0)));
			System.out.println(DIMACS.toString(currentDimacs.get(1)));
			System.out.println(mergedDimacs);
			
			//assertTrue(evaluate("./test/merge_A_B.txt") == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
