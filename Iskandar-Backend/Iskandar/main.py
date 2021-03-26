import configparser
import logging.config
import sys
import subprocess
import time
from os import path
#from watchdog.observers import Observer
from ini import ini_reader, ini_writer
# from lighting import lighting
from communication import server
#from file_watcher.handler import MyHandler


def main():
    logger.info("Starting...")

    # TODO: Once bluetooth connection has been established, ask for ssid and password and run the connect method
    # Note: CONNECT METHOD ONLY WORKS ON THE PI ITSELF AS IT CALLS A SHELL SCRIPT.

    active_connection = True

    if not active_connection:
        connect()

    try:
        config = ini_reader.LoadConfig()
    except configparser.Error as exc:
        logger.error('Could not load configuration. Exiting', exc)
        sys.exit(-1)

    is_configured = config.is_configured()

    if not is_configured:
        connect()
    else:
        start()


def connect():
    # TODO: Start the bluetooth code and await a connection
    # TODO: Retrieve the SSID and Password
    # TODO: Map them to the variables below

    ssid = 'TEST'
    password = 'TEST'

    subprocess.call(['sudo', 'sh', 'testing.sh', ssid, password], stdout=subprocess.DEVNULL)

    # TODO: Need to check for network connection here before we spin up the server

    # TODO: Need to ship the IP Address of the PI through bluetooth to the mobile app to use.

    # If we have a connection let's fire up the server. The mobile app should auto prompt for
    # weather, traffic, calendar information... But just in case we should ship with stock values like NYC

    start()


def start():
    interface_thread = server.Interface()

    interface_thread.setDaemon(True)

    interface_thread.start()

    # lighting_thread = lighting.Lighting()
    #
    # lighting_thread.start()

    try:
        while True:
            time.sleep(1)
            # lighting_thread.refresh()

    except KeyboardInterrupt:
        # lighting_thread.join()

        logger.warning('Stopping...')
        exit(0)


if __name__ == '__main__':
    log_file_path = path.join(path.dirname(path.abspath(__file__)), '../config/logging.conf')

    logging.config.fileConfig(log_file_path)
    logger = logging.getLogger(__name__)

    main()
