import socket
from urllib import request

from bs4 import BeautifulSoup

user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36'
headers = {
    'User-Agent': user_agent,
    'Connection': 'keep-alive'}


def obtainProblem(url):
    req_comment = request.Request(url=url, headers=headers)
    socket.setdefaulttimeout(3)

    state = False
    while not state:
        try:
            html = request.urlopen(req_comment).read().decode('utf-8')
            state = True
        except socket.timeout:
            state = False
    print(html)
    soup = BeautifulSoup(html, "html.parser")
    comments = soup.find_all('div', 'linke-fc-pipeline-job_src_comp_Common_Collapse_index.less_1_box')
    for div in comments:
        text = div.select("div.div.div > span")[0]
        print(text.get_text() + '\n')


if __name__ == '__main__':
    url = 'https://code.alipay.com/AntGraph/AntGraph/pipelines/5ed0f72c59a1a49a9c2fc457'
    obtainProblem(url)
