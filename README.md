# Singleton Website

We use Hugo to maintain Singleton documentation and publish them to [Singleton website](https://vmware.github.io/singleton/). 

The contents in the documentation are stored under /content and organized as below:

```bash
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
        │       │   ├── enable-S3-key-encrypted.md
        │       │   ├── enable-S3-storage.md
        │       │   ├── enable-new-product.md
        │       │   ├── enable-product-white-list.md
        │       │   ├── enable-pseudo-translation.md
        │       │   └── enable-source-collection.md
        │       ├── singleton-service-DB-Build-Introducation.md
        │       ├── singleton-service-apis.md
        │       ├── singleton-service-custom-log.md
        │       └── singleton-service-script.md
        └── tutorials
            ├── _index.md
            ├── deploy-singleton-service-in-your-app.md
            ├── integrate-singleton-in-angular-app.md
            ├── integrate-singleton-in-csharp-app.md
            ├── integrate-singleton-in-java-app.md
            ├── integrate-singleton-in-javascript-app.md
            └── integrate-singleton-in-python-app.md
```

If you want to contribute to Singleton documentation, please refer to [Contributing Guideline](https://github.com/vmware/singleton/blob/website/CONTRIBUTING.md).