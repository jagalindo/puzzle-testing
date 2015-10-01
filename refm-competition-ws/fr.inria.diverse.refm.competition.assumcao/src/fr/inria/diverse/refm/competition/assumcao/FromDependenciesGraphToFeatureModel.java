package fr.inria.diverse.refm.competition.assumcao;

import fm.FeatureModel;
import fm.FeatureTreeNode;
import at.jku.isse.fm.operator.FeatureModelBuilder;

public class FromDependenciesGraphToFeatureModel {

	public void translate(){
		FeatureModelBuilder builder = new FeatureModelBuilder();
		FeatureModel fm = new FeatureModel() {
			
			@Override
			protected void saveNodes() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected FeatureTreeNode createNodes() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
