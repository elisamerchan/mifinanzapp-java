/**
 * Charts — Inicialización de gráficos con Chart.js
 */

/**
 * Inicializa los gráficos en las páginas que los contengan
 */
function initCharts() {
    const chartCanvas = document.getElementById('chartDashboard');
    if (!chartCanvas || typeof Chart === 'undefined') return;

    const evLabels = window.evLabels || [];
    const evGastos = window.evGastos || [];
    const evIngresos = window.evIngresos || [];

    if (evLabels.length === 0) return;

    new Chart(chartCanvas, {
        type: 'bar',
        data: {
            labels: evLabels,
            datasets: [
                {
                    label: 'Ingresos',
                    data: evIngresos,
                    backgroundColor: 'rgba(76, 175, 80, 0.7)',
                    borderRadius: 4
                },
                {
                    label: 'Gastos',
                    data: evGastos,
                    backgroundColor: 'rgba(229, 57, 53, 0.7)',
                    borderRadius: 4
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'top', labels: { usePointStyle: true, padding: 16 } }
            },
            scales: {
                y: { beginAtZero: true, grid: { color: '#F0F0F0' } },
                x: { grid: { display: false } }
            }
        }
    });
}

/**
 * Inicializa gráficos de donut/pie en la página de reportes
 */
function initDonutChart(canvasId, labels, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || typeof Chart === 'undefined') return;

    new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    '#6E71C4', '#4CAF50', '#FF9800', '#E53935',
                    '#2196F3', '#9C27B0', '#00BCD4', '#FF5722'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'bottom', labels: { padding: 16, usePointStyle: true } }
            }
        }
    });
}
