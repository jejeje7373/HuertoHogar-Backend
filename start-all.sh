#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MVN_CMD="${MVN_CMD:-$(command -v mvn || echo /snap/intellij-idea/154/plugins/maven-plugin/lib/maven3/bin/mvn)}"

mkdir -p "$DIR/logs"

SERVICES=("ms-usuarios:8081" "ms-categorias:8082" "ms-productos:8083" "ms-ventas:8084" "ms-reportes:8085")

echo "=== Iniciando los 5 Microservicios de HuertoHogar ==="
for entry in "${SERVICES[@]}"; do
  svc="${entry%%:*}"
  port="${entry##*:}"
  echo "-> Iniciando $svc en el puerto $port..."
  "$MVN_CMD" -f "$DIR/$svc/pom.xml" spring-boot:run > "$DIR/logs/$svc.log" 2>&1 &
  echo $! > "$DIR/logs/$svc.pid"
done

echo "Esperando a que los microservicios estén activos (8081-8085)..."
for entry in "${SERVICES[@]}"; do
  svc="${entry%%:*}"
  port="${entry##*:}"
  for i in {1..60}; do
    if ss -tln | grep -q ":$port "; then
      echo "✓ $svc activo en http://localhost:$port"
      break
    fi
    sleep 1
  done
done

echo "=== Todos los microservicios están activos (8081-8085) ==="
wait
