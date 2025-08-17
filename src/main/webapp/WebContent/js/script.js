function clearFormLogin() {
	const errorMessage = document.getElementById('lblErrorMessage');
	const userID = document.getElementById("txtuserID");
    const password = document.getElementById("txtpassword");
	if (errorMessage) {
		errorMessage.textContent = '';
	}
	if(userID) {
		userID.value = "";
	}
	if(password) {
		password.value = "";
	}

}

function clearFormEdit() {
	const errorMessage = document.getElementById('lblErrorMessage');
	const customerName = document.getElementById('txtCustomerName');
	const birthday = document.getElementById('txtBirthday');
	const email = document.getElementById('txtEmail');
	const address = document.getElementById('txtAddress');
	if (errorMessage) {
		errorMessage.textContent = '';
	}
	if (customerName) {
		customerName.value = '';
	}
	if (birthday) {
		birthday.value = '';
	}
	if (email) {
		email.value = '';
	} if (address) {
		address.value = '';
	}


}
function showErrorMessage(message) {
	if (message && message.trim() !== "") {
		alert(message);
	}
}

function redirectToEditPage() {
    window.location.href = 'T003.do';
}