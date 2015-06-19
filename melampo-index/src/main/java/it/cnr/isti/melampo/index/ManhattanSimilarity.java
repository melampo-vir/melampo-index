package it.cnr.isti.melampo.index;

import org.apache.lucene.search.Similarity;

public class ManhattanSimilarity extends Similarity {

	/**
	 * 
	 *//*
	public CosineSimilarity() {
		// TODO Auto-generated constructor stub
	    for (int i = 0; i < 256; i++)
	        super.NORM_TABLE[i] = 1.0f/255.0f*(float)i;
	}

	public byte encodeNorm(float f) {
		    assert f<=1f && f>=0f;
		    
		    return (byte)Math.floor(((double)(f)*255));
	}
	  */
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#coord(int, int)
	 */
	@Override
	public float coord(int overlap, int maxOverlap) {
		//System.out.println(overlap + " " + maxOverlap);
		// TODO Auto-generated method stub
		//float d = overlap * maxOverlap;
		
		//if (d == 0) return 0;
		//else 		return 1 / d;
		return 1f;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#idf(int, int)
	 */
	@Override
	public float idf(int docFreq, int numDocs) {
		// TODO Auto-generated method stub
		//if (docFreq == 0)	return 0;
		//else 				return (float) Math.sqrt ( Math.log ( numDocs / docFreq ) );
		return 1f;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#lengthNorm(java.lang.String, int)
	 */
	@Override
	public float lengthNorm(String fieldName, int numTerms) {
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#queryNorm(float)
	 */
	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		// TODO Auto-generated method stub
		return 1;
		//return 1f/sumOfSquaredWeights;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#sloppyFreq(int)
	 */
	@Override
	public float sloppyFreq(int distance) {
		// TODO Auto-generated method stub
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Similarity#tf(float)
	 */
	@Override
	public float tf(float freq) {
		// TODO Auto-generated method stub
		//System.out.println(freq);
		return freq;
	}
	
    // TODO: Remove warning after API has been finalized
    public float scorePayload(String fieldName, byte[] payload, int offset, int length) {
      //we know it is size 4 here, so ignore the offset/length
      return payload[0];
    }

}
