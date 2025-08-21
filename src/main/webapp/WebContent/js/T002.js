
function showErrorMessage(message) {
	if (message && message.trim() !== "") {
		alert(message);
	}
}

function redirectToEditPage() {
    window.location.href = 'T003.do';
}