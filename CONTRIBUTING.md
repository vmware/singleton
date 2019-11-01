# Contributing to Singleton Java client


## Welcome

Welcome to Singleton Java client! This guide provides information on filing issues and guidelines for open source contributors. **Please leave comments / suggestions if you find something is missing or incorrect.**

## Get Started

### Fork Repository
Go to [Singleton repository](https://github.com/vmware/singleton) and click 'Fork' on top of page.

### Download the forked repository to local

``` 
git clone --branch g11n-java-client https://github.com/{github_user_id}/singleton.git g11n-java-client
```

### Set Signature, setup CI Pipeline 
Please refer to the [general guideline](https://github.com/vmware/singleton/blob/master/CONTRIBUTING.md).
Signature is mandatory for the commits, please make sure to get it worked. If not, your PR won't be able to merge.

## Develop, Build and Test
### Create your branch
Change should be made on your own fork in a new branch. The branch should be named g11n-java-client-XXX-description where XXX is the number of the issue. Run this command to create a new branch.

```
git checkout -b g11n-java-client-XXX-description g11n-java-client
```

### Setup development environment

Please refer to [developer guide](https://github.com/vmware/singleton/blob/master/g11n-ws/docs/developer_guide_javaclient.md) to setup environment.

### Develop
The coding style follows [JAVA style](https://petroware.no/javastyle.html).

### Build and Test
Unit test cases should be added to cover new code.
Run command `gradle clean build` to generate build and run unit tests.

## Keep sync with upstream

Once your branch gets out of sync with the singleton branch, use the following commands to update.

- Add vmware/singleton as a remote repository. Here it's named 'upstream'.

```
git remote add upstream https://github.com/vmware/singleton.git
git fetch upstream
```

- Rebase development branch.

```
git checkout g11n-java-client-XXX-description
git fetch -a
git rebase upstream/g11n-java-client
```
Please use fetch / rebase (as shown above) instead of git pull. git pull does a merge, which leaves merge commits. These make the commit history messy and violate the principle that commits ought to be individually understandable and useful. You can also consider changing your .git/config file via `git config branch.autoSetupRebase always` to change the behavior of git pull.

## Commit

The commit message should follow the convention on [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

## Push and Run CI pipeline
After pushing change to forked repository, CI pipeline will be triggered automatically. Go to [travis](https://travis-ci.com/) to check the result.

## Pull Request
If your code is ready to review and the pipeline is passed, go to [here](https://github.com/vmware/singleton/pulls) to submit a PR for your change.
After it is approved, please choose "Squash and merge" to avoid too many commit histories.

## Reporting issues

It is a great way to contribute by reporting an issue. Well-written and complete bug reports are always welcome! We suggest reporting issues with our template for high quality.

## Design new features

You can propose new designs for existing features. You can also design entirely new features, please submit a proposal in GitHub. We suggest to submit with our template.
