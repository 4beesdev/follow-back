docker-compose stop
docker-compose rm
git pull
mvn clean
mvn package -Dmaven.test.skip=true
docker-compose build
docker-compose up