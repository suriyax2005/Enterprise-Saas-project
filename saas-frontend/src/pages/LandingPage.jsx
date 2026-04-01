import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Layers,
  ShieldCheck,
  Mail,
  Zap,
  CheckCircle2,
  ChevronDown,
  MessageSquare,
  HelpCircle,
  User,
  Globe,
  Search,
  ArrowRight
} from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';
import homeImage from '../assets/home.png';

const LandingPage = () => {
  const { isAuthenticated } = useAuth();
  const [activeFaq, setActiveFaq] = useState(null);
  const [contactForm, setContactForm] = useState({ email: '', message: '' });
  const [sending, setSending] = useState(false);

  const toggleFaq = (index) => setActiveFaq(activeFaq === index ? null : index);

  const handleContactSubmit = (e) => {
    e.preventDefault();
    if (!contactForm.email || !contactForm.message) {
      toast.error('Please enter your email and message.');
      return;
    }
    setSending(true);
    setTimeout(() => {
      toast.success('Thank you! Your message has been sent.');
      setContactForm({ email: '', message: '' });
      setSending(false);
    }, 1500);
  };

  const faqs = [
    {
      q: 'How does multi-tenant data isolation work?',
      a: "Each tenant organization is placed inside a logically isolated space. All database calls dynamically check the user's tenant ID, ensuring that company data is never leaked or shared across boundaries.",
    },
    {
      q: 'Can we change user roles dynamically?',
      a: 'Yes, platform administrators and tenant organization admins can update roles (e.g., ADMIN, USER) dynamically from the members dashboard, which updates credentials instantly.',
    },
    {
      q: 'Is there a limit on audit logs export?',
      a: 'No. The platform allows compliance admins to search, filter, and export the entire audit database to CSV, Excel, or PDF without restrictions.',
    },
  ];

  const inputCls = 'mt-1 block w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-4 focus:ring-indigo-500/10 transition-all';

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-slate-900 font-sans selection:bg-indigo-600 selection:text-white">
      <Toaster position="top-right" />

      {/* ── Single Header Bar ───────────────────────────────── */}
      <header className="sticky top-0 z-50 w-full border-b border-slate-100 bg-white/95 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
          {/* Logo */}
          <div className="flex items-center gap-2.5">
            <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-indigo-600 to-indigo-500 flex items-center justify-center text-white font-bold shadow-sm shadow-indigo-500/25">
              S
            </div>
            <span className="font-extrabold text-slate-900 tracking-tight text-lg">SaaS Engine</span>
          </div>

          {/* Menu Items */}
          <nav className="hidden lg:flex items-center gap-7 text-sm font-semibold text-slate-600">
            <a href="#features" className="hover:text-slate-900 transition-colors">Products and Services</a>
            <Link to="/how-to-buy" className="hover:text-slate-900 transition-colors">How to Buy</Link>
            <Link to="/partners" className="hover:text-slate-900 transition-colors">Partners</Link>
            <a href="#features" className="hover:text-slate-900 transition-colors">Why SaaS Engine</a>
          </nav>

          {/* Right Actions - Single Header contains Log In */}
          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <Link to="/dashboard" className="flex items-center gap-1.5 text-sm font-semibold text-slate-600 hover:text-slate-900 transition-colors">
                <User className="h-4 w-4 text-slate-400" /> Dashboard
              </Link>
            ) : (
              <>
                <Link to="/login" className="text-sm font-semibold text-slate-600 hover:text-slate-900 transition-colors">
                  Log in
                </Link>
                <Link
                  to="/register"
                  className="rounded-full border border-indigo-600 text-indigo-600 hover:bg-indigo-50 px-5 py-1.5 text-xs font-semibold shadow-sm transition-all text-center whitespace-nowrap"
                >
                  Trials and demos
                </Link>
              </>
            )}
            <button className="text-slate-400 hover:text-slate-900 transition-colors p-1.5 cursor-pointer">
              <Search className="h-4.5 w-4.5" />
            </button>
          </div>
        </div>
      </header>

      {/* ── Full-Bleed Dark Split Hero Section ── */}
      <section className="bg-slate-950 text-white w-full min-h-[calc(100vh-64px)] flex flex-col lg:flex-row items-stretch overflow-hidden border-b border-slate-900">
        
        {/* Left Column (First): Full Window Image Panel */}
        <div className="lg:w-1/2 w-full bg-[#000209] flex items-center justify-center p-6 sm:p-12 relative border-r border-slate-900">
          <img
            src={homeImage}
            alt="SaaS Infinite Growth"
            className="w-full h-auto max-h-[75vh] object-contain select-none"
          />
          {/* Subtle status tag inside the image visual */}
          <div className="absolute bottom-6 left-6 right-6 bg-slate-950/75 backdrop-blur-md border border-white/10 rounded-xl p-3.5 flex items-center justify-between text-left">
            <div>
              <p className="text-[11px] font-semibold text-white">SaaS Engine Enterprise Console</p>
              <p className="text-[9px] text-slate-400 mt-0.5">Isolated postgres clustering layer</p>
            </div>
            <span className="inline-flex items-center gap-1.5 rounded-full bg-indigo-500/20 border border-indigo-500/30 px-2.5 py-0.5 text-[9px] font-bold text-indigo-300">
              <span className="h-1 w-1 rounded-full bg-indigo-400 animate-pulse" /> Active
            </span>
          </div>
        </div>

        {/* Right Column (Second): Typography & CTAs next to the image */}
        <div className="lg:w-1/2 w-full flex flex-col justify-center px-6 py-16 sm:px-12 lg:px-16 xl:px-20 space-y-6 bg-slate-950">
          <span className="inline-flex items-center gap-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/20 px-3.5 py-1.5 text-xs font-semibold text-indigo-400 w-fit">
            <Zap className="h-3.5 w-3.5" /> High Performance Multi-Tenant Platform
          </span>
          
          <h1 className="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl leading-[1.15]">
            Scale Up Your Company with Unified Tenant Isolation
          </h1>
          
          <p className="text-slate-400 text-sm sm:text-base leading-relaxed max-w-xl">
            Manage organizations, coordinate workspace directories, audit compliance actions, and control storage securely from one centralized SaaS portal. Engineered with logical postgres security boundaries.
          </p>
          
          <div className="flex flex-col sm:flex-row items-center gap-4 pt-2">
            <Link
              to="/register"
              className="btn-primary w-full sm:w-auto px-7 py-3.5 text-sm"
            >
              Start Free Trial
            </Link>
            <a
              href="#features"
              className="w-full sm:w-auto rounded-xl border border-slate-800 bg-slate-900/50 hover:bg-slate-900 hover:text-white px-6 py-3.5 text-sm font-semibold text-slate-300 shadow-sm transition-all text-center"
            >
              Explore Features
            </a>
          </div>
        </div>

      </section>

      {/* ── Features ────────────────────────────────────────── */}
      <section id="features" className="bg-white border-y border-slate-100 py-20 lg:py-28">
        <div className="mx-auto max-w-7xl px-6">
          <div className="text-center max-w-2xl mx-auto space-y-4">
            <h2 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
              Engineered for Speed, Built for Compliance
            </h2>
            <p className="text-sm text-slate-500">
              Each module has been refined to eliminate blocking operations, secure credentials, and deliver rapid data responses.
            </p>
          </div>

          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-4 mt-16">
            {[
              { icon: ShieldCheck, bg: 'bg-indigo-50', color: 'text-indigo-600', title: 'Tenant Isolation', desc: 'Zero crossover risk. Data filters verify the client request claims at the database query execution layer.' },
              { icon: Zap,         bg: 'bg-emerald-50', color: 'text-emerald-600', title: 'Asynchronous Mails', desc: 'Email verification, invitations, and password resets are dispatched off the request-response thread for sub-100ms load times.' },
              { icon: Layers,      bg: 'bg-violet-50',  color: 'text-violet-600',  title: 'Indexed Repositories', desc: 'PostgreSQL index structures align with searching parameters on users directory lists and logging history tables.' },
              { icon: Mail,        bg: 'bg-amber-50',   color: 'text-amber-600',   title: 'Audits Export', desc: 'Instantly stream compliance tracking logs directly into formatted spreadsheets, CSVs, or professional PDFs.' },
            ].map(({ icon: Icon, bg, color, title, desc }) => (
              <div key={title} className="rounded-xl border border-slate-100 p-6 space-y-4 hover:shadow-md transition-shadow bg-white">
                <div className={`h-10 w-10 ${bg} ${color} rounded-lg flex items-center justify-center`}>
                  <Icon className="h-5 w-5" />
                </div>
                <h3 className="font-bold text-slate-900 text-base">{title}</h3>
                <p className="text-xs text-slate-500 leading-relaxed">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Pricing ─────────────────────────────────────────── */}
      <section className="py-20 lg:py-28 bg-[#F8FAFC]">
        <div className="mx-auto max-w-7xl px-6">
          <div className="text-center max-w-2xl mx-auto space-y-4">
            <h2 className="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
              Transparent, Value-Driven Plans
            </h2>
            <p className="text-sm text-slate-500">
              No hidden fees or custom integration surcharge. Choose a plan that fits your corporate structure.
            </p>
          </div>

          <div className="grid gap-8 md:grid-cols-3 max-w-5xl mx-auto mt-16">
            {/* Startup */}
            <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm flex flex-col justify-between">
              <div className="space-y-4">
                <h4 className="text-sm font-bold text-slate-400 uppercase tracking-widest">Startup</h4>
                <div className="flex items-baseline">
                  <span className="text-4xl font-extrabold text-slate-900">$29</span>
                  <span className="text-slate-400 ml-1 text-xs">/month</span>
                </div>
                <p className="text-xs text-slate-500">Ideal for small organizations looking for isolated workspaces.</p>
                <div className="border-t border-slate-100 pt-4 space-y-2 text-xs text-slate-600">
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Up to 5 Active Users</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Isolated Workspace Tenant</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Standard Email Alerts</div>
                </div>
              </div>
              <Link to="/register" className="mt-8 block w-full rounded-xl border border-slate-200 bg-white py-3 text-center text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors">
                Get Started
              </Link>
            </div>

            {/* Growth — featured */}
            <div className="rounded-2xl border border-indigo-200 bg-white p-8 shadow-md flex flex-col justify-between ring-1 ring-indigo-400/20">
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <h4 className="text-sm font-bold text-indigo-600 uppercase tracking-widest">Growth</h4>
                  <span className="badge-indigo">Popular</span>
                </div>
                <div className="flex items-baseline">
                  <span className="text-4xl font-extrabold text-slate-900">$99</span>
                  <span className="text-slate-400 ml-1 text-xs">/month</span>
                </div>
                <p className="text-xs text-slate-500">Perfect for growing startups and corporate mid-markets.</p>
                <div className="border-t border-slate-100 pt-4 space-y-2 text-xs text-slate-600">
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-indigo-500" /> Up to 50 Active Users</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-indigo-500" /> Standard Audit Compliance</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-indigo-500" /> Invitations Manager</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-indigo-500" /> File uploads to 10 GB</div>
                </div>
              </div>
              <Link to="/register" className="mt-8 block w-full btn-primary py-3 text-xs text-center">
                Start Trial
              </Link>
            </div>

            {/* Enterprise */}
            <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm flex flex-col justify-between">
              <div className="space-y-4">
                <h4 className="text-sm font-bold text-slate-400 uppercase tracking-widest">Enterprise</h4>
                <div className="flex items-baseline">
                  <span className="text-4xl font-extrabold text-slate-900">$299</span>
                  <span className="text-slate-400 ml-1 text-xs">/month</span>
                </div>
                <p className="text-xs text-slate-500">For global compliance teams needing custom support features.</p>
                <div className="border-t border-slate-100 pt-4 space-y-2 text-xs text-slate-600">
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Unlimited Members</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Custom System Logs PDF/CSV</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Priority API Health</div>
                  <div className="flex items-center gap-2"><CheckCircle2 className="h-4 w-4 text-emerald-500" /> Dedicated Account Managers</div>
                </div>
              </div>
              <Link to="/register" className="mt-8 block w-full rounded-xl border border-slate-200 bg-white py-3 text-center text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors">
                Contact Sales
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* ── FAQ ─────────────────────────────────────────────── */}
      <section className="bg-white border-t border-slate-100 py-20 lg:py-28">
        <div className="mx-auto max-w-3xl px-6">
          <div className="text-center space-y-3 mb-12">
            <h2 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl flex items-center justify-center gap-2">
              <HelpCircle className="h-6 w-6 text-indigo-600" /> Frequently Asked Questions
            </h2>
            <p className="text-xs text-slate-500">Get quick answers regarding system features, data limits, and integrations.</p>
          </div>

          <div className="divide-y divide-slate-200">
            {faqs.map((faq, index) => (
              <div key={index} className="py-4">
                <button
                  onClick={() => toggleFaq(index)}
                  className="flex w-full items-center justify-between text-left font-semibold text-slate-900 text-sm focus:outline-none"
                >
                  <span>{faq.q}</span>
                  <ChevronDown
                    className={`h-4 w-4 text-slate-400 transition-transform duration-200 ${
                      activeFaq === index ? 'rotate-180' : ''
                    }`}
                  />
                </button>
                {activeFaq === index && (
                  <p className="mt-2.5 text-xs text-slate-500 leading-relaxed">{faq.a}</p>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Contact ─────────────────────────────────────────── */}
      <section className="py-20 lg:py-28 bg-[#F8FAFC]">
        <div className="mx-auto max-w-lg px-6">
          <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
            <h3 className="text-lg font-bold text-slate-900 text-center mb-1 flex items-center justify-center gap-2">
              <MessageSquare className="h-5 w-5 text-indigo-600" /> Drop us a Line
            </h3>
            <p className="text-xs text-slate-500 text-center mb-6">Need dedicated setup help or custom SLA requirements?</p>

            <form onSubmit={handleContactSubmit} className="space-y-4">
              <div>
                <label className="text-xs font-semibold text-slate-600 tracking-wide">Email Address</label>
                <input
                  type="email" required
                  value={contactForm.email}
                  onChange={(e) => setContactForm({ ...contactForm, email: e.target.value })}
                  placeholder="you@domain.com"
                  className={inputCls}
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-600 tracking-wide">Your Message</label>
                <textarea
                  required rows={4}
                  value={contactForm.message}
                  onChange={(e) => setContactForm({ ...contactForm, message: e.target.value })}
                  placeholder="Describe your inquiry..."
                  className={`${inputCls} resize-none`}
                />
              </div>
              <button
                type="submit" disabled={sending}
                className="w-full btn-primary disabled:opacity-55"
              >
                {sending ? 'Sending message...' : 'Send Message'}
              </button>
            </form>
          </div>
        </div>
      </section>

      {/* ── Footer ──────────────────────────────────────────── */}
      <footer className="border-t border-slate-100 bg-white py-8 text-center text-xs text-slate-400">
        <p>&copy; {new Date().getFullYear()} SaaS Engine Inc. All rights reserved. Managed with multi-tenant Postgres security isolation.</p>
      </footer>
    </div>
  );
};

export default LandingPage;
