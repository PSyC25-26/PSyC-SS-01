# 1. Matar procesos previos de Java
Get-Process java,javaw -ErrorAction SilentlyContinue | Stop-Process -Force

# 2. Limpiar contenedores de Docker
docker compose down -v --remove-orphans

# 3. Configurar variables de entorno
$env:APP_UI_ENABLED="true"
$env:JAVA_TOOL_OPTIONS="-Djava.awt.headless=false"

# 4. Arrancar el proyecto
.\mvnw.cmd spring-boot:run -e