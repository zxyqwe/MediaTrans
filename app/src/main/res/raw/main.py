#!/usr/bin/env python
"""
Start Udp Echo and HTTP
"""

import threading
import socket
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import urllib
import urlparse


class HttpHand(BaseHTTPRequestHandler):
    """
    HTTP Handler
    """

    def transDicts(self, params):
        dicts = {}
        if len(params) == 0:
            return
        params = params.split('&')
        for param in params:
            dicts[param.split('=')[0]] = param.split('=')[1]
        return dicts

    def do_POST(self):
        datas = self.rfile.read(int(self.headers['content-length']))
        datas = urllib.unquote(datas).decode("utf-8", 'ignore')
        datas = self.transDicts(datas)
        parsed_path = urlparse.urlparse(self.path)

        if parsed_path.path=="update":
            pass
        elif parsed_path.path=="upload":
            pass

        message = "aaa"

        self.send_response(200)
        self.end_headers()
        self.wfile.write(message)


class HttpServer(threading.Thread):
    """
    HTTP Thread
    """

    def run(self):
        server = HTTPServer(('', 8000), HttpHand)
        server.serve_forever()


if __name__ == "__main__":
    HSER = HttpServer()
    HSER.start()
    PORT = 5555
    UDPSOC = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    UDPSOC.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    UDPSOC.bind(('', PORT))
    while True:
        MSG, ADDR = UDPSOC.recvfrom()
        print "Got data", MSG, "from", ADDR
        UDPSOC.sendto(MSG, ADDR)
