#!/bin/sh
cat <<EOF > /usr/share/nginx/html/env.js
window.ENV = {
  VITE_BACKEND_URL: "${VITE_BACKEND_URL}",
  VITE_FRONTEND_URL: "${VITE_FRONTEND_URL}"
};
EOF
nginx -g 'daemon off;'
