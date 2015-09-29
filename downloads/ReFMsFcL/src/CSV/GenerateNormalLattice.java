package CSV;
import java.io.IOException;

import com.googlecode.erca.framework.algo.ConceptLatticeGenerator;
import com.googlecode.erca.framework.io.in.CsvContextParser;
import com.googlecode.erca.framework.io.out.LatticeToDot;
import com.googlecode.erca.rcf.FormalContext;

public class GenerateNormalLattice {
    public  void NormalLattice() {
        try {
           FormalContext fc = CsvContextParser.fromFile("Normal Lattice/bankProductsZiadi.csv"); //bankProductsZiadi.csv
            int i=0;
                ConceptLatticeGenerator gshCreator= new ConceptLatticeGenerator(fc);
                gshCreator.generateConceptLattice();
                LatticeToDot l2d = new LatticeToDot(gshCreator.getConceptLattice());
                l2d.generateCode();
                l2d.toFile("Lattice dot/lattice.dot");
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}
