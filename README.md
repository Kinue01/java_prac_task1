# Установка XServer
Для запуска приложения в Docker необходимо усановить XServer, который позволяет транслировать оконные приложения с одной ОС на экран другой ОС
## Windows
Скачайте одну из имплементаций XServer - [VcXsrv](https://sourceforge.net/projects/vcxsrv/).
При установке выберите полный тип установки.
После установки и запуска необходимо: Выбрать Multiple windows -> Выбрать Start no client -> Поставить галочку напротив Disable access control -> Нажать Готово.
## Linux (Debian, Ubuntu, Mint)
Выполните следующие команды:
```
sudo apt update
sudo apt install xorg openbox
xhost +local:docker
xhost si:localuser:root
```
# Запуск приложения
## Windows
Для запуска приложения в корне проекта в терминале необходимо запустить команду: `docker-compose up`
## Linux
Для запуска приложения в корне проекта в терминале необходимо запустить команду: `sudo docker compose run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix swing`
