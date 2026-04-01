import React from 'react';

const AuthButton = ({
  loading = false,
  text,
  loadingText = 'Please wait...',
  disabled = false,
  type = 'submit',
  onClick,
}) => {
  return (
    <button
      type={type}
      disabled={loading || disabled}
      onClick={onClick}
      className="auth-btn-gradient flex w-full items-center justify-center gap-2.5 rounded-xl py-3 text-[15px] font-semibold text-white shadow-md shadow-indigo-500/20 focus:outline-none focus:ring-4 focus:ring-indigo-500/20 disabled:opacity-60 disabled:cursor-not-allowed disabled:!transform-none disabled:!shadow-none cursor-pointer"
    >
      {loading ? (
        <>
          <svg
            className="animate-spin h-4 w-4 text-white shrink-0"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          {loadingText}
        </>
      ) : (
        text
      )}
    </button>
  );
};

export default AuthButton;