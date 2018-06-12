#feed getter 
from digitaltrends import DigitaltrendsFeed
from cnet import Cnet
from techcrunch import Techcrunch
from mashable import Mashable
from thenextweb import TheNextWeb

import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from nltk.stem import WordNetLemmatizer

import os
import pickle

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

#connect to firebase
cred = credentials.Certificate('newsfeedapp-41f8d-firebase-adminsdk-xexxn-b2a904989d.json')
firebase_admin.initialize_app(cred, {'databaseURL' : 'https://newsfeedapp-41f8d.firebaseio.com/'})

vect_tokenizer = TfidfVectorizer().build_analyzer()
lemmatizer = WordNetLemmatizer()

# custom tokenizer
def custom_tokenizer (doc) :
    tokens = vect_tokenizer(doc)
    return [lemmatizer.lemmatize(token) for token in tokens]

# get classifier and vectorizer
    
with open(os.path.join('pkl_object','classifier.pkl'),'rb') as file :
    clf = pickle.load(file)

with open(os.path.join('pkl_vect','vect.pkl'),'rb') as file:
    tokenizer = pickle.load(file)
    vect = pickle.load(file)
    
# get news from each websites
    
cnetFeeds = Cnet().get_feed()
digitalTrendsFeeds = DigitaltrendsFeed().get_feed()
mashableFeeds = Mashable().get_feed()
techcrunchFeeds = Techcrunch().get_feed()
thenextwebFeeds = TheNextWeb().get_feed()

# combine feeds

totalFeeds = cnetFeeds + techcrunchFeeds + thenextwebFeeds + mashableFeeds + digitalTrendsFeeds
feedLen = len(totalFeeds)

# classify unclassified news topic 

for num in range(feedLen):
    
    if totalFeeds[num]['thumbnail'] == None :
        totalFeeds[num]['thumbnail'] = 'None'
        
    if totalFeeds[num]['label'] == 'tech' or totalFeeds[num]['label'] == 'Tech':
        
        tfidf = vect.transform( pd.Series(totalFeeds[num]['summary']) )
        prob = clf.predict_proba(tfidf)
        
        if (prob[0][1] - prob[0][0]) >= 0.15 :
            totalFeeds[num]['label'] = 'App'
            
        elif (prob[0][1] - prob[0][0] <= -0.15):
            totalFeeds[num]['label'] = 'Hard'
            
        else :
            totalFeeds[num]['label'] = 'Tech'

# get reference to firebase DB 
root = db.reference()

# insert news into firebase DB
for newsnum in range (feedLen) :
    
    article = root.child('news').push({

        'title': totalFeeds[newsnum]['title'],
        'topic' : totalFeeds[newsnum]['label'],
        'press' : totalFeeds[newsnum]['press'],
        'pubdate' : totalFeeds[newsnum]['pubdate'],
        'pubdate_ms' : time.mktime(totalFeeds[newsnum]['pubdate_struct']) * 1000,
        'author' : totalFeeds[newsnum]['author'],
        'thumbnail' : totalFeeds[newsnum]['thumbnail'],
        'newsLink' : totalFeeds[newsnum]['link'],
        'summary' : totalFeeds[newsnum]['summary']
        
    })

