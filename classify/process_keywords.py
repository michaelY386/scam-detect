#!/bin/python2
import string
import fileinput
import re

# file scope number redaction
def redact_numbers(transcript_file):
    for line in fileinput.input(transcript_file, inplace=True, backup='.bak'):
        print(re.sub("\d", "X", line))

# clear non ascii characters
def clear_nonascii(char_string):
    return "".join(i for i in char_string if ord(i)<128)

# import words from a file into list
def get_bank_words(bank_file):
    with open(bank_file) as f:
        words = [line.rstrip('\n') for line in f]
    return set(words)

# import words from a call transcript with optional redaction
def get_transcript_words(transcript_file, redact, verbose):
    with open(transcript_file) as f:
        words = []
        for line in f:
            for word in line.split():
                word = word.strip()
                word = clear_nonascii(word)
                if (redact):
                    word = re.sub("\d", "X", word)
                word = word.translate(None, string.punctuation)
                word = word.lower()
                words.append(word)
    if (verbose):
        print('\nOriginal call transcript:')
        print(words)
    return words

# get subset of like words between two sets
def get_same_words(keywords, words, verbose):
    subset = words.intersection(keywords)
    if (verbose):
        print('\nKeywords found in transcript: ')
        print(subset)
        print('\n')
    return subset

# remove common words in English language
def remove_common_words(common, transcript, verbose):
    subset = transcript.difference(common)
    if (verbose):
        print('\nReduced call transcript:')
        print(subset)
    return subset

# get count of word occurence in list
def get_occurrence_count(word, bank):
    return bank.count(word)

# calculate perceptage of high risk words in reduced transcript
def get_percentage_subset(subset, master, reduced_transcript_len):
    total = 0
    for word in subset:
        total = total + get_occurrence_count(word, master)
    try:
        percent = (float(total) / reduced_transcript_len)
    except ZeroDivisionError:
        percent = 0.0
    #print('Match percentage: ' + str(percent) + '\n')
    return percent

# primary methohd for phishing scam analysis
def is_phishing(keyword_file, transcript, common_file, redact, verbose):
    key_words = get_bank_words(keyword_file)
    common_words = get_bank_words(common_file)

    if (transcript[0][-4:] == '.txt'):
        call_words_list = get_transcript_words(transcript[0], redact, verbose)
    else:
        call_words_list = transcript
        
    call_words_set = set(call_words_list)
    
    strict_call_words = remove_common_words(common_words, call_words_set, verbose)
    high_risk_words = get_same_words(key_words, strict_call_words, verbose)
    
    match_percent = get_percentage_subset(high_risk_words, call_words_list, len(strict_call_words))
    return (match_percent * 100)


if __name__ == '__main__':
    print()
