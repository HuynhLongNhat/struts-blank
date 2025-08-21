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
