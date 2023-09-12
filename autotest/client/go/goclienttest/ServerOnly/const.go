/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package ServerOnly

//COMMENT
const (
	Defaultcom      = "DefaultComponent"
	commonkey       = "message.translation.available"
	commonvalue     = "Translation is ready for this component."
	frcommonvalue   = "La traduction est prête pour ce composant.xxx"
	dlcncommonvalue = "该组件已准备好翻译。"

	holderkey       = "message.argument"
	holdervalue     = "Operator '{0}' is not support for property '{1}'."
	frholdervalue   = "L'opérateur '{0}' n'est pas pris en charge pour la propriété '{1}'."
	dlcnholdervalue = "运算符'{0}'不支持属性'{1}'。"

	htmlkey       = "message.url"
	htmlvalue     = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>"
	frhtmlvalue   = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong> La maintenance planifiée a démarré. </strong></span></p><p>Des informations importantes sur la maintenance peuvent être trouvées ici: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>"
	dlcnhtmlvalue = "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>"

	//key "about.title" of component "about" in ko/ja/zh-Hans isn't in service but in localbundles
	aboutcom       = "about"
	titlekey       = "about.title"
	titlevalue     = "About"
	frtitlevalue   = "Sur"
	kotitlevalue   = "에 대 한"
	jatitlevalue   = "に関しては"
	dlcntitlevalue = "关于"

	//component "contact" in fr and zh-Hans isn't in service but in localbundles
	contactcom       = "contact"
	messagekey       = "contact.message"
	messagevalue     = "Your contact page."
	frmessagevalue   = "Votre page de contact."
	dlcnmessagevalue = "您的联系人页面。"
)
