package com.github.coffeeframework.hlvlExtended;

import java.util.List;

public interface IHLVLExtendedFactory {

	public String getGroup(String parent, List<String> children, String min, String max);
	public String getImpliesList(String var1, List<String> list);
	public String getMutexList(String var1, List<String> list);
	public String getDecompositionList(String parent, List<String> children, String min, String max);
}
