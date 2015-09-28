package CSV;
import java.io.IOException;

import com.googlecode.erca.framework.algo.NaiveGSH;
import com.googlecode.erca.framework.io.in.CsvContextParser;
import com.googlecode.erca.framework.io.out.LatticeToDot;
import com.googlecode.erca.rcf.FormalContext;

public class FromCSVFile {
    public  void CSVFile() {
        try {
           FormalContext fc = CsvContextParser.fromFile("FM/Ziadi.csv");
            int i=0;
                NaiveGSH gshCreator= new NaiveGSH(fc);
                gshCreator.generateGSH();
                LatticeToDot l2d = new LatticeToDot(gshCreator.getConceptLattice());
                l2d.generateCode();
                l2d.toFile("FM/Ziadi"+(i++)+".dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
