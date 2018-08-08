import threading
from random import *
import time
import sched

from process_keywords import is_phishing
from process_keywords import get_bank_words
from sms_analyzer import SMSSpamCollection_Analyzer

global keyword_list
global keyword_count

# to do: display key metrics
def display_work():

    global keyword_list
    global keyword_count
    
    while True:
        for i in xrange(len(keyword_count)):
            if (keyword_count[i] > 0):
                print('PARENT: ' + keyword_list[i] + ' : ' + str(keyword_count[i]))
        time.sleep(30)

# returns spam classification from data set
def is_spam(word):
    if (word == 'spam'):
        return 'Spam'
    return 'Not spam'

# repeatedly mimic call occurrence for testing
def child_task():
    
    global keyword_list
    global keyword_count
    smsfile = 'transcripts/smsspamcollection/SMSSpamCollection'
    common = 'keybank/common_words.txt'
    keybank = 'keybank/keybank_f1.txt'
    
    for t in xrange(5):
        call_words_list = SMSSpamCollection_Analyzer(smsfile)
        spam = is_spam(call_words_list[0])
        call_words_list = call_words_list[1:]
        for word in call_words_list:
            try:
                index = keyword_list.index(word)
                keyword_count[index] += 1
            except ValueError:
                continue
        match_percent = is_phishing(keybank, call_words_list, common, False, False)
        print("test: " + str(spam) + " = " + str(match_percent) + '\n')
        #print call_words_list
        time.sleep(randint(5, 10))

# parent thread main function
def spawn_threads(num_users):

    global keyword_list
    global keyword_count

    # initial setup
    keybank = 'keybank/keybank_f1.txt'
    keyword_list = list(get_bank_words(keybank))
    keyword_count = [0] * len(keyword_list)

    # spawn threads
    for child in xrange(1, num_users):
        threading.Thread(target=child_task).start()

    # begin display analysis
    display_work()

if __name__ == '__main__':
    spawn_threads(10)
    print
