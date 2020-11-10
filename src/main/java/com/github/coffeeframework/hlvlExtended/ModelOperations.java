package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.codegen.ecore.templates.model.FactoryClass;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EcoreFactory;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Common;
import com.coffee.hlvl.Decomposition;
import com.coffee.hlvl.ElmDeclaration;
import com.coffee.hlvl.Group;
import com.coffee.hlvl.HlvlFactory;
import com.coffee.hlvl.ListOfIDs;
import com.coffee.hlvl.Model;
import com.coffee.hlvl.Pair;
import com.coffee.hlvl.Range;
import com.coffee.hlvl.RelDeclaration;
import com.coffee.hlvl.Relation;
import com.coffee.hlvl.VarList;
import com.coffee.hlvl.impl.CommonImpl;
import com.github.coffeeframework.basickhlvlpackage.HlvlBasicKeys;
import com.ibm.icu.impl.UResource.Array;

public class ModelOperations {

	public static Model insert(Model baseModel, Model aspectModel, String baseElementName, String aspectElementName,
			HLVLExtendedKeys operator, int index) {

		ElmDeclaration baseElement = findElement(baseModel, baseElementName);
		ElmDeclaration aspectElement = findElement(aspectModel, aspectElementName);
		List<RelDeclaration> aspectElementRelations = new ArrayList<>();
		List<ElmDeclaration> aspectElementDeclaration = new ArrayList<>();
		getAspectComponents(aspectModel, aspectElement, aspectElementRelations, aspectElementDeclaration);
		
		baseModel.getElements().addAll(aspectElementDeclaration);
		baseModel.getRelations().addAll(aspectElementRelations);
		baseModel.getRelations().add(createJoinPoint(baseElement, aspectElement, operator, index));

		return baseModel;
	}

	private static RelDeclaration createJoinPoint(ElmDeclaration baseElement, ElmDeclaration aspectElement,
			HLVLExtendedKeys operator, int index) {
		RelDeclaration joinPoint = HlvlFactory.eINSTANCE.createRelDeclaration();
		joinPoint.setName("jp" + index);

		Relation relation = null;

		switch (operator) {
		case MANDATORY_DECOMPOSITION:

			Decomposition decomposition = HlvlFactory.eINSTANCE.createDecomposition();
			decomposition.setParent(baseElement);

			ListOfIDs listOfIDs = HlvlFactory.eINSTANCE.createListOfIDs();
			listOfIDs.getValues().add(aspectElement);
			decomposition.setChildren(listOfIDs);

			decomposition.setMin(1);
			decomposition.setMax(1);

			relation = decomposition;

			break;

		case OPTIONAL_DECOMPOSITION:

			decomposition = HlvlFactory.eINSTANCE.createDecomposition();
			decomposition.setParent(baseElement);

			listOfIDs = HlvlFactory.eINSTANCE.createListOfIDs();
			listOfIDs.getValues().add(aspectElement);
			decomposition.setChildren(listOfIDs);

			decomposition.setMin(0);
			decomposition.setMax(1);

			relation = decomposition;

			break;

		case OR_GROUP:

			Group group = HlvlFactory.eINSTANCE.createGroup();
			group.setParent(baseElement);

			listOfIDs = HlvlFactory.eINSTANCE.createListOfIDs();
			listOfIDs.getValues().add(aspectElement);
			group.setChildren(listOfIDs);

			Range max = HlvlFactory.eINSTANCE.createRange();
			max.setValue("*");
			group.setMin(1);
			group.setMax(max);

			relation = group;

			break;

		case XOR_GROUP:

			group = HlvlFactory.eINSTANCE.createGroup();
			group.setParent(baseElement);

			listOfIDs = HlvlFactory.eINSTANCE.createListOfIDs();
			listOfIDs.getValues().add(aspectElement);
			group.setChildren(listOfIDs);

			max = HlvlFactory.eINSTANCE.createRange();
			max.setValue("1");
			group.setMin(0);
			group.setMax(max);

			relation = group;

			break;

		default:
			break;
		}

		joinPoint.setExp(relation);

		return joinPoint;
	}

	private static List<ElmDeclaration> getElements(List<RelDeclaration> aspectElementRelations) {

		List<ElmDeclaration> elements = new ArrayList<>();

		for (RelDeclaration relation : aspectElementRelations) {
			if (relation.getExp() instanceof Common) {

				elements.addAll(((CommonImpl) relation.getExp()).getElements().getValues());

			} else if (relation instanceof Pair) {

				Pair pair = (Pair) relation.getExp();
				elements.add(pair.getVar1());
				elements.add(pair.getVar2());

			} else if (relation instanceof VarList) {

				VarList varList = (VarList) relation.getExp();
				elements.add(varList.getVar1());
				elements.addAll(varList.getList().getValues());

			} else if (relation instanceof Decomposition) {

				Decomposition decomposition = (Decomposition) relation.getExp();
				elements.add(decomposition.getParent());
				elements.addAll(decomposition.getChildren().getValues());

			} else if (relation instanceof Group) {

				Group group = (Group) relation.getExp();
				elements.add(group.getParent());
				elements.addAll(group.getChildren().getValues());

			}
		}
		return elements;
	}

	/**
	 * Permite extraer del parámetro model: 
	 * i. la cadena de relaciones asociadas al parámetro element,
	 * es decir, encuentra toda la cadena de elementos relacionados y retorna a
	 * través del parámetro relatedRelations todas las relaciones asociadas a dichos
	 * elementos.
	 * 
	 * ii. la cadena de elementos asociados al parámetro element y la retorna a
	 * través del parámetro relatedElements.
	 * 
	 * @param model:            modelo del cual se extraerán las relaciones.
	 * @param element:          elemento inicial para verificar las asociaciones.
	 * @param relatedRelations: lista usada para almacenar la cadena de relaciones
	 *                          asociadas al parámetro element.
	 * @param relatedElements:  lista usada para almacenar la cadena de elementos
	 *                          asociados al parámetro element.
	 */
	//Los nombres de los parámetros difieren en el llamado y el metodo
	private static void getAspectComponents(Model model, ElmDeclaration element, List<RelDeclaration> relatedRelations,
			List<ElmDeclaration> relatedElements) {

		List<RelDeclaration> modelRelations = new ArrayList<>();
		Collections.copy(modelRelations, model.getRelations());

		relatedElements.add(element);

		for (RelDeclaration relation : modelRelations) {
			if (relation.getExp() instanceof Common) {

				EList<ElmDeclaration> elements = ((CommonImpl) relation.getExp()).getElements().getValues();
				if (elements.stream().anyMatch(e -> relatedElements.contains(e))) {
					relatedElements.addAll(elements);
					relatedRelations.add(relation);
					modelRelations.remove(relation);
				}

			} else if (relation.getExp() instanceof Pair) {

				Pair pair = (Pair) relation.getExp();
				ElmDeclaration var1 = pair.getVar1();
				ElmDeclaration var2 = pair.getVar2();

				if (var1.getName().equals(element.getName()) || var2.getName().equals(element.getName())) {
					relatedRelations.add(relation);
				}

			} else if (relation.getExp() instanceof VarList) {

				VarList varList = (VarList) relation.getExp();
				ElmDeclaration var1 = varList.getVar1();

				if (var1.getName().equals(element.getName()) || varList.getList().getValues().contains(element)) {
					relatedRelations.add(relation);
				}

			} else if (relation.getExp() instanceof Decomposition) {

				Decomposition decomposition = (Decomposition) relation.getExp();
				ElmDeclaration parent = decomposition.getParent();

				if (parent.getName().equals(element.getName())
						|| decomposition.getChildren().getValues().contains(element)) {
					relatedRelations.add(relation);
				}

			} else if (relation.getExp() instanceof Group) {

				Group group = (Group) relation.getExp();
				ElmDeclaration parent = group.getParent();

				if (parent.getName().equals(element.getName()) || group.getChildren().getValues().contains(element)) {
					relatedRelations.add(relation);
				}
			}
		}
	}

	private static ElmDeclaration findElement(Model model, String elementName) {

		ElmDeclaration baseFeature = null;
		boolean found = false;
		for (RelDeclaration relation : model.getRelations()) {
			if (relation.getExp() instanceof Common) {

				EList<ElmDeclaration> elements = ((CommonImpl) relation.getExp()).getElements().getValues();
				for (int i = 0; i < elements.size() && !found; i++) {
					if (elements.get(i).getName().equals(elementName)) {
						baseFeature = elements.get(i);
						break;
					}
				}
			} else if (relation.getExp() instanceof Pair) {

				Pair pair = (Pair) relation.getExp();
				ElmDeclaration var1 = pair.getVar1();
				ElmDeclaration var2 = pair.getVar2();

				if (var1.getName().equals(elementName)) {
					baseFeature = var1;
				} else if (var2.getName().equals(elementName)) {
					baseFeature = var1;
				}
				break;

			} else if (relation.getExp() instanceof VarList) {

				VarList varList = (VarList) relation.getExp();
				ElmDeclaration var1 = varList.getVar1();

				if (var1.getName().equals(elementName)) {
					baseFeature = var1;
					break;
				}

				for (ElmDeclaration element : varList.getList().getValues()) {
					if (element.getName().equals(elementName)) {
						baseFeature = element;
						break;
					}
				}

			} else if (relation.getExp() instanceof Decomposition) {

				Decomposition decomposition = (Decomposition) relation.getExp();
				ElmDeclaration parent = decomposition.getParent();

				if (parent.getName().equals(elementName)) {
					baseFeature = parent;
					break;
				}

				for (ElmDeclaration element : decomposition.getChildren().getValues()) {
					if (element.getName().equals(elementName)) {
						baseFeature = element;
						break;
					}
				}

			} else if (relation.getExp() instanceof Group) {

				Group group = (Group) relation.getExp();
				ElmDeclaration parent = group.getParent();

				if (parent.getName().equals(elementName)) {
					baseFeature = parent;
					break;
				}

				for (ElmDeclaration element : group.getChildren().getValues()) {
					if (element.getName().equals(elementName)) {
						baseFeature = element;
						break;
					}
				}
			}
		}
		return baseFeature;
	}

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

		StringBuilder aggregatedModel = new StringBuilder();

		addElements(aggregatedModel, modelName, models);
		addRelations(aggregatedModel, modelName, models);

		System.out.println(aggregatedModel.toString());

		return HLVLParser.getInstance().generateModel(aggregatedModel.toString());
	}

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

	public static Model merge(MergeMode mode) {
		return null;
	}

	private boolean areDisjoint(Model[] models) {
		return false;
	}

	private static List<String> getMissingModels(List<String> inputModels, List<String> missingModels) {
		missingModels.removeAll(inputModels);
		return missingModels;
	}

	public static Model[] generateModels(String[] modelsUris) throws Exception {

		Model[] models = new Model[modelsUris.length];
		for (int i = 0; i < modelsUris.length; i++) {
			models[i] = HLVLParser.getInstance().generateModel(modelsUris[i]);
		}
		return models;
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
}
