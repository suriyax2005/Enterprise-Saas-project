import React from 'react';

/**
 * AuthCard — white card container for all auth form content.
 * max-w-[500px], 24px border-radius, 40px padding (desktop), soft shadow + border.
 */
const AuthCard = ({ children, className = '' }) => {
  return (
    <div
      className={`w-full max-w-[500px] rounded-3xl bg-white border border-slate-100 shadow-xl shadow-slate-200/60 p-5 sm:p-10 ${className}`}
    >
      <div className="space-y-6">{children}</div>
    </div>
  );
};

export default AuthCard;