package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;

public class Merge {

	/**
	 * Generates String DIMACS format for a set of models and read them to a data
	 * structure of Lists. The structure consist of three nested lists: The outside
	 * list represents each of the DIMACS formats for the given models. The middle
	 * list represents the set of clauses from a DIMACS format for a specific model.
	 * The inner list represents the set of variables from a clause for a specific
	 * DIMACS format
	 * 
	 * @param models
	 * @return
	 */
	public static List<List<List<Integer>>> getDIMACSs(Model[] models) {

		HLVLParser parser = HLVLParser.getInstance();
		List<List<List<Integer>>> dimacs = new ArrayList<>();

		for (Model model : models) {

			String currentDimacs = parser.getDIMACS(model).toString();

			List<List<Integer>> currentClauses = new ArrayList<>();
			Arrays.asList(currentDimacs.split("\r\n")).forEach(line -> {

				List<Integer> clause = new ArrayList<>();
				if (!(line.startsWith("c") || line.startsWith("p"))) {

					Arrays.asList(line.split(" ")).forEach(variable -> {
						clause.add(new Integer(variable));
					});
					currentClauses.add(clause);

				}
			});

			dimacs.add(currentClauses);
		}
		return dimacs;
	}

	private static List<Integer> getElementsFromDIMACS(List<List<Integer>> dimacs) {

		List<Integer> elements = new ArrayList<>();
		dimacs.forEach(clause -> {
			elements.addAll(clause);
		});
		return elements.stream().map(variable -> Math.abs(variable)).distinct().collect(Collectors.toList());
	}

	/**
	 * 
	 * @param dimacs
	 * @return
	 */
	public static String union(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi

		List<List<Integer>> mergeResult = dimacs.get(0); // φFM1
		for (int i = 1; i < dimacs.size(); i++) {

			List<List<Integer>> currentDimacs = dimacs.get(i); // φFM2
			List<Integer> currentElements = getElementsFromDIMACS(currentDimacs); // FFM2
			List<Integer> mergeResultElements = getElementsFromDIMACS(mergeResult); // FFM1

			// exp1 = not(FFM2 \ FFM1)
			List<Integer> exp1 = (new ArrayList<>(currentElements)).stream()
					.filter(element -> !(mergeResultElements.contains(element))).map(element -> (element *= -1))
					.collect(Collectors.toList());

			// exp2 = not(FFM1 \ FFM2)
			List<Integer> exp2 = (new ArrayList<>(mergeResultElements)).stream()
					.filter(element -> !(currentElements.contains(element))).map(element -> (element *= -1))
					.collect(Collectors.toList());

			// exp3 = φFM1 ∧ not(FFM2 \ FFM1 )
			List<List<Integer>> exp3 = new ArrayList<>(mergeResult);
			exp1.forEach(element -> exp3.add(new ArrayList<Integer>(element)));

			// exp4 = φFM2 ∧ not(FFM1 \ FFM2 )
			List<List<Integer>> exp4 = new ArrayList<>(currentDimacs);
			exp2.forEach(element -> exp4.add(new ArrayList<Integer>(element)));

			// mergeResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))

		}
		return null;
	}

	public static String difference(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ ¬(φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

	public static String intersection(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

	private List<List<Integer>> dimacsDisjunction(List<List<List<Integer>>> modelsDimacs) {

		List<List<Integer>> result = new ArrayList<>();

		for (List<List<Integer>> dimacs : modelsDimacs) {
			for (List<Integer> clause : dimacs) {
				for (List<Integer> clauseResult : result) {
					result.addAll(clausesDisjunction(clauseResult, clause));
				}
			}
		}
		return result;
	}

	private List<List<Integer>> clausesDisjunction(List<Integer> firstClause, List<Integer> secondClause) {

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
}
