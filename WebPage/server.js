const fs = require('fs'); //Node's File system api
const express = require('express'); //Express.js - Install with 'npm install express --save'
const qstring = require('querystring'); // Install with 'npm install querystring --save'
const uuidV4 = require('uuid/v4');//UUID - Install with 'npm install uuid --save'
const cookieParser = require('cookie-parser');//Install with 'npm install cookie-parser --save'
const mkdirp = require('mkdirp');//Install with 'npm install mkdirp --save'
const researchDir = "";//Relative path to Directory for research data
var collectData = true;

//Initializing Express server on localhost:3000
var app = express();
app.use(cookieParser());
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
//Sends out cookie to keep track of user
app.get('/welcome.html', function (req, res) {
    res.cookie('completedSurvey', 0);
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
     res.cookie('uuid', uuidV4());
    sendHTML(req, res);
});
//Training Page. Handles Post data from Pre-Questionnare
app.post('/training.html', function (req, res) {
    var user = req.cookies.uuid;
    var bodyData = "";
    //Recieves data from request
    req.on('data', function (chunk) {
        bodyData += chunk.toString();
    });
    //When done recieving data...
    req.on('end', function () {
        //Use Query String to parse data to make easily accessible
        var preData = qstring.parse(bodyData);
        var questions = convertPreqData(preData);
        //Write the csv when done converting data
        mkdirp(researchDir+"/"+user,'0777',function (err) {
            if (err) {
                console.error(err)
            } else {
                fs.writeFile(researchDir+"/"+user+"/"+user+ '_Questions.csv', questions, (err) => {
                if (err) throw err;
                //console.log('File saved.');
                sendHTML(req, res);
                });
            }
        });
    });
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
    var user = req.cookies.uuid;
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
        //Append to the csv when done converting data
        fs.appendFile(researchDir+"/"+user+"/"+user+ '_Questions.csv', questions, (err) => {
            if (err) throw err;
            //console.log('File appended.');
        });
    });
    sendHTML(req, res);
});
app.get('/thanks.html', function (req, res) {
    sendHTML(req, res);
});

//Send HTML file to the client from the directory of the server.
function sendHTML(req, res) {
    fs.readFile(__dirname + req.path, function(err, data){
        res.set({'Content-Type' : 'text/html'});
        res.status(200);
        res.send(data.toString());
    });
}

//Converts the Pre Questionnare data into csv format
//Also checks if the checkbox input is empty.
function convertPreqData(preData) {
    //Headers for the csv
    var tmp = "Age,Gender,Education,School,Degree Seeking,Degree Pursuement,Employed,Length of Employment,Computer Use,Computer Programs,Video Games,Video Game Genres,Devices,Video Game Experience,Video Games Usage\n";
    //PreQ data for the csv
    tmp += preData['age'] + "," + preData['gender'] + "," + preData['education'] + "," + preData['school'] + "," + preData['degree']
     + "," + preData['degree_yrs'] + "," + preData['employment'] + "," + preData['emp_years'] + "," + preData['cpu_usage'];
     if(preData['cpu_use'] === undefined) {
         tmp += ",None";
     } else {
         var split = preData['cpu_use'].toString().replace(",","\,");
         tmp += ",\"" + split + "\"";
     }
     if(preData['vgp'] === undefined) {
         tmp += ",None";
     } else {
         var split = preData['vgp'].toString().replace(",","\,");
         tmp += ",\"" + split+ "\"";
     }
     if(preData['vgt'] === undefined) {
         tmp += ",None";
     } else {
         var split = preData['vgt'].toString().replace(",","\,");
         tmp += ",\"" + split+ "\"";
     }
     if(preData['dev'] === undefined) {
         tmp += ",None";
     } else {
         var split = preData['dev'].toString().replace(",","\,");
         tmp += ",\"" + split+ "\"";
     }
     tmp += "," + preData['vg_exp'] + "," + preData['vg_freq'];
     return tmp;
}

//Converts the Post Questionnare data into csv format
function convertPostqData(postData) {
    var tmp= "\n\nListen to Conscience,Anticipate Needs,Respect Others,Get Along with People,Fairness,Stick to Rules,Law Enforcement,Good Word,Cooperation over Competition,Return Extra Chance,Cheat on Taxes,Follow Through,People are Moral,Finish what I Start,Retreat From Others,Doubt Things,Short-changed Life,Avoid Contact,People Lie,Hard to Forgive,Coordinate with Hospital,Goal Attainment,Reliability,Hospital Schedulers,Successful Hospital"
    tmp += "\n" + postData['cons'] + "," + postData['needs'] + "," + postData['respect'] + "," + postData['along'] + "," + postData['fair'] + "," + postData['rules']
     + "," + postData['laws'] + "," + postData['word'] + "," + postData['coop'] + "," + postData['change'] + "," + postData['taxes'] + "," + postData['plans']
      + "," + postData['moral'] + "," + postData['start'] + "," + postData['retreat'] + "," + postData['doubts'] + "," + postData['life'] + "," + postData['avoid']
       + "," + postData['lie'] + "," + postData['forgive'] + "," + postData['coordinate'] + "," + postData['attainment'] + "," + postData['rely'] + "," + postData['schedulers']
        + "," + postData['success'];
    return tmp;
}