const API = "/api/attendance";

document.addEventListener("DOMContentLoaded", () => {
    loadAttendance();

    document.getElementById("btn-check-in").onclick = async () => {
        await fetch(API + "/check-in", { method: "POST", credentials: "same-origin" });
        loadAttendance();
    };

    document.getElementById("btn-check-out").onclick = async () => {
        await fetch(API + "/check-out", { method: "POST", credentials: "same-origin" });
        loadAttendance();
    };
});

async function loadAttendance() {
    const res = await fetch(API, { credentials: "same-origin" });
    const table = document.querySelector("#attendance-table tbody");
    table.innerHTML = "";

    if (!res.ok) return;

    const data = await res.json();
    data.forEach(r => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${r.date}</td>
            <td>${r.checkIn || "-"}</td>
            <td>${r.checkOut || "-"}</td>
        `;
        table.appendChild(tr);
    });
}
