var baseURL = "appointment";
//var baseURL = "diary";

//The document ready function
try{
	$(function()
		{
		init();
		}
	);
} catch (e){
	alert("*** jQuery not loaded ***");
}

function init(){
	
	//sets up a dialog box to post to server
	$("#appointmentDetailsAdd").dialog({
		modal:true,
		autoOpen:false,
		title:"Add Appointment",
		minWidth:500,
		minHeight:400
	});
	
	//sets up dialog box to post an update to an existing appointment	
	$("#appointmentDetailsUpdate").dialog({
		modal:true,
		autoOpen:false,
		title:"Edit Appointment",
		minWidth:500,
		minHeight:400
	});
	
	//sets all input box values to nothing, and displays the add appointment dialog box
	$("#addAppointment").click(function(){
		$("#appointmentDate").val("");
		$("#appointmentDuration").val("");
		$("#appointmentDescription").val("");
		$("#appointmentOwner").val("");
		$("#appointmentDetailsAdd").dialog("open",true);
	});	
	
	//closes the add appointment dialog box
	$("#cancelAppointment").click(function(){
		$("#appointmentDetailsAdd").dialog("close");
	});
	
	//closes the update appointment dialog box
	$("#cancelAppointmentUpdate").click(function(){
		$("#appointmentDetailsUpdate").dialog("close");
	});
	
	//calls the save appointment function and closes the dialog box	
	$("#saveAppointment").click(function(){
		saveAppointment();
		$("#appointmentDetailsAdd").dialog("close");
	});
	
	//gets the id  of the selected table entry, calls the update function and closes the dialog box
	$("#saveAppointmentUpdate").click(function(){
		//gets all html tags within the table that has the .selected class applied(should only be 1)
		$("#appointmentsList .selected").each(function()
				{
			//calls thee update appointment
				updateAppointment($(this).attr("id"));
				});
		$("#appointmentDetailsUpdate").dialog("close");
	});
	
	//calls the search function to search between two dates + the owner
	$("#searchAppointments").click(function(){
		searchAppointment();
	});
	
	//gets the id and calls the delete function, passing through the acquired id
	$("#deleteAppointment").click(function()
			{
		//finds the table entry with the selected class applied
			$("#appointmentsList .selected").each(function()
										{
										//calls the delete function with the id being passed through
										deleteAppointment($(this).attr("id"));
										console.log($(this).attr("id"));
										//removes the entry from the html table
										$(this).remove();
										});
			});
	
	//gets parameters from selected table row appends them to the relevant input boxes
	$("#updateAppointment").click(function(){		

		//creates an empty array
		var row = new Array();
		
		//finds the selected row
		$("#appointmentsList .selected").each(function(){
			//creates a local variable to push the row parameters to the previously created array
			var tableData = $("#"+ $(this).attr("id")).find('td');
		    tableData.each(function() { row.push($(this).text()); });
		    
		    //checks id
		    console.log($(this).attr("id"));
		});
	    
	    //checks for correct parameters being picked up
	    console.log(row[0]);
	    console.log(row[1]);
	    console.log(row[2]);
	    console.log(row[3]);

	    //sets the input boxes to the appropriate values
	    $("#appointmentDateUpdate").val(row[0]);
		$("#appointmentDurationUpdate").val(row[1]);
		$("#appointmentDescriptionUpdate").val(row[2]);
		$("#appointmentOwnerUpdate").val(row[3]);
		
		//opens dialog box
		$("#appointmentDetailsUpdate").dialog("open",true);
	});
}


//these functions will call a date picker when the input box is clicked
$( function() {
	$( "#appointmentDateUpdate").datepicker();
});

$( function() {
    $( "#appointmentDate" ).datepicker();
  });

$( function() {
    $( "#startDate" ).datepicker();
  });

$( function() {
    $( "#endDate" ).datepicker();
  });

function saveAppointment(){
	
	//init variables to send to server
	var date = $("#appointmentDate").val();
	var duration = $("#appointmentDuration").val();
	var description = $("#appointmentDescription").val();
	var owner = $("#appointmentOwner").val();
	
	//check format of date -- it should be string
	console.log(date);
	
	//split the date into array with day, month, year
	var s=date.split("/");
	//check if it was split correctly
	console.log("s to string " + s);
	//create a new date object out of array
	var dateU = new Date(Date.UTC(s[2],(s[0]*1-1),(s[1]),0,0,0)).getTime();
	
	var dateU = dateU/1000;
	//check if the date is the same as stated on https://www.unixtimestamp.com/
	console.log("Test: " + dateU);
	
	//creates the url for api call
	var url = baseURL + "/appointments";
	
	//creates data as a map
	var data = {	"date":dateU,
					"duration":duration,
					"description":description,
					"owner":owner
					};
	//POST function to send 
	$.post(	url,		//api url
			data,		//data to send
			function(){
		alert("Appointment saved") 	//telling user that the appointment was saved
	});
}

function searchAppointment(){
	//gets search parameters
	var start = $("#startDate").val();
	var end = $("#endDate").val();
	var ownerSearch = $("#searchOwner").val();
	
	//converting dates to Unix time
	var startSplit = start.split("/");
	var endSplit = end.split("/");
	
	var startU = new Date(Date.UTC(startSplit[2],(startSplit[0]*1-1),(startSplit[1]),0,0,0)).getTime();
	var endU = new Date(Date.UTC(endSplit[2],(endSplit[0]*1-1),(endSplit[1]),0,0,0)).getTime();
	
	var startU = startU/1000;
	var endU = endU/1000;
	
	//checks the values 
	console.log(startU);
	console.log(endU);
	console.log(ownerSearch);
	
	//constructs the GET url	
	var url = baseURL + "/appointments/search/start="+startU+"&end="+endU+"&owner="+ownerSearch;
	
	$.getJSON(
	url,
	function(appointments){
		//empties the table
		$("#appointmentsList").empty();
		//sets the column titles
		$("#appointmentsList").append("<tr><th>Date</th><th>Duration</th><th>Description</th><th>Owner</th></tr>")
		//loops through the returned json
		for(var i in appointments){
			var appointment = appointments[i];
			//sets parameters 
			var id = appointment["id"];
			var date = appointment["date"];
			
			//converts returned unix date to human readable format
			var dateObj = new Date(date * 1000);
			var humanDate = (dateObj.getMonth()+1) + "/" + (dateObj.getDate()) + "/" + (dateObj.getFullYear());
			    
			// sets more parameters
			var duration = appointment["duration"];
			var description = appointment["description"];
			var owner = appointment["owner"];
			
			//constructs HTML code to append to the table
			//hides the id of the appointment within the id tag of the html
			var htmlCode="<tr id=" + id + "><td>"+ humanDate +"</td><td>"+ duration +"</td><td>"+ description +"</td><td>" + owner +"</td></tr>";
			//appends the html string to the table
			$("#appointmentsList").append(htmlCode)
		}
		
		//creates an onclick event and calls the appointmentClicked method
		$("#appointmentsList tr").click(function(){
			//passes id of the table row to the method
			appointmentClicked($(this).attr("id"));
		});
	});	
}

//when called will set the id of the table row to selected, used in update and delete functions
function appointmentClicked(id){
	//removes previously selected item from the class
	$("#appointmentsList tr").removeClass("selected");
	//adds the new clicked item to the selected class
	$("#"+id).addClass("selected");
	}

//called when the delete button is pressed, will only delete an entry if it is selected
function deleteAppointment(id){
	//constructs the url from the id parameter that is passed into the function
var url=baseURL+"/appointments/delete/id="+id;		//URL pattern of delete service
var settings={type:"DELETE"};	//options to the $.ajax(...) function call
//sends the request
$.ajax(url,settings);
}

//update function called when the update button is clicked within the update appointment dialog is pressed
function updateAppointment(id){
	//creates relevant variables from the input boxes
	var date = $("#appointmentDateUpdate").val();
	var duration = $("#appointmentDurationUpdate").val();
	var description = $("#appointmentDescriptionUpdate").val();
	var owner = $("#appointmentOwnerUpdate").val();
	
	//constructs url
	var url=baseURL+"/appointments/update";
	
	//converting the date to unix time
	var s=date.split("/");
	//check if it was split correctly
	console.log("s to string " + s);
	//create a new date object out of array
	var dateU = new Date(Date.UTC(s[2],(s[0]*1-1),(s[1]),0,0,0)).getTime();
	
	var dateU = dateU/1000;
	
	//creates a data map to send to the server
	var data = {	
				"id":id,
				"date":dateU,
				"duration":duration,
				"description":description,
				"owner":owner
			};
	//sends data via POST request because PUT wouldn't work -- there is server side checking for the appointment id.
	$.post(url, data, function(){
		alert("Appointment Updated");
	});	
}


