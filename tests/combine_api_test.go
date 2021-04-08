/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net/http"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"

	_ "sgtnserver/api/v2/combine"
	"sgtnserver/internal/common"
	"sgtnserver/internal/sgtnerror"
)

func TestGetCombined(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, tt := range []struct {
		combine                   int
		language, region, scope   string
		name, version, components string
		wanted                    string
	}{
		{combine: 1, language: "zh-Hans", region: "CN", scope: "currencies", components: "sunglow", name: Name, version: Version,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"components":[{"productName":"VPE","version":"1.0.0","locale":"zh-Hans","component":"sunglow","messages":{
					            "plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}",
					            "message": "消息",
					            "pagination": "{0}-{1} 个客户，共 {2} 个",
					            "one.arg": "测试一个参数{0}"
					          }}],"pattern":{"localeID":"zh-Hans","language":"zh-Hans","region":"CN","categories":{"currencies":{
					              "ADP" : {
					                "symbol" : "ADP",
					                "displayName" : "安道尔比塞塔",
					                "displayName-count-other" : "安道尔比塞塔"
					              },
					              "AED" : {
					                "symbol" : "AED",
					                "displayName" : "阿联酋迪拉姆",
					                "displayName-count-other" : "阿联酋迪拉姆"
					              },
					              "AFA" : {
					                "symbol" : "AFA",
					                "displayName" : "阿富汗尼 (1927–2002)"
					              },
					              "AFN" : {
					                "symbol" : "AFN",
					                "displayName" : "阿富汗尼",
					                "displayName-count-other" : "阿富汗尼"
					              },
					              "ALK" : {
					                "symbol" : "ALK",
					                "displayName" : "阿尔巴尼亚列克(1946–1965)",
					                "displayName-count-other" : "阿尔巴尼亚列克(1946–1965)"
					              },
					              "ALL" : {
					                "symbol" : "ALL",
					                "displayName" : "阿尔巴尼亚列克",
					                "displayName-count-other" : "阿尔巴尼亚列克"
					              },
					              "AMD" : {
					                "symbol" : "AMD",
					                "displayName" : "亚美尼亚德拉姆",
					                "displayName-count-other" : "亚美尼亚德拉姆"
					              },
					              "ANG" : {
					                "symbol" : "ANG",
					                "displayName" : "荷属安的列斯盾",
					                "displayName-count-other" : "荷属安的列斯盾"
					              },
					              "AOA" : {
					                "symbol" : "AOA",
					                "displayName" : "安哥拉宽扎",
					                "symbol-alt-narrow" : "Kz",
					                "displayName-count-other" : "安哥拉宽扎"
					              },
					              "AOK" : {
					                "symbol" : "AOK",
					                "displayName" : "安哥拉宽扎 (1977–1990)",
					                "displayName-count-other" : "安哥拉宽扎 (1977–1990)"
					              },
					              "AON" : {
					                "symbol" : "AON",
					                "displayName" : "安哥拉新宽扎 (1990–2000)",
					                "displayName-count-other" : "安哥拉新宽扎 (1990–2000)"
					              },
					              "AOR" : {
					                "symbol" : "AOR",
					                "displayName" : "安哥拉重新调整宽扎 (1995–1999)",
					                "displayName-count-other" : "安哥拉重新调整宽扎 (1995–1999)"
					              },
					              "ARA" : {
					                "symbol" : "ARA",
					                "displayName" : "阿根廷奥斯特拉尔",
					                "displayName-count-other" : "阿根廷奥斯特拉尔"
					              },
					              "ARL" : {
					                "symbol" : "ARL",
					                "displayName" : "阿根廷法定比索 (1970–1983)",
					                "displayName-count-other" : "阿根廷法定比索 (1970–1983)"
					              },
					              "ARM" : {
					                "symbol" : "ARM",
					                "displayName" : "阿根廷比索 (1881–1970)",
					                "displayName-count-other" : "阿根廷比索 (1881–1970)"
					              },
					              "ARP" : {
					                "symbol" : "ARP",
					                "displayName" : "阿根廷比索 (1983–1985)",
					                "displayName-count-other" : "阿根廷比索 (1983–1985)"
					              },
					              "ARS" : {
					                "symbol" : "ARS",
					                "displayName" : "阿根廷比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "阿根廷比索"
					              },
					              "ATS" : {
					                "symbol" : "ATS",
					                "displayName" : "奥地利先令",
					                "displayName-count-other" : "奥地利先令"
					              },
					              "AUD" : {
					                "symbol" : "AU$",
					                "displayName" : "澳大利亚元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "澳大利亚元"
					              },
					              "AWG" : {
					                "symbol" : "AWG",
					                "displayName" : "阿鲁巴弗罗林",
					                "displayName-count-other" : "阿鲁巴弗罗林"
					              },
					              "AZM" : {
					                "symbol" : "AZM",
					                "displayName" : "阿塞拜疆马纳特 (1993–2006)",
					                "displayName-count-other" : "阿塞拜疆马纳特 (1993–2006)"
					              },
					              "AZN" : {
					                "symbol" : "AZN",
					                "displayName" : "阿塞拜疆马纳特",
					                "displayName-count-other" : "阿塞拜疆马纳特"
					              },
					              "BAD" : {
					                "symbol" : "BAD",
					                "displayName" : "波士尼亚-赫塞哥维纳第纳尔 (1992–1994)",
					                "displayName-count-other" : "波士尼亚-赫塞哥维纳第纳尔 (1992–1994)"
					              },
					              "BAM" : {
					                "symbol" : "BAM",
					                "displayName" : "波斯尼亚-黑塞哥维那可兑换马克",
					                "symbol-alt-narrow" : "KM",
					                "displayName-count-other" : "波斯尼亚-黑塞哥维那可兑换马克"
					              },
					              "BAN" : {
					                "symbol" : "BAN",
					                "displayName" : "波士尼亚-赫塞哥维纳新第纳尔 (1994–1997)",
					                "displayName-count-other" : "波士尼亚-赫塞哥维纳新第纳尔 (1994–1997)"
					              },
					              "BBD" : {
					                "symbol" : "BBD",
					                "displayName" : "巴巴多斯元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "巴巴多斯元"
					              },
					              "BDT" : {
					                "symbol" : "BDT",
					                "displayName" : "孟加拉塔卡",
					                "symbol-alt-narrow" : "৳",
					                "displayName-count-other" : "孟加拉塔卡"
					              },
					              "BEC" : {
					                "symbol" : "BEC",
					                "displayName" : "比利时法郎（可兑换）",
					                "displayName-count-other" : "比利时法郎（可兑换）"
					              },
					              "BEF" : {
					                "symbol" : "BEF",
					                "displayName" : "比利时法郎",
					                "displayName-count-other" : "比利时法郎"
					              },
					              "BEL" : {
					                "symbol" : "BEL",
					                "displayName" : "比利时法郎（金融）",
					                "displayName-count-other" : "比利时法郎（金融）"
					              },
					              "BGL" : {
					                "symbol" : "BGL",
					                "displayName" : "保加利亚硬列弗",
					                "displayName-count-other" : "保加利亚硬列弗"
					              },
					              "BGM" : {
					                "symbol" : "BGM",
					                "displayName" : "保加利亚社会党列弗",
					                "displayName-count-other" : "保加利亚社会党列弗"
					              },
					              "BGN" : {
					                "symbol" : "BGN",
					                "displayName" : "保加利亚列弗",
					                "displayName-count-other" : "保加利亚新列弗"
					              },
					              "BGO" : {
					                "symbol" : "BGO",
					                "displayName" : "保加利亚列弗 (1879–1952)",
					                "displayName-count-other" : "保加利亚列弗 (1879–1952)"
					              },
					              "BHD" : {
					                "symbol" : "BHD",
					                "displayName" : "巴林第纳尔",
					                "displayName-count-other" : "巴林第纳尔"
					              },
					              "BIF" : {
					                "symbol" : "BIF",
					                "displayName" : "布隆迪法郎",
					                "displayName-count-other" : "布隆迪法郎"
					              },
					              "BMD" : {
					                "symbol" : "BMD",
					                "displayName" : "百慕大元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "百慕大元"
					              },
					              "BND" : {
					                "symbol" : "BND",
					                "displayName" : "文莱元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "文莱元"
					              },
					              "BOB" : {
					                "symbol" : "BOB",
					                "displayName" : "玻利维亚诺",
					                "symbol-alt-narrow" : "Bs",
					                "displayName-count-other" : "玻利维亚诺"
					              },
					              "BOL" : {
					                "symbol" : "BOL",
					                "displayName" : "玻利维亚诺 (1863–1963)",
					                "displayName-count-other" : "玻利维亚诺 (1863–1963)"
					              },
					              "BOP" : {
					                "symbol" : "BOP",
					                "displayName" : "玻利维亚比索",
					                "displayName-count-other" : "玻利维亚比索"
					              },
					              "BOV" : {
					                "symbol" : "BOV",
					                "displayName" : "玻利维亚 Mvdol（资金）",
					                "displayName-count-other" : "玻利维亚 Mvdol（资金）"
					              },
					              "BRB" : {
					                "symbol" : "BRB",
					                "displayName" : "巴西新克鲁赛罗 (1967–1986)",
					                "displayName-count-other" : "巴西新克鲁赛罗 (1967–1986)"
					              },
					              "BRC" : {
					                "symbol" : "BRC",
					                "displayName" : "巴西克鲁扎多 (1986–1989)",
					                "displayName-count-other" : "巴西克鲁扎多 (1986–1989)"
					              },
					              "BRE" : {
					                "symbol" : "BRE",
					                "displayName" : "巴西克鲁塞罗 (1990–1993)",
					                "displayName-count-other" : "巴西克鲁塞罗 (1990–1993)"
					              },
					              "BRL" : {
					                "symbol" : "R$",
					                "displayName" : "巴西雷亚尔",
					                "symbol-alt-narrow" : "R$",
					                "displayName-count-other" : "巴西雷亚尔"
					              },
					              "BRN" : {
					                "symbol" : "BRN",
					                "displayName" : "巴西新克鲁扎多 (1989–1990)",
					                "displayName-count-other" : "巴西新克鲁扎多 (1989–1990)"
					              },
					              "BRR" : {
					                "symbol" : "BRR",
					                "displayName" : "巴西克鲁塞罗 (1993–1994)",
					                "displayName-count-other" : "巴西克鲁塞罗 (1993–1994)"
					              },
					              "BRZ" : {
					                "symbol" : "BRZ",
					                "displayName" : "巴西克鲁塞罗 (1942–1967)",
					                "displayName-count-other" : "巴西克鲁塞罗 (1942–1967)"
					              },
					              "BSD" : {
					                "symbol" : "BSD",
					                "displayName" : "巴哈马元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "巴哈马元"
					              },
					              "BTN" : {
					                "symbol" : "BTN",
					                "displayName" : "不丹努尔特鲁姆",
					                "displayName-count-other" : "不丹努尔特鲁姆"
					              },
					              "BUK" : {
					                "symbol" : "BUK",
					                "displayName" : "缅元"
					              },
					              "BWP" : {
					                "symbol" : "BWP",
					                "displayName" : "博茨瓦纳普拉",
					                "symbol-alt-narrow" : "P",
					                "displayName-count-other" : "博茨瓦纳普拉"
					              },
					              "BYB" : {
					                "symbol" : "BYB",
					                "displayName" : "白俄罗斯新卢布 (1994–1999)",
					                "displayName-count-other" : "白俄罗斯新卢布 (1994–1999)"
					              },
					              "BYN" : {
					                "symbol" : "BYN",
					                "displayName" : "白俄罗斯卢布",
					                "symbol-alt-narrow" : "р.",
					                "displayName-count-other" : "白俄罗斯卢布"
					              },
					              "BYR" : {
					                "symbol" : "BYR",
					                "displayName" : "白俄罗斯卢布 (2000–2016)",
					                "displayName-count-other" : "白俄罗斯卢布 (2000–2016)"
					              },
					              "BZD" : {
					                "symbol" : "BZD",
					                "displayName" : "伯利兹元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "伯利兹元"
					              },
					              "CAD" : {
					                "symbol" : "CA$",
					                "displayName" : "加拿大元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "加拿大元"
					              },
					              "CDF" : {
					                "symbol" : "CDF",
					                "displayName" : "刚果法郎",
					                "displayName-count-other" : "刚果法郎"
					              },
					              "CHE" : {
					                "symbol" : "CHE",
					                "displayName" : "欧元 (WIR)",
					                "displayName-count-other" : "欧元 (WIR)"
					              },
					              "CHF" : {
					                "symbol" : "CHF",
					                "displayName" : "瑞士法郎",
					                "displayName-count-other" : "瑞士法郎"
					              },
					              "CHW" : {
					                "symbol" : "CHW",
					                "displayName" : "法郎 (WIR)",
					                "displayName-count-other" : "法郎 (WIR)"
					              },
					              "CLE" : {
					                "symbol" : "CLE",
					                "displayName" : "智利埃斯库多",
					                "displayName-count-other" : "智利埃斯库多"
					              },
					              "CLF" : {
					                "symbol" : "CLF",
					                "displayName" : "智利（资金）",
					                "displayName-count-other" : "智利（资金）"
					              },
					              "CLP" : {
					                "symbol" : "CLP",
					                "displayName" : "智利比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "智利比索"
					              },
					              "CNH" : {
					                "symbol" : "CNH",
					                "displayName" : "人民币（离岸）",
					                "displayName-count-other" : "人民币（离岸）"
					              },
					              "CNX" : {
					                "symbol" : "CNX",
					                "displayName" : "CNX"
					              },
					              "CNY" : {
					                "symbol" : "￥",
					                "displayName" : "人民币",
					                "symbol-alt-narrow" : "¥",
					                "displayName-count-other" : "人民币"
					              },
					              "COP" : {
					                "symbol" : "COP",
					                "displayName" : "哥伦比亚比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "哥伦比亚比索"
					              },
					              "COU" : {
					                "symbol" : "COU",
					                "displayName" : "哥伦比亚币",
					                "displayName-count-other" : "哥伦比亚币"
					              },
					              "CRC" : {
					                "symbol" : "CRC",
					                "displayName" : "哥斯达黎加科朗",
					                "symbol-alt-narrow" : "₡",
					                "displayName-count-other" : "哥斯达黎加科朗"
					              },
					              "CSD" : {
					                "symbol" : "CSD",
					                "displayName" : "旧塞尔维亚第纳尔",
					                "displayName-count-other" : "旧塞尔维亚第纳尔"
					              },
					              "CSK" : {
					                "symbol" : "CSK",
					                "displayName" : "捷克硬克朗",
					                "displayName-count-other" : "捷克硬克朗"
					              },
					              "CUC" : {
					                "symbol" : "CUC",
					                "displayName" : "古巴可兑换比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "古巴可兑换比索"
					              },
					              "CUP" : {
					                "symbol" : "CUP",
					                "displayName" : "古巴比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "古巴比索"
					              },
					              "CVE" : {
					                "symbol" : "CVE",
					                "displayName" : "佛得角埃斯库多",
					                "displayName-count-other" : "佛得角埃斯库多"
					              },
					              "CYP" : {
					                "symbol" : "CYP",
					                "displayName" : "塞浦路斯镑",
					                "displayName-count-other" : "塞浦路斯镑"
					              },
					              "CZK" : {
					                "symbol" : "CZK",
					                "displayName" : "捷克克朗",
					                "symbol-alt-narrow" : "Kč",
					                "displayName-count-other" : "捷克克朗"
					              },
					              "DDM" : {
					                "symbol" : "DDM",
					                "displayName" : "东德奥斯特马克",
					                "displayName-count-other" : "东德奥斯特马克"
					              },
					              "DEM" : {
					                "symbol" : "DEM",
					                "displayName" : "德国马克",
					                "displayName-count-other" : "德国马克"
					              },
					              "DJF" : {
					                "symbol" : "DJF",
					                "displayName" : "吉布提法郎",
					                "displayName-count-other" : "吉布提法郎"
					              },
					              "DKK" : {
					                "symbol" : "DKK",
					                "displayName" : "丹麦克朗",
					                "symbol-alt-narrow" : "kr",
					                "displayName-count-other" : "丹麦克朗"
					              },
					              "DOP" : {
					                "symbol" : "DOP",
					                "displayName" : "多米尼加比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "多米尼加比索"
					              },
					              "DZD" : {
					                "symbol" : "DZD",
					                "displayName" : "阿尔及利亚第纳尔",
					                "displayName-count-other" : "阿尔及利亚第纳尔"
					              },
					              "ECS" : {
					                "symbol" : "ECS",
					                "displayName" : "厄瓜多尔苏克雷",
					                "displayName-count-other" : "厄瓜多尔苏克雷"
					              },
					              "ECV" : {
					                "symbol" : "ECV",
					                "displayName" : "厄瓜多尔 (UVC)",
					                "displayName-count-other" : "厄瓜多尔 (UVC)"
					              },
					              "EEK" : {
					                "symbol" : "EEK",
					                "displayName" : "爱沙尼亚克朗",
					                "displayName-count-other" : "爱沙尼亚克朗"
					              },
					              "EGP" : {
					                "symbol" : "EGP",
					                "displayName" : "埃及镑",
					                "symbol-alt-narrow" : "E£",
					                "displayName-count-other" : "埃及镑"
					              },
					              "ERN" : {
					                "symbol" : "ERN",
					                "displayName" : "厄立特里亚纳克法",
					                "displayName-count-other" : "厄立特里亚纳克法"
					              },
					              "ESA" : {
					                "symbol" : "ESA",
					                "displayName" : "西班牙比塞塔（帐户 A）",
					                "displayName-count-other" : "西班牙比塞塔（帐户 A）"
					              },
					              "ESB" : {
					                "symbol" : "ESB",
					                "displayName" : "西班牙比塞塔（兑换帐户）",
					                "displayName-count-other" : "西班牙比塞塔（兑换帐户）"
					              },
					              "ESP" : {
					                "symbol" : "ESP",
					                "displayName" : "西班牙比塞塔",
					                "symbol-alt-narrow" : "₧",
					                "displayName-count-other" : "西班牙比塞塔"
					              },
					              "ETB" : {
					                "symbol" : "ETB",
					                "displayName" : "埃塞俄比亚比尔",
					                "displayName-count-other" : "埃塞俄比亚比尔"
					              },
					              "EUR" : {
					                "symbol" : "€",
					                "displayName" : "欧元",
					                "symbol-alt-narrow" : "€",
					                "displayName-count-other" : "欧元"
					              },
					              "FIM" : {
					                "symbol" : "FIM",
					                "displayName" : "芬兰马克",
					                "displayName-count-other" : "芬兰马克"
					              },
					              "FJD" : {
					                "symbol" : "FJD",
					                "displayName" : "斐济元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "斐济元"
					              },
					              "FKP" : {
					                "symbol" : "FKP",
					                "displayName" : "福克兰群岛镑",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "福克兰群岛镑"
					              },
					              "FRF" : {
					                "symbol" : "FRF",
					                "displayName" : "法国法郎",
					                "displayName-count-other" : "法国法郎"
					              },
					              "GBP" : {
					                "symbol" : "£",
					                "displayName" : "英镑",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "英镑"
					              },
					              "GEK" : {
					                "symbol" : "GEK",
					                "displayName" : "乔治亚库蓬拉瑞特",
					                "displayName-count-other" : "乔治亚库蓬拉瑞特"
					              },
					              "GEL" : {
					                "symbol" : "GEL",
					                "symbol-alt-variant" : "₾",
					                "displayName" : "格鲁吉亚拉里",
					                "symbol-alt-narrow" : "₾",
					                "displayName-count-other" : "格鲁吉亚拉里"
					              },
					              "GHC" : {
					                "symbol" : "GHC",
					                "displayName" : "加纳塞第",
					                "displayName-count-other" : "加纳塞第"
					              },
					              "GHS" : {
					                "symbol" : "GHS",
					                "displayName" : "加纳塞地",
					                "displayName-count-other" : "加纳塞地"
					              },
					              "GIP" : {
					                "symbol" : "GIP",
					                "displayName" : "直布罗陀镑",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "直布罗陀镑"
					              },
					              "GMD" : {
					                "symbol" : "GMD",
					                "displayName" : "冈比亚达拉西",
					                "displayName-count-other" : "冈比亚达拉西"
					              },
					              "GNF" : {
					                "symbol" : "GNF",
					                "displayName" : "几内亚法郎",
					                "symbol-alt-narrow" : "FG",
					                "displayName-count-other" : "几内亚法郎"
					              },
					              "GNS" : {
					                "symbol" : "GNS",
					                "displayName" : "几内亚西里",
					                "displayName-count-other" : "几内亚西里"
					              },
					              "GQE" : {
					                "symbol" : "GQE",
					                "displayName" : "赤道几内亚埃奎勒",
					                "displayName-count-other" : "赤道几内亚埃奎勒"
					              },
					              "GRD" : {
					                "symbol" : "GRD",
					                "displayName" : "希腊德拉克马",
					                "displayName-count-other" : "希腊德拉克马"
					              },
					              "GTQ" : {
					                "symbol" : "GTQ",
					                "displayName" : "危地马拉格查尔",
					                "symbol-alt-narrow" : "Q",
					                "displayName-count-other" : "危地马拉格查尔"
					              },
					              "GWE" : {
					                "symbol" : "GWE",
					                "displayName" : "葡萄牙几内亚埃斯库多",
					                "displayName-count-other" : "葡萄牙几内亚埃斯库多"
					              },
					              "GWP" : {
					                "symbol" : "GWP",
					                "displayName" : "几内亚比绍比索",
					                "displayName-count-other" : "几内亚比绍比索"
					              },
					              "GYD" : {
					                "symbol" : "GYD",
					                "displayName" : "圭亚那元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "圭亚那元"
					              },
					              "HKD" : {
					                "symbol" : "HK$",
					                "displayName" : "港元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "港元"
					              },
					              "HNL" : {
					                "symbol" : "HNL",
					                "displayName" : "洪都拉斯伦皮拉",
					                "symbol-alt-narrow" : "L",
					                "displayName-count-other" : "洪都拉斯伦皮拉"
					              },
					              "HRD" : {
					                "symbol" : "HRD",
					                "displayName" : "克罗地亚第纳尔",
					                "displayName-count-other" : "克罗地亚第纳尔"
					              },
					              "HRK" : {
					                "symbol" : "HRK",
					                "displayName" : "克罗地亚库纳",
					                "symbol-alt-narrow" : "kn",
					                "displayName-count-other" : "克罗地亚库纳"
					              },
					              "HTG" : {
					                "symbol" : "HTG",
					                "displayName" : "海地古德",
					                "displayName-count-other" : "海地古德"
					              },
					              "HUF" : {
					                "symbol" : "HUF",
					                "displayName" : "匈牙利福林",
					                "symbol-alt-narrow" : "Ft",
					                "displayName-count-other" : "匈牙利福林"
					              },
					              "IDR" : {
					                "symbol" : "IDR",
					                "displayName" : "印度尼西亚盾",
					                "symbol-alt-narrow" : "Rp",
					                "displayName-count-other" : "印度尼西亚盾"
					              },
					              "IEP" : {
					                "symbol" : "IEP",
					                "displayName" : "爱尔兰镑",
					                "displayName-count-other" : "爱尔兰镑"
					              },
					              "ILP" : {
					                "symbol" : "ILP",
					                "displayName" : "以色列镑",
					                "displayName-count-other" : "以色列镑"
					              },
					              "ILR" : {
					                "symbol" : "ILS",
					                "displayName" : "以色列谢克尔(1980–1985)",
					                "displayName-count-other" : "以色列谢克尔(1980–1985)"
					              },
					              "ILS" : {
					                "symbol" : "₪",
					                "displayName" : "以色列新谢克尔",
					                "symbol-alt-narrow" : "₪",
					                "displayName-count-other" : "以色列新谢克尔"
					              },
					              "INR" : {
					                "symbol" : "₹",
					                "displayName" : "印度卢比",
					                "symbol-alt-narrow" : "₹",
					                "displayName-count-other" : "印度卢比"
					              },
					              "IQD" : {
					                "symbol" : "IQD",
					                "displayName" : "伊拉克第纳尔",
					                "displayName-count-other" : "伊拉克第纳尔"
					              },
					              "IRR" : {
					                "symbol" : "IRR",
					                "displayName" : "伊朗里亚尔",
					                "displayName-count-other" : "伊朗里亚尔"
					              },
					              "ISJ" : {
					                "symbol" : "ISJ",
					                "displayName" : "冰岛克朗(1918–1981)",
					                "displayName-count-other" : "冰岛克朗(1918–1981)"
					              },
					              "ISK" : {
					                "symbol" : "ISK",
					                "displayName" : "冰岛克朗",
					                "symbol-alt-narrow" : "kr",
					                "displayName-count-other" : "冰岛克朗"
					              },
					              "ITL" : {
					                "symbol" : "ITL",
					                "displayName" : "意大利里拉",
					                "displayName-count-other" : "意大利里拉"
					              },
					              "JMD" : {
					                "symbol" : "JMD",
					                "displayName" : "牙买加元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "牙买加元"
					              },
					              "JOD" : {
					                "symbol" : "JOD",
					                "displayName" : "约旦第纳尔",
					                "displayName-count-other" : "约旦第纳尔"
					              },
					              "JPY" : {
					                "symbol" : "JP¥",
					                "displayName" : "日元",
					                "symbol-alt-narrow" : "¥",
					                "displayName-count-other" : "日元"
					              },
					              "KES" : {
					                "symbol" : "KES",
					                "displayName" : "肯尼亚先令",
					                "displayName-count-other" : "肯尼亚先令"
					              },
					              "KGS" : {
					                "symbol" : "KGS",
					                "displayName" : "吉尔吉斯斯坦索姆",
					                "displayName-count-other" : "吉尔吉斯斯坦索姆"
					              },
					              "KHR" : {
					                "symbol" : "KHR",
					                "displayName" : "柬埔寨瑞尔",
					                "symbol-alt-narrow" : "៛",
					                "displayName-count-other" : "柬埔寨瑞尔"
					              },
					              "KMF" : {
					                "symbol" : "KMF",
					                "displayName" : "科摩罗法郎",
					                "symbol-alt-narrow" : "CF",
					                "displayName-count-other" : "科摩罗法郎"
					              },
					              "KPW" : {
					                "symbol" : "KPW",
					                "displayName" : "朝鲜元",
					                "symbol-alt-narrow" : "₩",
					                "displayName-count-other" : "朝鲜元"
					              },
					              "KRH" : {
					                "symbol" : "KRH",
					                "displayName" : "韩元 (1953–1962)"
					              },
					              "KRO" : {
					                "symbol" : "KRO",
					                "displayName" : "韩元 (1945–1953)"
					              },
					              "KRW" : {
					                "symbol" : "￦",
					                "displayName" : "韩元",
					                "symbol-alt-narrow" : "₩",
					                "displayName-count-other" : "韩元"
					              },
					              "KWD" : {
					                "symbol" : "KWD",
					                "displayName" : "科威特第纳尔",
					                "displayName-count-other" : "科威特第纳尔"
					              },
					              "KYD" : {
					                "symbol" : "KYD",
					                "displayName" : "开曼元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "开曼元"
					              },
					              "KZT" : {
					                "symbol" : "KZT",
					                "displayName" : "哈萨克斯坦坚戈",
					                "symbol-alt-narrow" : "₸",
					                "displayName-count-other" : "哈萨克斯坦坚戈"
					              },
					              "LAK" : {
					                "symbol" : "LAK",
					                "displayName" : "老挝基普",
					                "symbol-alt-narrow" : "₭",
					                "displayName-count-other" : "老挝基普"
					              },
					              "LBP" : {
					                "symbol" : "LBP",
					                "displayName" : "黎巴嫩镑",
					                "symbol-alt-narrow" : "L£",
					                "displayName-count-other" : "黎巴嫩镑"
					              },
					              "LKR" : {
					                "symbol" : "LKR",
					                "displayName" : "斯里兰卡卢比",
					                "symbol-alt-narrow" : "Rs",
					                "displayName-count-other" : "斯里兰卡卢比"
					              },
					              "LRD" : {
					                "symbol" : "LRD",
					                "displayName" : "利比里亚元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "利比里亚元"
					              },
					              "LSL" : {
					                "symbol" : "LSL",
					                "displayName" : "莱索托洛蒂",
					                "displayName-count-other" : "莱索托洛蒂"
					              },
					              "LTL" : {
					                "symbol" : "LTL",
					                "displayName" : "立陶宛立特",
					                "symbol-alt-narrow" : "Lt",
					                "displayName-count-other" : "立陶宛立特"
					              },
					              "LTT" : {
					                "symbol" : "LTT",
					                "displayName" : "立陶宛塔咯呐司",
					                "displayName-count-other" : "立陶宛塔咯呐司"
					              },
					              "LUC" : {
					                "symbol" : "LUC",
					                "displayName" : "卢森堡可兑换法郎",
					                "displayName-count-other" : "卢森堡可兑换法郎"
					              },
					              "LUF" : {
					                "symbol" : "LUF",
					                "displayName" : "卢森堡法郎",
					                "displayName-count-other" : "卢森堡法郎"
					              },
					              "LUL" : {
					                "symbol" : "LUL",
					                "displayName" : "卢森堡金融法郎",
					                "displayName-count-other" : "卢森堡金融法郎"
					              },
					              "LVL" : {
					                "symbol" : "LVL",
					                "displayName" : "拉脱维亚拉特",
					                "symbol-alt-narrow" : "Ls",
					                "displayName-count-other" : "拉脱维亚拉特"
					              },
					              "LVR" : {
					                "symbol" : "LVR",
					                "displayName" : "拉脱维亚卢布",
					                "displayName-count-other" : "拉脱维亚卢布"
					              },
					              "LYD" : {
					                "symbol" : "LYD",
					                "displayName" : "利比亚第纳尔",
					                "displayName-count-other" : "利比亚第纳尔"
					              },
					              "MAD" : {
					                "symbol" : "MAD",
					                "displayName" : "摩洛哥迪拉姆",
					                "displayName-count-other" : "摩洛哥迪拉姆"
					              },
					              "MAF" : {
					                "symbol" : "MAF",
					                "displayName" : "摩洛哥法郎",
					                "displayName-count-other" : "摩洛哥法郎"
					              },
					              "MCF" : {
					                "symbol" : "MCF",
					                "displayName" : "摩纳哥法郎",
					                "displayName-count-other" : "摩纳哥法郎"
					              },
					              "MDC" : {
					                "symbol" : "MDC",
					                "displayName" : "摩尔多瓦库邦",
					                "displayName-count-other" : "摩尔多瓦库邦"
					              },
					              "MDL" : {
					                "symbol" : "MDL",
					                "displayName" : "摩尔多瓦列伊",
					                "displayName-count-other" : "摩尔多瓦列伊"
					              },
					              "MGA" : {
					                "symbol" : "MGA",
					                "displayName" : "马达加斯加阿里亚里",
					                "symbol-alt-narrow" : "Ar",
					                "displayName-count-other" : "马达加斯加阿里亚里"
					              },
					              "MGF" : {
					                "symbol" : "MGF",
					                "displayName" : "马达加斯加法郎",
					                "displayName-count-other" : "马达加斯加法郎"
					              },
					              "MKD" : {
					                "symbol" : "MKD",
					                "displayName" : "马其顿第纳尔",
					                "displayName-count-other" : "马其顿第纳尔"
					              },
					              "MKN" : {
					                "symbol" : "MKN",
					                "displayName" : "马其顿第纳尔 (1992–1993)",
					                "displayName-count-other" : "马其顿第纳尔 (1992–1993)"
					              },
					              "MLF" : {
					                "symbol" : "MLF",
					                "displayName" : "马里法郎",
					                "displayName-count-other" : "马里法郎"
					              },
					              "MMK" : {
					                "symbol" : "MMK",
					                "displayName" : "缅甸元",
					                "symbol-alt-narrow" : "K",
					                "displayName-count-other" : "缅甸元"
					              },
					              "MNT" : {
					                "symbol" : "MNT",
					                "displayName" : "蒙古图格里克",
					                "symbol-alt-narrow" : "₮",
					                "displayName-count-other" : "蒙古图格里克"
					              },
					              "MOP" : {
					                "symbol" : "MOP",
					                "displayName" : "澳门币",
					                "displayName-count-other" : "澳门元"
					              },
					              "MRO" : {
					                "symbol" : "MRO",
					                "displayName" : "毛里塔尼亚乌吉亚",
					                "displayName-count-other" : "毛里塔尼亚乌吉亚"
					              },
					              "MTL" : {
					                "symbol" : "MTL",
					                "displayName" : "马耳他里拉",
					                "displayName-count-other" : "马耳他里拉"
					              },
					              "MTP" : {
					                "symbol" : "MTP",
					                "displayName" : "马耳他镑",
					                "displayName-count-other" : "马耳他镑"
					              },
					              "MUR" : {
					                "symbol" : "MUR",
					                "displayName" : "毛里求斯卢比",
					                "symbol-alt-narrow" : "Rs",
					                "displayName-count-other" : "毛里求斯卢比"
					              },
					              "MVP" : {
					                "symbol" : "MVP",
					                "displayName" : "马尔代夫卢比(1947–1981)",
					                "displayName-count-other" : "马尔代夫卢比(1947–1981)"
					              },
					              "MVR" : {
					                "symbol" : "MVR",
					                "displayName" : "马尔代夫卢菲亚",
					                "displayName-count-other" : "马尔代夫卢菲亚"
					              },
					              "MWK" : {
					                "symbol" : "MWK",
					                "displayName" : "马拉维克瓦查",
					                "displayName-count-other" : "马拉维克瓦查"
					              },
					              "MXN" : {
					                "symbol" : "MX$",
					                "displayName" : "墨西哥比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "墨西哥比索"
					              },
					              "MXP" : {
					                "symbol" : "MXP",
					                "displayName" : "墨西哥银比索 (1861–1992)",
					                "displayName-count-other" : "墨西哥银比索 (1861–1992)"
					              },
					              "MXV" : {
					                "symbol" : "MXV",
					                "displayName" : "墨西哥（资金）",
					                "displayName-count-other" : "墨西哥（资金）"
					              },
					              "MYR" : {
					                "symbol" : "MYR",
					                "displayName" : "马来西亚林吉特",
					                "symbol-alt-narrow" : "RM",
					                "displayName-count-other" : "马来西亚林吉特"
					              },
					              "MZE" : {
					                "symbol" : "MZE",
					                "displayName" : "莫桑比克埃斯库多",
					                "displayName-count-other" : "莫桑比克埃斯库多"
					              },
					              "MZM" : {
					                "symbol" : "MZM",
					                "displayName" : "旧莫桑比克美提卡",
					                "displayName-count-other" : "旧莫桑比克美提卡"
					              },
					              "MZN" : {
					                "symbol" : "MZN",
					                "displayName" : "莫桑比克美提卡",
					                "displayName-count-other" : "莫桑比克美提卡"
					              },
					              "NAD" : {
					                "symbol" : "NAD",
					                "displayName" : "纳米比亚元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "纳米比亚元"
					              },
					              "NGN" : {
					                "symbol" : "NGN",
					                "displayName" : "尼日利亚奈拉",
					                "symbol-alt-narrow" : "₦",
					                "displayName-count-other" : "尼日利亚奈拉"
					              },
					              "NIC" : {
					                "symbol" : "NIC",
					                "displayName" : "尼加拉瓜科多巴 (1988–1991)",
					                "displayName-count-other" : "尼加拉瓜科多巴 (1988–1991)"
					              },
					              "NIO" : {
					                "symbol" : "NIO",
					                "displayName" : "尼加拉瓜科多巴",
					                "symbol-alt-narrow" : "C$",
					                "displayName-count-other" : "尼加拉瓜金科多巴"
					              },
					              "NLG" : {
					                "symbol" : "NLG",
					                "displayName" : "荷兰盾",
					                "displayName-count-other" : "荷兰盾"
					              },
					              "NOK" : {
					                "symbol" : "NOK",
					                "displayName" : "挪威克朗",
					                "symbol-alt-narrow" : "kr",
					                "displayName-count-other" : "挪威克朗"
					              },
					              "NPR" : {
					                "symbol" : "NPR",
					                "displayName" : "尼泊尔卢比",
					                "symbol-alt-narrow" : "Rs",
					                "displayName-count-other" : "尼泊尔卢比"
					              },
					              "NZD" : {
					                "symbol" : "NZ$",
					                "displayName" : "新西兰元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "新西兰元"
					              },
					              "OMR" : {
					                "symbol" : "OMR",
					                "displayName" : "阿曼里亚尔",
					                "displayName-count-other" : "阿曼里亚尔"
					              },
					              "PAB" : {
					                "symbol" : "PAB",
					                "displayName" : "巴拿马巴波亚",
					                "displayName-count-other" : "巴拿马巴波亚"
					              },
					              "PEI" : {
					                "symbol" : "PEI",
					                "displayName" : "秘鲁印第",
					                "displayName-count-other" : "秘鲁印第"
					              },
					              "PEN" : {
					                "symbol" : "PEN",
					                "displayName" : "秘鲁索尔",
					                "displayName-count-other" : "秘鲁索尔"
					              },
					              "PES" : {
					                "symbol" : "PES",
					                "displayName" : "秘鲁索尔 (1863–1965)",
					                "displayName-count-other" : "秘鲁索尔 (1863–1965)"
					              },
					              "PGK" : {
					                "symbol" : "PGK",
					                "displayName" : "巴布亚新几内亚基那",
					                "displayName-count-other" : "巴布亚新几内亚基那"
					              },
					              "PHP" : {
					                "symbol" : "PHP",
					                "displayName" : "菲律宾比索",
					                "symbol-alt-narrow" : "₱",
					                "displayName-count-other" : "菲律宾比索"
					              },
					              "PKR" : {
					                "symbol" : "PKR",
					                "displayName" : "巴基斯坦卢比",
					                "symbol-alt-narrow" : "Rs",
					                "displayName-count-other" : "巴基斯坦卢比"
					              },
					              "PLN" : {
					                "symbol" : "PLN",
					                "displayName" : "波兰兹罗提",
					                "symbol-alt-narrow" : "zł",
					                "displayName-count-other" : "波兰兹罗提"
					              },
					              "PLZ" : {
					                "symbol" : "PLZ",
					                "displayName" : "波兰兹罗提 (1950–1995)",
					                "displayName-count-other" : "波兰兹罗提 (1950–1995)"
					              },
					              "PTE" : {
					                "symbol" : "PTE",
					                "displayName" : "葡萄牙埃斯库多",
					                "displayName-count-other" : "葡萄牙埃斯库多"
					              },
					              "PYG" : {
					                "symbol" : "PYG",
					                "displayName" : "巴拉圭瓜拉尼",
					                "symbol-alt-narrow" : "₲",
					                "displayName-count-other" : "巴拉圭瓜拉尼"
					              },
					              "QAR" : {
					                "symbol" : "QAR",
					                "displayName" : "卡塔尔里亚尔",
					                "displayName-count-other" : "卡塔尔里亚尔"
					              },
					              "RHD" : {
					                "symbol" : "RHD",
					                "displayName" : "罗得西亚元",
					                "displayName-count-other" : "罗得西亚元"
					              },
					              "ROL" : {
					                "symbol" : "ROL",
					                "displayName" : "旧罗马尼亚列伊",
					                "displayName-count-other" : "旧罗马尼亚列伊"
					              },
					              "RON" : {
					                "symbol" : "RON",
					                "displayName" : "罗马尼亚列伊",
					                "symbol-alt-narrow" : "lei",
					                "displayName-count-other" : "罗马尼亚列伊"
					              },
					              "RSD" : {
					                "symbol" : "RSD",
					                "displayName" : "塞尔维亚第纳尔",
					                "displayName-count-other" : "塞尔维亚第纳尔"
					              },
					              "RUB" : {
					                "symbol" : "RUB",
					                "displayName" : "俄罗斯卢布",
					                "symbol-alt-narrow" : "₽",
					                "displayName-count-other" : "俄罗斯卢布"
					              },
					              "RUR" : {
					                "symbol" : "RUR",
					                "displayName" : "俄国卢布 (1991–1998)",
					                "symbol-alt-narrow" : "р.",
					                "displayName-count-other" : "俄国卢布 (1991–1998)"
					              },
					              "RWF" : {
					                "symbol" : "RWF",
					                "displayName" : "卢旺达法郎",
					                "symbol-alt-narrow" : "RF",
					                "displayName-count-other" : "卢旺达法郎"
					              },
					              "SAR" : {
					                "symbol" : "SAR",
					                "displayName" : "沙特里亚尔",
					                "displayName-count-other" : "沙特里亚尔"
					              },
					              "SBD" : {
					                "symbol" : "SBD",
					                "displayName" : "所罗门群岛元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "所罗门群岛元"
					              },
					              "SCR" : {
					                "symbol" : "SCR",
					                "displayName" : "塞舌尔卢比",
					                "displayName-count-other" : "塞舌尔卢比"
					              },
					              "SDD" : {
					                "symbol" : "SDD",
					                "displayName" : "苏丹第纳尔 (1992–2007)",
					                "displayName-count-other" : "苏丹第纳尔 (1992–2007)"
					              },
					              "SDG" : {
					                "symbol" : "SDG",
					                "displayName" : "苏丹镑",
					                "displayName-count-other" : "苏丹镑"
					              },
					              "SDP" : {
					                "symbol" : "SDP",
					                "displayName" : "旧苏丹镑",
					                "displayName-count-other" : "旧苏丹镑"
					              },
					              "SEK" : {
					                "symbol" : "SEK",
					                "displayName" : "瑞典克朗",
					                "symbol-alt-narrow" : "kr",
					                "displayName-count-other" : "瑞典克朗"
					              },
					              "SGD" : {
					                "symbol" : "SGD",
					                "displayName" : "新加坡元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "新加坡元"
					              },
					              "SHP" : {
					                "symbol" : "SHP",
					                "displayName" : "圣赫勒拿群岛磅",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "圣赫勒拿群岛磅"
					              },
					              "SIT" : {
					                "symbol" : "SIT",
					                "displayName" : "斯洛文尼亚托拉尔",
					                "displayName-count-other" : "斯洛文尼亚托拉尔"
					              },
					              "SKK" : {
					                "symbol" : "SKK",
					                "displayName" : "斯洛伐克克朗",
					                "displayName-count-other" : "斯洛伐克克朗"
					              },
					              "SLL" : {
					                "symbol" : "SLL",
					                "displayName" : "塞拉利昂利昂",
					                "displayName-count-other" : "塞拉利昂利昂"
					              },
					              "SOS" : {
					                "symbol" : "SOS",
					                "displayName" : "索马里先令",
					                "displayName-count-other" : "索马里先令"
					              },
					              "SRD" : {
					                "symbol" : "SRD",
					                "displayName" : "苏里南元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "苏里南元"
					              },
					              "SRG" : {
					                "symbol" : "SRG",
					                "displayName" : "苏里南盾",
					                "displayName-count-other" : "苏里南盾"
					              },
					              "SSP" : {
					                "symbol" : "SSP",
					                "displayName" : "南苏丹镑",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "南苏丹镑"
					              },
					              "STD" : {
					                "symbol" : "STD",
					                "displayName" : "圣多美和普林西比多布拉",
					                "symbol-alt-narrow" : "Db",
					                "displayName-count-other" : "圣多美和普林西比多布拉"
					              },
					              "STN" : {
					                "symbol" : "STN",
					                "displayName" : "STN"
					              },
					              "SUR" : {
					                "symbol" : "SUR",
					                "displayName" : "苏联卢布",
					                "displayName-count-other" : "苏联卢布"
					              },
					              "SVC" : {
					                "symbol" : "SVC",
					                "displayName" : "萨尔瓦多科朗",
					                "displayName-count-other" : "萨尔瓦多科朗"
					              },
					              "SYP" : {
					                "symbol" : "SYP",
					                "displayName" : "叙利亚镑",
					                "symbol-alt-narrow" : "£",
					                "displayName-count-other" : "叙利亚镑"
					              },
					              "SZL" : {
					                "symbol" : "SZL",
					                "displayName" : "斯威士兰里兰吉尼",
					                "displayName-count-other" : "斯威士兰里兰吉尼"
					              },
					              "THB" : {
					                "symbol" : "THB",
					                "displayName" : "泰铢",
					                "symbol-alt-narrow" : "฿",
					                "displayName-count-other" : "泰铢"
					              },
					              "TJR" : {
					                "symbol" : "TJR",
					                "displayName" : "塔吉克斯坦卢布",
					                "displayName-count-other" : "塔吉克斯坦卢布"
					              },
					              "TJS" : {
					                "symbol" : "TJS",
					                "displayName" : "塔吉克斯坦索莫尼",
					                "displayName-count-other" : "塔吉克斯坦索莫尼"
					              },
					              "TMM" : {
					                "symbol" : "TMM",
					                "displayName" : "土库曼斯坦马纳特 (1993–2009)",
					                "displayName-count-other" : "土库曼斯坦马纳特 (1993–2009)"
					              },
					              "TMT" : {
					                "symbol" : "TMT",
					                "displayName" : "土库曼斯坦马纳特",
					                "displayName-count-other" : "土库曼斯坦马纳特"
					              },
					              "TND" : {
					                "symbol" : "TND",
					                "displayName" : "突尼斯第纳尔",
					                "displayName-count-other" : "突尼斯第纳尔"
					              },
					              "TOP" : {
					                "symbol" : "TOP",
					                "displayName" : "汤加潘加",
					                "symbol-alt-narrow" : "T$",
					                "displayName-count-other" : "汤加潘加"
					              },
					              "TPE" : {
					                "symbol" : "TPE",
					                "displayName" : "帝汶埃斯库多"
					              },
					              "TRL" : {
					                "symbol" : "TRL",
					                "displayName" : "土耳其里拉 (1922–2005)",
					                "displayName-count-other" : "土耳其里拉 (1922–2005)"
					              },
					              "TRY" : {
					                "symbol" : "TRY",
					                "symbol-alt-variant" : "TL",
					                "displayName" : "土耳其里拉",
					                "symbol-alt-narrow" : "₺",
					                "displayName-count-other" : "土耳其里拉"
					              },
					              "TTD" : {
					                "symbol" : "TTD",
					                "displayName" : "特立尼达和多巴哥元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "特立尼达和多巴哥元"
					              },
					              "TWD" : {
					                "symbol" : "NT$",
					                "displayName" : "新台币",
					                "symbol-alt-narrow" : "NT$",
					                "displayName-count-other" : "新台币"
					              },
					              "TZS" : {
					                "symbol" : "TZS",
					                "displayName" : "坦桑尼亚先令",
					                "displayName-count-other" : "坦桑尼亚先令"
					              },
					              "UAH" : {
					                "symbol" : "UAH",
					                "displayName" : "乌克兰格里夫纳",
					                "symbol-alt-narrow" : "₴",
					                "displayName-count-other" : "乌克兰格里夫纳"
					              },
					              "UAK" : {
					                "symbol" : "UAK",
					                "displayName" : "乌克兰币",
					                "displayName-count-other" : "乌克兰币"
					              },
					              "UGS" : {
					                "symbol" : "UGS",
					                "displayName" : "乌干达先令 (1966–1987)",
					                "displayName-count-other" : "乌干达先令 (1966–1987)"
					              },
					              "UGX" : {
					                "symbol" : "UGX",
					                "displayName" : "乌干达先令",
					                "displayName-count-other" : "乌干达先令"
					              },
					              "USD" : {
					                "symbol" : "US$",
					                "displayName" : "美元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "美元"
					              },
					              "USN" : {
					                "symbol" : "USN",
					                "displayName" : "美元（次日）",
					                "displayName-count-other" : "美元（次日）"
					              },
					              "USS" : {
					                "symbol" : "USS",
					                "displayName" : "美元（当日）",
					                "displayName-count-other" : "美元（当日）"
					              },
					              "UYI" : {
					                "symbol" : "UYI",
					                "displayName" : "乌拉圭比索（索引单位）",
					                "displayName-count-other" : "乌拉圭比索（索引单位）"
					              },
					              "UYP" : {
					                "symbol" : "UYP",
					                "displayName" : "乌拉圭比索 (1975–1993)",
					                "displayName-count-other" : "乌拉圭比索 (1975–1993)"
					              },
					              "UYU" : {
					                "symbol" : "UYU",
					                "displayName" : "乌拉圭比索",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "乌拉圭比索"
					              },
					              "UZS" : {
					                "symbol" : "UZS",
					                "displayName" : "乌兹别克斯坦苏姆",
					                "displayName-count-other" : "乌兹别克斯坦苏姆"
					              },
					              "VEB" : {
					                "symbol" : "VEB",
					                "displayName" : "委内瑞拉玻利瓦尔 (1871–2008)",
					                "displayName-count-other" : "委内瑞拉玻利瓦尔 (1871–2008)"
					              },
					              "VEF" : {
					                "symbol" : "VEF",
					                "displayName" : "委内瑞拉玻利瓦尔",
					                "symbol-alt-narrow" : "Bs",
					                "displayName-count-other" : "委内瑞拉玻利瓦尔"
					              },
					              "VND" : {
					                "symbol" : "₫",
					                "displayName" : "越南盾",
					                "symbol-alt-narrow" : "₫",
					                "displayName-count-other" : "越南盾"
					              },
					              "VNN" : {
					                "symbol" : "VNN",
					                "displayName" : "越南盾 (1978–1985)"
					              },
					              "VUV" : {
					                "symbol" : "VUV",
					                "displayName" : "瓦努阿图瓦图",
					                "displayName-count-other" : "瓦努阿图瓦图"
					              },
					              "WST" : {
					                "symbol" : "WST",
					                "displayName" : "萨摩亚塔拉",
					                "displayName-count-other" : "萨摩亚塔拉"
					              },
					              "XAF" : {
					                "symbol" : "FCFA",
					                "displayName" : "中非法郎",
					                "displayName-count-other" : "中非法郎"
					              },
					              "XAG" : {
					                "symbol" : "XAG",
					                "displayName" : "银"
					              },
					              "XAU" : {
					                "symbol" : "XAU",
					                "displayName" : "黄金"
					              },
					              "XBA" : {
					                "symbol" : "XBA",
					                "displayName" : "欧洲复合单位"
					              },
					              "XBB" : {
					                "symbol" : "XBB",
					                "displayName" : "欧洲货币联盟"
					              },
					              "XBC" : {
					                "symbol" : "XBC",
					                "displayName" : "欧洲计算单位 (XBC)"
					              },
					              "XBD" : {
					                "symbol" : "XBD",
					                "displayName" : "欧洲计算单位 (XBD)"
					              },
					              "XCD" : {
					                "symbol" : "EC$",
					                "displayName" : "东加勒比元",
					                "symbol-alt-narrow" : "$",
					                "displayName-count-other" : "东加勒比元"
					              },
					              "XDR" : {
					                "symbol" : "XDR",
					                "displayName" : "特别提款权"
					              },
					              "XEU" : {
					                "symbol" : "XEU",
					                "displayName" : "欧洲货币单位",
					                "displayName-count-other" : "欧洲货币单位"
					              },
					              "XFO" : {
					                "symbol" : "XFO",
					                "displayName" : "法国金法郎"
					              },
					              "XFU" : {
					                "symbol" : "XFU",
					                "displayName" : "法国法郎 (UIC)"
					              },
					              "XOF" : {
					                "symbol" : "CFA",
					                "displayName" : "西非法郎",
					                "displayName-count-other" : "西非法郎"
					              },
					              "XPD" : {
					                "symbol" : "XPD",
					                "displayName" : "钯"
					              },
					              "XPF" : {
					                "symbol" : "CFPF",
					                "displayName" : "太平洋法郎",
					                "displayName-count-other" : "太平洋法郎"
					              },
					              "XPT" : {
					                "symbol" : "XPT",
					                "displayName" : "铂"
					              },
					              "XRE" : {
					                "symbol" : "XRE",
					                "displayName" : "RINET 基金"
					              },
					              "XSU" : {
					                "symbol" : "XSU",
					                "displayName" : "XSU"
					              },
					              "XTS" : {
					                "symbol" : "XTS",
					                "displayName" : "测试货币代码"
					              },
					              "XUA" : {
					                "symbol" : "XUA",
					                "displayName" : "XUA"
					              },
					              "XXX" : {
					                "symbol" : "XXX",
					                "displayName" : "未知货币",
					                "displayName-count-other" : "（未知货币）"
					              },
					              "YDD" : {
					                "symbol" : "YDD",
					                "displayName" : "也门第纳尔",
					                "displayName-count-other" : "也门第纳尔"
					              },
					              "YER" : {
					                "symbol" : "YER",
					                "displayName" : "也门里亚尔",
					                "displayName-count-other" : "也门里亚尔"
					              },
					              "YUD" : {
					                "symbol" : "YUD",
					                "displayName" : "南斯拉夫硬第纳尔 (1966–1990)",
					                "displayName-count-other" : "南斯拉夫硬第纳尔 (1966–1990)"
					              },
					              "YUM" : {
					                "symbol" : "YUM",
					                "displayName" : "南斯拉夫新第纳尔 (1994–2002)",
					                "displayName-count-other" : "南斯拉夫新第纳尔 (1994–2002)"
					              },
					              "YUN" : {
					                "symbol" : "YUN",
					                "displayName" : "南斯拉夫可兑换第纳尔 (1990–1992)",
					                "displayName-count-other" : "南斯拉夫可兑换第纳尔 (1990–1992)"
					              },
					              "YUR" : {
					                "symbol" : "YUR",
					                "displayName" : "南斯拉夫改良第纳尔 (1992–1993)",
					                "displayName-count-other" : "南斯拉夫改良第纳尔 (1992–1993)"
					              },
					              "ZAL" : {
					                "symbol" : "ZAL",
					                "displayName" : "南非兰特 (金融)",
					                "displayName-count-other" : "南非兰特 (金融)"
					              },
					              "ZAR" : {
					                "symbol" : "ZAR",
					                "displayName" : "南非兰特",
					                "symbol-alt-narrow" : "R",
					                "displayName-count-other" : "南非兰特"
					              },
					              "ZMK" : {
					                "symbol" : "ZMK",
					                "displayName" : "赞比亚克瓦查 (1968–2012)",
					                "displayName-count-other" : "赞比亚克瓦查 (1968–2012)"
					              },
					              "ZMW" : {
					                "symbol" : "ZMW",
					                "displayName" : "赞比亚克瓦查",
					                "symbol-alt-narrow" : "ZK",
					                "displayName-count-other" : "赞比亚克瓦查"
					              },
					              "ZRN" : {
					                "symbol" : "ZRN",
					                "displayName" : "新扎伊尔 (1993–1998)",
					                "displayName-count-other" : "新扎伊尔 (1993–1998)"
					              },
					              "ZRZ" : {
					                "symbol" : "ZRZ",
					                "displayName" : "扎伊尔 (1971–1993)",
					                "displayName-count-other" : "扎伊尔 (1971–1993)"
					              },
					              "ZWD" : {
					                "symbol" : "ZWD",
					                "displayName" : "津巴布韦元 (1980–2008)",
					                "displayName-count-other" : "津巴布韦元 (1980–2008)"
					              },
					              "ZWL" : {
					                "symbol" : "ZWL",
					                "displayName" : "津巴布韦元 (2009)",
					                "displayName-count-other" : "津巴布韦元 (2009)"
					              },
					              "ZWR" : {
					                "symbol" : "ZWR",
					                "displayName" : "津巴布韦元 (2008)",
					                "displayName-count-other" : "津巴布韦元 (2008)"
					              }
					            },"numbers":{
					              "defaultNumberingSystem" : "latn",
					              "numberSymbols" : {
					                "decimal" : ".",
					                "group" : ",",
					                "list" : ";",
					                "percentSign" : "%",
					                "plusSign" : "+",
					                "minusSign" : "-",
					                "exponential" : "E",
					                "superscriptingExponent" : "×",
					                "perMille" : "‰",
					                "infinity" : "∞",
					                "nan" : "NaN",
					                "timeSeparator" : ":"
					              },
					              "numberFormats" : {
					                "decimalFormats" : "#,##0.###",
					                "percentFormats" : "#,##0%",
					                "currencyFormats" : "¤#,##0.00",
					                "scientificFormats" : "#E0",
					                "decimalFormats-long" : {
					                  "decimalFormat" : {
					                    "1000-count-other" : "0",
					                    "10000-count-other" : "0万",
					                    "100000-count-other" : "00万",
					                    "1000000-count-other" : "000万",
					                    "10000000-count-other" : "0000万",
					                    "100000000-count-other" : "0亿",
					                    "1000000000-count-other" : "00亿",
					                    "10000000000-count-other" : "000亿",
					                    "100000000000-count-other" : "0000亿",
					                    "1000000000000-count-other" : "0兆",
					                    "10000000000000-count-other" : "00兆",
					                    "100000000000000-count-other" : "000兆"
					                  }
					                },
					                "decimalFormats-short" : {
					                  "decimalFormat" : {
					                    "1000-count-other" : "0",
					                    "10000-count-other" : "0万",
					                    "100000-count-other" : "00万",
					                    "1000000-count-other" : "000万",
					                    "10000000-count-other" : "0000万",
					                    "100000000-count-other" : "0亿",
					                    "1000000000-count-other" : "00亿",
					                    "10000000000-count-other" : "000亿",
					                    "100000000000-count-other" : "0000亿",
					                    "1000000000000-count-other" : "0兆",
					                    "10000000000000-count-other" : "00兆",
					                    "100000000000000-count-other" : "000兆"
					                  }
					                },
					                "currencyFormats-short" : {
					                  "standard" : {
					                    "1000-count-other" : "0",
					                    "10000-count-other" : "¤0万",
					                    "100000-count-other" : "¤00万",
					                    "1000000-count-other" : "¤000万",
					                    "10000000-count-other" : "¤0000万",
					                    "100000000-count-other" : "¤0亿",
					                    "1000000000-count-other" : "¤00亿",
					                    "10000000000-count-other" : "¤000亿",
					                    "100000000000-count-other" : "¤0000亿",
					                    "1000000000000-count-other" : "¤0兆",
					                    "10000000000000-count-other" : "¤00兆",
					                    "100000000000000-count-other" : "¤000兆"
					                  }
					                }
					              }
					            },"supplemental":{"currencies":{
					          "fractions" : {
					            "CHF" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "5"
					            },
					            "ITL" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ALL" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "DJF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "STD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "CLP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "UGX" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "TND" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "TZS" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ADP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "VND" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "TRL" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "SLL" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "GYD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "KPW" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "IDR" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "AMD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "LBP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "IQD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "JOD" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "RWF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "TWD" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "RSD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "UYI" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "DKK" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "50"
					            },
					            "KMF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ZWD" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "CAD" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "5"
					            },
					            "MMK" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "MUR" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "NOK" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "SYP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "XOF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "CRC" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "CZK" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "OMR" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "PKR" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "SEK" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "GNF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "BHD" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "YER" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "IRR" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "AFN" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "MRO" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "UZS" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "XPF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "KRW" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "JPY" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "MNT" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "BYN" : {
					              "_rounding" : "0",
					              "_digits" : "2"
					            },
					            "LYD" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "HUF" : {
					              "_rounding" : "0",
					              "_digits" : "2",
					              "_cashRounding" : "0",
					              "_cashDigits" : "0"
					            },
					            "KWD" : {
					              "_rounding" : "0",
					              "_digits" : "3"
					            },
					            "BYR" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "LUF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "BIF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "PYG" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ISK" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ESP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "COP" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "MGA" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "MGF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "TMM" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "SOS" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "VUV" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "LAK" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "ZMK" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "CLF" : {
					              "_rounding" : "0",
					              "_digits" : "4"
					            },
					            "XAF" : {
					              "_rounding" : "0",
					              "_digits" : "0"
					            },
					            "DEFAULT" : {
					              "_rounding" : "0",
					              "_digits" : "2"
					            }
					          },
					          "region" : {
					            "AC" : "SHP",
					            "AD" : "EUR",
					            "AE" : "AED",
					            "AF" : "AFN",
					            "AG" : "XCD",
					            "AI" : "XCD",
					            "AL" : "ALL",
					            "AM" : "AMD",
					            "AO" : "AOA",
					            "AR" : "ARS",
					            "AS" : "USD",
					            "AT" : "EUR",
					            "AU" : "AUD",
					            "AW" : "AWG",
					            "AX" : "EUR",
					            "AZ" : "AZN",
					            "BA" : "BAM",
					            "BB" : "BBD",
					            "BD" : "BDT",
					            "BE" : "EUR",
					            "BF" : "XOF",
					            "BG" : "BGN",
					            "BH" : "BHD",
					            "BI" : "BIF",
					            "BJ" : "XOF",
					            "BL" : "EUR",
					            "BM" : "BMD",
					            "BN" : "BND",
					            "BO" : "BOB",
					            "BQ" : "USD",
					            "BR" : "BRL",
					            "BS" : "BSD",
					            "BT" : "BTN",
					            "BV" : "NOK",
					            "BW" : "BWP",
					            "BY" : "BYN",
					            "BZ" : "BZD",
					            "CA" : "CAD",
					            "CC" : "AUD",
					            "CD" : "CDF",
					            "CF" : "XAF",
					            "CG" : "XAF",
					            "CH" : "CHF",
					            "CI" : "XOF",
					            "CK" : "NZD",
					            "CL" : "CLP",
					            "CM" : "XAF",
					            "CN" : "CNH",
					            "CO" : "COP",
					            "CR" : "CRC",
					            "CU" : "CUC",
					            "CV" : "CVE",
					            "CW" : "ANG",
					            "CX" : "AUD",
					            "CY" : "EUR",
					            "CZ" : "CZK",
					            "DE" : "EUR",
					            "DG" : "USD",
					            "DJ" : "DJF",
					            "DK" : "DKK",
					            "DM" : "XCD",
					            "DO" : "DOP",
					            "DZ" : "DZD",
					            "EA" : "EUR",
					            "EC" : "USD",
					            "EE" : "EUR",
					            "EG" : "EGP",
					            "EH" : "MAD",
					            "ER" : "ERN",
					            "ES" : "EUR",
					            "ET" : "ETB",
					            "EU" : "EUR",
					            "FI" : "EUR",
					            "FJ" : "FJD",
					            "FK" : "FKP",
					            "FM" : "USD",
					            "FO" : "DKK",
					            "FR" : "EUR",
					            "GA" : "XAF",
					            "GB" : "GBP",
					            "GD" : "XCD",
					            "GE" : "GEL",
					            "GF" : "EUR",
					            "GG" : "GBP",
					            "GH" : "GHS",
					            "GI" : "GIP",
					            "GL" : "DKK",
					            "GM" : "GMD",
					            "GN" : "GNF",
					            "GP" : "EUR",
					            "GQ" : "XAF",
					            "GR" : "EUR",
					            "GS" : "GBP",
					            "GT" : "GTQ",
					            "GU" : "USD",
					            "GW" : "XOF",
					            "GY" : "GYD",
					            "HK" : "HKD",
					            "HM" : "AUD",
					            "HN" : "HNL",
					            "HR" : "HRK",
					            "HT" : "USD",
					            "HU" : "HUF",
					            "IC" : "EUR",
					            "ID" : "IDR",
					            "IE" : "EUR",
					            "IL" : "ILS",
					            "IM" : "GBP",
					            "IN" : "INR",
					            "IO" : "USD",
					            "IQ" : "IQD",
					            "IR" : "IRR",
					            "IS" : "ISK",
					            "IT" : "EUR",
					            "JE" : "GBP",
					            "JM" : "JMD",
					            "JO" : "JOD",
					            "JP" : "JPY",
					            "KE" : "KES",
					            "KG" : "KGS",
					            "KH" : "KHR",
					            "KI" : "AUD",
					            "KM" : "KMF",
					            "KN" : "XCD",
					            "KP" : "KPW",
					            "KR" : "KRW",
					            "KW" : "KWD",
					            "KY" : "KYD",
					            "KZ" : "KZT",
					            "LA" : "LAK",
					            "LB" : "LBP",
					            "LC" : "XCD",
					            "LI" : "CHF",
					            "LK" : "LKR",
					            "LR" : "LRD",
					            "LS" : "LSL",
					            "LT" : "EUR",
					            "LU" : "EUR",
					            "LV" : "EUR",
					            "LY" : "LYD",
					            "MA" : "MAD",
					            "MC" : "EUR",
					            "MD" : "MDL",
					            "ME" : "EUR",
					            "MF" : "EUR",
					            "MG" : "MGA",
					            "MH" : "USD",
					            "MK" : "MKD",
					            "ML" : "XOF",
					            "MM" : "MMK",
					            "MN" : "MNT",
					            "MO" : "MOP",
					            "MP" : "USD",
					            "MQ" : "EUR",
					            "MR" : "MRO",
					            "MS" : "XCD",
					            "MT" : "EUR",
					            "MU" : "MUR",
					            "MV" : "MVR",
					            "MW" : "MWK",
					            "MX" : "MXN",
					            "MY" : "MYR",
					            "MZ" : "MZN",
					            "NA" : "NAD",
					            "NC" : "XPF",
					            "NE" : "XOF",
					            "NF" : "AUD",
					            "NG" : "NGN",
					            "NI" : "NIO",
					            "NL" : "EUR",
					            "NO" : "NOK",
					            "NP" : "NPR",
					            "NR" : "AUD",
					            "NU" : "NZD",
					            "NZ" : "NZD",
					            "OM" : "OMR",
					            "PA" : "USD",
					            "PE" : "PEN",
					            "PF" : "XPF",
					            "PG" : "PGK",
					            "PH" : "PHP",
					            "PK" : "PKR",
					            "PL" : "PLN",
					            "PM" : "EUR",
					            "PN" : "NZD",
					            "PR" : "USD",
					            "PS" : "JOD",
					            "PT" : "EUR",
					            "PW" : "USD",
					            "PY" : "PYG",
					            "QA" : "QAR",
					            "RE" : "EUR",
					            "RO" : "RON",
					            "RS" : "RSD",
					            "RU" : "RUB",
					            "RW" : "RWF",
					            "SA" : "SAR",
					            "SB" : "SBD",
					            "SC" : "SCR",
					            "SD" : "SDG",
					            "SE" : "SEK",
					            "SG" : "SGD",
					            "SH" : "SHP",
					            "SI" : "EUR",
					            "SJ" : "NOK",
					            "SK" : "EUR",
					            "SL" : "SLL",
					            "SM" : "EUR",
					            "SN" : "XOF",
					            "SO" : "SOS",
					            "SR" : "SRD",
					            "SS" : "SSP",
					            "ST" : "STN",
					            "SV" : "USD",
					            "SX" : "ANG",
					            "SY" : "SYP",
					            "SZ" : "SZL",
					            "TA" : "GBP",
					            "TC" : "USD",
					            "TD" : "XAF",
					            "TF" : "EUR",
					            "TG" : "XOF",
					            "TH" : "THB",
					            "TJ" : "TJS",
					            "TK" : "NZD",
					            "TL" : "USD",
					            "TM" : "TMT",
					            "TN" : "TND",
					            "TO" : "TOP",
					            "TR" : "TRY",
					            "TT" : "TTD",
					            "TV" : "AUD",
					            "TW" : "TWD",
					            "TZ" : "TZS",
					            "UA" : "UAH",
					            "UG" : "UGX",
					            "UM" : "USD",
					            "US" : "USD",
					            "UY" : "UYU",
					            "UZ" : "UZS",
					            "VA" : "EUR",
					            "VC" : "XCD",
					            "VE" : "VEF",
					            "VG" : "USD",
					            "VI" : "USD",
					            "VN" : "VND",
					            "VU" : "VUV",
					            "WF" : "XPF",
					            "WS" : "WST",
					            "XK" : "EUR",
					            "YE" : "YER",
					            "YT" : "EUR",
					            "ZA" : "ZAR",
					            "ZM" : "ZMW",
					            "ZW" : "USD"
					          }
					        },"numbers":{
					          "numberingSystems" : {
					            "adlm" : {
					              "_type" : "numeric",
					              "_digits" : "𞥐𞥑𞥒𞥓𞥔𞥕𞥖𞥗𞥘𞥙"
					            },
					            "ahom" : {
					              "_type" : "numeric",
					              "_digits" : "𑜰𑜱𑜲𑜳𑜴𑜵𑜶𑜷𑜸𑜹"
					            },
					            "arab" : {
					              "_type" : "numeric",
					              "_digits" : "٠١٢٣٤٥٦٧٨٩"
					            },
					            "arabext" : {
					              "_type" : "numeric",
					              "_digits" : "۰۱۲۳۴۵۶۷۸۹"
					            },
					            "armn" : {
					              "_rules" : "armenian-upper",
					              "_type" : "algorithmic"
					            },
					            "armnlow" : {
					              "_rules" : "armenian-lower",
					              "_type" : "algorithmic"
					            },
					            "bali" : {
					              "_type" : "numeric",
					              "_digits" : "᭐᭑᭒᭓᭔᭕᭖᭗᭘᭙"
					            },
					            "beng" : {
					              "_type" : "numeric",
					              "_digits" : "০১২৩৪৫৬৭৮৯"
					            },
					            "bhks" : {
					              "_type" : "numeric",
					              "_digits" : "𑱐𑱑𑱒𑱓𑱔𑱕𑱖𑱗𑱘𑱙"
					            },
					            "brah" : {
					              "_type" : "numeric",
					              "_digits" : "𑁦𑁧𑁨𑁩𑁪𑁫𑁬𑁭𑁮𑁯"
					            },
					            "cakm" : {
					              "_type" : "numeric",
					              "_digits" : "𑄶𑄷𑄸𑄹𑄺𑄻𑄼𑄽𑄾𑄿"
					            },
					            "cham" : {
					              "_type" : "numeric",
					              "_digits" : "꩐꩑꩒꩓꩔꩕꩖꩗꩘꩙"
					            },
					            "cyrl" : {
					              "_rules" : "cyrillic-lower",
					              "_type" : "algorithmic"
					            },
					            "deva" : {
					              "_type" : "numeric",
					              "_digits" : "०१२३४५६७८९"
					            },
					            "ethi" : {
					              "_rules" : "ethiopic",
					              "_type" : "algorithmic"
					            },
					            "fullwide" : {
					              "_type" : "numeric",
					              "_digits" : "０１２３４５６７８９"
					            },
					            "geor" : {
					              "_rules" : "georgian",
					              "_type" : "algorithmic"
					            },
					            "gonm" : {
					              "_type" : "numeric",
					              "_digits" : "𑵐𑵑𑵒𑵓𑵔𑵕𑵖𑵗𑵘𑵙"
					            },
					            "grek" : {
					              "_rules" : "greek-upper",
					              "_type" : "algorithmic"
					            },
					            "greklow" : {
					              "_rules" : "greek-lower",
					              "_type" : "algorithmic"
					            },
					            "gujr" : {
					              "_type" : "numeric",
					              "_digits" : "૦૧૨૩૪૫૬૭૮૯"
					            },
					            "guru" : {
					              "_type" : "numeric",
					              "_digits" : "੦੧੨੩੪੫੬੭੮੯"
					            },
					            "hanidays" : {
					              "_rules" : "zh/SpelloutRules/spellout-numbering-days",
					              "_type" : "algorithmic"
					            },
					            "hanidec" : {
					              "_type" : "numeric",
					              "_digits" : "〇一二三四五六七八九"
					            },
					            "hans" : {
					              "_rules" : "zh/SpelloutRules/spellout-cardinal",
					              "_type" : "algorithmic"
					            },
					            "hansfin" : {
					              "_rules" : "zh/SpelloutRules/spellout-cardinal-financial",
					              "_type" : "algorithmic"
					            },
					            "hant" : {
					              "_rules" : "zh_Hant/SpelloutRules/spellout-cardinal",
					              "_type" : "algorithmic"
					            },
					            "hantfin" : {
					              "_rules" : "zh_Hant/SpelloutRules/spellout-cardinal-financial",
					              "_type" : "algorithmic"
					            },
					            "hebr" : {
					              "_rules" : "hebrew",
					              "_type" : "algorithmic"
					            },
					            "hmng" : {
					              "_type" : "numeric",
					              "_digits" : "𖭐𖭑𖭒𖭓𖭔𖭕𖭖𖭗𖭘𖭙"
					            },
					            "java" : {
					              "_type" : "numeric",
					              "_digits" : "꧐꧑꧒꧓꧔꧕꧖꧗꧘꧙"
					            },
					            "jpan" : {
					              "_rules" : "ja/SpelloutRules/spellout-cardinal",
					              "_type" : "algorithmic"
					            },
					            "jpanfin" : {
					              "_rules" : "ja/SpelloutRules/spellout-cardinal-financial",
					              "_type" : "algorithmic"
					            },
					            "kali" : {
					              "_type" : "numeric",
					              "_digits" : "꤀꤁꤂꤃꤄꤅꤆꤇꤈꤉"
					            },
					            "khmr" : {
					              "_type" : "numeric",
					              "_digits" : "០១២៣៤៥៦៧៨៩"
					            },
					            "knda" : {
					              "_type" : "numeric",
					              "_digits" : "೦೧೨೩೪೫೬೭೮೯"
					            },
					            "lana" : {
					              "_type" : "numeric",
					              "_digits" : "᪀᪁᪂᪃᪄᪅᪆᪇᪈᪉"
					            },
					            "lanatham" : {
					              "_type" : "numeric",
					              "_digits" : "᪐᪑᪒᪓᪔᪕᪖᪗᪘᪙"
					            },
					            "laoo" : {
					              "_type" : "numeric",
					              "_digits" : "໐໑໒໓໔໕໖໗໘໙"
					            },
					            "latn" : {
					              "_type" : "numeric",
					              "_digits" : "0123456789"
					            },
					            "lepc" : {
					              "_type" : "numeric",
					              "_digits" : "᱀᱁᱂᱃᱄᱅᱆᱇᱈᱉"
					            },
					            "limb" : {
					              "_type" : "numeric",
					              "_digits" : "᥆᥇᥈᥉᥊᥋᥌᥍᥎᥏"
					            },
					            "mathbold" : {
					              "_type" : "numeric",
					              "_digits" : "𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗"
					            },
					            "mathdbl" : {
					              "_type" : "numeric",
					              "_digits" : "𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡"
					            },
					            "mathmono" : {
					              "_type" : "numeric",
					              "_digits" : "𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿"
					            },
					            "mathsanb" : {
					              "_type" : "numeric",
					              "_digits" : "𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵"
					            },
					            "mathsans" : {
					              "_type" : "numeric",
					              "_digits" : "𝟢𝟣𝟤𝟥𝟦𝟧𝟨𝟩𝟪𝟫"
					            },
					            "mlym" : {
					              "_type" : "numeric",
					              "_digits" : "൦൧൨൩൪൫൬൭൮൯"
					            },
					            "modi" : {
					              "_type" : "numeric",
					              "_digits" : "𑙐𑙑𑙒𑙓𑙔𑙕𑙖𑙗𑙘𑙙"
					            },
					            "mong" : {
					              "_type" : "numeric",
					              "_digits" : "᠐᠑᠒᠓᠔᠕᠖᠗᠘᠙"
					            },
					            "mroo" : {
					              "_type" : "numeric",
					              "_digits" : "𖩠𖩡𖩢𖩣𖩤𖩥𖩦𖩧𖩨𖩩"
					            },
					            "mtei" : {
					              "_type" : "numeric",
					              "_digits" : "꯰꯱꯲꯳꯴꯵꯶꯷꯸꯹"
					            },
					            "mymr" : {
					              "_type" : "numeric",
					              "_digits" : "၀၁၂၃၄၅၆၇၈၉"
					            },
					            "mymrshan" : {
					              "_type" : "numeric",
					              "_digits" : "႐႑႒႓႔႕႖႗႘႙"
					            },
					            "mymrtlng" : {
					              "_type" : "numeric",
					              "_digits" : "꧰꧱꧲꧳꧴꧵꧶꧷꧸꧹"
					            },
					            "newa" : {
					              "_type" : "numeric",
					              "_digits" : "𑑐𑑑𑑒𑑓𑑔𑑕𑑖𑑗𑑘𑑙"
					            },
					            "nkoo" : {
					              "_type" : "numeric",
					              "_digits" : "߀߁߂߃߄߅߆߇߈߉"
					            },
					            "olck" : {
					              "_type" : "numeric",
					              "_digits" : "᱐᱑᱒᱓᱔᱕᱖᱗᱘᱙"
					            },
					            "orya" : {
					              "_type" : "numeric",
					              "_digits" : "୦୧୨୩୪୫୬୭୮୯"
					            },
					            "osma" : {
					              "_type" : "numeric",
					              "_digits" : "𐒠𐒡𐒢𐒣𐒤𐒥𐒦𐒧𐒨𐒩"
					            },
					            "roman" : {
					              "_rules" : "roman-upper",
					              "_type" : "algorithmic"
					            },
					            "romanlow" : {
					              "_rules" : "roman-lower",
					              "_type" : "algorithmic"
					            },
					            "saur" : {
					              "_type" : "numeric",
					              "_digits" : "꣐꣑꣒꣓꣔꣕꣖꣗꣘꣙"
					            },
					            "shrd" : {
					              "_type" : "numeric",
					              "_digits" : "𑇐𑇑𑇒𑇓𑇔𑇕𑇖𑇗𑇘𑇙"
					            },
					            "sind" : {
					              "_type" : "numeric",
					              "_digits" : "𑋰𑋱𑋲𑋳𑋴𑋵𑋶𑋷𑋸𑋹"
					            },
					            "sinh" : {
					              "_type" : "numeric",
					              "_digits" : "෦෧෨෩෪෫෬෭෮෯"
					            },
					            "sora" : {
					              "_type" : "numeric",
					              "_digits" : "𑃰𑃱𑃲𑃳𑃴𑃵𑃶𑃷𑃸𑃹"
					            },
					            "sund" : {
					              "_type" : "numeric",
					              "_digits" : "᮰᮱᮲᮳᮴᮵᮶᮷᮸᮹"
					            },
					            "takr" : {
					              "_type" : "numeric",
					              "_digits" : "𑛀𑛁𑛂𑛃𑛄𑛅𑛆𑛇𑛈𑛉"
					            },
					            "talu" : {
					              "_type" : "numeric",
					              "_digits" : "᧐᧑᧒᧓᧔᧕᧖᧗᧘᧙"
					            },
					            "taml" : {
					              "_rules" : "tamil",
					              "_type" : "algorithmic"
					            },
					            "tamldec" : {
					              "_type" : "numeric",
					              "_digits" : "௦௧௨௩௪௫௬௭௮௯"
					            },
					            "telu" : {
					              "_type" : "numeric",
					              "_digits" : "౦౧౨౩౪౫౬౭౮౯"
					            },
					            "thai" : {
					              "_type" : "numeric",
					              "_digits" : "๐๑๒๓๔๕๖๗๘๙"
					            },
					            "tibt" : {
					              "_type" : "numeric",
					              "_digits" : "༠༡༢༣༤༥༦༧༨༩"
					            },
					            "tirh" : {
					              "_type" : "numeric",
					              "_digits" : "𑓐𑓑𑓒𑓓𑓔𑓕𑓖𑓗𑓘𑓙"
					            },
					            "vaii" : {
					              "_type" : "numeric",
					              "_digits" : "꘠꘡꘢꘣꘤꘥꘦꘧꘨꘩"
					            },
					            "wara" : {
					              "_type" : "numeric",
					              "_digits" : "𑣠𑣡𑣢𑣣𑣤𑣥𑣦𑣧𑣨𑣩"
					            }
					          }
					        }}},"isExistPattern":true}}}`},
		{combine: 1, language: "de", region: "de", scope: "currencies", components: "sunglow", name: Name, version: "1.1.1.1.1.1",
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"components":[{"productName":"VPE","version":"1.1.1","locale":"de","component":"sunglow","messages":{"plural.files": "{files, plural,=0 {category 0 : No files.} one {category one : # Es gibt eine Datei auf {place}.} other {category other : Es gibt # Dateien auf {place}} }","message": "Meldung-de","pagination": "{0} – {1} von {2} Kunden","one.arg": "teste ein Argument {0}"}}],"pattern":{"localeID":"de","language":"de","region":"de","categories":{"currencies":{"ADP" : {"symbol" : "ADP","displayName" : "Andorranische Pesete","displayName-count-one" : "Andorranische Pesete","displayName-count-other" : "Andorranische Peseten"},"AED" : {"symbol" : "AED","displayName" : "VAE-Dirham","displayName-count-one" : "VAE-Dirham","displayName-count-other" : "VAE-Dirham"},"AFA" : {"symbol" : "AFA","displayName" : "Afghanische Afghani (1927–2002)","displayName-count-one" : "Afghanische Afghani (1927–2002)","displayName-count-other" : "Afghanische Afghani (1927–2002)"},"AFN" : {"symbol" : "AFN","displayName" : "Afghanischer Afghani","displayName-count-one" : "Afghanischer Afghani","displayName-count-other" : "Afghanische Afghani"},"ALK" : {"symbol" : "ALK","displayName" : "Albanischer Lek (1946–1965)","displayName-count-one" : "Albanischer Lek (1946–1965)","displayName-count-other" : "Albanische Lek (1946–1965)"},"ALL" : {"symbol" : "ALL","displayName" : "Albanischer Lek","displayName-count-one" : "Albanischer Lek","displayName-count-other" : "Albanische Lek"},"AMD" : {"symbol" : "AMD","displayName" : "Armenischer Dram","displayName-count-one" : "Armenischer Dram","displayName-count-other" : "Armenische Dram"},"ANG" : {"symbol" : "ANG","displayName" : "Niederländische-Antillen-Gulden","displayName-count-one" : "Niederländische-Antillen-Gulden","displayName-count-other" : "Niederländische-Antillen-Gulden"},"AOA" : {"symbol" : "AOA","displayName" : "Angolanischer Kwanza","displayName-count-one" : "Angolanischer Kwanza","symbol-alt-narrow" : "Kz","displayName-count-other" : "Angolanische Kwanza"},"AOK" : {"symbol" : "AOK","displayName" : "Angolanischer Kwanza (1977–1990)","displayName-count-one" : "Angolanischer Kwanza (1977–1990)","displayName-count-other" : "Angolanische Kwanza (1977–1990)"},"AON" : {"symbol" : "AON","displayName" : "Angolanischer Neuer Kwanza (1990–2000)","displayName-count-one" : "Angolanischer Neuer Kwanza (1990–2000)","displayName-count-other" : "Angolanische Neue Kwanza (1990–2000)"},"AOR" : {"symbol" : "AOR","displayName" : "Angolanischer Kwanza Reajustado (1995–1999)","displayName-count-one" : "Angolanischer Kwanza Reajustado (1995–1999)","displayName-count-other" : "Angolanische Kwanza Reajustado (1995–1999)"},"ARA" : {"symbol" : "ARA","displayName" : "Argentinischer Austral","displayName-count-one" : "Argentinischer Austral","displayName-count-other" : "Argentinische Austral"},"ARL" : {"symbol" : "ARL","displayName" : "Argentinischer Peso Ley (1970–1983)","displayName-count-one" : "Argentinischer Peso Ley (1970–1983)","displayName-count-other" : "Argentinische Pesos Ley (1970–1983)"},"ARM" : {"symbol" : "ARM","displayName" : "Argentinischer Peso (1881–1970)","displayName-count-one" : "Argentinischer Peso (1881–1970)","displayName-count-other" : "Argentinische Pesos (1881–1970)"},"ARP" : {"symbol" : "ARP","displayName" : "Argentinischer Peso (1983–1985)","displayName-count-one" : "Argentinischer Peso (1983–1985)","displayName-count-other" : "Argentinische Peso (1983–1985)"},"ARS" : {"symbol" : "ARS","displayName" : "Argentinischer Peso","displayName-count-one" : "Argentinischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Argentinische Pesos"},"ATS" : {"symbol" : "öS","displayName" : "Österreichischer Schilling","displayName-count-one" : "Österreichischer Schilling","displayName-count-other" : "Österreichische Schilling"},"AUD" : {"symbol" : "AU$","displayName" : "Australischer Dollar","displayName-count-one" : "Australischer Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Australische Dollar"},"AWG" : {"symbol" : "AWG","displayName" : "Aruba-Florin","displayName-count-one" : "Aruba-Florin","displayName-count-other" : "Aruba-Florin"},"AZM" : {"symbol" : "AZM","displayName" : "Aserbaidschan-Manat (1993–2006)","displayName-count-one" : "Aserbaidschan-Manat (1993–2006)","displayName-count-other" : "Aserbaidschan-Manat (1993–2006)"},"AZN" : {"symbol" : "AZN","displayName" : "Aserbaidschan-Manat","displayName-count-one" : "Aserbaidschan-Manat","displayName-count-other" : "Aserbaidschan-Manat"},"BAD" : {"symbol" : "BAD","displayName" : "Bosnien und Herzegowina Dinar (1992–1994)","displayName-count-one" : "Bosnien und Herzegowina Dinar (1992–1994)","displayName-count-other" : "Bosnien und Herzegowina Dinar (1992–1994)"},"BAM" : {"symbol" : "BAM","displayName" : "Bosnien und Herzegowina Konvertierbare Mark","displayName-count-one" : "Bosnien und Herzegowina Konvertierbare Mark","symbol-alt-narrow" : "KM","displayName-count-other" : "Bosnien und Herzegowina Konvertierbare Mark"},"BAN" : {"symbol" : "BAN","displayName" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)","displayName-count-one" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)","displayName-count-other" : "Bosnien und Herzegowina Neue Dinar (1994–1997)"},"BBD" : {"symbol" : "BBD","displayName" : "Barbados-Dollar","displayName-count-one" : "Barbados-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Barbados-Dollar"},"BDT" : {"symbol" : "BDT","displayName" : "Bangladesch-Taka","displayName-count-one" : "Bangladesch-Taka","symbol-alt-narrow" : "৳","displayName-count-other" : "Bangladesch-Taka"},"BEC" : {"symbol" : "BEC","displayName" : "Belgischer Franc (konvertibel)","displayName-count-one" : "Belgischer Franc (konvertibel)","displayName-count-other" : "Belgische Franc (konvertibel)"},"BEF" : {"symbol" : "BEF","displayName" : "Belgischer Franc","displayName-count-one" : "Belgischer Franc","displayName-count-other" : "Belgische Franc"},"BEL" : {"symbol" : "BEL","displayName" : "Belgischer Finanz-Franc","displayName-count-one" : "Belgischer Finanz-Franc","displayName-count-other" : "Belgische Finanz-Franc"},"BGL" : {"symbol" : "BGL","displayName" : "Bulgarische Lew (1962–1999)","displayName-count-one" : "Bulgarische Lew (1962–1999)","displayName-count-other" : "Bulgarische Lew (1962–1999)"},"BGM" : {"symbol" : "BGK","displayName" : "Bulgarischer Lew (1952–1962)","displayName-count-one" : "Bulgarischer Lew (1952–1962)","displayName-count-other" : "Bulgarische Lew (1952–1962)"},"BGN" : {"symbol" : "BGN","displayName" : "Bulgarischer Lew","displayName-count-one" : "Bulgarischer Lew","displayName-count-other" : "Bulgarische Lew"},"BGO" : {"symbol" : "BGJ","displayName" : "Bulgarischer Lew (1879–1952)","displayName-count-one" : "Bulgarischer Lew (1879–1952)","displayName-count-other" : "Bulgarische Lew (1879–1952)"},"BHD" : {"symbol" : "BHD","displayName" : "Bahrain-Dinar","displayName-count-one" : "Bahrain-Dinar","displayName-count-other" : "Bahrain-Dinar"},"BIF" : {"symbol" : "BIF","displayName" : "Burundi-Franc","displayName-count-one" : "Burundi-Franc","displayName-count-other" : "Burundi-Francs"},"BMD" : {"symbol" : "BMD","displayName" : "Bermuda-Dollar","displayName-count-one" : "Bermuda-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Bermuda-Dollar"},"BND" : {"symbol" : "BND","displayName" : "Brunei-Dollar","displayName-count-one" : "Brunei-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Brunei-Dollar"},"BOB" : {"symbol" : "BOB","displayName" : "Bolivianischer Boliviano","displayName-count-one" : "Bolivianischer Boliviano","symbol-alt-narrow" : "Bs","displayName-count-other" : "Bolivianische Bolivianos"},"BOL" : {"symbol" : "BOL","displayName" : "Bolivianischer Boliviano (1863–1963)","displayName-count-one" : "Bolivianischer Boliviano (1863–1963)","displayName-count-other" : "Bolivianische Bolivianos (1863–1963)"},"BOP" : {"symbol" : "BOP","displayName" : "Bolivianischer Peso","displayName-count-one" : "Bolivianischer Peso","displayName-count-other" : "Bolivianische Peso"},"BOV" : {"symbol" : "BOV","displayName" : "Boliviansiche Mvdol","displayName-count-one" : "Boliviansiche Mvdol","displayName-count-other" : "Bolivianische Mvdol"},"BRB" : {"symbol" : "BRB","displayName" : "Brasilianischer Cruzeiro Novo (1967–1986)","displayName-count-one" : "Brasilianischer Cruzeiro Novo (1967–1986)","displayName-count-other" : "Brasilianische Cruzeiro Novo (1967–1986)"},"BRC" : {"symbol" : "BRC","displayName" : "Brasilianischer Cruzado (1986–1989)","displayName-count-one" : "Brasilianischer Cruzado (1986–1989)","displayName-count-other" : "Brasilianische Cruzado (1986–1989)"},"BRE" : {"symbol" : "BRE","displayName" : "Brasilianischer Cruzeiro (1990–1993)","displayName-count-one" : "Brasilianischer Cruzeiro (1990–1993)","displayName-count-other" : "Brasilianische Cruzeiro (1990–1993)"},"BRL" : {"symbol" : "R$","displayName" : "Brasilianischer Real","displayName-count-one" : "Brasilianischer Real","symbol-alt-narrow" : "R$","displayName-count-other" : "Brasilianische Real"},"BRN" : {"symbol" : "BRN","displayName" : "Brasilianischer Cruzado Novo (1989–1990)","displayName-count-one" : "Brasilianischer Cruzado Novo (1989–1990)","displayName-count-other" : "Brasilianische Cruzado Novo (1989–1990)"},"BRR" : {"symbol" : "BRR","displayName" : "Brasilianischer Cruzeiro (1993–1994)","displayName-count-one" : "Brasilianischer Cruzeiro (1993–1994)","displayName-count-other" : "Brasilianische Cruzeiro (1993–1994)"},"BRZ" : {"symbol" : "BRZ","displayName" : "Brasilianischer Cruzeiro (1942–1967)","displayName-count-one" : "Brasilianischer Cruzeiro (1942–1967)","displayName-count-other" : "Brasilianischer Cruzeiro (1942–1967)"},"BSD" : {"symbol" : "BSD","displayName" : "Bahamas-Dollar","displayName-count-one" : "Bahamas-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Bahamas-Dollar"},"BTN" : {"symbol" : "BTN","displayName" : "Bhutan-Ngultrum","displayName-count-one" : "Bhutan-Ngultrum","displayName-count-other" : "Bhutan-Ngultrum"},"BUK" : {"symbol" : "BUK","displayName" : "Birmanischer Kyat","displayName-count-one" : "Birmanischer Kyat","displayName-count-other" : "Birmanische Kyat"},"BWP" : {"symbol" : "BWP","displayName" : "Botswanischer Pula","displayName-count-one" : "Botswanischer Pula","symbol-alt-narrow" : "P","displayName-count-other" : "Botswanische Pula"},"BYB" : {"symbol" : "BYB","displayName" : "Belarus-Rubel (1994–1999)","displayName-count-one" : "Belarus-Rubel (1994–1999)","displayName-count-other" : "Belarus-Rubel (1994–1999)"},"BYN" : {"symbol" : "BYN","displayName" : "Weißrussischer Rubel","displayName-count-one" : "Weißrussischer Rubel","symbol-alt-narrow" : "р.","displayName-count-other" : "Weißrussische Rubel"},"BYR" : {"symbol" : "BYR","displayName" : "Weißrussischer Rubel (2000–2016)","displayName-count-one" : "Weißrussischer Rubel (2000–2016)","displayName-count-other" : "Weißrussische Rubel (2000–2016)"},"BZD" : {"symbol" : "BZD","displayName" : "Belize-Dollar","displayName-count-one" : "Belize-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Belize-Dollar"},"CAD" : {"symbol" : "CA$","displayName" : "Kanadischer Dollar","displayName-count-one" : "Kanadischer Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Kanadische Dollar"},"CDF" : {"symbol" : "CDF","displayName" : "Kongo-Franc","displayName-count-one" : "Kongo-Franc","displayName-count-other" : "Kongo-Francs"},"CHE" : {"symbol" : "CHE","displayName" : "WIR-Euro","displayName-count-one" : "WIR-Euro","displayName-count-other" : "WIR-Euro"},"CHF" : {"symbol" : "CHF","displayName" : "Schweizer Franken","displayName-count-one" : "Schweizer Franken","displayName-count-other" : "Schweizer Franken"},"CHW" : {"symbol" : "CHW","displayName" : "WIR Franken","displayName-count-one" : "WIR Franken","displayName-count-other" : "WIR Franken"},"CLE" : {"symbol" : "CLE","displayName" : "Chilenischer Escudo","displayName-count-one" : "Chilenischer Escudo","displayName-count-other" : "Chilenische Escudo"},"CLF" : {"symbol" : "CLF","displayName" : "Chilenische Unidades de Fomento","displayName-count-one" : "Chilenische Unidades de Fomento","displayName-count-other" : "Chilenische Unidades de Fomento"},"CLP" : {"symbol" : "CLP","displayName" : "Chilenischer Peso","displayName-count-one" : "Chilenischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Chilenische Pesos"},"CNH" : {"symbol" : "CNH","displayName" : "Renminbi Yuan (Off–Shore)","displayName-count-one" : "Renminbi Yuan (Off–Shore)","displayName-count-other" : "Renminbi Yuan (Off–Shore)"},"CNX" : {"symbol" : "CNX","displayName" : "Dollar der Chinesischen Volksbank","displayName-count-one" : "Dollar der Chinesischen Volksbank","displayName-count-other" : "Dollar der Chinesischen Volksbank"},"CNY" : {"symbol" : "CN¥","displayName" : "Renminbi Yuan","displayName-count-one" : "Chinesischer Yuan","symbol-alt-narrow" : "¥","displayName-count-other" : "Renminbi Yuan"},"COP" : {"symbol" : "COP","displayName" : "Kolumbianischer Peso","displayName-count-one" : "Kolumbianischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Kolumbianische Pesos"},"COU" : {"symbol" : "COU","displayName" : "Kolumbianische Unidades de valor real","displayName-count-one" : "Kolumbianische Unidad de valor real","displayName-count-other" : "Kolumbianische Unidades de valor real"},"CRC" : {"symbol" : "CRC","displayName" : "Costa-Rica-Colón","displayName-count-one" : "Costa-Rica-Colón","symbol-alt-narrow" : "₡","displayName-count-other" : "Costa-Rica-Colón"},"CSD" : {"symbol" : "CSD","displayName" : "Serbischer Dinar (2002–2006)","displayName-count-one" : "Serbischer Dinar (2002–2006)","displayName-count-other" : "Serbische Dinar (2002–2006)"},"CSK" : {"symbol" : "CSK","displayName" : "Tschechoslowakische Krone","displayName-count-one" : "Tschechoslowakische Kronen","displayName-count-other" : "Tschechoslowakische Kronen"},"CUC" : {"symbol" : "CUC","displayName" : "Kubanischer Peso (konvertibel)","displayName-count-one" : "Kubanischer Peso (konvertibel)","symbol-alt-narrow" : "Cub$","displayName-count-other" : "Kubanische Pesos (konvertibel)"},"CUP" : {"symbol" : "CUP","displayName" : "Kubanischer Peso","displayName-count-one" : "Kubanischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Kubanische Pesos"},"CVE" : {"symbol" : "CVE","displayName" : "Cabo-Verde-Escudo","displayName-count-one" : "Cabo-Verde-Escudo","displayName-count-other" : "Cabo-Verde-Escudos"},"CYP" : {"symbol" : "CYP","displayName" : "Zypern-Pfund","displayName-count-one" : "Zypern Pfund","displayName-count-other" : "Zypern Pfund"},"CZK" : {"symbol" : "CZK","displayName" : "Tschechische Krone","displayName-count-one" : "Tschechische Krone","symbol-alt-narrow" : "Kč","displayName-count-other" : "Tschechische Kronen"},"DDM" : {"symbol" : "DDM","displayName" : "Mark der DDR","displayName-count-one" : "Mark der DDR","displayName-count-other" : "Mark der DDR"},"DEM" : {"symbol" : "DM","displayName" : "Deutsche Mark","displayName-count-one" : "Deutsche Mark","displayName-count-other" : "Deutsche Mark"},"DJF" : {"symbol" : "DJF","displayName" : "Dschibuti-Franc","displayName-count-one" : "Dschibuti-Franc","displayName-count-other" : "Dschibuti-Franc"},"DKK" : {"symbol" : "DKK","displayName" : "Dänische Krone","displayName-count-one" : "Dänische Krone","symbol-alt-narrow" : "kr","displayName-count-other" : "Dänische Kronen"},"DOP" : {"symbol" : "DOP","displayName" : "Dominikanischer Peso","displayName-count-one" : "Dominikanischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Dominikanische Pesos"},"DZD" : {"symbol" : "DZD","displayName" : "Algerischer Dinar","displayName-count-one" : "Algerischer Dinar","displayName-count-other" : "Algerische Dinar"},"ECS" : {"symbol" : "ECS","displayName" : "Ecuadorianischer Sucre","displayName-count-one" : "Ecuadorianischer Sucre","displayName-count-other" : "Ecuadorianische Sucre"},"ECV" : {"symbol" : "ECV","displayName" : "Verrechnungseinheit für Ecuador","displayName-count-one" : "Verrechnungseinheiten für Ecuador","displayName-count-other" : "Verrechnungseinheiten für Ecuador"},"EEK" : {"symbol" : "EEK","displayName" : "Estnische Krone","displayName-count-one" : "Estnische Krone","displayName-count-other" : "Estnische Kronen"},"EGP" : {"symbol" : "EGP","displayName" : "Ägyptisches Pfund","displayName-count-one" : "Ägyptisches Pfund","symbol-alt-narrow" : "E£","displayName-count-other" : "Ägyptische Pfund"},"ERN" : {"symbol" : "ERN","displayName" : "Eritreischer Nakfa","displayName-count-one" : "Eritreischer Nakfa","displayName-count-other" : "Eritreische Nakfa"},"ESA" : {"symbol" : "ESA","displayName" : "Spanische Peseta (A–Konten)","displayName-count-one" : "Spanische Peseta (A–Konten)","displayName-count-other" : "Spanische Peseten (A–Konten)"},"ESB" : {"symbol" : "ESB","displayName" : "Spanische Peseta (konvertibel)","displayName-count-one" : "Spanische Peseta (konvertibel)","displayName-count-other" : "Spanische Peseten (konvertibel)"},"ESP" : {"symbol" : "ESP","displayName" : "Spanische Peseta","displayName-count-one" : "Spanische Peseta","symbol-alt-narrow" : "₧","displayName-count-other" : "Spanische Peseten"},"ETB" : {"symbol" : "ETB","displayName" : "Äthiopischer Birr","displayName-count-one" : "Äthiopischer Birr","displayName-count-other" : "Äthiopische Birr"},"EUR" : {"symbol" : "€","displayName" : "Euro","displayName-count-one" : "Euro","symbol-alt-narrow" : "€","displayName-count-other" : "Euro"},"FIM" : {"symbol" : "FIM","displayName" : "Finnische Mark","displayName-count-one" : "Finnische Mark","displayName-count-other" : "Finnische Mark"},"FJD" : {"symbol" : "FJD","displayName" : "Fidschi-Dollar","displayName-count-one" : "Fidschi-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Fidschi-Dollar"},"FKP" : {"symbol" : "FKP","displayName" : "Falkland-Pfund","displayName-count-one" : "Falkland-Pfund","symbol-alt-narrow" : "Fl£","displayName-count-other" : "Falkland-Pfund"},"FRF" : {"symbol" : "FRF","displayName" : "Französischer Franc","displayName-count-one" : "Französischer Franc","displayName-count-other" : "Französische Franc"},"GBP" : {"symbol" : "£","displayName" : "Britisches Pfund","displayName-count-one" : "Britisches Pfund","symbol-alt-narrow" : "£","displayName-count-other" : "Britische Pfund"},"GEK" : {"symbol" : "GEK","displayName" : "Georgischer Kupon Larit","displayName-count-one" : "Georgischer Kupon Larit","displayName-count-other" : "Georgische Kupon Larit"},"GEL" : {"symbol" : "GEL","symbol-alt-variant" : "₾","displayName" : "Georgischer Lari","displayName-count-one" : "Georgischer Lari","symbol-alt-narrow" : "₾","displayName-count-other" : "Georgische Lari"},"GHC" : {"symbol" : "GHC","displayName" : "Ghanaischer Cedi (1979–2007)","displayName-count-one" : "Ghanaischer Cedi (1979–2007)","displayName-count-other" : "Ghanaische Cedi (1979–2007)"},"GHS" : {"symbol" : "GHS","displayName" : "Ghanaischer Cedi","displayName-count-one" : "Ghanaischer Cedi","displayName-count-other" : "Ghanaische Cedi"},"GIP" : {"symbol" : "GIP","displayName" : "Gibraltar-Pfund","displayName-count-one" : "Gibraltar-Pfund","symbol-alt-narrow" : "£","displayName-count-other" : "Gibraltar-Pfund"},"GMD" : {"symbol" : "GMD","displayName" : "Gambia-Dalasi","displayName-count-one" : "Gambia-Dalasi","displayName-count-other" : "Gambia-Dalasi"},"GNF" : {"symbol" : "GNF","displayName" : "Guinea-Franc","displayName-count-one" : "Guinea-Franc","symbol-alt-narrow" : "F.G.","displayName-count-other" : "Guinea-Franc"},"GNS" : {"symbol" : "GNS","displayName" : "Guineischer Syli","displayName-count-one" : "Guineischer Syli","displayName-count-other" : "Guineische Syli"},"GQE" : {"symbol" : "GQE","displayName" : "Äquatorialguinea-Ekwele","displayName-count-one" : "Äquatorialguinea-Ekwele","displayName-count-other" : "Äquatorialguinea-Ekwele"},"GRD" : {"symbol" : "GRD","displayName" : "Griechische Drachme","displayName-count-one" : "Griechische Drachme","displayName-count-other" : "Griechische Drachmen"},"GTQ" : {"symbol" : "GTQ","displayName" : "Guatemaltekischer Quetzal","displayName-count-one" : "Guatemaltekischer Quetzal","symbol-alt-narrow" : "Q","displayName-count-other" : "Guatemaltekische Quetzales"},"GWE" : {"symbol" : "GWE","displayName" : "Portugiesisch Guinea Escudo","displayName-count-one" : "Portugiesisch Guinea Escudo","displayName-count-other" : "Portugiesisch Guinea Escudo"},"GWP" : {"symbol" : "GWP","displayName" : "Guinea-Bissau Peso","displayName-count-one" : "Guinea-Bissau Peso","displayName-count-other" : "Guinea-Bissau Pesos"},"GYD" : {"symbol" : "GYD","displayName" : "Guyana-Dollar","displayName-count-one" : "Guyana-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Guyana-Dollar"},"HKD" : {"symbol" : "HK$","displayName" : "Hongkong-Dollar","displayName-count-one" : "Hongkong-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Hongkong-Dollar"},"HNL" : {"symbol" : "HNL","displayName" : "Honduras-Lempira","displayName-count-one" : "Honduras-Lempira","symbol-alt-narrow" : "L","displayName-count-other" : "Honduras-Lempira"},"HRD" : {"symbol" : "HRD","displayName" : "Kroatischer Dinar","displayName-count-one" : "Kroatischer Dinar","displayName-count-other" : "Kroatische Dinar"},"HRK" : {"symbol" : "HRK","displayName" : "Kroatischer Kuna","displayName-count-one" : "Kroatischer Kuna","symbol-alt-narrow" : "kn","displayName-count-other" : "Kroatische Kuna"},"HTG" : {"symbol" : "HTG","displayName" : "Haitianische Gourde","displayName-count-one" : "Haitianische Gourde","displayName-count-other" : "Haitianische Gourdes"},"HUF" : {"symbol" : "HUF","displayName" : "Ungarischer Forint","displayName-count-one" : "Ungarischer Forint","symbol-alt-narrow" : "Ft","displayName-count-other" : "Ungarische Forint"},"IDR" : {"symbol" : "IDR","displayName" : "Indonesische Rupiah","displayName-count-one" : "Indonesische Rupiah","symbol-alt-narrow" : "Rp","displayName-count-other" : "Indonesische Rupiah"},"IEP" : {"symbol" : "IEP","displayName" : "Irisches Pfund","displayName-count-one" : "Irisches Pfund","displayName-count-other" : "Irische Pfund"},"ILP" : {"symbol" : "ILP","displayName" : "Israelisches Pfund","displayName-count-one" : "Israelisches Pfund","displayName-count-other" : "Israelische Pfund"},"ILR" : {"symbol" : "ILR","displayName" : "Israelischer Schekel (1980–1985)","displayName-count-one" : "Israelischer Schekel (1980–1985)","displayName-count-other" : "Israelische Schekel (1980–1985)"},"ILS" : {"symbol" : "₪","displayName" : "Israelischer Neuer Schekel","displayName-count-one" : "Israelischer Neuer Schekel","symbol-alt-narrow" : "₪","displayName-count-other" : "Israelische Neue Schekel"},"INR" : {"symbol" : "₹","displayName" : "Indische Rupie","displayName-count-one" : "Indische Rupie","symbol-alt-narrow" : "₹","displayName-count-other" : "Indische Rupien"},"IQD" : {"symbol" : "IQD","displayName" : "Irakischer Dinar","displayName-count-one" : "Irakischer Dinar","displayName-count-other" : "Irakische Dinar"},"IRR" : {"symbol" : "IRR","displayName" : "Iranischer Rial","displayName-count-one" : "Iranischer Rial","displayName-count-other" : "Iranische Rial"},"ISJ" : {"symbol" : "ISJ","displayName" : "Isländische Krone (1918–1981)","displayName-count-one" : "Isländische Krone (1918–1981)","displayName-count-other" : "Isländische Kronen (1918–1981)"},"ISK" : {"symbol" : "ISK","displayName" : "Isländische Krone","displayName-count-one" : "Isländische Krone","symbol-alt-narrow" : "kr","displayName-count-other" : "Isländische Kronen"},"ITL" : {"symbol" : "ITL","displayName" : "Italienische Lira","displayName-count-one" : "Italienische Lira","displayName-count-other" : "Italienische Lire"},"JMD" : {"symbol" : "JMD","displayName" : "Jamaika-Dollar","displayName-count-one" : "Jamaika-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Jamaika-Dollar"},"JOD" : {"symbol" : "JOD","displayName" : "Jordanischer Dinar","displayName-count-one" : "Jordanischer Dinar","displayName-count-other" : "Jordanische Dinar"},"JPY" : {"symbol" : "¥","displayName" : "Japanischer Yen","displayName-count-one" : "Japanischer Yen","symbol-alt-narrow" : "¥","displayName-count-other" : "Japanische Yen"},"KES" : {"symbol" : "KES","displayName" : "Kenia-Schilling","displayName-count-one" : "Kenia-Schilling","displayName-count-other" : "Kenia-Schilling"},"KGS" : {"symbol" : "KGS","displayName" : "Kirgisischer Som","displayName-count-one" : "Kirgisischer Som","displayName-count-other" : "Kirgisische Som"},"KHR" : {"symbol" : "KHR","displayName" : "Kambodschanischer Riel","displayName-count-one" : "Kambodschanischer Riel","symbol-alt-narrow" : "៛","displayName-count-other" : "Kambodschanische Riel"},"KMF" : {"symbol" : "KMF","displayName" : "Komoren-Franc","displayName-count-one" : "Komoren-Franc","symbol-alt-narrow" : "FC","displayName-count-other" : "Komoren-Francs"},"KPW" : {"symbol" : "KPW","displayName" : "Nordkoreanischer Won","displayName-count-one" : "Nordkoreanischer Won","symbol-alt-narrow" : "₩","displayName-count-other" : "Nordkoreanische Won"},"KRH" : {"symbol" : "KRH","displayName" : "Südkoreanischer Hwan (1953–1962)","displayName-count-one" : "Südkoreanischer Hwan (1953–1962)","displayName-count-other" : "Südkoreanischer Hwan (1953–1962)"},"KRO" : {"symbol" : "KRO","displayName" : "Südkoreanischer Won (1945–1953)","displayName-count-one" : "Südkoreanischer Won (1945–1953)","displayName-count-other" : "Südkoreanischer Won (1945–1953)"},"KRW" : {"symbol" : "₩","displayName" : "Südkoreanischer Won","displayName-count-one" : "Südkoreanischer Won","symbol-alt-narrow" : "₩","displayName-count-other" : "Südkoreanische Won"},"KWD" : {"symbol" : "KWD","displayName" : "Kuwait-Dinar","displayName-count-one" : "Kuwait-Dinar","displayName-count-other" : "Kuwait-Dinar"},"KYD" : {"symbol" : "KYD","displayName" : "Kaiman-Dollar","displayName-count-one" : "Kaiman-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Kaiman-Dollar"},"KZT" : {"symbol" : "KZT","displayName" : "Kasachischer Tenge","displayName-count-one" : "Kasachischer Tenge","symbol-alt-narrow" : "₸","displayName-count-other" : "Kasachische Tenge"},"LAK" : {"symbol" : "LAK","displayName" : "Laotischer Kip","displayName-count-one" : "Laotischer Kip","symbol-alt-narrow" : "₭","displayName-count-other" : "Laotische Kip"},"LBP" : {"symbol" : "LBP","displayName" : "Libanesisches Pfund","displayName-count-one" : "Libanesisches Pfund","symbol-alt-narrow" : "L£","displayName-count-other" : "Libanesische Pfund"},"LKR" : {"symbol" : "LKR","displayName" : "Sri-Lanka-Rupie","displayName-count-one" : "Sri-Lanka-Rupie","symbol-alt-narrow" : "Rs","displayName-count-other" : "Sri-Lanka-Rupien"},"LRD" : {"symbol" : "LRD","displayName" : "Liberianischer Dollar","displayName-count-one" : "Liberianischer Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Liberianische Dollar"},"LSL" : {"symbol" : "LSL","displayName" : "Loti","displayName-count-one" : "Loti","displayName-count-other" : "Loti"},"LTL" : {"symbol" : "LTL","displayName" : "Litauischer Litas","displayName-count-one" : "Litauischer Litas","symbol-alt-narrow" : "Lt","displayName-count-other" : "Litauische Litas"},"LTT" : {"symbol" : "LTT","displayName" : "Litauischer Talonas","displayName-count-one" : "Litauische Talonas","displayName-count-other" : "Litauische Talonas"},"LUC" : {"symbol" : "LUC","displayName" : "Luxemburgischer Franc (konvertibel)","displayName-count-one" : "Luxemburgische Franc (konvertibel)","displayName-count-other" : "Luxemburgische Franc (konvertibel)"},"LUF" : {"symbol" : "LUF","displayName" : "Luxemburgischer Franc","displayName-count-one" : "Luxemburgische Franc","displayName-count-other" : "Luxemburgische Franc"},"LUL" : {"symbol" : "LUL","displayName" : "Luxemburgischer Finanz-Franc","displayName-count-one" : "Luxemburgische Finanz-Franc","displayName-count-other" : "Luxemburgische Finanz-Franc"},"LVL" : {"symbol" : "LVL","displayName" : "Lettischer Lats","displayName-count-one" : "Lettischer Lats","symbol-alt-narrow" : "Ls","displayName-count-other" : "Lettische Lats"},"LVR" : {"symbol" : "LVR","displayName" : "Lettischer Rubel","displayName-count-one" : "Lettische Rubel","displayName-count-other" : "Lettische Rubel"},"LYD" : {"symbol" : "LYD","displayName" : "Libyscher Dinar","displayName-count-one" : "Libyscher Dinar","displayName-count-other" : "Libysche Dinar"},"MAD" : {"symbol" : "MAD","displayName" : "Marokkanischer Dirham","displayName-count-one" : "Marokkanischer Dirham","displayName-count-other" : "Marokkanische Dirham"},"MAF" : {"symbol" : "MAF","displayName" : "Marokkanischer Franc","displayName-count-one" : "Marokkanische Franc","displayName-count-other" : "Marokkanische Franc"},"MCF" : {"symbol" : "MCF","displayName" : "Monegassischer Franc","displayName-count-one" : "Monegassischer Franc","displayName-count-other" : "Monegassische Franc"},"MDC" : {"symbol" : "MDC","displayName" : "Moldau-Cupon","displayName-count-one" : "Moldau-Cupon","displayName-count-other" : "Moldau-Cupon"},"MDL" : {"symbol" : "MDL","displayName" : "Moldau-Leu","displayName-count-one" : "Moldau-Leu","displayName-count-other" : "Moldau-Leu"},"MGA" : {"symbol" : "MGA","displayName" : "Madagaskar-Ariary","displayName-count-one" : "Madagaskar-Ariary","symbol-alt-narrow" : "Ar","displayName-count-other" : "Madagaskar-Ariary"},"MGF" : {"symbol" : "MGF","displayName" : "Madagaskar-Franc","displayName-count-one" : "Madagaskar-Franc","displayName-count-other" : "Madagaskar-Franc"},"MKD" : {"symbol" : "MKD","displayName" : "Mazedonischer Denar","displayName-count-one" : "Mazedonischer Denar","displayName-count-other" : "Mazedonische Denari"},"MKN" : {"symbol" : "MKN","displayName" : "Mazedonischer Denar (1992–1993)","displayName-count-one" : "Mazedonischer Denar (1992–1993)","displayName-count-other" : "Mazedonische Denar (1992–1993)"},"MLF" : {"symbol" : "MLF","displayName" : "Malischer Franc","displayName-count-one" : "Malische Franc","displayName-count-other" : "Malische Franc"},"MMK" : {"symbol" : "MMK","displayName" : "Myanmarischer Kyat","displayName-count-one" : "Myanmarischer Kyat","symbol-alt-narrow" : "K","displayName-count-other" : "Myanmarische Kyat"},"MNT" : {"symbol" : "MNT","displayName" : "Mongolischer Tögrög","displayName-count-one" : "Mongolischer Tögrög","symbol-alt-narrow" : "₮","displayName-count-other" : "Mongolische Tögrög"},"MOP" : {"symbol" : "MOP","displayName" : "Macao-Pataca","displayName-count-one" : "Macao-Pataca","displayName-count-other" : "Macao-Pataca"},"MRO" : {"symbol" : "MRO","displayName" : "Mauretanischer Ouguiya","displayName-count-one" : "Mauretanischer Ouguiya","displayName-count-other" : "Mauretanische Ouguiya"},"MTL" : {"symbol" : "MTL","displayName" : "Maltesische Lira","displayName-count-one" : "Maltesische Lira","displayName-count-other" : "Maltesische Lira"},"MTP" : {"symbol" : "MTP","displayName" : "Maltesisches Pfund","displayName-count-one" : "Maltesische Pfund","displayName-count-other" : "Maltesische Pfund"},"MUR" : {"symbol" : "MUR","displayName" : "Mauritius-Rupie","displayName-count-one" : "Mauritius-Rupie","symbol-alt-narrow" : "Rs","displayName-count-other" : "Mauritius-Rupien"},"MVP" : {"symbol" : "MVP","displayName" : "Malediven-Rupie (alt)","displayName-count-one" : "Malediven-Rupie (alt)","displayName-count-other" : "Malediven-Rupien (alt)"},"MVR" : {"symbol" : "MVR","displayName" : "Malediven-Rufiyaa","displayName-count-one" : "Malediven-Rufiyaa","displayName-count-other" : "Malediven-Rupien"},"MWK" : {"symbol" : "MWK","displayName" : "Malawi-Kwacha","displayName-count-one" : "Malawi-Kwacha","displayName-count-other" : "Malawi-Kwacha"},"MXN" : {"symbol" : "MX$","displayName" : "Mexikanischer Peso","displayName-count-one" : "Mexikanischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Mexikanische Pesos"},"MXP" : {"symbol" : "MXP","displayName" : "Mexikanischer Silber-Peso (1861–1992)","displayName-count-one" : "Mexikanische Silber-Peso (1861–1992)","displayName-count-other" : "Mexikanische Silber-Pesos (1861–1992)"},"MXV" : {"symbol" : "MXV","displayName" : "Mexicanischer Unidad de Inversion (UDI)","displayName-count-one" : "Mexicanischer Unidad de Inversion (UDI)","displayName-count-other" : "Mexikanische Unidad de Inversion (UDI)"},"MYR" : {"symbol" : "MYR","displayName" : "Malaysischer Ringgit","displayName-count-one" : "Malaysischer Ringgit","symbol-alt-narrow" : "RM","displayName-count-other" : "Malaysische Ringgit"},"MZE" : {"symbol" : "MZE","displayName" : "Mosambikanischer Escudo","displayName-count-one" : "Mozambikanische Escudo","displayName-count-other" : "Mozambikanische Escudo"},"MZM" : {"symbol" : "MZM","displayName" : "Mosambikanischer Metical (1980–2006)","displayName-count-one" : "Mosambikanischer Metical (1980–2006)","displayName-count-other" : "Mosambikanische Meticais (1980–2006)"},"MZN" : {"symbol" : "MZN","displayName" : "Mosambikanischer Metical","displayName-count-one" : "Mosambikanischer Metical","displayName-count-other" : "Mosambikanische Meticais"},"NAD" : {"symbol" : "NAD","displayName" : "Namibia-Dollar","displayName-count-one" : "Namibia-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Namibia-Dollar"},"NGN" : {"symbol" : "NGN","displayName" : "Nigerianischer Naira","displayName-count-one" : "Nigerianischer Naira","symbol-alt-narrow" : "₦","displayName-count-other" : "Nigerianische Naira"},"NIC" : {"symbol" : "NIC","displayName" : "Nicaraguanischer Córdoba (1988–1991)","displayName-count-one" : "Nicaraguanischer Córdoba (1988–1991)","displayName-count-other" : "Nicaraguanische Córdoba (1988–1991)"},"NIO" : {"symbol" : "NIO","displayName" : "Nicaragua-Córdoba","displayName-count-one" : "Nicaragua-Córdoba","symbol-alt-narrow" : "C$","displayName-count-other" : "Nicaragua-Córdobas"},"NLG" : {"symbol" : "NLG","displayName" : "Niederländischer Gulden","displayName-count-one" : "Niederländischer Gulden","displayName-count-other" : "Niederländische Gulden"},"NOK" : {"symbol" : "NOK","displayName" : "Norwegische Krone","displayName-count-one" : "Norwegische Krone","symbol-alt-narrow" : "kr","displayName-count-other" : "Norwegische Kronen"},"NPR" : {"symbol" : "NPR","displayName" : "Nepalesische Rupie","displayName-count-one" : "Nepalesische Rupie","symbol-alt-narrow" : "Rs","displayName-count-other" : "Nepalesische Rupien"},"NZD" : {"symbol" : "NZ$","displayName" : "Neuseeland-Dollar","displayName-count-one" : "Neuseeland-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Neuseeland-Dollar"},"OMR" : {"symbol" : "OMR","displayName" : "Omanischer Rial","displayName-count-one" : "Omanischer Rial","displayName-count-other" : "Omanische Rials"},"PAB" : {"symbol" : "PAB","displayName" : "Panamaischer Balboa","displayName-count-one" : "Panamaischer Balboa","displayName-count-other" : "Panamaische Balboas"},"PEI" : {"symbol" : "PEI","displayName" : "Peruanischer Inti","displayName-count-one" : "Peruanische Inti","displayName-count-other" : "Peruanische Inti"},"PEN" : {"symbol" : "PEN","displayName" : "Peruanischer Sol","displayName-count-one" : "Peruanischer Sol","displayName-count-other" : "Peruanische Sol"},"PES" : {"symbol" : "PES","displayName" : "Peruanischer Sol (1863–1965)","displayName-count-one" : "Peruanischer Sol (1863–1965)","displayName-count-other" : "Peruanische Sol (1863–1965)"},"PGK" : {"symbol" : "PGK","displayName" : "Papua-Neuguineischer Kina","displayName-count-one" : "Papua-Neuguineischer Kina","displayName-count-other" : "Papua-Neuguineische Kina"},"PHP" : {"symbol" : "PHP","displayName" : "Philippinischer Peso","displayName-count-one" : "Philippinischer Peso","symbol-alt-narrow" : "₱","displayName-count-other" : "Philippinische Pesos"},"PKR" : {"symbol" : "PKR","displayName" : "Pakistanische Rupie","displayName-count-one" : "Pakistanische Rupie","symbol-alt-narrow" : "Rs","displayName-count-other" : "Pakistanische Rupien"},"PLN" : {"symbol" : "PLN","displayName" : "Polnischer Złoty","displayName-count-one" : "Polnischer Złoty","symbol-alt-narrow" : "zł","displayName-count-other" : "Polnische Złoty"},"PLZ" : {"symbol" : "PLZ","displayName" : "Polnischer Zloty (1950–1995)","displayName-count-one" : "Polnischer Zloty (1950–1995)","displayName-count-other" : "Polnische Zloty (1950–1995)"},"PTE" : {"symbol" : "PTE","displayName" : "Portugiesischer Escudo","displayName-count-one" : "Portugiesische Escudo","displayName-count-other" : "Portugiesische Escudo"},"PYG" : {"symbol" : "PYG","displayName" : "Paraguayischer Guaraní","displayName-count-one" : "Paraguayischer Guaraní","symbol-alt-narrow" : "₲","displayName-count-other" : "Paraguayische Guaraníes"},"QAR" : {"symbol" : "QAR","displayName" : "Katar-Riyal","displayName-count-one" : "Katar-Riyal","displayName-count-other" : "Katar-Riyal"},"RHD" : {"symbol" : "RHD","displayName" : "Rhodesischer Dollar","displayName-count-one" : "Rhodesische Dollar","displayName-count-other" : "Rhodesische Dollar"},"ROL" : {"symbol" : "ROL","displayName" : "Rumänischer Leu (1952–2006)","displayName-count-one" : "Rumänischer Leu (1952–2006)","displayName-count-other" : "Rumänische Leu (1952–2006)"},"RON" : {"symbol" : "RON","displayName" : "Rumänischer Leu","displayName-count-one" : "Rumänischer Leu","symbol-alt-narrow" : "L","displayName-count-other" : "Rumänische Leu"},"RSD" : {"symbol" : "RSD","displayName" : "Serbischer Dinar","displayName-count-one" : "Serbischer Dinar","displayName-count-other" : "Serbische Dinaren"},"RUB" : {"symbol" : "RUB","displayName" : "Russischer Rubel","displayName-count-one" : "Russischer Rubel","symbol-alt-narrow" : "₽","displayName-count-other" : "Russische Rubel"},"RUR" : {"symbol" : "RUR","displayName" : "Russischer Rubel (1991–1998)","displayName-count-one" : "Russischer Rubel (1991–1998)","symbol-alt-narrow" : "р.","displayName-count-other" : "Russische Rubel (1991–1998)"},"RWF" : {"symbol" : "RWF","displayName" : "Ruanda-Franc","displayName-count-one" : "Ruanda-Franc","symbol-alt-narrow" : "F.Rw","displayName-count-other" : "Ruanda-Francs"},"SAR" : {"symbol" : "SAR","displayName" : "Saudi-Rial","displayName-count-one" : "Saudi-Rial","displayName-count-other" : "Saudi-Rial"},"SBD" : {"symbol" : "SBD","displayName" : "Salomonen-Dollar","displayName-count-one" : "Salomonen-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Salomonen-Dollar"},"SCR" : {"symbol" : "SCR","displayName" : "Seychellen-Rupie","displayName-count-one" : "Seychellen-Rupie","displayName-count-other" : "Seychellen-Rupien"},"SDD" : {"symbol" : "SDD","displayName" : "Sudanesischer Dinar (1992–2007)","displayName-count-one" : "Sudanesischer Dinar (1992–2007)","displayName-count-other" : "Sudanesische Dinar (1992–2007)"},"SDG" : {"symbol" : "SDG","displayName" : "Sudanesisches Pfund","displayName-count-one" : "Sudanesisches Pfund","displayName-count-other" : "Sudanesische Pfund"},"SDP" : {"symbol" : "SDP","displayName" : "Sudanesisches Pfund (1957–1998)","displayName-count-one" : "Sudanesisches Pfund (1957–1998)","displayName-count-other" : "Sudanesische Pfund (1957–1998)"},"SEK" : {"symbol" : "SEK","displayName" : "Schwedische Krone","displayName-count-one" : "Schwedische Krone","symbol-alt-narrow" : "kr","displayName-count-other" : "Schwedische Kronen"},"SGD" : {"symbol" : "SGD","displayName" : "Singapur-Dollar","displayName-count-one" : "Singapur-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Singapur-Dollar"},"SHP" : {"symbol" : "SHP","displayName" : "St. Helena-Pfund","displayName-count-one" : "St. Helena-Pfund","symbol-alt-narrow" : "£","displayName-count-other" : "St. Helena-Pfund"},"SIT" : {"symbol" : "SIT","displayName" : "Slowenischer Tolar","displayName-count-one" : "Slowenischer Tolar","displayName-count-other" : "Slowenische Tolar"},"SKK" : {"symbol" : "SKK","displayName" : "Slowakische Krone","displayName-count-one" : "Slowakische Kronen","displayName-count-other" : "Slowakische Kronen"},"SLL" : {"symbol" : "SLL","displayName" : "Sierra-leonischer Leone","displayName-count-one" : "Sierra-leonischer Leone","displayName-count-other" : "Sierra-leonische Leones"},"SOS" : {"symbol" : "SOS","displayName" : "Somalia-Schilling","displayName-count-one" : "Somalia-Schilling","displayName-count-other" : "Somalia-Schilling"},"SRD" : {"symbol" : "SRD","displayName" : "Suriname-Dollar","displayName-count-one" : "Suriname-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Suriname-Dollar"},"SRG" : {"symbol" : "SRG","displayName" : "Suriname Gulden","displayName-count-one" : "Suriname-Gulden","displayName-count-other" : "Suriname-Gulden"},"SSP" : {"symbol" : "SSP","displayName" : "Südsudanesisches Pfund","displayName-count-one" : "Südsudanesisches Pfund","symbol-alt-narrow" : "£","displayName-count-other" : "Südsudanesische Pfund"},"STD" : {"symbol" : "STD","displayName" : "São-toméischer Dobra","displayName-count-one" : "São-toméischer Dobra","symbol-alt-narrow" : "Db","displayName-count-other" : "São-toméische Dobra"},"STN" : {"symbol" : "STN","displayName" : "STN"},"SUR" : {"symbol" : "SUR","displayName" : "Sowjetischer Rubel","displayName-count-one" : "Sowjetische Rubel","displayName-count-other" : "Sowjetische Rubel"},"SVC" : {"symbol" : "SVC","displayName" : "El Salvador Colon","displayName-count-one" : "El Salvador-Colon","displayName-count-other" : "El Salvador-Colon"},"SYP" : {"symbol" : "SYP","displayName" : "Syrisches Pfund","displayName-count-one" : "Syrisches Pfund","symbol-alt-narrow" : "SYP","displayName-count-other" : "Syrische Pfund"},"SZL" : {"symbol" : "SZL","displayName" : "Swasiländischer Lilangeni","displayName-count-one" : "Swasiländischer Lilangeni","displayName-count-other" : "Swasiländische Emalangeni"},"THB" : {"symbol" : "฿","displayName" : "Thailändischer Baht","displayName-count-one" : "Thailändischer Baht","symbol-alt-narrow" : "฿","displayName-count-other" : "Thailändische Baht"},"TJR" : {"symbol" : "TJR","displayName" : "Tadschikistan Rubel","displayName-count-one" : "Tadschikistan-Rubel","displayName-count-other" : "Tadschikistan-Rubel"},"TJS" : {"symbol" : "TJS","displayName" : "Tadschikistan-Somoni","displayName-count-one" : "Tadschikistan-Somoni","displayName-count-other" : "Tadschikistan-Somoni"},"TMM" : {"symbol" : "TMM","displayName" : "Turkmenistan-Manat (1993–2009)","displayName-count-one" : "Turkmenistan-Manat (1993–2009)","displayName-count-other" : "Turkmenistan-Manat (1993–2009)"},"TMT" : {"symbol" : "TMT","displayName" : "Turkmenistan-Manat","displayName-count-one" : "Turkmenistan-Manat","displayName-count-other" : "Turkmenistan-Manat"},"TND" : {"symbol" : "TND","displayName" : "Tunesischer Dinar","displayName-count-one" : "Tunesischer Dinar","displayName-count-other" : "Tunesische Dinar"},"TOP" : {"symbol" : "TOP","displayName" : "Tongaischer Paʻanga","displayName-count-one" : "Tongaischer Paʻanga","symbol-alt-narrow" : "T$","displayName-count-other" : "Tongaische Paʻanga"},"TPE" : {"symbol" : "TPE","displayName" : "Timor-Escudo","displayName-count-one" : "Timor-Escudo","displayName-count-other" : "Timor-Escudo"},"TRL" : {"symbol" : "TRL","displayName" : "Türkische Lira (1922–2005)","displayName-count-one" : "Türkische Lira (1922–2005)","displayName-count-other" : "Türkische Lira (1922–2005)"},"TRY" : {"symbol" : "TRY","symbol-alt-variant" : "TL","displayName" : "Türkische Lira","displayName-count-one" : "Türkische Lira","symbol-alt-narrow" : "₺","displayName-count-other" : "Türkische Lira"},"TTD" : {"symbol" : "TTD","displayName" : "Trinidad und Tobago-Dollar","displayName-count-one" : "Trinidad und Tobago-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Trinidad und Tobago-Dollar"},"TWD" : {"symbol" : "NT$","displayName" : "Neuer Taiwan-Dollar","displayName-count-one" : "Neuer Taiwan-Dollar","symbol-alt-narrow" : "NT$","displayName-count-other" : "Neue Taiwan-Dollar"},"TZS" : {"symbol" : "TZS","displayName" : "Tansania-Schilling","displayName-count-one" : "Tansania-Schilling","displayName-count-other" : "Tansania-Schilling"},"UAH" : {"symbol" : "UAH","displayName" : "Ukrainische Hrywnja","displayName-count-one" : "Ukrainische Hrywnja","symbol-alt-narrow" : "₴","displayName-count-other" : "Ukrainische Hrywen"},"UAK" : {"symbol" : "UAK","displayName" : "Ukrainischer Karbovanetz","displayName-count-one" : "Ukrainische Karbovanetz","displayName-count-other" : "Ukrainische Karbovanetz"},"UGS" : {"symbol" : "UGS","displayName" : "Uganda-Schilling (1966–1987)","displayName-count-one" : "Uganda-Schilling (1966–1987)","displayName-count-other" : "Uganda-Schilling (1966–1987)"},"UGX" : {"symbol" : "UGX","displayName" : "Uganda-Schilling","displayName-count-one" : "Uganda-Schilling","displayName-count-other" : "Uganda-Schilling"},"USD" : {"symbol" : "$","displayName" : "US-Dollar","displayName-count-one" : "US-Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "US-Dollar"},"USN" : {"symbol" : "USN","displayName" : "US Dollar (Nächster Tag)","displayName-count-one" : "US-Dollar (Nächster Tag)","displayName-count-other" : "US-Dollar (Nächster Tag)"},"USS" : {"symbol" : "USS","displayName" : "US Dollar (Gleicher Tag)","displayName-count-one" : "US-Dollar (Gleicher Tag)","displayName-count-other" : "US-Dollar (Gleicher Tag)"},"UYI" : {"symbol" : "UYI","displayName" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)","displayName-count-one" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)","displayName-count-other" : "Uruguayische Pesos (Indexierte Rechnungseinheiten)"},"UYP" : {"symbol" : "UYP","displayName" : "Uruguayischer Peso (1975–1993)","displayName-count-one" : "Uruguayischer Peso (1975–1993)","displayName-count-other" : "Uruguayische Pesos (1975–1993)"},"UYU" : {"symbol" : "UYU","displayName" : "Uruguayischer Peso","displayName-count-one" : "Uruguayischer Peso","symbol-alt-narrow" : "$","displayName-count-other" : "Uruguayische Pesos"},"UZS" : {"symbol" : "UZS","displayName" : "Usbekistan-Sum","displayName-count-one" : "Usbekistan-Sum","displayName-count-other" : "Usbekistan-Sum"},"VEB" : {"symbol" : "VEB","displayName" : "Venezolanischer Bolívar (1871–2008)","displayName-count-one" : "Venezolanischer Bolívar (1871–2008)","displayName-count-other" : "Venezolanische Bolívares (1871–2008)"},"VEF" : {"symbol" : "VEF","displayName" : "Venezolanischer Bolívar","displayName-count-one" : "Venezolanischer Bolívar","symbol-alt-narrow" : "Bs","displayName-count-other" : "Venezolanische Bolívares"},"VND" : {"symbol" : "₫","displayName" : "Vietnamesischer Dong","displayName-count-one" : "Vietnamesischer Dong","symbol-alt-narrow" : "₫","displayName-count-other" : "Vietnamesische Dong"},"VNN" : {"symbol" : "VNN","displayName" : "Vietnamesischer Dong(1978–1985)","displayName-count-one" : "Vietnamesischer Dong(1978–1985)","displayName-count-other" : "Vietnamesische Dong(1978–1985)"},"VUV" : {"symbol" : "VUV","displayName" : "Vanuatu-Vatu","displayName-count-one" : "Vanuatu-Vatu","displayName-count-other" : "Vanuatu-Vatu"},"WST" : {"symbol" : "WST","displayName" : "Samoanischer Tala","displayName-count-one" : "Samoanischer Tala","displayName-count-other" : "Samoanische Tala"},"XAF" : {"symbol" : "FCFA","displayName" : "CFA-Franc (BEAC)","displayName-count-one" : "CFA-Franc (BEAC)","displayName-count-other" : "CFA-Franc (BEAC)"},"XAG" : {"symbol" : "XAG","displayName" : "Unze Silber","displayName-count-one" : "Unze Silber","displayName-count-other" : "Unzen Silber"},"XAU" : {"symbol" : "XAU","displayName" : "Unze Gold","displayName-count-one" : "Unze Gold","displayName-count-other" : "Unzen Gold"},"XBA" : {"symbol" : "XBA","displayName" : "Europäische Rechnungseinheit","displayName-count-one" : "Europäische Rechnungseinheiten","displayName-count-other" : "Europäische Rechnungseinheiten"},"XBB" : {"symbol" : "XBB","displayName" : "Europäische Währungseinheit (XBB)","displayName-count-one" : "Europäische Währungseinheiten (XBB)","displayName-count-other" : "Europäische Währungseinheiten (XBB)"},"XBC" : {"symbol" : "XBC","displayName" : "Europäische Rechnungseinheit (XBC)","displayName-count-one" : "Europäische Rechnungseinheiten (XBC)","displayName-count-other" : "Europäische Rechnungseinheiten (XBC)"},"XBD" : {"symbol" : "XBD","displayName" : "Europäische Rechnungseinheit (XBD)","displayName-count-one" : "Europäische Rechnungseinheiten (XBD)","displayName-count-other" : "Europäische Rechnungseinheiten (XBD)"},"XCD" : {"symbol" : "EC$","displayName" : "Ostkaribischer Dollar","displayName-count-one" : "Ostkaribischer Dollar","symbol-alt-narrow" : "$","displayName-count-other" : "Ostkaribische Dollar"},"XDR" : {"symbol" : "XDR","displayName" : "Sonderziehungsrechte","displayName-count-one" : "Sonderziehungsrechte","displayName-count-other" : "Sonderziehungsrechte"},"XEU" : {"symbol" : "XEU","displayName" : "Europäische Währungseinheit (XEU)","displayName-count-one" : "Europäische Währungseinheiten (XEU)","displayName-count-other" : "Europäische Währungseinheiten (XEU)"},"XFO" : {"symbol" : "XFO","displayName" : "Französischer Gold-Franc","displayName-count-one" : "Französische Gold-Franc","displayName-count-other" : "Französische Gold-Franc"},"XFU" : {"symbol" : "XFU","displayName" : "Französischer UIC-Franc","displayName-count-one" : "Französische UIC-Franc","displayName-count-other" : "Französische UIC-Franc"},"XOF" : {"symbol" : "CFA","displayName" : "CFA-Franc (BCEAO)","displayName-count-one" : "CFA-Franc (BCEAO)","displayName-count-other" : "CFA-Francs (BCEAO)"},"XPD" : {"symbol" : "XPD","displayName" : "Unze Palladium","displayName-count-one" : "Unze Palladium","displayName-count-other" : "Unzen Palladium"},"XPF" : {"symbol" : "CFPF","displayName" : "CFP-Franc","displayName-count-one" : "CFP-Franc","displayName-count-other" : "CFP-Franc"},"XPT" : {"symbol" : "XPT","displayName" : "Unze Platin","displayName-count-one" : "Unze Platin","displayName-count-other" : "Unzen Platin"},"XRE" : {"symbol" : "XRE","displayName" : "RINET Funds","displayName-count-one" : "RINET Funds","displayName-count-other" : "RINET Funds"},"XSU" : {"symbol" : "XSU","displayName" : "SUCRE","displayName-count-one" : "SUCRE","displayName-count-other" : "SUCRE"},"XTS" : {"symbol" : "XTS","displayName" : "Testwährung","displayName-count-one" : "Testwährung","displayName-count-other" : "Testwährung"},"XUA" : {"symbol" : "XUA","displayName" : "Rechnungseinheit der AfEB","displayName-count-one" : "Rechnungseinheit der AfEB","displayName-count-other" : "Rechnungseinheiten der AfEB"},"XXX" : {"symbol" : "XXX","displayName" : "Unbekannte Währung","displayName-count-one" : "(unbekannte Währung)","displayName-count-other" : "(unbekannte Währung)"},"YDD" : {"symbol" : "YDD","displayName" : "Jemen-Dinar","displayName-count-one" : "Jemen-Dinar","displayName-count-other" : "Jemen-Dinar"},"YER" : {"symbol" : "YER","displayName" : "Jemen-Rial","displayName-count-one" : "Jemen-Rial","displayName-count-other" : "Jemen-Rial"},"YUD" : {"symbol" : "YUD","displayName" : "Jugoslawischer Dinar (1966–1990)","displayName-count-one" : "Jugoslawischer Dinar (1966–1990)","displayName-count-other" : "Jugoslawische Dinar (1966–1990)"},"YUM" : {"symbol" : "YUM","displayName" : "Jugoslawischer Neuer Dinar (1994–2002)","displayName-count-one" : "Jugoslawischer Neuer Dinar (1994–2002)","displayName-count-other" : "Jugoslawische Neue Dinar (1994–2002)"},"YUN" : {"symbol" : "YUN","displayName" : "Jugoslawischer Dinar (konvertibel)","displayName-count-one" : "Jugoslawische Dinar (konvertibel)","displayName-count-other" : "Jugoslawische Dinar (konvertibel)"},"YUR" : {"symbol" : "YUR","displayName" : "Jugoslawischer reformierter Dinar (1992–1993)","displayName-count-one" : "Jugoslawischer reformierter Dinar (1992–1993)","displayName-count-other" : "Jugoslawische reformierte Dinar (1992–1993)"},"ZAL" : {"symbol" : "ZAL","displayName" : "Südafrikanischer Rand (Finanz)","displayName-count-one" : "Südafrikanischer Rand (Finanz)","displayName-count-other" : "Südafrikanischer Rand (Finanz)"},"ZAR" : {"symbol" : "ZAR","displayName" : "Südafrikanischer Rand","displayName-count-one" : "Südafrikanischer Rand","symbol-alt-narrow" : "R","displayName-count-other" : "Südafrikanische Rand"},"ZMK" : {"symbol" : "ZMK","displayName" : "Kwacha (1968–2012)","displayName-count-one" : "Kwacha (1968–2012)","displayName-count-other" : "Kwacha (1968–2012)"},"ZMW" : {"symbol" : "ZMW","displayName" : "Kwacha","displayName-count-one" : "Kwacha","symbol-alt-narrow" : "K","displayName-count-other" : "Kwacha"},"ZRN" : {"symbol" : "ZRN","displayName" : "Zaire-Neuer Zaïre (1993–1998)","displayName-count-one" : "Zaire-Neuer Zaïre (1993–1998)","displayName-count-other" : "Zaire-Neue Zaïre (1993–1998)"},"ZRZ" : {"symbol" : "ZRZ","displayName" : "Zaire-Zaïre (1971–1993)","displayName-count-one" : "Zaire-Zaïre (1971–1993)","displayName-count-other" : "Zaire-Zaïre (1971–1993)"},"ZWD" : {"symbol" : "ZWD","displayName" : "Simbabwe-Dollar (1980–2008)","displayName-count-one" : "Simbabwe-Dollar (1980–2008)","displayName-count-other" : "Simbabwe-Dollar (1980–2008)"},"ZWL" : {"symbol" : "ZWL","displayName" : "Simbabwe-Dollar (2009)","displayName-count-one" : "Simbabwe-Dollar (2009)","displayName-count-other" : "Simbabwe-Dollar (2009)"},"ZWR" : {"symbol" : "ZWR","displayName" : "Simbabwe-Dollar (2008)","displayName-count-one" : "Simbabwe-Dollar (2008)","displayName-count-other" : "Simbabwe-Dollar (2008)"}},"numbers":{"defaultNumberingSystem" : "latn","numberSymbols" : {"decimal" : ",","group" : ".","list" : ";","percentSign" : "%","plusSign" : "+","minusSign" : "-","exponential" : "E","superscriptingExponent" : "·","perMille" : "‰","infinity" : "∞","nan" : "NaN","timeSeparator" : ":"},"numberFormats" : {"decimalFormats" : "#,##0.###","percentFormats" : "#,##0 %","currencyFormats" : "#,##0.00 ¤","scientificFormats" : "#E0","decimalFormats-long" : {"decimalFormat" : {"1000-count-one" : "0 Tausend","1000-count-other" : "0 Tausend","10000-count-one" : "00 Tausend","10000-count-other" : "00 Tausend","100000-count-one" : "000 Tausend","100000-count-other" : "000 Tausend","1000000-count-one" : "0 Million","1000000-count-other" : "0 Millionen","10000000-count-one" : "00 Millionen","10000000-count-other" : "00 Millionen","100000000-count-one" : "000 Millionen","100000000-count-other" : "000 Millionen","1000000000-count-one" : "0 Milliarde","1000000000-count-other" : "0 Milliarden","10000000000-count-one" : "00 Milliarden","10000000000-count-other" : "00 Milliarden","100000000000-count-one" : "000 Milliarden","100000000000-count-other" : "000 Milliarden","1000000000000-count-one" : "0 Billion","1000000000000-count-other" : "0 Billionen","10000000000000-count-one" : "00 Billionen","10000000000000-count-other" : "00 Billionen","100000000000000-count-one" : "000 Billionen","100000000000000-count-other" : "000 Billionen"}},"decimalFormats-short" : {"decimalFormat" : {"1000-count-one" : "0 Tsd'.'","1000-count-other" : "0 Tsd'.'","10000-count-one" : "00 Tsd'.'","10000-count-other" : "00 Tsd'.'","100000-count-one" : "000 Tsd'.'","100000-count-other" : "000 Tsd'.'","1000000-count-one" : "0 Mio'.'","1000000-count-other" : "0 Mio'.'","10000000-count-one" : "00 Mio'.'","10000000-count-other" : "00 Mio'.'","100000000-count-one" : "000 Mio'.'","100000000-count-other" : "000 Mio'.'","1000000000-count-one" : "0 Mrd'.'","1000000000-count-other" : "0 Mrd'.'","10000000000-count-one" : "00 Mrd'.'","10000000000-count-other" : "00 Mrd'.'","100000000000-count-one" : "000 Mrd'.'","100000000000-count-other" : "000 Mrd'.'","1000000000000-count-one" : "0 Bio'.'","1000000000000-count-other" : "0 Bio'.'","10000000000000-count-one" : "00 Bio'.'","10000000000000-count-other" : "00 Bio'.'","100000000000000-count-one" : "000 Bio'.'","100000000000000-count-other" : "000 Bio'.'"}},"currencyFormats-short" : {"standard" : {"1000-count-one" : "0 Tsd'.' ¤","1000-count-other" : "0 Tsd'.' ¤","10000-count-one" : "00 Tsd'.' ¤","10000-count-other" : "00 Tsd'.' ¤","100000-count-one" : "000 Tsd'.' ¤","100000-count-other" : "000 Tsd'.' ¤","1000000-count-one" : "0 Mio'.' ¤","1000000-count-other" : "0 Mio'.' ¤","10000000-count-one" : "00 Mio'.' ¤","10000000-count-other" : "00 Mio'.' ¤","100000000-count-one" : "000 Mio'.' ¤","100000000-count-other" : "000 Mio'.' ¤","1000000000-count-one" : "0 Mrd'.' ¤","1000000000-count-other" : "0 Mrd'.' ¤","10000000000-count-one" : "00 Mrd'.' ¤","10000000000-count-other" : "00 Mrd'.' ¤","100000000000-count-one" : "000 Mrd'.' ¤","100000000000-count-other" : "000 Mrd'.' ¤","1000000000000-count-one" : "0 Bio'.' ¤","1000000000000-count-other" : "0 Bio'.' ¤","10000000000000-count-one" : "00 Bio'.' ¤","10000000000000-count-other" : "00 Bio'.' ¤","100000000000000-count-one" : "000 Bio'.' ¤","100000000000000-count-other" : "000 Bio'.' ¤"}}}},"supplemental":{"currencies":{"fractions" : {"CHF" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "5"},"ITL" : {"_rounding" : "0","_digits" : "0"},"ALL" : {"_rounding" : "0","_digits" : "0"},"DJF" : {"_rounding" : "0","_digits" : "0"},"STD" : {"_rounding" : "0","_digits" : "0"},"CLP" : {"_rounding" : "0","_digits" : "0"},"UGX" : {"_rounding" : "0","_digits" : "0"},"TND" : {"_rounding" : "0","_digits" : "3"},"TZS" : {"_rounding" : "0","_digits" : "0"},"ADP" : {"_rounding" : "0","_digits" : "0"},"VND" : {"_rounding" : "0","_digits" : "0"},"TRL" : {"_rounding" : "0","_digits" : "0"},"SLL" : {"_rounding" : "0","_digits" : "0"},"GYD" : {"_rounding" : "0","_digits" : "0"},"KPW" : {"_rounding" : "0","_digits" : "0"},"IDR" : {"_rounding" : "0","_digits" : "0"},"AMD" : {"_rounding" : "0","_digits" : "0"},"LBP" : {"_rounding" : "0","_digits" : "0"},"IQD" : {"_rounding" : "0","_digits" : "0"},"JOD" : {"_rounding" : "0","_digits" : "3"},"RWF" : {"_rounding" : "0","_digits" : "0"},"TWD" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"RSD" : {"_rounding" : "0","_digits" : "0"},"UYI" : {"_rounding" : "0","_digits" : "0"},"DKK" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "50"},"KMF" : {"_rounding" : "0","_digits" : "0"},"ZWD" : {"_rounding" : "0","_digits" : "0"},"CAD" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "5"},"MMK" : {"_rounding" : "0","_digits" : "0"},"MUR" : {"_rounding" : "0","_digits" : "0"},"NOK" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"SYP" : {"_rounding" : "0","_digits" : "0"},"XOF" : {"_rounding" : "0","_digits" : "0"},"CRC" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"CZK" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"OMR" : {"_rounding" : "0","_digits" : "3"},"PKR" : {"_rounding" : "0","_digits" : "0"},"SEK" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"GNF" : {"_rounding" : "0","_digits" : "0"},"BHD" : {"_rounding" : "0","_digits" : "3"},"YER" : {"_rounding" : "0","_digits" : "0"},"IRR" : {"_rounding" : "0","_digits" : "0"},"AFN" : {"_rounding" : "0","_digits" : "0"},"MRO" : {"_rounding" : "0","_digits" : "0"},"UZS" : {"_rounding" : "0","_digits" : "0"},"XPF" : {"_rounding" : "0","_digits" : "0"},"KRW" : {"_rounding" : "0","_digits" : "0"},"JPY" : {"_rounding" : "0","_digits" : "0"},"MNT" : {"_rounding" : "0","_digits" : "0"},"BYN" : {"_rounding" : "0","_digits" : "2"},"LYD" : {"_rounding" : "0","_digits" : "3"},"HUF" : {"_rounding" : "0","_digits" : "2","_cashRounding" : "0","_cashDigits" : "0"},"KWD" : {"_rounding" : "0","_digits" : "3"},"BYR" : {"_rounding" : "0","_digits" : "0"},"LUF" : {"_rounding" : "0","_digits" : "0"},"BIF" : {"_rounding" : "0","_digits" : "0"},"PYG" : {"_rounding" : "0","_digits" : "0"},"ISK" : {"_rounding" : "0","_digits" : "0"},"ESP" : {"_rounding" : "0","_digits" : "0"},"COP" : {"_rounding" : "0","_digits" : "0"},"MGA" : {"_rounding" : "0","_digits" : "0"},"MGF" : {"_rounding" : "0","_digits" : "0"},"TMM" : {"_rounding" : "0","_digits" : "0"},"SOS" : {"_rounding" : "0","_digits" : "0"},"VUV" : {"_rounding" : "0","_digits" : "0"},"LAK" : {"_rounding" : "0","_digits" : "0"},"ZMK" : {"_rounding" : "0","_digits" : "0"},"CLF" : {"_rounding" : "0","_digits" : "4"},"XAF" : {"_rounding" : "0","_digits" : "0"},"DEFAULT" : {"_rounding" : "0","_digits" : "2"}},"region" : {"AC" : "SHP","AD" : "EUR","AE" : "AED","AF" : "AFN","AG" : "XCD","AI" : "XCD","AL" : "ALL","AM" : "AMD","AO" : "AOA","AR" : "ARS","AS" : "USD","AT" : "EUR","AU" : "AUD","AW" : "AWG","AX" : "EUR","AZ" : "AZN","BA" : "BAM","BB" : "BBD","BD" : "BDT","BE" : "EUR","BF" : "XOF","BG" : "BGN","BH" : "BHD","BI" : "BIF","BJ" : "XOF","BL" : "EUR","BM" : "BMD","BN" : "BND","BO" : "BOB","BQ" : "USD","BR" : "BRL","BS" : "BSD","BT" : "BTN","BV" : "NOK","BW" : "BWP","BY" : "BYN","BZ" : "BZD","CA" : "CAD","CC" : "AUD","CD" : "CDF","CF" : "XAF","CG" : "XAF","CH" : "CHF","CI" : "XOF","CK" : "NZD","CL" : "CLP","CM" : "XAF","CN" : "CNH","CO" : "COP","CR" : "CRC","CU" : "CUC","CV" : "CVE","CW" : "ANG","CX" : "AUD","CY" : "EUR","CZ" : "CZK","DE" : "EUR","DG" : "USD","DJ" : "DJF","DK" : "DKK","DM" : "XCD","DO" : "DOP","DZ" : "DZD","EA" : "EUR","EC" : "USD","EE" : "EUR","EG" : "EGP","EH" : "MAD","ER" : "ERN","ES" : "EUR","ET" : "ETB","EU" : "EUR","FI" : "EUR","FJ" : "FJD","FK" : "FKP","FM" : "USD","FO" : "DKK","FR" : "EUR","GA" : "XAF","GB" : "GBP","GD" : "XCD","GE" : "GEL","GF" : "EUR","GG" : "GBP","GH" : "GHS","GI" : "GIP","GL" : "DKK","GM" : "GMD","GN" : "GNF","GP" : "EUR","GQ" : "XAF","GR" : "EUR","GS" : "GBP","GT" : "GTQ","GU" : "USD","GW" : "XOF","GY" : "GYD","HK" : "HKD","HM" : "AUD","HN" : "HNL","HR" : "HRK","HT" : "USD","HU" : "HUF","IC" : "EUR","ID" : "IDR","IE" : "EUR","IL" : "ILS","IM" : "GBP","IN" : "INR","IO" : "USD","IQ" : "IQD","IR" : "IRR","IS" : "ISK","IT" : "EUR","JE" : "GBP","JM" : "JMD","JO" : "JOD","JP" : "JPY","KE" : "KES","KG" : "KGS","KH" : "KHR","KI" : "AUD","KM" : "KMF","KN" : "XCD","KP" : "KPW","KR" : "KRW","KW" : "KWD","KY" : "KYD","KZ" : "KZT","LA" : "LAK","LB" : "LBP","LC" : "XCD","LI" : "CHF","LK" : "LKR","LR" : "LRD","LS" : "LSL","LT" : "EUR","LU" : "EUR","LV" : "EUR","LY" : "LYD","MA" : "MAD","MC" : "EUR","MD" : "MDL","ME" : "EUR","MF" : "EUR","MG" : "MGA","MH" : "USD","MK" : "MKD","ML" : "XOF","MM" : "MMK","MN" : "MNT","MO" : "MOP","MP" : "USD","MQ" : "EUR","MR" : "MRO","MS" : "XCD","MT" : "EUR","MU" : "MUR","MV" : "MVR","MW" : "MWK","MX" : "MXN","MY" : "MYR","MZ" : "MZN","NA" : "NAD","NC" : "XPF","NE" : "XOF","NF" : "AUD","NG" : "NGN","NI" : "NIO","NL" : "EUR","NO" : "NOK","NP" : "NPR","NR" : "AUD","NU" : "NZD","NZ" : "NZD","OM" : "OMR","PA" : "USD","PE" : "PEN","PF" : "XPF","PG" : "PGK","PH" : "PHP","PK" : "PKR","PL" : "PLN","PM" : "EUR","PN" : "NZD","PR" : "USD","PS" : "JOD","PT" : "EUR","PW" : "USD","PY" : "PYG","QA" : "QAR","RE" : "EUR","RO" : "RON","RS" : "RSD","RU" : "RUB","RW" : "RWF","SA" : "SAR","SB" : "SBD","SC" : "SCR","SD" : "SDG","SE" : "SEK","SG" : "SGD","SH" : "SHP","SI" : "EUR","SJ" : "NOK","SK" : "EUR","SL" : "SLL","SM" : "EUR","SN" : "XOF","SO" : "SOS","SR" : "SRD","SS" : "SSP","ST" : "STN","SV" : "USD","SX" : "ANG","SY" : "SYP","SZ" : "SZL","TA" : "GBP","TC" : "USD","TD" : "XAF","TF" : "EUR","TG" : "XOF","TH" : "THB","TJ" : "TJS","TK" : "NZD","TL" : "USD","TM" : "TMT","TN" : "TND","TO" : "TOP","TR" : "TRY","TT" : "TTD","TV" : "AUD","TW" : "TWD","TZ" : "TZS","UA" : "UAH","UG" : "UGX","UM" : "USD","US" : "USD","UY" : "UYU","UZ" : "UZS","VA" : "EUR","VC" : "XCD","VE" : "VEF","VG" : "USD","VI" : "USD","VN" : "VND","VU" : "VUV","WF" : "XPF","WS" : "WST","XK" : "EUR","YE" : "YER","YT" : "EUR","ZA" : "ZAR","ZM" : "ZMW","ZW" : "USD"}},"numbers":{"numberingSystems" : {"adlm" : {"_type" : "numeric","_digits" : "𞥐𞥑𞥒𞥓𞥔𞥕𞥖𞥗𞥘𞥙"},"ahom" : {"_type" : "numeric","_digits" : "𑜰𑜱𑜲𑜳𑜴𑜵𑜶𑜷𑜸𑜹"},"arab" : {"_type" : "numeric","_digits" : "٠١٢٣٤٥٦٧٨٩"},"arabext" : {"_type" : "numeric","_digits" : "۰۱۲۳۴۵۶۷۸۹"},"armn" : {"_rules" : "armenian-upper","_type" : "algorithmic"},"armnlow" : {"_rules" : "armenian-lower","_type" : "algorithmic"},"bali" : {"_type" : "numeric","_digits" : "᭐᭑᭒᭓᭔᭕᭖᭗᭘᭙"},"beng" : {"_type" : "numeric","_digits" : "০১২৩৪৫৬৭৮৯"},"bhks" : {"_type" : "numeric","_digits" : "𑱐𑱑𑱒𑱓𑱔𑱕𑱖𑱗𑱘𑱙"},"brah" : {"_type" : "numeric","_digits" : "𑁦𑁧𑁨𑁩𑁪𑁫𑁬𑁭𑁮𑁯"},"cakm" : {"_type" : "numeric","_digits" : "𑄶𑄷𑄸𑄹𑄺𑄻𑄼𑄽𑄾𑄿"},"cham" : {"_type" : "numeric","_digits" : "꩐꩑꩒꩓꩔꩕꩖꩗꩘꩙"},"cyrl" : {"_rules" : "cyrillic-lower","_type" : "algorithmic"},"deva" : {"_type" : "numeric","_digits" : "०१२३४५६७८९"},"ethi" : {"_rules" : "ethiopic","_type" : "algorithmic"},"fullwide" : {"_type" : "numeric","_digits" : "０１２３４５６７８９"},"geor" : {"_rules" : "georgian","_type" : "algorithmic"},"gonm" : {"_type" : "numeric","_digits" : "𑵐𑵑𑵒𑵓𑵔𑵕𑵖𑵗𑵘𑵙"},"grek" : {"_rules" : "greek-upper","_type" : "algorithmic"},"greklow" : {"_rules" : "greek-lower","_type" : "algorithmic"},"gujr" : {"_type" : "numeric","_digits" : "૦૧૨૩૪૫૬૭૮૯"},"guru" : {"_type" : "numeric","_digits" : "੦੧੨੩੪੫੬੭੮੯"},"hanidays" : {"_rules" : "zh/SpelloutRules/spellout-numbering-days","_type" : "algorithmic"},"hanidec" : {"_type" : "numeric","_digits" : "〇一二三四五六七八九"},"hans" : {"_rules" : "zh/SpelloutRules/spellout-cardinal","_type" : "algorithmic"},"hansfin" : {"_rules" : "zh/SpelloutRules/spellout-cardinal-financial","_type" : "algorithmic"},"hant" : {"_rules" : "zh_Hant/SpelloutRules/spellout-cardinal","_type" : "algorithmic"},"hantfin" : {"_rules" : "zh_Hant/SpelloutRules/spellout-cardinal-financial","_type" : "algorithmic"},"hebr" : {"_rules" : "hebrew","_type" : "algorithmic"},"hmng" : {"_type" : "numeric","_digits" : "𖭐𖭑𖭒𖭓𖭔𖭕𖭖𖭗𖭘𖭙"},"java" : {"_type" : "numeric","_digits" : "꧐꧑꧒꧓꧔꧕꧖꧗꧘꧙"},"jpan" : {"_rules" : "ja/SpelloutRules/spellout-cardinal","_type" : "algorithmic"},"jpanfin" : {"_rules" : "ja/SpelloutRules/spellout-cardinal-financial","_type" : "algorithmic"},"kali" : {"_type" : "numeric","_digits" : "꤀꤁꤂꤃꤄꤅꤆꤇꤈꤉"},"khmr" : {"_type" : "numeric","_digits" : "០១២៣៤៥៦៧៨៩"},"knda" : {"_type" : "numeric","_digits" : "೦೧೨೩೪೫೬೭೮೯"},"lana" : {"_type" : "numeric","_digits" : "᪀᪁᪂᪃᪄᪅᪆᪇᪈᪉"},"lanatham" : {"_type" : "numeric","_digits" : "᪐᪑᪒᪓᪔᪕᪖᪗᪘᪙"},"laoo" : {"_type" : "numeric","_digits" : "໐໑໒໓໔໕໖໗໘໙"},"latn" : {"_type" : "numeric","_digits" : "0123456789"},"lepc" : {"_type" : "numeric","_digits" : "᱀᱁᱂᱃᱄᱅᱆᱇᱈᱉"},"limb" : {"_type" : "numeric","_digits" : "᥆᥇᥈᥉᥊᥋᥌᥍᥎᥏"},"mathbold" : {"_type" : "numeric","_digits" : "𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗"},"mathdbl" : {"_type" : "numeric","_digits" : "𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡"},"mathmono" : {"_type" : "numeric","_digits" : "𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿"},"mathsanb" : {"_type" : "numeric","_digits" : "𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵"},"mathsans" : {"_type" : "numeric","_digits" : "𝟢𝟣𝟤𝟥𝟦𝟧𝟨𝟩𝟪𝟫"},"mlym" : {"_type" : "numeric","_digits" : "൦൧൨൩൪൫൬൭൮൯"},"modi" : {"_type" : "numeric","_digits" : "𑙐𑙑𑙒𑙓𑙔𑙕𑙖𑙗𑙘𑙙"},"mong" : {"_type" : "numeric","_digits" : "᠐᠑᠒᠓᠔᠕᠖᠗᠘᠙"},"mroo" : {"_type" : "numeric","_digits" : "𖩠𖩡𖩢𖩣𖩤𖩥𖩦𖩧𖩨𖩩"},"mtei" : {"_type" : "numeric","_digits" : "꯰꯱꯲꯳꯴꯵꯶꯷꯸꯹"},"mymr" : {"_type" : "numeric","_digits" : "၀၁၂၃၄၅၆၇၈၉"},"mymrshan" : {"_type" : "numeric","_digits" : "႐႑႒႓႔႕႖႗႘႙"},"mymrtlng" : {"_type" : "numeric","_digits" : "꧰꧱꧲꧳꧴꧵꧶꧷꧸꧹"},"newa" : {"_type" : "numeric","_digits" : "𑑐𑑑𑑒𑑓𑑔𑑕𑑖𑑗𑑘𑑙"},"nkoo" : {"_type" : "numeric","_digits" : "߀߁߂߃߄߅߆߇߈߉"},"olck" : {"_type" : "numeric","_digits" : "᱐᱑᱒᱓᱔᱕᱖᱗᱘᱙"},"orya" : {"_type" : "numeric","_digits" : "୦୧୨୩୪୫୬୭୮୯"},"osma" : {"_type" : "numeric","_digits" : "𐒠𐒡𐒢𐒣𐒤𐒥𐒦𐒧𐒨𐒩"},"roman" : {"_rules" : "roman-upper","_type" : "algorithmic"},"romanlow" : {"_rules" : "roman-lower","_type" : "algorithmic"},"saur" : {"_type" : "numeric","_digits" : "꣐꣑꣒꣓꣔꣕꣖꣗꣘꣙"},"shrd" : {"_type" : "numeric","_digits" : "𑇐𑇑𑇒𑇓𑇔𑇕𑇖𑇗𑇘𑇙"},"sind" : {"_type" : "numeric","_digits" : "𑋰𑋱𑋲𑋳𑋴𑋵𑋶𑋷𑋸𑋹"},"sinh" : {"_type" : "numeric","_digits" : "෦෧෨෩෪෫෬෭෮෯"},"sora" : {"_type" : "numeric","_digits" : "𑃰𑃱𑃲𑃳𑃴𑃵𑃶𑃷𑃸𑃹"},"sund" : {"_type" : "numeric","_digits" : "᮰᮱᮲᮳᮴᮵᮶᮷᮸᮹"},"takr" : {"_type" : "numeric","_digits" : "𑛀𑛁𑛂𑛃𑛄𑛅𑛆𑛇𑛈𑛉"},"talu" : {"_type" : "numeric","_digits" : "᧐᧑᧒᧓᧔᧕᧖᧗᧘᧙"},"taml" : {"_rules" : "tamil","_type" : "algorithmic"},"tamldec" : {"_type" : "numeric","_digits" : "௦௧௨௩௪௫௬௭௮௯"},"telu" : {"_type" : "numeric","_digits" : "౦౧౨౩౪౫౬౭౮౯"},"thai" : {"_type" : "numeric","_digits" : "๐๑๒๓๔๕๖๗๘๙"},"tibt" : {"_type" : "numeric","_digits" : "༠༡༢༣༤༥༦༧༨༩"},"tirh" : {"_type" : "numeric","_digits" : "𑓐𑓑𑓒𑓓𑓔𑓕𑓖𑓗𑓘𑓙"},"vaii" : {"_type" : "numeric","_digits" : "꘠꘡꘢꘣꘤꘥꘦꘧꘨꘩"},"wara" : {"_type" : "numeric","_digits" : "𑣠𑣡𑣢𑣣𑣤𑣥𑣦𑣧𑣨𑣩"}}}}},"isExistPattern":true}}}`},
		{combine: 2, language: "zh-Hans", region: "", scope: "dates", components: "sunglow", name: Name, version: Version,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"components":[{"productName":"VPE","version":"1.0.0","locale":"zh-Hans","component":"sunglow","messages":{
					            "plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}",
					            "message": "消息",
					            "pagination": "{0}-{1} 个客户，共 {2} 个",
					            "one.arg": "测试一个参数{0}"
					          }}],"pattern":{"localeID":"zh-Hans","language":"zh","region":"CN","categories":{"dates":{
					              "dayPeriodsFormat" : {
					                "narrow" : [ "上午", "下午" ],
					                "abbreviated" : [ "上午", "下午" ],
					                "wide" : [ "上午", "下午" ]
					              },
					              "dayPeriodsStandalone" : {
					                "narrow" : [ "上午", "下午" ],
					                "abbreviated" : [ "上午", "下午" ],
					                "wide" : [ "上午", "下午" ]
					              },
					              "daysFormat" : {
					                "narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],
					                "abbreviated" : [ "周日", "周一", "周二", "周三", "周四", "周五", "周六" ],
					                "wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],
					                "short" : [ "周日", "周一", "周二", "周三", "周四", "周五", "周六" ]
					              },
					              "daysStandalone" : {
					                "narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],
					                "abbreviated" : [ "周日", "周一", "周二", "周三", "周四", "周五", "周六" ],
					                "wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],
					                "short" : [ "周日", "周一", "周二", "周三", "周四", "周五", "周六" ]
					              },
					              "monthsFormat" : {
					                "narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],
					                "abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],
					                "wide" : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" ]
					              },
					              "monthsStandalone" : {
					                "narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],
					                "abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],
					                "wide" : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" ]
					              },
					              "eras" : {
					                "narrow" : [ "公元前", "公元" ],
					                "abbreviated" : [ "公元前", "公元" ],
					                "wide" : [ "公元前", "公元" ]
					              },
					              "firstDayOfWeek" : 0,
					              "weekendRange" : [ 6, 0 ],
					              "dateFormats" : {
					                "short" : "y/M/d",
					                "medium" : "y年M月d日",
					                "long" : "y年M月d日",
					                "full" : "y年M月d日EEEE"
					              },
					              "timeFormats" : {
					                "short" : "ah:mm",
					                "medium" : "ah:mm:ss",
					                "long" : "z ah:mm:ss",
					                "full" : "zzzz ah:mm:ss"
					              },
					              "dateTimeFormats" : {
					                "short" : "{1} {0}",
					                "medium" : "{1} {0}",
					                "long" : "{1} {0}",
					                "full" : "{1} {0}",
					                "appendItems" : {
					                  "Day" : "{0} ({2}: {1})",
					                  "Day-Of-Week" : "{0} {1}",
					                  "Era" : "{1} {0}",
					                  "Hour" : "{0} ({2}: {1})",
					                  "Minute" : "{0} ({2}: {1})",
					                  "Month" : "{0} ({2}: {1})",
					                  "Quarter" : "{0} ({2}: {1})",
					                  "Second" : "{0} ({2}: {1})",
					                  "Timezone" : "{1}{0}",
					                  "Week" : "{0} ({2}: {1})",
					                  "Year" : "{1} {0}"
					                },
					                "intervalFormats" : {
					                  "d" : {
					                    "d" : "d–d日"
					                  },
					                  "h" : {
					                    "a" : "ah时至ah时",
					                    "h" : "ah时至h时"
					                  },
					                  "H" : {
					                    "H" : "HH–HH"
					                  },
					                  "hm" : {
					                    "a" : "ah:mm至ah:mm",
					                    "h" : "ah:mm至h:mm",
					                    "m" : "ah:mm至h:mm"
					                  },
					                  "Hm" : {
					                    "H" : "HH:mm–HH:mm",
					                    "m" : "HH:mm–HH:mm"
					                  },
					                  "hmv" : {
					                    "a" : "vah:mm至ah:mm",
					                    "h" : "vah:mm至h:mm",
					                    "m" : "vah:mm至h:mm"
					                  },
					                  "Hmv" : {
					                    "H" : "v HH:mm–HH:mm",
					                    "m" : "v HH:mm–HH:mm"
					                  },
					                  "hv" : {
					                    "a" : "vah时至ah时",
					                    "h" : "vah时至h时"
					                  },
					                  "Hv" : {
					                    "H" : "v HH–HH"
					                  },
					                  "intervalFormatFallback" : "{0} – {1}",
					                  "M" : {
					                    "M" : "M–M月"
					                  },
					                  "Md" : {
					                    "d" : "M/d – M/d",
					                    "M" : "M/d – M/d"
					                  },
					                  "MEd" : {
					                    "d" : "M/dE至M/dE",
					                    "M" : "M/dE至M/dE"
					                  },
					                  "MMM" : {
					                    "M" : "MMM – MMM"
					                  },
					                  "MMMd" : {
					                    "d" : "M月d日至d日",
					                    "M" : "M月d日至M月d日"
					                  },
					                  "MMMEd" : {
					                    "d" : "M月d日E至d日E",
					                    "M" : "M月d日E至M月d日E"
					                  },
					                  "y" : {
					                    "y" : "y–y年"
					                  },
					                  "yM" : {
					                    "y" : "y年M月至y年M月",
					                    "M" : "y年M月至M月"
					                  },
					                  "yMd" : {
					                    "d" : "y/M/d – y/M/d",
					                    "y" : "y/M/d – y/M/d",
					                    "M" : "y/M/d – y/M/d"
					                  },
					                  "yMEd" : {
					                    "d" : "y/M/dE至y/M/dE",
					                    "y" : "y/M/dE至y/M/dE",
					                    "M" : "y/M/dE至y/M/dE"
					                  },
					                  "yMMM" : {
					                    "y" : "y年M月至y年M月",
					                    "M" : "y年M月至M月"
					                  },
					                  "yMMMd" : {
					                    "d" : "y年M月d日至d日",
					                    "y" : "y年M月d日至y年M月d日",
					                    "M" : "y年M月d日至M月d日"
					                  },
					                  "yMMMEd" : {
					                    "d" : "y年M月d日E至d日E",
					                    "y" : "y年M月d日E至y年M月d日E",
					                    "M" : "y年M月d日E至M月d日E"
					                  },
					                  "yMMMM" : {
					                    "y" : "y年M月至y年M月",
					                    "M" : "y年M月至M月"
					                  }
					                },
					                "availableFormats" : {
					                  "Bh" : "Bh时",
					                  "Bhm" : "Bh:mm",
					                  "Bhms" : "Bh:mm:ss",
					                  "d" : "d日",
					                  "E" : "ccc",
					                  "EBhm" : "EBh:mm",
					                  "EBhms" : "EBh:mm:ss",
					                  "Ed" : "d日E",
					                  "Ehm" : "Eah:mm",
					                  "EHm" : "EHH:mm",
					                  "Ehms" : "Eah:mm:ss",
					                  "EHms" : "EHH:mm:ss",
					                  "Gy" : "Gy年",
					                  "GyMMM" : "Gy年M月",
					                  "GyMMMd" : "Gy年M月d日",
					                  "GyMMMEd" : "Gy年M月d日E",
					                  "H" : "H时",
					                  "h" : "ah时",
					                  "hm" : "ah:mm",
					                  "Hm" : "HH:mm",
					                  "hms" : "ah:mm:ss",
					                  "Hms" : "HH:mm:ss",
					                  "hmsv" : "v ah:mm:ss",
					                  "Hmsv" : "v HH:mm:ss",
					                  "hmv" : "v ah:mm",
					                  "Hmv" : "v HH:mm",
					                  "hmz" : "zzzz ah:mm",
					                  "M" : "M月",
					                  "Md" : "M/d",
					                  "MEd" : "M/dE",
					                  "MMdd" : "MM/dd",
					                  "MMM" : "LLL",
					                  "MMMd" : "M月d日",
					                  "MMMEd" : "M月d日E",
					                  "MMMMd" : "M月d日",
					                  "MMMMW-count-other" : "MMM第W周",
					                  "ms" : "mm:ss",
					                  "y" : "y年",
					                  "yM" : "y年M月",
					                  "yMd" : "y/M/d",
					                  "yMEd" : "y/M/dE",
					                  "yMM" : "y年M月",
					                  "yMMM" : "y年M月",
					                  "yMMMd" : "y年M月d日",
					                  "yMMMEd" : "y年M月d日E",
					                  "yMMMM" : "y年M月",
					                  "yQQQ" : "y年第Q季度",
					                  "yQQQQ" : "y年第Q季度",
					                  "yw-count-other" : "Y年第w周"
					                }
					              }
					            }},"isExistPattern":true}}}`},
	} {
		tt := tt
		t.Run(fmt.Sprintf("%v:%v:%v:%v:%v:%v:%v", tt.combine, tt.language, tt.region, tt.scope, tt.name, tt.version, tt.components), func(t *testing.T) {
			if tt.version == Version { // Don't test version fall back
				querys := map[string]interface{}{
					"combine":  tt.combine,
					"language": tt.language, "region": tt.region, "scope": tt.scope,
					"productName": tt.name, "version": tt.version, "components": tt.components}
				resp := e.GET(GetCombinedURL).WithQueryObject(querys).Expect()
				resp.Status(http.StatusOK)
				assert.JSONEq(t, tt.wanted, resp.Body().Raw())
			}
			{
				querys := map[string]interface{}{
					"combine":  tt.combine,
					"language": tt.language, "region": tt.region, "scope": tt.scope,
					"productName": tt.name, "version": tt.version, "components": strings.Split(tt.components, common.ParamSep)}
				bts, _ := json.Marshal(querys)
				resp := e.POST(GetCombinedByPostURL).WithBytes(bts).Expect()
				resp.Status(http.StatusOK)
				assert.JSONEq(t, tt.wanted, resp.Body().Raw())
			}
		})
	}
}

func TestGetSupportedLangsByDispLang(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, tt := range []struct {
		displayLanguage, wanted string
		wantedCode              int
	}{
		{displayLanguage: "en", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"displayLanguage":"en","languages":[{"languageTag":"de","displayName":"German","displayName_sentenceBeginning":"German","displayName_uiListOrMenu":"German","displayName_standalone":"German"},{"languageTag":"en","displayName":"English","displayName_sentenceBeginning":"English","displayName_uiListOrMenu":"English","displayName_standalone":"English"},{"languageTag":"es-MX","displayName":"Mexican Spanish","displayName_sentenceBeginning":"Mexican Spanish","displayName_uiListOrMenu":"Mexican Spanish","displayName_standalone":"Mexican Spanish"},{"languageTag":"es","displayName":"Spanish","displayName_sentenceBeginning":"Spanish","displayName_uiListOrMenu":"Spanish","displayName_standalone":"Spanish"},{"languageTag":"fr-CA","displayName":"Canadian French","displayName_sentenceBeginning":"Canadian French","displayName_uiListOrMenu":"Canadian French","displayName_standalone":"Canadian French"},{"languageTag":"fr","displayName":"French","displayName_sentenceBeginning":"French","displayName_uiListOrMenu":"French","displayName_standalone":"French"},{"languageTag":"ja","displayName":"Japanese","displayName_sentenceBeginning":"Japanese","displayName_uiListOrMenu":"Japanese","displayName_standalone":"Japanese"},{"languageTag":"ko","displayName":"Korean","displayName_sentenceBeginning":"Korean","displayName_uiListOrMenu":"Korean","displayName_standalone":"Korean"},{"languageTag":"pt-PT","displayName":"European Portuguese","displayName_sentenceBeginning":"European Portuguese","displayName_uiListOrMenu":"European Portuguese","displayName_standalone":"European Portuguese"},{"languageTag":"pt","displayName":"Portuguese","displayName_sentenceBeginning":"Portuguese","displayName_uiListOrMenu":"Portuguese","displayName_standalone":"Portuguese"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"Cantonese","displayName_sentenceBeginning":"Cantonese","displayName_uiListOrMenu":"Cantonese","displayName_standalone":"Cantonese"},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"Simplified Chinese","displayName_sentenceBeginning":"Simplified Chinese","displayName_uiListOrMenu":"Simplified Chinese","displayName_standalone":"Simplified Chinese"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"Traditional Chinese","displayName_sentenceBeginning":"Traditional Chinese","displayName_uiListOrMenu":"Traditional Chinese","displayName_standalone":"Traditional Chinese"}],"productName":"VPE","version":"1.0.0"}}`},
		{displayLanguage: "", wantedCode: sgtnerror.StatusPartialSuccess.Code(),
			wanted: `{"response":{"code":207,"message":"1 error occurred. Not Found: Invalid locale 'yue'"},"data":{"displayLanguage":"","languages":[{"languageTag":"de","displayName":"Deutsch","displayName_sentenceBeginning":"Deutsch","displayName_uiListOrMenu":"Deutsch","displayName_standalone":"Deutsch"},{"languageTag":"en","displayName":"English","displayName_sentenceBeginning":"English","displayName_uiListOrMenu":"English","displayName_standalone":"English"},{"languageTag":"es-MX","displayName":"español de México","displayName_sentenceBeginning":"Español de México","displayName_uiListOrMenu":"titlecase-firstword","displayName_standalone":"titlecase-firstword"},{"languageTag":"es","displayName":"español","displayName_sentenceBeginning":"Español","displayName_uiListOrMenu":"titlecase-firstword","displayName_standalone":"titlecase-firstword"},{"languageTag":"fr-CA","displayName":"français canadien","displayName_sentenceBeginning":"Français canadien","displayName_uiListOrMenu":"titlecase-firstword","displayName_standalone":"français canadien"},{"languageTag":"fr","displayName":"français","displayName_sentenceBeginning":"Français","displayName_uiListOrMenu":"titlecase-firstword","displayName_standalone":"français"},{"languageTag":"ja","displayName":"日本語","displayName_sentenceBeginning":"日本語","displayName_uiListOrMenu":"日本語","displayName_standalone":"日本語"},{"languageTag":"ko","displayName":"한국어","displayName_sentenceBeginning":"한국어","displayName_uiListOrMenu":"한국어","displayName_standalone":"한국어"},{"languageTag":"pt-PT","displayName":"português europeu","displayName_sentenceBeginning":"Português europeu","displayName_uiListOrMenu":"no-change","displayName_standalone":"titlecase-firstword"},{"languageTag":"pt","displayName":"português","displayName_sentenceBeginning":"Português","displayName_uiListOrMenu":"titlecase-firstword","displayName_standalone":"titlecase-firstword"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"简体中文","displayName_sentenceBeginning":"简体中文","displayName_uiListOrMenu":"简体中文","displayName_standalone":"简体中文"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"繁體中文","displayName_sentenceBeginning":"繁體中文","displayName_uiListOrMenu":"繁體中文","displayName_standalone":"繁體中文"}],"productName":"VPE","version":"1.0.0"}}`},
		{displayLanguage: "en-US", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"displayLanguage":"en-US","languages":[{"languageTag":"de","displayName":"German","displayName_sentenceBeginning":"German","displayName_uiListOrMenu":"German","displayName_standalone":"German"},{"languageTag":"en","displayName":"English","displayName_sentenceBeginning":"English","displayName_uiListOrMenu":"English","displayName_standalone":"English"},{"languageTag":"es-MX","displayName":"Mexican Spanish","displayName_sentenceBeginning":"Mexican Spanish","displayName_uiListOrMenu":"Mexican Spanish","displayName_standalone":"Mexican Spanish"},{"languageTag":"es","displayName":"Spanish","displayName_sentenceBeginning":"Spanish","displayName_uiListOrMenu":"Spanish","displayName_standalone":"Spanish"},{"languageTag":"fr-CA","displayName":"Canadian French","displayName_sentenceBeginning":"Canadian French","displayName_uiListOrMenu":"Canadian French","displayName_standalone":"Canadian French"},{"languageTag":"fr","displayName":"French","displayName_sentenceBeginning":"French","displayName_uiListOrMenu":"French","displayName_standalone":"French"},{"languageTag":"ja","displayName":"Japanese","displayName_sentenceBeginning":"Japanese","displayName_uiListOrMenu":"Japanese","displayName_standalone":"Japanese"},{"languageTag":"ko","displayName":"Korean","displayName_sentenceBeginning":"Korean","displayName_uiListOrMenu":"Korean","displayName_standalone":"Korean"},{"languageTag":"pt-PT","displayName":"European Portuguese","displayName_sentenceBeginning":"European Portuguese","displayName_uiListOrMenu":"European Portuguese","displayName_standalone":"European Portuguese"},{"languageTag":"pt","displayName":"Portuguese","displayName_sentenceBeginning":"Portuguese","displayName_uiListOrMenu":"Portuguese","displayName_standalone":"Portuguese"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"Cantonese","displayName_sentenceBeginning":"Cantonese","displayName_uiListOrMenu":"Cantonese","displayName_standalone":"Cantonese"},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"Simplified Chinese","displayName_sentenceBeginning":"Simplified Chinese","displayName_uiListOrMenu":"Simplified Chinese","displayName_standalone":"Simplified Chinese"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"Traditional Chinese","displayName_sentenceBeginning":"Traditional Chinese","displayName_uiListOrMenu":"Traditional Chinese","displayName_standalone":"Traditional Chinese"}],"productName":"VPE","version":"1.0.0"}}`},
		{displayLanguage: "zh-Hans-CN", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"displayLanguage":"zh-Hans-CN","languages":[{"languageTag":"de","displayName":"德语","displayName_sentenceBeginning":"德语","displayName_uiListOrMenu":"德语","displayName_standalone":"德语"},{"languageTag":"en","displayName":"英语","displayName_sentenceBeginning":"英语","displayName_uiListOrMenu":"英语","displayName_standalone":"英语"},{"languageTag":"es-MX","displayName":"墨西哥西班牙语","displayName_sentenceBeginning":"墨西哥西班牙语","displayName_uiListOrMenu":"墨西哥西班牙语","displayName_standalone":"墨西哥西班牙语"},{"languageTag":"es","displayName":"西班牙语","displayName_sentenceBeginning":"西班牙语","displayName_uiListOrMenu":"西班牙语","displayName_standalone":"西班牙语"},{"languageTag":"fr-CA","displayName":"加拿大法语","displayName_sentenceBeginning":"加拿大法语","displayName_uiListOrMenu":"加拿大法语","displayName_standalone":"加拿大法语"},{"languageTag":"fr","displayName":"法语","displayName_sentenceBeginning":"法语","displayName_uiListOrMenu":"法语","displayName_standalone":"法语"},{"languageTag":"ja","displayName":"日语","displayName_sentenceBeginning":"日语","displayName_uiListOrMenu":"日语","displayName_standalone":"日语"},{"languageTag":"ko","displayName":"韩语","displayName_sentenceBeginning":"韩语","displayName_uiListOrMenu":"韩语","displayName_standalone":"韩语"},{"languageTag":"pt-PT","displayName":"欧洲葡萄牙语","displayName_sentenceBeginning":"欧洲葡萄牙语","displayName_uiListOrMenu":"欧洲葡萄牙语","displayName_standalone":"欧洲葡萄牙语"},{"languageTag":"pt","displayName":"葡萄牙语","displayName_sentenceBeginning":"葡萄牙语","displayName_uiListOrMenu":"葡萄牙语","displayName_standalone":"葡萄牙语"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"粤语","displayName_sentenceBeginning":"粤语","displayName_uiListOrMenu":"粤语","displayName_standalone":"粤语"},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"简体中文","displayName_sentenceBeginning":"简体中文","displayName_uiListOrMenu":"简体中文","displayName_standalone":"简体中文"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"繁体中文","displayName_sentenceBeginning":"繁体中文","displayName_uiListOrMenu":"繁体中文","displayName_standalone":"繁体中文"}],"productName":"VPE","version":"1.0.0"}}`},
		{displayLanguage: "zh-CN", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"displayLanguage":"zh-CN","languages":[{"languageTag":"de","displayName":"德语","displayName_sentenceBeginning":"德语","displayName_uiListOrMenu":"德语","displayName_standalone":"德语"},{"languageTag":"en","displayName":"英语","displayName_sentenceBeginning":"英语","displayName_uiListOrMenu":"英语","displayName_standalone":"英语"},{"languageTag":"es-MX","displayName":"墨西哥西班牙语","displayName_sentenceBeginning":"墨西哥西班牙语","displayName_uiListOrMenu":"墨西哥西班牙语","displayName_standalone":"墨西哥西班牙语"},{"languageTag":"es","displayName":"西班牙语","displayName_sentenceBeginning":"西班牙语","displayName_uiListOrMenu":"西班牙语","displayName_standalone":"西班牙语"},{"languageTag":"fr-CA","displayName":"加拿大法语","displayName_sentenceBeginning":"加拿大法语","displayName_uiListOrMenu":"加拿大法语","displayName_standalone":"加拿大法语"},{"languageTag":"fr","displayName":"法语","displayName_sentenceBeginning":"法语","displayName_uiListOrMenu":"法语","displayName_standalone":"法语"},{"languageTag":"ja","displayName":"日语","displayName_sentenceBeginning":"日语","displayName_uiListOrMenu":"日语","displayName_standalone":"日语"},{"languageTag":"ko","displayName":"韩语","displayName_sentenceBeginning":"韩语","displayName_uiListOrMenu":"韩语","displayName_standalone":"韩语"},{"languageTag":"pt-PT","displayName":"欧洲葡萄牙语","displayName_sentenceBeginning":"欧洲葡萄牙语","displayName_uiListOrMenu":"欧洲葡萄牙语","displayName_standalone":"欧洲葡萄牙语"},{"languageTag":"pt","displayName":"葡萄牙语","displayName_sentenceBeginning":"葡萄牙语","displayName_uiListOrMenu":"葡萄牙语","displayName_standalone":"葡萄牙语"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"粤语","displayName_sentenceBeginning":"粤语","displayName_uiListOrMenu":"粤语","displayName_standalone":"粤语"},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"简体中文","displayName_sentenceBeginning":"简体中文","displayName_uiListOrMenu":"简体中文","displayName_standalone":"简体中文"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"繁体中文","displayName_sentenceBeginning":"繁体中文","displayName_uiListOrMenu":"繁体中文","displayName_standalone":"繁体中文"}],"productName":"VPE","version":"1.0.0"}}`},
		{displayLanguage: "yue-Hant", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"displayLanguage":"yue-Hant","languages":[{"languageTag":"de","displayName":"德文","displayName_sentenceBeginning":"德文","displayName_uiListOrMenu":"德文","displayName_standalone":"德文"},{"languageTag":"en","displayName":"英文","displayName_sentenceBeginning":"英文","displayName_uiListOrMenu":"英文","displayName_standalone":"英文"},{"languageTag":"es-MX","displayName":"西班牙文 (墨西哥)","displayName_sentenceBeginning":"西班牙文 (墨西哥)","displayName_uiListOrMenu":"西班牙文 (墨西哥)","displayName_standalone":"西班牙文 (墨西哥)"},{"languageTag":"es","displayName":"西班牙文","displayName_sentenceBeginning":"西班牙文","displayName_uiListOrMenu":"西班牙文","displayName_standalone":"西班牙文"},{"languageTag":"fr-CA","displayName":"法文 (加拿大)","displayName_sentenceBeginning":"法文 (加拿大)","displayName_uiListOrMenu":"法文 (加拿大)","displayName_standalone":"法文 (加拿大)"},{"languageTag":"fr","displayName":"法文","displayName_sentenceBeginning":"法文","displayName_uiListOrMenu":"法文","displayName_standalone":"法文"},{"languageTag":"ja","displayName":"日文","displayName_sentenceBeginning":"日文","displayName_uiListOrMenu":"日文","displayName_standalone":"日文"},{"languageTag":"ko","displayName":"韓文","displayName_sentenceBeginning":"韓文","displayName_uiListOrMenu":"韓文","displayName_standalone":"韓文"},{"languageTag":"pt-PT","displayName":"葡萄牙文 (葡萄牙)","displayName_sentenceBeginning":"葡萄牙文 (葡萄牙)","displayName_uiListOrMenu":"葡萄牙文 (葡萄牙)","displayName_standalone":"葡萄牙文 (葡萄牙)"},{"languageTag":"pt","displayName":"葡萄牙文","displayName_sentenceBeginning":"葡萄牙文","displayName_uiListOrMenu":"葡萄牙文","displayName_standalone":"葡萄牙文"},{"languageTag":"yue-Hant","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"yue","displayName":"粵語","displayName_sentenceBeginning":"粵語","displayName_uiListOrMenu":"粵語","displayName_standalone":"粵語"},{"languageTag":"zh-Hans-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hans","displayName":"簡體中文","displayName_sentenceBeginning":"簡體中文","displayName_uiListOrMenu":"簡體中文","displayName_standalone":"簡體中文"},{"languageTag":"zh-Hant-HK","displayName":"","displayName_sentenceBeginning":"","displayName_uiListOrMenu":"","displayName_standalone":""},{"languageTag":"zh-Hant","displayName":"繁體中文","displayName_sentenceBeginning":"繁體中文","displayName_uiListOrMenu":"繁體中文","displayName_standalone":"繁體中文"}],"productName":"VPE","version":"1.0.0"}}`},
	} {
		tt := tt
		t.Run(fmt.Sprintf(tt.displayLanguage+":%d", tt.wantedCode), func(t *testing.T) {
			resp := e.GET(GetSupportedLanguageListURL).WithQuery("displayLanguage", tt.displayLanguage).
				WithQuery("productName", Name).WithQuery("version", Version).Expect()
			resp.Status(tt.wantedCode).Body().Equal(tt.wanted)
		})
	}
}

func TestGetCombinedExcep(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, tt := range []struct {
		testName                  string
		combine                   int
		language, region, scope   string
		name, version, components string
		wantedCode                int
	}{
		{testName: "invalideType", combine: 3, language: Language, region: Region, scope: "currencies", name: Name, version: Version, components: Component, wantedCode: http.StatusBadRequest},
		{testName: "noProductName", combine: 1, language: Language, region: Region, scope: "currencies", name: "", version: Version, components: Component, wantedCode: http.StatusBadRequest},
		{testName: "invalideLanguage", combine: 1, language: "€", region: Region, scope: "currencies", name: Name, version: Version, components: Component, wantedCode: http.StatusBadRequest},
		{testName: "invalideRegion", combine: 1, language: Language, region: "€", scope: "currencies", name: Name, version: Version, components: Component, wantedCode: http.StatusBadRequest},
		{testName: "invalideScope", combine: 1, language: Language, region: Region, scope: "€", name: Name, version: Version, components: Component, wantedCode: http.StatusBadRequest},
		{testName: "invalideComponents", combine: 1, language: Language, region: Region, scope: "€", name: Name, version: Version, components: "€", wantedCode: http.StatusBadRequest},
		{testName: "nullRegionType1", combine: 1, language: Language, region: "", scope: "currencies", name: Name, version: Version, components: Component, wantedCode: http.StatusBadRequest},
	} {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()
			{
				querys := map[string]interface{}{
					"combine":    tt.combine,
					"language":   tt.language,
					"region":     tt.region,
					"scope":      tt.scope,
					"components": tt.components,
					"version":    tt.version,
				}
				if len(tt.name) > 0 {
					querys["productName"] = tt.name
				}

				resp := e.GET(GetCombinedURL).WithQueryObject(querys).Expect()

				resp.Status(tt.wantedCode)
			}
			{
				querys := map[string]interface{}{
					"combine":  tt.combine,
					"language": tt.language, "region": tt.region, "scope": tt.scope,
					"version": tt.version, "components": strings.Split(tt.components, common.ParamSep)}
				if len(tt.name) > 0 {
					querys["productName"] = tt.name
				}
				bts, _ := json.Marshal(querys)
				resp := e.POST(GetCombinedByPostURL).WithBytes(bts).Expect()
				resp.Status(tt.wantedCode)
			}
		})
	}
}

func TestGetSupportedLangsByDispLangExcep(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, tt := range []struct {
		testName        string
		name, version   string
		displayLanguage string
		wantedCode      int
	}{
		{testName: "invalidName", name: "€", version: Version, displayLanguage: Language, wantedCode: http.StatusBadRequest},
		{testName: "invalidVersion", name: Name, version: "€", displayLanguage: Language, wantedCode: http.StatusBadRequest},
		{testName: "notFoundVersion", name: Name, version: "0.0.1", displayLanguage: Language, wantedCode: http.StatusNotFound},
		{testName: "invalidLanguage-€", name: Name, version: Version, displayLanguage: "€", wantedCode: http.StatusBadRequest},
		{testName: "notFoundLanguage-en-AB", name: Name, version: Version, displayLanguage: "en-AB", wantedCode: http.StatusNotFound},
	} {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			resp := e.GET(GetSupportedLanguageListURL).WithQuery("displayLanguage", tt.displayLanguage).
				WithQuery("productName", tt.name).WithQuery("version", tt.version).Expect()
			resp.Status(tt.wantedCode)
		})
	}
}
