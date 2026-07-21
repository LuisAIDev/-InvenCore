import { useState } from 'react';

export default function ProductThumb({ url, className }) {
  const [failed, setFailed] = useState(false);
  if (!url || failed) {
    return (
      <div className={`${className || 'w-10 h-10'} rounded-lg overflow-hidden bg-gray-100 flex items-center justify-center`}>
        <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
      </div>
    );
  }
  return (
    <div className={`${className || 'w-10 h-10'} rounded-lg overflow-hidden bg-gray-100`}>
      <img src={url} alt="" className="w-full h-full object-cover" onError={() => setFailed(true)} />
    </div>
  );
}
