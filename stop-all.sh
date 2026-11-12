#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Deteniendo Microservicios de HuertoHogar ==="
for port in 8081 8082 8083 8084 8085; do
  pids=$(lsof -t -i:"$port" 2>/dev/null || true)
  if [ -n "$pids" ]; then
    echo "-> Cerrando proceso en puerto $port (PID: $pids)"
    kill -9 $pids 2>/dev/null || true
  fi
done
rm -f "$DIR/logs/"*.pid 2>/dev/null || true
echo "=== Microservicios detenidos ==="
