package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;

public class Merge {

	/**
	 * 
	 * @param dimacs
	 * @return
	 */
	public static List<List<Integer>> union(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∨ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi

		List<List<Integer>> mergeResult = dimacs.get(0); // φFM1
		for (int i = 1; i < dimacs.size(); i++) {

			List<List<Integer>> currentDimacs = dimacs.get(i); // φFM2
			List<Integer> currentElements = HLVLParser.getElementsFromDIMACS(currentDimacs); // FFM2
			List<Integer> mergeResultElements = HLVLParser.getElementsFromDIMACS(mergeResult); // FFM1

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

	public static List<List<Integer>> difference(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ ¬(φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

	public static List<List<Integer>> intersection(List<List<List<Integer>>> dimacs) {
		// φResult = (φFM1 ∧ not(FFM2 \ FFM1 )) ∧ (φFM2 ∧ not(FFM1 \ FFM2 ))
		// not({f1, f2, ..., fn}) = ? ¬fi
		return null;
	}

	public static void main(String[] args) {
		
		String fameDBA = "model  A\n" + 
				"elements: \n" + 
				"	boolean a\n" + 
				"	boolean b\n" + 
				"relations:\n" + 
				"	r0: common(a)\n" +
				"   r1: decomposition(a, [b], [1,1])";
		
		String fameDBB = "model  B\n" + 
				"elements: \n" + 
				"	boolean b\n" +
				"relations:\n" + 
				"	r0: common(b)\n";
		
		String[] modelUris = { fameDBA, fameDBB };
		try {
			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);

			System.out.println(DIMACS.toString(union(currentDimacs)));
		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}
}
