package com.github.coffeeframework.hlvlExtended;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.coffee.generator.HLVLParser;

public class DIMACS {

	private static List<Integer> clausesDisjunction(List<Integer> firstClause, List<Integer> secondClause) {

//		List<List<Integer>> result = new ArrayList<>();
//		for (Integer firstClauseVariable : firstClause) {
//			for (Integer secondClauseVariable : secondClause) {
//				List<Integer> newClause = new ArrayList<>();
//				newClause.add(firstClauseVariable);
//				newClause.add(secondClauseVariable);
//				result.add(newClause);
//			}
//		}

		List<Integer> result = new ArrayList<>();
		firstClause.addAll(secondClause);
		result.addAll(firstClause);
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
}
