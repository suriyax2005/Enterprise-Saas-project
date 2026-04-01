import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import toast, { Toaster } from 'react-hot-toast';
import { User, Lock, Save, Trash2 } from 'lucide-react';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const [profile, setProfile] = useState({ name: '', email: '' });
  const [passwordForm, setPasswordForm] = useState({ oldPassword: '', newPassword: '' });
  const [loading, setLoading] = useState(true);
  const [updatingProfile, setUpdatingProfile] = useState(false);
  const [updatingPassword, setUpdatingPassword] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get('/v1/api/users/profile');
        setProfile({ name: response.data.name, email: response.data.email });
      } catch (err) {
        toast.error('Failed to load profile details');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    if (!profile.name) { toast.error('Name cannot be empty'); return; }
    setUpdatingProfile(true);
    try {
      const response = await api.put('/v1/api/users/profile', { name: profile.name });
      setProfile({ name: response.data.name, email: response.data.email });
      const stored = JSON.parse(localStorage.getItem('user'));
      stored.name = response.data.name;
      localStorage.setItem('user', JSON.stringify(stored));
      toast.success('Profile updated successfully!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update profile.');
    } finally {
      setUpdatingProfile(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (!passwordForm.oldPassword || !passwordForm.newPassword) {
      toast.error('Both fields are required');
      return;
    }
    setUpdatingPassword(true);
    try {
      const response = await api.put('/v1/api/users/profile/change-password', passwordForm);
      toast.success(response.data.message || 'Password changed successfully!');
      setPasswordForm({ oldPassword: '', newPassword: '' });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Password update failed.');
    } finally {
      setUpdatingPassword(false);
    }
  };

  const handleDeleteProfile = async () => {
    if (window.confirm('CRITICAL: Are you sure you want to permanently delete your account? This action is irreversible.')) {
      try {
        await api.delete('/v1/api/users/profile');
        toast.success('Account deleted successfully.');
        logout();
      } catch (err) {
        toast.error('Failed to delete account.');
      }
    }
  };

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600" />
      </div>
    );
  }

  const inputCls = 'mt-1.5 block w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder-slate-400 shadow-sm focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all';
  const inputDisabledCls = 'mt-1.5 block w-full rounded-xl border border-slate-200 bg-slate-50 px-3.5 py-2.5 text-sm text-slate-400 cursor-not-allowed';
  const labelCls = 'text-xs font-semibold text-slate-600 tracking-wide';

  return (
    <div className="space-y-8 max-w-4xl mx-auto">
      <Toaster position="top-right" />

      <div>
        <h1 className="text-xl font-bold tracking-tight text-slate-900">Profile Settings</h1>
        <p className="text-xs text-slate-500 mt-1">
          Manage your workspace identity and password credentials.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Profile Card */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2 mb-6">
            <User className="h-4 w-4 text-indigo-600" />
            Personal Details
          </h3>
          <form onSubmit={handleUpdateProfile} className="space-y-4">
            <div>
              <label className={labelCls}>Email (Read Only)</label>
              <input type="email" disabled value={profile.email} className={inputDisabledCls} />
            </div>
            <div>
              <label className={labelCls}>Display Name</label>
              <input
                type="text" required value={profile.name}
                onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                placeholder="Full Name"
                className={inputCls}
              />
            </div>
            <div className="pt-2">
              <button type="submit" disabled={updatingProfile} className="btn-primary disabled:opacity-55">
                <Save className="h-4 w-4" />
                {updatingProfile ? 'Saving...' : 'Save Name'}
              </button>
            </div>
          </form>
        </div>

        {/* Change Password Card */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2 mb-6">
            <Lock className="h-4 w-4 text-indigo-600" />
            Security &amp; Password
          </h3>
          <form onSubmit={handleChangePassword} className="space-y-4">
            <div>
              <label className={labelCls}>Current Password</label>
              <input
                type="password" required value={passwordForm.oldPassword}
                onChange={(e) => setPasswordForm({ ...passwordForm, oldPassword: e.target.value })}
                placeholder="••••••••"
                className={inputCls}
              />
            </div>
            <div>
              <label className={labelCls}>New Password</label>
              <input
                type="password" required value={passwordForm.newPassword}
                onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                placeholder="••••••••"
                className={inputCls}
              />
            </div>
            <div className="pt-2">
              <button type="submit" disabled={updatingPassword} className="btn-primary disabled:opacity-55">
                <Save className="h-4 w-4" />
                {updatingPassword ? 'Updating...' : 'Update Password'}
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* Danger Zone */}
      <div className="rounded-xl border border-red-200 bg-red-50/30 p-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h3 className="text-xs font-bold text-red-800 uppercase tracking-wider">Danger Zone</h3>
            <p className="text-xs text-red-600 mt-1">
              Permanently delete your profile and account database records. This is immediate.
            </p>
          </div>
          <button
            onClick={handleDeleteProfile}
            className="flex items-center justify-center gap-2 rounded-xl bg-red-600 px-4 py-2.5 text-xs font-semibold text-white shadow-sm hover:bg-red-500 transition-colors cursor-pointer"
          >
            <Trash2 className="h-4 w-4" />
            Delete Account
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
