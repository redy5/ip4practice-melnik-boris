/* Main Dictionary Class
 * Melnik Boris
 * IP
 * Practice-4
 * ATTENTION
 * I cleared Token, Dictionary and Tester classes from useless (in this lab) methods, only leaving the required for lab 4
 * So features like coordinate search and phrase search are missing here (and so joker search only tells full words and doc_ids, but it is
 * VERY easy to make it show also coordinates (but it will be less readable)
 * You can find all the listed above mathods and classes in lab3 (previous)
 * 
 * But this way it is easier to read
 * 
 * I will also attach winrar archive with full version (with a lot of code together)
 * (not advised to read, but works just fine)
 * */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dictionary implements Serializable {

	HashMap<String, Boolean> dict = new HashMap<String, Boolean>(); // hashmap to quickly check if the word is already added
	Token[] tokens = new Token[100]; // dictionary

	int docnum = 0;
	HashMap<Integer, String> documents = new HashMap<Integer, String>(); // to store doc names
	
	BTree forwardBTree = new BTree();
	BTree backwardBTree = new BTree();

	int tokenCount; //total token count

	public void addText(String f_loc) {
		try {
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new FileReader(f_loc)));

			System.out.println("Beginning to index " + f_loc);

			st.lowerCaseMode(true);
			st.whitespaceChars(',', ',');
			st.whitespaceChars('-', '-');
			st.whitespaceChars('.', '.');

			docnum++;
			documents.put(docnum, f_loc);

			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				if (st.sval == null)
					continue;
				String current_word = clear(st.sval);
				if (isWord(current_word)) {
					consideration(current_word);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("No such file!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Words added.");
		Arrays.sort(tokens, 0, tokenCount);
		System.out.println("Dictionary sorted.");
		
		for (int i = 0; i < tokenCount; i++)
			tokens[i].index = i;
		
		System.out.println("Tokens indexed.");
	}

	private boolean isWord(String s) { //easy check if input string is a word
		for (int i = 0; i < s.length(); i++)
			if (Character.isAlphabetic(s.charAt(i)))
				return true;
		return false;
	}

	private void consideration(String s) { //method to either add new token, or ++ count to existing (or resize array)
		if (tokens.length == tokenCount)
			resizeToken();
		if (dict.get(s) != null) {
			for (int i = 0; i < tokenCount; i++) {
				if (tokens[i].getText().equals(s) == true) {
					tokens[i].countPlus();
					if (!tokens[i].docs.contains((Integer) docnum))
						tokens[i].docs.add(docnum);
					break;
				}
			}
		} else {
			Token n = new Token(s);
			n.docs.add(docnum);
			tokens[tokenCount++] = n;
			dict.put(s, true);
			forwardBTree.addWord(s);
			backwardBTree.addWord(backward(s));
		}
	}

	private void resizeToken() { //resizes Token[] array
		Token[] temp = new Token[tokens.length * 2];
		for (int i = 0; i < tokens.length; i++) {
			temp[i] = tokens[i];
		}
		tokens = temp;
	}

	private String clear(String initial) { //clears input string from all chars except letters
		char[] r = new char[initial.length()];
		int count = 0;
		for (int i = 0; i < initial.length(); i++) {
			char c = initial.charAt(i);
			if (Character.isLetter(c))
				r[count++] = c;
		}
		char[] re = new char[count];
		for (int i = 0; i < count; i++)
			re[i] = r[i]; // this way i dont += to String and take much less memory!
		String res = new String(re);
		return res;
	}
	
	public void trigrams(){ //shows trigram list
		System.out.println("Trigram Word list:");
		for (int i = 0; i < tokenCount; i++)
			System.out.println(tokens[i].trigram);
	}
	
	public void shuffles(){ //shows shuufles list (with $)
		System.out.println("Shuffle Word list:");
		for (int i = 0; i < tokenCount; i++)
			System.out.println(tokens[i].shuffle);
	}
	
	private Token findToken(String s) { // return token by word
		for (int i = 0; i < tokenCount; i++) {
			if (tokens[i].getText().equals(s) == true)
				return tokens[i];
		}
		return null;
	}
	
	public ArrayList<Token> findIncompleteToken(String s){ //returns token from a request with joker/wildcard/*
		ArrayList<Token> results = new ArrayList<Token>();
		if(s.charAt(0)=='*'){
			String request=backward(s).substring(0, s.length()-1);
			ArrayList<String> r = backwardBTree.query(request);
			for(int i=0;i<r.size();i++)
				results.add(findToken(backward(r.get(i))));
		}
		else if(s.charAt(s.length()-1)=='*'){
			String request=s.substring(0, s.length()-1);
			ArrayList<String> r = forwardBTree.query(request);
			for(int i=0;i<r.size();i++)
				results.add(findToken(r.get(i)));
		}
		else{
			if(!s.contains("*")){
				return null;
			}
			String[] ws = s.split("\\*");
			String part1=ws[0];
			String part2=backward(ws[1]);
			ArrayList<String> r1 = forwardBTree.query(part1);
			ArrayList<String> r2 = backwardBTree.query(part2);
			for(int i=0;i<r2.size();i++)
				r2.set(i, backward(r2.get(i)));
			ArrayList<String> r = crossing(r1,r2);
			for(int i=0;i<r.size();i++)
				results.add(findToken(r.get(i)));
		}
		return results;
	}
	
	private String backward(String s){
		char[] mat = new char[s.length()];
		for(int i=0;i<s.length();i++){
			mat[i]=s.charAt(s.length()-1-i);
		}
		return new String(mat);
	}
	
	private ArrayList<String> crossing(ArrayList<String> a1, ArrayList<String> a2){
		ArrayList<String> res = new ArrayList<String>();
			for(int i=0;i<a1.size();i++){
				String word = a1.get(i);
				for(int j=0;j<a2.size();j++)
					if(word.equals(a2.get(j)))
						res.add(word);
			}
		return res;
	}
}