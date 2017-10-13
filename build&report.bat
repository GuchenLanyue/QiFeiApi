@echo off
call mvn clean test -e
call allure serve ./allure-results