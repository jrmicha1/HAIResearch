const fs = require('fs'); //Node's File system api
const express = require('express'); //Express.js - Install with 'npm install express --save'
const qstring = require('querystring'); // Install with 'npm install querystring --save'
var collectData = true;

//Initializing Express server on localhost:3000
var app = express();
app.listen(3000, function(){
    console.log("Server started! At http://localhost:3000");
});

//Landing Page redirects to either No Survey or Welcome page
//depending if you're taking data
app.get('/', function (req, res) {
    //Right now simple boolean. Will be replaced later.
    if(collectData) {
        res.redirect("/welcome.html");
    } else {
        red.redirect("/nosurvey.html");
    }
});
//Welcome Page
app.get('/welcome.html', function (req, res) {
    sendHTML(req, res);
});
//No Survey Page
app.get('/nosurvey.html', function (req, res) {
    sendHTML(req, res);
});
//Sends the javacript/jQuery files when asked for.
app.get('/Javascript*', function (req, res) {
    res.sendFile(req.path,{"root": __dirname});
});
//Pre-Questionnare Page
app.get('/preQ.html', function (req, res) {
    sendHTML(req, res);
});
//Training Page. Handles Post data from Pre-Questionnare
app.post('/training.html', function (req, res) {
    //TODO Handle data from PreQ Post.
    sendHTML(req, res);
});
//Sends window.js when called for in Training page.
app.get('/window.js', function (req, res) {
    res.sendFile("window.js",{"root": __dirname});
});
//Sends resources(images) when called for.
app.get('/Resources/*', function (req, res) {
    res.sendFile(req.path,{"root": __dirname});
});
//Tutorial page
app.get('/tutorial.html', function (req, res) {
    sendHTML(req, res);
});
//Game page
app.all('/game.html', function (req, res) {
    sendHTML(req, res);
});
//Post Questionnare page
app.all('/postQ.html', function (req, res) {
    sendHTML(req, res);
});
//Thanks Page. Handles Post data from Post Questionnare
app.post('/thanks.html', function (req, res) {
    var bodyData = "";
    //Recieves data from request
    req.on('data', function (chunk) {
        bodyData += chunk.toString();
    });
    //When done recieving data...
    req.on('end', function () {
        //Use Query String to parse data to make easily accessible
        var postData = qstring.parse(bodyData);
        var questions = convertPostqData(postData);
        //Write the csv when done converting data
        fs.writeFile('test.csv', questions, (err) => {
            if (err) throw err;
            console.log('File saved.');
        });
    });
    sendHTML(req, res);
});

//Send HTML file to the client from the current directory.
function sendHTML(req, res) {
    fs.readFile(__dirname + req.path, function(err, data){
        res.set({'Content-Type' : 'text/html'});
        res.status(200);
        res.send(data.toString());
    });
}

//Converts the Post Questionnare data into csv format
function convertPostqData(postData) {
    var tmp = postData['cons'] + "," + postData['needs'] + "," + postData['respect'] + "," + postData['along'] + "," + postData['fair'] + "," + postData['rules']
     + "," + postData['laws'] + "," + postData['word'] + "," + postData['coop'] + "," + postData['change'] + "," + postData['taxes'] + "," + postData['plans']
      + "," + postData['moral'] + "," + postData['start'] + "," + postData['retreat'] + "," + postData['doubts'] + "," + postData['life'] + "," + postData['avoid']
       + "," + postData['lie'] + "," + postData['forgive'] + "," + postData['coordinate'] + "," + postData['attainment'] + "," + postData['rely'] + "," + postData['schedulers']
        + "," + postData['success'];
    return tmp;
}