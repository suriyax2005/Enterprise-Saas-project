import React, { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { Lock, ArrowLeft, ShieldCheck, Zap } from 'lucide-react';
import AuthLayout from './AuthLayout';
import AuthCard from '../../components/auth/AuthCard';
import AuthInput from '../../components/auth/AuthInput';
import AuthButton from '../../components/auth/AuthButton';

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) {
      toast.error('Token parameter is missing in URL');
      return;
    }
    if (!password) {
      toast.error('Password is required');
      return;
    }
    setSubmitting(true);
    try {
      const res = await api.post('/v1/api/auth/reset-password', {
        token,
        newPassword: password,
      });
      toast.success(res.data.message || 'Password updated successfully!');
      setTimeout(() => navigate('/login'), 2500);
    } catch (err) {
      const errMsg =
        err.response?.data?.message || 'Failed to update password.';
      toast.error(errMsg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout type="login">
      <Toaster position="top-right" />

      <AuthCard>
        {/* ── Header ────────────────────────────────────────── */}
        <div className="flex flex-col items-center text-center">
          {/* Logo mark */}
          <div className="flex items-center gap-2.5 mb-7">
            <div className="h-8 w-8 rounded-lg bg-gradient-to-br from-indigo-600 to-indigo-500 flex items-center justify-center shadow-md shadow-indigo-500/30">
              <Zap className="h-4 w-4 text-white fill-white" />
            </div>
            <span className="text-[15px] font-bold text-slate-900 tracking-tight">
              Workspace
            </span>
          </div>

          {/* Icon badge */}
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-50 border border-indigo-100 mb-4 shadow-sm">
            <ShieldCheck className="h-6 w-6 text-indigo-600" />
          </div>

          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Set new password
          </h1>
          <p className="mt-2 text-sm text-slate-500 leading-relaxed">
            Choose a strong password for your account
          </p>
        </div>

        {/* ── Form ──────────────────────────────────────────── */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <AuthInput
            id="reset-password"
            icon={<Lock className="h-4 w-4" />}
            label="New Password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 8 characters"
            disabled={submitting}
          />

          <div className="pt-1">
            <AuthButton
              loading={submitting}
              text="Save Password"
              loadingText="Updating..."
            />
          </div>
        </form>

        {/* ── Footer ────────────────────────────────────────── */}
        <div className="text-center pt-1 border-t border-slate-100">
          <Link
            to="/login"
            className="inline-flex items-center gap-1.5 text-sm font-semibold text-slate-500 hover:text-slate-800 transition-colors"
          >
            <ArrowLeft className="h-3.5 w-3.5" />
            Cancel
          </Link>
        </div>
      </AuthCard>
    </AuthLayout>
  );
};

export default ResetPasswordPage;
