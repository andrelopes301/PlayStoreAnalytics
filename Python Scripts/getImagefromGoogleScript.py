# This is a sample Python getImageFromGoogleScript.


import requests
from bs4 import BeautifulSoup


def get_image_from_google(appname):
    text = appname + 'app google play'
    url = 'https://www.google.com/search?q={0}&tbm=isch'.format(text)
    content = requests.get(url).content
    soup = BeautifulSoup(content, 'html.parser')
    images = soup.select('img', limit=2)
    images_url = images[1]['src']
    print(images_url)

    return images_url

# for image in images:
# print(images[1].get('src'))


get_image_from_google("express vpn")