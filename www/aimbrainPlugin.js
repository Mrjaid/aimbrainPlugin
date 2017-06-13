module.exports = {
        enrol: function (userId,aimbrainAPIKey,aimbrainAPISecret, success, error) {
        var passParams = [];
                passParams.put(userId);
                passParams.put(aimbrainAPIKey);
                passParams.put(aimbrainAPISecret);
            cordova.exec(success, error, 'aimbrainPlugin', "enrol", passParams);
        },
        authenticate: function (userId,aimbrainAPIKey,aimbrainAPISecret, success, error) {
        var passParams = [];
                passParams.put(userId);
                passParams.put(aimbrainAPIKey);
                passParams.put(aimbrainAPISecret);
            cordova.exec(success, error, 'aimbrainPlugin', "authenticate", passParams);
        }
    };