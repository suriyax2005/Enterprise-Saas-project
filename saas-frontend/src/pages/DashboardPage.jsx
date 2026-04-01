import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Users, Layers, ShieldAlert, Bell, TrendingUp, RefreshCw, FolderOpen, Mail, ShieldCheck } from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import toast, { Toaster } from 'react-hot-toast';

const DashboardPage = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchStats = async () => {
    try {
      setLoading(true);
      if (user?.role === 'ADMIN') {
        const res = await api.get('/v1/api/admin/dashboard');
        setStats(res.data);
      } else {
        const [statsRes, notificationsRes] = await Promise.all([
          api.get('/v1/api/organization/statistics'),
          api.get('/v1/api/notifications'),
        ]);

        const storedFiles = localStorage.getItem(`saas_files_${user?.userId}`);
        const filesCount = storedFiles ? JSON.parse(storedFiles).length : 0;
        const parsedFiles = storedFiles ? JSON.parse(storedFiles) : [];

        setStats({
          totalMembers: statsRes.data.totalMembers,
          activeInvitations: statsRes.data.activeInvitations,
          organizationName: statsRes.data.organizationName,
          notifications: notificationsRes.data,
          unreadNotifications: notificationsRes.data.filter((n) => !n.read).length,
          filesCount,
          recentFiles: parsedFiles.slice(0, 5),
        });
      }
    } catch (err) {
      toast.error('Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchStats(); }, [user]);

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />
      </div>
    );
  }

  const chartData = [
    { name: 'Mon', Activity: 8 },
    { name: 'Tue', Activity: 15 },
    { name: 'Wed', Activity: user?.role === 'ADMIN' && stats?.totalAuditLogs ? stats.totalAuditLogs : 24 },
    { name: 'Thu', Activity: user?.role === 'ADMIN' && stats?.totalAuditLogs ? stats.totalAuditLogs + 5 : 18 },
    { name: 'Fri', Activity: user?.role === 'ADMIN' && stats?.totalAuditLogs ? stats.totalAuditLogs + 12 : 32 },
  ];

  /* ── Shared sub-components ───────────────────────────────── */

  const StatCard = ({ label, value, icon: Icon, iconBg, iconColor }) => (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex items-center justify-between">
        <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">{label}</span>
        <span className={`rounded-lg ${iconBg} ${iconColor} p-2`}>
          <Icon className="h-5 w-5" />
        </span>
      </div>
      <div className="mt-4">
        <h3 className="text-3xl font-bold text-slate-900">{value}</h3>
      </div>
    </div>
  );

  const ChartCard = ({ id, title, children }) => (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-xs font-bold text-slate-500 mb-6 uppercase tracking-wider">{title}</h3>
      <div className="h-64 w-full">{children}</div>
    </div>
  );

  const chartTooltipStyle = {
    backgroundColor: '#FFFFFF',
    border: '1px solid #E2E8F0',
    borderRadius: '10px',
    boxShadow: '0 4px 12px rgba(15,23,42,0.08)',
    fontSize: '12px',
  };

  return (
    <div className="space-y-8">
      <Toaster position="top-right" />

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900">
            {user?.role === 'ADMIN' ? 'Platform Administrator Console' : 'Employee Workspace'}
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            {user?.role === 'ADMIN'
              ? 'Track system-wide workspace metrics, active audit logs, and status updates.'
              : `Dashboard for ${stats?.organizationName || 'your organization'} member workspace.`}
          </p>
        </div>
        <button
          onClick={fetchStats}
          className="flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3.5 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-50 shadow-sm transition-colors cursor-pointer"
        >
          <RefreshCw className="h-3.5 w-3.5 text-slate-400" />
          Refresh
        </button>
      </div>

      {/* ── Admin Dashboard ─────────────────────────────────── */}
      {user?.role === 'ADMIN' ? (
        <>
          {/* Stats Cards */}
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Users</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <Users className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.totalUsers || 0}</h3>
                <p className="text-[10px] text-indigo-600 mt-1.5 font-semibold flex items-center gap-1">
                  <TrendingUp className="h-3 w-3" />
                  Direct registry profiles
                </p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Active Tenants</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <Layers className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.totalTenants || 0}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Database workspaces</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Audit Records</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <ShieldAlert className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.totalAuditLogs || 0}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Registered security entries</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Unread Alerts</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <Bell className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.unreadNotifications || 0}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Action alerts waiting for read</p>
              </div>
            </div>
          </div>

          {/* Chart */}
          <ChartCard id="admin-chart" title="Activity Growth">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="colorActivity" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%"  stopColor="#4F46E5" stopOpacity={0.12} />
                    <stop offset="95%" stopColor="#4F46E5" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
                <XAxis dataKey="name" stroke="#94A3B8" fontSize={11} tickLine={false} />
                <YAxis stroke="#94A3B8" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip contentStyle={chartTooltipStyle} />
                <Area type="monotone" dataKey="Activity" stroke="#4F46E5" strokeWidth={2} fillOpacity={1} fill="url(#colorActivity)" />
              </AreaChart>
            </ResponsiveContainer>
          </ChartCard>

          {/* Recent Activity Table */}
          <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
            <div className="border-b border-slate-200 px-6 py-4">
              <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Recent Activity Logs</h3>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="table-header">
                  <tr>
                    <th className="px-6 py-3.5">Action</th>
                    <th className="px-6 py-3.5">Details</th>
                    <th className="px-6 py-3.5">Email</th>
                    <th className="px-6 py-3.5">IP Address</th>
                    <th className="px-6 py-3.5">Time</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 bg-white">
                  {stats?.recentActivity?.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="px-6 py-10 text-center text-slate-400">
                        No activity logs recorded.
                      </td>
                    </tr>
                  ) : (
                    stats?.recentActivity?.map((log, i) => (
                      <tr key={i} className="table-row">
                        <td className="whitespace-nowrap px-6 py-4 font-semibold text-slate-900">{log.action}</td>
                        <td className="px-6 py-4 max-w-xs truncate text-slate-500">{log.description}</td>
                        <td className="px-6 py-4 text-slate-500">{log.userEmail}</td>
                        <td className="px-6 py-4 text-slate-500">{log.ipAddress}</td>
                        <td className="whitespace-nowrap px-6 py-4 text-slate-400">
                          {new Date(log.createdAt).toLocaleString()}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      ) : (
        /* ── Employee Dashboard ─────────────────────────────── */
        <>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">My Workspace</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <ShieldCheck className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-xl font-bold text-slate-900 truncate max-w-[200px]">
                  {stats?.organizationName || 'Default Workspace'}
                </h3>
                <p className="text-[10px] text-slate-400 mt-2">Active SaaS environment</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Colleagues</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <Users className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.totalMembers || 1}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Registered workspace accounts</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Active Invites</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <Mail className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.activeInvitations || 0}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Pending employee invites</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">My Documents</span>
                <span className="rounded-lg bg-indigo-50 p-2 text-indigo-600">
                  <FolderOpen className="h-5 w-5" />
                </span>
              </div>
              <div className="mt-4">
                <h3 className="text-3xl font-bold text-slate-900">{stats?.filesCount || 0}</h3>
                <p className="text-[10px] text-slate-400 mt-1.5">Personal storage entries</p>
              </div>
            </div>
          </div>

          {/* Chart + Alerts */}
          <div className="grid gap-6 md:grid-cols-3">
            <ChartCard id="user-chart" title="My Workload Activity">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="colorWorkload" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%"  stopColor="#4F46E5" stopOpacity={0.12} />
                      <stop offset="95%" stopColor="#4F46E5" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
                  <XAxis dataKey="name" stroke="#94A3B8" fontSize={11} tickLine={false} />
                  <YAxis stroke="#94A3B8" fontSize={11} tickLine={false} axisLine={false} />
                  <Tooltip contentStyle={chartTooltipStyle} />
                  <Area type="monotone" dataKey="Activity" stroke="#4F46E5" strokeWidth={2} fillOpacity={1} fill="url(#colorWorkload)" />
                </AreaChart>
              </ResponsiveContainer>
            </ChartCard>

            {/* System Alerts */}
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm flex flex-col">
              <h3 className="text-xs font-bold text-slate-500 mb-6 uppercase tracking-wider">System Alerts</h3>
              <div className="space-y-4 flex-1">
                {stats?.notifications?.length === 0 ? (
                  <p className="text-xs text-slate-400 text-center py-10">No recent notifications.</p>
                ) : (
                  stats?.notifications?.slice(0, 4).map((n) => (
                    <div key={n.id} className="flex items-start gap-2.5">
                      <div
                        className={`mt-0.5 h-2 w-2 rounded-full shrink-0 ${
                          !n.isRead ? 'bg-indigo-500' : 'bg-slate-300'
                        }`}
                      />
                      <div>
                        <p className="text-xs text-slate-700 leading-normal">{n.message}</p>
                        <span className="text-[9px] text-slate-400 mt-0.5 block">
                          {new Date(n.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default DashboardPage;
