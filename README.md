# Особенности установки Docker на Linux
Для запуска необходимо установить плагин compose: `sudo apt install docker-compose-plugin`.

Желательно производить установку Docker согласно гайду с [этой](https://docs.docker.com/engine/install/) страницы в соответствии с вашем дистрибутивом
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
xhost +local:docker
xhost si:localuser:root
```
# Запуск приложения
## Windows
Перед запуском предварительно должен быть запущен XServer.

Для запуска приложения необходимо в корне проекта в терминале запустить команду: `docker compose up`.
## Linux
Для запуска приложения необходимо в корне проекта в терминале запустить команду: `sudo docker compose up`

# Отладка приложения
При необходимости, для полного удаления созданных контейнеров, образов и хранилищ, выполните команду
## Windows
`docker compose down --rmi all --volumes`
## Linux
`sudo docker compose down --rmi all --volumes`