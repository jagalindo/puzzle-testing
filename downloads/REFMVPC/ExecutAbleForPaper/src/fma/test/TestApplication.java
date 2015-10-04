package fma.test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import equ.EquivalenceChecker;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;

import at.jku.sea.mvsc.SPL;
import at.jku.sea.mvsc.SPLOT2FAMA;
import at.jku.sea.mvsc.examples.SPLOT;



public class TestApplication {
	public static void main(String[] args) {
		String directory;
		File splotFile;
		File famaFile;
		File cnfWorkingDir;
		if(args.length!=1){
			System.out.println("Please pass the path to the Splot-File as command line argument to the application.");
		}else{
			splotFile = new File(args[0]);
			if(splotFile.getParentFile()==null){
				directory="";
			}else{
				directory = splotFile.getParentFile().getAbsolutePath()+File.separator;
			}
			famaFile = new File(directory+"inputModelInFamaFormat.xml");
			if(!splotFile.exists()) {
				System.out.println("Please pass the path to the Splot-File as command line argument to the application.");
				System.exit(0);
			}
			
			System.out.println("Converting Splot to Fama file...");
			ConvertSplotToFama(splotFile, famaFile);
			
			System.out.println("Generating a file containing all valid product configurations... ");
			int numProds = writeProductsToFile(famaFile.getPath(), directory+"products.txt", true);
			if (numProds == 0) {
				System.out.println("Timeout during products generation! \r\n");
			} else {
				callAnalyzer(directory+"products.txt", directory+"ExtractedFM.xml");
				cnfWorkingDir =  new File(directory + File.separator + "CNFRepresentations");
				cnfWorkingDir.mkdirs();
				if (EquivalenceChecker.checkEquality(splotFile.getPath(), directory+"ExtractedFM.xml", cnfWorkingDir.getPath())) {
					System.out.println("The input and output model are equivalent!");
				} else {
					System.out.println("The input and output model are not equivalent.");
					System.out.println("Please check whether the input model only includes basic CTCs.");
					System.out.println("Please check whether the input model does not contain circular requires CTCs or constructs that are equivalent.");
				}
			}
		}
	}

	private static void ConvertSplotToFama(File splotFile, File famaFile) {
		SPLOT2FAMA transformer = new SPLOT2FAMA();
		SPL splotGPL = new SPLOT("dummy"); // only to fill the purposes
		transformer.splot2FAMA(splotGPL,splotFile.getPath(), 
				famaFile.getPath());
	}
	
	public static int writeProductsToFile(String ModelPath, String FilePath,
			boolean evalNum) {
		int numProds = -1;
		try {
			File file = new File(FilePath);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			StringBuffer sb = new StringBuffer();


			QuestionTrader qt = new QuestionTrader();
			GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(ModelPath);
			
			qt.setVariabilityModel(fm);
			//qt.setSelectedReasoner("JavaBDD");

			// Read all possible features
			ArrayList<GenericFeature> features = new ArrayList<GenericFeature>();
			Iterator<? extends GenericFeature> it2 = fm.getFeatures()
					.iterator();
			String s;
			while (it2.hasNext()) {
				GenericFeature p = (GenericFeature) it2.next();
				s = p.toString();
				sb.append(s + ";");
				features.add(p);
			}
			sb.append("\r\n");
			bw.write(sb.toString());
			sb = new StringBuffer();

			if (evalNum) {
				// Get the number of products
				NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt
						.createQuestion("#Products");
				qt.ask(npq);

				numProds = (int) npq.getNumberOfProducts();
				System.out.println("The number of products is: " + numProds);
			}

			if (numProds != 0) {
				// Get all possible product configurations
				ProductsQuestion pq = (ProductsQuestion) qt
						.createQuestion("Products");
				qt.ask(pq);
				Iterator<? extends GenericProduct> it = pq.getAllProducts()
						.iterator();

				while (it.hasNext()) {
					Product p = (Product) it.next();
					Collection<GenericFeature> pFeatures = p.getFeatures();

					for (GenericFeature f : features) {
						if (pFeatures.contains(f)) {
							sb.append("1");
						} else {
							sb.append("0");
						}
					}
					sb.append("\r\n");
					bw.write(sb.toString());
					sb = new StringBuffer();
				}
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numProds;
	}
	
	public static void callAnalyzer(String object1Path, String genModelPath) {

		Set<fma.Product> products = new HashSet<fma.Product>();
		Set<fma.Feature> attributes = new HashSet<fma.Feature>();
		fma.RNode root = new fma.RNode(fma.RType.root, null);

		fma.Analyzer os = new fma.Analyzer();

		System.out.println("Starting to read in the products...");
		fma.Reader.readProduct(object1Path, products, attributes, ";");

		long time1 = System.currentTimeMillis();
		System.out.println("Starting the algorithm to reverse engineer the FM...");
		os.analyse(products, attributes, root);
		long time2 = System.currentTimeMillis();
		System.out.println("Time needed to extract the model (in ms):"+time2+"-"+time1+"="+(time2-time1));

		os.getFm().writeSplotModelToFile(genModelPath);
	}
}
