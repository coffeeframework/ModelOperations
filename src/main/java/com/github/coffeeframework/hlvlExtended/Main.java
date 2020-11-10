package com.github.coffeeframework.hlvlExtended;

import java.io.IOException;

import com.coffee.hlvl.Model;

public class Main {

	public static void main(String[] args) throws IOException {

		String test = "		model empty\r\n" + "		elements:\r\n" + "		boolean A\r\n" + "		boolean B\r\n"
				+ "		relations:\r\n" + "		r1: common(A,B)";

		String procesador = "model Procesador\n" + 
				"  elements:\n" + 
				"   boolean procesador\n" + 
				"   boolean frecuencia\n" + 
				"   boolean ONEGHz\n" + 
				"   boolean TWOGHz\n" + 
				"   boolean FOURGHz\n" + 
				"   boolean nucleos\n" + 
				"   boolean N2\n" + 
				"   boolean N3\n" + 
				"   boolean N4\n" + 
				"   boolean hilos\n" + 
				"   boolean H2\n" + 
				"   boolean H3\n" + 
				"  relations:\n" + 
				"   com1: common(procesador)\n" + 
				"   grp1: group(procesador, [frecuencia, nucleos, hilos],[3,3])\n" + 
				"   grp2: group(frecuencia, [ONEGHz, TWOGHz, FOURGHz],[1,1])\n" + 
				"   dec2: group(hilos, [H2, H3],[1,1])\n" + 
				"   grp3: group(nucleos, [N2, N3, N4],[1,*])";
		
		String os = "model SistemaOperativo\n" + 
				"   elements:\n" + 
				"   boolean sistemaOperativo\n" + 
				"   boolean Windows\n" + 
				"   boolean Linux\n" + 
				"   boolean MacOS\n" + 
				"   relations:\n" + 
				"   com1: common(sistemaOperativo)\n" + 
				"   grp1: group(sistemaOperativo, [Windows, Linux, MacOS],[1,1])";
		
		String pc = "model PC extends \"SistemaOperativo.hlvl\",\"Procesador.hlvl\"\n" + 
				"	elements:\n" + 
				"		boolean pc\n" + 
				"		boolean tarjetaGrafica\n" + 
				"		boolean msi\n" + 
				"		boolean asus\n" + 
				"		boolean evga\n" + 
				"	relations:\n" + 
				"	com1: common(pc)\n" + 
				"	mut1: mutex(Procesador.FOURGHz,evga)\n" + 
				"	imp1: implies(Procesador.N4,msi)";
//		HLVLParser.runGenerator(test);
//		System.out.println(HLVLParser.runGenerator(test));
		
		String[] modelsUris = {procesador,os, pc};

		try {
			Model[] models = ModelOperations.generateModels(modelsUris);
//			Model model = ModelOperations.aggregate(models, "TestAggregation");
			
			Model mergedModel = ModelOperations.insert(models[2], models[1], "evga", "sistemaOperativo", HLVLExtendedKeys.MANDATORY_DECOMPOSITION, 0);
			System.out.println(mergedModel.toString());
			int i = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
