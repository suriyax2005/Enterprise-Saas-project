import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import api from '../../api/axios';
import { CheckCircle, XCircle, Loader2, Zap, ArrowRight } from 'lucide-react';
import AuthLayout from './AuthLayout';
import AuthCard from '../../components/auth/AuthCard';

const VerifyEmailPage = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [status, setStatus] = useState('verifying'); // verifying | success | error
  const [message, setMessage] = useState('');

  useEffect(() => {
    const verifyToken = async () => {
      if (!token) {
        setStatus('error');
        setMessage('Verification token is missing in URL');
        return;
      }
      try {
        const res = await api.get(`/v1/api/auth/verify?token=${token}`);
        setStatus('success');
        setMessage(res.data.message || 'Email verified successfully!');
      } catch (err) {
        setStatus('error');
        setMessage(
          err.response?.data?.message ||
            'Verification link expired or invalid.'
        );
      }
    };
    verifyToken();
  }, [token]);

  return (
    <AuthLayout type="register">
      <AuthCard>
        {/* ── Logo mark ─────────────────────────────────────── */}
        <div className="flex items-center gap-2.5 justify-center">
          <div className="h-8 w-8 rounded-lg bg-gradient-to-br from-indigo-600 to-indigo-500 flex items-center justify-center shadow-md shadow-indigo-500/30">
            <Zap className="h-4 w-4 text-white fill-white" />
          </div>
          <span className="text-[15px] font-bold text-slate-900 tracking-tight">
            Workspace
          </span>
        </div>

        {/* ── Verifying ─────────────────────────────────────── */}
        {status === 'verifying' && (
          <div className="flex flex-col items-center py-6 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-indigo-50 border border-indigo-100 shadow-sm">
              <Loader2 className="h-7 w-7 text-indigo-600 animate-spin" />
            </div>
            <h2 className="text-xl font-bold mt-5 text-slate-900">
              Verifying your email…
            </h2>
            <p className="text-sm text-slate-500 mt-2">
              Please wait while we validate your link
            </p>
          </div>
        )}

        {/* ── Success ───────────────────────────────────────── */}
        {status === 'success' && (
          <div className="flex flex-col items-center py-6 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-emerald-50 border border-emerald-100 shadow-sm">
              <CheckCircle className="h-7 w-7 text-emerald-500" />
            </div>
            <h2 className="text-2xl font-bold mt-5 text-slate-900">
              Email Verified!
            </h2>
            <p className="text-sm text-slate-500 mt-2 leading-relaxed max-w-[260px]">
              {message}
            </p>
            <Link
              to="/login"
              className="auth-btn-gradient mt-7 flex w-full items-center justify-center gap-2 rounded-xl py-3 text-[15px] font-semibold text-white shadow-md shadow-indigo-500/20"
            >
              Go to Sign In
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        )}

        {/* ── Error ─────────────────────────────────────────── */}
        {status === 'error' && (
          <div className="flex flex-col items-center py-6 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-red-50 border border-red-100 shadow-sm">
              <XCircle className="h-7 w-7 text-red-500" />
            </div>
            <h2 className="text-2xl font-bold mt-5 text-slate-900">
              Verification Failed
            </h2>
            <p className="text-sm text-slate-500 mt-2 leading-relaxed max-w-[260px]">
              {message}
            </p>
            <Link
              to="/login"
              className="mt-7 flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white py-3 text-[15px] font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Back to Sign In
            </Link>
          </div>
        )}
      </AuthCard>
    </AuthLayout>
  );
};

export default VerifyEmailPage;
