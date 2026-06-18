/**
 * Sidebar — Toggle responsivo y navegación activa
 */

/**
 * Inicializa el comportamiento del sidebar
 */
function initSidebar() {
    const toggleBtn = document.querySelector('.mobile-toggle');
    const sidebar = document.querySelector('.sidebar');

    if (!toggleBtn || !sidebar) return;

    toggleBtn.addEventListener('click', function (e) {
        e.stopPropagation();
        sidebar.classList.toggle('show');
    });

    document.addEventListener('click', function (e) {
        if (window.innerWidth <= 768) {
            const isClickInside = sidebar.contains(e.target) || toggleBtn.contains(e.target);
            if (!isClickInside) {
                sidebar.classList.remove('show');
            }
        }
    });

    window.addEventListener('resize', function () {
        if (window.innerWidth > 768) {
            sidebar.classList.remove('show');
        }
    });
}
