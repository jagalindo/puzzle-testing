package fr.inria.diverse.refm.competition.assumcao;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.SPLXWriter;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.refm.competition.common.utils.FileUtils;

public class AssumcaoREFM {

	public FAMAFeatureModel execute(Graph<Vertex, Arc> dependenciesGraph, String PCMFile) throws Exception {
		String PCMName = PCMFile.substring(PCMFile.lastIndexOf("/"), PCMFile.length()).replace(".txt", "");
		
		// The approach receives a SPLOT model with the dependencies graph and in initial solution to the problem.
		// In this step, we build such input and store it in an xml file that is later passed to the synthesizer.
		String dependenciesGraphPath = "new_models" + PCMName + ".xml";
		FAMAFeatureModel dependenciesGraphFm = new FAMAFeatureModel();
		Feature root = new Feature("root");
		dependenciesGraphFm.setRoot(root);
		Relation relation = new Relation("o");
		root.addRelation(relation);
		for (Vertex vertex : dependenciesGraph.getVertex()) {
			Feature currentFeature = new Feature(vertex.getIdentifier());
			relation.addDestination(currentFeature);
			relation.addCardinality(new Cardinality(0, dependenciesGraph.getVertex().size()));
		}
		
		SPLXWriter splotWritter = new SPLXWriter();
		splotWritter.writeFile(dependenciesGraphPath, dependenciesGraphFm);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		File xmlFile = new File(dependenciesGraphPath);
		System.out.println(xmlFile.exists());
		Document doc = dBuilder.parse(xmlFile);
		
		Element dependencies = doc.createElement("dependencies");
		Element feature_model = (Element) doc.getDocumentElement();
		feature_model.appendChild(dependencies);
		
		for (Arc arc : dependenciesGraph.getArcs()) {
			Element dependency = doc.createElement("dependency");
			Attr from = doc.createAttribute("from");
			from.setNodeValue("(" + arc.getFrom().getIdentifier() + ")");
			dependency.setAttributeNode(from);
			Attr to = doc.createAttribute("to");
			to.setNodeValue("(" + arc.getTo().getIdentifier() + ")");
			dependency.setAttributeNode(to);
			Attr weight = doc.createAttribute("weight");
			weight.setNodeValue("1");
			dependency.setAttributeNode(weight);
			dependencies.appendChild(dependency);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(dependenciesGraphPath));
		transformer.transform(source, result);
		
		
		String originalPCM = FileUtils.readFileContent(new File(PCMFile));
		PCMFormatTranslator translator = new PCMFormatTranslator();
		translator.loadPCM(originalPCM);
		String adaptedPCM = translator.fromPuzzleToLopezFormat(originalPCM);
		String formatedFilePath = "new_models/" + PCMName + ".prods";
		FileUtils.saveFile(adaptedPCM, formatedFilePath);
		
		String[] args = new String[2];
		args[0] = PCMName;
//		args[0] = "argouml";
		run.RunNSGAIIFM_3obj.main(args);
		
		// load the result from the output file

		return null;
	}

	private Feature findFeature(Feature root, String identifier) {
		for (Feature child : root.getChilds()) {
			if(child.getName().equals(identifier)){
				return child;
			}
		}
		
		for (Feature child : root.getChilds()) {
			Feature searched = this.findFeature(child, identifier);
			if(searched != null)
				return searched;
		}
		
		return null;
	}
}
