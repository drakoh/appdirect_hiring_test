# appdirect_hiring_test
This app is a challenge for potential candidates at AppDirect.

It runs on top of spring boot, use openid with appdirect as openid provider, bootstrap for a cheap and efficient UI

Can be deployed with maven using: `mvn clean heroku:deploy`

There is a running instance on heroku here : http://appdirect-hiring-test.herokuapp.com/

To run a  local instance, use : `mvn spring-boot:run`, the app will be bound to port 8080.

So far code coverage is about 85+% (based on lines)