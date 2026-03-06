// Universal attendance widget used by dashboards (home).
// Uses endpoints:
//  GET /api/me, GET /api/attendance, POST /api/attendance/check-in, POST /api/attendance/check-out

document.addEventListener('DOMContentLoaded', initAttendanceWidget);

async function initAttendanceWidget() {
  try {
    const me = await apiGet('/api/me').catch(()=>({}));
    if (document.querySelector('#welcome-sub') && me) {
      document.querySelector('#welcome-sub').textContent = `Hello, ${me.name || me.email || 'User'}`;
    }
    if (document.querySelector('#emp-id') && me) {
      document.querySelector('#emp-id').textContent = `ID: ${me.id || ''}`;
    }
  } catch (err) { console.error('initAttendanceWidget -> me', err); }

  const btnIn = document.querySelector('#btn-check-in');
  const btnOut = document.querySelector('#btn-check-out');

  if (btnIn) {
    btnIn.addEventListener('click', async () => {
      try {
        btnIn.disabled = true;
        await apiPost('/api/attendance/check-in', {});
        await loadAttendanceTable();
      } catch (e) {
        alert('Check-in failed: ' + (e.message || e));
      } finally { btnIn.disabled = false; }
    });
  }

  if (btnOut) {
    btnOut.addEventListener('click', async () => {
      try {
        btnOut.disabled = true;
        await apiPost('/api/attendance/check-out', {});
        await loadAttendanceTable();
      } catch (e) {
        alert('Check-out failed: ' + (e.message || e));
      } finally { btnOut.disabled = false; }
    });
  }

  // initial load for small recent table
  await loadAttendanceTable();
}

async function loadAttendanceTable() {
  try {
    const data = await apiGet('/api/attendance');
    const tbody = document.querySelector('#attendance-table tbody');
    if (!tbody) return;
    tbody.innerHTML = '';
    if (!Array.isArray(data) || data.length === 0) {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td colspan="3" class="muted">No attendance records found</td>`;
      tbody.appendChild(tr);
      return;
    }
    data.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${escapeHtml(r.date)}</td><td>${escapeHtml(r.checkIn || '-')}</td><td>${escapeHtml(r.checkOut || '-')}</td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error('loadAttendanceTable', err);
  }
}
