package it.cnr.isti.melampo.index.searching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import it.cnr.isti.melampo.index.BaseIndexTest;
import it.cnr.isti.melampo.index.Parameters;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.junit.Test;

public class TestMelampoSearcherHub extends BaseIndexTest {

	MelampoSearcherHub searcher = null;

	@Test
	/**
	 * The index must be created prior to invocation of this test
	 * 
	 * {@link #it.cnr.isti.melampo.index.indexing.TestLireIndexer}
	 */
	public void testSearchById() throws IOException, VIRException,
			ParseException {

		//open index
		openSearcher();
		final List<String> docIdList = Arrays.asList(new String[]{TEST_DOC_ID});
		final List<String> fieldList = Arrays.asList(new String[]{ Parameters.LIRE_MP7ALL});
		
		long start = System.currentTimeMillis();
		//perform search
		searcher.query(docIdList, fieldList, true);
		final int LIMIT = 5; 
		String[][] results = searcher.getResults(0, LIMIT);
		
		long end = System.currentTimeMillis();
		//log time effort
		System.out.println("Time spent (secs):" + ((double)end - start) / 1000);
		
		//check returned results
		assertNotNull(results);
		assertNotNull(results[0]);
		
		//check results size
		assertEquals(results.length, LIMIT);
		
		//check first result same as query
		assertEquals(results[0][1], TEST_DOC_ID);
		
		//print out same result. Should be the same as the query object
		System.out.println("Id:" + results[0][1]);
		System.out.println("score:" + results[0][0]);
		
	}

	private void openSearcher() throws IOException, VIRException {
		if (searcher == null) {
			searcher = new MelampoSearcherHub();
			// File test = new File(".");
			// System.out.println(">>>>>> " + test.getAbsolutePath());
			// File indexFolder = getConfiguration().getIndexHomeFolder();

			// log.trace("loading image index from following location: "
			// + indexFolder.getAbsolutePath());
			// // the indices.properties and LIRE_MP7ALL properties
			searcher.openIndices(getIndexConfigFolder());
		}
	}
}
