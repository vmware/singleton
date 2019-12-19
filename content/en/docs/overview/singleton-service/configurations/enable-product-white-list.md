---
title: "Enable Product White List"
date: 2019-12-11T14:14:49+08:00
draft: false
---

Singleton Service adds a new feature to support the product white list defination, that means only the product in the white list can work well to get translations and get supported language list. It is no impact for *DateTimes / Numbers / Currencies / Plurals / Measurements / DateFields / RegionList* .

By default, this feature is disabled.

This page will introduce the details about this new feature.

How to enable product white list in Singleton Service?
------------------

The steps:

1. Generate a Singleton Service build or get a Singleton service build based on v0.3.0;

2. Create a JSON file named `bundle.json`, and put it into the folder `.\l10n\bundles\`.
The content is like:

```
{
    "Testing": ["1.0.0", "1.0.5"],
    "SampleAPP": ["1.0.0"]
}
```
The products in this file will be supported by Singleton Service, it's not related to *version*, only **productName**. 

**Notes**:
1. This configuration file will be loaded into cache when Singleton Service starts. Please restart Singleton Service if any change for this configuration file. 

2. If you use Singleton Service S3 build, please also put this configuration file into local folder `.\l10n\bundles\` with Singleton Service S3 build together. Don't move it to AWS storage.
