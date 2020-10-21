package com.github.coffeeframework.hlvlExtended;

import java.util.List;

import com.github.coffeeframework.basickhlvlpackage.HlvlBasicFactory;

public class HlvlExtendedFactory extends HlvlBasicFactory implements IHlvlExtendedFactory{

	private static int numId = 0;
	
	@Override
	public String getGroup(String parent, List<String> children, String min, String max) {
		String out = "grp" + numId++ + COLON + SPACE + GROUP
				+ OPEN_CALL + parent + COMMA + SPACE
				+ OPEN_LIST;

		for (String id : children) {
			out += id + COMMA + SPACE;
		}
		
		out = out.substring(0, out.length() - 2) + CLOSE_LIST + COMMA + SPACE
				+ OPEN_LIST + min + COMMA + SPACE + max
				+ CLOSE_LIST + CLOSE_CALL + "\n";
		
		return out;
	}

	@Override
	public String getImpliesList(String var1, List<String> list) {
		String out = "imp" + numId++ + COLON + SPACE + IMPLIES
				+ OPEN_CALL;
		out += var1 + ", " + OPEN_LIST;
		
		for (String id : list) {
			out += id + COMMA + SPACE;
		}
		
		out = out.substring(0, out.length() - 2) + CLOSE_LIST + CLOSE_CALL + "\n";

		return out;
	}

	@Override
	public String getMutexList(String var1, List<String> list) {
		String out = "mut" + numId++ + COLON + SPACE + SPACE
				+ MUTEX + OPEN_CALL;
		out += var1 + ", " + OPEN_LIST;
		
		for (String id : list) {
			out += id + COMMA + SPACE;
		}
		
		out = out.substring(0, out.length() - 2) + CLOSE_LIST + CLOSE_CALL + "\n";

		return out;
	}

	@Override
	public String getDecompositionList(String parent, List<String> children, String min, String max) {
		String out= "dec"+ numId++ + COLON +  DECOMPOSITION + OPEN_CALL +  parent + COMMA + OPEN_LIST;

		for(String id: children){
			out+= id + COMMA + SPACE;
		}
		
		out = out.substring(0, out.length() - 2) + CLOSE_LIST + COMMA + SPACE
				+ OPEN_LIST + min + COMMA + SPACE + max
				+ CLOSE_LIST + CLOSE_CALL + "\n";
		
		return out;
	}
	
}
