/**
 * Food ordering UI - wired to backend APIs.
 * - GET /api/menu on load
 * - POST /api/order on checkout
 */

const CATEGORIES = ["pizza", "burger", "fries"];
const BASE_URL =
  typeof window !== "undefined" && window.__FOOD_API_BASE__
    ? String(window.__FOOD_API_BASE__).replace(/\/$/, "")
    : "http://localhost:8080";
const TYPE_TO_CATEGORY = {
  PIZZA: "pizza",
  BURGER: "burger",
  SANDWICH: "fries",
  FRIES: "fries",
};

const MENU = {
  pizza: {
    label: "Pizza",
    items: [],
    addons: [
      { id: "corn", name: "Corn" },
      { id: "cheese", name: "Extra Cheese" },
      { id: "mayo", name: "Mayo Drizzle" },
      { id: "jalapeno", name: "Jalapenos" },
      { id: "olives", name: "Olives" },
      { id: "paneer", name: "Paneer Cubes" },
    ],
  },
  burger: {
    label: "Burger",
    items: [],
    addons: [
      { id: "corn", name: "Corn" },
      { id: "cheese", name: "Extra Cheese" },
      { id: "mayo", name: "Mayo" },
      { id: "bacon", name: "Bacon" },
      { id: "onion", name: "Caramelized Onion" },
      { id: "pickle", name: "Pickles" },
    ],
  },
  fries: {
    label: "Fries",
    items: [],
    addons: [
      { id: "corn", name: "Sweet Corn" },
      { id: "cheese", name: "Cheese Sauce" },
      { id: "mayo", name: "Mayo" },
      { id: "peri", name: "Peri Peri" },
      { id: "jalapeno", name: "Jalapenos" },
      { id: "pickle", name: "Pickles" },
    ],
  },
};

/** Progressive tracking: absolute ms from confirmation open (smooth gaps). */
const TRACKING_TRANSITIONS = [
  { atMs: 2000, completeKey: "placed", nextKey: "preparing" },
  { atMs: 5000, completeKey: "preparing", nextKey: "ready" },
  { atMs: 8000, completeKey: "ready", nextKey: "out" },
  { atMs: 11000, completeKey: "out", nextKey: "delivered" },
  { atMs: 14000, completeKey: "delivered", nextKey: null },
];

let trackingGeneration = 0;
let lastPlacedTotal = null;

function formatMoney(value) {
  const n = Math.round(Number(value || 0));
  return `₹${n}`;
}

/** Split backend description e.g. "Pizza + Corn + Olives" into title + extras. */
function parseFoodDescription(description) {
  const raw = String(description || "").trim();
  if (!raw) {
    return { title: "", extras: [] };
  }
  const parts = raw.split(" + ").map((s) => s.trim()).filter(Boolean);
  if (parts.length <= 1) {
    return { title: raw, extras: [] };
  }
  return { title: parts[0], extras: parts.slice(1) };
}

function normalizeMenuResponse(menuRows) {
  const grouped = {
    pizza: [],
    burger: [],
    fries: [],
  };

  menuRows.forEach((row) => {
    const category = TYPE_TO_CATEGORY[String(row.type || "").toUpperCase()];
    if (!category || !grouped[category]) {
      return;
    }
    grouped[category].push({
      code: row.code,
      name: row.name,
      type: row.type,
      basePrice: row.basePrice,
    });
  });

  MENU.pizza.items = grouped.pizza;
  MENU.burger.items = grouped.burger;
  MENU.fries.items = grouped.fries;
}

async function fetchJson(url, options) {
  const fetchOptions = {
    ...options,
    mode: "cors",
    cache: "no-store",
  };
  let response;
  try {
    response = await fetch(url, fetchOptions);
  } catch (err) {
    console.error("[Food UI] fetch network error:", url, err);
    throw new Error(
      `Cannot reach API (${url}). Is Spring Boot running at ${BASE_URL}?`
    );
  }
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    console.error("[Food UI] fetch HTTP error:", url, response.status, payload);
    throw new Error(payload.error || `Request failed: ${response.status}`);
  }
  return payload;
}

async function loadMenuFromApi() {
  const menuUrl = `${BASE_URL}/api/menu`;
  console.log("[Food UI] GET", menuUrl);
  const menuRows = await fetchJson(menuUrl);
  normalizeMenuResponse(Array.isArray(menuRows) ? menuRows : []);
  console.log("[Food UI] Menu loaded, rows:", Array.isArray(menuRows) ? menuRows.length : 0);
}

function getSelectedItem(category) {
  const select = document.getElementById(`${category}-select`);
  if (select.disabled) {
    return null;
  }
  const value = (select.value || "").trim();
  if (value === "") {
    return null;
  }
  const selectedOption = Array.from(select.options).find((o) => o.value === value);
  if (!selectedOption || !selectedOption.dataset.name) {
    return null;
  }
  return {
    code: selectedOption.value,
    name: selectedOption.dataset.name,
    type: selectedOption.dataset.type,
    basePrice: Number(selectedOption.dataset.basePrice || 0),
  };
}

function getSelectedExtras(category) {
  const container = document.getElementById(`${category}-addons`);
  const extras = [];
  container.querySelectorAll('input[type="checkbox"]:checked').forEach((input) => {
    if (input.dataset.addonName) {
      extras.push(input.dataset.addonName);
    }
  });
  return extras;
}

function collectSelectedLines() {
  return CATEGORIES.map((category) => {
    const item = getSelectedItem(category);
    const addons = getSelectedExtras(category);
    return {
      category,
      label: MENU[category].label,
      item,
      addons,
    };
  }).filter((line) => Boolean(line.item));
}

function buildOrderPayload() {
  const lines = collectSelectedLines();
  return {
    items: lines.map((line) => ({
      type: line.item.type,
      name: line.item.name,
      extras: Array.isArray(line.addons) ? line.addons : [],
    })),
    paymentMethod: "UPI",
  };
}

function appendPlaceholderOption(select) {
  const opt = document.createElement("option");
  opt.value = "";
  opt.textContent = "Select item";
  select.appendChild(opt);
}

function populateSelects() {
  CATEGORIES.forEach((category) => {
    const select = document.getElementById(`${category}-select`);
    const items = MENU[category].items;
    select.innerHTML = "";

    if (items.length === 0) {
      const opt = document.createElement("option");
      opt.value = "";
      opt.textContent = `No ${MENU[category].label.toLowerCase()} available`;
      select.appendChild(opt);
      select.disabled = true;
      return;
    }

    select.disabled = false;
    appendPlaceholderOption(select);

    items.forEach((item) => {
      const opt = document.createElement("option");
      opt.value = item.code;
      opt.dataset.name = item.name;
      opt.dataset.type = item.type;
      opt.dataset.basePrice = String(item.basePrice);
      opt.textContent = `${item.name} — ${formatMoney(item.basePrice)}`;
      select.appendChild(opt);
    });

    // Force placeholder: never leave a real menu row selected by default.
    select.selectedIndex = 0;
  });
}

function populateAddonGrids() {
  CATEGORIES.forEach((category) => {
    const container = document.getElementById(`${category}-addons`);
    container.innerHTML = "";
    MENU[category].addons.forEach((addon) => {
      const wrap = document.createElement("div");
      wrap.className = "checkbox-tile";
      const id = `${category}-addon-${addon.id}`;
      const input = document.createElement("input");
      input.type = "checkbox";
      input.value = addon.id;
      input.id = id;
      input.dataset.category = category;
      input.dataset.addonName = addon.name;
      const label = document.createElement("label");
      label.htmlFor = id;
      label.textContent = addon.name;
      wrap.appendChild(input);
      wrap.appendChild(label);
      container.appendChild(wrap);
    });
  });
}

function renderOrderPanel() {
  const lines = collectSelectedLines();
  const listEl = document.getElementById("order-lines");
  const emptyEl = document.getElementById("order-empty");
  const totalEl = document.getElementById("order-total");

  listEl.innerHTML = "";

  lines.forEach((line) => {
    const li = document.createElement("li");
    li.className = "order-line";

    const title = document.createElement("p");
    title.className = "order-line-title";
    title.textContent = line.item.name;

    const meta = document.createElement("p");
    meta.className = "order-line-meta order-line-extras";
    if (line.addons.length > 0) {
      meta.textContent = `+ ${line.addons.join(", ")}`;
    } else {
      meta.textContent = "";
      meta.hidden = true;
    }

    const price = document.createElement("p");
    price.className = "order-line-price";
    price.textContent = formatMoney(line.item.basePrice);

    li.appendChild(title);
    li.appendChild(meta);
    li.appendChild(price);
    listEl.appendChild(li);
  });

  const hasContent = lines.length > 0;
  emptyEl.hidden = hasContent;
  emptyEl.textContent = "No items selected";
  listEl.hidden = !hasContent;

  if (!hasContent) {
    lastPlacedTotal = null;
  }
  totalEl.textContent = hasContent && lastPlacedTotal !== null ? formatMoney(lastPlacedTotal) : "₹0";
}

function onCategorySelectChange(event) {
  const select = event.target;
  const category = select.id.replace("-select", "");
  if (select.value === "") {
    document.querySelectorAll(`#${category}-addons input[type="checkbox"]`).forEach((cb) => {
      cb.checked = false;
    });
  }
  renderOrderPanel();
}

function attachMenuListeners() {
  CATEGORIES.forEach((category) => {
    const select = document.getElementById(`${category}-select`);
    select.addEventListener("change", onCategorySelectChange);
  });

  document.querySelectorAll(".checkbox-grid").forEach((grid) => {
    grid.addEventListener("change", renderOrderPanel);
  });
}

function resetTimelineUI() {
  document.querySelectorAll(".timeline-step").forEach((step) => {
    step.classList.remove("is-active", "is-complete");
    const status = step.querySelector("[data-status]");
    if (status) status.textContent = "";
  });
}

function setTimelineStepState(stepEl, state) {
  stepEl.classList.remove("is-active", "is-complete");
  const status = step.querySelector("[data-status]");
  if (state === "pending" && status) status.textContent = "";
  if (state === "active") {
    stepEl.classList.add("is-active");
    if (status) status.textContent = "In progress...";
  }
  if (state === "complete") {
    stepEl.classList.add("is-complete");
    if (status) status.textContent = "Done";
  }
}

function runTrackingSimulation(generation) {
  const firstDelayMs = 300;
  setTimeout(() => {
    if (generation !== trackingGeneration) return;
    const first = document.querySelector('.timeline-step[data-step="placed"]');
    if (first) setTimelineStepState(first, "active");
  }, firstDelayMs);

  TRACKING_TRANSITIONS.forEach(({ atMs, completeKey, nextKey }) => {
    setTimeout(() => {
      if (generation !== trackingGeneration) return;
      const doneEl = document.querySelector(`.timeline-step[data-step="${completeKey}"]`);
      if (doneEl) setTimelineStepState(doneEl, "complete");
      if (nextKey) {
        const nextEl = document.querySelector(`.timeline-step[data-step="${nextKey}"]`);
        if (nextEl) setTimelineStepState(nextEl, "active");
      }
    }, atMs);
  });
}

function fillConfirmationSummaryFromResponse(orderResponse) {
  const ul = document.getElementById("confirmation-summary");
  ul.innerHTML = "";

  const responseItems = Array.isArray(orderResponse.items) ? orderResponse.items : [];
  responseItems.forEach((line) => {
    const li = document.createElement("li");
    const left = document.createElement("div");
    left.className = "summary-line-stack";

    const parsed = parseFoodDescription(line.description);
    const titleEl = document.createElement("span");
    titleEl.className = "summary-line-title";
    titleEl.textContent = parsed.title || line.description;

    left.appendChild(titleEl);
    if (parsed.extras.length > 0) {
      const extrasEl = document.createElement("span");
      extrasEl.className = "summary-line-extras";
      extrasEl.textContent = `+ ${parsed.extras.join(", ")}`;
      left.appendChild(extrasEl);
    }

    const right = document.createElement("span");
    right.className = "summary-line-price";
    right.textContent = formatMoney(line.lineTotal);

    li.appendChild(left);
    li.appendChild(right);
    ul.appendChild(li);
  });

  document.getElementById("confirmation-total").textContent = formatMoney(orderResponse.totalAmount);
}

function showConfirmation(orderResponse) {
  const screen = document.getElementById("confirmation-screen");
  const app = document.getElementById("order-app");

  trackingGeneration += 1;
  const generation = trackingGeneration;

  document.getElementById("confirmation-order-id").textContent = orderResponse.orderId;
  fillConfirmationSummaryFromResponse(orderResponse);

  resetTimelineUI();
  screen.classList.remove("hidden");
  screen.setAttribute("aria-hidden", "false");
  app.setAttribute("aria-hidden", "true");

  runTrackingSimulation(generation);
}

function hideConfirmation() {
  trackingGeneration += 1;
  const screen = document.getElementById("confirmation-screen");
  const app = document.getElementById("order-app");
  screen.classList.add("hidden");
  screen.setAttribute("aria-hidden", "true");
  app.setAttribute("aria-hidden", "false");
  resetTimelineUI();
}

async function onPlaceOrder() {
  console.log("Place Order Clicked");
  const payload = buildOrderPayload();
  console.log("Payload:", payload);
  if (!payload.items.length) {
    window.alert("Please select at least one item before placing an order.");
    return;
  }

  const placeOrderButton = document.getElementById("place-order");
  if (!placeOrderButton) {
    console.error("[Food UI] Missing #place-order button");
    return;
  }
  const originalLabel = placeOrderButton.textContent;
  const orderUrl = `${BASE_URL}/api/order`;

  try {
    placeOrderButton.disabled = true;
    placeOrderButton.textContent = "Placing Order...";

    console.log("[Food UI] POST", orderUrl);
    const orderResponse = await fetchJson(orderUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    console.log("[Food UI] Order response:", orderResponse);

    lastPlacedTotal = Number(orderResponse.totalAmount || 0);
    renderOrderPanel();
    showConfirmation(orderResponse);
  } catch (error) {
    window.alert(error.message || "Failed to place order.");
  } finally {
    placeOrderButton.disabled = false;
    placeOrderButton.textContent = originalLabel;
  }
}

function resetOrderUiState() {
  hideConfirmation();
  lastPlacedTotal = null;
  CATEGORIES.forEach((category) => {
    const select = document.getElementById(`${category}-select`);
    if (!select.disabled && select.options.length > 0 && select.options[0].value === "") {
      select.selectedIndex = 0;
    }
    document.querySelectorAll(`#${category}-addons input[type="checkbox"]`).forEach((cb) => {
      cb.checked = false;
    });
  });
  renderOrderPanel();
}

function onBackToMenu() {
  resetOrderUiState();
}

async function init() {
  populateAddonGrids();
  attachMenuListeners();

  try {
    await loadMenuFromApi();
    populateSelects();
    renderOrderPanel();
  } catch (error) {
    populateSelects();
    renderOrderPanel();
    window.alert(`Could not load menu from backend: ${error.message}`);
  }

  const placeBtn = document.getElementById("place-order");
  const backBtn = document.getElementById("new-order");
  if (placeBtn) {
    placeBtn.addEventListener("click", onPlaceOrder);
  } else {
    console.error("[Food UI] init: #place-order not found");
  }
  if (backBtn) {
    backBtn.addEventListener("click", onBackToMenu);
  }
  console.log("[Food UI] Ready. API base:", BASE_URL);
}

document.addEventListener("DOMContentLoaded", init);
