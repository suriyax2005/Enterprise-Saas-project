import React from 'react';
import { Link } from 'react-router-dom';
import { FileQuestion, Home } from 'lucide-react';

const NotFoundPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-[#F8FAFC] px-4 text-center font-sans">
      <div className="flex h-20 w-20 items-center justify-center rounded-full bg-slate-100 border border-slate-200 text-slate-400">
        <FileQuestion className="h-10 w-10" />
      </div>
      <h1 className="mt-6 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
        Page Not Found
      </h1>
      <p className="mt-2 text-sm text-slate-500 max-w-md">
        The page you are looking for does not exist or has been moved.
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

export default NotFoundPage;
