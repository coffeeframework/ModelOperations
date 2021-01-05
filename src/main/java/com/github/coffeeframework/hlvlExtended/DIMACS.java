package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.List;

import com.coffee.generator.HLVLParser;

public class DIMACS {

	public static List<List<Integer>> dimacsDisjunction(List<List<List<Integer>>> modelsDimacs) {

		List<List<Integer>> result = new ArrayList<>();

		for (int i = 0; i < modelsDimacs.size() - 1; i++) {
			List<List<Integer>> firstDimacs = modelsDimacs.get(i);
			List<List<Integer>> secondDimacs = modelsDimacs.get(i + 1);

			for (int j = 0; j < firstDimacs.size(); j++) {
				for (int j2 = 0; j2 < secondDimacs.size(); j2++) {
					result.addAll(clausesDisjunction(firstDimacs.get(j), secondDimacs.get(j2)));
				}
			}
		}
		return result;
	}

	private static List<List<Integer>> clausesDisjunction(List<Integer> firstClause, List<Integer> secondClause) {

		List<List<Integer>> result = new ArrayList<>();

		for (Integer firstClauseVariable : firstClause) {
			for (Integer secondClauseVariable : secondClause) {
				List<Integer> newClause = new ArrayList<>();
				newClause.add(firstClauseVariable);
				newClause.add(secondClauseVariable);
				result.add(newClause);
			}
		}
		return result;
	}

	public static String toString(List<List<Integer>> dimacs) {
		String result = 
		"c\n" + 
		"c DIMACS code generated using the Coffee framework\n" + 
		"c\n" +
		"p cnf " + HLVLParser.getElementsFromDIMACS(dimacs).size() + " " + dimacs.size() + "\n";

		for (int i = 0; i < dimacs.size(); i++) {
			List<Integer> clause = dimacs.get(i);
			String sClause = "";
			for (int j = 0; j < clause.size(); j++) {
				sClause += (j == (clause.size() - 1)) ? clause.get(j).toString() : clause.get(j) + " ";
			}
			result += (i == (dimacs.size() - 1)) ? sClause + " 0": sClause + " 0\n";

		}
		return result;
	}
	
	
}
