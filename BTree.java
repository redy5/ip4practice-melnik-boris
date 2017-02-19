/*IP-4 Melnik Boris
 * My B-tree class*/
import java.util.ArrayList;

public class BTree {
	
	Node first = new Node();

	private class Node {
		String word = null; // word can be null, which means this node just doesnt contain a word
		char letter; // but letter must be, because it is the main characteristic of node
		ArrayList<Node> sons = new ArrayList<Node>(); // collection of all sons of this node

		Node() { // blank constructor for first node
			
		}

		Node(char ch) { // constructor for a node without a word
			this.letter = ch;
		}

		public void addSon(String word, int count) {
			if (count == word.length()) { // if true, this node is a place for a word being added
				this.word = word;
			} else {
				for (int i = 0; i < sons.size(); i++)
					if (sons.get(i).letter == word.charAt(count)) {
						sons.get(i).addSon(word, count + 1);
						return;
					}
				Node n = new Node(word.charAt(count));
				n.addSon(word, count + 1);
				sons.add(n);
			}
		}

		public void getAllSonWords(ArrayList<String> al) { // adds all words from this node and all sons to a collection
			if (word != null)
				al.add(word);
			for (int i = 0; i < sons.size(); i++)
				sons.get(i).getAllSonWords(al);
		}
	}

	private Node findNode(String path){ //returns node at path, or null, if there is no node there
		Node current=first;
		boolean cur_changed;
		for(int i=0;i<path.length();i++){
			cur_changed=false;
			for(int j=0;j<current.sons.size();j++){
				if(current.sons.get(j).letter==path.charAt(i)){
					current=current.sons.get(j);
					cur_changed=true;
					break;
				}
			}
			if(cur_changed==false)
				return null;
		}
		return current;
	}

	public void addWord(String word) {
		first.addSon(word, 0);
	}

	public String delWord(String word) {
		if(findNode(word)!=null){
			String res=findNode(word).word;
			findNode(word).word=null;
			return res;
		}else
			return null;
	}

	public boolean hasWord(String word) {
		Node n = findNode(word);
		if(n!=null&&n.word!=null&&n.word.equals(word))
			return true;
		return false;
	}

	public ArrayList<String> query(String query) {
		ArrayList<String> al = new ArrayList<String>();
		if(findNode(query)==null)
			al.add("No words found!");
		else
			findNode(query).getAllSonWords(al);
		return al;
	}
}