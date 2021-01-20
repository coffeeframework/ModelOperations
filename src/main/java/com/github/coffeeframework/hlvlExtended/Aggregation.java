package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Common;
import com.coffee.hlvl.Decomposition;
import com.coffee.hlvl.ElmDeclaration;
import com.coffee.hlvl.Group;
import com.coffee.hlvl.Model;
import com.coffee.hlvl.Pair;
import com.coffee.hlvl.RelDeclaration;
import com.coffee.hlvl.Relation;
import com.coffee.hlvl.VarList;
import com.coffee.hlvl.impl.CommonImpl;
import com.github.coffeeframework.basickhlvlpackage.HlvlBasicKeys;

public class Aggregation {

	/**
	 * Compose an array of HLVL variability models performing an aggregation
	 * operation. A new variability model is created where a new root element has a
	 * parent-child relationship with the roots from the input models.
	 * 
	 * @param models:    the models to be composed.
	 * @param modelName: a name for the aggregated model, this name will be used to
	 *                   name the root element as well.
	 * @return Returns the aggregated model.
	 * @throws Exception if there are models needed but not given due to mechanisms
	 *                   as inheritance or interfaces specified in the models.
	 */
	public static Model aggregate(Model[] models, String modelName) throws Exception {

		// de alguna forma verificar si todos los modelos extendidos, si es el caso, se
		// han dado como
		// parámetro, si no tal vez tirar una excepción que pida los demás modelos
		// requeridos

		StringBuilder aggregatedModel = new StringBuilder();

		addElements(aggregatedModel, modelName, models);
		addRelations(aggregatedModel, modelName, models);

		// System.out.println(aggregatedModel.toString());
		return HLVLParser.getInstance().generateModel(aggregatedModel.toString());
	}

	public static String aggregateToString(Model[] models, String modelName) {

		StringBuilder aggregatedModel = new StringBuilder();
		addElements(aggregatedModel, modelName, models);
		addRelations(aggregatedModel, modelName, models);

		return aggregatedModel.toString();
	}

	/**
	 * Adds the elements from an array of models to a variability model given as
	 * argument in the form of an string.
	 * 
	 * @param aggregatedModel: the variability model where the elements will be
	 *                         added.
	 * @param modelName:       the name of the aggregated model. It will be used to
	 *                         add the new root for the aggregated model as it is
	 *                         specified for the aggregation operation.
	 * @param models:          the models that are being aggregated.
	 */
	private static void addElements(StringBuilder aggregatedModel, String modelName, Model[] models) {

		HLVLExtendedFactory hlvlFactory = new HLVLExtendedFactory();
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
	}

	/**
	 * Adds the relations from an array of models to a variability model given as
	 * argument in the form of an string.
	 * 
	 * @param aggregatedModel: the variability model where the elements will be
	 *                         added.
	 * @param modelName:       the name of the aggregated model. It will be used to
	 *                         add the relation between the new root for the
	 *                         aggregated and the roots from the input models.
	 * @param models:          the models that are being aggregated.
	 */
	private static void addRelations(StringBuilder aggregatedModel, String modelName, Model[] models) {
		HLVLExtendedFactory hlvlFactory = new HLVLExtendedFactory();

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

	private static String getRelation(Relation relation, List<String> commonIds, HLVLExtendedFactory hlvlFactory) {
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
				relationString += hlvlFactory.getImplies(var1, var2);
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

}
