FROM maven
ADD . /app/
WORKDIR /app/build
RUN mvn clean package
ENTRYPOINT java -jar /app/startApp/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar