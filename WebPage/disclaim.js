$(document).ready(function(){
    jQuery(function($) {
                $('#disclaimer').on('scroll', function() {
                    if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                        alert('end reached');
                    }
                })
    });
});