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

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------

	public static List<List<Integer>> merge(Model[] models, MergeMode mode) {
		return Merge.merge(models, mode);
	}
	
	public static String mergeToString(Model[] models, MergeMode mode) {
		return Merge.mergeToString(models, mode);
	}
	

	public static String aggregateToString(Model[] models, String modelName) throws Exception {
		return Aggregation.aggregateToString(models, modelName);
	}
	
	public static Model aggregate(Model[] models, String modelName) throws Exception {
		return Aggregation.aggregate(models, modelName);
	}

}
