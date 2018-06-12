from news import News
import feedparser
import re
import html
import pickle

class TheNextWeb(News):
    
    def __init__(self):
        News.__init__(self,'thenextweb')
    
    #overriding 
    def cleanHtml(self,raw_html):
        cleanr = re.compile('<.*?>')
        cleantext = re.sub(cleanr,'',raw_html)
        cleanr = re.compile('This story continues at The Next.*')
        cleantext = re.sub(cleanr,'',cleantext,re.IGNORECASE)
        return cleantext
    
    def get_feed(self):
        
        feeds = {'App' :{'apps':'https://thenextweb.com/section/Apps/feed/'}, 
                 'Hard':{'gear':'https://thenextweb.com/section/gear/feed/'},
                 'Tech':{'tech':'https://thenextweb.com/section/tech/feed/'} }
        
        articleList = []
        
        for label,urldict in feeds.items():
            
            for section,url in urldict.items():
            
                parsed_data = feedparser.parse (url)
                items = self.item_after_date(self.prev_max_date[section],parsed_data)
                
                if len (items) == 0:
                    continue
                else :
                    self.prev_max_date[section] = self.max_entry_data(items)
                
                for item in items :
            
                    article = {}
                    article['press'] = 'Thenextweb'
                    article['link'] = item.link
                    article['pubdate'] = item.published
                    article['label'] = label            
                    article['author'] =item.authors[0]['name']
                    article['title'] = item.title
                    article['summary'] = html.unescape(self.cleanHtml(item.summary))
                    article['summary'] = self.remove_white_space(article['summary'])
                    article['thumbnail'] = None
                    article['pubdate_struct'] = item.published_parsed
           
                    if 'links' in item :
                        links = item.links
                        if links[-1]['rel'] == 'enclosure':
                            article['thumbnail'] = links[-1]['href']
            
                    articleList.append(article)
        
        with open ('thenextweb.pkl','wb') as output:
            pickle.dump(self.prev_max_date,output,pickle.HIGHEST_PROTOCOL)
        
        return articleList

'''       
if 'tags' in items:
    length = len(items.tags)
    article['tag'] = ''
    for i,tag in enumerate(items.tags) :
        article['tag'] += tag['term']
        if i != length-1:
            article['tag'] += ','
else :
    article['tag'] = None
'''