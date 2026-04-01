import React, { useEffect, useState } from 'react';
import api from '../../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { Layers, Plus, ShieldCheck, RefreshCw } from 'lucide-react';

const TenantsPage = () => {
  const [tenants, setTenants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newTenantName, setNewTenantName] = useState('');
  const [creating, setCreating] = useState(false);

  const fetchTenants = async () => {
    try {
      setLoading(true);
      const response = await api.get('/v1/api/tenant');
      setTenants(response.data);
    } catch (err) {
      toast.error('Failed to load tenants list');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchTenants(); }, []);

  const handleCreateTenant = async (e) => {
    e.preventDefault();
    if (!newTenantName) { toast.error('Tenant name is required'); return; }
    setCreating(true);
    try {
      const response = await api.post('/v1/api/tenant', { name: newTenantName });
      toast.success(`Tenant "${response.data.name}" created successfully with ID: ${response.data.id}`);
      setNewTenantName('');
      fetchTenants();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to register tenant.');
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="space-y-8">
      <Toaster position="top-right" />

      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900">Tenant Workspaces</h1>
          <p className="text-xs text-slate-500 mt-1">Configure boundaries and register new tenant databases.</p>
        </div>
        <button
          onClick={fetchTenants}
          className="flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3.5 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-50 shadow-sm transition-colors cursor-pointer"
        >
          <RefreshCw className="h-3.5 w-3.5 text-slate-400" />
          Refresh
        </button>
      </div>

      <div className="grid gap-8 md:grid-cols-3">
        {/* Create Workspace Form */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm h-fit">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2 mb-6">
            <Plus className="h-4 w-4 text-indigo-600" />
            New Workspace
          </h3>
          <form onSubmit={handleCreateTenant} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-600 tracking-wide">Tenant Name</label>
              <input
                type="text" required
                value={newTenantName}
                onChange={(e) => setNewTenantName(e.target.value)}
                placeholder="e.g. Acme Corp"
                className="mt-1.5 block w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder-slate-400 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all"
              />
            </div>
            <div className="pt-2">
              <button
                type="submit" disabled={creating}
                className="w-full btn-primary disabled:opacity-55"
              >
                {creating ? 'Registering...' : 'Create Workspace'}
              </button>
            </div>
          </form>
        </div>

        {/* Active Workspaces List */}
        <div className="md:col-span-2 rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
          <div className="border-b border-slate-200 px-6 py-4">
            <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Active Tenants Directory</h3>
          </div>
          <div className="divide-y divide-slate-100 bg-white">
            {loading ? (
              <div className="flex justify-center py-12">
                <div className="h-6 w-6 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />
              </div>
            ) : tenants.length === 0 ? (
              <div className="text-center py-12 text-slate-400">No tenants registered.</div>
            ) : (
              tenants.map((t) => (
                <div key={t.id} className="flex items-center justify-between px-6 py-4 hover:bg-slate-50 transition-colors">
                  <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
                      <Layers className="h-5 w-5" />
                    </div>
                    <div>
                      <h4 className="font-semibold text-slate-900 text-sm">{t.name}</h4>
                      <p className="text-xs text-slate-400">ID: {t.id}</p>
                    </div>
                  </div>
                  <span className="badge-success flex items-center gap-1">
                    <ShieldCheck className="h-3.5 w-3.5" /> Isolated
                  </span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TenantsPage;
