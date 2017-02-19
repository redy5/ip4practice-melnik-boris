/*Boris Melnik PR-4 IP
 * Tester class*/
import java.util.ArrayList;
import java.util.Scanner;

public class Tester {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("Dictionary by Melnik Boris");
		Dictionary d = new Dictionary();
		while (true) {
			System.out.println("Choose action:");
			System.out.println("1.Load txt file to dictionary.");
			System.out.println("2.Show all trigramms for existing words.");
			System.out.println("3.Show all shuffles for existing words.");
			System.out.println("4.Joker search.");
			System.out.println("5.Exit.");
			int i = 0;
			if (s.hasNextInt())
				i = s.nextInt();
			else if (s.hasNext() && !s.hasNextInt())
				s.nextLine();
			if (i == 1) {
				System.out.println("Enter file name:");
				String f_loc = s.next();
				d.addText(f_loc);
			}
			if (i == 2) {
				d.trigrams();
			}
			if (i == 3) {
				d.shuffles();
			}
			if (i == 4) {
				System.out.println("Joker/wildcard/* search! Tells you document(s) and coordinate(s) of the word you are looking for!");
				System.out.println("Starting, ending or 1 in the middle jokers work. No jokers results in bad attitude from the programm.");
				System.out.println("Enter your request:");
				String request = s.next();
				ArrayList<Token> res = d.findIncompleteToken(request);
				if (res == null)
					System.out.println("Wrong request, baka.");
				else
					for (int j = 0; j < res.size(); j++)
						System.out.println(res.get(j));
			}
			if (i == 5) {
				s.close();
				return;
			}
		}

	}
}