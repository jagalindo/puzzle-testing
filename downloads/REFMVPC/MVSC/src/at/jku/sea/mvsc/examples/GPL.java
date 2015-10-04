/**
 * Definition of GPL example product line test
 */

package at.jku.sea.mvsc.examples;

import java.util.Arrays;

import at.jku.sea.mvsc.*;

public class GPL extends SPL {

	public GPL(String splName, String dir, String rootName) { super (splName, dir, rootName); }
	
	/**
	 * Here we define the 20 constraints of GPL
	 */
	public void defineDomainConstraints() { 
		
		// 1) Mandatory GPL
		Mandatory m1 = new Mandatory( LogicTransformation.TRUE, "GPL", this);
		addDomainConstraint(m1);
		
		// 2) Mandatory TestProg - GPL
		Mandatory m2 = new Mandatory( "GPL", "TestProg", this);
		addDomainConstraint(m2);
		
		// 3) Mandatory Alg - GPL
		// Mandatory m3 = new Mandatory( "Alg", "GPL", this); // ERROR the nodes should be the other way around because GPL is the parent of algorithm
		Mandatory m3 = new Mandatory("GPL", "Alg",  this); // this I guess is correct now!!
		addDomainConstraint(m3);
		
		// 4) Optional Src - GPL
		Optional o4 = new Optional("GPL","Src",this);
		addDomainConstraint(o4);
		
		// 5) Optional Weighted - GPL
		Optional o5 = new Optional("GPL","Weighted",this);
		addDomainConstraint(o5);
		
		// 6) Mandatory Gtp - GPL
		Mandatory o6 = new Mandatory("GPL","Gtp",this);
		addDomainConstraint(o6);
		
		// 7) Inc-Or Alg - Number, Connected, StronglyConnected, Cycle, MSTPrim, MSTKruskal
		InclusiveOR incor7 = new InclusiveOR("Alg",Arrays.asList("Number","Connected","StronglyConnected","Cycle","MSTPrim", "MSTKruskal"),this);
		addDomainConstraint(incor7);
		
		// 8) Exc-Or Src - BFS, DFS
		ExclusiveOR eor8 = new ExclusiveOR("Src",Arrays.asList("BFS","DFS"),this);
		addDomainConstraint(eor8);
		
		// 9) Exc-Or Gtp - Directed, Undirected
		ExclusiveOR eor9 = new ExclusiveOR("Gtp",Arrays.asList("Directed","UnDirected"),this);
		addDomainConstraint(eor9);
		
		// 10) Requires Number - Search
		Requires r10 = new Requires("Src","Number",this);
		addDomainConstraint(r10);

		// 11) Requires Connected - Search
		Requires r11 = new Requires("Src","Connected",this);
		addDomainConstraint(r11);

		// 12) Requires Connected - UnDirected
		Requires r12 = new Requires("UnDirected","Connected",this);
		addDomainConstraint(r12);
		
		// 13) Mandatory StronglyConnected - Transpose
		Mandatory m13 = new Mandatory("StronglyConnected","Transpose",this);
		addDomainConstraint(m13);
		
		// 14) Requires StronglyConnected - Directed
		Requires r14 = new Requires("Directed","StronglyConnected",this);
		addDomainConstraint(r14);

		// 15) Requires StronglyConnected - DFS
		Requires r15 = new Requires("DFS","StronglyConnected",this);
		addDomainConstraint(r15);
		
		// 16) Requires Cycle - DFS
		Requires r16 = new Requires("DFS","Cycle",this);
		addDomainConstraint(r16);	
		
		// 17) Requires MSTPrim - Weighted
		Requires r17 = new Requires("Weighted","MSTPrim",this);
		addDomainConstraint(r17);		
		
		// 18)  Requires MSTPrim- UnDirected
		Requires r18 = new Requires("UnDirected","MSTPrim",this);
		addDomainConstraint(r18);					

		// 19)  Requires MSTKruskal - Weighted
		Requires r19 = new Requires("Weighted","MSTKruskal",this);
		addDomainConstraint(r19);
		
		// 20)  Requires MSTKruskal - UnDirected
		Requires r20 = new Requires("UnDirected","MSTKruskal",this);
		addDomainConstraint(r20);		
		
		// 21) Excludes MSTKruskal - MSTPrim
		Excludes e21 = new Excludes("MSTPrim","MSTKruskal",this);
		addDomainConstraint(e21);
		
		// 22) Dummy test of exclusive or
		// @Note Test works fine for any number of children
		// ExclusiveOR eor22 = new ExclusiveOR("Gtp",Arrays.asList("Directed","UnDirected","Number","Cycle","Connected"),this);
		// addDomainConstraint(eor22);
		
	}
	
	/**
	 * We define features GPL, Alg, Src, and Gtp
	 */
	public void defineDecisionFeatures() {
		
		// Definition of features
		Feature gpl = new Feature("GPL");
		Feature alg = new Feature("Alg");
		Feature src = new Feature("Src");
		Feature gtp = new Feature("Gtp");
		
		// Adding the features to the feature list and corresponding maps

		// GPL
		int currentVal = getVariableValue();
		features.add(gpl);
		mapFeatureToVariable.put(gpl, currentVal);
		mapVariableToFeature.put(currentVal, gpl);
		
		// Alg
		currentVal = getVariableValue();
		features.add(alg);
		mapFeatureToVariable.put(alg, currentVal);
		mapVariableToFeature.put(currentVal, alg);
		
		// Src
		currentVal = getVariableValue();
		features.add(src);
		mapFeatureToVariable.put(src, currentVal);
		mapVariableToFeature.put(currentVal, src);
		
		// Gtp
		currentVal = getVariableValue();
		features.add(gtp);
		mapFeatureToVariable.put(gtp, currentVal);
		mapVariableToFeature.put(currentVal, gtp);		
	}

	
	/**
	 * Defines the decision features and the domain constraints
	 */
	public void setUpConstraints() {
		defineDecisionFeatures();
		defineDomainConstraints();
	}
	
}
