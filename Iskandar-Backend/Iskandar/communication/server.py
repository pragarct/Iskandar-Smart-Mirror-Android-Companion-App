import json
import logging.config
import urllib.request
from threading import Thread

from flask import Flask, request, render_template, jsonify
from ini import ini_reader, ini_writer
from communication import client


class Interface(Thread):

    __app = Flask(__name__, template_folder='../front_end/templates', static_folder='../front_end/static')

    def __init__(self):
        Thread.__init__(self)

        self.logger = logging.getLogger(__name__)

    @__app.route('/')
    def home():

        return render_template('index.html')

    @__app.route('/location', methods=['GET'])
    def get_location():
        config = ini_reader.LoadConfig()

        if config is None:
            return jsonify("Could not load configuration. Unable to change values."), 500

        zip_code = config.get_weather_zip()

        city = config.get_weather_location()

        state = config.get_weather_location_state()

        home_address = config.get_home_address()

        work_school = config.get_work_school_address()

        units = config.get_weather_units()

        location_dict = {'city': city, 'state': state, 'zip': zip_code, 'home': home_address, 'work': work_school, 'units': units}

        location_json = json.dumps(location_dict)

        return location_json, 200

    @__app.route('/lighting', methods=['GET'])
    def get_lighting():
        config = ini_reader.LoadConfig()

        if config is None:
            return jsonify("Could not load configuration. Unable to change values."), 500

        red = config.get_lighting_red()

        green = config.get_lighting_green()

        blue = config.get_lighting_blue()

        brightness = config.get_lighting_brightness()

        lighting_dict = {'red': red, 'green': green, 'blue': blue, 'brightness': brightness}

        lighting_json = json.dumps(lighting_dict)

        return lighting_json, 200

    @__app.route('/lighting', methods=['PUT'])
    def update_lighting():
        config_writer = ini_writer.WriteConfig()

        rgb = request.args.get('rgb')

        brightness = request.args.get('brightness')

        brightness_percent = (int(brightness) / 100)

        rgb_string = rgb.replace('#', '')

        red_hex, green_hex, blue_hex = rgb_string[0:2], rgb_string[2:4], rgb_string[4:6]

        red = int(red_hex, 16)

        green = int(green_hex, 16)

        blue = int(blue_hex, 16)

        if red is not None:
            config_writer.set_lighting_red(str(red))
        if green is not None:
            config_writer.set_lighting_green(str(green))
        if blue is not None:
            config_writer.set_lighting_blue(str(blue))
        if brightness is not None:
            config_writer.set_lighting_brightness(str(brightness_percent))

        config_writer.write_config()

        return 'Updated', 202

    @__app.route('/location', methods=['PUT'])
    def update_location():
        config_writer = ini_writer.WriteConfig()

        city = request.args.get('city')

        state = request.args.get('state')

        zip_code = request.args.get('zip')

        home = request.args.get('home')

        work = request.args.get('work')

        units = request.args.get('units')

        if city is not None:
            config_writer.set_weather_city(city)
        if state is not None:
            config_writer.set_weather_state(state)
        if zip_code is not None:
            config_writer.set_weather_zip_code(zip_code)
        if home is not None:
            config_writer.set_traffic_home_address(home)
        if work is not None:
            config_writer.set_traffic_work_school(work)
        if units is not None:
            config_writer.set_weather_units(units)

        config_writer.write_config()

        # refresh_client = client.Client()

        # refresh_client.connect()

        # refresh_client.send_command('Refresh')

        return 'Updated', 202

    @__app.route('/calendar', methods=['PUT'])
    def update_calendar():

        url = request.args.get('url')

        urllib.request.urlretrieve(url, '../config/Calendar.ics')

        return 'Updated', 202

    @__app.route('/calendar_read', methods=['GET'])
    def get_calendar_read():

        f =  open("../config/Calendar.ics", "r")

        if f is None:
            return jsonify("Could not open ical file."), 500

        data = f.read()
        f.close()

        cal_dict = {'ical': data}

        cal_json = json.dumps(cal_dict)

        return cal_json, 200

    @__app.route('/calendar', methods=['GET'])
    def get_calendar():
        config = ini_reader.LoadConfig()

        if config is None:
            return jsonify("Could not load configuration. Unable to change values."), 500

        cal_link = config.get_calendar_ical()

        cal_dict = {'ical': cal_link}

        cal_json = json.dumps(cal_dict)

        return cal_json, 200

    @__app.route('/configured', methods=['GET'])
    def get_is_configured():
        config = ini_reader.LoadConfig()

        if config is None:
            return jsonify("Could not load configuration. Unable to change values."), 500

        configured = config.is_configured()

        configured_dict = {'is_configured': configured}

        configured_json = json.dumps(configured_dict)

        return configured_json, 200

    @__app.route('/refresh', methods=['PUT'])
    def refresh():
        url = request.args.get('url')

        urllib.request.urlretrieve(url, '../config/Calendar.ics')

        # refresh_client = client.Client()

        # refresh_client.connect()

        # refresh_client.send_command('Refresh')

        return 'Refreshed', 202

    def run(self):
        self.logger.info('Starting interface thread.')

        self.__app.run(debug=False, host='0.0.0.0')
