# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

## Sequence Diagram Link for Phase 2
[https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqsyb0YKAE9VuImgDmMAAwA6ABybMHOcABGwVShgdHm41AgBXbDAAxGjAVLowAEooxkiqOnJIEGgAXOS+ymqqQQDuABZIYGKIqKQAtAB85JQ0UKkA2gAKAPJkACoAujAA9L6uUAA6aADeAES9lCEAtigjySMwIwA0C7jq2dAcs-NLCyiTwEgIWwsAvpjCNTAVrOxclKmj41BTM3MLyyOrqutQm287Iz2ByO-zObE43Fg1wuolSUGisUKUAAFFEYnFKFEAI6+NRgACU52qoiulVk8iUKnUqWMKDAAFU+sini9CeTFBl1KT3I5kgAxJCcGCMyjsmCOcIs4DTTDsymZK7Q4kqVJoXwIBBEkQqblyzmqZIgeFyFAilFPdlshzyrnXDzJBQcDjCvrsrVUEnXPVUg1GlAmhS+MC5ZHAIO5S2y6367n2x3OwPBt0wnWla7gu61SIIjFQKKqdVYDOQxWVC73GCPPoveapbYfMPB1oQADW6FrC1O7sopfgyG0qQATGYzIMqxNpTMYHX3gtG7lm220B3tmd0BwvD5-EEQmFs+j4gQkqkAELAZ3Y3FxHL5QqYYrabnlrONFodbpPMdjauT44Ar4-H89a7Pshx-mcz7csWFbjs8v7-B8AEbH+HxAmBoKYNBUJlsqKBwjmSKogRmIoDieKEu6nrprckKpGiiIkQWCBFjRPZpjh1AwQs0zqMAtKzJ2ACiUA+FmzjOvCZFxPMEHVGx1wPhgqQACwjmO3GZHxrxCSJ0CpOJMCSVeYAyZg66bn4ATBKE4T0XEUAJMeMAKAgxocOErTAEuN4FEU-ZlBxNT1M0bSdD0fRflK0woSsLjfMhCEgcC4HdthNwQjB34TtFiWfHFgExYCoEgqumGsWlKZ4YZxEonZSKXuRlGpmS0Y+jSdJmsyP7TFaFIxnavICkKZpihKMBRWI3oKuxVTalVI0OE1bgzVhdE1fmhZlRlUJKpxWZDBpvH8TOwmiakwCuf67kwPIS6mZBM2KWAKlmAAzOpIw8aoWkCfMp16TAF1ueEt3tjAa6cBZ27WXudUOUeKTCmgKAAB7YCg4AoM6-2wIEeS+fe-nMLtQUwK+oUfhFwxZXBOXAXlawJfTaElV2D2VKtlY0zWuVIb8hUsylWFPrh+EHpQRHi3mpHGYSlW6q1mTtQyTITb1HI+rGg2Cs6C3yOKkrdZNiu2iTsIwGqGpLQrfVtX6JqdRaDjqzaWQDQ6TouqKi3y16Ju+sahSJiG86RlNtqVHGnvB8muG9pzdWMZtwszc+DyHd9x06WdMDIhwahGsQCMwBAABmMCUKJ+L3XJO2VE9qQAKxqcMGc-SdulZnnBdQEXiRoCX5eV9A1fg2ZkO2N4llBNA7C0jAAAyEAxIjZDpJreO3n5JTE4FFbk++XSuOo-djhNyx8xwsl7VB5UPNz8H05fQvlSLc2pAgy+CsiS8rw1cRyzjn7W2SsYC0hVhLNWUYQERx5PyHWXsoCjUNtlY2MC3ZmxVIg2Oc0bYayVvbIO4ZQzhjDv7LWHsEykJ9kA6i21Ui-0FBtZiW1My9jTlzCaK5ZwjHnIuMGM4RjX0uI9ImQ4W6wRrNOTsAI+GtgETIiGG5MBT2hrPEwbhGGI3pCEcM0AkAAC8sY+TvE9N+pMD5hWPqoU+wxz4wEvsItiHM76cKNoVZ+GEU6BXNp-FeP8v5oH-gSa2wD8HUjAR1VWRsXb9UjtrYaroHAG3GkbaB4SME+KwZbTUvs6GZgYYE5hLFtrsNrunT6mks5-U7qkXwujgz6KMZsMeD0FJiJgKpAAjB9L67ds4A3qfOJpWNTLmRUVuKy6j55aNSDo1G6NMbY07iY7ej4Sb7xCofaxtihj2McalW+9C3GoI8flJmXZvGzQ9Fgvx38tHBMAbgsJrtlaOxiek12FChq6ySfrMaE1Pn9UwfNP5wBQktXQYaQOKBg4kKTM7IFmt3bxmctQ+QoT8m0UXkUtQydX6p3KZWNu1SYA41SN3VQhdsDFzLhXTuo8nF1z7DvJuEiSXaRqTnSl1LaVDwZWMieUMrLeBQOgHFxg-DPTSDGTeBMzEbJfNIQSC9BKtEElYzIti5FLkOViisOr0CsJLCCj+y8pUBMlUGR5EKZD+2VnCw1aBYnIviakH5aKEX-PCE6pF01TWeojDQuaFRw4GnztwIhwZkQx0RWGio9oZAoEjW4WNGLKrsU5r-KVxTjWUHYhwg6TLewNxgMOUcrclHCqCKK8V2agxzIabkEZzo5WmKJuY-eyrVXqs1SfJIgxfXs3SgUwG4Z+FoDzRVUWMA-EWvrWAG1vtIUZIdcQp1LrppupgB6tNwAUm+rDWU9+Ft1S5NoS445C7c0p0VRUvppLyXjSbS2muN9RGss6WYHprdKlHU5WS2pz7hm92aYK5RqiRXwjreahtSMFkY0KMs0SqzCY707UqlVaqNXdB2QOptE69WXtHb6q5lUzVWrAJanNMtGrLrtVCyJYBHXjvkc6v1sDE27vRfusah7yGEpPXupaob7URrpLC4he7N3qATbyJNKbA04JuSgTNrjr14pYbeve+0OW-UA9y-OVLe40v7oPelVc30iPaZ+5uFaDp-szgBp9PKTN8osyPcDtgJnT0CPCZ0C9EQwAAOKTgNDKje+N23obvWTYLaqwrGEnIO1jurh2c1IwSrJVVkBxFCzxH+iJ8tqCXbhUTULwEsabGxmTbtt3ca9bxn1qWjVHsEyp1Iwn6Nho-kVsLyJatyfdQgi6CAQuTjdPGio5HxvTDFKXaAFdQC5DAZOETFQs19Z4je8qBaiUOaSzxASdRRiHZQAASWkAJLpg5XrKQ+FF007i5hLEGAsRwCBQAtjNDzEYAIzsADlJwvZODAdoxaP0DjLeykYZ3VDHdO5OS713bv3YWI9n7j9XsDxGB9r7mO6b-cnED6YIOwdVp89Dfzi8gvFfCzokDhjjFtrWQFa5+94uHzOyl6raXa5HJIy1ydZGZ25bAHTwreWwulZDeUHrlX11C9qxQndCC90HqFxxzJ1zzY5MxcR7FgWpfbc0yUth7WuKOf6VywZL7QOjNafzyHSkv0-ocw+5zQGhl6Pty0inkGgjU6N8wOnjaENLIM4tlnaH1k6fqJzxLyWCNsaIyO7FmXSkBrFxL4PdOZcqfK6uhX0aN1a5Vw1oN3qx289awJgNXWgErtebOrbagBta6G6roUo3ZsoEm-7PbJ7ivzcW-6EAK2zvrfKJt43JXTdTuPaTd3VTPeGZ7n3JI5nh5QEZXqmzUO7O9JX-plzRneVmbpdv3f4zq2BGwL4KA2BuDwBhb31Ia9ZWPZj2zjhljujc+TyXDHEB0nAhwNwNSF2AOJ1AIXwDUIRQGK2RDgBhWK3z1EEL1eWLxDiVzL3di7yoUaw1xr0nTa3rx4xEzl3tXAUQJAJ6g73KC4wQWH2STGloLQQyUHw61PStgzUoKhXgJoOgLoLa23VRWYPTTKw21cWQP9EKFQPnywk4MtzO2R1SBuzuzHiegAB40wOFy0PoVCrs1DUcx5xkA878H8n83AZCTQ38YAzwLxaNrxo8FU48yYtlE9pgecFw2MoDpgScUAwC08IDiC-CUAAiwQssdcsEBDJwkCUDJw0DUx9UswbC5DEiFCCVYtl9-0T8gMDIjI8QrN5J64OlVJ7M9MO4c4CinCTJTCJ5zD79H9n80i3BitG1GdmlUNXD2cXwPD-8k8nUwiIjU8MtIDhg2DIjM9stUhYjph4jZCECMipIQleD5c6QqsfClxlc8CK9kFq8tja90FF9dcz19dgjUiEjpgdtM9sjKiBksxvdGlfdijmVS1ukj9ciqjbdOiHd-dJkggmirCX9Fi7D5k0ZENjEcZuiO1Ys-8ugAChiJihDAjRjXEkShhJjYCZiYA5iUAFiTR5CVinkC8+Ci8NjFdiCdj6s1ceMiDDiSC68cSG9Zd1jxc4i2DBsGD5MPVxCmtVt-C1sptygZs9c1j7U8TkROTcDRDPY+Sp9OZWj5CmIzcTU7irdH0gNXMN8B5L8BVHd3198XdD9f0Pc8i19jMdSt99SKdb9dBk1P5sgYAAApCAQUOwj-DePHEAFsb-XeXo4KekLnQY8YhzJ-YAe0qAOACAT+KALYAAdRYHO3VS6BPAXgUDgAAGkidphVCYB1DlIDSRFwCswMSVhPtIzozYyEykyUy0yMzsyPhDCUcNCpjzccSAArN0tARA10wUIk2WCgtkzYidakuBfApTFg5rYg3AsgxrIc+1Ls-sjkpHaQLkxgoUPklJQwjvabGdeU8UqFfwewdI+Y-ZCsygKskeegxNekbAE8toibZJXoQUYwQYcMysmMxbAAMlSVQQVNcT7J7IyJVKnSUN0zOG0N0KJX0MrXHggwBMCHtI1AgCdKArsIcOzBWKCG9N9J6N-waCDM8JQG8InQ+g-MvK-LjLmETOTMElTPTKzJzIuyMPzJMKCLGNCN-QoqjKoprLooYobOYrzILLHhFxPSXOAvmKAoHLo0kOn0Au7OVPxW2nAvvWPzrCfRqJWNMigtKA4XKM+Kc1+i0vPGqh0vqIQt82QsdJdO7LBLtyZ1bVwr9Iw0DODK8MAPQHIovN4urJotrPovrKYqbNXJbMLI4vRNDPLIjMov8pGForrMYsbIWGbOMNbOxOiKqkkt7KUuWMHMPPJOY0pIZLHI3IIMr35P42OPa1OJ4IvQuNSBkpApUvN3VLNO+MeMcrAyLJKJZShw+NNI0oeLqW6r+PgrtIdNQrsvdPaOckunPA8i8nFUCBcvwqJUaCIoGM8rLM+F8qvOooSsCsEpCtSrCvSoirROOV2p4oOv4qSqEtCtzNYtErbJNU7Lyuks+pQCSNUzJMwIpJLxwJEPHL2KnIOMI1IOZPIMKteRypXOevXJ5KYKfKrx3OFJmwPIas5mauuMyNuLcJyOMs6vOgWuulBmXF6reLKLeiMut0j1LLJpBmWspv+OsqmrQvsrmvBMWSQwZpws+x9NcrhMIo8pIq8rQB8tir8ugHuqCuSuEpevYqusFy4rDP2r4oCoEuCpSth3OrYoyvEq4Phq+uXLxuJIXIq0BuwKpJlNBtpMIL401yhqys6xhrK3+v1GSBNvxMMKRuG03NRv5PRoHz3KHyDsttXWPMJLiPPOloOv9uFHvNsK3JfJMHfI1tjJgF-MBQzSkOOVxp+vxrasJvuJty7jPzcwv35UsyppLQ6RNKJvptP3X1M03z1NrttMpygwjL7H9FgGAGwCf0IEclXnXgVBcNhLcLqG7WwzCg8BgHUkiuOSLUypmxAG4DwHb263tVxM3qgE6jKvk0QH7uwWSXsnSDAAfzEB3sYw3v7sQKPtSBPrwF7zFAvvAGvttR6z3v7rhSfr7tfvVw-qvvhHOMVP3puJLoDOJQhyNOlVgtXrMMQu8F7pfoHqHuQBAFHrDwhIj2hMnpi2ntnt7W6AXqXpVuxVXqNvNnvq3pJM9Cby9t-rwEPrtsTXQbPv1hAa-tvtXRYagEfvYePv3rfvPp0Evt4cbwY34boagH-uEeftEeAYkc-rAbyRLKUf7qgbVNLo1NXwBm1Lbt1Jrs8zrud2lUbrLoZopUrqtI7rMaUSAA]
```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
