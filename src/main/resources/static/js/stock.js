// stock-dashboard.js

const tablaContainer = document.getElementById("tabla-container");
const buscador = document.getElementById("buscador");

// CSRF
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

// Tamaño de página para calcular la página de un producto
const pageSize = 50;

// ------------------- FUNCIONES -------------------

// Recargar tabla
function recargarTabla(url = null) {
    const params = url ? new URLSearchParams(url.split('?')[1]) : new URLSearchParams(window.location.search);
    fetch("?" + params.toString())
        .then(res => res.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");
            tablaContainer.innerHTML = doc.querySelector("#tabla-container").innerHTML;
        });
}

// Crear y mostrar autocomplete
function mostrarAutocomplete(items) {
    // Remover lista anterior
    let lista = document.getElementById("autocomplete-list");
    if (lista) lista.remove();

    if (!items || items.length === 0) return;

    lista = document.createElement("div");
    lista.id = "autocomplete-list";
    lista.classList.add("autocomplete-items");
    lista.style.position = "absolute";
    lista.style.background = "#fff";
    lista.style.border = "1px solid #ccc";
    lista.style.zIndex = "1000";
    lista.style.maxHeight = "200px";
    lista.style.overflowY = "auto";
    lista.style.width = buscador.offsetWidth + "px";

    items.forEach(item => {
        const div = document.createElement("div");
        div.classList.add("autocomplete-item");
        div.dataset.fila = item.fila;
        div.textContent = `${item.matnr} - ${item.descripcion}`;
        lista.appendChild(div);
    });

    buscador.parentNode.appendChild(lista);
}

// ------------------- EVENTOS -------------------

// Autocomplete
let timeoutBuscar;
buscador.addEventListener("input", function () {
    clearTimeout(timeoutBuscar);
    const buscar = buscador.value.trim();
    if (!buscar) {
        mostrarAutocomplete([]);
        return;
    }

    timeoutBuscar = setTimeout(() => {
        fetch("/stock/autocomplete?buscar=" + encodeURIComponent(buscar))
            .then(res => res.json())
            .then(data => mostrarAutocomplete(data))
            .catch(err => console.error(err));
    }, 300);
});

// Selección de producto en autocomplete
document.addEventListener("click", function (e) {
    const option = e.target.closest(".autocomplete-item");
    if (option) {
        const fila = parseInt(option.dataset.fila, 10);
        const pagina = !isNaN(fila) ? Math.floor(fila / pageSize) : 0;
        const buscar = buscador.value.trim();
        window.location.href = "/?buscar=" + encodeURIComponent(buscar) + "&pagina=" + pagina;
        return;
    }

    // Paginar
    const link = e.target.closest(".page-link");
    if (link) {
        e.preventDefault();
        history.pushState(null, "", link.getAttribute("href"));
        recargarTabla(link.getAttribute("href"));
    }

    // Editar stock mínimo
    const editBtn = e.target.closest(".editar-minimo");
    if (editBtn) {
        const wrapper = editBtn.closest(".stock-minimo-wrapper");
        wrapper.querySelector(".modo-lectura").classList.add("d-none");
        wrapper.querySelector(".modo-edicion").classList.remove("d-none");
        const input = wrapper.querySelector(".input-minimo");
        input.focus();
        input.select();
    }

    // Cancelar edición
    const cancelBtn = e.target.closest(".cancelar-edicion");
    if (cancelBtn) {
        const wrapper = cancelBtn.closest(".stock-minimo-wrapper");
        wrapper.querySelector(".modo-edicion").classList.add("d-none");
        wrapper.querySelector(".modo-lectura").classList.remove("d-none");
    }

    // Sincronizar tabla
    if (e.target.id === "btn-sync") recargarTabla();
});

// Submit edición stock mínimo con CSRF
document.addEventListener("submit", function (e) {
    const form = e.target.closest(".modo-edicion");
    if (!form) return;

    e.preventDefault();

    fetch("/stock/minimo", {
        method: "POST",
        body: new FormData(form),
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar stock mínimo");
            recargarTabla();
        })
        .catch(err => console.error(err));
});

// Escape para cerrar edición
document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
        const form = document.querySelector(".modo-edicion:not(.d-none)");
        if (form) {
            form.classList.add("d-none");
            form.closest(".stock-minimo-wrapper")
                .querySelector(".modo-lectura")
                .classList.remove("d-none");
        }
    }
});

// Click fuera del autocomplete para cerrarlo
document.addEventListener("click", function (e) {
    if (!e.target.closest(".autocomplete-items") && e.target !== buscador) {
        const lista = document.getElementById("autocomplete-list");
        if (lista) lista.remove();
    }
});