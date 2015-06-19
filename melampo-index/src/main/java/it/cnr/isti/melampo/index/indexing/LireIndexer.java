package it.cnr.isti.melampo.index.indexing;

import it.cnr.isti.melampo.index.CosineSimilarity;
import it.cnr.isti.melampo.index.LireObjectFieldAdder;
import it.cnr.isti.melampo.index.Parameters;
import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.similarity.metric.LireMetric;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LireIndexer extends MelampoIndexerAbstract {

	protected IndexWriter m_w;
	protected LireObjectFieldAdder m_sfaALL = null;

	protected boolean m_create;
	protected String m_lucenePath;
	protected int m_toppivs;
	protected int m_nPivots;
	protected String m_PivFile;
	protected IndexReader rMPG7C;
	protected IndexSearcher m_sMPG7C;

	public void OpenIndex(File propertyFile) throws IOException, VIRException {
		LireSettings settings = null;
		try {
			settings = new LireSettings(propertyFile);
			OpenIndex(settings);
		} catch (VIRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void OpenIndex(LireSettings settings) throws IOException,
			VIRException {

		Directory index = null;

		registerAnalyzers();

		m_create = settings.isCreateIndex();

		// to be fixed
		m_lucenePath = settings.getLuceneIndexPathLire();

		File f = new File(m_lucenePath);

		index = FSDirectory.open(f);

		if(!f.exists())
			m_create = true;
		else
			m_create = false;
			
		m_w = new IndexWriter(index, wrapper, m_create,
				IndexWriter.MaxFieldLength.UNLIMITED);

		// ***for update and delete purpose
		Directory dir = FSDirectory.open(new File(m_lucenePath));
		rMPG7C = IndexReader.open(dir, false);
		m_sMPG7C = new IndexSearcher(rMPG7C);
		m_sMPG7C.setSimilarity(new CosineSimilarity());
		// ***

		m_nPivots = settings.getnPivots();
		m_PivFile = settings.getPivotsPath();

		initLireObjectFieldAdder();

		m_toppivs = settings.getToppivsI();
	}

	protected void initLireObjectFieldAdder() {
		m_sfaALL = new LireObjectFieldAdder(new LireMetric());
		m_sfaALL.loadBinPivots(m_PivFile, m_nPivots);
	}

	protected void registerAnalyzers() {
		WhitespaceAnalyzer wsa = new WhitespaceAnalyzer();
		wrapper.addAnalyzer(Parameters.LIRE_MP7ALL, wsa);
	}

	public void addDocument(LireObject s, String uri)
			throws CorruptIndexException, IOException, VIRException {
		Document doc = new Document();

		// MPEG-7
		m_sfaALL.addFieldToDoc(doc, s, m_toppivs);

		// ID
		m_sfaALL.AddIDField(doc, uri);
		System.out.println("id " + uri);

		m_w.addDocument(doc);

		System.out.println("indexed doc " + uri);
	}

	public void deleteDocument(String uri) throws CorruptIndexException,
			IOException, VIRException, ParseException {
		
		Term idTerm = new Term(Parameters.IDFIELD, uri);
		if (m_sMPG7C.docFreq(idTerm) > 0) {
			m_w.deleteDocuments(idTerm);
		} else {
			throw new VIRException("Error, unable to delete the document "
					+ uri);
		}
		
//		int docId = getDocIDFromURI(uri);
//		if (docId != -1) {
//			rMPG7C.deleteDocument(docId);
//		} else {
//			throw new VIRException("Error, unable to retrieve the document "
//					+ uri);
//		}
		System.out.println("deleted doc " + uri);
	}

	public void updateDocument(LireObject s, String uri)
			throws CorruptIndexException, IOException, VIRException {

		Document doc = new Document();
		// MPEG-7
		m_sfaALL.addFieldToDoc(doc, s, m_toppivs);
		// ID
		m_sfaALL.AddIDField(doc, uri);
		System.out.println("id " + uri);

		Term idTerm = new Term(Parameters.IDFIELD, uri);
		if (m_sMPG7C.docFreq(idTerm) > 0) {
			m_w.updateDocument(idTerm, doc);
		} else {
			throw new VIRException("Error, unable to retrieve the document "
					+ uri);
		}

		System.out.println("updated doc " + uri);
	}

	public void closeIndex() throws CorruptIndexException, IOException {
		if (m_w != null) {
			m_w.optimize();
			m_w.close();
		}
		if (m_sMPG7C != null) {
			m_sMPG7C.close();
		}
		if (rMPG7C != null) {
			rMPG7C.close();
		}
	}
	
	public void optimizeIndex() throws CorruptIndexException, IOException {
		m_w.optimize();
	}
	
	public void commit() throws CorruptIndexException, IOException {
		m_w.commit();
	}

	private int getDocIDFromURI(String uri) throws ParseException, IOException {
		WhitespaceAnalyzer wsa = new WhitespaceAnalyzer();

		Query q = null;

		// q = new QueryParser(ver, Parameters.IDFIELD, wsa).parse(uri);
		q = new TermQuery(new Term(Parameters.IDFIELD, uri));

		// TopDocs td = m_ps.search(q, m_hitsPerPage);
		TopDocs td = m_sMPG7C.search(q, 1);
		if (td.totalHits > 0)
			return td.scoreDocs[0].doc;
		else
			return -1;
	}

}
