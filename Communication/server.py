import socket
import threading

class server():
    def __init__(self):
        self.HOST = socket.gethostbyname(socket.gethostname())
        self.PORT = 50602
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.HOST, self.PORT)) #Bind the socket on the network

    def start_server(self):
        print("Starting server...")
        self.sock.listen()           #Listen for clients
        try:
            self.client, self.addr = self.sock.accept() #Accept
            try:
                with self.client:
                    print('Connected by', self.client.getpeername())
                    while True:
                        self.data = self.client.recv(1024)  # data sent from client is stored
                        if not self.data:
                            break
                        print("Received %s" % (self.data))
                        self.client.sendall(self.data)     # send back the data
            except self.client.error:
                print("Error with Client")
            finally:
                self.client.close()
        except self.sock.error:
            print("Error with Server")
        finally:
            self.sock.close() #Close the socket
    
    def __exit__(self):
        self.sock.close()

server = server()
server.start_server()
