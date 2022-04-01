# How to contribute to Singleton documentation?

Please download and install Hugo following the instructions on https://gohugo.io/getting-started/quick-start/.

Follow [Singleton contributing guideline](https://github.com/vmware/singleton/blob/master/CONTRIBUTING.md) to fork and clone Singleton project source code.

##### Switch to website branch

```
git checkout website
```

##### Download the theme files for the website

```
git submodule init
git submodule update
```

##### Add a new document

```
hugo new [document-name].md //e.g. hugo new docs/get-started/overview.md
```

##### Modify the content of existing documents

###### For Java client documents

Please directly update the contents to branch 'gh-pages' under [/java-client](https://github.com/vmware/singleton/tree/gh-pages/docs/overview/singleton-sdk/resources/java-client), and update the quotation of Java client documents version to [Java client introduction](https://github.com/vmware/singleton/tree/gh-pages/docs/overview/singleton-sdk/java-client-introduction) page if needed, finally you will see the change on website https://vmware.github.io/singleton/docs/. 

As for document synchronization only, please update the Java client documents to branch 'website'. Don't need execute command `./publish_to_ghpages.sh`  to publish the update as it will break the website https://vmware.github.io/singleton/docs/.

###### For general documents

Please directly modify the contents in .md files under /content if needed using any markdown file editor.

After your modification is done, please run the following command to publish the update to Singleton website.

```bash
./publish_to_ghpages.sh
```

After publish_to_ghpages.sh execution, the updated contents will be submitted to your remote folded branch 'gh-pages', you should go to GitHub and merge the new content to https://github.com/vmware/singleton/tree/gh-pages, finally you will see the change on the website https://vmware.github.io/singleton/docs/.
