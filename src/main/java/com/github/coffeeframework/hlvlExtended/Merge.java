package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;

public class Merge {

	public static List<List<Integer>> merge(Model[] models, MergeMode mode) {

		List<List<Integer>> mergedDimacs = null;
		List<List<List<Integer>>> dimacs = HLVLParser.getInstance().getDIMACSs(models);

		switch (mode) {
		case UNION:
			mergedDimacs = union(dimacs);
			break;

		case INTERSECTION:
			mergedDimacs = intersection(dimacs);
			break;

		case DIFF:
			mergedDimacs = difference(dimacs);
			break;

		default:
			break;
		}
		return mergedDimacs;
	}
	
	public static String mergeToString(Model[] models, MergeMode mode) {

		List<List<Integer>> mergedDimacs = null;
		List<List<List<Integer>>> dimacs = HLVLParser.getInstance().getDIMACSs(models);

		switch (mode) {
		case UNION:
			mergedDimacs = union(dimacs);
			break;

		case INTERSECTION:
			mergedDimacs = intersection(dimacs);
			break;

		case DIFF:
			mergedDimacs = difference(dimacs);
			break;

		default:
			break;
		}
		return DIMACS.toString(mergedDimacs);
	}

	/**
	 * 
	 * @param dimacs
	 * @return
	 */
	private static List<List<Integer>> union(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi

		List<List<Integer>> mergeResult = dimacs.get(0); // φFM1
		for (int i = 1; i < dimacs.size(); i++) {

			List<List<Integer>> currentDimacs = dimacs.get(i); // φFM2
			List<Integer> currentElements = DIMACS.getElementsFromDIMACS(new ArrayList<>(currentDimacs)); // FFM2
			List<Integer> mergeResultElements = DIMACS.getElementsFromDIMACS(new ArrayList<>(mergeResult)); // FFM1

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
			exp1.forEach(element -> exp3.add(new ArrayList<>(Arrays.asList(element))));

			// exp4 = φFM2 ∧ not(FFM1 \ FFM2 )
			List<List<Integer>> exp4 = new ArrayList<>(currentDimacs);
			exp2.forEach(element -> exp4.add(new ArrayList<>(Arrays.asList(element))));

			// mergeResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
			List<List<List<Integer>>> modelDimacs = new ArrayList<>();
			modelDimacs.add(exp3);
			modelDimacs.add(exp4);
			mergeResult = DIMACS.dimacsDisjunction(modelDimacs);
		}
		return mergeResult;
	}

	private static List<List<Integer>> difference(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ ¬(φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

	private static List<List<Integer>> intersection(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

}
