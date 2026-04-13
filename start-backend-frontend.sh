#!/usr/bin/env bash
set -euo pipefail

# Start both backend and frontend, and stop both on Ctrl+C.

command -v java >/dev/null || {
  echo "Java not found. Install Java 17+ and retry."
  exit 1
}
command -v mvn >/dev/null || {
  echo "Maven not found."
  echo "Install it with: brew install maven"
  exit 1
}
command -v python3 >/dev/null || {
  echo "Python3 not found. Install Python 3 and retry."
  exit 1
}

cleanup() {
  echo
  echo "Stopping services..."
  if [[ -n "${BACKEND_PID:-}" ]]; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi
  if [[ -n "${FRONTEND_PID:-}" ]]; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
  wait 2>/dev/null || true
  echo "Stopped."
}

trap cleanup INT TERM EXIT

echo "Starting backend on http://localhost:8080 ..."
mvn spring-boot:run > /tmp/dpta-backend.log 2>&1 &
BACKEND_PID=$!

echo "Starting frontend on http://localhost:5500 ..."
python3 -m http.server 5500 --directory web-order-ui > /tmp/dpta-frontend.log 2>&1 &
FRONTEND_PID=$!

echo "Backend PID:  $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo
echo "Open:"
echo "  Frontend: http://localhost:5500"
echo "  Backend:  http://localhost:8080/api/menu"
echo
echo "Logs:"
echo "  /tmp/dpta-backend.log"
echo "  /tmp/dpta-frontend.log"
echo
echo "Press Ctrl+C to stop both services."

wait
