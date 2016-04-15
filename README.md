# Version Management Client

One of the boring and badly solved questions in CI is how do you version your changes on CI. These clients, use a hosted service to give you an automatically provision versions for you.

## Clients
We provide two clients, with more hopefully on the way. The supported clients are Java and Gradle. The Gradle client uses the Java client.

### Gradle Client
The Gradle client integrates the service with your project. For details on how it's used you can check out their projects build.gradle. Some important details are that a project's version will be set pulling from the service. In the event of a service outage or offline access, it will use the latest retrieved version or `0.0.1-SNAPSHOT`.

Using the version manager extension you can specify the project and repo names.

```
versionManager {
    projectName = 'ethankhall'
    repoName = 'version-manager-clients'
}
```

Additionally you can set `authToken` and `baseUrl`. Using the Gradle client the auth token can also come from the environment variable `VERSION_MANAGER_AUTH_TOKEN` or by setting `-PversionManager.authToken=<token>`.

### Java Client
The Java client provides access to the API's to get access to the version number and to claim it. In the future the API will expand to include other functionality.

## How it Works
Given that most project want to follow [semver](http://semver.org/), the service will automatically provision your versions based on the commit message and your commit history.

In you commit message you can use things like `[bump major]` or `[bump minor]` and the service will use your message to update the versioning as you specify. If you don't specify anything then you will just get the next patch version.

When you post to the server, 'claiming' a version you must send three things.

### Claiming Versions
The first is your current commit id. This can be any commit id you want. Most use cases will use the git hash or svn commit id. But you can also use timestamp or anything else as long as it's less than or equal to 16 characters long.

The second item is the message for the commit. Most use cases will use the real message, but some people may want to write a different message into the service to set their own versions.

The last item is a list of your history. In most use cases you will just send the last 50 commit ids and the service will find your last commit and use that as the seed. You can also send a list containing only `"latest"`. This will grab you latest version and use that.

### Other Versioning Schemes

If you don't like semver and want to follow another versioning scheme you are welcome to, all you have to do is in your message specify `[set version 1.2.3-RELEASE]` to get the version 'number' `1.2.3-RELEASE`.

If you don't want to take part in the versioning system at all, but want automation. You can use what's called an `atomic` bumper when creating the repo. This will give you an atomically increasing number for each version.

## Api Docs
Coming Soon.
