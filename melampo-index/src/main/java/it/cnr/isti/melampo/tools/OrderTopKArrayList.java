package it.cnr.isti.melampo.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class OrderTopKArrayList {

	public ArrayList<PD> m_list;
	public int m_k;
	
	public OrderTopKArrayList(int k){
		m_list = new ArrayList<PD>(k+1);
		m_k = k;
	}
	
	public boolean insert(double dist, int piv){
		PD p = new PD();
		p.pivid = piv;
		p.dist = dist;
		
	    int loc = Collections.binarySearch(m_list, p);
		if (loc >= 0) {
			System.out.println("ERROR duplicates!!");
			return false;
		}
		else {
			m_list.add((-loc - 1), p);
			if (m_list.size()>m_k){
				m_list.remove(m_k);
			}
		}
		
		return true;
	}

	public int get(int index){
		return m_list.get(index).pivid;
	}

	public static class PD implements Comparable<PD> {
		public double 	dist;
		public int		pivid;

		public int compareTo(PD arg0) {
			
			if (dist <= arg0.dist) return -1;
			if (dist > arg0.dist) return 1;
			
			return 0;
		}
	}
		
}



