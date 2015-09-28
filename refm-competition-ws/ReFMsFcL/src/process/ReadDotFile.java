package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class ReadDotFile {
	
	List<List<String>> allAtomic = new ArrayList<List<String>>();
	
	public static List<String> ConceptUsed = new ArrayList<String>();
	public static List<String> ConceptUnUsed = new ArrayList<String>();
	
	public static List<String> numofPaths = new ArrayList<String>();
	
	public static String TopConceptNumber;
	public static String BottomConceptNumber;
	
	public static List<String> TopConcept = new ArrayList<String>();
	public static List<String> BottomConcept = new ArrayList<String>();
	public static List<String> XORConceptUnUsed = new ArrayList<String>();
	public static List<String> XORConceptName = new ArrayList<String>();
	
	public static String[][] Map;
	public static int[][] AdMatrix ;
	
	
	public static Set<String> FirstPart = new HashSet<String>();
	public static Set<String> SecondPart = new HashSet<String>();
	public static Set<String> FirstPart1 = new HashSet<String>();
	public static Set<String> SecondPart1 = new HashSet<String>();
	
	public static List<String> atomicSetFeature = new ArrayList<String>();
	public static List<String> atomicSetFeature22 = new ArrayList<String>();
	public static List<Concept> tempAtomic = new ArrayList<Concept>();
	public static List<Concept> tempAtomic1 = new ArrayList<Concept>();
	
	public static List<String> XorFeature = new ArrayList<String>();
	public static List<Concept> tempXor = new ArrayList<Concept>();
	public static List<String> alternative = new ArrayList<String>();
	
	public static List<Concept> require = new ArrayList<Concept>();
	public static List<String> requireFeature = new ArrayList<String>();
	
	
//	store the optional features from all disjoint paths by extraction the common features
	
	public static List<String> Optional = new ArrayList<String>(); 
	public static List<String> Optional2 = new ArrayList<String>();
	
//	store the Xor features from all disjoint paths by extraction the unique features for each path
	
	public static List<String> Xor = new ArrayList<String>(); 
	
	public static String root1;
	public static List<String> rootNode = new ArrayList<String>();
	
	public static List<String> root = new ArrayList<String>();
	public static List<String> mandatory = new ArrayList<String>();
	
	public static List<Concept> temp = new ArrayList<Concept>();
	public List<Lattice> RCALattices = new ArrayList<Lattice>();
	
	public List<Concept> FeatureConceptList = new ArrayList<Concept>();
	public List<Concept> BCList = new ArrayList<Concept>();
	
	public int FeatureAdjacencyMatrix[][];
	private StackX theStack = new StackX();
	private HashMap<Concept, Integer> hm = new HashMap<Concept, Integer>();
	private List<Concept> AllFeatureLatticeConcepts = new ArrayList<Concept>();
	
	public void ReadDotFileMethod (String Path) {

		String LatticeName = null;
		File f = new File(Path);
		String[] Concepts;
		String t;
		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String textinLine;

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;
				sb.append(textinLine);
			}

			t = sb.toString();

			String[] Lattices = t.split("digraph");
			for (int L = 1; L < Lattices.length; L++) {
				Lattice LatticeObj = new Lattice();
				List<Concept> ConceptList = new ArrayList<Concept>();
				Concepts = Lattices[L].split(";");
				LatticeName = getLatticeName(Concepts[0]);
				System.out.println("Lattic Name : " + LatticeName);

				for (int i = 1; i < Concepts.length - 1; i++) {

					Concept ConceptObj = PrintConcept(Concepts[i], i,
							LatticeName);
					ConceptObj.LatticeName = LatticeName;
					ConceptList.add(ConceptObj);

				}

//              Map ==== >> 7807046 -> 10458977 7807046 -> 25915643 15205143 -> 2286508

				Map = ReadMapping(Concepts[Concepts.length - 1]);

//			    System.out.println(Concepts[Concepts.length - 1]);

				LatticeObj.LatticeName = LatticeName;
				LatticeObj.setConceptList(ConceptList);
				LatticeObj.edges = Map;

//				System.err.println(LatticeObj.edges );

				int[][] adjacencyMatrix = buildingAdjacencyMatrix(Map, ConceptList);
				LatticeObj.adjMat = adjacencyMatrix;
				RCALattices.add(LatticeObj);
				
			}
			
			System.out.println("THE END");
			fs.close();
			in.close();
			br.close();

			FirstPart1.addAll(FirstPart);
			SecondPart1.addAll(SecondPart);
			
			SecondPart.removeAll(FirstPart);
			Iterator<String> iterator;
			iterator = SecondPart.iterator();
			while (iterator.hasNext()) {
			TopConceptNumber= iterator.next();
			TopConcept.add(TopConceptNumber);
			System.err.println("The Top Concept Number = "+ TopConceptNumber);
			}
			
			FirstPart1.removeAll(SecondPart1);
			Iterator<String> iterator11;
			iterator11 = FirstPart1.iterator();
			while (iterator11.hasNext()) {
			BottomConceptNumber= iterator11.next();
			BottomConcept.add(BottomConceptNumber);
			System.err.println("The Bottom Concept Number = "+ BottomConceptNumber);
			}	
			
			
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public int[][] buildingAdjacencyMatrix(String[][] Map, List<Concept> ConceptList) {

		int[][] adjacencyMatrix = new int[ConceptList.size()][ConceptList.size()];
		
		AdMatrix = new int[ConceptList.size()][ConceptList.size()];
		
		for (int i = 0; i < ConceptList.size(); i++) {
			
			
			String[] maps = determiningMapping(ConceptList.get(i).getNumber(),Map);
		
			if (maps.length != 0) {

				int[] adjacents = determiningAdjacents(ConceptList, maps);
				
				for (int j = 0; j < adjacents.length; j++) {
					
					adjacencyMatrix[i][i] = 1;
				    adjacencyMatrix[i][adjacents[j]] = 1;
						
//					 System.out.print(adjacencyMatrix[i] + "\t");

//					 System.out.println(adjacents[j]);
				}

//				 System.out.println();

			}
		}

		System.out.print("\t");

		for (int i = 0; i < ConceptList.size(); i++) {

			System.out.print(ConceptList.get(i).getCoName() + "\t");
		}

		System.out.println("");

		for (int f = 0; f < adjacencyMatrix.length; f++) {

			System.out.print(ConceptList.get(f).getCoName() + "\t");

			// System.err.print(adjacencyMatrix.length);

			for (int g = 0; g < adjacencyMatrix.length; g++) {

				 System.out.print(adjacencyMatrix[f][g] + " "); // to print Adjacency Matrix
				 AdMatrix[f][g]=adjacencyMatrix[f][g];												
//				 System.out.print(adjacencyMatrix[0].length);

			}

			System.out.println();
		}

		return adjacencyMatrix;

	}

	public String[] determiningMapping(String CoNumber, String[][] Map) {

		List<String> temp1 = new ArrayList<String>();

		String[] first = new String[Map.length];
		String[] Second = new String[Map.length];

		for (int i = 0; i < Map.length; i++) {
			if (CoNumber.equals(Map[i][0])) {
				first[i] = Map[i][0];
				FirstPart.add(first[i]);
				Second[i] = Map[i][1];
				SecondPart.add(Second[i]);
//				 System.err.println(first[i]);
			}
		}

		for (int i = 0; i < Map.length; i++) {

			// System.err.println(Map[i][0]);

			if (CoNumber.equals(Map[i][0])) {

				temp1.add(Map[i][1]);

				// System.err.println(Map[i][1]);
			}

		}
		String temp[] = temp1.toArray(new String[temp1.size()]);

		// System.err.println(temp1.toArray(new String[temp1.size()]));

		// temp = temp1.toArray();
		return temp;
	}

	public int[] determiningAdjacents(List<Concept> ConceptList, String[] maps) {

		int temp[] = new int[maps.length];
		int count = 0;

		for (int j = 0; j < maps.length; j++) {

//			 System.out.println(maps[j]);

			for (int i = 0; i < ConceptList.size(); i++) {

				if (ConceptList.get(i).getNumber().equals(maps[j])) {

					temp[count] = i;
					count++;
					
				}
			}
		}
//		 System.err.println(temp);
		return temp;

	}

	public String[][] ReadMapping(String key) {

		List<String> l1 = new ArrayList<String>();
		int count = 0;
		String[] temp;
		// String[] temp1;
		key = key.replaceAll("\\s|->", "#");
		key = key.replaceAll("[}]", "#");
		temp = key.trim().split("#");

		// System.err.println(temp.toString());

		for (int i = 0; i < temp.length; i++) {
			if (!temp[i].isEmpty()) {
				l1.add(temp[i]);
				// System.out.println(l1);
			}
		}

		// ====================================================

		String[][] mapping = new String[l1.size() / 2][2];

		for (int i = 0; i < l1.size() / 2; i++) {
			for (int j = 0; j < 2; j++) {
				mapping[i][j] = l1.get(count).trim();
				count++;
				// System.out.print(mapping[i][j] + "\t");
			}
			// System.out.println();
		}

		return mapping;
	}

	public Concept PrintConcept(String Concept, int ii, String LatticeName) throws IOException {

		Concept ConceptObj = new Concept();
		String ConceptNumbr = null;
		String ConceptName = null;
		List<String> Intent = new ArrayList<String>();
		List<String> Extent = new ArrayList<String>();

		// System.out.println(Concept);
		String[] Temp = Concept.split("\\[");
		String[] TempIntent = null;
		String[] TempExtent = null;
		String[] TempIntentExtent = null;

		// System.out.println(Temp[0]);
		// System.out.println(Temp[1]);
		ConceptNumbr = Temp[0].trim(); // <<<< Concept Number
		ConceptObj.ConceptNumber = ConceptNumbr;
		Concept = Temp[1];
		Temp = null;
		Temp = Concept.split("\"[{]");
		// System.out.println(Temp[0]);
		// System.out.println(Temp[1]);
		Concept = Temp[1];
		Temp = null;
		Temp = Concept.split("\\|");
		// System.out.println(Temp[0]);
		// System.out.println(Temp[1]);
		// System.out.println(Temp[2]);
		ConceptName = Temp[0].trim(); // <<<<< Concept Name
		ConceptObj.ConceptName = ConceptName;

		TempIntent = Temp[1].split("\\\\n");
		TempExtent = Temp[2].split("\\\\n");

		if (TempIntent.length != 0) {
			for (int gg = 0; gg < TempIntent.length; gg++) {
				TempIntentExtent = TempIntent[gg].split(":");
				if (TempIntentExtent.length == 2) {
					// System.out.println(TempIntentExtent[1]);
					Intent.add(TempIntentExtent[1].trim()); // <<<< Current
															// Intent
				}

				else if (TempIntentExtent.length == 1) {
					// System.out.println(TempIntentExtent[0]);
					Intent.add(TempIntentExtent[0].trim()); // <<<< Current
															// Intent
				}

				TempIntentExtent = null;
			}
		}

		if (TempExtent.length != 0) {
			TempIntentExtent = null;
			for (int ff = 0; ff < TempExtent.length - 1; ff++) {
				TempIntentExtent = TempExtent[ff].split(":");
				// System.out.println(TempIntentExtent.length);
				if (TempIntentExtent.length == 2) {
					// System.out.println(TempIntentExtent[1]);
					Extent.add(TempIntentExtent[1]); // <<<<< Current Extent
				}

				else if (TempIntentExtent.length == 1) {
					// System.out.println(TempIntentExtent[1]);
					Extent.add(TempIntentExtent[0]); // <<<< Current Intent
				}
				TempIntentExtent = null;
			}

		}

		// System.out.println("ConceptName : " + ConceptName);

		ConceptObj.setIntent(Intent);
		ConceptObj.setExtent(Extent);

		return ConceptObj;

	}

	public List<String> FromSetToList(Set<String> LinesOfMeth) {
		List<String> ListOfMethods = new ArrayList<String>();

		for (String s : LinesOfMeth) {
			ListOfMethods.add(s);
		}

		return ListOfMethods;
	}

	public String getLatticeName(String SequenceOfWords) {

		String[] temp = SequenceOfWords.split("rankdir=");
//		System.err.println(temp[1]);
		return temp[1].trim();
	}

	public void dfs() throws IOException { // depth-first search

		List<Concept> FeatureL = new ArrayList<Concept>();

		for (int i = 0; i < RCALattices.size(); i++) {

			if (RCALattices.get(i).getLatticeName().equals("BT")) {

				FeatureAdjacencyMatrix = RCALattices.get(i).adjMat;
				
				
				
				for (int j = 0; j < RCALattices.get(i).getConceptList().size(); j++) {
					
					hm.put(RCALattices.get(i).getConceptList().get(j), j);
					AllFeatureLatticeConcepts.add(RCALattices.get(i).getConceptList().get(j));

					FeatureL.add(RCALattices.get(i).getConceptList().get(j));

				}
//				System.out.println(FeatureL.get(i));
			}

		}

		FeatureConceptList = FeatureL;
	
		for (int i = 0; i < FeatureConceptList.size(); i++) {
			
		temp = DFS_Visit(FeatureConceptList.get(i));
		temp.add(FeatureConceptList.get(i));
		
		if (!FeatureConceptList.get(i).getNumber().equals(TopConceptNumber) && FeatureConceptList.get(i).getIntent().size()>=2)
		ConceptUsed.add(FeatureConceptList.get(i).getNumber());	

		}

		root=rootFeature(temp);
		System.out.println("Root Feature :"+ root);
		
//		System.err.println(allAtomic);
		
		mandatory = mandatoryFeature(temp);
		System.out.println("Mandatory Features :"+ mandatory);
		
		System.err.println("The used Concept # = " + ConceptUsed);
	}

	public List<String> rootFeature(List<Concept> temp) {

	
		List<String> CIntent = new ArrayList<String>();

		for (int i = 0; i < temp.size(); i++) {
			
			if (temp.get(i).getNumber().equals(TopConceptNumber)) {
				
				CIntent = temp.get(i).getIntent();
				
				for (int j = 0; j < CIntent.size(); j++) {
					if(CIntent.get(j).contains(".")){
						rootNode.add(CIntent.get(j).replace(".", ""));
					}
				}
			}
		}
		
//		System.out.println(accumulator);
		
		return rootNode;
		
	}

	public List<String> mandatoryFeature(List<Concept> temp) {
		
		List<String> accumulator = new ArrayList<String>();
		List<String> CIntent = new ArrayList<String>();

		for (int i = 0; i < temp.size(); i++) {
			
			if (temp.get(i).getNumber().equals(TopConceptNumber)) {
				ConceptUsed.add(temp.get(i).getNumber());	
				CIntent = temp.get(i).getIntent();

				for (int j = 0; j < CIntent.size(); j++) {
					    if (!CIntent.get(j).equals(rootNode))
						accumulator.add(CIntent.get(j).replace(".", ""));
						
				}
			}
		}
		
		accumulator.removeAll(rootNode);
//		System.out.println(accumulator);
		return accumulator;
		
	}
	
	public List<String> atomicSet(List<Concept> temp) {

		List<String> atomicFeature = new ArrayList<String>();
		List<String> CIntent = new ArrayList<String>();
		atomicFeature.clear();
		for (int i = 0; i < temp.size(); i++) {
			CIntent = temp.get(i).getIntent();

			ConceptUsed.add(temp.get(i).getNumber());
			
				for (int j = 0; j < CIntent.size(); j++) {
					
					atomicFeature.add(CIntent.get(j));
					}
				}

		System.out.println(atomicFeature);
		return atomicFeature;
	}
	
	public List<Concept> DFS_VisitAtomic(Concept Co) {

		List<Concept> AllIntent = new ArrayList<Concept>();
		
		if (!Co.getNumber().equals(TopConceptNumber) && Co.getIntent().size() >= 2) {
			
		theStack.push(Co); // push it
		
		while (!theStack.isEmpty()) {    // until stack empty,
			
			int v = getAdjUnvisitedVertex(theStack.peek());
			if (v == -1)         // if no such vertex,
				theStack.pop();
			
			else { 
				
				AllFeatureLatticeConcepts.get(v).wasVisited = true; 
 				if(!AllFeatureLatticeConcepts.get(v).getNumber().equals(TopConceptNumber) && AllFeatureLatticeConcepts.get(v).getIntent().size() >= 2){
 				AllIntent.clear();
 				AllIntent.add(AllFeatureLatticeConcepts.get(v));
// 				System.out.println(AllFeatureLatticeConcepts.get(v).getIntent());
 				theStack.push(AllFeatureLatticeConcepts.get(v));
// 				theStack.pop();
 				}
 				theStack.pop();

 				
			}
			}
			
		} // end while

		// stack is empty, so we're done
		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++) {// reset flags
			AllFeatureLatticeConcepts.get(j).wasVisited = false;
		} // end dfs
		
//		System.out.println(AllIntent);
		
		return AllIntent;

	}
    
	public List<Concept> DFS_Visit(Concept Co) {

		List<Concept> AllIntent = new ArrayList<Concept>();

		theStack.push(Co); // push it

		while (!theStack.isEmpty()) { // until stack empty,

			// get an unvisited vertex adjacent to stack top
			int v = getAdjUnvisitedVertex(theStack.peek());
			if (v == -1) // if no such vertex,
				theStack.pop();
			else // if it exists,
			{
				AllFeatureLatticeConcepts.get(v).wasVisited = true; // mark it
				// System.out.println(AllFeatureLatticeConcepts.get(v).ConceptName);
				// displayVertex(v); // display it
				AllIntent.add(AllFeatureLatticeConcepts.get(v));
				theStack.push(AllFeatureLatticeConcepts.get(v)); // push it
			}
		} // end while

		// stack is empty, so we're done
		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++) {// reset flags
			AllFeatureLatticeConcepts.get(j).wasVisited = false;
		} // end dfs
		return AllIntent;

	}
	
	public int getAdjUnvisitedVertex(Concept t) {
		int v;
		v = hm.get(t);

		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++)

		{
			if (FeatureAdjacencyMatrix[v][j] == 1 && AllFeatureLatticeConcepts.get(j).wasVisited == false)
			       return j;
		}
		return -1;

	}


//	=============================================================		
	
	public void FM() throws IOException {
		
		int clusterNumber=1;
		int AR_counter = 1;
		String MR="Base";
        String AR="AND";
        
		@SuppressWarnings("rawtypes")
		Iterator iterator;
		iterator = rootFeature(temp).iterator();
		  while (iterator.hasNext()) {
			  root1=(String) iterator.next();}
		   
		BufferedWriter bw = new BufferedWriter(new FileWriter("FM dot/FM.dot"));
		
		bw.write("digraph G  { " + "\n");
		bw.write("graph [bgcolor=gray97];"+ "\n");
		bw.write("subgraph cluster_"+clusterNumber+"{"+ "\n");
	 	++clusterNumber;
//	     ============================================================================================================= "root"
		
		bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // abstract
		
		bw.write("subgraph cluster_"+clusterNumber+"{"+ "\n");
	 	++clusterNumber;
	 	bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=red, fillcolor=yellow1];"+ "\n"); // abstract
		bw.write("\""+root1+"\""+";" + "\n");
		
//	     ============================================================================================================= "mandatory"
		
		if (mandatory.size()>0){
			
		bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=red, fillcolor=yellow1];"+ "\n"); // abstract
		
		for(int i =0; i<1;i++){
		bw.write("\"" + root1 + "\"" + "->" + "\"" + MR + "\""+ ":n[arrowhead=\"dot\"];");
		}
		
		bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // concreate
		
	    for (int jj =0; jj<mandatory.size(); jj++) {
	    bw.write("\"" + MR + "\"" + "->" + "\"" + mandatory.get(jj) + "\""+ ":n[arrowhead=\"box\"];");}
		}
		
		bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // concreate
	
//	     ============================================================================================================= "atomic"
	
		for (int i = 0; i < FeatureConceptList.size(); i++) {

			if (!FeatureConceptList.get(i).getNumber().equals(TopConceptNumber) && FeatureConceptList.get(i).getIntent().size()>=2){
				
			atomicSetFeature.addAll(FeatureConceptList.get(i).getIntent());
			
			if(!atomicSetFeature.isEmpty())
			bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=red, fillcolor=yellow1];"+ "\n"); // abstract
			bw.write("\"" + root1 + "\"" + "->" + "\"" + AR + "_"+ AR_counter + "\"" + ":n[arrowhead=\"odot\"];" + "\n");	
			System.err.println("Atomic Set of Features : " + atomicSetFeature);
			
			for(int ii = 0; ii < atomicSetFeature.size(); ii++){

			bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // concreate
		
			bw.write("\"" + AR + "_" + AR_counter + "\"" + "->" + "\""+ atomicSetFeature.get(ii) + "\""+ ":n[arrowhead=\"box\"];" + "\n");

			}
			AR_counter++;
			atomicSetFeature.clear();
		 }
		}
		
//	     ============================================================================================================= " * Xor "
		
	    List<List<String>> lists = new ArrayList<List<String>>();

	    
		for (int i = 0; i < FeatureConceptList.size(); i++) {

			tempXor = DFS(FeatureConceptList.get(i));
			XorFeature = Xor(tempXor);
			XorFeature.remove("");
			if (!XorFeature.isEmpty()) {
				System.out.println("The Lattice disjoint path:" + XorFeature);
//				System.out.println(XorFeature);
				lists.add(XorFeature);
				numofPaths.add("1");;
				
		}
			
			}

//		System.err.println(numofPaths);
//		alternative.addAll(getCommonElements(lists));
//		System.out.println(alternative + "    ");
		
//		System.out.println(getUniqueElements(lists) + "    ");
//		System.out.println(getCommonElements(lists) + "    ");
	    
		List<Set<String>> key=getUniqueElements(lists); 
//		System.err.println(key);
		
		for (int in = 0; in < key.size(); in++){
			alternative.addAll(key.get(in));
		}

//		System.out.println(numofPaths.size());
		if(numofPaths.size()>1){
		System.err.println("The Xor Features :" + alternative);}

		bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=red, fillcolor=yellow1];"+ "\n"); // abstract
		
		if(alternative.size()>1 && numofPaths.size()>1){
//			bw.write("\"" + root1 + "\"" + "->" + "\"" + "Exclusive-or" + "_"+ xorCunter + "\"" + ":n[arrowhead=\"odot\"];" + "\n");
			bw.write("\"" + root1 + "\"" + "->" + "\"" + "XOR" + "\"" + ":n[arrowhead=\"dot\"];" + "\n");
			bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // concreate
			for(int i=0; i<alternative.size(); i++){
				Xor.add(alternative.get(i));
//				bw.write("\"" + "Exclusive-or" + "_" + xorCunter + "\"" + "->" + "\""+ alternative.get(i) + "\""+ ":n[arrowhead=\"invodot\"];" + "\n");
				bw.write("\"" + "XOR" + "\"" + "->" + "\""+ alternative.get(i) + "\""+ ":n[arrowhead=\"invodot\"];" + "\n");

			}
		}

//            ==================================================================== The XOR Concept Number
		Concept a;
		
		for (int i = 0; i < FeatureConceptList.size(); i++) {
           a = FeatureConceptList.get(i);
			for (int ii = 0; ii < Xor.size(); ii++) {
				if (a.getIntent().contains(Xor.get(ii))) {
				  XORConceptUnUsed.add(a.getNumber());
				  XORConceptName.addAll(a.getIntent());
				  System.out.println("The XOR Concept Number = " + a.getNumber() + " ==> " + a.getIntent());	
			}
			}
			
		
		}
		
//		  System.out.println(Xor);
		
//	     ============================================================================================================= " * Optional  "
		
		  List<List<String>> listsOptional = new ArrayList<List<String>>();
		  List<String> Atomic_concept_number_as_List = new ArrayList<String>();
		  String Atomic_concept_number_ = null;
			for (int i = 0; i < FeatureConceptList.size(); i++) {

				if(FeatureConceptList.get(i).getIntent().size()>1){
			    	Atomic_concept_number_ = FeatureConceptList.get(i).getCoName().substring(FeatureConceptList.get(i).getCoName().lastIndexOf("_") + 1);
			    	Atomic_concept_number_as_List.add(Atomic_concept_number_);
//			    	System.out.print(Atomic_concept_number_as_List);
			    	}
			    	
				
				tempXor = DFS(FeatureConceptList.get(i));
				
				XorFeature = Xor(tempXor);
				
				if (!XorFeature.isEmpty()) {
					listsOptional.add(XorFeature);
//					System.out.println(getUniqueElements(listsOptional));
					List<Set<String>> keyyy=getUniqueElements(listsOptional);
					
					for (int in = 0; in < keyyy.size(); in++){
						Optional2.removeAll(keyyy.get(in));
						Optional2.addAll(keyyy.get(in));
//						System.out.println(Optional2);
					}
				  }
				}
			
			Optional2.removeAll(Xor);
//			Optional2.removeAll(Optional);

			for (int in = 0; in < Optional2.size(); in++){
				Optional2.remove("");
				}   
		
			if(Optional2.size()>0){
			System.err.println("The Optional Features :" + Optional2 );
		    bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=red, fillcolor=yellow1];"+ "\n"); // abstract
//			bw.write("\"" + root1 + "\"" + "->" + "\"" + "Optional_Features" + "_"+ Optional_Features + "\"" + ":n[arrowhead=\"odot\"];" + "\n");
			bw.write("\"" + root1 + "\"" + "->" + "\"" + "OR" + "\"" + ":n[arrowhead=\"odot\"];" + "\n");
		    bw.write("node [shape=box, width=0.7, height=0.3,style=filled, color=blue, fillcolor=yellow1];"+ "\n"); // concreate
			
			for (int in = 0; in < Optional2.size(); in++){
				if(!Optional2.get(in).isEmpty()) {
//				bw.write("\"" + root1 + "\"" + "->" + "\""+ Optional2.get(in) + "\""+ ":n[arrowhead=\"odiamond\"];" + "\n");
//				System.err.println(Optional2.get(in));
				bw.write("\"" + "OR"  + "\"" + "->" + "\""+ Optional2.get(in) + "\""+ ":n[arrowhead=\"odiamond\"];" + "\n");
				}
			}
		}
			
//		 ============================================================================================================= " * Requires "	
			
		    List<String> rrrr = new ArrayList<String>();
		    List<String> requireCTC = new ArrayList<String>();
		    String concept_number_;
		    String Top_concept_number_ = null;

		    for (int i = 0; i < FeatureConceptList.size(); i++) { 
		    	
		    	if(FeatureConceptList.get(i).getNumber().equals(TopConceptNumber)){
		    	Top_concept_number_ = FeatureConceptList.get(i).getCoName().substring(FeatureConceptList.get(i).getCoName().lastIndexOf("_") + 1);}
		    	
		    	if(FeatureConceptList.get(i).getIntent().size()>1){
		    	Atomic_concept_number_ = FeatureConceptList.get(i).getCoName().substring(FeatureConceptList.get(i).getCoName().lastIndexOf("_") + 1);
		    	Atomic_concept_number_as_List.add(Atomic_concept_number_);
//		    	System.out.print(Atomic_concept_number_as_List);
		    	}
		    	
		    	if(!FeatureConceptList.get(i).getIntent().contains("") && FeatureConceptList.get(i).getIntent().size()<2
		    			&& !FeatureConceptList.get(i).getNumber().equals(TopConceptNumber)) {
		    	
		    	rrrr= getNearby(FeatureConceptList.get(i));
		    	
//		    	System.out.print(FeatureConceptList.get(i).getCoName() +" " + " ======>> ");
		    	
		    	concept_number_ = FeatureConceptList.get(i).getCoName().substring(FeatureConceptList.get(i).getCoName().lastIndexOf("_") + 1);

		    	for(int ii = 0; ii < rrrr.size(); ii++) {
		    		rrrr.remove(concept_number_);
		    		rrrr.remove(Top_concept_number_);
		    		rrrr.remove(Atomic_concept_number_);
		    		rrrr.removeAll(Atomic_concept_number_as_List);
//		    		if(rrrr.size()>0)
//		    	    System.out.print(rrrr.get(ii) + " ");
		        }
//		    	System.out.println();
		    	
		    	for(int iii = 0; iii < rrrr.size(); iii++) {
		    	String numberConcept3333 = rrrr.get(iii);
		    	int iiii = Integer.parseInt(numberConcept3333);
		    	String leftofRe=FeatureConceptList.get(i).getIntent().toString();
		    	String rightofRe=FeatureConceptList.get(iiii).getIntent().toString();
		    	
		    	System.out.println("The requires CTC : " + leftofRe.replace("[", "").replace("]", "")  + "  " + " ==== " + 
		    			rightofRe.replace("[", "").replace("]", ""));
		    	
		    	bw.write("\"" + leftofRe.replace("[", "").replace("]", "") + "\"" + "->" + "\""+ rightofRe.replace("[", "").replace("]", "") + "\""+ ":n[color=\"red\",label=\"Requires\"];" + "\n");
		    	}
		    	
		        }

		    }

//	     ============================================================================================================= " * Excludes "
		  
		List<Set<String>> keyExclude = null;
		List<List<String>> listsExclude = new ArrayList<List<String>>();

		List<Concept> exclude = new ArrayList<Concept>();
		List<String> excludeFeatures = new ArrayList<String>(); 
		List<String> left = new ArrayList<String>(); 
		List<String> right = new ArrayList<String>(); 
	
		listsExclude.add(Optional2);
		
		
		for (int i = 0; i < FeatureConceptList.size(); i++) {

			exclude = DFS_Exclude(FeatureConceptList.get(i));
			excludeFeatures = excludeFea(exclude);

			if (!excludeFeatures.isEmpty()) {
				
				listsExclude.add(excludeFeatures);
				keyExclude=getUniqueElements(listsExclude); 
				
				for(int nnn=0; nnn < FeatureConceptList.size(); nnn++){
				for(int nn=0; nn < keyExclude.size(); nn++){
					if (keyExclude.get(nn).isEmpty())
					keyExclude.remove(nn);
					}
				}
				
					if (keyExclude.size() >= 2){
						      left.addAll(keyExclude.get(0));
						      right.addAll(keyExclude.get(1));
					}
					int z=0;
					for(int yy=0; yy < left.size(); yy++){
						System.err.println("The excludes CTC : " + left.get(yy) +" < === > "+ right.get(z)); 
						bw.write("\"" + left.get(yy)+ "\"" + "->" + "\""+  right.get(z) + "\""+ ":n[style=\"dashed\", color=\"blue\",label=\" Excludes\", dir=\"both\"];" + "\n");
					}

			}
			left.clear();
			right.clear();
			excludeFeatures.clear();

		}

		
//        bw.write("\"" + keyExclude.get(0)+ "\"" + "->" + "\""+ keyExclude.get(1) + "\""+ ":n[style=\"dashed\", color=\"deeppink1\",label=\" Excludes\", dir=\"both\"];" + "\n");
//        System.err.println("The excludes feature : " + keyExclude.get(0) +" < === > "+ keyExclude.get(1)); 	
		
		
//		============================================================================================================= " * Excludes "
		  
		bw.write("\t" +"label = \"Feature Model\";"+"\n");
		bw.write("\t" +"color=green3;");
	 	bw.write("\t" +"}");
     
//	     ============================================================================================================= "Legend"
	   
	    bw.write("\n");	 
	 	bw.write("subgraph cluster_"+clusterNumber+"{" +"\n"+"\n");
	 	++clusterNumber;
	 	bw.write("\t" + "subgraph cluster_"+clusterNumber+"{" +"\n");
	 	bw.write("\t" +"\"Abstract\":n[fillcolor=\"yellow1\", color=\"red\"];" +"\n");
	 	bw.write("\t" +"\"Concrete\":n[fillcolor=\"yellow1\", color=\"blue\"];" +"\n");
	 	bw.write("\t" +"\"                \"->\"Mandatory\":n[arrowhead=\"dot\"];" +"\n");
	 	bw.write("\t" +"\"             \"->\"Optional\":n[arrowhead=\"odot\"];" +"\n");
	 	bw.write("\t" +"label = \"Features:\";"+"\n");
	 	bw.write("\t" +"color=blue;"+"\n");
	 	bw.write("\t" +"}"+"\n");
	 	++clusterNumber;
		bw.write("\t" + "subgraph cluster_"+clusterNumber+"{" +"\n");
	 	bw.write("\t" +"edge [dir=none]\"F1 \"->\"F2 \"[style=\"dashed\", color=\"blue\",label=\" Excludes\", dir=\"both\"];" +"\n");
	 	bw.write("\t" +" edge [dir=forward]\"F1\"->\"F2\"[color=\"red\",label=\" Requires\"];" +"\n");
	 	bw.write("\t" +"label = \"Cross-tree Constraints:\";");
	 	bw.write("\t" +"color=blue;");
	 	bw.write("\t" +"}");
	 	++clusterNumber;
	 	bw.write("\t" + "subgraph cluster_"+clusterNumber+"{" +"\n");
	 	bw.write("\t" +"\"Or\" ->\"   \":n[arrowhead=\"odiamond\"];" +"\n");
	 	bw.write("\t" +"\"Or\" ->\"     \":n[arrowhead=\"odiamond\"];" +"\n");
	 	bw.write("\t" +"\"Xor\"->\"      \":n[arrowhead=\"invodot\"];" +"\n");
		bw.write("\t" +"\"Xor\"->\"       \":n[arrowhead=\"invodot\"];" +"\n");
		bw.write("\t" +"\"And\"->\"        \":n[arrowhead=\"box\"];"+"\n");
		bw.write("\t" +"\"And\"->\"         \":n[arrowhead=\"box\"];"+"\n");
	 	bw.write("\t" +"label = \"Group of Features:\";"+"\n");
	 	bw.write("\t" +"color=blue;"+"\n");
	 	bw.write("\t" +"}"+"\n");
	 	bw.write("\t" +"label = \"Legend:\";"+"\n");
	 	bw.write("\t" +"color=red"+"\n");
	 	bw.write("}"+"\n");
		bw.write("}"+"\n");
		bw.write("}");
	    bw.close();
	   
//	     ============================================================================================================= "Legend"
	}


    public List<String> Xor (List<Concept> temp) {

	List<String> accumulator = new ArrayList<String>();
	List<String> CIntent = new ArrayList<String>();

	for (int i = 0; i < temp.size(); i++) {
//		System.err.println(temp.get(i).getNumber());
		if(!temp.get(i).equals(TopConceptNumber) && temp.get(i).getIntent().size() == 1){
			CIntent = temp.get(i).getIntent();
			for (int j = 0; j < CIntent.size(); j++) {
					accumulator.add(CIntent.get(j));
			}
		}
	}
//	System.err.println(accumulator);
	return accumulator;
	
	}

    public List<Concept> DFS (Concept Co) {            //        xor
	    
		List<Concept> AllIntentA = new ArrayList<Concept>();
		
//		List<String> temp = new ArrayList<String>();
//		List<String> newXor= new ArrayList<String>();

		for(int tt=0; tt<BottomConcept.size();tt++){
			
		if(Co.getNumber().equals(BottomConcept.get(tt))){
			
		theStack.push(Co); 
		
		while (!theStack.isEmpty()) {   
			int v = getAdjUnvisitedVertex(theStack.peek());
//			System.err.print(v);
			if (v == -1) 
				theStack.pop();	
			else {  
				AllFeatureLatticeConcepts.get(v).wasVisited = true; 
				if(!AllFeatureLatticeConcepts.get(v).getNumber().equals(TopConceptNumber) && AllFeatureLatticeConcepts.get(v).getIntent().size() == 1){
//				xor.addAll(AllFeatureLatticeConcepts.get(v).getIntent());
				AllIntentA.add(AllFeatureLatticeConcepts.get(v));
//				System.out.println(AllFeatureLatticeConcepts.get(v).getIntent());
				}
				theStack.push(AllFeatureLatticeConcepts.get(v)); 
			}
		} 
		
		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++) {// reset flags
			AllFeatureLatticeConcepts.get(j).wasVisited = false;} // end dfs
		} }
		
//		System.out.println(getUniqueElements(lists));

		return AllIntentA;
	}

    public List<Concept> DFS_Exclude(Concept Co) {

 		List<Concept> AllIntentA = new ArrayList<Concept>();
 		List<String> tempExclude = new ArrayList<String>();
 		
// 		List<String> newXor= new ArrayList<String>();

 		for(int tt=0; tt<BottomConcept.size();tt++){
 			
 		if(Co.getNumber().equals(BottomConcept.get(tt))){
 			
 		theStack.push(Co); 
 		
 		while (!theStack.isEmpty()) {   
 			int v = getAdjUnvisitedVertex(theStack.peek());
// 			System.err.print(v);
 			if (v == -1) 
 				theStack.pop();	
 			else {  
 				AllFeatureLatticeConcepts.get(v).wasVisited = true; 
 				if(!AllFeatureLatticeConcepts.get(v).getNumber().equals(TopConceptNumber) && AllFeatureLatticeConcepts.get(v).getIntent().size() == 1 && !AllFeatureLatticeConcepts.get(v).getNumber().equals(XORConceptUnUsed)){
 				tempExclude.addAll(AllFeatureLatticeConcepts.get(v).getIntent());
 				AllIntentA.add(AllFeatureLatticeConcepts.get(v));
// 				System.out.println(AllFeatureLatticeConcepts.get(v).getIntent());
 				}
 				theStack.push(AllFeatureLatticeConcepts.get(v)); 
 			}
 		} 
 		
 		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++) {// reset flags
 			AllFeatureLatticeConcepts.get(j).wasVisited = false;} // end dfs
 		} }
 		
// 		System.out.println(tempExclude);

 		return AllIntentA;
	}
	 
    public List<String> excludeFea(List<Concept> temp) {

		List<String> atomicFeature = new ArrayList<String>();
		List<String> CIntent = new ArrayList<String>();

		for (int i = 0; i < temp.size(); i++) {
			
			CIntent = temp.get(i).getIntent();

				for (int j = 0; j < CIntent.size(); j++) {
					
					atomicFeature.add(CIntent.get(j));
					atomicFeature.remove("");
//					atomicFeature.removeAll(XORConceptName);
				}

			}
		
//		System.out.println(atomicFeature);
		
		return atomicFeature;
    	
     }

    public static <T> List<Set<T>> getUniqueElements(Collection<? extends Collection<T>> collections) {
	List<Set<T>> allUniqueSets = new ArrayList<Set<T>>();
    for (Collection<T> collection : collections) {
       Set<T> unique = new LinkedHashSet<T>(collection);
       allUniqueSets.add(unique);
       for (Collection<T> otherCollection : collections) {
          if (collection != otherCollection) {
             unique.removeAll(otherCollection);
          }
       }
   }
    return allUniqueSets;
    }

    public static <T> Set<T> getCommonElements(Collection<? extends Collection<T>> collections) {
    Set<T> common = new LinkedHashSet<T>();
    if (!collections.isEmpty()) {
       Iterator<? extends Collection<T>> iterator = collections.iterator();
       common.addAll(iterator.next());
       while (iterator.hasNext()) {
          common.retainAll(iterator.next());
       }
    }
    return common;
    }
    
    public List<String>  getNearby (Concept t) {
    	
    	List<String> ConceptNumber = new ArrayList<String>();
    	int v;
		v = hm.get(t);
		String number = null;
		ConceptNumber.clear();
		for (int j = 0; j < FeatureAdjacencyMatrix.length; j++) {
			
			if (FeatureAdjacencyMatrix[v][j] == 1){

				number=Integer.toString (j);
//			    System.out.println(number);
				ConceptNumber.add(number);	}
				else
				number="r";	
//				ConceptNumber.add("rr");
		}

//		System.out.println(ConceptNumber);
		return ConceptNumber;
		
	} 

    
}