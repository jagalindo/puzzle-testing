package process;

import java.util.ArrayList;
import java.util.List;

public class Lattice {
	
	public String LatticeName;
	public List<Concept> Concepts;
	public String[][] edges;
	public int adjMat[][];

	public Lattice() {
		LatticeName = null;
		Concepts = new ArrayList<Concept>();

	}

	public String getLatticeName() {
		return this.LatticeName;
	}

	public void setLatticeName(String name) {
		this.LatticeName = name;
	}

	public void addConcept(Concept co)

	{
		Concepts.add(co);
	}

	public Concept getConceptByName(String name) {

		for (int i = 0; i < Concepts.size(); i++) {

			if (Concepts.get(i).getCoName().equals(name)) {

				return Concepts.get(i);
			}

			// return temp;

		}

		return null;
	}

	public Concept getConceptByNumber(String no) {
		for (int i = 0; i < Concepts.size(); i++) {

			if (Concepts.get(i).getNumber().equals(no)) {

				return Concepts.get(i);
			}

			// return temp;

		}

		return null;
	}

	public void setConceptList(List<Concept> li) {
		this.Concepts = li;
	}

	public List<Concept> getConceptList() {
		return this.Concepts;
	}

}
