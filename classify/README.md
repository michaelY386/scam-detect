# Classification of Calls as Scams

Algorithm to determine the likelihood of a call being a scam, or legitimate.

# Setup Instructions:

To run the Java programs, set up the JDK and JRE on Windows 10 using the following steps. (Or refer to https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)

1. Since having multiple versions of Java can be messy, if you have previously installed older version(s) of JDK/JRE, un-install ALL of them. Goto "Control Panel" ⇒ "Programs" ⇒ "Programs and Features" ⇒ Un-install ALL programs begin with "Java", such as "Java SE Development Kit ...", "Java SE Runtime ...", "Java X Update ...", and etc.
2. Goto Java SE download site @ http://www.oracle.com/technetwork/java/javase/downloads/index.html.
3. Click on the download button under JDK in the Java Platform, Standard Edition section for the latest version.
4. Accept the License Agreement and download the exe for Windows.
5. Run the downloaded installer and use the default settings during installation.
6. Using the File Explorer, goto "C:\Program Files\Java" and note the name of the JDK directory.
7. Launch "Control Panel" ⇒ System and Security ⇒ System ⇒ Click "Advanced system settings" on the left pane.
8. Switch to "Advanced" tab ⇒ Push "Environment Variables" button.
9. Under "System Variables" (the bottom pane), scroll down to select "Path" ⇒ Click "Edit...".
10. You shall see a TABLE listing all the existing PATH entries. Click "New" ⇒ Enter the JDK's "bin" directory "c:\Program Files\Java\jdk-x.y.z\bin" (Replace x,y,z with your installation number) ⇒ Select "Move Up" to move this entry all the way to the TOP.

To verify that the above steps were successful,

1. Open command prompt.
2. Type path and check if "c:\Program Files\Java\jdk-x.y.z\bin" is at the beginning of the result.
3. Type java -version to see if JRE was installed correctly.
4. Type javac -version to see if JDK was installed correctly.

# Method of Execution:

  To test whether scam likelihood is being printed correctly and to view significant words extracted from a transcript, follow these steps:
  1. Open the command prompt. 
  1. Traverse to the scams/classify/javaprograms/ folder in the local copy of the repo.
  1. Type javac ProcessNoJson.java
  1. Type java ProcessNoJson

# Current Work:
  The current algorithm focuses on statistical processing of call transcript.

The get_scam_likelihood function takes a transcript and a space separated list of scam-indicating-keywords as String inputs.

1. It removes any numeric value present in the transcript, as a primitive redaction method to clear out personal information such as bank account numbers, etc.
1. Next, excluding common words, the program counts the number of words in the transcript that are indicative a scam. Words indicative of a scam are keywords read from the database, written into by the python module described later.
1. Finally, the percentage of 'suspicious' words present in the transcript is printed.

For testing, the get_scam_likelihood function is called from the main function using a test transcript and a basic keyword list.

The extract_info function takes a transcript and a flag indicating whether the call was finally classified as a scam as inputs.

1. It removes any numeric value present in the transcript, as a primitive redaction method to clear out personal information such as bank account numbers, etc.
1. Next, excluding common words, the program collects the remaining words in the transcript so that they can be given as input, along with scam flag to the python module described later.

For testing, the extract_info function is called from the main function using a test transcript.

These functions and their helper functions are included in the client-side code in scams/tree/develop/client/app/src/main/java/edu/cmu/eps/scams/classify/ClassifyFacade.java. The scam likelihood is calculated whenever a call is recorded and transcribed. The percentage returned is used to determine the action to be taken (ignore, alert reviewer, drop call).

Once a day, call data (extracted words along with whether the call was classified as a scam or not) for all calls in all the clients are collected by the python module to be consolidated to create an updated keyword list. The python module then writes this keyword list to the database. This is the keyword list queried every time get_scam_likelihood is called. Before any call data is consolidated, the initial keyword list is created based on threat profile research done from a number of sources including Phone Scam Information posted on the FTC's website.

This python module is still being tested and modified, but is based on process_real_time.py, which was demonstrated in the demo of the last prototype version.

# Assumptions and Notes:
  The calls to functions in ClassifyFacade has not been tested during an actual call yet. However, to observe and test the actual classification and keyword extraction, the ProcessNoJson program can be used, giving different sample transcripts as input.

# Results and Conclusions:
  The classifier spits out a higher percentage when the call transcript is shorter (or when analyzing the first 20 seconds of the call rather than the whole call). This is due to the fact that our keyword bank is primitive, and needs to be expanded and weightages for each keyword has not been assigned yet.
  
# Future Work:
  In basic classifier:
 1. Redaction can be improved by removing name of primary as well as names present in the primary's phonebook.
 1. The blacklist database can be updated by adding the phone numbers from which scam calls are made.
 1. The confidence of speech-to-text translator can be taken into consideration for classification (in case of robocalls or calls from those who mask their voice, confidence of the translator may be different).
 1. Take into account the format of the phone number from which call is made. Eg: Often if your number is (555) 867-5309, then scammers call you from a number that looks like (555) 867-####.

A second iteration classifier was also developed but not integrated into the application for this project checkpoint. With the new functionality included in the naive_bayes_helper.py file, we attempted to build a smarter classifier that was still reasonable to implement from scratch and stayed within the scope of the project. This updated classifier is based on the Naive Bayes text classification approach (more info: https://www.inf.ed.ac.uk/teaching/courses/inf2b/learnnotes/inf2b-learn-note07-2up.pdf), specifically using the bag of words approach that allows us to continue simply analyzing keywords without consideration of order which helps with both simplicity of implementing the algorithm and efficiency during run time classification.

The algorithm works by converting input scam and non scram training data/transcripts into frequency tables needed by the classifier. I begin by building a full dictionary of words found within any of the input transcripts (scam or not scam) and then remove duplicate words and the most common words in the English language. For every word in the dictionary, I use the input transcripts again to calculate frequency tables of of the conditional probabilities for each word. For example, if the word "account" is in the general dictionary, I can calculate the likelihood of the word "account" being found in a scam call. For our two classes, scam (S) and non scam (N), these likelihoods translate to the conditional probability P(W|S) that I'll see the word, W, in a scam transcript and 1 - P(W|S) = P(W|N) is the likelihood that the word is found in a non scam transcript.

The algorithm also needs to know P(S) and P(N) the general probabilities of a given call being a scam call or a non scam call. I had to manually calculate these values myself based on recent scam reports and statistics (sources cited below). This value ended up being estimated to approximately 9.41% of calls are (automated or non automated) phishing attempts.

For this algorithm, we needed training data comprised of both scam and non scam transcripts (found in ./transcripts/training). This was actually the hardest part in comparison to building the classifier because we wanted to be very careful about the way that the training data biased our classifier. Overall, we decided we wanted to first and foremost prioritize usability and therefore reduce false positives or even false "grey" area scenarios that would involve a third party reviewer. Realistically, the worst our app should perform should be equivalent to a phone that doesn't have our application installed. If we erred too much on the side of caution and raised a notification or forcibly hung up the phone in a false positive too often, then the phone would basically be renderred unusable. Of course, this does not mean that we want to have false negatives and provide a false sense of security to the user without any substance. To combat this issue and also due to a lack of useful phishing training data available online, I have had to partially modify training data that originated from a spam email data set online (http://csmining.org/index.php/spam-email-datasets-.html) and in some cases completely write my own training transcripts from scratch. In an attempt to get the best results, I have tried to get a diverse set of likely/most common phishing call scenarios based on reports online (https://www.ag.state.mn.us/consumer/publications/VoicePhishing.asp and https://www.rd.com/advice/saving-money/phone-call-scams/). However, due to limited time I have only been able to build training data in the following categories:
1. Card services
1. Phony debt collection
1. High tech computer scam
1. Financial Information
1. IRS impersonations
1. Bank calls
1. Lucky winner 
1. Donation collections

As future work, I also plan to add training data in the following categories, which should only help during the upcoming testing process anyway:
1. Medical alert
1. Bogus gift card offer
1. Work at home scam
1. Government grant scam
1. New medicare card
1. Can you hear me?
1. Virtual kidnapping
1. Grandparent scam
1. Jury eligibility

Unfortunately other integration issues as well as a lack of proof of concept testing for this new classifier due to lack of complete training data meant that it couldn't be incorporated into the application for this checkpoint. For future work, we hope to have the original classifier working with the application first and then eventually transition into using the new classifier parameters in the java application.

Scam likelihood calculation sources:
1. http://www.pewresearch.org/fact-tank/2017/01/12/evolution-of-technology/
1. http://www.mtab.com/statistics-say-phone-scams-escalating/
1. https://www.theverge.com/2018/1/1/16837814/robocall-spam-phone-call-increase-2017-ftc-report
1. https://www.robokiller.com/robocalltaskforce
1. https://www.scamwatch.gov.au/about-scamwatch/scam-statistics?scamid=all&date=2018-02
