

cordova.define("com.fasyl.aimbrainplugin.aimbrainPlugin", function(require, exports, module) {
var PLUGIN_NAME = 'aimbrainPlugin';

function aimbrainPlugin() {
	this.enrol = function (arg0, success, error) {
     cordova.exec(success, error, PLUGIN_NAME, "enrol", [arg0]);
    };
  this.authenticate = function (arg0, success, error) {
     cordova.exec(success, error, PLUGIN_NAME, "authenticate", [arg0]);
   };
}

window.aimbrainPlugin = new aimbrainPlugin();

});