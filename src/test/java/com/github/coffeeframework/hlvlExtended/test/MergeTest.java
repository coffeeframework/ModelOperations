package com.github.coffeeframework.hlvlExtended.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

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

class MergeTest {

	@Test
	void test() {
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		PrintWriter out = new PrintWriter(System.out, true);
		try {
			IProblem problem = reader.parseInstance("./test/fameDB.txt");
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

		SolutionCounter counter = new SolutionCounter(solver);
		try {
			long nbSol = counter.countSolutions();
			// the exact number of solutions is nbSol
			System.out.println("Number of Solutions: " + nbSol);
		} catch (TimeoutException te) {
			int lowerBound = counter.lowerBound();
			// the solver found lowerBound solutions so far.
			System.out.println("LowerBound" + lowerBound);
		}
	}
}
