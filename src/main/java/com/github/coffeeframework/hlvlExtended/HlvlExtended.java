package com.github.coffeeframework.hlvlExtended;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.coffee.generator.HLVLParser;
import com.coffee.hlvl.Model;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class HlvlExtended {

	@Inject
	private static Provider<ResourceSet> resourceSetProvider;
	@Inject
	private static IResourceValidator validator;

	private static Model[] models;

	public static void main(String[] args) throws IOException {

		String test = "		model empty\r\n" + "		elements:\r\n" + "		boolean A\r\n" + "		boolean B\r\n"
				+ "		relations:\r\n" + "		r1: common(A,B)";

//		HLVLParser.runGenerator(test);
		System.out.println(HLVLParser.runGenerator(test));

	}

	private static Model aggregate(String modelName) {

		StringBuilder aggregatedModel = new StringBuilder();

		aggregatedModel.append("model " + modelName + "\n");
		aggregatedModel.append("elements:\n");
		return null;
	}

	public static Model merge(MergeMode mode) {
		return null;
	}

	private static boolean areDisjoint() {
		return false;
	}

	public static boolean isHlvlExtended(Model model) {
//		return (model.getExtendedModels() != null && !model.getExtendedModels().isEmpty()) ? true : false;
		return false;
	}

	public static void generateModels(String[] modelsUris) {
		// Load the resources
		ResourceSet set = resourceSetProvider.get();
		models = new Model[modelsUris.length];

		for (int i = 0; i < modelsUris.length; i++) {
			Resource resource = set.getResource(URI.createFileURI(modelsUris[i]), true);
			// Validate the resource
			List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
			if (!list.isEmpty()) {
				for (Issue issue : list) {
					System.err.println(issue);
				}
			}
			// obtaining the model
			models[i] = (Model) resource.getContents().get(0);
		}
	}

	public static boolean verifyModels(String[] modelsUris) {
		generateModels(modelsUris);
		return areDisjoint();
	}

}
