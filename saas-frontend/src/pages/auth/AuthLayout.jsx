import React, { useState, useEffect } from 'react';
import AuthImage from '../../components/auth/AuthImage';

/**
 * AuthLayout — master split-screen auth shell.
 *
 * Container: 95vw × 90vh, max-w-1600, centered, rounded-3xl, white, large shadow.
 * Left panel : image (50% lg / 40% md / hidden mobile)  — fades between login & register images.
 * Right panel: form   (50% lg / 60% md / 100% mobile)  — slides in on route change.
 *
 * Animation:
 *   • Image cross-fade — 600ms CSS transition driven by `imageType` state (50ms delayed).
 *   • Form  slide-in   — CSS @keyframes triggered by React `key={type}` remount.
 *     Login  → slides in from the right  (.auth-slide-in-right)
 *     Register → slides in from the left (.auth-slide-in-left)
 */
const AuthLayout = ({ type, children }) => {
  // Delay image swap slightly so the form animation leads, image follows.
  const [imageType, setImageType] = useState(type);

  useEffect(() => {
    const id = setTimeout(() => setImageType(type), 80);
    return () => clearTimeout(id);
  }, [type]);

  const isRegister = type === 'register';

  return (
    <div className="min-h-screen w-full bg-[#F8FAFC] flex items-center justify-center py-6 px-3 md:px-6 font-sans">
      {/* ── Main Auth Container ──────────────────────────────── */}
      <div className="w-[95%] max-w-[1600px] md:h-[90vh] bg-white rounded-3xl shadow-2xl shadow-slate-300/40 overflow-hidden flex flex-col md:flex-row">

        {/* ── Left: Image Panel (hidden on mobile) ─────────── */}
        <div className="hidden md:block md:w-[40%] lg:w-1/2 flex-shrink-0 relative">
          <AuthImage type={imageType} />
        </div>

        {/* ── Right: Form Panel ────────────────────────────── */}
        <div className="flex-1 flex items-start md:items-center justify-center p-5 sm:p-8 md:p-10 overflow-y-auto">
          {/*
            key={type} forces a DOM remount when the route switches,
            which re-triggers the CSS animation from scratch.
          */}
          <div
            key={type}
            className={`w-full flex items-center justify-center ${
              isRegister ? 'auth-slide-in-left' : 'auth-slide-in-right'
            }`}
          >
            {children}
          </div>
        </div>

      </div>
    </div>
  );
};

export default AuthLayout;