function searchInHTML(event){
	document.getElementById("searchModalBody").innerHTML = ""; // Flush modal
	var list = document.getElementsByClassName("tree-element");
	var keyword = $('#keyword').val().toUpperCase();
	for (var i = 0; i < list.length; i++) {
		var s = list[i].innerText;
		var str = s.toUpperCase();
    		if(str.match(keyword)){
			//console.log(list[i].innerHTML); //second console output
			var item = list[i].children;			
			document.getElementById("searchModalBody").innerHTML += "<a target='view' href='" + item[0].href + "' onclick=hideModal()> " + item[0].text + "<br><br>";
		}	
	}
	$("#searchModal").modal("show");
}

function hideModal(){
$("#searchModal").modal("hide");
}

