// employee-attendance.js
document.addEventListener('DOMContentLoaded', init);

async function init() {
  try {
    const me = await apiGet('/api/me');
    el('#welcome-sub').textContent = `Hello, ${escapeHtml(me.name || me.email)}`;
    el('#emp-id').textContent = `ID: ${me.id || ''}`;
  } catch (err) {
    console.error(err);
  }

  // bind buttons
  el('#btn-check-in').addEventListener('click', async () => {
    try {
      await apiPost('/api/attendance/check-in', {});
      await loadTable();
      el('#btn-check-in').disabled = true;
    } catch (ex) { alert('Check-in failed: ' + ex.message) }
  });

  el('#btn-check-out').addEventListener('click', async () => {
    try {
      await apiPost('/api/attendance/check-out', {});
      await loadTable();
      el('#btn-check-out').disabled = true;
    } catch (ex) { alert('Check-out failed: ' + ex.message) }
  });

  await loadTable();

  // disable buttons if already checked in/out today
  const records = await apiGet('/api/attendance');
  const today = (new Date()).toISOString().slice(0,10);
  const todayRec = records.find(r => r.date === today);
  if (todayRec && todayRec.checkIn) el('#btn-check-in').disabled = true;
  if (todayRec && todayRec.checkOut) el('#btn-check-out').disabled = true;
}

async function loadTable() {
  try {
    const data = await apiGet('/api/attendance');
    const tbody = el('#attendance-table tbody');
    tbody.innerHTML = '';
    data.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${r.date}</td><td>${r.checkIn || '-'}</td><td>${r.checkOut || '-'}</td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error(err);
  }
}
