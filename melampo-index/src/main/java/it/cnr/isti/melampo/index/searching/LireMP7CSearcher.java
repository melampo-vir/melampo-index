package it.cnr.isti.melampo.index.searching;

import it.cnr.isti.melampo.index.CosineSimilarity;
import it.cnr.isti.melampo.index.LireObjectFieldAdder;
import it.cnr.isti.melampo.index.Parameters;
import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.melampo.vir.lire.LireSearcher;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.similarity.metric.LireMetric;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LireMP7CSearcher implements MelampoSearcher {

	protected static Version ver = Version.LUCENE_29;

	private static Object singleton = new Object();

	private static IndexSearcher m_sMPG7C;

	private static LireSearcher m_soSearcher;
	private ScoreDoc[] m_hits;

	private static PerFieldAnalyzerWrapper m_wrapper;
	private static int m_retrieve = 100;

	private static LireObjectFieldAdder m_sfaALL;

	private static int m_toppivsQ;
	private static int m_toppivsI;
	private static int m_nPivots;
	private static String m_PivFile;

	private LireObject sq;
	private static IndexReader rMPG7C;

	private Analyzer analyzer = new WhitespaceAnalyzer();
	private String query;

	public void OpenIndex(File propertyFile) throws IOException, VIRException {
		synchronized (singleton) {
			if (m_sMPG7C != null)
				return;

			System.out.println("index conf file: "
					+ propertyFile.getAbsolutePath());
			LireSettings settings = new LireSettings(propertyFile);

			m_soSearcher = new LireSearcher(settings);

			// m_so = m_sreader.getAllSapirObject();

			String indexPath = settings.getLuceneIndexPathLire();
			if (indexPath != null) {
				Directory dir = FSDirectory.open(new File(indexPath));
				rMPG7C = IndexReader.open(dir, true);
				m_sMPG7C = new IndexSearcher(rMPG7C);
				m_sMPG7C.setSimilarity(new CosineSimilarity());
				System.out
						.println("mpeg-7 Pre-Combined using cosine similarity");
				System.out.println(indexPath + " open " + rMPG7C.numDocs()
						+ " docs");
			}

			StandardAnalyzer sa = new StandardAnalyzer(ver);
			m_wrapper = new PerFieldAnalyzerWrapper(sa);
			m_wrapper.addAnalyzer(Parameters.LIRE_MP7ALL, analyzer);

			m_nPivots = settings.getnPivots();
			m_PivFile = settings.getPivotsPath();

			m_sfaALL = new LireObjectFieldAdder(new LireMetric());
			m_sfaALL.loadBinPivots(m_PivFile, m_nPivots);

			m_toppivsQ = settings.getToppivsQ();

			m_toppivsI = settings.getToppivsI();
		}
	}

	public String prepareQuery(String value, String field, boolean isQueryID)
			throws VIRException {
		if (isQueryID) {
			try {
				int docid = getDocIDFromURI(value);
				if (docid < 0)
					throw new ParseException(
							"There is no document in the index for the given "
									+ Parameters.IDFIELD + value);
				
				query = getTopFieldFromDocID(docid, field, m_toppivsQ);
			} catch (ParseException e) {
				throw new VIRException(VIRException.MESSAGE_WRONG_ID, e);
			} catch (IOException e) {
				throw new VIRException(VIRException.MESSAGE_IO_PROBLEM, e);
			}
		} else {
			sq = m_soSearcher.getObject(value);
			query = m_sfaALL.metricObjectToStringQ(sq, m_toppivsQ, m_toppivsI);
		}
		// System.out.println(query);
		return query;
	}

	public void query() throws ParseException, IOException {
		BooleanClause.Occur[] flags = new BooleanClause.Occur[1];
		flags[0] = getOccur();

		String[] v = { query };
		String[] f = { Parameters.LIRE_MP7ALL };

		Query q = MultiFieldQueryParser.parse(ver, v, f, flags, m_wrapper);

		TopDocs td = null;

		System.out.println("Using standalone MPEG-7 combined features index");
		td = m_sMPG7C.search(q, m_retrieve);

		m_hits = td.scoreDocs;
	}

	public void reorderResults() {
	}

	public String[][] getResults(int startFrom, int numElements)
			throws IOException {
		String[][] retval = new String[numElements][3];
		for (int i = 0; i < numElements; i++) {
			if (startFrom + i > m_hits.length - 1)
				break;
			float score = m_hits[startFrom + i].score;
			retval[i][0] = ((Float) score).toString();
			Document d = null;
			d = m_sMPG7C.doc(m_hits[startFrom + i].doc);
			retval[i][1] = d.get(Parameters.IDFIELD);
			retval[i][2] = d.get(Parameters.THMBURL);
		}
		return retval;
	}

	public Searcher getIndex() {
		return m_sMPG7C;
	}

	public Occur getOccur() {
		return Occur.SHOULD;
	}

	public IndexReader getIndexReader() {
		return rMPG7C;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	private int getDocIDFromURI(String uri) throws ParseException, IOException {
		Query q = null;
		q = new TermQuery(new Term(Parameters.IDFIELD, uri));

		TopDocs td = m_sMPG7C.search(q, 1);
		if (td.totalHits > 0)
			return td.scoreDocs[0].doc;
		else
			return -1;
	}

	private String getTopFieldFromDocID(int docID, String field, int topq)
			throws ParseException, IOException {

		//TODO: check if this code is redundant. See also exception in prepareQuery method
		if (docID == -1)
			return "";

		return getTopFieldFromTFV(rMPG7C.getTermFreqVector(docID, field), topq);

	}

	private String getTopFieldFromTFV(TermFreqVector tf, int topq) {
		String outstr = "";

		String[] t = tf.getTerms();
		int[] f = tf.getTermFrequencies();

		for (int i = 0; i < t.length; i++) {
			if (f[i] > t.length - topq) {
				// outstr = outstr + t[i]+"^"+f[i]+" ";
				outstr = outstr + t[i] + "^" + (f[i] - (t.length - topq)) + " ";
			}
		}

		return outstr;
	}

}
