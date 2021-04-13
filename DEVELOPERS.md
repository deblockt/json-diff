## Json-diff developer guide

### release process

build a snapshot version:

``` shell
mvn clean deploy
```

release the version

``` shell
mvn nexus-staging:release
```