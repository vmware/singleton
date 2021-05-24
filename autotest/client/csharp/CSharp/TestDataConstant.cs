/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;
using System.Reflection;



namespace CSharp
{
 

    public class TestDataConstant
    {
        //bat file path
        public static string bat_path = @"E:\test_devops_csharp\test_new_csharp\test1\csharp\newbat\";
        public static string bat_path1 = @"E:\test_devops_csharp\test_new_csharp\test1\csharp\newbat1\";
        //source collect test data
        public static string key1 = "collect.message";
        public static string value1 = "application";
        public static string value1PseudoFromService = "#@application#@";

        public static string key2 = "collect.argument";
        public static string value2 = "{0} is the {1} day of a week.";
        public static string value2PseudoFromService = "#@{0} is the {1} day of a week.#@";
        public static string value2PseudoFromclient = "@@{0} is the {1} day of a week.@@";


        public static string key3 = "collect.00000000-0000-0000-0000-000000000000.templates_Draas-SSLCertificateRenew-completed.text";
        public static string value3 = "<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager and vSphere Replication (VR) appliances running in your VMC SDDC completed successfully.</p><ul><li><span style=\"color: #003366;\"><strong>ORG : <span style=\"color: #3366ff;\">{org_id}</span></strong></span></li><li><span style=\"color: #003366;\"><strong>SDDC : <span style=\"color: #3366ff;\">{sddc_id}</span></strong></span></li></ul><p><strong style=\"color: #003366;\">Customer actions required</strong></p><p>Due this operation the remote VR status will look disconnected in the SRM UI of the SDDCs which have VR pairing to this SDDC. This doesn't affect the existing replications, so your workloads continue to be protected. However, the UI might show stale data for the replications status and replication management operations across the two sites won't work. To resolve the issue, now you can execute either of the following actions:<strong style=\"color: #003366;\">:</strong></p><ul><li><span style=\"color: #003366;\">Reconnect the VR pairing. The steps how to do this are outlined in the VR documentation <a href=\"https://docs.vmware.com/en/vSphere-Replication/8.2/com.vmware.vsphere.replication-admin.doc/GUID-AF7E944C-D077-498E-88AC-C5E71AE7E5C0.html\">here</a>.</span></li><li><span style=\"color: #003366;\">Restart the on-prem VR appliance or the hms service inside it</span></li></ul><p><strong>Additional Information and Support:</strong></p><ul><li><span style=\"color: #000000;\">For additional support, please utilize the chat function within the VMC Console, or by filing a request from MyVMware at <a class=\"external-link\" href=\"http://my.vmware.com/\" rel=\"nofollow\">http://my.vmware.com</a>.</span></li></ul><p>Thank you,<br/> The VMware Site Recovery Team</p></body></html>";
        public static string value3PseudoFromService = "#@<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager and vSphere Replication (VR) appliances running in your VMC SDDC completed successfully.</p><ul><li><span style=\"color: #003366;\"><strong>ORG : <span style=\"color: #3366ff;\">{org_id}</span></strong></span></li><li><span style=\"color: #003366;\"><strong>SDDC : <span style=\"color: #3366ff;\">{sddc_id}</span></strong></span></li></ul><p><strong style=\"color: #003366;\">Customer actions required</strong></p><p>Due this operation the remote VR status will look disconnected in the SRM UI of the SDDCs which have VR pairing to this SDDC. This doesn't affect the existing replications, so your workloads continue to be protected. However, the UI might show stale data for the replications status and replication management operations across the two sites won't work. To resolve the issue, now you can execute either of the following actions:<strong style=\"color: #003366;\">:</strong></p><ul><li><span style=\"color: #003366;\">Reconnect the VR pairing. The steps how to do this are outlined in the VR documentation <a href=\"https://docs.vmware.com/en/vSphere-Replication/8.2/com.vmware.vsphere.replication-admin.doc/GUID-AF7E944C-D077-498E-88AC-C5E71AE7E5C0.html\">here</a>.</span></li><li><span style=\"color: #003366;\">Restart the on-prem VR appliance or the hms service inside it</span></li></ul><p><strong>Additional Information and Support:</strong></p><ul><li><span style=\"color: #000000;\">For additional support, please utilize the chat function within the VMC Console, or by filing a request from MyVMware at <a class=\"external-link\" href=\"http://my.vmware.com/\" rel=\"nofollow\">http://my.vmware.com</a>.</span></li></ul><p>Thank you,<br/> The VMware Site Recovery Team</p></body></html>#@";
        public static string value3PseudoFromclient = "@@<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager and vSphere Replication (VR) appliances running in your VMC SDDC completed successfully.</p><ul><li><span style=\"color: #003366;\"><strong>ORG : <span style=\"color: #3366ff;\">{org_id}</span></strong></span></li><li><span style=\"color: #003366;\"><strong>SDDC : <span style=\"color: #3366ff;\">{sddc_id}</span></strong></span></li></ul><p><strong style=\"color: #003366;\">Customer actions required</strong></p><p>Due this operation the remote VR status will look disconnected in the SRM UI of the SDDCs which have VR pairing to this SDDC. This doesn't affect the existing replications, so your workloads continue to be protected. However, the UI might show stale data for the replications status and replication management operations across the two sites won't work. To resolve the issue, now you can execute either of the following actions:<strong style=\"color: #003366;\">:</strong></p><ul><li><span style=\"color: #003366;\">Reconnect the VR pairing. The steps how to do this are outlined in the VR documentation <a href=\"https://docs.vmware.com/en/vSphere-Replication/8.2/com.vmware.vsphere.replication-admin.doc/GUID-AF7E944C-D077-498E-88AC-C5E71AE7E5C0.html\">here</a>.</span></li><li><span style=\"color: #003366;\">Restart the on-prem VR appliance or the hms service inside it</span></li></ul><p><strong>Additional Information and Support:</strong></p><ul><li><span style=\"color: #000000;\">For additional support, please utilize the chat function within the VMC Console, or by filing a request from MyVMware at <a class=\"external-link\" href=\"http://my.vmware.com/\" rel=\"nofollow\">http://my.vmware.com</a>.</span></li></ul><p>Thank you,<br/> The VMware Site Recovery Team</p></body></html>@@";

        public static string key4 = "collect.message.application";
        public static string value4 = "application message";
        public static string value4PseudoFromService = "#@application message#@";
        public static string value4PseudoFromclient = "@@application message@@";
        //Resx file test data
        public static string keyArg = "RESX.ARGUMENT";
        public static string valueArg = "Add {0} to the object.";
        public static string valueArgPseudo = "@@Add {0} to the object.@@";

        public static string keyError = "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error";
        public static string valueError = "Your contact page.";
        public static string valueErrorPseudo = "@@Your contact page.@@";


        public static string keyURL = "Resx-message.URL";
        public static string valueURL = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>";
        public static string valueURLcn = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>";
        public static string valueURLja = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>定期メンテナンスが開始されました。</strong></span></p><p>メンテナンスに関する重要な情報は次の場所にあります：<a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>";
        public static string valueURLfr = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong> La maintenance planifiée a démarré. </strong></span></p><p>Des informations importantes sur la maintenance peuvent être trouvées ici: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>";
        public static string valueURLPseudo = "@@<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>@@";
        public static string valueURLMTes = "<html><body><p><estilo de intervalo\"color: rgb(255,0,0);\"><strong>El mantenimiento programado se ha iniciado.</strong></span></p> <p>Información importante sobre el mantenimiento se puede encontrar aquí: <a class-\"external-link\" href-\"http://www.vmware.com\"https://www.vmware.com</a><strong><br/></strong></p></body></html>";

        //properties file test data
        public static string keyURLP = "message.url";
        public static string valueURLP = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>";
        public static string valueURLPPseudo = "@@<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>@@";
        public static string valueURLPMTes = "<html><body><p><estilo de intervalo\"color: rgb(255,0,0);\"><strong>El mantenimiento programado se ha iniciado.</strong></span></p> <p>Información importante sobre el mantenimiento se puede encontrar aquí: <a class-\"external-link\" href-\"http://www.vmware.com\"https://www.vmware.com</a><strong><br/></strong></p></body></html>";





    }
}

