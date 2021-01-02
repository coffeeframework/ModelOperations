package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.List;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;

/**
 * 
 * @author Sara Ortiz Drada
 * @author Juan Diego Carvajal Castaño
 * 
 *         Version Control:
 * 
 *         September 17th 2020 - Documentation first version
 * 
 *         ModelOpertions class provides services to compose HLVL Variability
 *         models
 */
public class ModelOperations {

	/**
	 * 
	 * @param inputModels
	 * @param missingModels
	 * @return
	 */
	private static List<String> getMissingModels(List<String> inputModels, List<String> missingModels) {
		missingModels.removeAll(inputModels);
		return missingModels;
	}

	/**
	 * 
	 * @return list of extended models that the user didn't upload
	 */
	private static List<String> verifyInheritance(Model[] models) {

		List<String> missingModels = new ArrayList<>();
		List<String> inputModels = new ArrayList<String>();

		for (int i = 0; i < models.length; i++) {
			inputModels.add(models[i].getName());

			if (models[i].getExtendedModels() != null && !models[i].getExtendedModels().getIds().isEmpty()) {
				models[i].getExtendedModels().getIds().forEach(id -> missingModels.add(id.getImportURI()));
				getMissingModels(inputModels, missingModels);
			}
		}
		return null;
	}

	/**
	 * Creates model objects from an array of HLVL variability models represented as
	 * strings.
	 * 
	 * @param modelsUris: array of HLVL variability models represented as strings.
	 * 
	 * @return Returns an array of model objects that represents the string models
	 *         given as input.
	 * @throws Exception if the content of the any of the models has syntactic
	 *                   errors.
	 */
	public static Model[] generateModels(String[] modelsUris) throws Exception {

		Model[] models = new Model[modelsUris.length];
		for (int i = 0; i < modelsUris.length; i++) {
			models[i] = HLVLParser.getInstance().generateModel(modelsUris[i]);
		}
		return models;
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------

	public static String merge(Model[] models, MergeMode mode) {

		String mergedDimacs = "";
		List<List<List<Integer>>> dimacs = Merge.getDIMACSs(models);

		switch (mode) {
		case UNION:
			mergedDimacs = Merge.union(dimacs);
			break;

		case INTERSECTION:
			mergedDimacs = Merge.intersection(dimacs);
			break;

		case DIFF:
			mergedDimacs = Merge.difference(dimacs);
			break;

		default:
			break;
		}
		return null;
	}

}
