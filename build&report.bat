@echo off
echo 清除测试报告残留文件
rd /s /Q test-output
call allure generate -c
echo 清除成功
call mvn clean test -e
call allure serve ./allure-results