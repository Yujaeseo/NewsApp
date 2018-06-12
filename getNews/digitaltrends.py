import feedparser
import html
import pickle
from news import News

class DigitaltrendsFeed(News) :

    def __init__ (self):
        News.__init__(self,'digitaltrends')
    
    def get_feed (self):

        feed = 'https://www.digitaltrends.com/feed/'
        parsed_data = feedparser.parse (feed)
        items = self.item_after_date (self.prev_max_date['tech'],parsed_data)
        
        #press = self.to_utf(parsed_data['feed']['title'])
        
        articleList = []
        
        if len(items) == 0:
            return articleList
        
        else :
            self.prev_max_date['tech'] = self.max_entry_data(items)
            
            with open ('digitaltrends.pkl','wb') as output:
                pickle.dump(self.prev_max_date,output,pickle.HIGHEST_PROTOCOL)
        
        for item in items :

            article = {}
            article['press'] = 'digitaltrends'
            article['link'] = self.to_utf(item.link)
            article['label'] = 'tech'
            article['pubdate'] = self.to_utf(item.published)
            # category가 존재하지 않는다.            
            article['author'] = self.to_utf(item.authors[0]['name'])
            article['pubdate_struct'] = item.published_parsed
            article['title'] = self.to_utf(item.title)
            article['summary'] = self.to_utf( html.unescape(self.cleanHtml(item.summary)) )


            article['thumbnail'] = None

            if 'links' in item :
                for link in item['links']:
                    if 'image' in link['type']:
                        article['thumbnail'] = self.to_utf(link['href'])
                        
            articleList.append(article)

        return articleList

# test content 

'''
if(__name__ == '__main__'):
    for articleBypress in get_feed() :
        print(articleBypress)
        print('\n')
'''