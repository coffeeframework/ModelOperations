package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Common;
import com.coffee.hlvl.ComplexImplies;
import com.coffee.hlvl.ComplexMutex;
import com.coffee.hlvl.Constraint;
import com.coffee.hlvl.Decomposition;
import com.coffee.hlvl.ElmDeclaration;
import com.coffee.hlvl.Group;
import com.coffee.hlvl.Model;
import com.coffee.hlvl.Pair;
import com.coffee.hlvl.RelDeclaration;
import com.coffee.hlvl.Relation;
import com.coffee.hlvl.VarList;
import com.coffee.hlvl.Visibility;
import com.coffee.hlvl.impl.CommonImpl;
import com.github.coffeeframework.basickhlvlpackage.GroupType;
import com.github.coffeeframework.basickhlvlpackage.HlvlBasicFactory;
import com.github.coffeeframework.basickhlvlpackage.HlvlBasicKeys;

public class HlvlExtended {

	/**
	 * 
	 * @param models
	 * @param modelName
	 * @return
	 * @throws Exception
	 */
	public static Model aggregate(Model[] models, String modelName) throws Exception {

		// de alguna forma verificar si todos los modelos extendidos, si es el caso, se
		// han dado como
		// parámetro, si no tal vez tirar una excepción que pida los demás modelos
		// requeridos
		HlvlBasicFactory hlvlFactory = new HlvlBasicFactory();

		StringBuilder aggregatedModel = new StringBuilder();

		// Adds model and elements block declarations
		aggregatedModel.append(hlvlFactory.getHeader(modelName));

		// Adds new element that is the root of the aggregated model
		aggregatedModel.append("    " + hlvlFactory.getElement(modelName));

		// Adds the elements from the input models
		for (Model model : models) {
			for (ElmDeclaration element : model.getElements()) {
				aggregatedModel.append("    " + hlvlFactory.getElement(element.getName()));
			}
		}

		addRelations(aggregatedModel, modelName, models);

		System.out.println(aggregatedModel.toString());
		return HLVLParser.generateModel(aggregatedModel.toString());
	}

	private static void addRelations(StringBuilder aggregatedModel, String modelName, Model[] models) {
		HlvlExtendedFactory hlvlFactory = new HlvlExtendedFactory();

		// Adds relations block declaration
		aggregatedModel.append("  " + hlvlFactory.getRelationsLab());

		// Add the relations from the input models
		List<String> commonIds = new ArrayList<>(); // Used to collect the identifiers from common relations from the
													// input models
		commonIds.add(modelName);// New root
		for (Model model : models) {

			for (RelDeclaration relation : model.getRelations()) {
				aggregatedModel.append(getRelation(relation.getExp(), commonIds, hlvlFactory));
			}
		}
		aggregatedModel.append("    " + hlvlFactory.getCommonList(commonIds));
	}

	private static String getRelation(Relation relation, List<String> commonIds, HlvlExtendedFactory hlvlFactory) {
		String relationString = "    ";

		if (relation instanceof Common) {
			
			EList<ElmDeclaration> elements = ((CommonImpl) relation).getElements().getValues();
			elements.forEach((element) -> commonIds.add(element.getName()));
			relationString = "";
			
		} else if (relation instanceof Pair) {
			
			Pair pair = (Pair) relation;
			String var1 = pair.getVar1().getName();
			String var2 = pair.getVar2().getName();

			if (pair.getOperator().equals(HlvlBasicKeys.IMPLIES)) {
				relationString = hlvlFactory.getImplies(var1, var2);
			} else if (pair.getOperator().equals(HlvlBasicKeys.MUTEX)) {
				relationString += hlvlFactory.getMutex(var1, var2);
			}
			
		} else if (relation instanceof VarList) {
			
			VarList varList = (VarList) relation;
			String var1 = varList.getVar1().getName();
			List<String> list = new ArrayList<>();

			varList.getList().getValues().forEach((element) -> list.add(element.getName()));

			if (varList.getOperator().equals(HlvlBasicKeys.IMPLIES)) {
				relationString = hlvlFactory.getImpliesList(var1, list);
			} else if (varList.getOperator().equals(HlvlBasicKeys.MUTEX)) {
				relationString += hlvlFactory.getMutexList(var1, list);
			}

		} else if (relation instanceof Decomposition) {
			
			Decomposition decomposition = (Decomposition) relation;
			String min = decomposition.getMin() + "";
			String max = decomposition.getMax() + "";
			String parent = decomposition.getParent().getName();
			List<String> children = new ArrayList<>();

			decomposition.getChildren().getValues().forEach((element) -> children.add(element.getName()));

			relationString += hlvlFactory.getDecompositionList(parent, children, min, max);
			
		} else if (relation instanceof Group) {
			
			Group group = (Group) relation;
			String min = group.getMin() + "";
			String max = group.getMax().getValue();
			String parent = group.getParent().getName();
			List<String> children = new ArrayList<>();

			group.getChildren().getValues().forEach((element) -> children.add(element.getName()));

			relationString += hlvlFactory.getGroup(parent, children, min, max);
		}
		// TODO Estos casos por ahora están fuera del alcance del proyecto de grado	
//		} else if (relation instanceof Constraint) {
//
//			
//			
//		} else if (relation instanceof Visibility) {
//
//			
//			
//		} else if (relation instanceof ComplexImplies) {
//
//			
//			
//		} else if (relation instanceof ComplexMutex) {
//
//		}

		return relationString;
	}

	public static Model merge(MergeMode mode) {
		return null;
	}

	private boolean areDisjoint(Model[] models) {
		return false;
	}

	public static boolean isHlvlExtended(Model model) {
//		return (model.getExtendedModels() != null && !model.getExtendedModels().isEmpty()) ? true : false;
		return false;
	}

	public static Model[] generateModels(String[] modelsUris) throws Exception {

		Model[] models = new Model[modelsUris.length];
		for (int i = 0; i < modelsUris.length; i++) {
			models[i] = HLVLParser.generateModel(modelsUris[i]);
		}
		return models;
	}
}
