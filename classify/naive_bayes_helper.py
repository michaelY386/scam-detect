#!/bin/python2

import os, string

scam_path = 'transcripts/training/scam'
not_scam_path = 'transcripts/training/not_scam'

# clear non ascii characters
def clear_nonascii(char_string):
    return "".join(i for i in char_string if ord(i)<128)

# remove common words in English language
def remove_common_words(transcript):
    with open('keybank/common_words.txt') as f:
        words = set([line.rstrip('\n') for line in f])
    subset = transcript.difference(words)
    return subset

# get full dictionary of all words in the scam and non scam training data
def gather_full_dict():
    all_words = []

    # collect words in scam training files
    for file in [f for f in os.listdir(scam_path) if f.endswith('.txt')]:
        with open(os.path.join(scam_path, file)) as f:
            for line in f:
                for word in line.split():
                    # sanitize and redact word before adding to list
                    word = word.strip()
                    word = clear_nonascii(word)
                    word = word.translate(None, string.punctuation)
                    word = word.lower()
                    all_words.append(word)

    # collect words in non scam training files
    for file in [f for f in os.listdir(not_scam_path) if f.endswith('.txt')]:
        with open(os.path.join(not_scam_path, file)) as f:
            for line in f:
                for word in line.split():
                    # sanitize and redact word before adding to list
                    word = word.strip()
                    word = clear_nonascii(word)
                    word = word.translate(None, string.punctuation)
                    word = word.lower()
                    all_words.append(word)

    #remove duplicates and common words
    all_words = set(all_words)
    key_words = remove_common_words(all_words)
    return list(all_words)


# get scam frequency values
def process_scam_files(full_dict):

    #initialize counts to 0 for each word
    scam_freq = [0.0] * len(full_dict)
    num_scam_files = 0.0

    for file in [f for f in os.listdir(scam_path) if f.endswith('.txt')]:
        with open(os.path.join(scam_path, file)) as f:
            for line in f:
                for word in line.split():
                    # sanitize and redact word before adding to list
                    word = word.strip()
                    word = clear_nonascii(word)
                    word = word.translate(None, string.punctuation)
                    word = word.lower()
                    index = full_dict.index(word)
                    current_value = scam_freq[index]
                    scam_freq[index] = current_value + 1.0
        
        num_scam_files = num_scam_files + 1.0

    #convert counts to frequency percentages
    for idx in xrange(len(scam_freq)):
        scam_freq[idx] = (scam_freq[idx] / num_scam_files)
    
    return scam_freq

# get non scam frequency values
def process_not_scam_files(full_dict):

    #initialize counts to 0 for each word
    not_scam_freq = [0.0] * len(full_dict)
    num_not_scam_files = 0.0

    for file in [f for f in os.listdir(not_scam_path) if f.endswith('.txt')]:
        with open(os.path.join(not_scam_path, file)) as f:
            for line in f:
                for word in line.split():
                    # sanitize and redact word before adding to list
                    word = word.strip()
                    word = clear_nonascii(word)
                    word = word.translate(None, string.punctuation)
                    word = word.lower()
                    index = full_dict.index(word)
                    current_value = not_scam_freq[index]
                    not_scam_freq[index] = current_value + 1.0

        num_not_scam_files = num_not_scam_files + 1.0

    #convert counts to frequency percentages
    for idx in xrange(len(not_scam_freq)):
        not_scam_freq[idx] = (not_scam_freq[idx] / num_not_scam_files)
                    
    return not_scam_freq


# Using the training data transcripts, compile a dictionary of the full set of
# words found in both scam and non scam files, removing only the most common
# words in the english language. For each word in this dictionary, obtain a
# corresponding percentage for the frequency of the word found in scam calls
# and a frequency of the word being found in non scam calls. These frequency
# tables would be written to the server and used by the application's classifier
# to classify the phone call transcript in real time using the Naive Bayes
# bernoulli document model. This model also requires likelihood probability values
# for the overal scam vs. not scam call. These values for adults owning smart
# phones in the United States were calculated manually based on 2017 phishing
# statistics, and would also be written to the server
def get_frequency_tables():
    full_dict = gather_full_dict()
    #print full_dict
    scam_freq = process_scam_files(full_dict)
    #print scam_freq
    not_scam_freq = process_not_scam_files(full_dict)
    #print not_scam_freq

#get_frequency_tables()
