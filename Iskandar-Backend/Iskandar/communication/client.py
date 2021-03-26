import socket
import time


class Client:
    def __init__(self):
        self.HOST = 'localhost'
        self.PORT = 8080
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    def connect(self):
        print('Connecting to %s:%s' % (self.HOST, self.PORT))
        self.sock.connect((self.HOST, self.PORT))

    def send_command(self, command):
        string_bytes = command.encode()
        self.sock.sendall(string_bytes)  # Send a bytes object (b)
        time.sleep(2)
        self.sock.close()