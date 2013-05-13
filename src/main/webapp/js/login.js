var login = function() {
	var emailB = encodeURIComponent($('#email').val());
	var passwordB = encodeURIComponent($('#password').val());
	$('#content').fadeOut();
	$('#login_effect').fadeIn(function() {// c
		$.ajax({// d
			type : 'POST',
			url : '/account/login.json',
			dataType : "json",
			data : {
				email : emailB,
				password : passwordB,
			},
			success : function(result) { // e
				if (!result.success) {
					timeout = setTimeout(function() {
						$('#login_effect').fadeOut();
					}, 2500);
					document.location.href = 'wrongpassword/';
				} else {
					timeout = setTimeout(function() {
						$('#login_effect').fadeOut();
					}, 2500);
					document.location.href = '/profile/';
				}
			} // e
		});// d
	});// c
};// b
