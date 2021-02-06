#heroku build and deploy : 
mvn clean install spring-boot:repackage && heroku deploy:jar target/StockTrackingService-0.1.jar --app=dkstock 
#heroku buildpacks:clear --app=dkstock