import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import toast, { Toaster } from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { Folder, Upload, HardDrive, FileText, Download, Trash2, Eye, Link2 } from 'lucide-react';

const FilesPage = () => {
  const { user } = useAuth();
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [dragActive, setDragActive] = useState(false);

  const storageKey = `saas_files_${user?.userId}`;

  useEffect(() => {
    const stored = localStorage.getItem(storageKey);
    if (stored) {
      try { setFiles(JSON.parse(stored)); }
      catch (e) { localStorage.removeItem(storageKey); }
    }
  }, [storageKey]);

  const saveFiles = (fileList) => {
    setFiles(fileList);
    localStorage.setItem(storageKey, JSON.stringify(fileList));
  };

  const handleUpload = async (fileObj) => {
    const formData = new FormData();
    formData.append('file', fileObj);
    setUploading(true);
    try {
      const res = await api.post('/v1/api/files/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const newFile = {
        id: Date.now().toString(),
        name: fileObj.name,
        fileName: res.data.fileName,
        downloadUrl: res.data.downloadUrl,
        size: fileObj.size,
        type: fileObj.type,
        uploadedAt: new Date().toISOString(),
      };
      saveFiles([newFile, ...files]);
      toast.success(`File "${fileObj.name}" uploaded successfully!`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'File upload failed. Only JPEGs, PNGs, and WebPs are permitted.');
    } finally {
      setUploading(false);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) handleUpload(e.target.files[0]);
  };

  const handleDrag = (e) => {
    e.preventDefault(); e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') setDragActive(true);
    else if (e.type === 'dragleave') setDragActive(false);
  };

  const handleDrop = (e) => {
    e.preventDefault(); e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) handleUpload(e.dataTransfer.files[0]);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to remove this file?')) {
      saveFiles(files.filter((f) => f.id !== id));
      toast.success('File entry removed.');
    }
  };

  const copyDownloadUrl = (downloadUrl) => {
    navigator.clipboard.writeText(`http://localhost:8080${downloadUrl}`);
    toast.success('Download link copied to clipboard!');
  };

  const formatBytes = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const actionBtnCls = 'inline-flex items-center gap-1 rounded-lg border border-slate-200 px-2.5 py-1.5 text-xs font-semibold text-slate-600 hover:bg-slate-50 shadow-sm transition-colors cursor-pointer';

  return (
    <div className="space-y-8">
      <Toaster position="top-right" />

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900">Document Center</h1>
          <p className="text-xs text-slate-500 mt-1">Upload and access organization storage files securely.</p>
        </div>
      </div>

      {/* Stats Widgets */}
      <div className="grid gap-5 sm:grid-cols-3">
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-indigo-50 rounded-lg text-indigo-600">
            <HardDrive className="h-6 w-6" />
          </div>
          <div>
            <p className="text-xs text-slate-400 font-medium">Total Files</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-0.5">{files.length}</h3>
          </div>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-emerald-50 rounded-lg text-emerald-600">
            <Folder className="h-6 w-6" />
          </div>
          <div>
            <p className="text-xs text-slate-400 font-medium">Storage Used</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-0.5">
              {formatBytes(files.reduce((acc, f) => acc + f.size, 0))}
            </h3>
          </div>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-violet-50 rounded-lg text-violet-600">
            <Upload className="h-6 w-6" />
          </div>
          <div>
            <p className="text-xs text-slate-400 font-medium">Upload Limit</p>
            <h3 className="text-2xl font-bold text-slate-900 mt-0.5">100 MB</h3>
          </div>
        </div>
      </div>

      {/* Drag & Drop Area */}
      <div
        onDragEnter={handleDrag} onDragOver={handleDrag}
        onDragLeave={handleDrag} onDrop={handleDrop}
        className={`rounded-2xl border-2 border-dashed p-10 text-center transition-all ${
          dragActive
            ? 'border-indigo-400 bg-indigo-50/20'
            : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50/50'
        }`}
      >
        <div className="flex flex-col items-center justify-center space-y-3">
          <div className="h-12 w-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center shadow-sm">
            <Upload className="h-6 w-6" />
          </div>
          <div className="space-y-1">
            <p className="text-sm font-semibold text-slate-800">
              Drag and drop your file here, or{' '}
              <label className="text-indigo-600 hover:text-indigo-500 cursor-pointer underline underline-offset-2">
                browse
                <input
                  type="file" className="hidden"
                  onChange={handleFileChange}
                  accept="image/png, image/jpeg, image/webp"
                />
              </label>
            </p>
            <p className="text-[10px] text-slate-400">Supported formats: JPEG, PNG, WEBP (Max 10MB)</p>
          </div>
          {uploading && (
            <div className="flex items-center gap-2 text-xs font-semibold text-indigo-600 mt-2">
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />
              Uploading file to storage...
            </div>
          )}
        </div>
      </div>

      {/* Files Table */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="border-b border-slate-200 px-6 py-4">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Uploaded Documents</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="table-header border-b border-slate-100">
                <th className="px-6 py-3">File Name</th>
                <th className="px-6 py-3">Size</th>
                <th className="px-6 py-3">Format</th>
                <th className="px-6 py-3">Uploaded At</th>
                <th className="px-6 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm text-slate-700 bg-white">
              {files.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-slate-400">No documents uploaded.</td>
                </tr>
              ) : (
                files.map((file) => (
                  <tr key={file.id} className="table-row">
                    <td className="px-6 py-4 font-medium text-slate-900 flex items-center gap-2.5">
                      <FileText className="h-4 w-4 text-indigo-500 shrink-0" />
                      <span className="truncate max-w-[200px]" title={file.name}>{file.name}</span>
                    </td>
                    <td className="px-6 py-4 text-slate-500">{formatBytes(file.size)}</td>
                    <td className="px-6 py-4 text-xs text-slate-400 font-mono uppercase">
                      {file.type.split('/')[1] || 'Unknown'}
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-400">
                      {new Date(file.uploadedAt).toLocaleDateString()}{' '}
                      {new Date(file.uploadedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </td>
                    <td className="px-6 py-4 text-right space-x-1.5 whitespace-nowrap">
                      <a
                        href={`http://localhost:8080${file.downloadUrl}`}
                        target="_blank" rel="noreferrer"
                        className={actionBtnCls}
                      >
                        <Eye className="h-3.5 w-3.5" /> Preview
                      </a>
                      <a
                        href={`http://localhost:8080${file.downloadUrl}`}
                        download={file.name}
                        className={actionBtnCls}
                      >
                        <Download className="h-3.5 w-3.5" /> Download
                      </a>
                      <button onClick={() => copyDownloadUrl(file.downloadUrl)} className={actionBtnCls}>
                        <Link2 className="h-3.5 w-3.5" /> Link
                      </button>
                      <button
                        onClick={() => handleDelete(file.id)}
                        className="inline-flex items-center gap-1 rounded-lg bg-red-50 hover:bg-red-100 border border-red-200 px-2.5 py-1.5 text-xs font-semibold text-red-600 shadow-sm transition-colors cursor-pointer"
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default FilesPage;
