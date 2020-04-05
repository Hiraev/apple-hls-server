Apple HLS Server

Пример запуска сервера
```bash
docker build -t khiraev/hls .
docker run -it --name hls -p 80:80 --rm hiraev/hls -b 0:0 -p 80
```

* -p задает порт, на котором будет запущен сервер
* -b задает битрейт генерируемых m3u8 файлов (на данный момент не исользуется)
