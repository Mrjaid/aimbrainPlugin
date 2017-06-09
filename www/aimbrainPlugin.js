var exec = require('cordova/exec');

exports.enrol = function(arg0, success, error) {
    exec(success, error, "aimbrainPlugin", "enrol", [arg0]);
};

exports.authenticate = function(arg0, success, error) {
    exec(success, error, "aimbrainPlugin", "authenticate", [arg0]);
};
