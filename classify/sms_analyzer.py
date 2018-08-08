from random import *
import string

# clear non ascii characters
def sms_clear_nonascii(char_string):
    return "".join(i for i in char_string if ord(i)<128)

# get number of SMS messages (lines) in file
def getLineCount(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1

# pick a random message and return words in list format
def SMSSpamCollection_Analyzer(fname):

    max_line = getLineCount(fname)
    line = randint(0, max_line)
    words = []
    
    with open(fname) as f:
        line_count = 0

        for curr_line in f:
            if (line == line_count):
                for word in curr_line.split():
                    word = word.strip()
                    word = sms_clear_nonascii(word)
                    word = word.translate(None, string.punctuation)
                    word = word.lower()
                    words.append(word)
                break
            else:
                line_count += 1
    return words

if __name__ == '__main__':
    print()
