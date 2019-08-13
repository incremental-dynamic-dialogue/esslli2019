package module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WordUtil {
	
	public static Set<String> confirmWords;
	public static Set<String> rejectWords;
	public static Set<String> hesitateWords;
	
	
	final static String intregex = "\\d+";
	public static String language;
	private  static String[] roman = {"null", "i", "ii", "iii", "iv", "v", "vi"};
	
	private  static String[] germanunits = {"null", "eins","zwei","drei","fier","fünf",
		"sechs","sieben","acht","neun","zehn","elf",
		"zwölf","dreizehn","vierzehn","","fünfzehn",
		"sechzehn","siebzehn", "achtzehn", "neunzehn"};
	private static String[] germantens = {"","","zwanzig","dreiβig","fierzig","fünfzig",
		"sechzig","siebzig","achtzig","neunzig"};
	private static String[] germanordinals = {"null","erste","zweite","dritte",
		"vierte", "fünfte", "sechste", "siebte", "achte",  "neunte", "zehnte", "elfte", 
		"zwölfte"};
	
	private static String[] englishunits = {"zero", "one","two","three","four","five",
			"six","seven","eight","nine","ten","eleven",
			"twelve","thirteen","fourteen","","fifteen",
			"sixteen","seventeen","eighteen", "nineteen"};
		private static String[] englishtens = {"","","twenty","thirty","forty","fifty",
			"sixty","seventy","eighty","ninety"};
		private static String[] englishordinals = {"zeroth","first","second", "third",
			"fourth", "fifth", "sixth", "seventh", "eighth",  "ninth", "tenth", "eleventh", 
			"twelfth"};
		
	
	public WordUtil(String language) {
		this.language = language;
		switch (this.language){
		case "de":
			break;
		case "en":
			break;
		default:
			System.out.println("Error, no language for " + language);
			break;
		}
		

		// TODO should do this from a file
		switch (this.language){
		case "de":
			confirmWords = new HashSet<String>(Arrays.asList(
					"genau", "ja", "supi", "danke", "gut", "stimmt", "super", "korrekt", "<silentconfirm>", "richtig"));
			rejectWords = new HashSet<String>(Arrays.asList("nein", "nee", "nichts", "falsch", "anderer", "9", "stop", "abrechen"));
			hesitateWords = new HashSet<String>(Arrays.asList("ähm", "äh", "hm", "<sil>"));
			break;
		case "en":
			confirmWords = new HashSet<String>(Arrays.asList(
					"yeah", "yes", "super", "okay", "good", "correct", "confirm", "<silentconfirm>"));
			rejectWords = new HashSet<String>(Arrays.asList("no", "nope", "stop", "wrong", "other", "cancel"));
			hesitateWords = new HashSet<String>(Arrays.asList("um", "uh", "er", "hmm", "<sil>"));
			break;
		default:
			System.err.println("No language for DA recognition:" + language);
			break;
		}
			
			
	}
	
	public static boolean isConfirmWord(String word) {
		return confirmWords.contains(word.toLowerCase());
	}
	
	public static boolean isRejectWord(String word) {
		return rejectWords.contains(word.toLowerCase());
	}
	
	public static boolean isHesistationWord(String word) {
		return hesitateWords.contains(word.toLowerCase());
	}

	


	public class LengthComparator implements java.util.Comparator<String> {

	    private int referenceLength;

	    public LengthComparator(String reference) {
	        super();
	        this.referenceLength = reference.length();
	    }

	    public int compare(String s1, String s2) {
	        int dist1 = Math.abs(s1.length() - referenceLength);
	        int dist2 = Math.abs(s2.length() - referenceLength);

	        return dist1 - dist2;
	    }
	}
	
	public static String convertInteger(Integer i){
		switch (language){
		case "de":
			return convertGermanInteger(i);
		case "en":
			return convertEnglishInteger(i);
		default:
			System.err.println("No integer conversion for integers :" + language);
			break;
		}
		return "";
	}

	public static String convertGermanInteger(Integer i) {
		//
		if (i<0) return "minus";
		if( i < 20)  return germanunits[i];
		if( i < 100) return  ((i % 10 > 0)? " " + convertGermanInteger(i % 10):"") + "und" + germantens[i/10];
		if( i < 1000) return germanunits[i/100] + "hundert" + ((i % 100 > 0)? convertGermanInteger(i % 100):"");
		if( i < 1000000) return convertGermanInteger(i / 1000) + "tausend" + ((i % 1000 > 0)? convertGermanInteger(i % 1000):"") ;
		return convertGermanInteger(i / 1000000) + " Million" + ((i/1000000)>1? "en": "") + ((i % 1000000 > 0)? " " + convertGermanInteger(i % 1000000):"") ;
	}
	
	public static String convertEnglishInteger(Integer i){
		if (i<0) return "minus";
		if( i < 20)  return englishunits[i];
		if( i < 100) return  ((i % 10 > 0)? "" +  englishtens[i/10] + "-" + convertEnglishInteger(i % 10):"");
		if( i < 1000) return englishunits[i/100] + " hundred" + ((i % 100 > 0)? " and " + convertEnglishInteger(i % 100):"");
		if( i < 1000000) return convertEnglishInteger(i / 1000) + " thousand " + (((i % 1000  > 0) & (i % 1000  < 100))? "and ":"") + ((i % 1000 > 0)? convertEnglishInteger(i % 1000):"") ;
		return convertEnglishInteger(i / 1000000) + " million" + ((i/1000000)>1? "": "") + ((i % 1000000 > 0)? " " + (((i % 1000000  > 0) & (i % 1000000  < 100))? "and ":"")  + convertEnglishInteger(i % 1000000):"") ;
	}
	
	
	public String normalizeFromASR(String word){
		//Gives back all possible alternatives for this word
		//The disjunctions are caused by number segmentation mainly
		if (word.matches(intregex)){
		//convert if it's a number. NB there may be ambiguity
			word = convertInteger(Integer.parseInt(word));
		}
		return word;
	}
	
	public ArrayList<String> digitsFromASR(String word){
		ArrayList<String> wordHyps = new ArrayList<String>();
		if (word.matches(intregex)){
			//convert if it's a number. NB there may be ambiguity
				for (int i=0; i<word.length();i++) {
					wordHyps.add(convertInteger(Integer.parseInt(String.valueOf(word.charAt(i)))));
				}
				
		} else {
			for (String number : this.englishunits) {
				if (word.equalsIgnoreCase(number)){
					wordHyps.add(word);
					break;
				}
			}
		}
		return wordHyps;
		
	}
	
	public static ArrayList<String> normalizeFromASRNBest(String word){
		//Gives back all possible alternatives for this word
		//The disjunctions are caused by number segmentation mainly
		ArrayList<String> wordHyps = new ArrayList<String>();
		if (word.matches(intregex)){
		//convert if it's a number. NB there may be ambiguity
			ArrayList<Integer> possiblenumbers = new ArrayList<Integer>();
			String sofar = "";
			for (char c : word.toCharArray()){
				sofar+=c;
				possiblenumbers.add(Integer.parseInt(String.valueOf(c)));
				if (sofar.length()>1){
					possiblenumbers.add(Integer.parseInt(sofar));
				}
			}
			for (int myint : possiblenumbers){
				//wordHyps.add(convert(myint));
				//NB not going to convert for now
				wordHyps.add(String.valueOf(myint));
			}
		} else {
			wordHyps.add(word); //NB letters?
		}
		
		return wordHyps;
	}


	public static String get1Best(ArrayList<String> wordhyps,
			ArrayList<String> positionsinplay) {
		if (wordhyps.size()==1){
			return wordhyps.get(0);
		}
		//if multiple, for now only for numbers, only admit those positions in play
		for (int i=wordhyps.size()-1; i>=0; i--){
			String word = wordhyps.get(i);
			for (String pos : positionsinplay){
				if (word.equals(pos)){
					return word; 
					//TODO this is just looking for the first match backwards in sequences like:
					//1, 1, 11, 2, 112, (backwards, it would favour 112, then 2 etc.)
				}
			}
		}
		return null;
	}


	public static String romanFromStringInt(String intcandidate){
		if (intcandidate.matches(intregex)&&Integer.parseInt(intcandidate)<7){
			
			return roman[Integer.parseInt(intcandidate)];
				
			
		}
		return "null";
	}
	
	public static String ordinalFromStringIntGerman(String intcandidate) {
		if (intcandidate.matches(intregex)&&Integer.parseInt(intcandidate)<13){
			return germanordinals[Integer.parseInt(intcandidate)];
		}
		return "null";
	}
	
	public static String ordinalFromStringIntEnglish(String intcandidate) {
		if (intcandidate.matches(intregex)&&Integer.parseInt(intcandidate)<13){
			return englishordinals[Integer.parseInt(intcandidate)];
		}
		return "null";
	}
	
	public static String ordinalFromStringInt(String intcandidate) {
		switch (language){
		case "de":
			return ordinalFromStringIntGerman(intcandidate);
		case "en":
			return ordinalFromStringIntEnglish(intcandidate);
		default:
			System.err.println("No integer conversion for integers :" + language);
			break;
		}
		return "";
	}
	
	
	public static void main(String[] args) {
		WordUtil w = new WordUtil("en");
		System.out.println(w.convertInteger(422243));
		System.out.println(w.convertInteger(10));
		System.out.println(w.convertInteger(100));
		System.out.println(w.convertInteger(1000));
		System.out.println(w.convertInteger(10000));
		System.out.println(w.convertInteger(100000));
		System.out.println(w.convertInteger(1000000));
		System.out.println(w.convertInteger(1001));
		System.out.println(w.convertInteger(1000099));
		System.out.println(w.convertInteger(1100099));
		System.out.println(w.digitsFromASR("07799851159"));
		
		
		
	}
}
