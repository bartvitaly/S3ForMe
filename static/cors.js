function createCORSRequest(method, url) {
	var xhr = new XMLHttpRequest();
	if ("withCredentials" in xhr) {

		// Check if the XMLHttpRequest object has a "withCredentials" property.
		// "withCredentials" only exists on XMLHTTPRequest2 objects.
		xhr.open(method, url, true);

	} else if (typeof XDomainRequest != "undefined") {

		// Otherwise, check if XDomainRequest.
		// XDomainRequest only exists in IE, and is IE's way of making CORS
		// requests.
		xhr = new XDomainRequest();
		xhr.open(method, url);

	} else {

		// Otherwise, CORS is not supported by the browser.
		xhr = null;

	}
	return xhr;
}

function httpRequest(type, url) {
	var xmlHttp = null;
	try {
		xmlHttp = new createCORSRequest(type, url);
		xmlHttp.open(type, url, false);
		xmlHttp.send(null);
		xmlHttp.status
	} catch (err) {
		return err.message.concat(' ').concat(err.name);
	}
	return xmlHttp.status;
}

function appendChild() {
	var span = document.createElement("span");
	span.appendChild(document.createTextNode("text"));
	document.body.appendChild(span);
}