$JQry(function() {

    // Toast initialization
    document.querySelectorAll('.toast').forEach(toastElement => {
        let toast;
        if (!toastElement.classList.contains('show') && !toastElement.classList.contains('hide')) {
            toast = new bootstrap.Toast(toastElement);
            toast.show();
        }
    });

});
