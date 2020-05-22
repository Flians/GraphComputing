package com.antfin.util;

import java.util.List;

public class GraphHelper<VV> {

	public static void swap(int x,int y, List ll){
		Object ob=ll.get(x);
		ll.set(x, ll.get(y));
		ll.set(y, ob);
	}

}
