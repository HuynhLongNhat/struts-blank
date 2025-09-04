/*function moveOptions(fromId, toId) {
	var from = document.getElementById(fromId);
	var to = document.getElementById(toId);

	for (var i = 0; i < from.options.length; i++) {
		if (from.options[i].selected) {
			var opt = from.options[i];
			to.add(new Option(opt.text, opt.value));
			from.remove(i);
			i--;
		}
	}
}

function moveUp(selectId) {
	var select = document.getElementById(selectId);
	for (var i = 1; i < select.options.length; i++) {
		if (select.options[i].selected
			&& !select.options[i - 1].selected) {
			var tmpText = select.options[i - 1].text;
			var tmpValue = select.options[i - 1].value;

			select.options[i - 1].text = select.options[i].text;
			select.options[i - 1].value = select.options[i].value;

			select.options[i].text = tmpText;
			select.options[i].value = tmpValue;

			select.options[i - 1].selected = true;
			select.options[i].selected = false;
		}
	}
}

function moveDown(selectId) {
	var select = document.getElementById(selectId);
	for (var i = select.options.length - 2; i >= 0; i--) {
		if (select.options[i].selected
			&& !select.options[i + 1].selected) {
			var tmpText = select.options[i + 1].text;
			var tmpValue = select.options[i + 1].value;

			select.options[i + 1].text = select.options[i].text;
			select.options[i + 1].value = select.options[i].value;

			select.options[i].text = tmpText;
			select.options[i].value = tmpValue;

			select.options[i + 1].selected = true;
			select.options[i].selected = false;
		}
	}
}
*/
document.addEventListener("DOMContentLoaded", function () {
		var errorElement = document.getElementById("errorMessages");
		var errors = errorElement ? errorElement.textContent.trim() : "";
		if (errors) {
			alert(errors);
		}
	});
