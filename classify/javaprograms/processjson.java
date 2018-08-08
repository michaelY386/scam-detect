import java.io.*;
import org.json.simple.JSONObject;
import java.util.*;

public class Classifier {
 
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

  /*
  public static List<String> get_bank_words(String filename)
  {
    String line;

    List<String> words = new ArrayList<String>();
    try 
    {
      FileReader fileReader = new FileReader(filename);

      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while((line = bufferedReader.readLine()) != null) 
      {
        words.add(line);
      }   
  
      bufferedReader.close();  
      return words;       
    }
    catch(FileNotFoundException ex) 
    {
      System.out.println("Unable to open file '" + filename + "'");    
      return null;            
    }
    catch(IOException ex) 
    {
      System.out.println("Error reading file '" + fileName + "'");                  
      return null;
    }
  }
  */

  /*
  public static List<String> get_transcript_words(String filename)
  {
    String line;
    List<String> words = new ArrayList<String>();
    try 
    {
      FileReader fileReader = new FileReader(filename);

      BufferedReader bufferedReader = new BufferedReader(fileReader);

      while((line = bufferedReader.readLine()) != null) 
      {
        for (String word:line.split("\\s+"))
        {
           word = clear_nonascii(word);
           word = word.replaceAll("\\d","");
           word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase(); //to remove punctuation marks
           words.add(word);
        } 
      }   
  
      bufferedReader.close();  
      return words;       
    }
    catch(FileNotFoundException ex) 
    {
      System.out.println("Unable to open file '" + filename + "'");    
      return null;            
    }
    catch(IOException ex) 
    {
      System.out.println("Error reading file '" + fileName + "'");                  
      return null;
    }
  }
  */

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

  public static List<String> get_same_words(List<String> keywords, List<String> words)
  {
    List<String> subset = new ArrayList<String> (keywords.size() > words.size() ? keywords.size() : words.size());
    subset.addAll(keywords);
    subset.retainAll(words);
    return subset;
  }

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

  /*
  // Add removal of name data to this (like names of primary and contacts)
  public static float get_scam_likelihood (String transcriptfile_name, String keyfile_name )
  {
    List<String> key_words = get_bank_words(keyfile_name);
    List<String> call_words_list = get_transcript_words(transcriptfile_name);

    List<String> strict_call_words = remove_common_words(call_words_list);
    List<String> high_risk_words = get_same_words(key_words,strict_call_words);

    float match_percent = get_percentage_subset(high_risk_words,call_words_list,strict_call_words.size());

    return (match_percent*100);
  }
  */
  /*
  public static float get_scam_likelihood (String transcriptfile_name, JSONObject keywordsobj )//(String filename, JSONObject keywords)
  {
    
    String keywordlist = keywordsobj.get("keywords");
    String[] kwarray = keywordlist.split("\\s+");

    List<String> key_words = new ArrayList<String>(kwarray);

    List<String> call_words_list = get_transcript_words(transcriptfile_name);

    List<String> strict_call_words = remove_common_words(call_words_list);
    List<String> high_risk_words = get_same_words(key_words,strict_call_words);

    float match_percent = get_percentage_subset(high_risk_words,call_words_list,strict_call_words.size());

    return (match_percent*100);
  }

  public static JSONObject extract_info(String transcriptfile_name, boolean scam_flag)
  {
    List<String> call_words_list = get_transcript_words(transcriptfile_name);
    List<String> strict_call_words = remove_common_words(call_words_list);

    StringJoiner joiner = new StringJoiner(" ", "", "");
    strict_call_words.forEach(joiner::add);

    String extracted_words_list = joiner.toString();

    JSONObject extracted_obj = new JSONObject();

    extracted_obj.put("keywords",extracted_words_list);
    extracted_obj.put("scam_flag",scam_flag);

  }
  */
  public static double get_scam_likelihood (String transcript, JSONObject keywordsobj )//(String filename, JSONObject keywords)
  {
    
    String keywordlist = keywordsobj.get("keywords");
    String[] kwarray = keywordlist.split("\\s+");

    List<String> key_words = new ArrayList<String>(Arrays.asList(kwarray));

    List<String> call_words_list = get_transcript_words(transcript);

    List<String> strict_call_words = remove_common_words(call_words_list);
    List<String> high_risk_words = get_same_words(key_words,strict_call_words);

    double match_percent = get_percentage_subset(high_risk_words,call_words_list,strict_call_words.size());

    return (match_percent*100);
  }
  
  public static JSONObject extract_info(String transcript, boolean scam_flag)
  {
    List<String> call_words_list = get_transcript_words(transcript);
    List<String> strict_call_words = remove_common_words(call_words_list);

    StringJoiner joiner = new StringJoiner(" ", "", "");
    strict_call_words.forEach(joiner::add);

    String extracted_words_list = joiner.toString();

    JSONObject extracted_obj = new JSONObject();

    extracted_obj.put("keywords",extracted_words_list);
    extracted_obj.put("scam_flag",scam_flag);

    return extracted_obj;

  }
  public static void main(String[] args) 
  {
    String transcript = "Hey Aunt Jude, this is James, Ronald's son. Hello James, how are you? How's dad? What have you been up to? I'm doing ok, school just started last month for the kids, so we've been busy with getting back into routine. Dad's doing fine. I just wanted to call because I know you're turning 68 next week, and I wanted to come visit this weekend. Are you free? Can Julie and I by with the kids? Of course. I'd love to have you over, kiddo. I haven't seen Julie in several months, how is she holding up with going back to work and handling the kids? Oh, she - Aunt Jude, I'll have to call you back, would 7 o'clock be ok? Sure thing, bye James.";
    
    JSONObject obj = new JSONObject();
    
    obj.put("keywords","scam money dollar dollars bank banks late urgent immediate opportunity win deadline deadlines amount rich poor generous charity charities profit profits selected offer offers free bonus bonuses buy valuable prize prizes foreign lottery winner credit card investment investments stock stocks risk risks passport birth certificate social security return trust check order company charge charges shipping travel loan interest irs charitable registration offer savings confirm password user name username account address security key billing sensitive debt collection audit tax taxes court seized seize report unpaid case form license amount license owe confidential bill private arrest arrested warrant unpaid property insurance retirement fund funds emergency cash withdraw fee interest legal lawsuit accountant investment investigation panic revenue deposit wire federal transaction accounting recorded payment assurance risk payments paperwork tax problem federal department internal revenue service legal randomly selected");

    double result = get_scam_likelihood(transcript,obj);

    System.out.println("Result is "+result);
  }
}
