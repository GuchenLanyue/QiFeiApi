@echo off
echo ������Ա�������ļ�
rd /s /Q allure-report
rd /s /Q allure-results
rd /s /Q test-output
call allure generate -c
echo ����ɹ�
call mvn clean test -e
call allure serve ./allure-results