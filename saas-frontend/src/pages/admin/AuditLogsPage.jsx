import React, { useEffect, useState } from 'react';
import api from '../../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { Search, ShieldAlert, ArrowUpDown, ChevronLeft, ChevronRight, FileDown } from 'lucide-react';

const AuditLogsPage = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  const [action, setAction] = useState('');
  const [email, setEmail] = useState('');
  const [ip, setIp] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const params = { page, size: 10, sortBy, sortDir };
      if (action) params.action = action;
      if (email) params.userEmail = email;
      if (ip) params.ipAddress = ip;
      if (search) params.search = search;

      const response = await api.get('/v1/api/audit/logs', { params });
      setLogs(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      toast.error('Failed to load audit logs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchLogs(); }, [page, sortBy, sortDir, action]);

  const handleSearchSubmit = (e) => { e.preventDefault(); setPage(0); fetchLogs(); };

  const handleSort = (field) => {
    const isAsc = sortBy === field && sortDir === 'asc';
    setSortDir(isAsc ? 'desc' : 'asc');
    setSortBy(field);
  };

  const handleExport = async (format) => {
    const params = {};
    if (action) params.action = action;
    if (email) params.userEmail = email;
    if (ip) params.ipAddress = ip;
    if (search) params.search = search;

    const fileFormat = format === 'excel' ? 'xlsx' : format;
    toast.success(`Exporting as ${format.toUpperCase()}...`);
    try {
      const res = await api.get(`/v1/api/audit/export/${format}`, { params, responseType: 'blob' });
      const blob = new Blob([res.data], {
        type:
          format === 'pdf'
            ? 'application/pdf'
            : format === 'csv'
            ? 'text/csv'
            : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `audit_logs_${Date.now()}.${fileFormat}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      toast.success(`${format.toUpperCase()} downloaded successfully!`);
    } catch (err) {
      toast.error(`Failed to export audit logs as ${format.toUpperCase()}.`);
    }
  };

  const filterInputCls = 'block w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all';
  const exportBtnCls = 'flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors shadow-sm cursor-pointer';
  const paginationBtnCls = 'flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-colors cursor-pointer';

  return (
    <div className="space-y-6">
      <Toaster position="top-right" />

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900">Security &amp; Audit Logs</h1>
          <p className="text-xs text-slate-500 mt-1">Track API request signatures, logins, and permission changes.</p>
        </div>
        <div className="flex gap-2">
          <button onClick={() => handleExport('csv')}   className={exportBtnCls}><FileDown className="h-4 w-4 text-slate-400" /> CSV</button>
          <button onClick={() => handleExport('excel')} className={exportBtnCls}><FileDown className="h-4 w-4 text-slate-400" /> Excel</button>
          <button onClick={() => handleExport('pdf')}   className={exportBtnCls}><FileDown className="h-4 w-4 text-slate-400" /> PDF</button>
        </div>
      </div>

      {/* Filters */}
      <form onSubmit={handleSearchSubmit} className="grid gap-4 sm:grid-cols-5 bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
        <div className="relative col-span-2">
          <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
            <Search className="h-4 w-4" />
          </span>
          <input
            type="text" placeholder="Search details..."
            value={search} onChange={(e) => setSearch(e.target.value)}
            className={`${filterInputCls} pl-9`}
          />
        </div>
        <input
          type="text" placeholder="Action (e.g. LOGIN)"
          value={action} onChange={(e) => setAction(e.target.value)}
          className={filterInputCls}
        />
        <input
          type="text" placeholder="User Email"
          value={email} onChange={(e) => setEmail(e.target.value)}
          className={filterInputCls}
        />
        <button type="submit" className="btn-primary">Apply Filters</button>
      </form>

      {/* Logs Table */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="table-header">
              <tr>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('action')}>
                  <div className="flex items-center gap-1">Action <ArrowUpDown className="h-3 w-3" /></div>
                </th>
                <th className="px-6 py-4">Description</th>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('userEmail')}>
                  <div className="flex items-center gap-1">Email <ArrowUpDown className="h-3 w-3" /></div>
                </th>
                <th className="px-6 py-4">IP Address</th>
                <th className="px-6 py-4">Metadata</th>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('createdAt')}>
                  <div className="flex items-center gap-1">Time <ArrowUpDown className="h-3 w-3" /></div>
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan="6" className="px-6 py-10 text-center">
                    <div className="inline-block h-6 w-6 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-10 text-center text-slate-400">
                    No audit logs matching selection found.
                  </td>
                </tr>
              ) : (
                logs.map((item, i) => (
                  <tr key={i} className="table-row">
                    <td className="whitespace-nowrap px-6 py-4 font-semibold text-slate-900 flex items-center gap-2">
                      <ShieldAlert className="h-4 w-4 text-slate-400" />
                      {item.action}
                    </td>
                    <td className="px-6 py-4 text-slate-500">{item.description}</td>
                    <td className="px-6 py-4 text-slate-500">{item.userEmail}</td>
                    <td className="px-6 py-4 text-slate-400">{item.ipAddress}</td>
                    <td className="px-6 py-4 text-xs">
                      {item.browser && (
                        <div className="text-slate-400">
                          {item.browser} ({item.operatingSystem})
                        </div>
                      )}
                      <div className="font-mono text-slate-400">{item.requestMethod} {item.requestUrl}</div>
                    </td>
                    <td className="whitespace-nowrap px-6 py-4 text-slate-400">
                      {new Date(item.createdAt).toLocaleString()}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-100 px-6 py-4">
            <span className="text-xs text-slate-500">Page {page + 1} of {totalPages}</span>
            <div className="flex gap-2">
              <button disabled={page === 0} onClick={() => setPage(page - 1)} className={paginationBtnCls}>
                <ChevronLeft className="h-4 w-4" /> Prev
              </button>
              <button disabled={page === totalPages - 1} onClick={() => setPage(page + 1)} className={paginationBtnCls}>
                Next <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuditLogsPage;
