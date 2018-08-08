from process_keywords import is_phishing
from sms_analyzer import SMSSpamCollection_Analyzer

common = 'keybank/common_words.txt'
keybank = 'keybank/keybank_f1.txt'

my_tests = ['transcripts/my_tests/test1.txt',
            'transcripts/my_tests/test2_long.txt',
            'transcripts/my_tests/test2_short.txt',
            'transcripts/my_tests/test3_long.txt',
            'transcripts/my_tests/test3_short.txt',
            'transcripts/my_tests/test4.txt',
            'transcripts/my_tests/test5.txt',
            'transcripts/my_tests/test6.txt',
            'transcripts/my_tests/test7.txt',
            'transcripts/my_tests/test8.txt',
            'transcripts/my_tests/test9.txt'
            ]

def verbose_simple_spam():   
    redact = True
    verbose = True
    
    transcript = ['transcripts/my_tests/test2_long.txt']
    match = is_phishing(keybank, transcript, common, redact, verbose)
    print('Perfectage high risk words: ' + str(match) + '%')

def verbose_simple_not_spam():
    redact = True
    verbose = True
    
    transcript = ['transcripts/my_tests/test7.txt']
    match = is_phishing(keybank, transcript, common, redact, verbose)
    print('Perfectage high risk words: ' + str(match) + '%')


# run tests
def main():

    redact = True
    verbose = False

    for f in my_tests:
        print('\n')
        print f
        transcript = [f]
        match = is_phishing(keybank, transcript, common, redact, verbose)
        print('Perfectage high risk words: ' + str(match) + '%')

#verbose_simple_spam()
#verbose_simple_not_spam()
main()
