package it.cnr.isti.melampo.index.searching;

import it.cnr.isti.melampo.tools.Tools;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

public class MelampoSearcherHub {
	private String[][] indices;
	private HashMap<String, MelampoSearcher> indexImpl = new HashMap<String, MelampoSearcher>();
	private String[][] queryResults;
	private MelampoSearcher index;
	private ParallelSearcher parallelSearcher;
	private boolean isParallel;
	
	//to avoid duplicated index instances
	private HashMap<String, String> indexInstances;

	public void openIndices(File propertyDir) throws VIRException {
			try {
				indices = Tools.getAllOrderedProperties(new File(propertyDir,
						"indices.properties"));
				indexImpl = new HashMap<String, MelampoSearcher>();
				indexInstances = new HashMap<String, String>();
				for (int i = 0; i < indices.length; i++) {
					if (indexInstances.containsKey(indices[i][1])) {
						MelampoSearcher tmp = indexImpl.get(indexInstances.get(indices[i][1]));
						indexImpl.put(indices[i][0], tmp);
					} else {
						indexImpl.put(indices[i][0],
								(MelampoSearcher) Class.forName(indices[i][1])
										.newInstance());
						indexImpl.get(indices[i][0]).OpenIndex(
								new File(propertyDir, indices[i][0] + ".properties"));
						indexInstances.put(indices[i][1], indices[i][0]);
					}
				}
				if (indexInstances.size() > 1) {
					parallelSearcher = new ParallelSearcher(indexImpl);
				}
			} catch (Exception e) {
				throw new VIRException(VIRException.MESSAGE_OPEN_INDICES, e);
			}
	}

	/**
	 * 
	 * @param values
	 * @param fields
	 * @param isQueryID
	 * @throws VIRException
	 */
	public void query(List<String> values, List<String> fields,
			boolean isQueryID) throws VIRException {
		queryResults = null;
		isParallel = false;
		List<String> queriesList = new ArrayList<String>();
		List<String> fieldsList = new ArrayList<String>();
		MelampoSearcher indexCheck = null;
		String query;
		try {
			for (int i = 0; i < fields.size(); i++) {
				index = null;
				if ((index = indexImpl.get(fields.get(i))) != null) {
					query = index.prepareQuery(values.get(i), fields.get(i), isQueryID);
					queriesList.add(query);
					fieldsList.add(fields.get(i));
					//test if they are the same index instance
					if (indexCheck != null && index != indexCheck) {
						isParallel = true;
					}
					indexCheck = index;
				}
			}
			if (isParallel) {
				parallelSearcher.query(queriesList, fieldsList);
			} else {
				//query one field
				index.query(fields.get(0));
				index.reorderResults();
			}
		} catch (ParseException e) {
			
			throw new VIRException("Cannot parse query for fields: " + fields + "and Values: "+ values, e);
		} catch (IOException e) {
			throw new VIRException("Cannot search index! ", e);
		}
	}

	public String[][] getResults(int startFrom, int numElements)
			throws IOException {
		if (isParallel) {
			queryResults = parallelSearcher.getResults(startFrom, numElements);
		} else {
			queryResults = index.getResults(startFrom, numElements);
		}
		return queryResults;
	}
}
