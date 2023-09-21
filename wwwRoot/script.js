let btn_color = document.getElementById("btn_color");
let html_el = document.getElementById("html_el");

btn_color.addEventListener("click", (e) => {
    let attr = html_el.getAttribute("data-bs-theme");
    if (attr === "dark") {
        html_el.setAttribute("data-bs-theme", "light");
        btn_color.innerHTML = "Go to dark-mode!"
        return;
    }
    html_el.setAttribute("data-bs-theme", "dark");
    btn_color.innerHTML = "Go to light-mode!"
})