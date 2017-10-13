@echo off
call mvn clean test -e
call allure serve target/allure-results