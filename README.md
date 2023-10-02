# Captcha Solver

## Author

* Simone Maliziola
* GitHub: https://github.com/Sim0ne42

## First steps

```
git clone https://github.com/Sim0ne42/captcha-solver
cd captcha-solver
```

> **_NOTE:_**  this application needs a running PostgreSQL instance. Once started, update your local
> configuration [here](src/main/resources/application.yaml) and [here](src/main/resources/python/captcha.py).

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/captcha-solver-runner`.

## How to use it

Create a virtual environment and install the required libraries
in [requirements.txt](src/main/resources/python/requirements.txt):

```shell script
cd src/main/resources/python   
pip install -r requirements.txt
```

This project uses [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework.

You can run the application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

> **_NOTE:_**  Quarkus ships with a [Dev UI](http://localhost:8080/q/dev/), which is available in dev mode only.

This application exposes the following endpoints:

* `POST /captcha/add`: add a new captcha image
* `GET /captcha/random`: get a random captcha image
* `POST /captcha/verify`: verify that the entered text is correct

Once the application is started, use the tests present
in [CaptchaServiceIT](src/test/java/org/captcha/solver/service/CaptchaServiceIT.java)
in order to add the samples to your local DB and predict their value.

At this point, you can use the [index page](http://localhost:63342/captcha-solver/target/classes/index.html)
to get a captcha image and manually enter some text.

You can use [SeleniumIT](src/test/java/org/captcha/solver/SeleniumIT.java) to solve the captcha automatically,
simulating human behavior.

## Notes

* Python is required
* Java version >= 11 is required
* [Maven](https://maven.apache.org/) is required
* If you started the application in dev mode, you can use [Swagger UI](http://localhost:8080/q/swagger-ui/)
* You can also interact with the application using [postman collection](postman_collection.json)
  and [postman environment](postman_environment.json)
* You can check the health of the application (liveness and readiness)
  from [Health UI](http://localhost:8080/q/health-ui/).
  In particular, you can see how accurate the model is in predicting captchas in the DB.
  If the accuracy is above a certain threshold, the status is UP, otherwise it is DOWN
* Logging level is DEBUG. You can change the level in [application.yaml](src/main/resources/application.yaml)
* In the `src/main/resources/python/notebook` directory, there is the notebook in `ipynb` and `py` formats.
  Starting from this notebook and using, for example, [Google Colab](https://colab.research.google.com/),
  you can create a new model

## Disclaimer

This project is provided for demonstration and educational purposes only, and should not be used for illegal
activities. The author is not responsible for any improper use and users are expected to comply with all laws and
regulations.

Automatic CAPTCHA resolution may harm websites and users are advised to respect website policies and use this
project ethically and legally.

The author of this project does not guarantee the ability to resolve all types of CAPTCHA or specific results,
as resolution depends on various factors, including CAPTCHA complexity and the specific implementation of the website.

By using this project, users agree to this disclaimer and the author retains the right to change it at any time
without notice.
