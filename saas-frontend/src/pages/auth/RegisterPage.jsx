import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast, { Toaster } from 'react-hot-toast';
import { User, Mail, Lock, Hash, Zap } from 'lucide-react';
import api from '../../api/axios';
import AuthLayout from './AuthLayout';
import AuthCard from '../../components/auth/AuthCard';
import AuthInput from '../../components/auth/AuthInput';
import AuthButton from '../../components/auth/AuthButton';

const RegisterPage = () => {
  const [searchParams] = useSearchParams();
  const inviteToken = searchParams.get('inviteToken');

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [tenantId, setTenantId] = useState('');
  const [orgName, setOrgName] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (inviteToken) {
      const fetchInviteDetail = async () => {
        try {
          const res = await api.get(
            `/v1/api/organization/invitations/detail?token=${inviteToken}`
          );
          setEmail(res.data.email);
          setTenantId(res.data.tenantId);
          setOrgName(res.data.organizationName);
        } catch (err) {
          toast.error('Invitation is invalid or has expired.');
        }
      };
      fetchInviteDetail();
    }
  }, [inviteToken]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !email || !password || !tenantId) {
      toast.error('All fields are required');
      return;
    }
    setSubmitting(true);
    try {
      const res = await register(name, email, password, tenantId, inviteToken);
      toast.success(
        res.message || 'Registration successful! Verification email sent.'
      );
      setTimeout(() => navigate('/login'), 3000);
    } catch (err) {
      const errMsg =
        err.response?.data?.message ||
        'Registration failed. Please check details.';
      toast.error(errMsg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout type="register">
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

          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Create your account
          </h1>
          <p className="mt-2 text-sm text-slate-500 leading-relaxed">
            Join your organization&apos;s workspace today
          </p>

          {/* Invite badge */}
          {orgName && (
            <div className="mt-3 inline-flex items-center gap-1.5 rounded-full bg-indigo-50 border border-indigo-100 px-3.5 py-1.5">
              <div className="h-1.5 w-1.5 rounded-full bg-indigo-500 shrink-0" />
              <span className="text-xs font-semibold text-indigo-600">
                Joining: {orgName}
              </span>
            </div>
          )}
        </div>

        {/* ── Form ──────────────────────────────────────────── */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <AuthInput
            id="reg-name"
            icon={<User className="h-4 w-4" />}
            label="Full Name"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="John Doe"
            disabled={submitting}
          />

          <AuthInput
            id="reg-email"
            icon={<Mail className="h-4 w-4" />}
            label="Email Address"
            type="email"
            required
            disabled={!!inviteToken || submitting}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="name@company.com"
          />

          <AuthInput
            id="reg-password"
            icon={<Lock className="h-4 w-4" />}
            label="Password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Min. 8 chars, 1 digit, 1 special symbol"
            disabled={submitting}
          />

          <AuthInput
            id="reg-tenant"
            icon={<Hash className="h-4 w-4" />}
            label="Workspace ID"
            type="number"
            required
            disabled={!!inviteToken || submitting}
            value={tenantId}
            onChange={(e) => setTenantId(e.target.value)}
            placeholder="e.g. 1"
          />

          <div className="pt-1">
            <AuthButton
              loading={submitting}
              text="Create Account"
              loadingText="Creating account..."
            />
          </div>
        </form>

        {/* ── Footer ────────────────────────────────────────── */}
        <div className="text-center pt-1 border-t border-slate-100">
          <p className="text-sm text-slate-500">
            Already have an account?{' '}
            <Link
              to="/login"
              className="font-semibold text-indigo-600 hover:text-indigo-500 transition-colors"
            >
              Sign in →
            </Link>
          </p>
        </div>
      </AuthCard>
    </AuthLayout>
  );
};

export default RegisterPage;
