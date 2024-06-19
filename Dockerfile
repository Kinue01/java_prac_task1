FROM maven as build
COPY . /app/
WORKDIR /app/build
RUN mvn clean package

FROM openjdk:8
RUN apt-get update && apt-get install -y x11-apps libxext6 libxrender1 libxtst6 && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/startApp/target /app/target
ENTRYPOINT java -jar /app/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar