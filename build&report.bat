@echo off
echo ������Ա�������ļ�
rd /s /Q test-output
rd /s /Q ./sources/temp
call allure generate -c
echo ����ɹ�
call mvn clean test -e
call allure serve ./allure-results