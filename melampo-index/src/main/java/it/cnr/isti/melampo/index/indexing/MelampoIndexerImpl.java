package it.cnr.isti.melampo.index.indexing;

import it.cnr.isti.melampo.tools.Tools;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.index.CorruptIndexException;

public class MelampoIndexerImpl {
	private static String[][] INDICES;
	private HashMap<String, MelampoIndexer> indexImpl;
	private MelampoIndexer index;

	public void openIndices(File propertyDir) throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, VIRException {

		INDICES = Tools.getAllOrderedProperties(new File(propertyDir,
				"indices.properties"));
		indexImpl = new HashMap<String, MelampoIndexer>();
		for (int i = 0; i < INDICES.length; i++) {
			indexImpl.put(
					INDICES[i][0],
					(MelampoIndexer) Class.forName(
							INDICES[i][1].split(",")[0].trim()).newInstance());
			indexImpl.get(INDICES[i][0]).OpenIndex(
					new File(propertyDir, "index.properties"));
		}
	}

	public void addDocument(String indexId, int docIndex) throws CorruptIndexException, IOException, VIRException {
		index = null;
		if ((index = indexImpl.get(indexId)) != null) {
			//index.addDocument(docIndex);

		}
	}

	public void closeIndex(String indexId) throws IOException {
		index = null;
		if ((index = indexImpl.get(indexId)) != null) {
			index.closeIndex();
		}
	}
	
	
}
