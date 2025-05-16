@echo off
echo Iniciando todos os microservicos...

start "User Service" cmd /c "cd user_service && mvn spring-boot:run"
start "Media Service" cmd /c "cd media_service && mvn spring-boot:run"
start "Pet Service" cmd /c "cd pet_service && mvn spring-boot:run"
start "API Gateway" cmd /c "cd api_gateway && mvn spring-boot:run"

echo Comandos enviados para iniciar os servicos.

@REM .\start_all.bat