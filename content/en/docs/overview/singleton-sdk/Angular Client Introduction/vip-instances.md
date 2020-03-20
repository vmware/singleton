---
title: "VIP Instances"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

| Instance  Type                          | Host                                                         | Port | Purpose                                                      | Connected by                | swagger-ui URL                                               |
| :-------------------------------------- | :----------------------------------------------------------- | :--- | :----------------------------------------------------------- | :-------------------------- | :----------------------------------------------------------- |
| Internal Production                     | g11n-vip.eng.vmware.com                                      | 8090 | Run time VIP server for VMWare's SaaS products               | Product's Production VM     | https://g11n-vip.eng.vmware.com:8090/swagger-ui.html         |
| Internal Production for SolutionBuilder | g11n-vip-3.eng.vmware.com                                    | 8090 | Run time VIP server and source collection for SolutionBuilder | Product's Production VM     | disable swagger-ui on this host                              |
| Internal Staging                        | g11n-vip-stg-1.eng.vmware.com                                | 8090 | Source collection and G11n official translation drop that will be delivered to Product | Product's Staging VM        | https://g11n-vip-stg-1.eng.vmware.com:8090/swagger-ui.html   |
| Internal Sandbox                        | g11n-vip-sandbox.eng.vmware.com                              | 8090 | G11n official translation drop sanity testing server         | Product's Sanity Testing VM | /                                                            |
| Internal Testing                        | g11n-vip-testing-1.eng.vmware.com                            | 8090 | G11n testing server                                          | Product's Testing VM        | https://g11n-vip-testing-1.eng.vmware.com:8090/swagger-ui.html |
| Internal Development                    | g11n-vip-dev-1.eng.vmware.com                                | 8090 | Core-dev development and debug server                        | IDE/CLI                     | https://g11n-vip-dev-1.eng.vmware.com:8090/swagger-ui.html   |
| CSP Production                          | [console.cloud.vmware.com](https://console.cloud.vmware.com/) | /    | Provide official translations for the product that through CSP |                             | disable swagger-ui on this host                              |
| CSP Preview                             | [console-preview.cloud.vmware.com](https://console-preview.cloud.vmware.com/) | /    | Provide official translations for the product that through CSP |                             | https://console-preview.cloud.vmware.com/i18n/api/doc/swagger-ui |
| CSP Staging                             | [console-stg.cloud.vmware.com](https://console-stg.cloud.vmware.com/) | /    | Provide official translations for the product that through CSP. Source collection provided with CSP token. |                             | https://console-stg.cloud.vmware.com/i18n/api/doc/swagger-ui |
| CSP Development                         | [console-dev.cloud.vmware.com](https://console-dev.cloud.vmware.com/) | /    | Provide official translations for the product that through CSP |                             | https://console-dev.cloud.vmware.com/i18n/api/doc/swagger-ui |



<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    article section.page table th {
        font-weight:500;
        text-transform: inherit;
    }
    table thead tr th:first-child {
        width:11rem;
    }
    table thead tr th:nth-child(2) {
        width:15rem;
    }
    table thead tr th:nth-child(3) {
        width:4rem;
    }
    table thead tr th:nth-child(5) {
        width:10rem;
    }
</style>