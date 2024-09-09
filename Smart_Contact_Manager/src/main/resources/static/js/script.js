function toggleSidebar() {
	const sidebar = document.getElementsByClassName("sidebar")[0];
	const content = document.getElementsByClassName("content")[0];

	if (window.getComputedStyle(sidebar).display == "none") {

		sidebar.style.display = "block";
		content.style.marginLeft = "20%";
	} else {
		sidebar.style.display = "none";
		content.style.marginLeft = "0%";
	}
}

function search() {
	console.log("heheheh");

	let query = document.getElementById("search-input").value;
	const res = document.getElementsByClassName("search-result")[0];

	if (query.length == 0) {
		res.style.display = "none";
	} else {
		console.log(query);

		let url = `http://localhost:8080/search/${query}`;

		fetch(url).then(response => {
			return response.json();
		}).then((data) => {
			console.log(data);

			let text = `<div class='list-group'>`
			data.forEach((contact) => {
				text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action' >${contact.cname}</a>`
			})
			text += `</div>`

			res.innerHTML = text;
			res.style.display = "block";
		})
	}

}

