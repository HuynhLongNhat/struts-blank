
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

	function toggleAll(source) {
		const checkboxes = document.querySelectorAll('input[name="customerIds"]');
		checkboxes.forEach(chk => chk.checked = source.checked);
	}
	document.addEventListener("DOMContentLoaded", function () {
		const checkAll = document.getElementById('chkAll');
		if (checkAll) {
			checkAll.addEventListener('change', function () {
				toggleAll(this);
			});
		}
	});
	