
var text = [
"<b>Objective:</b></br>You are the Bayview Medical Clinic manager who must schedule doctors </br>and hospital staff to treat as many patients as possible.	</br></br>",

"<b>How to Play:</b></br>There are 2 types of patients:<br>-Standard<br>-Surgery<br><br>The standard patient requires a doctor and a nurse in order to begin recovering,</br>and a surgery patient requires a surgeon and a nurse to begin recovering.</br></br>As soon as a patient is assigned to a room and has both resources assigned,</br> a timer will begin to count down to their full recovery.",

"You will also have help! The neighboring medical clinic, OneLife Medical Center,</br>is willing to share their staff with you. Both you, and the Center, can share resources </br> by sending and requesting particular staff to help save the greatest numnber of patients.	</br></br><img src=\"./Resources/sharing.png\" style=\"width:330px;height:150px;\">",

"<b>Interface:</b></br>There are four main parts to the interface: Your panel, OneLife </br>Medical's panel, rooms, and your controls.</br></br><img src=\"./Resources/interface.png\" style=\"width:600px;height:319px;\"></br></br>",

"To treat patients, you need to assign them to a room with the correct hospital</br>staff. Your hospital only has 6 rooms. Once a staff member is assigned to a room, they</br>cannot be unassigned until the patient has recovered.</br></br><img src=\"./Resources/assigning.png\" style=\"width:600px;height:450px;\"></br>",

"</br>Within the waiting room, each circle represents a patient. The color of</br>the circles reflects the number of patients waiting:</br>Green: 1-2</br>Yellow: 2-4</br>Red: 5-6</br></br><img src=\"./Resources/stats.png\" style=\"width:200px;height:450px;\"></br></br>The maximum number of patients each hospital can have waiting is 6. Once the waiting room is full, no new patients</br>can enter the waiting room.</br></br>Your available staff will be displayed in the Bayview Medical panel.</br>You can see OneLife's available staff and queue color as well. For each	</br>patient treated in <b>EITHER</b> hospital, your score is increased by 1.</br></br>",

"After assigning all necessary staff members to a patient, their treatment will begin.</br>After 60 seconds, their treament will be complete. From here, you can collect your resources</br>by clicking on the room, before you use them again.</br></br><img src=\"./Resources/collect.png\" style=\"width:350px;height:450px;\"></br></br>",

"<b>Trial run:</b></br>You will play two runs. The first will begin when you go to the next</br>page by clicking the button at the bottom of the screen. This first</br>run will be for practice and will allow you to get used to the controls.</br>This will last 2 minutes and then you will begin the full trial.</br>The full trial will be 8 minutes long and a final score will be shown</br>at the end. Please contact the proctor if you have any further questions.</br>"
];

var page = 0;
var currentPage = document.getElementById("page")

window.onload = function init() {

    var element = document.getElementById("main");
	element.innerHTML = text[0];
	document.getElementById("back").style.visibility = 'hidden';
	
	document.getElementById("back").onclick = function() {
		if (page > 0){
			page--;
			element.innerHTML = text[page];
			document.getElementById("next").style.visibility = 'visible';
		}
		
		if (page == 0){
			document.getElementById("back").style.visibility = 'hidden';
		}else{
			document.getElementById("back").style.visibility = 'visible';
		}
		
	};
	document.getElementById("next").onclick = function() {
		if (page < 7){
			page++;
			element.innerHTML = text[page];
			document.getElementById("back").style.visibility = 'visible';
		}
		
		if (page == 7){
			document.getElementById("next").style.visibility = 'hidden';
		}else{
			document.getElementById("next").style.visibility = 'visible';
		}
		
	};

}


