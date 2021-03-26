import logging
import configparser


class LoadConfig:

    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.config = configparser.ConfigParser(strict=False, interpolation=None)

        self.config.read('../config/config.ini')

    def get_weather_zip(self):
        try:
            return self.config.getint('weather', 'zip_code')
        except ValueError as exc:
            self.logger.error('Zip code is not in the correct format.', exc)
            return None

    def get_weather_location(self):
        return self.config.get('weather', 'city')

    def get_weather_location_state(self):
        return self.config.get('weather', 'state')

    def get_weather_units(self):
        return self.config.get('weather', 'units')

    def get_home_address(self):
        return self.config.get('traffic', 'home')

    def get_work_school_address(self):
        return self.config.get('traffic', 'work_school')

    def get_lighting_red(self):
        try:
            return self.config.getint('lighting', 'red')
        except ValueError as exc:
            self.logger.error('Red rgb value is not in the correct format. Defaulting to 255.', exc)
            return 255

    def get_lighting_green(self):
        try:
            return self.config.getint('lighting', 'green')
        except ValueError as exc:
            self.logger.error('Green rgb value is not in the correct format. Defaulting to 255.', exc)
            return 255

    def get_lighting_blue(self):
        try:
            return self.config.getint('lighting', 'blue')
        except ValueError as exc:
            self.logger.error('Blue rgb value is not in the correct format. Defaulting to 255.', exc)
            return 255

    def get_lighting_brightness(self):
        return self.config.get('lighting', 'brightness')

    def get_calendar_ical(self):
        return self.config.get('calendar', 'ical')

    def is_configured(self):
        try:
            configured = self.config.getboolean('initial_config', 'is_configured')
        except ValueError:
            configured = self.config.get('initial_config', 'is_configured')

        if not configured:
            return False

        return configured
