
document.addEventListener("DOMContentLoaded", function () {
		var errorElement = document.getElementById("errorMessages");
		var errors = errorElement ? errorElement.textContent.trim() : "";
		if (errors) {
			alert(errors);
		}
	});
