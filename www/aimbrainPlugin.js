var exec = require('cordova/exec');

var PLUGIN_NAME = 'aimbrainPlugin';

var aimbrainPlugin = {
 enrol: function(arg0, success, error) {
    exec(success, error, PLUGIN_NAME, "enrol", [arg0]);
};

authenticate: function(arg0, success, error) {
    exec(success, error, PLUGIN_NAME, "authenticate", [arg0]);
};

  
};

alert('made it here...');
window.aimbrainPlugin = aimbrainPlugin;