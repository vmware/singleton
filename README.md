Singleton Website

We use Hugo to maintain Singleton documentation and publish them to Singleton website. Please download and install Hugo following the instructions on https://gohugo.io/getting-started/quick-start/.

The contents in the documentation are stored under /content and organized as below:

.
└── en
    ├── _footer.md
    ├── _index.md
    └── docs
        ├── _index.md
        ├── get-started
        │   ├── _index.md
        │   ├── download-and-installation.md
        │   └── quick-start.md
        ├── overview
        │   ├── _index.md
        │   ├── singleton-sdk
        │   │   ├── _index.md
        │   │   ├── angular-client-introduction.md
        │   │   ├── csharp-client-introduction.md
        │   │   ├── java-client-introduction.md
        │   │   ├── javascript-client-introduction.md
        │   │   └── python-client-introduction.md
        │   └── singleton-service
        │       ├── _index.md
        │       ├── configurations
        │       │   ├── _index.md
        │       │   ├── enable-pseudo-translation.md
        │       │   └── enable-source-collection.md
        │       └── singleton-service-apis.md
        └── tutorials
            ├── _index.md
            ├── deploy-singleton-service-in-your-app.md
            ├── integrate-singleton-in-angular-app.md
            ├── integrate-singleton-in-csharp-app.md
            ├── integrate-singleton-in-java-app.md
            ├── integrate-singleton-in-javascript-app.md
            └── integrate-singleton-in-python-app.md

Please modify the contents in .md files under /content if needed.

After the modification is done, please run the following command to publish the update to Singleton website.

```bash
./publish_to_ghpages.sh
```

