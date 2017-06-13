module.exports = {
        enrol: function (userId,aimbrainAPIKey,aimbrainAPISecret, success, error) {
        var passParams = [];
                passParams.push(userId);
                passParams.push(aimbrainAPIKey);
                passParams.push(aimbrainAPISecret);
            cordova.exec(success, error, 'aimbrainPlugin', "enrol", passParams);
        },
        authenticate: function (userId,aimbrainAPIKey,aimbrainAPISecret, success, error) {
        var passParams = [];
                passParams.push(userId);
                passParams.push(aimbrainAPIKey);
                passParams.push(aimbrainAPISecret);
            cordova.exec(success, error, 'aimbrainPlugin', "authenticate", passParams);
        }
    };