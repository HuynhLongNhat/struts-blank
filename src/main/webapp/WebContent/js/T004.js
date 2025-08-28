
function showErrorMessage(message) {
	if (message && message.trim() !== "") {
		alert(message);
	}
}

document.addEventListener("DOMContentLoaded", function () {
		var errorElement = document.getElementById("errorMessages");
		var errors = errorElement ? errorElement.textContent.trim() : "";
		if (errors) {
			alert(errors);
		}
	});
