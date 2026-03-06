// Full client logic for Employee CRUD
const API = '/api/employees';

document.addEventListener('DOMContentLoaded', () => {
  setupHandlers();
  loadEmployees();
});

function setupHandlers() {
  document.getElementById('btn-add').addEventListener('click', () => {
    openForm();
  });

  document.getElementById('cancel-btn').addEventListener('click', (e) => {
    e.preventDefault();
    closeForm();
  });

  document.getElementById('employee-form').addEventListener('submit', submitForm);

  document.getElementById('search').addEventListener('input', (e) => {
    const q = e.target.value.trim().toLowerCase();
    filterTable(q);
  });
}

async function loadEmployees() {
  try {
    const res = await fetch(API, { credentials: 'same-origin' });
    if (!res.ok) throw new Error('Failed to load employees');
    const list = await res.json();
    renderTable(list);
  } catch (err) {
    console.error(err);
    alert('Could not load employees. Make sure you are logged in.');
  }
}

function formatEmployeeId(id) {
  return "EMP" + String(id).padStart(3, '0');
}

function renderTable(list) {
  const tbody = document.querySelector('#employees-table tbody');
  tbody.innerHTML = '';

  list.forEach(e => {
    const tr = document.createElement('tr');

    tr.innerHTML = `
      <td>${formatEmployeeId(e.id)}</td>
      <td>${escapeHtml(e.name)}</td>
      <td>${escapeHtml(e.email)}</td>
      <td>${escapeHtml(e.department || '')}</td>
      <td>${escapeHtml(e.jobTitle || '')}</td>
      <td>${escapeHtml(e.phone || '')}</td>
      <td>${e.dateOfJoining || ''}</td>
      <td>${e.status}</td>
      <td>
        <button onclick="editEmployee(${e.id})">Edit</button>
        <button onclick="deleteEmployee(${e.id})">Delete</button>
      </td>
    `;

    tbody.appendChild(tr);
  });
}

function openForm(employee = null) {
  document.getElementById('form-panel').style.display = 'block';
  document.getElementById('employee-list-section')?.setAttribute('aria-hidden', 'true');

  document.getElementById('form-title').textContent =
    employee ? 'Edit Employee' : 'Add Employee';

  if (employee) {
    document.getElementById('emp-id').value = employee.id;
    document.getElementById('emp-name').value = employee.name || '';
    document.getElementById('emp-email').value = employee.email || '';
    document.getElementById('emp-dept').value = employee.department || '';
    document.getElementById('emp-job').value = employee.jobTitle || '';
    document.getElementById('emp-phone').value = employee.phone || '';
    document.getElementById('emp-doj').value = employee.dateOfJoining || '';
    document.getElementById('emp-status').value = employee.status || 'ACTIVE';
  } else {
    clearForm();
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function closeForm() {
  document.getElementById('form-panel').style.display = 'none';
  clearForm();
}

function clearForm() {
  document.getElementById('emp-id').value = '';
  document.getElementById('emp-name').value = '';
  document.getElementById('emp-email').value = '';
  document.getElementById('emp-dept').value = '';
  document.getElementById('emp-job').value = '';
  document.getElementById('emp-phone').value = '';
  document.getElementById('emp-doj').value = '';
  document.getElementById('emp-status').value = 'ACTIVE';
}

async function submitForm(e) {
  e.preventDefault();

  const id = document.getElementById('emp-id').value;

  const payload = {
    name: document.getElementById('emp-name').value,
    email: document.getElementById('emp-email').value,
    department: document.getElementById('emp-dept').value,
    jobTitle: document.getElementById('emp-job').value,
    phone: document.getElementById('emp-phone').value,
    dateOfJoining: document.getElementById('emp-doj').value || null,
    status: document.getElementById('emp-status').value
  };

  try {
    let res;

    if (id) {
      res = await fetch(`${API}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        credentials: 'same-origin'
      });
    } else {
      res = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        credentials: 'same-origin'
      });
    }

    if (!res.ok) {
      const txt = await res.text();
      alert('Error: ' + txt);
      return;
    }

    closeForm();
    await loadEmployees();

  } catch (err) {
    console.error(err);
    alert('Request failed. Check console.');
  }
}

async function editEmployee(id) {
  try {
    const res = await fetch(`${API}/${id}`, { credentials: 'same-origin' });

    if (!res.ok) {
      alert('Employee not found');
      return;
    }

    const emp = await res.json();
    openForm(emp);

  } catch (err) {
    console.error(err);
    alert('Could not retrieve employee');
  }
}

async function deleteEmployee(id) {
  if (!confirm('Delete employee #' + id + '?')) return;

  try {
    const res = await fetch(`${API}/${id}`, {
      method: 'DELETE',
      credentials: 'same-origin'
    });

    if (!res.ok) {
      alert('Delete failed');
      return;
    }

    await loadEmployees();

  } catch (err) {
    console.error(err);
    alert('Delete request failed');
  }
}

function filterTable(query) {
  const rows = document.querySelectorAll('#employees-table tbody tr');

  rows.forEach(r => {
    const text = r.textContent.toLowerCase();
    r.style.display = text.includes(query) ? '' : 'none';
  });
}

/* small helper */
function escapeHtml(s) {
  if (s === null || s === undefined) return '';

  return s.toString().replace(/[&<>"']/g, function(m) {
    return {
      '&':'&amp;',
      '<':'&lt;',
      '>':'&gt;',
      '"':'&quot;',
      "'":'&#39;'
    }[m];
  });
}