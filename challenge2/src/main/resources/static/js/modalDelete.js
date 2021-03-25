/**
 * 
 */

const modalConfirm = new bootstrap.Modal(document.getElementById('confirmDelete'));
	
	$('body').on("click", '.link1', function(e) {
	    e.preventDefault();
	    let url = $(this).attr('href');
	    console.log(url);
	    $('.modal-title').html("¿Está seguro que desea eliminar este post?");
	    modalConfirm.show();
	    $("#modal-btn-si").on("click", function(){
	         window.location.href = url;
	      });
	});