#!/bin/bash

# 익명 게시판 실행 스크립트

echo "🚀 익명 게시판 애플리케이션을 시작합니다..."
echo ""

# Java 17 설정
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "✅ Java 17 설정 완료: $JAVA_HOME"
echo ""

# MySQL 연결 확인 메시지
echo "⚠️  MySQL 데이터베이스 'board'가 생성되어 있는지 확인하세요!"
echo "   생성되지 않았다면 다음 명령어를 실행하세요:"
echo "   mysql -u root -p -e \"CREATE DATABASE board CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
echo ""

# 애플리케이션 실행
echo "🔧 애플리케이션을 시작합니다..."
./gradlew bootRun
