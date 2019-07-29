# Contributring to Singleton-demo

## Welcome

Welcome ......


## Getting Started

### Fork Repository

People can only commit changes on fork repository then open a pull request to Singleton repository, so please fork this repository first.

### Branch

We have sub projects in each branch:

|      branch    | purpose |
|----------------|---------|
|   master       | Singleton service code  |
|   java-client  | Singleton java client code  |
|   js-client    | Singleton js client code  |
|   devops       | CI and automation testing code, it is not place to contribute code unless you want to add CI or test scripts.  |


## Contribute flow

### Fork Repository

### Set Signature

Set Signature in Github, refer https://help.github.com/en/articles/about-commit-signature-verification#gpg-commit-signature-verification

### Setup CI Pipeline
Singleton use Travis-ci as CI framework and help contributor to test their code change as early as possible. Some configuration need to be done to setup CI pipeline in your fork repository.

##### SonarCloud Configuration
1. Generate Sonar Token

    a. After you fork the repositpry, login to 'sonarcloud.io' with your github account;
    
    b. My Account -> Security, generate a token and copy it, it will be used later.
    
2. Get Organization Key

    Go to 'My Organizations', mark down the key of your personal organization in right-up corner(not Organization name), it will be used in Travis CI configuration later.

##### Travis CI Configuration

1. After you configured SonarCloud, login to Travis-ci 'https://travis-ci.org' with your github account

2. Find your fork repository in your account settings, enabled it then click 'settings', add following environment variables
    
    SONAR_ORG=\<Organization key in SonarCloud configuration step2\>
    
    SONAR_TOKEN=\<token generated in SonarCloud\>
    
##### CI Pipeline Result

After you finished above configurations, you can commit and push code to your fork repository. It will trigger CI on Travis, you can login Travis to see result. You can login SonarCloud.io to see code scan details.

The sonar project and quality gate will be created automatically, project name should like: <SONAR_ORG>:\<sub-project name\>:\<branch>. Quality gate name should like: singleton:\<sub-project name\>-gate

#### Vulnerable Scan

We use Dependabot Preview(A Github APP owned by Github which provide dependencies scan) to do vulnerable scan

#### Configuration

1. Login to Github, click 'Marketplace' in navigation on top.

2. Search for 'Dependabot Preview' and click the search result.

3. Click 'Set up a plan', then the screen scroll down to the end of the page, find 'Install it for free' and click it, then click 'Complete order and begin installation', then click 'Install' to install the Github APP.

4. Click 'Sign in with Github', then click 'Authorize Dependabot Preview by GitHub' to sign in Dependabot Preview. After that, you will able to see the Dependabot Preview panel.

5. Click 'Select repos to add' and fill in the repo, dependencies description file(e.g. pom.xml for Maven, build.gradle for Gradle, package.json for NPM) info in the popup modal, then click 'Add language' to finish the set up.

6. After the set up, the Dependabot Preview will scan the open dependencies in your project automatically, if any vulnerability found, pull requests with detailed info and auto fix will be created for the repo, please keep eye on it.

### Pull Request

You can open a PR after you get pass result of CI pipeline. CI pipeline will run again for this PR, then you can wait for review and merge.



