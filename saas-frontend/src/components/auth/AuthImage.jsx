import React from 'react';
import loginImage from '../../assets/login.png';
import registerImage from '../../assets/register.png';

/**
 * AuthImage — full-bleed image panel that fades between login and register images.
 * Cross-fade duration: 600ms via CSS transition.
 */
const AuthImage = ({ type }) => {
  const isRegister = type === 'register';

  return (
    <div className="relative w-full h-full overflow-hidden bg-indigo-950">
      {/* Login image */}
      <img
        src={loginImage}
        alt="Sign in background"
        className="absolute inset-0 w-full h-full object-cover"
        style={{
          opacity: isRegister ? 0 : 1,
          transition: 'opacity 600ms cubic-bezier(0.4, 0, 0.2, 1)',
        }}
      />

      {/* Register image */}
      <img
        src={registerImage}
        alt="Register background"
        className="absolute inset-0 w-full h-full object-cover"
        style={{
          opacity: isRegister ? 1 : 0,
          transition: 'opacity 600ms cubic-bezier(0.4, 0, 0.2, 1)',
        }}
      />

      {/* Dark gradient overlay for depth + text legibility */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-900/50 via-indigo-950/30 to-transparent pointer-events-none" />

      {/* Bottom text overlay */}
      <div className="absolute bottom-0 left-0 right-0 p-8 pointer-events-none">
        {/* Indicator dots */}
        <div className="flex items-center gap-1.5 mb-5">
          <div
            className="h-1 rounded-full bg-white"
            style={{
              width: isRegister ? '12px' : '28px',
              opacity: isRegister ? 0.4 : 0.85,
              transition: 'all 600ms cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          />
          <div
            className="h-1 rounded-full bg-white"
            style={{
              width: isRegister ? '28px' : '12px',
              opacity: isRegister ? 0.85 : 0.4,
              transition: 'all 600ms cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          />
          <div className="h-1 w-3 rounded-full bg-white/30" />
        </div>

        <p
          className="text-white/95 text-lg font-semibold leading-snug max-w-xs"
          style={{
            transition: 'opacity 600ms cubic-bezier(0.4, 0, 0.2, 1)',
          }}
        >
          {isRegister
            ? 'Join thousands of teams who trust our platform.'
            : 'Secure access to your workspace, every time.'}
        </p>
        <p className="text-white/55 text-sm mt-2 font-medium">
          Enterprise-grade security. Zero compromise.
        </p>
      </div>
    </div>
  );
};

export default AuthImage;