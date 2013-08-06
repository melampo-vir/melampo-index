package it.cnr.isti.melampo.index.searching;

import it.cnr.isti.melampo.index.Parameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ParallelReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

public class ParallelSearcher {

	private static Object singleton = new Object();
	private static Searcher m_s;
	private ScoreDoc[] m_hits;
	private static PerFieldAnalyzerWrapper m_wrapper;
	private static int m_retrieve = 1000;
	private static HashMap<String, MelampoSearcher> indexImpl;

	public ParallelSearcher(HashMap<String, MelampoSearcher> indexImpl)
			throws IOException {
		synchronized (singleton) {
		if (m_s != null)
			return;
		this.indexImpl = indexImpl;
		IndexReader m_r = new ParallelReader();
		Iterator<String> keys = indexImpl.keySet().iterator();
		while (keys.hasNext()) {
			((ParallelReader) m_r).add(indexImpl.get(keys.next())
					.getIndexReader());
		}
		m_s = new IndexSearcher(m_r);
		System.out.println("Parallel index open");
		}
	}

	public void query(List<String> vals, List<String> flds)
			throws ParseException, IOException {

		Query q = null;

		BooleanClause.Occur[] flags = new BooleanClause.Occur[flds.size()];
		
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_29);
		m_wrapper = new PerFieldAnalyzerWrapper(sa);

		for (int i = 0; i < flds.size(); i++) {
			System.out.println("flds.get("+ i +"): " + flds.get(i));
			MelampoSearcher index = indexImpl.get(flds.get(i));
			flags[i] = index.getOccur();
			m_wrapper.addAnalyzer(flds.get(i), index.getAnalyzer());
		}

		String[] v = new String[vals.size()];
		String[] f = new String[flds.size()];

		q = MultiFieldQueryParser.parse(Version.LUCENE_29, vals.toArray(v),
				flds.toArray(f), flags, m_wrapper);

		System.out.println("Using combined index");

		TopDocs td = m_s.search(q, m_retrieve);
		m_hits = td.scoreDocs;
	}

	public String[][] getResults(int startFrom, int numElements)
			throws IOException {
		String[][] retval = new String[numElements][3];
		for (int i = 0; i < numElements; i++) {
			if (startFrom + i > m_hits.length - 1)
				break;
			float score = m_hits[startFrom + i].score;
			retval[i][0] = ((Float) score).toString();
			Document d = m_s.doc(m_hits[startFrom + i].doc);
			retval[i][1] = d.get(Parameters.IDFIELD);
		}
		return retval;

	}
}
