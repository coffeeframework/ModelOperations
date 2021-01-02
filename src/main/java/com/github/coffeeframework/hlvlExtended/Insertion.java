package com.github.coffeeframework.hlvlExtended;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

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

public class Insertion {

	/**
	 * Allows to insert a chain of elements from a model (aspect model) below a
	 * certain element of another model (base model), through an specified operator.
	 * 
	 * @param baseModel:         Model in which the elements will be inserted.
	 * @param aspectModel:       Model from which a set of elements are going to be
	 *                           inserted.
	 * @param baseElementName:   Element from the base model where the elements will
	 *                           be inserted.
	 * @param aspectElementName: First element from the chain of element that will
	 *                           be inserted in the base model.
	 * @param operator:          Specifies the variability relation to be used to
	 *                           relate the aspectElement and the baseElement.
	 * @param index:             Number used to create an identifier for the
	 *                           relation between aspectElement and the baseElement.
	 * 
	 * @return Returns the model baseModel where the chain of elements from
	 *         aspectModel where inserted
	 */
	public static Model insert(Model baseModel, Model aspectModel, String baseElementName, String aspectElementName,
			HLVLExtendedKeys operator, int index) {

		ElmDeclaration baseElement = findElement(baseModel, baseElementName);
		ElmDeclaration aspectElement = findElement(aspectModel, aspectElementName);
		List<RelDeclaration> aspectRelatedRelations = new ArrayList<>();
		List<ElmDeclaration> aspectRelatedElements = new ArrayList<>();
		getAspectComponents(aspectModel, aspectElement, aspectRelatedRelations, aspectRelatedElements);

		baseModel.getElements().addAll(aspectRelatedElements);
		baseModel.getRelations().addAll(aspectRelatedRelations);
		baseModel.getRelations().add(createJoinPoint(baseElement, aspectElement, operator, index));

		return baseModel;
	}

	/**
	 * Creates a Variability Relation between a base and aspect elements through an
	 * specified operator.
	 * 
	 * @param baseElementName:   Element from the base model.
	 * @param aspectElementName: Element from the aspect model.
	 * @param operator:          Specifies the variability relation to be used to
	 *                           relate the aspectElement and the baseElement.
	 * @param index:             Number used to create an identifier for the
	 *                           relation between aspectElement and baseElement.
	 * @return Returns a RelDeclaration object that represent the relation between
	 *         aspectElement and baseElement through the specified operator.
	 */
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

	/**
	 * Permite extraer del parámetro model:
	 * 
	 * i. la cadena de relaciones asociadas al parámetro element, es decir,
	 * encuentra toda la cadena de elementos relacionados y retorna a través del
	 * parámetro relatedRelations todas las relaciones asociadas a dichos elementos.
	 * 
	 * ii. la cadena de elementos asociados al parámetro element y la retorna a
	 * través del parámetro relatedElements.
	 * 
	 * @param model:                  modelo del cual se extraerán las relaciones.
	 * @param element:                elemento inicial para verificar las
	 *                                asociaciones.
	 * @param aspectRelatedRelations: lista usada para almacenar la cadena de
	 *                                relaciones asociadas al parámetro element.
	 * @param aspectRelatedElements:  lista usada para almacenar la cadena de
	 *                                elementos asociados al parámetro element.
	 */
	private static void getAspectComponents(Model aspectModel, ElmDeclaration aspectElement,
			List<RelDeclaration> aspectRelatedRelations, List<ElmDeclaration> aspectRelatedElements) {

		List<RelDeclaration> modelRelations = new ArrayList<>();
		modelRelations.addAll(aspectModel.getRelations());
		aspectRelatedElements.add(aspectElement);

		for (RelDeclaration relation : modelRelations) {
			if (relation.getExp() instanceof Common) {

				EList<ElmDeclaration> elements = ((CommonImpl) relation.getExp()).getElements().getValues();

				if (elements.stream().anyMatch(e -> aspectRelatedElements.contains(e))) {

					elements.forEach(e -> {
						if (!e.getName().equals(aspectElement.getName())) {
							aspectRelatedElements.add(e);
						}
					});
					relation = null;
				}

			} else if (relation.getExp() instanceof Pair) {

				Pair pair = (Pair) relation.getExp();
				ElmDeclaration var1 = pair.getVar1();
				ElmDeclaration var2 = pair.getVar2();

				if (aspectRelatedElements.contains(var1) || aspectRelatedElements.contains(var2)) {
					aspectRelatedElements.add(var1);
					aspectRelatedElements.add(var2);
					aspectRelatedRelations.add(relation);
					relation = null;
				}

			} else if (relation.getExp() instanceof VarList) {

				VarList varList = (VarList) relation.getExp();
				ElmDeclaration var1 = varList.getVar1();
				List<ElmDeclaration> list = varList.getList().getValues();

				if (aspectRelatedElements.contains(var1)
						|| list.stream().anyMatch(e -> aspectRelatedElements.contains(e))) {
					aspectRelatedElements.add(var1);
					aspectRelatedElements.addAll(list);
					aspectRelatedRelations.add(relation);
					relation = null;
				}

			} else if (relation.getExp() instanceof Decomposition) {

				Decomposition decomposition = (Decomposition) relation.getExp();
				ElmDeclaration parent = decomposition.getParent();
				List<ElmDeclaration> children = decomposition.getChildren().getValues();

				if (aspectRelatedElements.contains(parent)
						|| children.stream().anyMatch(e -> aspectRelatedElements.contains(e))) {
					aspectRelatedElements.add(parent);
					aspectRelatedElements.addAll(children);
					aspectRelatedRelations.add(relation);
					relation = null;
				}

			} else if (relation.getExp() instanceof Group) {

				Group group = (Group) relation.getExp();
				ElmDeclaration parent = group.getParent();
				List<ElmDeclaration> children = group.getChildren().getValues();

				if (aspectRelatedElements.contains(parent)
						|| children.stream().anyMatch(e -> aspectRelatedElements.contains(e))) {
					aspectRelatedElements.add(parent);
					aspectRelatedElements.addAll(children);
					aspectRelatedRelations.add(relation);
					relation = null;
				}
			}
		}
	}

	private static ElmDeclaration findElement(Model model, String elementName) {
		return model.getElements().stream().filter(element -> element.getName().equals(elementName)).findFirst().get();
	}

}
