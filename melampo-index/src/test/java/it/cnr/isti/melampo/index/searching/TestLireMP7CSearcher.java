package it.cnr.isti.melampo.index.searching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import it.cnr.isti.melampo.index.BaseIndexTest;
import it.cnr.isti.melampo.index.Parameters;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import org.apache.lucene.queryParser.ParseException;
import org.junit.Test;

public class TestLireMP7CSearcher extends BaseIndexTest {

	LireMP7CSearcher searcher = null;

	@Test
	/**
	 * The index must be created prior to invocation of this test
	 * 
	 * {@link #it.cnr.isti.melampo.index.indexing.TestLireIndexer}
	 */
	public void testSearchById() throws IOException, VIRException,
			ParseException {

		// open index
		openSearcher();
		final int LIMIT = 5;
		long start = System.currentTimeMillis();
		// perform search
		String query = searcher.prepareQuery(TEST_DOC_ID,
				Parameters.LIRE_MP7ALL, true);
		searcher.query();
		String[][] results = searcher.getResults(0, LIMIT);

		// log time effort
		long end = System.currentTimeMillis();
		System.out
				.println("Time spent (secs):" + ((double) end - start) / 1000);

		// log
		System.out.println("For query: " + query);
		//50 pivots used
		assertTrue(query.indexOf("0807^50") >= 0);
		
		// check returned results
		assertNotNull(results);
		assertNotNull(results[0]);

		// check results size
		assertEquals(results.length, LIMIT);

		// check first result same as query
		assertEquals(results[0][1], TEST_DOC_ID);

		// print out same result. Should be the same as the query object
		System.out.println("Id:" + results[0][1]);
		System.out.println("score:" + results[0][0]);

	}

	private void openSearcher() throws IOException, VIRException {
		if (searcher == null) {
			searcher = new LireMP7CSearcher();
			searcher.OpenIndex(getConfigIndexFile());
		}
	}
}
