
var text = [
"<b>Objective:</b></br></br>You are the Bayview Medical Clinic manager who must schedule</br>doctors and hospital staff to treat as many patients as possible.",
"<b>How to Play:</b></br></br>There are two types of patients:</br>&bull; Standard</br>&bull; Surgery</br></br>The standard patient requires a doctor and a nurse in order to begin recovering,</br>and a surgery patient requires a surgeon and a nurse to begin recovering.</br></br>As soon as a patient is assigned to a room and has both resources assigned,</br>a timer will begin to count down to their full recovery.",
"You will also have help! The neighboring medical clinic,</br>OneLife Medical Center, is willing to share their staff with you.</br>Both you, and the Center, can share resources by sending and</br>requesting particular staff to help save the greatest number of patients.</br></br><img src=\"./Resources/sharing.png\" style=\"width:330px;height:150px;\">",
"<b>Interface:</b></br></br>The main parts of the interface are:</br>Your panel, OneLife Medical's panel,</br>the patient rooms, the timer, and your controls.</br></br><img src=\"./Resources/interface.png\" style=\"width:600px;height:400px;\">",
"To treat patients, you need to assign them to a room with the correct hospital staff.</br>Your hospital only has 6 rooms. Once a staff member is assigned to a room,</br>they cannot be unassigned until the patient has recovered.</br></br><img src=\"./Resources/assigning.png\" style=\"width:600px;height:546px;\">",
"Within the waiting room, each circle represents a patient.</br>The color of the circles reflects the number of patients waiting:</br>Green: 1-2</br>Yellow: 3-4</br>Red: 5-6</br></br><img src=\"./Resources/stats.png\" style=\"width:200px;height:450px;\"></br></br>The maximum number of patients each hospital can have waiting is 6.</br>Once the waiting room is full, no new patients can enter the waiting room.</br>Your available staff will be displayed in the Bayview Medical panel.</br>You can see OneLife's available staff and queue color as well.</br>For each patient treated in <b>EITHER</b> hospital, your score is increased by 1.",
"After assigning all necessary staff members</br>to a patient, their treatment will begin.</br>After 60 seconds, their treament will be complete.</br>From here, you have to collect your resources by</br>clicking on the room before you use them again.</br></br><img src=\"./Resources/collect.png\" style=\"width:350px;height:450px;\">",
"<b>The Game:</b></br></br>You will play two games.</br>The first game will be for practice and</br>will allow you to get used to the controls.</br>This game will last 2 minutes.</br> You will then begin the full game.</br>The full game will last 8 minutes and a</br>final score will be shown at the end.</br></br>Please contact the proctor if you have any further questions."
];

var page = 0;

window.onload = function init() {
    var element = document.getElementById("tng");
	element.innerHTML = text[page];
    
	document.getElementById("back").style.visibility = 'hidden';
    document.getElementById("continue").style.visibility = 'hidden';
    
    document.getElementById("next").innerText = "Please wait...";
    document.getElementById("next").disabled = true;
    setInterval(function() { document.getElementById("next").disabled = false;document.getElementById("next").innerText = 'Next'; }, 4000);
    
    document.getElementById("next").onclick = function() {
        page++;
        element.innerHTML = text[page];

        if (page == 7) {
            document.getElementById("next").style.visibility = 'hidden';
            document.getElementById("continue").style.visibility = 'visible';
            
            document.getElementById("continue").innerText = "Please wait...";
            document.getElementById("continue").disabled = true;
            setInterval(function() { document.getElementById("continue").disabled = false;document.getElementById("continue").innerText = 'Continue'; }, 8000);
        } else {
            document.getElementById("back").style.visibility = 'visible';
            
            document.getElementById("next").innerText = "Please wait...";
            document.getElementById("next").disabled = true;
            setInterval(function() { document.getElementById("next").disabled = false;document.getElementById("next").innerText = 'Next'; }, 10000);
        }
    };
    
    document.getElementById("back").onclick = function() {
        page--;
        element.innerHTML = text[page];
        
        if (page == 0) {
            document.getElementById("back").style.visibility = 'hidden';
        } else {
            document.getElementById("next").style.visibility = 'visible';
            document.getElementById("continue").style.visibility = 'hidden';
        }
    };
}


