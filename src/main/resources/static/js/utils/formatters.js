/**
 * Formatters — Utilidades de formateo para montos y fechas
 */

/**
 * Formatea un número como moneda
 * @param {number} amount - Monto a formatear
 * @param {string} currency - Símbolo de moneda (default: $)
 * @returns {string} Monto formateado
 */
function formatCurrency(amount, currency) {
    currency = currency || '$';
    return currency + ' ' + Number(amount).toLocaleString('es-CO', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

/**
 * Formatea una fecha ISO a formato legible
 * @param {string} dateStr - Fecha en formato ISO (YYYY-MM-DD)
 * @returns {string} Fecha formateada
 */
function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}
