$(function () {
    "use strict";

    var header = $('#header');
    var rooms = $('#rooms');
    var content = $('#content');
    var users = $('#users');
    var input = $('#input');
    var status = $('#status');
    var myName = false;
    var author = null;
    var logged = false;
    var socket;
    var traceId = 0;

    input.removeAttr('disabled').focus();

    var handler = new zodiark.EnvelopeHandler();
    handler.onEnvelope = function (envelope) {
        var message = envelope.getMessage().getData();
        ;
        try {
            var json = atmosphere.util.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        input.removeAttr('disabled').focus();
        if (!logged && myName) {
            logged = true;
            status.text(myName + ': ').css('color', 'blue');
        } else {
            var me = json.author == author;
            var date = typeof(json.time) == 'string' ? parseInt(json.time) : json.time;
            addMessage(json.author, json.message, me ? 'blue' : 'black', new Date(date));
        }
    };

    handler.onError = function (error) {
        console.error(error);
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
        logged = false;
    }
    handler.onClose = function (response) {
        content.html($('<p>', { text: 'Server closed the connection after a timeout' }));
        input.attr('disabled', 'disabled');
    }


    socket = new zodiark.Builder().url(document.location.toString()).build();

    socket.handler(handler).open();

    input.keydown(function (e) {
        if (e.keyCode === 13) {
            var msg = $(this).val();

            // First message is always the author's name
            if (author == null) {
                author = msg;
            }

            var message = zodiark.Message();
            message.path("/chat/room1").data({ author: author, message: msg });

            var envelope = new zodiark.Envelope()
                .traceId(++traceId)
                .path("/command/execute")
                .to("Server")
                .message(message);

            socket.send(envelope);
            $(this).val('');

            input.attr('disabled', 'disabled');
            if (myName === false) {
                myName = msg;
            }
        }
    });

    function addMessage(author, message, color, datetime) {
        content.append('<p><span style="color:' + color + '">' + author + '</span> @ ' + +(datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
            + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
            + ': ' + message + '</p>');
    }
});
