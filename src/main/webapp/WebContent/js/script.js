function resetFormFields() {
	const form = document.getElementById('loginForm');
	if (form) {
		form.reset();
	}
	const errorMessage = document.getElementById('lblErrorMessage');
	if (errorMessage) {
		errorMessage.textContent = '';
	}

}
function showErrorMessage(message) {
	if (message && message.trim() !== "") {
		alert(message);
	}
}

function redirectToEditPage() {
    window.location.href = 'T003';
}
