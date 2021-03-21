FROM java:8

MAINTAINER youshenghui

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /com.jf.service

WORKDIR /com.jf.service

EXPOSE 9082

ADD ./target/ROOT.jar ./

CMD java $JAVA_OPTS -jar ROOT.jar
