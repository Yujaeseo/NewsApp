import feedparser
import re
import html
import pickle 
from news import News

class Cnet(News):
    
    def __init__(self):
        News.__init__(self,'cnet')
    
    def cleantitle (self,raw_html):
        cleanr = re.compile('\s*- CNET')
        cleantext = re.sub(cleanr,'',raw_html)
        return cleantext
    
    def get_feed(self):
        
        feeds = {'App' :{'android-update':'https://www.cnet.com/rss/android-update/'}, 
                 'Hard':{'most-popular-products':'https://www.cnet.com/rss/most-popular-products/'} }
        
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
                    article['press'] = 'Cnet'
                    article['link'] = item.link
                    article['pubdate'] = item.published
                    article['label'] = label            
                    article['author'] =item.author
                    article['pubdate_struct'] = item.published_parsed
                    article['title'] = self.cleantitle(item.title)
                    article['summary'] = html.unescape(self.cleanHtml(item.summary))
                    article['summary'] = self.remove_white_space(article['summary'])
                    article['thumbnail'] = None
                    
                    if 'media_thumbnail' in item:
                        if 'url' in item.media_thumbnail[0]:
                            article['thumbnail'] = item.media_thumbnail[0]['url']
                            
                    articleList.append(article)
        
        with open ('cnet.pkl','wb') as output:
            pickle.dump(self.prev_max_date,output,pickle.HIGHEST_PROTOCOL)
        
        return articleList
  