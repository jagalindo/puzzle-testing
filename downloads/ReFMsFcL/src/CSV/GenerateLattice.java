package CSV;

import java.io.IOException;

import com.googlecode.erca.framework.algo.NaiveGSH;
import com.googlecode.erca.framework.io.in.RcftParser;
import com.googlecode.erca.framework.io.out.LatticeToDot;
import com.googlecode.erca.rcf.FormalContext;
import com.googlecode.erca.rcf.RelationalContextFamily;


public class GenerateLattice {

	public  void Lattice() {

		try {
			RelationalContextFamily rcf = RcftParser.fromFile("rcft/FC.rcft");
			int i=0;
			for (FormalContext fc : rcf.getFormalContexts())
			{
				NaiveGSH gshCreator= new NaiveGSH(fc);
				gshCreator.generateGSH();
				LatticeToDot l2d = new LatticeToDot(gshCreator.getConceptLattice());
				l2d.generateCode();
				l2d.toFile("Lattice dot/lattice.dot");
			}

		} catch (IOException e) {
	
			e.printStackTrace();
		}

	}
}
