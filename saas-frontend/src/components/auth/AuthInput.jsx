import React, { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';

const AuthInput = ({
  icon,
  label,
  type = 'text',
  value,
  onChange,
  placeholder,
  disabled = false,
  required = false,
  error,
  id,
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const isPassword = type === 'password';
  const resolvedType = isPassword ? (showPassword ? 'text' : 'password') : type;

  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={id}
          className="block text-xs font-semibold text-slate-600 mb-1.5 tracking-wide"
        >
          {label}
        </label>
      )}

      <div className="relative group">
        {/* Left icon */}
        {icon && (
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400 transition-colors group-focus-within:text-indigo-500">
            {icon}
          </div>
        )}

        <input
          id={id}
          type={resolvedType}
          value={value}
          disabled={disabled}
          required={required}
          placeholder={placeholder}
          onChange={onChange}
          className={[
            'block w-full rounded-xl border bg-white py-3 text-sm text-slate-900',
            'placeholder-slate-400 shadow-sm transition-all duration-200',
            'focus:outline-none focus:ring-4',
            icon ? 'pl-10' : 'pl-4',
            isPassword ? 'pr-11' : 'pr-4',
            error
              ? 'border-red-300 focus:border-red-400 focus:ring-red-500/10'
              : 'border-slate-200 hover:border-slate-300 focus:border-indigo-500 focus:ring-indigo-500/10',
            disabled
              ? 'bg-slate-50 text-slate-400 cursor-not-allowed border-slate-200 hover:border-slate-200'
              : '',
          ]
            .filter(Boolean)
            .join(' ')}
        />

        {/* Password visibility toggle */}
        {isPassword && (
          <button
            type="button"
            tabIndex={-1}
            aria-label={showPassword ? 'Hide password' : 'Show password'}
            className="absolute inset-y-0 right-0 flex items-center pr-3.5 text-slate-400 hover:text-slate-600 transition-colors focus:outline-none"
            onClick={() => setShowPassword((v) => !v)}
          >
            {showPassword ? (
              <EyeOff className="h-4 w-4" />
            ) : (
              <Eye className="h-4 w-4" />
            )}
          </button>
        )}
      </div>

      {error && (
        <p className="mt-1.5 text-xs font-medium text-red-500">{error}</p>
      )}
    </div>
  );
};

export default AuthInput;