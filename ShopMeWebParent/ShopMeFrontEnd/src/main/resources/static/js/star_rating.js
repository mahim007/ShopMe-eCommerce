$(document).ready(function(){
    const stars = document.querySelectorAll('#ratingComponent .star');
    const ratingInput = document.getElementById('ratingInput');

    function setRating(value) {
        ratingInput.value = value;

        stars.forEach(star => {
            star.classList.toggle(
                'filled',
                star.dataset.value <= value
            );
        });
    }

    stars.forEach(star => {
        star.addEventListener('click', () => {
            setRating(star.dataset.value);
        });
    });

    // Optional: initialize from server value
    if (ratingInput.value) {
        setRating(ratingInput.value);
    }
});