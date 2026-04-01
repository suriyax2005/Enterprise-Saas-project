import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldX, Home } from 'lucide-react';

const ForbiddenPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-[#F8FAFC] px-4 text-center font-sans">
      <div className="flex h-20 w-20 items-center justify-center rounded-full bg-red-50 border border-red-100 text-red-500">
        <ShieldX className="h-10 w-10" />
      </div>
      <h1 className="mt-6 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
        Access Forbidden
      </h1>
      <p className="mt-2 text-sm text-slate-500 max-w-md">
        You do not have the required administrative scopes to access this tool.
      </p>
      <Link
        to="/dashboard"
        className="btn-primary mt-8"
      >
        <Home className="h-4 w-4" />
        Return Dashboard
      </Link>
    </div>
  );
};

export default ForbiddenPage;
