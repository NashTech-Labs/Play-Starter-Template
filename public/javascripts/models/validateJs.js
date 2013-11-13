// 
//	jQuery Validate example script
//
//	Prepared by David Cochran
//	
//	Free for your use -- No warranties, no guarantees!
//

$(document).ready(
		function() {
			$("#login-form").validate(
					{
						rules : {
							Password : {
								required : true
							},
							EmailId : {
								required : true,
								email : true
							}
						},
						highlight : function(label) {
							$(label).closest('.control-group')
									.addClass('error');
						},
						success : function(label) {
							$(label).text('OK!').addClass('valid').closest(
									'.control-group').addClass('success');
						}
					});
			$('#signup-form').validate(
					{
						rules : {
							Password : {
								minlength : 6,
								required : true
							},
							ConfirmPassword : {
								equalTo : "#Password",
								required : true
							},
							EmailId : {
								required : true,
								email : true
							}
						},
						highlight : function(label) {
							$(label).closest('.control-group')
									.addClass('error');
						},
						success : function(label) {
							label.text('OK!').addClass('valid').closest(
									'.control-group').addClass('success');
						}
					});
		}); // end document.ready