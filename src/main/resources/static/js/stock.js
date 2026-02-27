document.addEventListener("DOMContentLoaded", function () {

    function actualizarTabla() {

        const params = new URLSearchParams(window.location.search);

        fetch(`/api/stock/pagina?${params.toString()}`)
            .then(response => response.json())
            .then(data => {

                const tbody = document.getElementById("tabla-stock");

                if (!tbody) {
                    console.error("No se encontró tabla-stock");
                    return;
                }

                tbody.innerHTML = "";

                data.stocks.forEach(item => {

                    const row = document.createElement("tr");

                    if (item.stockMinimo !== null &&
                        item.stockLibre < item.stockMinimo) {
                        row.classList.add("table-danger");
                    }

                    row.innerHTML = `
                        <td>${item.matnr}</td>
                        <td>${item.descripcion}</td>
                        <td>${item.centro}</td>
                        <td>${item.almacen}</td>
                        <td>${item.stockLibre}</td>

                        <td>
                            <form action="/stock/minimo" method="post" class="d-flex gap-2 align-items-center">
                                <input type="hidden" name="material" value="${item.matnr}">
                                <input type="hidden" name="centro" value="${item.centro}">
                                <input type="hidden" name="almacen" value="${item.almacen}">
                                <input type="number"
                                       name="stockMinimo"
                                       step="0.01"
                                       min="0"
                                       class="form-control form-control-sm"
                                       style="width: 100px;"
                                       value="${item.stockMinimo ?? ''}">
                        </td>

                        <td>${item.valorTotal}</td>

                        <td>
                                <button type="submit"
                                        class="btn btn-sm btn-outline-primary">
                                    Guardar
                                </button>
                            </form>
                        </td>
                    `;

                    tbody.appendChild(row);
                });

            })
            .catch(error => console.error("Error AJAX:", error));
    }
    // prueba cada 10 segundos
    setInterval(actualizarTabla, 10000);

});