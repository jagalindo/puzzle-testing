/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package foreverse.afmsynthesis.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import fr.familiar.attributedfm.AttributedFeatureModel;
import fr.familiar.attributedfm.Constraint;
import fr.familiar.attributedfm.Feature;
import fr.familiar.attributedfm.GenericAttribute;
import fr.familiar.attributedfm.Relation;
import fr.familiar.attributedfm.domain.*;

public class AttributedWriter {

	private BufferedWriter writer = null;
	private AttributedFeatureModel fm = null;
	private Collection<String> relationshipsCol = new ArrayList<String>();
	private Collection<String> attributesCol = new ArrayList<String>();
	private Collection<String> constraintsCol = new ArrayList<String>();
	private Collection<Feature> usedFeats = new ArrayList<Feature>();

	public void writeFile(String fileName, AttributedFeatureModel vm)
			throws Exception {

		File file = new File(fileName);
		fm = vm;

		writer = new BufferedWriter(new FileWriter(file));

		generateStringsCols();

		Iterator<String> relColIt = relationshipsCol.iterator();
		Iterator<String> attColIt = attributesCol.iterator();
		Iterator<String> consColIt = constraintsCol.iterator();

		if (relationshipsCol.size() > 1) {
			while (relColIt.hasNext()) {
				writer.write(relColIt.next());
				writer.write("\n");// blank
			}
			writer.write("\n");// blank
		}
		
		if (attributesCol.size() > 1) {
			while (attColIt.hasNext()) {
				writer.write(attColIt.next());
				writer.write("\n");// blank
			}
			writer.write("\n");// blank	
		}
		
		if (constraintsCol.size() > 1) {
			while (consColIt.hasNext()) {
				writer.write(consColIt.next());// el maquina de jesus tenia hecho el
				// recorrido inorden del AST xD
				writer.write("\n");// blank
			}
			writer.write("\n");// blank	
		}
		

		writer.flush();
		writer.close();

	}

	private void generateStringsCols() {

		relationshipsCol.add("%Relationships");
		attributesCol.add("%Attributes");
		constraintsCol.add("%Constraints");

		recursiveWay(fm.getRoot());
		// Ahora las constraints
		Iterator<Constraint> constIt = fm.getConstraints().iterator();
		while (constIt.hasNext()) {
			Constraint cons = constIt.next();
			if (!(cons.toString() == " ")
					|| !constraintsCol.contains(cons.toString() + ";"))
				constraintsCol.add(cons.toString() + ";");
		}

		// rel &attribs
		// las constrainsts aparte// recorrido del ast
	}

	private void recursiveWay(Feature feat) {
		// Pillamos los atributos de la feature
		usedFeats.add(feat);
		Iterator<GenericAttribute> attIt = feat.getAttributes().iterator();
		while (attIt.hasNext()) {

			GenericAttribute att = attIt.next();
			String attLine = feat.getName() + "." + att.getName() + ": ";

			// Pillamos los dominios
			Domain domain = att.getDomain();
			String domainStr = "";
			if (domain instanceof RangeIntegerDomain) {
				domainStr += "Integer";
				Iterator<Range> rangesIt = ((RangeIntegerDomain) domain)
						.getRanges().iterator();
				while (rangesIt.hasNext()) {
					Range range = rangesIt.next();
					domainStr += "[" +range.getMin() + " to " + range.getMax() + "]";
				}
//				domainStr = domainStr.substring(0, domainStr.length() - 1);
			} else if (domain instanceof SetIntegerDomain) {
				domainStr += " [";
				Iterator<Integer> domIt = domain.getAllIntegerValues()
						.iterator();
				while (domIt.hasNext()) {
					domainStr += domIt.next().toString() + ",";
				}
				domainStr = domainStr.substring(0, domainStr.length() - 1);
				domainStr += "]";


			} else if (domain instanceof StringDomain) {
				StringDomain dom = ((StringDomain) domain);

				int domainSize = dom.getAllIntegerValues().size();

				domainStr += "[";
				for (int i = 0; i < domainSize; i++) {
					domainStr += '"' + dom.getValue(i).toString() + '"' + ",";
				}
				domainStr = domainStr.substring(0, domainStr.length() - 1);
				domainStr += "]";

			} else if (domain instanceof ObjectDomain) {
				ObjectDomain dom = ((ObjectDomain) domain);
				if (dom.getObjectValues().size()>0){
				domainStr += "[";

				Iterator<Object> domIt = dom
						.getObjectValues().iterator();
				while (domIt.hasNext()) {
					domainStr += domIt.next().toString() + ",";
				}
				domainStr = domainStr.substring(0, domainStr.length() - 1);
				domainStr += "]";
				}
			}

			
			
				attributesCol.add(attLine + domainStr + ","
						+ att.getDefaultValue() + "," + att.getNullValue()
						+ ";");

			
		}
		// Origens
		Iterator<Relation> relIt = feat.getRelations();
		if (feat.getNumberOfRelations() > 0) {
			String relsStr = feat.getName() + " : ";
			while (relIt.hasNext()) {
				Relation rel = relIt.next();
				Iterator<Feature> destIt = rel.getDestination();

				if (rel.isMandatory() && rel.getNumberOfDestination() == 1) {
					while (destIt.hasNext()) {
						Feature dest = destIt.next();
						relsStr += " " + dest.getName() + " ";

					}
				} else if (rel.isOptional() && rel.getNumberOfDestination() == 1) {
					while (destIt.hasNext()) {
						Feature dest = destIt.next();
						relsStr += "[" + dest.getName() + "] ";

					}
				} else {

					Iterator<Cardinality> cardIt = rel.getCardinalities();
					relsStr += "[";

					while (cardIt.hasNext()) {
						Cardinality card = cardIt.next();
						relsStr += card.getMin() + "," + card.getMax();

					}
					relsStr += "] {";
					while (destIt.hasNext()) {
						Feature dest = destIt.next();
						relsStr += dest.getName() + " ";

					}
					relsStr += "}";

				}

			}
			relationshipsCol.add(relsStr + ";");
			relIt = feat.getRelations();
			while (relIt.hasNext()) {
				Relation rel = relIt.next();

				Iterator<Feature> destIt = rel.getDestination();
				while (destIt.hasNext()) {
					Feature dest = destIt.next();

					if (!usedFeats.contains(dest)) {
						recursiveWay(dest);
						usedFeats.add(dest);
					}
				}
			}
		}

		// Hay que procesar los invariantes
		if (feat.getInvariants().size() > 0) {
			String invStr = feat.getName() + "{\n";
			Iterator<Constraint> invIt = feat.getInvariants().iterator();
			while (invIt.hasNext()) {
				Constraint cons = invIt.next();
				invStr += "\t" + cons.toString() + "\n";
			}
			constraintsCol.add(invStr + "}");
		}

	}
}
