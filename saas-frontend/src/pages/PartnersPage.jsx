import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Search, User, Users, CheckCircle2 } from 'lucide-react';

const PartnersPage = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-slate-900 font-sans">
      {/* ── Single Header Bar ───────────────────────────────── */}
      <header className="sticky top-0 z-50 w-full border-b border-slate-100 bg-white/95 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-indigo-600 to-indigo-500 flex items-center justify-center text-white font-bold shadow-sm shadow-indigo-500/25">
              S
            </div>
            <span className="font-extrabold text-slate-900 tracking-tight text-lg">SaaS Engine</span>
          </Link>

          <nav className="hidden lg:flex items-center gap-7 text-sm font-semibold text-slate-600">
            <Link to="/" className="hover:text-slate-900 transition-colors">Home</Link>
            <Link to="/how-to-buy" className="hover:text-slate-900 transition-colors">How to Buy</Link>
            <Link to="/partners" className="text-indigo-600 transition-colors">Partners</Link>
          </nav>

          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <Link to="/dashboard" className="flex items-center gap-1.5 text-sm font-semibold text-slate-600 hover:text-slate-900 transition-colors">
                <User className="h-4 w-4" /> Dashboard
              </Link>
            ) : (
              <>
                <Link to="/login" className="text-sm font-semibold text-slate-600 hover:text-slate-900 transition-colors">
                  Log in
                </Link>
                <Link to="/register" className="btn-primary text-xs">
                  Start Free Trial
                </Link>
              </>
            )}
            <button className="text-slate-400 hover:text-slate-900 transition-colors p-1.5 cursor-pointer">
              <Search className="h-4.5 w-4.5" />
            </button>
          </div>
        </div>
      </header>

      {/* ── Page Content ────────────────────────────────────── */}
      <main className="mx-auto max-w-4xl px-6 py-20">
        <div className="text-center space-y-4 mb-16">
          <span className="inline-flex items-center gap-1.5 rounded-full bg-indigo-50 border border-indigo-100 px-3.5 py-1.5 text-xs font-semibold text-indigo-700">
            <Users className="h-3.5 w-3.5" /> Partner Program
          </span>
          <h1 className="text-4xl font-extrabold tracking-tight text-slate-900 sm:text-5xl">
            SaaS Engine Partner Ecosystem
          </h1>
          <p className="text-base text-slate-500 max-w-xl mx-auto leading-relaxed">
            Collaborate with us to deploy isolated multi-tenant workspaces, audit controls, and modern compliance tools for organizations worldwide.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-2">
          <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm space-y-6">
            <h3 className="text-xl font-bold text-slate-900">Integration Partners</h3>
            <p className="text-sm text-slate-500 leading-relaxed">
              Design, build, and deploy multi-tenant apps on the SaaS Engine database architecture. Help your clients isolate user workspaces.
            </p>
            <ul className="space-y-2.5 text-xs text-slate-600">
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Exclusive API access &amp; tools</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Joint marketing &amp; listing directory</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Direct Slack developer channel</li>
            </ul>
            <a href="mailto:partners@saas-engine.com" className="block w-full btn-primary py-3 text-center text-xs">
              Apply as Integrator
            </a>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm space-y-6">
            <h3 className="text-xl font-bold text-slate-900">Technology Affiliates</h3>
            <p className="text-sm text-slate-500 leading-relaxed">
              Refer customers to SaaS Engine and receive recurring commissions on database workspace volumes.
            </p>
            <ul className="space-y-2.5 text-xs text-slate-600">
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> 20% recurring affiliate commissions</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Marketing assets and banner graphics</li>
              <li className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Dynamic link tracking console</li>
            </ul>
            <a href="mailto:partners@saas-engine.com" className="block w-full text-center rounded-xl border border-slate-200 bg-white py-3 text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors shadow-sm">
              Contact Affiliate Team
            </a>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-100 bg-white py-8 text-center text-xs text-slate-400">
        <p>&copy; {new Date().getFullYear()} SaaS Engine Inc. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default PartnersPage;
