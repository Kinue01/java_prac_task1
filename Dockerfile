FROM maven as build
COPY . /app/
WORKDIR /app/build
RUN mvn clean package

FROM openjdk:8
RUN apt-get update && apt-get install -y x11-apps libxext6 libxrender1 libxtst6 && apt-get clean && rm -rf /var/lib/apt/lists/*
ENV DISPLAY=host.docker.internal:0
COPY --from=build /app/app/target /app/target
ENTRYPOINT java -jar /app/target/app-1.0-SNAPSHOT-jar-with-dependencies.jar