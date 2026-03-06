// attendance-report.js
// Usage: include on dashboards where a container with IDs below exists:
//
// <canvas id="attendanceChart"></canvas>
// <table id="attendance-report-table">...</table>
//
// This script fetches /api/attendance/monthly or /api/attendance/user/{id}/monthly when ?userId= is present.

(async function () {
  // helper functions from your common.js may exist. Use apiGet if present, else fallback.
  async function apiGet(path) {
    if (window.apiGet) return window.apiGet(path);
    const res = await fetch(path, { credentials: 'same-origin' });
    if (!res.ok) throw new Error('API error ' + res.status);
    return res.json();
  }

  function el(sel) { return document.querySelector(sel); }
  function fmtTime(s) { return s ? s.replace('T', ' ') : '-'; }

  // determine target user (optional)
  const url = new URL(window.location.href);
  const userId = url.searchParams.get('userId');

  // determine year & month (optional)
  const year = url.searchParams.get('year');
  const month = url.searchParams.get('month');

  let apiPath;
  if (userId) {
    apiPath = `/api/attendance/user/${userId}/monthly${year || month ? `?year=${year||''}&month=${month||''}` : ''}`;
  } else {
    apiPath = `/api/attendance/monthly${year || month ? `?year=${year||''}&month=${month||''}` : ''}`;
  }

  const tableBody = el('#attendance-report-table tbody');
  const chartCanvas = el('#attendanceChart');
  const headingEl = el('#attendance-report-heading');

  try {
    const data = await apiGet(apiPath);

    // Fill table
    if (tableBody) {
      tableBody.innerHTML = '';
      data.forEach(d => {
        const tr = document.createElement('tr');
        const durText = d.durationHours !== null ? `${d.durationHours}h (${d.durationMinutes}m)` : '-';
        tr.innerHTML = `<td>${d.date}</td>
                        <td>${fmtTime(d.checkIn)}</td>
                        <td>${fmtTime(d.checkOut)}</td>
                        <td>${durText}</td>`;
        tableBody.appendChild(tr);
      });
    }

    if (headingEl) {
      const ym = (data.length && data[0].date) ? data[0].date.slice(0,7) : '';
      headingEl.textContent = userId ? `Attendance for user #${userId} — ${ym}` : `My Attendance — ${ym}`;
    }

    // Build chart: bar chart of hours per day
    if (chartCanvas) {
      const labels = data.map(d => d.date.slice(-2));
      const values = data.map(d => (d.durationHours === null ? 0 : d.durationHours));
      const ctx = chartCanvas.getContext('2d');

      // remove existing chart if present
      if (chartCanvas._chart) {
        chartCanvas._chart.destroy();
      }

      chartCanvas._chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels,
          datasets: [{
            label: 'Hours',
            data: values,
            backgroundColor: 'rgba(30,144,255,0.8)',
            borderColor: 'rgba(30,144,255,1)',
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: { beginAtZero: true, title: { display: true, text: 'Hours' } }
          },
          plugins: {
            legend: { display: false },
            tooltip: { callbacks: { label: function(ctx){ return ctx.parsed.y + ' hrs'; } } }
          }
        }
      });
    }
  } catch (err) {
    console.error('Attendance report load failed', err);
    if (tableBody) tableBody.innerHTML = '<tr><td colspan="4">Failed to load attendance</td></tr>';
    if (headingEl) headingEl.textContent = 'Attendance (error)';
  }
})();
