
function showErrorMessage(message) {
	if (message && message.trim() !== "") {
		alert(message);
	}
}

function redirectToEditPage() {
    window.location.href = 'T003.do';
}

function redirectToExportPage() {
    window.location.href = 'T004.do';
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

	function redirectToEditPage() {
		window.location.href = 'T003.do';
	}

	document.addEventListener("DOMContentLoaded", function () {
		const checkAll = document.getElementById('chkAll');
		if (checkAll) {
			checkAll.addEventListener('change', function () {
				toggleAll(this);
			});
		}
	});
	
	function setActionAndSubmit(actionValue) {
	    document.getElementById("actionHidden").value = actionValue;
	    document.forms[0].submit();
	}
