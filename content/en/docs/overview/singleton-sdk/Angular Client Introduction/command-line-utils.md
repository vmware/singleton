---

title: "Command Line Utils"

date: 2019-09-24T20:08:31+08:00

draft: false

weight: 30

---



#### **Load Translation**

Download the specified languages of translation files in your project location.

##### **Command line arguments description**


| Parameter |  Type  | Value  |             Description             |
| :---------: | :--------: | :------: | :----------------------------------------------------------: |
| --directory |  string  | required |    The directory you want to put translations in.    |
|  --host  |  string  | required | About singleton host, please see [Singleton Instances](https://ngx.eng.vmware.com/@vmw/ngx-vip/vip-instance). |
| --product |  string  | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
| --component |  string  | required |            Component name.            |
| --version |  string  | required |            Release version.            |
| --languages |  string  | required | Specified the languages your product supports; Separated by , for example, zh-cn,en-US. |
| --verbose | Don't need | optional | If set, it will show all information during command execution for debug purpose. |


For example

![command-line-utils-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-1.png)


##### **Config script in package.json**


![command-line-utils-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-2.png)



##### **Use the script**


![command-line-utils-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-3.png)


#### **Collect Source Bundle**

Collect source strings when using source bundle feature in the singleton

##### **Command line arguments description**

|  Parameter   |       Type       |  Value   |                         Description                          |
| :----------: | :--------------: | :------: | :----------------------------------------------------------: |
| --source-dir |      string      | required |              The directory your source code in.              |
|    --host    |      string      | required | About singleton host, please see [Singleton Instances](https://ngx.eng.vmware.com/@vmw/ngx-vip/vip-instance). |
|  --product   |      string      | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
| --component  |      string      | required |                       Component name.                        |
|  --version   |      string      | required |                       Release version.                       |
|  --verbose   | Don't need value | optional | If set, will show all information during command execution for debug purpose. |


For example

![command-line-utils-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-4.png)

##### **Config script in package.json**

![command-line-utils-5](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-5.png)

##### **Use the script**

![command-line-utils-6](https://github.com/zmengjiao/singleton/raw/website/content/en/images/command-line-utils/command-line-utils-6.png)


<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    article section.page table th {
        font-weight:500;
        text-transform: inherit;
    }
    table thead tr th:first-child {
        width:13rem;
    }
    table thead tr th:nth-child(2) {
        width:10rem;
    }
    table thead tr th:nth-child(3) {
        width:10rem;
    }
</style>