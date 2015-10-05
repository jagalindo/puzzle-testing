package fr.inria.diverse.refm.competition.haslinger;

public class PCMFormatTranslator {

	private String[][] PCM;
	
	public void loadPCM(String PCMString) {
		String[] products = PCMString.split("\n");
		int productsAmount = products.length;

		String[] features = products[0].split(",");
		int featuresAmount = features.length;

		PCM = new String[productsAmount][featuresAmount];

		for (int i = 1; i < features.length; i++) {
			PCM[0][i] = features[i];
		}

		for (int i = 1; i < products.length; i++) {
			String[] productFeatures = products[i].split(",");
			for (int j = 0; j < productFeatures.length; j++) {
				PCM[i][j] = productFeatures[j];
			}
		}
		PCM[0][0] = "\"Product\"";
	}
	
	public String fromPuzzleToBecanFormat(String originalPCM){
		String newPCM = "root;";
		
		for (int i = 1; i < PCM[0].length; i++) {
			newPCM += PCM[0][i].replace("\"", "") + ";";
		}
		newPCM += "\n";
		
		for (int i = 1; i < PCM.length; i++) {
			newPCM += "1";
			for (int j = 1; j < PCM[0].length; j++) {
				if(PCM[i][j].replace("\"", "").equals("YES")){
					newPCM += "1";
				}
				else{
					newPCM += "0";
				}
			}
			newPCM += "\n";
		}
		
		return newPCM;
	}
}
