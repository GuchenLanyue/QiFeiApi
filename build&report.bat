@echo off
echo ������Ա�������ļ�
call allure generate -c
echo ����ɹ�
call mvn clean test -e
call allure serve ./allure-results