package process;

import java.io.IOException;

import csvTOrcft.printRCFTInfo;

import CSV.FromCSVFile;
import CSV.GenerateLattice;
import CSV.GenerateNormalLattice;
import GraphViz.FMViz;
import GraphViz.LatticeViz;

public class run {

	public static void main(String[] args) throws IOException, ArrayIndexOutOfBoundsException{

		long time1 = System.currentTimeMillis();
		
//	    convert product configurations to formal context
		printRCFTInfo rcft = new printRCFTInfo();  
		rcft.printRCFT();
		
//	    Generate AOC-poset from the rcft formal context	
		GenerateLattice e= new GenerateLattice();
		e.Lattice();
		
//	    Generate Normal Lattice from the CSV formal context	
		GenerateNormalLattice w= new GenerateNormalLattice();
		w.NormalLattice();
		
//	    Generate Normal Lattice from the CSV formal context	
		FromCSVFile j=new FromCSVFile();
		j.CSVFile();
		
//		Visualization of the extracted Lattice
		
//		LatticeViz l = new LatticeViz();
//		l.start();
		
//		read dot file
//		ReadDotFile rd = new ReadDotFile();
//		rd.ReadDotFileMethod("Lattice dot/lattice.dot");
//		rd.ReadDotFileMethod("FM/rr.txt");
//		rd.dfs();
		
//		reverse engineering feature models as dot file
		
//		rd.FM();
		
		long time2 = System.currentTimeMillis();
		
//		Visualization of the extracted feature model
		
//		FMViz p = new FMViz();
//		p.start2();
		
//		Time needed to extract the FM (in ms)	
        System.out.println("Time needed to extract the FM (in ms):" + time2 + "-" + time1 + "=" + (time2 - time1));
      
	}
	
}
