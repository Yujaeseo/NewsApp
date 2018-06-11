import feedparser
import html
import pickle 
from news import News

class Mashable (News):
    
    def __init__(self):
        News.__init__(self,'mashable')
    
    def removebackslash (self,string):
        return string.replace(u'\n',u' ')
                              
    def get_feed(self):
        
        feeds = {'tech' :{'tech':'https://mashable.com/tech/rss/'} }
        
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
                    article['press'] = 'Mashable'
                    article['link'] = item.link
                    article['pubdate'] = item.published
                    article['label'] = label            
                    article['author'] =item.authors[0]['name']
                    article['title'] = item.title
                    article['summary'] = html.unescape(self.cleanHtml(item.summary))
                    article['summary'] = self.remove_white_space(article['summary'])
                    article['summary'] = self.removebackslash(article['summary'])
                    article['thumbnail'] = None
                    article['pubdate_struct'] = item.published_parsed
                   
                    if 'media_thumbnail' in item:
                        if 'url' in item.media_thumbnail[0]:
                            article['thumbnail'] = item.media_thumbnail[0]['url']
                            
                    articleList.append(article)
        
        with open ('mashable.pkl','wb') as output:
            pickle.dump(self.prev_max_date,output,pickle.HIGHEST_PROTOCOL)
        
        return articleList
    