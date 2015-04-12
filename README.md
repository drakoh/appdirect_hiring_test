# appdirect_hiring_test
This app is a challenge for potential candidates at AppDirect.

It runs on top of spring boot, use openid with appdirect as openid provider, bootstrap for a cheap and efficient UI

Can be deployed with maven using: `mvn clean heroku:deploy`

There is a running instance on heroku here : http://appdirect-hiring-test.herokuapp.com/

To run a  local instance, use : `mvn spring-boot:run`, the app will be bound to port 8080. (You will have to modify `application.properties` to change the db settings)

Db Schema is :
<pre>
    CREATE TABLE "user" (
        email text,
        first_name text,
        last_name text,
        uuid text,
        open_id text NOT NULL,
        account_identifier text,
        language text
    );

    ALTER TABLE ONLY "user"
        ADD CONSTRAINT open_id_pk PRIMARY KEY (open_id);
</pre>

So far code coverage is about 85+% (based on lines)