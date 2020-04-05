FROM hiraev/alpine_gradle_ffmpeg
MAINTAINER Malik Khiraev

ENV VERSION 1.0
ENV APP_NAME hls-server-$VERSION

RUN mkdir -p /$APP_NAME/src

COPY . /$APP_NAME/src
WORKDIR /$APP_NAME/src

RUN gradle hls-server:jar

ENTRYPOINT ["java", "-jar", "hls-server/build/libs/hls-server-1.0.jar", "-r", "site"]
CMD []
