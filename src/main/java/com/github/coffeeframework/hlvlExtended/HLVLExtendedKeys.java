package com.github.coffeeframework.hlvlExtended;

import com.github.coffeeframework.basickhlvlpackage.HlvlBasicKeys;

public enum HLVLExtendedKeys {

	MANDATORY_DECOMPOSITION(HlvlBasicKeys.DECOMPOSITION, HlvlBasicKeys.MANDATORY),
	OPTIONAL_DECOMPOSITION(HlvlBasicKeys.DECOMPOSITION, HlvlBasicKeys.OPTIONAL),
	XOR_GROUP(HlvlBasicKeys.GROUP, HlvlBasicKeys.ALTERNATIVE),
	OR_GROUP(HlvlBasicKeys.GROUP, HlvlBasicKeys.OR);
	
	private final String relation;
	private final String cardinality;
	
	HLVLExtendedKeys(String relation, String cardinality) {
		this.relation = relation;
		this.cardinality = cardinality;
	}

	public String getRelation() {
		return relation;
	}

	public String getCardinality() {
		return cardinality;
	}
	
}
