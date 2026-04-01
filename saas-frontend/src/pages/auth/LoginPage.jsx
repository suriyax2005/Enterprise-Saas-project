import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast, { Toaster } from 'react-hot-toast';
import { Mail, Lock, Zap } from 'lucide-react';
import AuthLayout from './AuthLayout';
import AuthCard from '../../components/auth/AuthCard';
import AuthInput from '../../components/auth/AuthInput';
import AuthButton from '../../components/auth/AuthButton';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();

  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      toast.error('Please enter all fields');
      return;
    }
    setSubmitting(true);
    try {
      await login(email, password);
      toast.success('Successfully logged in!');
      navigate(from, { replace: true });
    } catch (err) {
      const errMsg =
        err.response?.data?.message ||
        'Login failed. Please check your credentials.';
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

          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Sign in to your account
          </h1>
          <p className="mt-2 text-sm text-slate-500 leading-relaxed">
            Welcome back — enter your credentials below
          </p>
        </div>

        {/* ── Form ──────────────────────────────────────────── */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <AuthInput
            id="login-email"
            icon={<Mail className="h-4 w-4" />}
            label="Email Address"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="name@company.com"
            disabled={submitting}
          />

          {/* Password row — label + forgot link side-by-side */}
          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label
                htmlFor="login-password"
                className="text-xs font-semibold text-slate-600 tracking-wide"
              >
                Password
              </label>
              <Link
                to="/forgot-password"
                className="text-xs font-semibold text-indigo-600 hover:text-indigo-500 transition-colors"
              >
                Forgot password?
              </Link>
            </div>
            <AuthInput
              id="login-password"
              icon={<Lock className="h-4 w-4" />}
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              disabled={submitting}
            />
          </div>

          <div className="pt-1">
            <AuthButton
              loading={submitting}
              text="Sign in"
              loadingText="Signing in..."
            />
          </div>
        </form>

        {/* ── Footer ────────────────────────────────────────── */}
        <div className="text-center pt-1 border-t border-slate-100">
          <p className="text-sm text-slate-500">
            Don&apos;t have an account?{' '}
            <Link
              to="/register"
              className="font-semibold text-indigo-600 hover:text-indigo-500 transition-colors"
            >
              Create one →
            </Link>
          </p>
        </div>
      </AuthCard>
    </AuthLayout>
  );
};

export default LoginPage;
