/*
 * buscador.js
 * Sugerencias en vivo para la caja de busqueda de la wiki.
 *
 * Es una mejora progresiva: si el JavaScript falla o esta desactivado, el
 * formulario sigue funcionando enviandose por GET a /wiki/buscar.
 */
(function () {
  "use strict";

  var MIN_LENGTH = 2;      // caracteres minimos antes de consultar
  var DEBOUNCE_MS = 200;   // espera tras la ultima pulsacion

  document.addEventListener("DOMContentLoaded", function () {
    var forms = document.querySelectorAll("[data-wiki-search]");
    Array.prototype.forEach.call(forms, function (form) {
      initSearch(form);
    });
  });

  function initSearch(form) {
    var input = form.querySelector("[data-wiki-search-input]");
    var list = form.querySelector("[data-wiki-search-suggestions]");
    var categoryInput = form.querySelector("[data-wiki-search-category]");

    if (!input || !list) {
      return;
    }

    // El fragmento ya emite ids unicos por posicion (nav / page).
    var listId = list.id || "wiki-search-suggestions";

    var timer = null;
    var controller = null;
    var activeIndex = -1;
    var items = [];

    input.addEventListener("input", function () {
      schedule(input.value);
    });

    input.addEventListener("focus", function () {
      if (items.length > 0) {
        open();
      }
    });

    input.addEventListener("keydown", function (event) {
      if (list.hidden) {
        return;
      }
      if (event.key === "ArrowDown") {
        event.preventDefault();
        move(1);
      } else if (event.key === "ArrowUp") {
        event.preventDefault();
        move(-1);
      } else if (event.key === "Enter") {
        if (activeIndex >= 0 && items[activeIndex]) {
          event.preventDefault();
          window.location.href = items[activeIndex].url;
        }
      } else if (event.key === "Escape") {
        close();
      }
    });

    // Cerrar al pulsar fuera del formulario.
    document.addEventListener("click", function (event) {
      if (!form.contains(event.target)) {
        close();
      }
    });

    function schedule(value) {
      window.clearTimeout(timer);
      var query = value.trim();

      if (query.length < MIN_LENGTH) {
        items = [];
        close();
        return;
      }

      timer = window.setTimeout(function () {
        fetchSuggestions(query);
      }, DEBOUNCE_MS);
    }

    function fetchSuggestions(query) {
      // Cancela la peticion anterior para que no llegue fuera de orden.
      if (controller) {
        controller.abort();
      }
      controller = new AbortController();

      var url = "/api/wiki/sugerencias?q=" + encodeURIComponent(query);
      if (categoryInput && categoryInput.value) {
        url += "&categoria=" + encodeURIComponent(categoryInput.value);
      }

      fetch(url, { signal: controller.signal, headers: { Accept: "application/json" } })
        .then(function (response) {
          if (!response.ok) {
            throw new Error("HTTP " + response.status);
          }
          return response.json();
        })
        .then(function (data) {
          items = Array.isArray(data) ? data : [];
          render(query);
        })
        .catch(function (error) {
          if (error.name !== "AbortError") {
            items = [];
            close();
          }
        });
    }

    function render(query) {
      list.innerHTML = "";
      activeIndex = -1;

      if (items.length === 0) {
        var empty = document.createElement("li");
        empty.className = "wiki-search-suggestion-empty";
        empty.textContent = 'Sin resultados para "' + query + '"';
        list.appendChild(empty);
        open();
        return;
      }

      items.forEach(function (item, index) {
        list.appendChild(buildItem(item, index, listId));
      });
      open();
    }

    function buildItem(item, index, idPrefix) {
      var li = document.createElement("li");
      li.className = "wiki-search-suggestion";
      li.id = idPrefix + "-option-" + index;
      li.setAttribute("role", "option");
      li.setAttribute("aria-selected", "false");

      var link = document.createElement("a");
      link.href = item.url;

      var title = document.createElement("span");
      title.className = "wiki-search-suggestion-title";
      // highlightedTitle viene del servidor ya escapado, solo trae <mark>.
      title.innerHTML = item.highlightedTitle || escapeHtml(item.title);
      link.appendChild(title);

      if (item.snippet) {
        var snippet = document.createElement("span");
        snippet.className = "wiki-search-suggestion-snippet";
        snippet.innerHTML = item.snippet;
        link.appendChild(snippet);
      }

      li.appendChild(link);
      li.addEventListener("mouseenter", function () {
        setActive(index);
      });
      return li;
    }

    function move(delta) {
      if (items.length === 0) {
        return;
      }
      var next = activeIndex + delta;
      if (next < 0) {
        next = items.length - 1;
      } else if (next >= items.length) {
        next = 0;
      }
      setActive(next);
    }

    function setActive(index) {
      var options = list.querySelectorAll(".wiki-search-suggestion");
      Array.prototype.forEach.call(options, function (option, i) {
        var selected = i === index;
        option.classList.toggle("wiki-search-suggestion--active", selected);
        option.setAttribute("aria-selected", selected ? "true" : "false");
      });
      activeIndex = index;
      input.setAttribute("aria-activedescendant",
        index >= 0 && options[index] ? options[index].id : "");
    }

    function open() {
      list.hidden = false;
      input.setAttribute("aria-expanded", "true");
    }

    function close() {
      list.hidden = true;
      activeIndex = -1;
      input.setAttribute("aria-expanded", "false");
      input.removeAttribute("aria-activedescendant");
    }
  }

  function escapeHtml(text) {
    var div = document.createElement("div");
    div.textContent = text == null ? "" : text;
    return div.innerHTML;
  }
})();
