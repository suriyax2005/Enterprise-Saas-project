import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { Layers, Mail, Plus, Save, Users, Link2, CheckCircle, Clock } from 'lucide-react';

const OrganizationPage = () => {
  const [org, setOrg] = useState(null);
  const [stats, setStats] = useState(null);
  const [invitations, setInvitations] = useState([]);
  const [loading, setLoading] = useState(true);

  const [orgForm, setOrgForm] = useState({ name: '', description: '', logoUrl: '' });
  const [invForm, setInvForm] = useState({ email: '', role: 'EMPLOYEE' });
  const [updating, setUpdating] = useState(false);
  const [inviting, setInviting] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [orgRes, statsRes, invRes] = await Promise.all([
        api.get('/v1/api/organization/my'),
        api.get('/v1/api/organization/statistics'),
        api.get('/v1/api/organization/invitations'),
      ]);
      setOrg(orgRes.data);
      setOrgForm({
        name: orgRes.data.name,
        description: orgRes.data.description || '',
        logoUrl: orgRes.data.logoUrl || '',
      });
      setStats(statsRes.data);
      setInvitations(invRes.data);
    } catch (err) {
      toast.error('Failed to load organization details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const handleUpdateOrg = async (e) => {
    e.preventDefault();
    if (!orgForm.name) { toast.error('Organization name is required'); return; }
    setUpdating(true);
    try {
      const res = await api.put('/v1/api/organization/my', orgForm);
      setOrg(res.data);
      toast.success('Organization updated successfully!');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update organization.');
    } finally {
      setUpdating(false);
    }
  };

  const handleSendInvite = async (e) => {
    e.preventDefault();
    if (!invForm.email || !invForm.role) { toast.error('All fields are required'); return; }
    setInviting(true);
    try {
      await api.post('/v1/api/organization/invitations', invForm);
      toast.success(`Invitation sent to ${invForm.email}`);
      setInvForm({ email: '', role: 'EMPLOYEE' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to send invitation.');
    } finally {
      setInviting(false);
    }
  };

  const copyInviteLink = (token) => {
    const link = `${window.location.origin}/register?inviteToken=${token}`;
    navigator.clipboard.writeText(link);
    toast.success('Invitation link copied to clipboard!');
  };

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />
      </div>
    );
  }

  /* shared input class */
  const inputCls = 'mt-1.5 block w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder-slate-400 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all';
  const inputDisabledCls = 'mt-1.5 block w-full rounded-xl border border-slate-200 bg-slate-50 px-3.5 py-2.5 text-sm text-slate-400 cursor-not-allowed';
  const labelCls = 'text-xs font-semibold text-slate-600 tracking-wide';

  return (
    <div className="space-y-8">
      <Toaster position="top-right" />

      {/* Header */}
      <div>
        <h1 className="text-xl font-bold tracking-tight text-slate-900">Organization Settings</h1>
        <p className="text-xs text-slate-500 mt-1">
          Manage tenant profile, configuration options, and workspace invitations.
        </p>
      </div>

      {/* Stats Cards */}
      {stats && (
        <div className="grid gap-5 sm:grid-cols-3">
          <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
            <div className="p-3 bg-indigo-50 rounded-lg text-indigo-600">
              <Users className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs text-slate-400 font-medium">Workspace Members</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-0.5">{stats.totalMembers}</h3>
            </div>
          </div>
          <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
            <div className="p-3 bg-amber-50 rounded-lg text-amber-600">
              <Mail className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs text-slate-400 font-medium">Active Invitations</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-0.5">{stats.activeInvitations}</h3>
            </div>
          </div>
          <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
            <div className="p-3 bg-emerald-50 rounded-lg text-emerald-600">
              <Layers className="h-6 w-6" />
            </div>
            <div>
              <p className="text-xs text-slate-400 font-medium">Organization Workspace</p>
              <h3 className="text-base font-bold text-slate-900 mt-1 truncate max-w-[180px]">
                {stats.organizationName}
              </h3>
            </div>
          </div>
        </div>
      )}

      {/* Settings Grid */}
      <div className="grid gap-6 md:grid-cols-2">
        {/* Profile Card */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2 mb-6">
            <Layers className="h-4 w-4 text-indigo-600" />
            Organization Profile
          </h3>
          <form onSubmit={handleUpdateOrg} className="space-y-4">
            <div>
              <label className={labelCls}>Workspace ID (Tenant ID)</label>
              <input type="text" disabled value={org?.tenantId || ''} className={inputDisabledCls} />
            </div>
            <div>
              <label className={labelCls}>Organization Name</label>
              <input
                type="text" required value={orgForm.name}
                onChange={(e) => setOrgForm({ ...orgForm, name: e.target.value })}
                placeholder="Organization Name"
                className={inputCls}
              />
            </div>
            <div>
              <label className={labelCls}>Description</label>
              <textarea
                value={orgForm.description}
                onChange={(e) => setOrgForm({ ...orgForm, description: e.target.value })}
                placeholder="Workspace purpose, details, etc."
                rows={3}
                className={`${inputCls} resize-none`}
              />
            </div>
            <div>
              <label className={labelCls}>Logo URL</label>
              <input
                type="text" value={orgForm.logoUrl}
                onChange={(e) => setOrgForm({ ...orgForm, logoUrl: e.target.value })}
                placeholder="https://example.com/logo.png"
                className={inputCls}
              />
            </div>
            <div className="pt-2">
              <button
                type="submit" disabled={updating}
                className="btn-primary disabled:opacity-55"
              >
                <Save className="h-4 w-4" />
                {updating ? 'Saving...' : 'Save Settings'}
              </button>
            </div>
          </form>
        </div>

        {/* Invite Member Card */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm h-fit">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2 mb-6">
            <Plus className="h-4 w-4 text-indigo-600" />
            Invite Workspace Member
          </h3>
          <form onSubmit={handleSendInvite} className="space-y-4">
            <div>
              <label className={labelCls}>Email Address</label>
              <input
                type="email" required value={invForm.email}
                onChange={(e) => setInvForm({ ...invForm, email: e.target.value })}
                placeholder="colleague@company.com"
                className={inputCls}
              />
            </div>
            <div>
              <label className={labelCls}>Assign Role</label>
              <select
                value={invForm.role}
                onChange={(e) => setInvForm({ ...invForm, role: e.target.value })}
                className={inputCls}
              >
                <option value="EMPLOYEE">Employee</option>
                <option value="COMPANY_ADMIN">Company Admin</option>
                <option value="ORGANIZATION_ADMIN">Organization Admin</option>
                <option value="GUEST">Guest</option>
              </select>
            </div>
            <div className="pt-2">
              <button type="submit" disabled={inviting} className="w-full btn-primary disabled:opacity-55">
                {inviting ? 'Sending invitation...' : 'Send Invitation Link'}
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* Invitations Table */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="border-b border-slate-200 px-6 py-4 flex items-center justify-between">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Sent Invitations</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="table-header border-b border-slate-100">
                <th className="px-6 py-3">Invited Email</th>
                <th className="px-6 py-3">Role</th>
                <th className="px-6 py-3">Status</th>
                <th className="px-6 py-3">Sent At</th>
                <th className="px-6 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm text-slate-700 bg-white">
              {invitations.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-slate-400">
                    No invitations sent yet.
                  </td>
                </tr>
              ) : (
                invitations.map((inv) => (
                  <tr key={inv.id} className="table-row">
                    <td className="px-6 py-4 font-medium text-slate-900">{inv.email}</td>
                    <td className="px-6 py-4">
                      <span className="badge-slate">{inv.role}</span>
                    </td>
                    <td className="px-6 py-4">
                      {inv.accepted ? (
                        <span className="flex items-center gap-1 text-emerald-600 font-semibold text-xs">
                          <CheckCircle className="h-4 w-4" /> Accepted
                        </span>
                      ) : (
                        <span className="flex items-center gap-1 text-amber-600 font-semibold text-xs">
                          <Clock className="h-4 w-4" /> Pending
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-400">
                      {new Date(inv.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-right">
                      {!inv.accepted && (
                        <button
                          onClick={() => copyInviteLink(inv.inviteToken)}
                          className="inline-flex items-center gap-1 rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50 shadow-sm transition-colors cursor-pointer"
                        >
                          <Link2 className="h-3.5 w-3.5" />
                          Link
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default OrganizationPage;
