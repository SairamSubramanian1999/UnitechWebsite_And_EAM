// admin-hr-attendance.js
document.addEventListener('DOMContentLoaded', adminInit);

async function adminInit(){
  try {
    const me = await apiGet('/api/me');
    el('#welcome-sub').textContent = `Signed in as ${escapeHtml(me.name || me.email)} (${me.role})`;
    el('#emp-id').textContent = `ID: ${me.id || ''}`;
  } catch (err) { console.error(err); }

  el('#btn-manual-update').addEventListener('click', async () => {
    try {
      const employeeId = el('#manual-emp-id').value.trim();
      const date = el('#manual-date').value;
      const checkIn = el('#manual-checkin').value || null;
      const checkOut = el('#manual-checkout').value || null;

      if (!employeeId || !date) { alert('Employee ID and date are required'); return; }

      const payload = { employeeId: employeeId.toString(), date, checkIn: checkIn || null, checkOut: checkOut || null };
      const res = await apiPost('/api/attendance/manual-update', payload);
      alert('Attendance updated');
      await loadRecords(); // refresh table
    } catch (err) {
      alert('Update failed: ' + err.message);
    }
  });

  el('#btn-search').addEventListener('click', async () => {
    const id = el('#search-emp-id').value.trim();
    if (!id) return;
    try {
      // call employee API
      const res = await apiGet(`/api/employees/${id}`);
      el('#search-result').innerHTML = `<strong>${escapeHtml(res.name)}</strong><br/><span class="muted">${escapeHtml(res.email)}</span>`;
    } catch (err) {
      el('#search-result').textContent = 'Employee not found';
    }
  });

  await loadRecords();
}

async function loadRecords() {
  // show recent attendance across employees (for simplicity, call all attendance via /api/attendance? not implemented globally)
  // We'll try to fetch a sample by searching employee id input if present
  const tbody = el('#records-table tbody');
  tbody.innerHTML = '';
  // We can show last 20 attendance re-queries by employee id from UI - but as a fallback we poll own attendance as sample
  try {
    const me = await apiGet('/api/me');
    const myRecords = await apiGet('/api/attendance/user/' + me.id).catch(()=>[]);
    // If endpoint /api/attendance/user/{id} isn't present fallback to own attendance API
    const rows = Array.isArray(myRecords) && myRecords.length ? myRecords : await apiGet('/api/attendance').catch(()=>[]);
    rows.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${r.userId || me.id}</td><td>${r.date}</td><td>${r.checkIn || '-'}</td><td>${r.checkOut || '-'}</td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.warn('Could not load records', err);
  }
}
