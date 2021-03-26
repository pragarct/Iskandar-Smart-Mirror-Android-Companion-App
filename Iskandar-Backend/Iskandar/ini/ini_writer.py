import logging
import configparser


class WriteConfig:

    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.parser = configparser.ConfigParser()

        self.parser.read('../config/config.ini')

    def set_weather_zip_code(self, zip_code):
        self.parser.set('weather', 'zip_code', str(zip_code))

    def set_weather_city(self, city):
        self.parser.set('weather', 'city', city)

    def set_weather_state(self, state):
        self.parser.set('weather', 'state', state)

    def set_weather_units(self, units):
        self.parser.set('weather', 'units', units)

    def set_traffic_home_address(self, home):
        self.parser.set('traffic', 'home', home)

    def set_traffic_work_school(self, work_school):
        self.parser.set('traffic', 'work_school', work_school)

    def set_lighting_red(self, red):
        self.parser.set('lighting', 'red', red)

    def set_lighting_green(self, green):
        self.parser.set('lighting', 'green', green)

    def set_lighting_blue(self, blue):
        self.parser.set('lighting', 'blue', blue)

    def set_lighting_brightness(self, brightness):
        self.parser.set('lighting', 'brightness', brightness)

    def set_calendar_ical(self, ical):
        self.parser.set('calendar', 'ical', ical)

    def write_config(self):
        self.parser.set('initial_config', 'is_configured', 'true')

        with open('../config/config.ini', 'w') as configfile:
            self.parser.write(configfile)

        configfile.close()
