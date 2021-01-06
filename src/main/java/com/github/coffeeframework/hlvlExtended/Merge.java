package com.github.coffeeframework.hlvlExtended;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

		String fameDBA = "model  GPLA\n" + "elements: \n" + "	boolean WithEdges\n" + "	boolean WithNeighbors\n"
				+ "	boolean OnlyVertices\n" + "	boolean Implementation\n" + "	boolean Undirected\n"
				+ "	boolean Directed\n" + "	boolean Gtp\n" + "	boolean UndirectedOnlyVertices\n"
				+ "	boolean UndirectedWithNeighbors\n" + "	boolean UndirectedWithEdges\n"
				+ "	boolean DirectedOnlyVertices\n" + "	boolean DirectedWithNeighbors\n"
				+ "	boolean DirectedWithEdges\n" + "	boolean HiddenGtp\n" + "	boolean WeightedOnlyVertices\n"
				+ "	boolean WeightedWithNeighbors\n" + "	boolean WeightOptions\n" + "	boolean HiddenWgt\n"
				+ "	boolean DFS\n" + "	boolean Src\n" + "	boolean MSTKruskal\n" + "	boolean MSTPrim\n"
				+ "	boolean Cycle\n" + "	boolean StronglyConnected\n" + "	boolean Transpose\n"
				+ "	boolean StrongC\n" + "	boolean Alg\n" + "	boolean TestProg\n" + "	boolean MainGpl\n"
				+ "	boolean GPL\n" + "relations:\n" + "	r0: common(GPL)\n" + "	r1:decomposition(GPL,[MainGpl],[1,1])\n"
				+ "	r2:decomposition(MainGpl,[TestProg],[1,1])\n" + "	r3:decomposition(MainGpl,[Alg],[1,1])\n"
				+ "	r4:decomposition(StrongC,[Transpose],[1,1])\n"
				+ "	r5:decomposition(StrongC,[StronglyConnected],[1,1])\n" + "	r6:decomposition(Alg,[Cycle],[0,1])\n"
				+ "	r7:decomposition(Alg,[MSTPrim],[0,1])\n" + "	r8:decomposition(Alg,[MSTKruskal],[0,1])\n"
				+ "	r9:decomposition(Src,[DFS],[1,1])\n" + "	r10:decomposition(MainGpl,[HiddenWgt],[1,1])\n"
				+ "	r11:decomposition(HiddenWgt,[WeightOptions],[1,1])\n"
				+ "	r12:decomposition(WeightOptions,[WeightedWithNeighbors],[0,1])\n"
				+ "	r13:decomposition(WeightOptions,[WeightedOnlyVertices],[0,1])\n"
				+ "	r14:group(HiddenGtp,[DirectedWithEdges, DirectedWithNeighbors, DirectedOnlyVertices, UndirectedWithEdges, UndirectedWithNeighbors, UndirectedOnlyVertices],[1,1])\n"
				+ "	r15:decomposition(MainGpl,[HiddenGtp],[1,1])\n" + "	r16:group(Gtp,[Directed, Undirected],[1,1])\n"
				+ "	r17:decomposition(MainGpl,[Gtp],[1,1])\n"
				+ "	r18:group(Implementation,[OnlyVertices, WithNeighbors, WithEdges],[1,1])\n"
				+ "	r19:decomposition(MainGpl,[Implementation],[1,1])\n"
				+ "	r20:expression(((StrongC) => ((Directed) AND (DFS))))\n"
				+ "	r21:expression(((Cycle) => ((Gtp) AND (DFS))))\n"
				+ "	r22:expression((((MSTKruskal) OR (MSTPrim)) => (~ ((MSTKruskal) AND (MSTPrim)))))\n"
				+ "	r23:expression((((OnlyVertices) AND (Directed)) => (DirectedOnlyVertices)))\n"
				+ "	r24:expression((((WithNeighbors) AND (Directed)) => (DirectedWithNeighbors)))\n"
				+ "	r25:expression((((WithEdges) AND (Directed)) => (DirectedWithEdges)))\n"
				+ "	r26:expression((((OnlyVertices) AND (Undirected)) => (UndirectedOnlyVertices)))\n"
				+ "	r27:expression((((WithNeighbors) AND (Undirected)) => (UndirectedWithNeighbors)))\n"
				+ "	r28:expression((((WithEdges) AND (Undirected)) => (UndirectedWithEdges)))\n"
				+ "	r29:expression(((~ (Alg)) OR (StrongC)))\n"
				+ "	r30:expression(((~ (MSTKruskal)) OR (Undirected)))\n"
				+ "	r31:expression(((~ (MSTPrim)) OR (Undirected)))\n"
				+ "	r32:expression(((~ (WithNeighbors)) OR (WeightedWithNeighbors)))\n"
				+ "	r33:expression(((~ (OnlyVertices)) OR (WeightedOnlyVertices)))\n"
				+ "	r34:expression(((~ (MSTKruskal)) OR (WeightedWithNeighbors)))\n"
				+ "	r35:expression(((~ (MSTKruskal)) OR (WeightedOnlyVertices)))\n"
				+ "	r36:decomposition(MainGpl,[Src],[0,1])\n" + "	r37:decomposition(Alg,[StrongC],[0,1])\n";

		String fameDBB = "model  GPLB\n" + "elements: \n" + "	boolean Base\n" + "	boolean WithEdges\n"
				+ "	boolean WithNeighbors\n" + "	boolean OnlyVertices\n" + "	boolean Implementation\n"
				+ "	boolean Undirected\n" + "	boolean Directed\n" + "	boolean Gtp\n"
				+ "	boolean UndirectedOnlyVertices\n" + "	boolean UndirectedWithNeighbors\n"
				+ "	boolean UndirectedWithEdges\n" + "	boolean DirectedOnlyVertices\n"
				+ "	boolean DirectedWithNeighbors\n" + "	boolean DirectedWithEdges\n" + "	boolean HiddenGtp\n"
				+ "	boolean Unweighted\n" + "	boolean Weighted\n" + "	boolean Wgt\n" + "	boolean WeightedWithEdges\n"
				+ "	boolean WeightOptions\n" + "	boolean HiddenWgt\n" + "	boolean DFS\n" + "	boolean BFS\n"
				+ "	boolean Src\n" + "	boolean MSTKruskal\n" + "	boolean MSTPrim\n" + "	boolean Cycle\n"
				+ "	boolean Connected\n" + "	boolean Number\n" + "	boolean Alg\n" + "	boolean TestProg\n"
				+ "	boolean MainGpl\n" + "	boolean GPL\n" + "relations:\n" + "	r0: common(GPL)\n"
				+ "	r1:decomposition(GPL,[MainGpl],[1,1])\n" + "	r2:decomposition(MainGpl,[TestProg],[1,1])\n"
				+ "	r3:decomposition(MainGpl,[Alg],[1,1])\n" + "	r4:decomposition(Alg,[Number],[0,1])\n"
				+ "	r5:decomposition(Alg,[Connected],[0,1])\n" + "	r6:decomposition(Alg,[Cycle],[0,1])\n"
				+ "	r7:decomposition(Alg,[MSTPrim],[0,1])\n" + "	r8:decomposition(Alg,[MSTKruskal],[0,1])\n"
				+ "	r9:group(Src,[BFS, DFS],[1,1])\n" + "	r10:decomposition(MainGpl,[HiddenWgt],[1,1])\n"
				+ "	r11:decomposition(HiddenWgt,[WeightOptions],[1,1])\n"
				+ "	r12:decomposition(WeightOptions,[WeightedWithEdges],[0,1])\n"
				+ "	r13:group(Wgt,[Weighted, Unweighted],[1,1])\n" + "	r14:decomposition(MainGpl,[Wgt],[1,1])\n"
				+ "	r15:group(HiddenGtp,[DirectedWithEdges, DirectedWithNeighbors, DirectedOnlyVertices, UndirectedWithEdges, UndirectedWithNeighbors, UndirectedOnlyVertices],[1,1])\n"
				+ "	r16:decomposition(MainGpl,[HiddenGtp],[1,1])\n" + "	r17:group(Gtp,[Directed, Undirected],[1,1])\n"
				+ "	r18:decomposition(MainGpl,[Gtp],[1,1])\n"
				+ "	r19:group(Implementation,[OnlyVertices, WithNeighbors, WithEdges],[1,1])\n"
				+ "	r20:decomposition(MainGpl,[Implementation],[1,1])\n"
				+ "	r21:decomposition(MainGpl,[Base],[1,1])\n" + "	r22:expression(((Number) => ((Gtp) AND (Src))))\n"
				+ "	r23:expression(((Connected) => ((Undirected) AND (Src))))\n"
				+ "	r24:expression(((Cycle) => ((Gtp) AND (DFS))))\n"
				+ "	r25:expression((((MSTKruskal) OR (MSTPrim)) => ((Undirected) AND (Weighted))))\n"
				+ "	r26:expression((((MSTKruskal) OR (MSTPrim)) => (~ ((MSTKruskal) AND (MSTPrim)))))\n"
				+ "	r27:expression((((WithEdges) AND (Weighted)) => (WeightedWithEdges)))\n"
				+ "	r28:expression((((OnlyVertices) AND (Directed)) => (DirectedOnlyVertices)))\n"
				+ "	r29:expression((((WithNeighbors) AND (Directed)) => (DirectedWithNeighbors)))\n"
				+ "	r30:expression((((WithEdges) AND (Directed)) => (DirectedWithEdges)))\n"
				+ "	r31:expression((((OnlyVertices) AND (Undirected)) => (UndirectedOnlyVertices)))\n"
				+ "	r32:expression((((WithNeighbors) AND (Undirected)) => (UndirectedWithNeighbors)))\n"
				+ "	r33:expression((((WithEdges) AND (Undirected)) => (UndirectedWithEdges)))\n"
				+ "	r34:expression(((~ (Alg)) OR (MSTKruskal)))\n" + "	r35:expression(((~ (Alg)) OR (MSTKruskal)))\n"
				+ "	r36:decomposition(MainGpl,[Src],[0,1])\n" + "\n";

		
		String A = "model  A\n" + 
				"elements: \n" + 
				"	boolean a\n" + 
				"	boolean b\n" + 
				"relations:\n" + 
				"	r0: common(a)\n" +
				"   r1: decomposition(a, [b], [1,1])";
		
		String B = "model  B\n" + 
				"elements: \n" + 
				"	boolean a\n" +
				"	boolean c\n" +
				"relations:\n" + 
				"	r0: common(a)\n" + 
				"   r1: decomposition(a, [c], [1,1])";
		
		String fameA = "model  FameDBA\n" + 
				"elements: \n" + 
				"	boolean delete\n" + 
				"	boolean put\n" + 
				"	boolean get\n" + 
				"	boolean API\n" + 
				"	boolean Storage\n" + 
				"	boolean DebugLogging\n" + 
				"	boolean InMemory\n" + 
				"	boolean Dynamic\n" + 
				"	boolean Static\n" + 
				"	boolean MemAlloc\n" + 
				"	boolean Persistent\n" + 
				"	boolean BufferMgr\n" + 
				"	boolean NutOS\n" + 
				"	boolean OS\n" + 
				"	boolean DB\n" + 
				"relations:\n" + 
				"	r0: common(DB)\n" + 
				"	r1:decomposition(DB,[OS],[1,1])\n" + 
				"	r2:decomposition(OS,[NutOS],[1,1])\n" + 
				"	r3:group(BufferMgr,[Persistent, InMemory],[1,1])\n" + 
				"	r4:decomposition(DB,[BufferMgr],[1,1])\n" + 
				"	r5:group(MemAlloc,[Static, Dynamic],[1,1])\n" + 
				"	r6:decomposition(Persistent,[MemAlloc],[1,1])\n" + 
				"	r7:decomposition(DB,[DebugLogging],[0,1])\n" + 
				"	r8:decomposition(DB,[Storage],[1,1])\n" + 
				"	r9:group(API,[get, put, delete],[1,*])\n" + 
				"	r10:decomposition(Storage,[API],[1,1])";
		
		String fameB = "model  FameDBB\n" + 
				"elements: \n" + 
				"	boolean Unindexed\n" + 
				"	boolean BTree\n" + 
				"	boolean Index\n" + 
				"	boolean Storage\n" + 
				"	boolean DebugLogging\n" + 
				"	boolean InMemory\n" + 
				"	boolean LFU\n" + 
				"	boolean LRU\n" + 
				"	boolean PageRepl\n" + 
				"	boolean Persistent\n" + 
				"	boolean BufferMgr\n" + 
				"	boolean Win\n" + 
				"	boolean OS\n" + 
				"	boolean DB\n" + 
				"relations:\n" + 
				"	r0: common(DB)\n" + 
				"	r1:decomposition(DB,[OS],[1,1])\n" + 
				"	r2:decomposition(OS,[Win],[1,1])\n" + 
				"	r3:group(BufferMgr,[Persistent, InMemory],[1,1])\n" + 
				"	r4:decomposition(DB,[BufferMgr],[1,1])\n" + 
				"	r5:group(PageRepl,[LRU, LFU],[1,1])\n" + 
				"	r6:decomposition(Persistent,[PageRepl],[1,1])\n" + 
				"	r7:decomposition(DB,[DebugLogging],[0,1])\n" + 
				"	r8:decomposition(DB,[Storage],[1,1])\n" + 
				"	r9:group(Index,[BTree, Unindexed],[1,1])\n" + 
				"	r10:decomposition(Storage,[Index],[1,1])\n";
		
		String fameDB = "model  FameDB\n" + 
				"elements: \n" + 
				"	boolean Unindexed\n" + 
				"	boolean BTree\n" + 
				"	boolean Index\n" + 
				"	boolean delete\n" + 
				"	boolean put\n" + 
				"	boolean get\n" + 
				"	boolean API\n" + 
				"	boolean Storage\n" + 
				"	boolean DebugLogging\n" + 
				"	boolean InMemory\n" + 
				"	boolean LFU\n" + 
				"	boolean LRU\n" + 
				"	boolean PageRepl\n" + 
				"	boolean Dynamic\n" + 
				"	boolean Static\n" + 
				"	boolean MemAlloc\n" + 
				"	boolean Persistent\n" + 
				"	boolean BufferMgr\n" + 
				"	boolean Win\n" + 
				"	boolean NutOS\n" + 
				"	boolean OS\n" + 
				"	boolean DB\n" + 
				"relations:\n" + 
				"	r0: common(DB)\n" + 
				"	r1:group(OS,[NutOS, Win],[1,1])\n" + 
				"	r2:decomposition(DB,[OS],[1,1])\n" + 
				"	r3:group(BufferMgr,[Persistent, InMemory],[1,1])\n" + 
				"	r4:decomposition(DB,[BufferMgr],[1,1])\n" + 
				"	r5:group(MemAlloc,[Static, Dynamic],[1,1])\n" + 
				"	r6:decomposition(Persistent,[MemAlloc],[1,1])\n" + 
				"	r7:group(PageRepl,[LRU, LFU],[1,1])\n" + 
				"	r8:decomposition(Persistent,[PageRepl],[1,1])\n" + 
				"	r9:decomposition(DB,[DebugLogging],[0,1])\n" + 
				"	r10:decomposition(DB,[Storage],[1,1])\n" + 
				"	r11:group(API,[get, put, delete],[1,*])\n" + 
				"	r12:decomposition(Storage,[API],[1,1])\n" + 
				"	r13:group(Index,[BTree, Unindexed],[1,1])\n" + 
				"	r14:decomposition(Storage,[Index],[1,1])\n";
		String[] modelUris = { fameDB};
		
		try {

			HLVLParser parser = HLVLParser.getInstance();
			Model[] models = parser.generateModels(modelUris);
			List<List<List<Integer>>> currentDimacs = parser.getDIMACSs(models);
			
			System.out.println(DIMACS.toString(currentDimacs.get(0)));
		//	System.out.println(DIMACS.toString(union(currentDimacs)));
		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}
}
