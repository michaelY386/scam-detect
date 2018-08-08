import java.io.*;
import java.util.*;

public class ProcessNoJson {
 
  //List of common words in the English language - these don't add value during processing, so are stripped from the transcript
  public static List<String> common_words = new ArrayList<String>(
    Arrays.asList("the","be","is","are","to","of","and","a","in","that","have","i","it","for","not","on","with","he","as","you","do","at","this","but",
      "his","by","from","they","we","say","her","she","or","will","an","my","one","all","would","there","their","what","so","up","out","if","about",
      "who","get","which","go","when","me","make","can","like","time","no","just","him","know","take","person","into","year","your","good","some",
      "could","them","see","other","than","then","now","look","only","come","its","over","think","also","back","after","use","two","how","our","work",
      "first","well","way","even","new","want","because","any","these","give","day","most","us","hi","hello","okay","been","those","was","has","each",
      "did","not"));

  public static String clear_nonascii(String line)
  {
    return line.replaceAll("[^\\x00-\\x7F]", "");
  }
 
  //Extract list of words from a transcript, removing non-ascii characters and numbers
  public static List<String> get_transcript_words(String transcript)
  {
    String line;
    List<String> words = new ArrayList<String>();
    
    for (String word:transcript.split("\\s+"))
    {
       word = clear_nonascii(word);
       word = word.replaceAll("\\d","");
       word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase(); //to remove punctuation marks
       words.add(word);
    } 

    return words;
  }

  //Get the list of scam-indicative keywords actually present in the transcript
  public static List<String> get_same_words(List<String> keywords, List<String> words)
  {
    List<String> subset = new ArrayList<String> (keywords.size() > words.size() ? keywords.size() : words.size());
    subset.addAll(keywords);
    subset.retainAll(words);
    return subset;
  }

  //Remove the common words from the transcript
  public static List<String> remove_common_words(List<String> words)
  {
    List<String> subset = new ArrayList<String> (words.size() > common_words.size() ? words.size() : common_words.size());
    subset.addAll(words);
    subset.removeAll(common_words);
    return subset;
  }

  public static int get_occurrence_count(String word, List<String> words)
  {
    return java.util.Collections.frequency(words, word);
  }

  //Get the percentage of words in the transcript that are actually scam-indicative keywords
  public static double get_percentage_subset(List<String> subset, List<String> master, int reduced_transcript_len)
  {
    int total=0;
    double percent;
    
    for(String word:subset)
      total+=get_occurrence_count(word,master);

    try
    {
      percent = (double)total/reduced_transcript_len;
      return percent;
    }

    catch(ArithmeticException e)
    {
      percent = 0.0;
      return percent;
    }
  }

  //Get the likelihood that a call is a scam by determining the percentage of words in the transcript that are scam-indicative
  public static double get_scam_likelihood (String transcript, String keywordsobj)
  {
    
    //String keywordlist = keywordsobj.get("keywords");
    String keywordlist = keywordsobj;
    String[] kwarray = keywordlist.split("\\s+");

    List<String> key_words = new ArrayList<String>(Arrays.asList(kwarray));

    List<String> call_words_list = get_transcript_words(transcript);

    List<String> strict_call_words = remove_common_words(call_words_list);
    List<String> high_risk_words = get_same_words(key_words,strict_call_words);

    double match_percent = get_percentage_subset(high_risk_words,call_words_list,strict_call_words.size());

    return (match_percent*100);
  }
  
  //Extract useful words from the transcript of the call to be used later for keyword updation
  public static String extract_info(String transcript, boolean scam_flag)
  {
    List<String> call_words_list = get_transcript_words(transcript);
    List<String> strict_call_words = remove_common_words(call_words_list);

    StringJoiner joiner = new StringJoiner(" ", "", "");
    strict_call_words.forEach(joiner::add);

    String extracted_words_list = joiner.toString();

    return extracted_words_list;

  }
  
  //Driver for testing the above methods
  public static void main(String[] args) 
  {
    //The following transcript is for a non-scam call
    //String transcript = "Hey Aunt Jude, this is James, Ronald's son. Hello James, how are you? How's dad? What have you been up to? I'm doing ok, school just started last month for the kids, so we've been busy with getting back into routine. Dad's doing fine. I just wanted to call because I know you're turning 68 next week, and I wanted to come visit this weekend. Are you free? Can Julie and I by with the kids? Of course. I'd love to have you over, kiddo. I haven't seen Julie in several months, how is she holding up with going back to work and handling the kids? Oh, she - Aunt Jude, I'll have to call you back, would 7 o'clock be ok? Sure thing, bye James.";
    
    //The following transcript is for a scam call
    String transcript = "Hello! You have been specially selected for a branch new offer by our company. You are a lucky winner who has been randomly chosen for one of five valuable prizes! All you have to pay is shipping and handling. This is an amazing one time offer that you must take advantage of as soon as possible. To find out what you have one, please have ready your home address and your credit card information ready so we can process your shipment.";
    
    //The following keyword list is created from threat profile research
    String obj = "scam money dollar dollars bank banks late urgent immediate opportunity win deadline deadlines amount rich poor generous charity charities profit profits selected offer offers free bonus bonuses buy valuable prize prizes foreign lottery winner credit card investment investments stock stocks risk risks passport birth certificate social security return trust check order company charge charges shipping travel loan interest irs charitable registration offer savings confirm password user name username account address security key billing sensitive debt collection audit tax taxes court seized seize report unpaid case form license amount license owe confidential bill private arrest arrested warrant unpaid property insurance retirement fund funds emergency cash withdraw fee interest legal lawsuit accountant investment investigation panic revenue deposit wire federal transaction accounting recorded payment assurance risk payments paperwork tax problem federal department internal revenue service legal randomly selected";
    double result = get_scam_likelihood(transcript,obj);
    
    System.out.println("Result is " + result);

    System.out.println("Extracted words are " + extract_info(transcript, true));
  }
}
