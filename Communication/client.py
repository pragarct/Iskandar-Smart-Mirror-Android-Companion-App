import socket
import time

class client():
    def __init__(self):     
        self.HOST = '' # TODO: Make configureable - You need to manually enter this for now 
        self.PORT = 50602
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    def connect(self):
        print('Connecting to %s:%s' % (self.HOST, self.PORT))
        self.sock.connect((self.HOST, self.PORT))

    def send_command(self, command):
        self.sock.sendall(command)  #Send a bytes object (b)
        data = self.sock.recv(1024) #Get response
        return data
    
    def __exit__(self):
        self.sock.close()

client = client()
client.connect()
client.send_command(b"Testing...")
time.sleep(1)
client.send_command(b"Example 123213")
time.sleep(1)
client.send_command(b"set_theme(abc, 123)")
