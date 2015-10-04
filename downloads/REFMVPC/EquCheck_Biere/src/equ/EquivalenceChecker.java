/**
 * @author Mauricio
 *
 */
package equ;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.LecteurDimacs;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import constraints.BooleanVariable;
import constraints.BooleanVariableInterface;
import constraints.CNFClause;
import constraints.CNFFormula;
import constraints.CNFLiteral;
import fm.FeatureModel;
import fm.FeatureModelException;
import fm.XMLFeatureModel;

/**
 * This class provides functions to check whether two feature models are equivalent. 
 * For an explanation why/how please refer to my master's thesis.
 * @author eHaslinger
 *
 */
public class EquivalenceChecker {
	/**
	 * </blockquote> No olvides Cambiar featureModelFileStr. Puedes intentar
	 * usar alguna GUI o leerlo como un argumento en la línea de comandos. Este
	 * ejemplo lo hice creando un proyecto general con una carpetita dentro
	 * llamada "SmallExampleOfPaper" que contiene el xml que grabas desde el
	 * SPLOT. <blockquote>
	 */


	public static void main(String[] args) {
		boolean equal = EquivalenceChecker.checkEquality(
				"..//SmartHome//SmallExampleOfPaper//car_fm1.xml",
				"..//SmartHome//SmallExampleOfPaper//car_fm.xml", "test");
		
		if(equal){
			System.out.println("\n\n\n The two models are equivalent.");
		}else{
			System.out.println("\n\n\n The two models are not equivalent.");
		}
	}
	

	private static boolean checkFormula(String destination) {
		boolean result = false;
		ISolver solver = SolverFactory.newDefault();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new LecteurDimacs(solver);

		try {
			IProblem problem = reader.parseInstance(destination);//"..//SmartHome//SmallExampleOfPaper//mycnf.txt");
			result = problem.isSatisfiable();
//			if (result) {
//				System.out.println("Satisfiable !");
//				System.out.println(reader.decode(problem.model()));
//			} else {
//				System.out.println("Unsatisfiable !");
//			}
		} catch (FileNotFoundException e) { 
			System.out.println(e.getStackTrace());
		} catch (ParseFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result;
	}


	public static CNFClause clone(CNFClause cnfC) {
		CNFClause clause = new CNFClause();
		for (CNFLiteral cnfL : cnfC.getLiterals()) {
			CNFLiteral l = new CNFLiteral(cnfL.getVariable(), cnfL.isPositive());
			clause.addLiteral(l);
		}
		return clause;
	}

	public static CNFFormula clone(CNFFormula cnfF) {
		CNFFormula formula = new CNFFormula();
		for (CNFClause cnfC : cnfF.getClauses()) {
			CNFClause c = clone(cnfC);
			formula.addClause(c);
		}
		return formula;
	}

	public static boolean checkEquality(String model1, String model2, String testDirectory) {
		try {
			FeatureModel featureModel = new XMLFeatureModel(model1,
					XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
			featureModel.loadModel();
			CNFFormula cnf1 = featureModel.FM2CNF();

			//System.out.println("Formula 1:");
			//System.out.println(cnf1 +"\n\n\n");
			
			featureModel = new XMLFeatureModel(model2,
					XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
			featureModel.loadModel();
			CNFFormula cnf2 = featureModel.FM2CNF();

			//System.out.println("Formula 2:");
			//System.out.println(cnf2 +"\n\n\n");
			
			boolean satisfyable = false;
			int i = 0;
			satisfyable = checkEqualityAux(cnf1, cnf2, testDirectory, i, satisfyable);
			i+=cnf1.getClauses().size();
			satisfyable = checkEqualityAux(cnf2, cnf1, testDirectory, i, satisfyable);			
			return !satisfyable;
		} catch (FeatureModelException e) {
			e.printStackTrace();
		}
		return false;
	}
	private static boolean checkEqualityAux(CNFFormula cnf1, CNFFormula cnf2, String testDirectory, int i, boolean satisfyable){		
		for (CNFClause cnfC2 : cnf2.getClauses()) {
			if(satisfyable){
				break;
			}
			CNFFormula cnfToCheck = clone(cnf1);
			CNFClause cnfClauseToAdd;
			for (CNFLiteral l : cnfC2.getLiterals()) {
				cnfClauseToAdd= new CNFClause();
				cnfClauseToAdd.addLiteral(new CNFLiteral(l.getVariable(),!l.isPositive()));
				cnfToCheck.addClause(cnfClauseToAdd);
			}
			//System.out.println("Formula to check #"+i+":");
			//System.out.println(cnfToCheck +"\n");
			writeCNFToFile(cnfToCheck, testDirectory+File.separator+i+".txt");
			satisfyable = checkFormula(testDirectory+File.separator+i+".txt");
			if(satisfyable){
				break;
			}
			i++;
		}
		
		return satisfyable;
	}

	public static void writeCNFToFile(CNFFormula cnfF, String file) {
		try {
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("c");
			out.newLine();
			out.write("c CNF-Formula to check equivalence of two feature models");
			out.write("c");
			out.newLine();
			out.write("p cnf " + cnfF.getVariables().size() + " "
					+ cnfF.getClauses().size());
			out.newLine();
			Map<BooleanVariableInterface, Integer> map = new HashMap<BooleanVariableInterface, Integer>();
			int i = 1;
			for (BooleanVariableInterface cnfV : cnfF.getVariables()) {
				//System.out.println(cnfV.getID()+" --> "+i);
				map.put(cnfV, i);
				i++;
			}
			//System.out.println("\n\n");
			for (CNFClause cnfC : cnfF.getClauses()) {
				for (CNFLiteral cnfL : cnfC.getLiterals()) {
					if (!cnfL.isPositive()) {
						out.write("-");
					}
					out.write(map.get(cnfL.getVariable()) + " ");
				}
				out.write("0");
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}