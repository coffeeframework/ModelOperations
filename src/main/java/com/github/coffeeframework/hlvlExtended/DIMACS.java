package com.github.coffeeframework.hlvlExtended;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.coffee.generator.HLVLParser;

public class DIMACS {

	public static List<List<Integer>> dimacsDisjunction(List<List<List<Integer>>> modelsDimacs) {
		List<List<Integer>> result = new ArrayList<>();
		HashMap<String, String> clausesHash = new HashMap<>();
		for (int i = 0; i < modelsDimacs.size() - 1; i++) {
			List<List<Integer>> firstDimacs = modelsDimacs.get(i);
			List<List<Integer>> secondDimacs = modelsDimacs.get(i + 1);

			for (int j = 0; j < firstDimacs.size(); j++) {
				for (int j2 = 0; j2 < secondDimacs.size(); j2++) {
					List<Integer> clause = clausesDisjunction(firstDimacs.get(j), secondDimacs.get(j2));
					StringBuilder sClause = new StringBuilder();
					clause.forEach(var -> {
						sClause.append(var);
					});
					if (!clausesHash.containsKey(sClause.toString())) {
						result.add(clause);
						clausesHash.put(sClause.toString(), "");
						clausesHash.put(sClause.reverse().toString(), "");
					}
				}
			}
		}
		return result;
	}

	private static List<Integer> clausesDisjunction(List<Integer> firstClause, List<Integer> secondClause) {

		List<Integer> result = new ArrayList<>();
		result.addAll(cloneVariables(firstClause));
		result.addAll(cloneVariables(secondClause));
		return result;
	}

	public static String toString(List<List<Integer>> dimacs) {
		String result = "c\n" + "c DIMACS code generated using the Coffee framework\n" + "c\n" + "p cnf "
				+ HLVLParser.getElementsFromDIMACS(dimacs).size() + " " + dimacs.size() + "\n";

		for (int i = 0; i < dimacs.size(); i++) {
			List<Integer> clause = dimacs.get(i);
			String sClause = "";
			for (int j = 0; j < clause.size(); j++) {
				sClause += (j == (clause.size() - 1)) ? clause.get(j).toString() : clause.get(j) + " ";
			}
			result += (i == (dimacs.size() - 1)) ? sClause + " 0" : sClause + " 0\n";

			try {
				BufferedWriter bw = null;
				File file = new File("../test/myfile2.txt");
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				bw.write(sClause);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		return result;
	}

	public static List<List<Integer>> union(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi

		List<List<Integer>> mergeResult = cloneDimacs(dimacs.get(0));

		for (int i = 1; i < dimacs.size(); i++) {

			List<List<Integer>> currentDimacs = cloneDimacs(dimacs.get(i)); // φFM2

			List<Integer> currentElements = HLVLParser.getElementsFromDIMACS(cloneDimacs(currentDimacs)); // FFM2
			List<Integer> mergeResultElements = HLVLParser.getElementsFromDIMACS(cloneDimacs(mergeResult)); // FFM1

			// exp1 = not(FFM2 \ FFM1)
			List<Integer> exp1 = (cloneVariables(currentElements)).stream()
					.filter(element -> !(mergeResultElements.contains(element))).map(element -> (element *= -1))
					.collect(Collectors.toList());

			// exp2 = not(FFM1 \ FFM2)
			List<Integer> exp2 = (cloneVariables(mergeResultElements)).stream()
					.filter(element -> !(currentElements.contains(element))).map(element -> (element *= -1))
					.collect(Collectors.toList());

			// exp3 = φFM1 ∧ not(FFM2 \ FFM1 )
			List<List<Integer>> exp3 = cloneDimacs(mergeResult);
			exp1.forEach(element -> exp3.add(new ArrayList<>(Arrays.asList(element.intValue()))));

			// exp4 = φFM2 ∧ not(FFM1 \ FFM2 )
			List<List<Integer>> exp4 = cloneDimacs(currentDimacs);
			exp2.forEach(element -> exp4.add(new ArrayList<>(Arrays.asList(element.intValue()))));

			// mergeResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
			List<List<List<Integer>>> modelDimacs = new ArrayList<>();
			modelDimacs.add(exp3);
			modelDimacs.add(exp4);
			mergeResult = DIMACS.dimacsDisjunction(modelDimacs);
		}
		
		return mergeResult;
	}

	private static List<List<Integer>> cloneDimacs(List<List<Integer>> dimacsToClone) {

		List<List<Integer>> dimacsClone = new ArrayList<>();
		for (List<Integer> clause : dimacsToClone) {
			ArrayList<Integer> tempClause = new ArrayList<>();
			clause.forEach(var -> {
				tempClause.add(var.intValue());
			});

			dimacsClone.add(tempClause);
		}
		return dimacsClone;
	}

	private static List<Integer> cloneVariables(List<Integer> variablesToClone) {

		List<Integer> clauseClone = new ArrayList<>();
		for (Integer variable : variablesToClone) {

			clauseClone.add(variable.intValue());
		}
		return clauseClone;
	}
}
