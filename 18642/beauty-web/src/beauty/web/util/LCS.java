package beauty.web.util;

import java.util.ArrayList;
import java.util.List;

import beauty.web.model.Product;

public class LCS {

	private static int[][] matrix; // cost matrix
	
	private static int min(int x, int y, int z)
	{
		return Math.min(x, Math.min(y, z));
	}
	
	private static int matchCost(char c, char d)
	{
		if(c == d){
			return 0;
		} else {
			return 1;
		}
	}
	
	private static int inDelCost()
	{
		return 1;
	} 
	
	private static int getLCSLength(String pattern, String text)
	{
		int plen = pattern.length(),
			tlen = text.length(), i, j;
		matrix = new int[plen + 1][tlen + 1];
		
		// row_init, insert
		for(j = 0; j < tlen + 1; j++){
			matrix[0][j] = j;
		}
		// col_init, delete
		for(i = 0; i < plen + 1; i++){
			matrix[i][0] = i;
		}
		// fill matrix with cost
		for(i = 1; i < plen + 1; i++){			
			for(j = 1; j < tlen + 1; j++){
				matrix[i][j] = min(
						matrix[i-1][j-1] + matchCost(pattern.charAt(i - 1), text.charAt(j - 1)),
						matrix[i][j-1] + inDelCost(), // insert a j
						matrix[i-1][j] + inDelCost() // delete an i
						);
			}
		}
		return plen - matrix[plen][tlen];
	}
	
	/**
	 * 
	 * @param pattern
	 * @param texts basically is the name, needs to be parsed as stdform. inefficient!
	 * @return
	 */
	public static String[] getSuspects(String pattern, ArrayList<String> texts)
	{
		if(pattern.length() == 0)
			return null;
		
		ArrayList<String> suspects = new ArrayList<String>();
		for(String text: texts){
			if(getLCSLength(pattern, StringUtil.getStdFrom(text)) > pattern.length()/2){
				suspects.add(text);
			}
		}
		
		if(suspects.size() == 0){
			return null;
		} else {
			String[] tmp = new String[suspects.size()];
			return suspects.toArray(tmp);
		}
	}

	/**
	 * 
	 * @param tmpProducts
	 * @param products
	 * @param query already in stdform
	 * @return
	 */
	public static boolean parseProduct(List<Product> tmpProducts,
			Product[] products, String query) {
		if(query.length() == 0 || products  == null){
			return false;
		}
		
		for(Product p : products){
			if(p.getStdForm().contains(query) 
			|| query.contains(p.getStdForm())
			|| getLCSLength(query, p.getStdForm()) > query.length()/2){
				tmpProducts.add(p);
			}
		}
		return true;
	}
}
