import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Home, User, Users, ShieldAlert, Layers, LogOut, X, FolderOpen } from 'lucide-react';

const Sidebar = ({ isOpen, toggleSidebar }) => {
  const { user, logout } = useAuth();

  const navigation = [
    { name: 'Dashboard',       to: '/dashboard',    icon: Home },
    { name: 'Profile',         to: '/profile',      icon: User },
    { name: 'Organization',    to: '/organization', icon: Layers },
    { name: 'Document Center', to: '/files',        icon: FolderOpen },
  ];

  const adminNavigation = [
    { name: 'User Management',   to: '/admin/users',       icon: Users },
    { name: 'Tenant Management', to: '/admin/tenants',     icon: Layers },
    { name: 'Audit Logs',        to: '/admin/audit-logs',  icon: ShieldAlert },
  ];

  const linkClass = ({ isActive }) =>
    `flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ${
      isActive
        ? 'bg-indigo-50 text-indigo-700 font-semibold'
        : 'text-slate-500 hover:bg-slate-50 hover:text-slate-800'
    }`;

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/10 backdrop-blur-sm lg:hidden"
          onClick={toggleSidebar}
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-slate-200 bg-white transition-transform duration-300 lg:static lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Logo Header */}
        <div className="flex h-16 items-center justify-between px-6 border-b border-slate-100 bg-white">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-gradient-to-br from-indigo-600 to-indigo-500 flex items-center justify-center text-white font-bold shadow-sm shadow-indigo-500/25">
              S
            </div>
            <span className="font-bold text-slate-900 tracking-tight text-base">
              SaaS Engine
            </span>
          </div>
          <button
            className="rounded-lg p-1 text-slate-400 hover:bg-slate-50 hover:text-slate-600 transition-colors lg:hidden cursor-pointer"
            onClick={toggleSidebar}
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Navigation links */}
        <nav className="flex-1 space-y-1 px-4 py-6 overflow-y-auto bg-white">
          <div className="space-y-0.5">
            {navigation.map((item) => (
              <NavLink
                key={item.name}
                to={item.to}
                className={linkClass}
                onClick={() => lgHiddenToggle(toggleSidebar)}
              >
                <item.icon className="h-4 w-4 shrink-0" />
                {item.name}
              </NavLink>
            ))}
          </div>

          {user?.role === 'ADMIN' && (
            <div className="pt-6">
              <p className="px-4 text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                Administration
              </p>
              <div className="space-y-0.5">
                {adminNavigation.map((item) => (
                  <NavLink
                    key={item.name}
                    to={item.to}
                    className={linkClass}
                    onClick={() => lgHiddenToggle(toggleSidebar)}
                  >
                    <item.icon className="h-4 w-4 shrink-0" />
                    {item.name}
                  </NavLink>
                ))}
              </div>
            </div>
          )}
        </nav>

        {/* Footer */}
        <div className="border-t border-slate-100 p-4 bg-white">
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-lg px-4 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 transition-colors cursor-pointer"
          >
            <LogOut className="h-4 w-4 shrink-0" />
            Sign Out
          </button>
        </div>
      </aside>
    </>
  );
};

const lgHiddenToggle = (toggleFn) => {
  if (window.innerWidth < 1024) toggleFn();
};

export default Sidebar;
