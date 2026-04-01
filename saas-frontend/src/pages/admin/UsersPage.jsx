import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { Search, UserMinus, ShieldAlert, ArrowUpDown, ChevronLeft, ChevronRight } from 'lucide-react';

const UsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [tenantFilter, setTenantFilter] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('id');
  const [sortDir, setSortDir] = useState('asc');

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const params = { page, size: 8, sortBy, sortDir };
      if (searchTerm) params.name = searchTerm;
      if (roleFilter) params.role = roleFilter;
      if (tenantFilter) params.tenantId = Number(tenantFilter);

      const response = await api.get('/v1/api/admin/users/search', { params });
      setUsers(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      toast.error('Failed to load user directory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, [page, sortBy, sortDir, roleFilter]);

  const handleSearchSubmit = (e) => { e.preventDefault(); setPage(0); fetchUsers(); };

  const handleSort = (field) => {
    const isAsc = sortBy === field && sortDir === 'asc';
    setSortDir(isAsc ? 'desc' : 'asc');
    setSortBy(field);
  };

  const handleRoleChange = async (id, currentRole) => {
    const newRole = currentRole === 'ADMIN' ? 'USER' : 'ADMIN';
    try {
      await api.put(`/v1/api/admin/users/${id}/role`, { role: newRole });
      toast.success('User role modified successfully!');
      fetchUsers();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update user role.');
    }
  };

  const handleDeleteUser = async (id) => {
    if (window.confirm('Are you sure you want to delete this user profile?')) {
      try {
        await api.delete(`/v1/api/admin/users/${id}`);
        toast.success('User profile removed.');
        fetchUsers();
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to delete user.');
      }
    }
  };

  const filterInputCls = 'block w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all';
  const paginationBtnCls = 'flex items-center gap-1 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-colors cursor-pointer';

  return (
    <div className="space-y-6">
      <Toaster position="top-right" />

      <div>
        <h1 className="text-xl font-bold tracking-tight text-slate-900">User Directory</h1>
        <p className="text-xs text-slate-500 mt-1">Search, edit user authorization scopes, and delete profiles.</p>
      </div>

      {/* Filters */}
      <form onSubmit={handleSearchSubmit} className="grid gap-4 sm:grid-cols-4 bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
        <div className="relative">
          <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
            <Search className="h-4 w-4" />
          </span>
          <input
            type="text" placeholder="Search by name..."
            value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
            className={`${filterInputCls} pl-9`}
          />
        </div>
        <select
          value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}
          className={filterInputCls}
        >
          <option value="">All Roles</option>
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
        </select>
        <input
          type="number" placeholder="Filter by Tenant ID"
          value={tenantFilter} onChange={(e) => setTenantFilter(e.target.value)}
          className={filterInputCls}
        />
        <button type="submit" className="btn-primary">Apply Filters</button>
      </form>

      {/* Users Table */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="table-header">
              <tr>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('id')}>
                  <div className="flex items-center gap-1">ID <ArrowUpDown className="h-3 w-3" /></div>
                </th>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('name')}>
                  <div className="flex items-center gap-1">Name <ArrowUpDown className="h-3 w-3" /></div>
                </th>
                <th className="px-6 py-4 cursor-pointer" onClick={() => handleSort('email')}>
                  <div className="flex items-center gap-1">Email <ArrowUpDown className="h-3 w-3" /></div>
                </th>
                <th className="px-6 py-4">Role</th>
                <th className="px-6 py-4">Tenant ID</th>
                <th className="px-6 py-4">Verification</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan="7" className="px-6 py-10 text-center">
                    <div className="inline-block h-6 w-6 animate-spin rounded-full border-2 border-slate-200 border-t-indigo-600" />
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-10 text-center text-slate-400">
                    No users matching criteria found.
                  </td>
                </tr>
              ) : (
                users.map((item) => (
                  <tr key={item.id} className="table-row">
                    <td className="px-6 py-4 font-semibold text-slate-900">{item.id}</td>
                    <td className="px-6 py-4 font-medium text-slate-900">{item.name}</td>
                    <td className="px-6 py-4 text-slate-500">{item.email}</td>
                    <td className="px-6 py-4">
                      <span className={item.role === 'ADMIN' ? 'badge-indigo' : 'badge-slate'}>
                        {item.role}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-500">{item.tenantId}</td>
                    <td className="px-6 py-4">
                      <span className={item.emailVerified ? 'badge-success' : 'badge-warning'}>
                        {item.emailVerified ? 'Verified' : 'Pending'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => handleRoleChange(item.id, item.role)}
                          className="rounded-lg border border-slate-200 bg-white p-1.5 text-slate-500 hover:bg-indigo-50 hover:text-indigo-600 transition-colors cursor-pointer"
                          title="Toggle Role"
                        >
                          <ShieldAlert className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteUser(item.id)}
                          className="rounded-lg border border-slate-200 bg-white p-1.5 text-red-500 hover:bg-red-50 transition-colors cursor-pointer"
                          title="Delete User"
                        >
                          <UserMinus className="h-4 w-4" />
                        </button>
                      </div>
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

export default UsersPage;
