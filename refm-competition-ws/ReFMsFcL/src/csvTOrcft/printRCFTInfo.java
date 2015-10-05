package csvTOrcft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class printRCFTInfo {

	public void printRCFT(String path) throws IOException {

//		String path="ProdConf\\PFs.csv";
//		String path="ProdConf\\wiki.csv";
//		String path="ProdConf\\clock.csv";
		//String path="./ProdConf/m.csv";
//		String path="ProdConf\\Product 100 Feature 27.csv";
		int columns = 0;
		int rows = 0;
		File rcftFile = new File("rcft\\FC.rcft");
		if (!rcftFile.exists()) {
			rcftFile.createNewFile();}

		FileWriter rcft = new FileWriter("rcft\\FC.rcft");
		PrintWriter prcft = new PrintWriter(rcft);
		
		prcft.write("FormalContext Products_Configurations" + "\n");
		
		BufferedReader brAtomic = new BufferedReader(new FileReader(new File(path)));
		BufferedReader brAtomic0 = new BufferedReader(new FileReader(new File(path)));

		String lineAtomic0 = "";

		while ((lineAtomic0 = brAtomic0.readLine()) != null) {
			rows++;
			StringTokenizer st1 = new StringTokenizer(lineAtomic0, ",");
			columns = st1.countTokens();
		}
	
		String lineAtomic = "";
		ArrayList<String[]> rowListAtomic = new ArrayList<String[]>();
		
		while ((lineAtomic = brAtomic.readLine()) !=null) {

			StringTokenizer st = new StringTokenizer(lineAtomic, ",");
			
			
//			System.out.print("|");
			prcft.write("|");
			String[] tokens = lineAtomic.split(" ");
			for(String token : tokens){
//            System.out.print(token.replace(",", "|"));
        	prcft.write(token.replace(",", "|"));
			}
//			System.out.println("|");
			prcft.write("|");
			prcft.write("\n");
		}

		prcft.write("\n");

		prcft.flush();

		prcft.close();

		rcft.close();

		
	}

}
