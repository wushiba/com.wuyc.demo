FROM java:8

MAINTAINER youshenghui

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /com.jf.front.service

WORKDIR /com.jf.front.service

EXPOSE 9081

ADD ./target/ROOT.jar ./

CMD java $JAVA_OPTS -jar ROOT.jar
