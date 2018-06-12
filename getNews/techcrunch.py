from news import News
import feedparser
import html
import pickle 
import re

class Techcrunch(News):
    
    def __init__ (self):
        News.__init__(self,'techcrunch')

    
    # overriding
    def cleanHtml (self,raw_html):
        cleanr = re.compile('<.*?>(Read More){0,1}')
        cleantext = re.sub(cleanr,'',raw_html)
        return cleantext       

    def get_feed(self):
        
        feeds = {'App' : {'apps':'https://techcrunch.com/apps/feed/'},
                'Hard' : {'gadgets':'https://techcrunch.com/gadgets/feed/','hardware':'https://techcrunch.com/hardware/feed/'}}
                
        article_list = []
        
        for label,urldict in feeds.items() :
            
            for section,url in urldict.items():
                
                parsed_data = feedparser.parse(url)
                # prev 넣어야
                items = self.item_after_date(self.prev_max_date[section],parsed_data)
                
                if len(items) == 0:
                    continue
                else:
                    self.prev_max_date[section] = self.max_entry_data(items)
                
                for item in items :
                    
                    article = {}
                    article['press'] = 'Techcrunch'
                    article['link'] = self.to_utf(item.link)
                    article['pubdate'] = self.to_utf(item.published)
                    article['label'] = label
                    article['author'] = self.to_utf (item.authors[0]['name'])
                    article['title'] = self.to_utf(item.title)
                    article['summary'] = self.to_utf(html.unescape(self.cleanHtml(item.summary)))
                    article['summary'] = self.remove_white_space(article['summary'])
                    article['pubdate_struct'] = item.published_parsed

                    #read more 지울 방법 생각
                    if 'media_thumbnail' in item :
                        article['thumbnail'] = self.to_utf(item.media_thumbnail[0]['url'])
                    
                    else:
                        if 'media_content' in item :
                            article['thumbnail'] = self.to_utf(item.media_content[0]['url'])
                        else:
                            article['thumbnail'] = None
                            
                    article_list.append(article)
        
        with open ('techcrunch.pkl','wb') as output:
            pickle.dump(self.prev_max_date,output,pickle.HIGHEST_PROTOCOL)
        
        return article_list
 
        
'''     
일단 생략하기로 결정
if 'tags' in item:
    length = len(item.tags)
    article['tag'] = ''
    for i,tag in enumerate(item.tags) :
        article['tag'] += tag['term']
        if i != length-1:
            article['tag'] += ','
else :
    article['tag'] = None
'''  
#self.to_utf(parsed_data['feed']['title'])
