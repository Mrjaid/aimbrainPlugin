

cordova.define("com.fasyl.aimbrainplugin.aimbrainPlugin", function (require, exports, module) {
    module.exports = {
        enrol: function (arg0, success, error) {
            cordova.exec(success, error, 'aimbrainPlugin', "enrol", [arg0]);
        },
        authenticate: function (arg0, success, error) {
            cordova.exec(success, error, 'aimbrainPlugin', "authenticate", [arg0]);
        }
    };
});