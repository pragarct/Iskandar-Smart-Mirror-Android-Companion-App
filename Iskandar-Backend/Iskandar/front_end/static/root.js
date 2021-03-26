var app = angular.module('mirrorMain', []);

app.config(['$interpolateProvider', function($interpolateProvider) {
    $interpolateProvider.startSymbol('{a');
    $interpolateProvider.endSymbol('a}');
}]);

app.controller('mirrorCtrl', function($scope, $http) {
  $scope.globals = {};
  $scope.globals.tileLocations = ['calendar', '', 'traffic',
                                  '', '', '',
                                  'clock', 'weather'];
});

app.directive('customDirective', function($http) {
  return {
    restrict: 'E',
    scope: {
      loc: '=',
      tiles: '='
    },
    templateUrl: "static/partials/customDirective.html",
    link: function(scope) {
      // Config Init Section -- Start
      scope.config = {
        weather: {},
        clock: {},
        calendar: {},
        traffic: {}
      };
      // Config Init Section -- End

      // createClockData - format data for the clock
      scope.createClockData = function() {
        function startTime() {
          scope.config.clock.today = new Date();
          var hr = scope.config.clock.today.getHours();
          var min = scope.config.clock.today.getMinutes();
          var sec = scope.config.clock.today.getSeconds();
          ap = (hr < 12) ? "<span>AM</span>" : "<span>PM</span>";
          hr = (hr == 0) ? 12 : hr;
          hr = (hr > 12) ? hr - 12 : hr;
          //Add a zero in front of numbers<10
          hr = checkTime(hr);
          min = checkTime(min);
          sec = checkTime(sec);
          document.getElementById("clock").innerHTML = hr + ":" + min + ":" + sec + " " + ap;

          scope.config.clock.months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
          scope.config.clock.days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
          scope.config.clock.day = {};
          scope.config.clock.day.curWeekDay = scope.config.clock.days[scope.config.clock.today.getDay()];
          scope.config.clock.day.curDay = scope.config.clock.today.getDate();
          scope.config.clock.day.curMonth = scope.config.clock.months[scope.config.clock.today.getMonth()];
          scope.config.clock.day.curYear = scope.config.clock.today.getFullYear();
          scope.config.clock.day.date = scope.config.clock.day.curWeekDay + ", " + scope.config.clock.day.curDay + " " + scope.config.clock.day.curMonth + " " + scope.config.clock.day.curYear;
          document.getElementById("date").innerHTML = scope.config.clock.day.date;

          var time = setTimeout(function(){ startTime() }, 500);
        }
        function checkTime(i) {
          if (i < 10) {
              i = "0" + i;
          }
          return i;
        }

        startTime();
      }

      // createWeatherData - format data for the weather
      scope.createWeatherData = function() {
        // Current Weather
        scope.config.weather.html = 'https://api.openweathermap.org/data/2.5/weather'
        scope.config.weather.key = '9f8516f2d3aeb32aeb29d1c797aa8ebd';

        scope.config.weather.request2 = $http({
          method: 'get',
          url: scope.config.weather.html,
          params: { q : scope.config.weather.location , appid : scope.config.weather.key, units : scope.config.weather.units }
        });

        scope.config.weather.request2.then(function(data) {
          scope.config.weather.data = data.data;
          console.log(data.data);
          callFutureForecast();
        }, function(){
          console.log('Error connecting to OpenWeatherMap Weather API');
        });

        // Future Weather
        function callFutureForecast() {
          scope.config.weather.future = {};
          scope.config.weather.future.html = 'https://api.openweathermap.org/data/2.5/onecall'
          scope.config.weather.future.lat = scope.config.weather.data.coord.lat;
          scope.config.weather.future.lon = scope.config.weather.data.coord.lon;
          scope.config.weather.future.key = '9f8516f2d3aeb32aeb29d1c797aa8ebd';
          scope.config.weather.future.exclude = 'current,minutely,hourly';

          scope.config.weather.future.request2 = $http({
            method: 'get',
            url: scope.config.weather.future.html,
            params: { lat : scope.config.weather.future.lat , lon : scope.config.weather.future.lon, exclude : scope.config.weather.future.exclude, appid : scope.config.weather.future.key, units : scope.config.weather.units }
          });

          scope.config.weather.future.request2.then(function(data) {
            scope.config.weather.future.data = data.data;
            console.log(data.data);
          }, function(){
            console.log('Error connecting to OpenWeatherMap OneCall API');
          });
        }

        scope.getFutureDate = function(day, index) {
          var date = new Date();
          var index2 = date.getDay() + index;
          index2 = (index2 > 6) ? index2 - 7 : index2;
          var days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
          day.name = days[index2];
          return day.name;
        }

        scope.getFutureIcon = function(day) {
          var id = day.weather[0].id;
          var icon = 'wi-owm-' + id;
          return icon;
        }
      }

      function getItemiCal(event, tag) {
        var data = null
        event[1].forEach( function(items){
          if (items[0] == tag) {
            data = items[3];
          }
        });
        return data;
      }

      scope.createCalendarData = function() {
        var icalData = ICAL.parse(scope.config.calendar.icalData);
        console.log(icalData);
        var events = icalData[2];

        scope.config.calendar.events = [];
        events.forEach( function(e){
          var event = {};
          var dateStr = getItemiCal(e, 'dtstart')
          event.date = dateStr;
          event.summary = getItemiCal(e, 'summary');
          event.color = '#' + intToRGB(hashCode(dateStr));

          var date = new Date();
          var dateInfo = event.date.split('-');
          if (date.getFullYear() == dateInfo[0] && date.getMonth() < dateInfo[1])
            scope.config.calendar.events.push(event);
        });
      }

      scope.isPastDate = function (e) {
        var dateStr = e.date;
        var dateInfo = dateStr.split('-');
        var date = new Date();
        if ((date.getFullYear() < dateInfo[0]) ||
            (date.getFullYear() == dateInfo[0] && date.getMonth() < dateInfo[1]) ||
            (date.getFullYear() == dateInfo[0] && date.getMonth() == dateInfo[1] && date.getDate() < dateInfo[2])) {
          return false;
        }
        return true;
      }

      function hashCode(str) { // java String#hashCode
        var hash = 0;
        for (var i = 0; i < str.length; i++) {
           hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        return hash;
    } 
    
    function intToRGB(i){
        var c = (i & 0x00FFFFFF)
            .toString(16)
            .toUpperCase();
    
        return "00000".substring(0, 6 - c.length) + c;
    }

      scope.logData = function( item ) {
        console.log(item)
        return item
      }

      function getDaysInMonth(month, year) {
        var date = new Date(year, month, 1);
        var days = [];
        var weeks = [];
        var weekCount = 0;
        
        var today = new Date();
        var todayDate = today.getDate();
        for ( var i = 0; i < date.getDay(); i++) {
          days.push({
            name: 'invalid',
            num: null,
            color: null
          })
        }

        while (date.getMonth() === month) {
          // get day name
          var daysNameList = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

          if ( daysNameList[date.getDay()] == 'Sun' && days.length > 0) {
            weekCount++;
            weeks.push(days);
            days = [];
          }

          var color = 'white';
          var dateDate = date.getDate();
          if (dateDate < todayDate) {
            color = '#838383'
          } else if (dateDate == todayDate) {
            color = '#69C9FF'
          }

          var events = []
          scope.config.calendar.events.forEach( function( e ) {
            var dateInfo = e.date.split('-');
            if (date.getFullYear() == dateInfo[0] && date.getMonth() + 1 == dateInfo[1] && date.getDate() == dateInfo[2]) {
              events.push(e.color);
            }
          })

          days.push({
            name: daysNameList[date.getDay()],
            num: date.getDate(),
            color: color,
            events: events
          })

          
          date.setDate(date.getDate() + 1);
        }

        for ( var i = date.getDay(); i < 7; i++) {
          days.push({
            name: 'invalid',
            num: null,
            color: null,
            events: []
          })
        }

        weeks.push(days);
        return weeks;
      }

      // Calendar Section -- End
      // Traffic Section -- Start
      scope.createTrafficData = function() {
        scope.config.traffic.token = 'pk.eyJ1IjoiaXNrYW5kYXJtaXJyb3IiLCJhIjoiY2ttaHFmcDRzMDl5bzJ4dDR6Ymo2MTlxcCJ9.Dj0C151HDifa7Kb_rvmIzw'
        
        scope.config.traffic.geo.html = 'https://api.mapbox.com/geocoding/v5/mapbox.places/';
        scope.config.traffic.geo.home = encodeURIComponent(scope.config.traffic.home);
        scope.config.traffic.geo.request1 = $http({
          method: 'get',
          url: scope.config.traffic.geo.html + scope.config.traffic.geo.home + '.json',
          params: { types: 'address', access_token: scope.config.traffic.token }
        });

        scope.config.traffic.geo.request1.then(function(data) {
          var data = data.data.features[0];
          scope.config.traffic.dir.home = data.center;
          console.log(scope.config.traffic.dir.home);
          getWorkCoords()
        }, function(){
          console.log('Error connecting to MapBox Geocoding API');
        });

        function getWorkCoords() {
          scope.config.traffic.geo.work = encodeURIComponent(scope.config.traffic.work);
          scope.config.traffic.geo.request2 = $http({
            method: 'get',
            url: scope.config.traffic.geo.html + scope.config.traffic.geo.work + '.json',
            params: { types: 'address', access_token: scope.config.traffic.token }
          });

          scope.config.traffic.geo.request2.then(function(data) {
            var data = data.data.features[0];
            scope.config.traffic.dir.work = data.center;
            console.log(scope.config.traffic.dir.work);
            getDirection()
          }, function(){
            console.log('Error connecting to MapBox Geocoding API');
          });
        }

        function getDirection()  {
          // [0] = long, [1] = lat
          var homeLong = encodeURIComponent(scope.config.traffic.dir.home[0])
          var homeLat = encodeURIComponent(scope.config.traffic.dir.home[1])
          var workLong = encodeURIComponent(scope.config.traffic.dir.work[0])
          var workLat = encodeURIComponent(scope.config.traffic.dir.work[1])
          scope.config.traffic.dir.html = 'https://api.mapbox.com/directions/v5/mapbox/driving/'
          scope.config.traffic.dir.request = $http({
            method: 'get',
            url: scope.config.traffic.dir.html + homeLong + ',' + homeLat + ';' + workLong + ',' + workLat,
            params: { alternatives: 'false', geometries: 'geojson', steps: 'false', access_token: scope.config.traffic.token }
          });

          scope.config.traffic.dir.request.then(function(data) {
            var route = data.data.routes[0];
            console.log(route);
            scope.config.traffic.dir.distance = Math.round((route.distance*0.000621371192) * 10) /10;
            scope.config.traffic.dir.duration = Math.ceil(route.duration / 60) + ' minute(s)';
            console.log(scope.config.traffic.dir.duration)
          }, function(){
            console.log('Error connecting to MapBox Directions API');
          });
        }
      }
      // Traffic Section -- End

      // Config Loading Section -- Start
      setTimeout(function() {
        if (scope.tiles[scope.loc] == 'clock') {
          scope.createClockData();
        } else if (scope.tiles[scope.loc] == 'weather') {
          scope.config.weather.request = $http({
            method: 'get',
            url: 'location',
            params: {}
          });

          scope.config.weather.request.then(function(json) {
            scope.config.weather.location = json.data.city +","+ json.data.state +",usa";
            scope.config.weather.units = json.data.units;
            scope.config.weather.unitSymbol = (scope.config.weather.units = 'imperial') ? '°F' : (scope.config.weather.units = 'celsius') ? '°C' : 'K';
            scope.createWeatherData();
          }, function(){
            console.log('Error grabbing weather information from config.ini file');
          });
        } else if (scope.tiles[scope.loc] == 'calendar') {
          scope.config.calendar.request = $http({
            method: 'get',
            url: 'calendar_read',
            params: {}
          });

          scope.config.calendar.request.then(function(json) {
            scope.config.calendar.icalData = json.data.ical;
            scope.createCalendarData();
            
            var today = new Date();
            monthNum = today.getMonth();
            yearNum = today.getFullYear();
            scope.config.calendar.weeks = getDaysInMonth(monthNum, yearNum);
            console.log(scope.config.calendar.weeks);
            var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            scope.config.calendar.month = months[today.getMonth()];
          }, function(){
            console.log('Error grabbing calendar information from config.ini file');
          });
        } else if (scope.tiles[scope.loc] == 'traffic') {
          scope.config.traffic.geo = {};
          scope.config.traffic.dir = {};

          scope.config.traffic.request = $http({
            method: 'get',
            url: 'location',
            params: {}
          });

          scope.config.traffic.request.then(function(json) {
            scope.config.traffic.home = json.data.home
            scope.config.traffic.work = json.data.work;
            scope.createTrafficData();
          }, function(){
            console.log('Error grabbing traffic information from config.ini file');
          });
        }
      }, 500)
      // Config Loading Section -- End

      // END OF FILE, AT LEAST FOR OUR USE
    }
  }
})
