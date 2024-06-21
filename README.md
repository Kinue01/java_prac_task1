# Установка XServer
Для запуска приложения в Docker необходимо усановить XServer, который позволяет транслировать оконные приложения с одной ОС на экран другой ОС.
## Windows
Скачайте одну из имплементаций XServer - [VcXsrv](https://sourceforge.net/projects/vcxsrv/).

При установке выберите полный тип установки.

После установки и запуска необходимо: Выбрать Multiple windows -> Выбрать Start no client -> Поставить галочку напротив Disable access control -> Нажать Готово.
## Linux (Ubuntu, Mint)
Выполните следующие команды:
```
sudo apt update
sudo apt install xorg openbox
```
# Запуск приложения
## Docker Compose
### Windows
Перед запуском предварительно должен быть запущен XServer.

Для запуска приложения необходимо в корне проекта в терминале запустить команду: `docker compose up`.
### Linux
Для запуска приложения необходимо в корне проекта в терминале запустить команду: `sudo docker compose up`
## Docker
### Windows
Для запуска приложения необходимо в корне проекта в терминале запустить следующие команды: 
```
docker build -t <тег образа> -f DockerfileAlone .
docker run -e DISPLAY=host.docker.internal:0.0 <тег созданного образа>
```
### Linux
Для запуска приложения необходимо в корне проекта в терминале запустить следующие команды:
```
export DISPLAY=:0
xhost +si:localuser:root
xhost +
docker build -t <тег образа> -f DockerfileAlone .
docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix <тег созданного образа>
```
# Отладка приложения
При необходимости, для полного удаления созданных контейнеров, образов и хранилищ, выполните команду
## Windows
`docker compose down --rmi all --volumes`
## Linux
`sudo docker compose down --rmi all --volumes`