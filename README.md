# This is the example shop of InfinityShopping platform

[InfinityShopping home page](https://www.infinityshopping.online).

[InfinityShopping on GitHub](https://www.github.com/PiotrZielonka/infinityshopping/tree/develop).

[Website of example shop of infinityshopping](https://www.infinityshopping.online/example-shop).

[YouTube](https://www.youtube.com/watch?v=YYEodtIGeZQ)

## Development

> Make sure you are on the develop branch.

You have to install in your local computer

- Java 11 AdoptOpenJDK
- Maven version 3.8.1
- Node version 14.17.6 LTS version Fermium [link here](https://nodejs.org/en/download/releases/)
- npm version 7.24.2
```
npm install -g npm@7.24.2
```
- The above are in the file pom.xml properties tag
- Angular CLI version 12.2.9
```
npm install -g @angular/cli@12.2.9
```
- You can find Angular version in the file package.json
- Jhipster version 7.3.0
```
npm install -g generator-jhipster@7.3.0
```
- Eclipse and Spring Tool Suite are not supported maybe it works but it was not tested. In Java / Spring environment use IntelliJ IDEA Community Edition and Visual Studio Code for Angular frontend environment
- In IntelliJ IDEA additionally install plugin MapStruct support for IntelliJ IDEA [plugin MapStruct homepage](https://plugins.jetbrains.com/plugin/10036-mapstruct-support)
- You can make sure if Building project automatically in Intellij is checked
- When Intellij will be ready, all the project will be analyzed and indexed by Intellij. In the project directory, run the command below
```
mvnw
```
- That's all you should run the application on your local computer. Infinityshopping is the Jhipster app if you need more details look at [Jhipster home page](https://www.jhipster.tech)

- When build will be successful and you use ctrl+c in the terminal. You should be able to run the application in IntelliJ (Java/Spring) by running the main method in class InfinityshoppingApp and in the project directory, the command below should run the Angular environment.
```
npm start
```

## Addons to Infinityshopping
> After making the steps above you can add addons to this shop from [Jhipster marketplace](https://www.jhipster.tech/modules/marketplace/#/list) or change something in yo-rc.json file as in an existing Jhipster project.

## Questions and Contributing

If you have any questions or you want to join infinityshopping and develop it in open source software spirit you find the contact on [www.infinityshopping.online](https://www.infinityshopping.online)