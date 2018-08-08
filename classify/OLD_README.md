# Classification of Calls as Scams

Algorithm to determine the likelihood of a call being a scam, or legitimate.

# Method of Execution

Simply run the process_keywords.py script in the classify directory, once the repository is cloned. In case Python running capability is not already set up, install Python for Windows using https://www.python.org/downloads/windows/, open IDLE (the Python GUI), navigate to the process_keywords.py script and use F5 as the keyboard shortcut to run it. We are using Python 2.7.10, but any later version should work.

# Current Work:
  The current algorithm focuses on statistical processing and simple redaction of call transcript:
1. The python program process_keywords.py takes a transcripts as input from the transcripts folder.
2. It replaces any numeric value present in the transcript with 'x'es, as a primitive redaction method to clear out personal information such as bank account numbers, etc.
3. Next, excluding common words (read from the flat file present in the keybank folder), the program counts the number of words in the transcript that are indicative a scam. Words indicative of a scam are keywords present in the keybank file, that were created based on threat profile research done from a number of sources including Phone Scam Information posted on the FTC's website.
4. Finally, the percentage of 'suspicious' words is printed.
  
  We also have a real time analysis module that is mostly functional. It attempts to mimic an incoming stream of call information from multiple users of the app using the statistical process above by creating multiple worker threads that each process different transcripts at random intervals and report detection of high risk keywords found over time. Currently we are using publically available SMS text message spam and non spam data sets as our test data for this module, so the reported percentages are not indicative of the success of our statistical analysis. However, using this information, we can test the overall functionality of the analysis tool that a professional 3rd party reviewer (potentially representing an insurance company or bank) might be using to analyze large amounts of user data and detect trends in phishing schemes, etc. This tool is available via the process_real_time.py script, which can simply be run as-is with a python interpreter.
  
# Assumptions and Notes:
  As integration between the Java modules and the classifier could not be set up successfully for this checkpoint, we are testing the classifier against test transcripts of real scams found online, as well as our own transcripts - both malicious and legitimate.

# Results and Conclusions:
  The classifier spits out a higher percentage when the call transcript is shorter (or when analyzing the first 20 seconds of the call rather than the whole call). This is due to the fact that our keyword bank is primitive, and needs to be expanded and weightages for each keyword has not been assigned yet.
  
# Future Work:
1. Rather than using this statistical processor, we intend to use a logistic regression algorithm to assign weights to keywords. 
2. We intend to update the basic keyword bank by adding keywords to it based on scam alerts received via email from FTC. We will also implement a retention policy to remove keywords based on the freshness of the news.
3. We need to fix communication issues with the server, in order to store the keywords and blacklisted numbers on the database, along with passing the transcript as input and results of classification as output.
4. We intend to improve redaction techniques if possible, to limit the amount of personal information retained in transcripts, so the primary's privacy can be protected from the reviewer.
