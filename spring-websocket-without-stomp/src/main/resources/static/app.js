
var ws;
var httpurl="http://realkeyip.in:8086/";
var wsurl="realkeyip.in:8084/"
//wsurl="localhost:8085/"
//httpurl="http://localhost:8086/";
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
	ws = new WebSocket('ws://'+wsurl+'/name');
	ws.onmessage = function(data){
		console.log(data);
		showGreeting(data.data);
	}
	 setConnected(true);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
	var jsonVal={
			"gameId": $("#gameId").val(),
			"state": "JOINROOM",
			
			"players": [{
				"name": $("#name").val(),
				"displayName":"Jay",
				"active":'Y'
			}]			
		}
	var data = JSON.stringify(jsonVal);
	console.log(data);
    ws.send(data);
}
function dropGame(){
	var jsonVal={
			"gameId": $("#gameId").val(),
			
			"state": "DROP",
			"players": [{
				"name": $("#name").val()
			}]			
		}
	var data = JSON.stringify(jsonVal);
	console.log(data);
    ws.send(data);
	
}

function showGreeting(message) {
    $("#greetings").append("<tr><td> " + message + "</td></tr>");
}

function getgame(message) {
	$.get(httpurl+"/game/getGameId?gameType=100&userId="+$("#name").val(), function(data, status){
		console.log(data);
		console.log("test");
		$("#gameId").val(data);
	
	});
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#getgame" ).click(function() { getgame(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#drop" ).click(function() { dropGame(); });
});

