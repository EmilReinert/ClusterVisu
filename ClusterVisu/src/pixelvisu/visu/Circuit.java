package pixelvisu.visu;


public class Circuit {
	String [] clustering;
	int c_idx =0;
	String [] linkage;
	int l_idx =0;
	String [] similarity;
	int s_idx =0;
	
	
	public Circuit() {
		
		clustering=new String[]{"agglomerative"};

		linkage=new String[]{"single","complete","average"};
				
		similarity=new String[]{"euclidean","rms","minkowski","maximum","absolute","cosine"};
		
	}
	
	public void up() {
		if(s_idx==similarity.length-1)
			LUp();
		SUp();
	}
	
	
	
	public void down() {
		if(s_idx==0)
			LDown();
		SDown();
	}
	
	public int getIndexOf(String s) {
		int match = -1;
		for (int i = 0;i<clustering.length;i++) if(clustering[i]==s) match=i; 
		for (int i = 0;i<linkage.length;i++) if(linkage[i]==s) match=i; 
		for (int i = 0;i<similarity.length;i++) if(similarity[i]==s) match=i; 
		if(match>-1) return match;
		else {System.err.println("There is no match for the given Input");return 0;}
	}
	public String[] getCircuit() {
		return new  String[] {clustering[c_idx],linkage[l_idx],similarity[s_idx]};
	}
	
	
	public void SUp() {
		//plus one
		if(s_idx==similarity.length-1)s_idx =0;
		else s_idx ++;
	}public void LUp() {
		//plus one
		if(l_idx==linkage.length-1)l_idx =0;
		else l_idx ++;
	}
	public void SDown() {
		//minus one
		if(s_idx==0)s_idx =similarity.length-1;
		else s_idx--;
	}public void LDown() {
		//minus one
		if(l_idx==0)l_idx =linkage.length-1;
		else l_idx--;
	}
	
}
