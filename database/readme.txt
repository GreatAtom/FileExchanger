Как настроить БД.
1.Находим папку %JAVA_HOME#\db
2.Копируем её туда, где можно создавать файлы и папки без прав администратора (например D:\db)
3. Можно переименовать (тогда будет путь  D:\derby)
4. копируем папку fileexchanger в D:\derby\bin
5. В командной строке:
    set DERBY_HOME=D:\derby
    set PATH=%DERBY_HOME%\bin;%PATH%
    set "DERBY_OPTS=-Duser.language=en -Dderby.drda.debug=true"
6. запускаем %DERBY_HOME%\bin startNetworkServer.bat
7. Убеждаемся, что в консоли оботрализась инфа о БД. если не отобразилась, то что-то не работает.

сейчас в базе 2 пользователя
dmitry dmitry
qwerty qwerty