package process;

import java.util.ArrayList;
import java.util.List;

public class Concept {
	
	public String LatticeName; // LatticeName (e.g. Classz)
	public String ConceptName; // ConceptName (e.g. Concept_1)
	public String ConceptNumber; // ConceptNumber (e.g. 123456789)
	public List<String> CurrentIntent;
	public List<String> CurrentExtent;
	public boolean wasVisited;
	public String TopConceptNumber;

	public Concept() { // constructor

		LatticeName = null;
		ConceptName = null;
		ConceptNumber = null;
		TopConceptNumber= null;
		CurrentIntent = new ArrayList<String>();
		CurrentExtent = new ArrayList<String>();
		wasVisited = false;

	}

	public String getTopConceptNumber() {
		return TopConceptNumber;
	}

	public void setTopConceptNumber(String topConceptNumber) {
		TopConceptNumber = topConceptNumber;
	}

	public void setLaName(String LaName) {

		this.LatticeName = LaName;

	}

	public String getLaName()

	{
		return this.LatticeName;

	}

	public void setCoName(String name) {

		this.ConceptName = name;

	}

	public void setCoNumber(String num) {

		this.ConceptNumber = num;

	}

	public String getCoName()

	{

		return this.ConceptName;

	}

	public String getNumber()

	{
		return this.ConceptNumber;

	}

	public void setIntent(List<String> in) {

		this.CurrentIntent = in;

	}

	public void setExtent(List<String> ex) {

		this.CurrentExtent = ex;

	}

	public List<String> getIntent()

	{
		return this.CurrentIntent;

	}

	public List<String> getExtent()

	{

		return this.CurrentExtent;
	}

} // end class Vertex

