
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