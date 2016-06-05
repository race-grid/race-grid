var post = function(url, data){
    console.log("Posting to " + url);
    return $.ajax({
            url: url,
            type: "POST",
            crossDomain: true,
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (response) {
                return response;
            },
            error: function (xhr, status) {
                console.log("ERROR!");
            }
    });
}

var get = function(url){
    console.log("Getting from " + url);
    return $.ajax({
            url: url,
            type: "GET",
            crossDomain: true,
            success: function (response) {
                return response;
            },
            error: function (xhr, status) {
                console.log("ERROR!");
            }
    });
}

var postApi = function(url, data){
    return post("http://localhost:8080/api/" + url, data);
}

var getApi = function(url){
    return get("http://localhost:8080/api/" + url);
}

var newUser = function(name){
    postApi("users?name=" + name).then(function(response){
        console.log(response);
        print(response);
    });

}

var users = function(){
    getApi("users").then(function(response){
       console.log(response);
       print(response);
   });
}

var newGameVsAi = function(userId, userHash, numOpponents){
    postApi("new-slow-game-vs-ai" +
        "?userId=" + userId +
        "&userHash=" + userHash +
        "&numOpponents=" + numOpponents)
        .then(function(response){
            console.log(response);
            print(response);
        });
}

var possibleMoves = function(userId, userHash, gameId){
    getApi("possible-moves" +
        "?userId=" + userId +
        "&userHash=" + userHash +
        "&gameId=" + gameId).then(function(response){
            console.log(response);
            print(response);
        });
}

var print = function(json){
    $("#content").html(JSON.stringify(json, null, 1));
}

var makeMove = function(userId, userHash, gameId, destination){
    postApi("make-move", {
        userId: userId,
        userHash: userHash,
        gameId: gameId,
        destination: destination
    }).then(function(response){
      console.log(response);
      print(response);
    });
}

var users = function(){
    getApi("users").then(function(response){
        console.log(response);
        print(response);
    });
}

var games = function(){
    getApi("games").then(function(response){
        console.log(response);
        print(response);
    });
}

console.log("Setting up...");
newUser("jonathan");
newGameVsAi("1", "a194e5fe-4413-481b-b00b-354560bf89eb", 2);
makeMove("1", "a194e5fe-4413-481b-b00b-354560bf89eb", "2", {x: 1, y: 1});
