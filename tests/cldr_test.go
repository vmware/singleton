/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"fmt"
	"reflect"
	"strings"
	"sync"
	"testing"
	"time"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr/cldrservice"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/localeutil"

	"github.com/go-test/deep"
	"github.com/stretchr/objx"
	"github.com/stretchr/testify/assert"
	"go.uber.org/zap"
)

func TestGetCLDRLocale(t *testing.T) {
	for _, d := range []struct{ input, wanted string }{
		{"en", "en"},
		{"en-US", "en"},
		{"zh-Hans-CN", "zh-Hans"},
		{"zh-CN", "zh-Hans"},
		{"de", "de"},
		{"es-MX", "es-MX"},
		{"es", "es"},
		{"fr-CA", "fr-CA"},
		{"fr", "fr"},
		{"ja", "ja"},
		{"ko", "ko"},
		{"pt-PT", "pt-PT"},
		{"pt", "pt"},
		{"yue-Hant", "yue-Hant"},
		{"yue", ""},
		{"zh-Hans-HK", "zh-Hans-HK"},
		{"zh-Hans", "zh-Hans"},
		{"zh-Hant-HK", "zh-Hant-HK"},
		{"zh-Hant", "zh-Hant"},
	} {
		d := d
		t.Run(d.input, func(t *testing.T) {
			newLocale := coreutil.GetCLDRLocale(d.input)
			assert.Equal(t, d.wanted, newLocale)
		})
	}
}

func TestGetLanguageFunc(t *testing.T) {
	for _, d := range []struct{ input, wanted string }{
		{"en", `{"lo":"Lao","lu":"Luba-Katanga","mga":"Middle Irish","ps-alt-variant":"Pushto","sms":"Skolt Sami","af":"Afrikaans","cy":"Welsh","ha":"Hausa","tk":"Turkmen","yue":"Cantonese","aeb":"Tunisian Arabic","mgo":"Metaʼ","or":"Odia","pdc":"Pennsylvania German","ss":"Swati","wbp":"Warlpiri","es-MX":"Mexican Spanish","pap":"Papiamento","rtm":"Rotuman","fy":"Western Frisian","avk":"Kotava","gon":"Gondi","hif":"Fiji Hindi","kv":"Komi","nia":"Nias","njo":"Ao Naga","suk":"Sukuma","ae":"Avestan","ary":"Moroccan Arabic","ee":"Ewe","grc":"Ancient Greek","rif":"Riffian","sus":"Susu","an":"Aragonese","ny":"Nyanja","cgg":"Chiga","loz":"Lozi","pro":"Old Provençal","pt-PT":"European Portuguese","ses":"Koyraboro Senni","zen":"Zenaga","bua":"Buriat","sog":"Sogdien","hup":"Hupa","krc":"Karachay-Balkar","no":"Norwegian","root":"Root","tem":"Timne","tkl":"Tokelau","en-US":"American English","hz":"Herero","myv":"Erzya","en-GB":"British English","co":"Corsican","ks":"Kashmiri","nv":"Navajo","ttt":"Muslim Tat","bho":"Bhojpuri","fan":"Fang","nds":"Low German","sbp":"Sangu","smn":"Inari Sami","vai":"Vai","dje":"Zarma","hi":"Hindi","kab":"Kabyle","kpe":"Kpelle","oj":"Ojibwa","ur":"Urdu","ewo":"Ewondo","goh":"Old High German","lez":"Lezghian","mad":"Madurese","nan":"Min Nan Chinese","wa":"Walloon","zun":"Zuni","cch":"Atsam","chk":"Chuukese","gez":"Geez","mul":"Multiple languages","vo":"Volapük","ca":"Catalan","lou":"Louisiana Creole","se":"Northern Sami","en-CA":"Canadian English","cad":"Caddo","crh":"Crimean Turkish","gl":"Galician","kum":"Kumyk","arn":"Mapuche","bgn":"Western Balochi","bik":"Bikol","krl":"Karelian","swb":"Comorian","zap":"Zapotec","ba":"Bashkir","bas":"Basaa","kbl":"Kanembu","kmb":"Kimbundu","kos":"Kosraean","kw":"Cornish","sl":"Slovenian","te":"Telugu","aa":"Afar","zu":"Zulu","bm":"Bambara","jpr":"Judeo-Persian","nzi":"Nzima","ars":"Najdi Arabic","nd":"North Ndebele","sw-CD":"Congo Swahili","bar":"Bavarian","hai":"Haida","ht":"Haitian Creole","ko":"Korean","nb":"Norwegian Bokmål","nnh":"Ngiemboon","nyn":"Nyankole","ru":"Russian","bn":"Bangla","chp":"Chipewyan","efi":"Efik","pau":"Palauan","tg":"Tajik","anp":"Angika","wo":"Wolof","za":"Zhuang","zea":"Zeelandic","ace":"Achinese","alt":"Southern Altai","koi":"Komi-Permyak","teo":"Teso","vls":"West Flemish","ach":"Acoli","nr":"South Ndebele","quc":"Kʼicheʼ","mwr":"Marwari","dyo":"Jola-Fonyi","gbz":"Zoroastrian Dari","ig":"Igbo","th":"Thai","tsi":"Tsimshian","del":"Delaware","bqi":"Bakhtiari","de-AT":"Austrian German","en-GB-alt-short":"UK English","lkt":"Lakota","arq":"Algerian Arabic","dsb":"Lower Sorbian","pon":"Pohnpeian","ter":"Tereno","tmh":"Tamashek","uk":"Ukrainian","ada":"Adangme","chr":"Cherokee","frp":"Arpitan","ga":"Irish","hsn":"Xiang Chinese","ii":"Sichuan Yi","kac":"Kachin","mfe":"Morisyen","aro":"Araona","nyo":"Nyoro","pfl":"Palatine German","rgn":"Romagnol","sq":"Albanian","tru":"Turoyo","nov":"Novial","hmn":"Hmong","mak":"Makasar","rwk":"Rwa","sel":"Selkup","zh":"Chinese","et":"Estonian","fur":"Friulian","iu":"Inuktitut","krj":"Kinaray-a","rm":"Romansh","sdc":"Sassarese Sardinian","ay":"Aymara","fat":"Fanti","sux":"Sumerian","bbc":"Batak Toba","dum":"Middle Dutch","he":"Hebrew","pam":"Pampanga","cv":"Chuvash","din":"Dinka","ho":"Hiri Motu","kaj":"Jju","kha":"Khasi","kl":"Kalaallisut","mh":"Marshallese","nn":"Norwegian Nynorsk","non":"Old Norse","da":"Danish","shi":"Tachelhit","eu":"Basque","ff":"Fulah","sm":"Samoan","tpi":"Tok Pisin","vot":"Votic","csb":"Kashubian","cps":"Capiznon","umb":"Umbundu","ceb":"Cebuano","fr-CH":"Swiss French","pnt":"Pontic","ccp":"Chakma","luo":"Luo","seh":"Sena","arz":"Egyptian Arabic","dzg":"Dazaga","mr":"Marathi","ms":"Malay","osa":"Osage","sam":"Samaritan Aramaic","tly":"Talysh","ts":"Tsonga","agq":"Aghem","hak":"Hakka Chinese","kho":"Khotanese","bin":"Bini","cr":"Cree","dtp":"Central Dusun","gv":"Manx","kri":"Krio","nl-BE":"Flemish","brh":"Brahui","mnc":"Manchu","rap":"Rapanui","xh":"Xhosa","de-CH":"Swiss High German","av":"Avaric","pcm":"Nigerian Pidgin","sc":"Sardinian","so":"Somali","ang":"Old English","doi":"Dogri","syr":"Syriac","vun":"Vunjo","ak":"Akan","id":"Indonesian","sa":"Sanskrit","smj":"Lule Sami","snk":"Soninke","eo":"Esperanto","guc":"Wayuu","rup":"Aromanian","bg":"Bulgarian","prg":"Prussian","sw":"Swahili","pi":"Pali","gay":"Gayo","mde":"Maba","tt":"Tatar","chn":"Chinook Jargon","peo":"Old Persian","syc":"Classical Syriac","jgo":"Ngomba","fr":"French","izh":"Ingrian","km":"Khmer","my-alt-variant":"Myanmar Language","pcd":"Picard","xal":"Kalmyk","fj":"Fijian","mk":"Macedonian","bax":"Bamun","ne":"Nepali","brx":"Bodo","it":"Italian","fi":"Finnish","la":"Latin","lrc":"Northern Luri","sgs":"Samogitian","shu":"Chadian Arabic","tzm":"Central Atlas Tamazight","bal":"Baluchi","ik":"Inupiaq","mye":"Myene","nwc":"Classical Newari","sn":"Shona","ug-alt-variant":"Uighur","gu":"Gujarati","bjn":"Banjar","ie":"Interlingue","zh-Hant":"Traditional Chinese","awa":"Awadhi","fa":"Persian","mg":"Malagasy","mgh":"Makhuwa-Meetto","phn":"Phoenician","tsd":"Tsakonian","aln":"Gheg Albanian","kam":"Kamba","fr-CA":"Canadian French","grb":"Grebo","ibb":"Ibibio","kbd":"Kabardian","lzh":"Literary Chinese","nso":"Northern Sotho","scn":"Sicilian","tli":"Tlingit","en-AU":"Australian English","vec":"Venetian","tiv":"Tiv","lah":"Lahnda","si":"Sinhala","st":"Southern Sotho","tet":"Tetum","bew":"Betawi","de":"German","kr":"Kanuri","mt":"Maltese","twq":"Tasawaq","ain":"Ainu","crs":"Seselwa Creole French","dar":"Dargwa","oc":"Occitan","xmf":"Mingrelian","yao":"Yao","bi":"Bislama","saz":"Saurashtra","pdt":"Plautdietsch","gd":"Scottish Gaelic","nl":"Dutch","cu":"Church Slavic","kut":"Kutenai","lt":"Lithuanian","mdf":"Moksha","ssy":"Saho","yav":"Yangben","khw":"Khowar","ota":"Ottoman Turkish","pa":"Punjabi","shn":"Shan","wuu":"Wu Chinese","kde":"Makonde","fo":"Faroese","hit":"Hittite","ja":"Japanese","rar":"Rarotongan","akz":"Alabama","byn":"Blin","el":"Greek","gaa":"Ga","hu":"Hungarian","iba":"Iban","ky-alt-variant":"Kirghiz","mag":"Magahi","bpy":"Bishnupriya","sr":"Serbian","tog":"Nyasa Tonga","mai":"Maithili","bkm":"Kom","ebu":"Embu","na":"Nauru","sk":"Slovak","sly":"Selayar","tr":"Turkish","ar-001":"Modern Standard Arabic","pt-BR":"Brazilian Portuguese","gom":"Goan Konkani","bss":"Akoose","khq":"Koyra Chiini","ro-MD":"Moldavian","sat":"Santali","afh":"Afrihili","egl":"Emilian","es-ES":"European Spanish","ml":"Malayalam","mos":"Mossi","tcy":"Tulu","ti":"Tigrinya","az-alt-short":"Azeri","cay":"Cayuga","en-US-alt-short":"US English","gn":"Guarani","rom":"Romany","sg":"Sango","sma":"Southern Sami","ab":"Abkhazian","bez":"Bena","bum":"Bulu","ast":"Asturian","lfn":"Lingua Franca Nova","lmo":"Lombard","nmg":"Kwasio","rug":"Roviana","wae":"Walser","bej":"Beja","frm":"Middle French","inh":"Ingush","ki":"Kikuyu","bfq":"Badaga","lad":"Ladino","vep":"Veps","yrl":"Nheengatu","io":"Ido","lg":"Ganda","li":"Limburgish","gba":"Gbaya","es":"Spanish","ken":"Kenyang","os":"Ossetic","zxx":"No linguistic content","bs":"Bosnian","new":"Newari","nog":"Nogai","ro":"Romanian","kru":"Kurukh","gag":"Gagauz","hr":"Croatian","fro":"Old French","ckb":"Central Kurdish","kiu":"Kirmanjki","ksf":"Bafia","lun":"Lunda","mwv":"Mentawai","und":"Unknown language","chy":"Cheyenne","zh-alt-long":"Mandarin Chinese","sid":"Sidamo","sba":"Ngambay","to":"Tongan","kn":"Kannada","dyu":"Dyula","enm":"Middle English","kaa":"Kara-Kalpak","tig":"Tigre","vmf":"Main-Franconian","as":"Assamese","esu":"Central Yupik","ka":"Georgian","mua":"Mundang","mus":"Creek","ng":"Ndonga","pag":"Pangasinan","vi":"Vietnamese","am":"Amharic","chb":"Chibcha","lb":"Luxembourgish","lzz":"Laz","nap":"Neapolitan","nus":"Nuer","raj":"Rajasthani","arc":"Aramaic","pl":"Polish","szl":"Silesian","zgh":"Standard Moroccan Tamazight","gwi":"Gwichʼin","en":"English","frr":"Northern Frisian","kj":"Kuanyama","lus":"Mizo","egy":"Ancient Egyptian","my":"Burmese","niu":"Niuean","kkj":"Kako","ilo":"Iloko","kok":"Konkani","fit":"Tornedalen Finnish","ia":"Interlingua","sco":"Scots","glk":"Gilaki","gil":"Gilbertese","kgp":"Kaingang","ltg":"Latgalian","mwl":"Mirandese","bug":"Buginese","bfd":"Bafut","car":"Carib","gor":"Gorontalo","lag":"Langi","pms":"Piedmontese","yi":"Yiddish","bem":"Bemba","xog":"Soga","tn":"Tswana","tum":"Tumbuka","yo":"Yoruba","maf":"Mafa","mic":"Mi'kmaq","vro":"Võro","lv":"Latvian","jbo":"Lojban","tw":"Twi","uz":"Uzbek","zh-Hans":"Simplified Chinese","arw":"Arawak","naq":"Nama","wal":"Wolaytta","jut":"Jutish","frs":"Eastern Frisian","lam":"Lamba","qu":"Quechua","ty":"Tahitian","dz":"Dzongkha","fon":"Fon","gsw":"Swiss German","haw":"Hawaiian","moh":"Mohawk","nqo":"N’Ko","udm":"Udmurt","zbl":"Blissymbols","ce":"Chechen","jrb":"Judeo-Arabic","rof":"Rombo","hy":"Armenian","tyv":"Tuvinian","ch":"Chamorro","arp":"Arapaho","asa":"Asu","gur":"Frafra","lui":"Luiseno","su":"Sundanese","tl":"Tagalog","akk":"Akkadian","bla":"Siksika","bra":"Braj","fil":"Filipino","kg":"Kongo","ku":"Kurdish","mn":"Mongolian","rue":"Rusyn","ale":"Aleut","yap":"Yapese","is":"Icelandic","rw":"Kinyarwanda","dak":"Dakota","luy":"Luyia","saq":"Samburu","sli":"Lower Silesian","kfo":"Koro","elx":"Elamite","gan":"Gan Chinese","sd":"Sindhi","stq":"Saterland Frisian","was":"Washo","chg":"Chagatai","liv":"Livonian","eka":"Ekajuk","ky":"Kyrgyz","cs":"Czech","got":"Gothic","hsb":"Upper Sorbian","jmc":"Machame","lij":"Ligurian","mi":"Maori","mni":"Manipuri","tkr":"Tsakhur","cho":"Choctaw","zza":"Zaza","den":"Slave","ext":"Extremaduran","kcg":"Tyap","lol":"Mongo","mrj":"Western Mari","see":"Seneca","ar":"Arabic","az":"Azerbaijani","bo":"Tibetan","ln":"Lingala","tvl":"Tuvalu","ase":"American Sign Language","dav":"Taita","hil":"Hiligaynon","om":"Oromo","pal":"Pahlavi","ps":"Pashto","sga":"Old Irish","srn":"Sranan Tongo","ady":"Adyghe","kea":"Kabuverdianu","kln":"Kalenjin","lua":"Luba-Lulua","mdr":"Mandar","jam":"Jamaican Creole English","frc":"Cajun French","gmh":"Middle High German","mas":"Masai","mer":"Meru","mzn":"Mazanderani","sad":"Sandawe","sdh":"Southern Kurdish","fa-AF":"Dari","uga":"Ugaritic","sh":"Serbo-Croatian","br":"Breton","byv":"Medumba","dgr":"Dogrib","guz":"Gusii","ksh":"Colognian","sei":"Seri","sv":"Swedish","be":"Belarusian","dv":"Divehi","kaw":"Kawi","men":"Mende","chm":"Mari","bbj":"Ghomala","kk":"Kazakh","ban":"Balinese","ksb":"Shambala","nym":"Nyamwezi","qug":"Chimborazo Highland Quichua","tlh":"Klingon","war":"Waray","dua":"Duala","rn":"Rundi","nds-NL":"Low Saxon","srr":"Serer","ta":"Tamil","ve":"Venda","jv":"Javanese","es-419":"Latin American Spanish","man":"Mandingo","min":"Minangkabau","pt":"Portuguese","sah":"Sakha","sas":"Sasak","trv":"Taroko","cop":"Coptic","ybb":"Yemba","ug":"Uyghur"}`},
		{"en-US", `{"sga":"Old Irish","ter":"Tereno","byn":"Blin","cgg":"Chiga","kkj":"Kako","mic":"Mi'kmaq","yo":"Yoruba","agq":"Aghem","dar":"Dargwa","dyo":"Jola-Fonyi","nov":"Novial","tsi":"Tsimshian","zgh":"Standard Moroccan Tamazight","cch":"Atsam","dv":"Divehi","mi":"Maori","pt":"Portuguese","ak":"Akan","ext":"Extremaduran","ilo":"Iloko","saq":"Samburu","khq":"Koyra Chiini","ku":"Kurdish","rug":"Roviana","tpi":"Tok Pisin","ace":"Achinese","bkm":"Kom","ff":"Fulah","gbz":"Zoroastrian Dari","sas":"Sasak","frr":"Northern Frisian","guz":"Gusii","my-alt-variant":"Myanmar Language","pfl":"Palatine German","ebu":"Embu","ksf":"Bafia","wbp":"Warlpiri","nn":"Norwegian Nynorsk","zh":"Chinese","ady":"Adyghe","kcg":"Tyap","kos":"Kosraean","lah":"Lahnda","chg":"Chagatai","ka":"Georgian","rgn":"Romagnol","sg":"Sango","ain":"Ainu","ase":"American Sign Language","bqi":"Bakhtiari","chb":"Chibcha","shi":"Tachelhit","tlh":"Klingon","tr":"Turkish","vmf":"Main-Franconian","mwl":"Mirandese","om":"Oromo","pms":"Piedmontese","ru":"Russian","dgr":"Dogrib","fj":"Fijian","hil":"Hiligaynon","ik":"Inupiaq","yue":"Cantonese","aeb":"Tunisian Arabic","hy":"Armenian","srn":"Sranan Tongo","vro":"Võro","xal":"Kalmyk","bfd":"Bafut","chn":"Chinook Jargon","gur":"Frafra","ks":"Kashmiri","pdc":"Pennsylvania German","tzm":"Central Atlas Tamazight","inh":"Ingush","lfn":"Lingua Franca Nova","mul":"Multiple languages","niu":"Niuean","see":"Seneca","ay":"Aymara","de-AT":"Austrian German","hit":"Hittite","mas":"Masai","lam":"Lamba","sgs":"Samogitian","ada":"Adangme","as":"Assamese","ho":"Hiri Motu","hu":"Hungarian","uga":"Ugaritic","bpy":"Bishnupriya","fa":"Persian","haw":"Hawaiian","mak":"Makasar","quc":"Kʼicheʼ","sc":"Sardinian","gmh":"Middle High German","ko":"Korean","loz":"Lozi","nds":"Low German","asa":"Asu","prg":"Prussian","qug":"Chimborazo Highland Quichua","jgo":"Ngomba","kab":"Kabyle","kea":"Kabuverdianu","lus":"Mizo","be":"Belarusian","brx":"Bodo","es":"Spanish","he":"Hebrew","ny":"Nyanja","zh-alt-long":"Mandarin Chinese","gsw":"Swiss German","jv":"Javanese","kde":"Makonde","nia":"Nias","af":"Afrikaans","chp":"Chipewyan","cs":"Czech","csb":"Kashubian","ybb":"Yemba","kmb":"Kimbundu","sly":"Selayar","ab":"Abkhazian","peo":"Old Persian","pnt":"Pontic","vec":"Venetian","bla":"Siksika","jrb":"Judeo-Arabic","nwc":"Classical Newari","nym":"Nyamwezi","rm":"Romansh","sh":"Serbo-Croatian","st":"Southern Sotho","tli":"Tlingit","ale":"Aleut","bg":"Bulgarian","mai":"Maithili","man":"Mandingo","ta":"Tamil","fil":"Filipino","luy":"Luyia","mag":"Magahi","sd":"Sindhi","tly":"Talysh","esu":"Central Yupik","lzz":"Laz","sms":"Skolt Sami","sw":"Swahili","zea":"Zeelandic","zh-Hans":"Simplified Chinese","arp":"Arapaho","bo":"Tibetan","cad":"Caddo","kok":"Konkani","ota":"Ottoman Turkish","rup":"Aromanian","vot":"Votic","yao":"Yao","byv":"Medumba","ceb":"Cebuano","nd":"North Ndebele","zu":"Zulu","en-CA":"Canadian English","lag":"Langi","nds-NL":"Low Saxon","suk":"Sukuma","aln":"Gheg Albanian","bn":"Bangla","brh":"Brahui","da":"Danish","ug-alt-variant":"Uighur","wal":"Wolaytta","dyu":"Dyula","grb":"Grebo","kpe":"Kpelle","ttt":"Muslim Tat","tw":"Twi","yi":"Yiddish","fa-AF":"Dari","ii":"Sichuan Yi","lt":"Lithuanian","sdh":"Southern Kurdish","cu":"Church Slavic","phn":"Phoenician","cop":"Coptic","koi":"Komi-Permyak","mr":"Marathi","nso":"Northern Sotho","sq":"Albanian","vo":"Volapük","zun":"Zuni","bfq":"Badaga","gd":"Scottish Gaelic","ht":"Haitian Creole","nog":"Nogai","ti":"Tigrinya","zxx":"No linguistic content","arz":"Egyptian Arabic","en":"English","pag":"Pangasinan","pl":"Polish","si":"Sinhala","tem":"Timne","zap":"Zapotec","akk":"Akkadian","glk":"Gilaki","gn":"Guarani","new":"Newari","sv":"Swedish","wuu":"Wu Chinese","yap":"Yapese","yav":"Yangben","fr-CA":"Canadian French","kaj":"Jju","mgo":"Metaʼ","sel":"Selkup","aro":"Araona","gba":"Gbaya","lkt":"Lakota","ne":"Nepali","tkl":"Tokelau","trv":"Taroko","fan":"Fang","guc":"Wayuu","hsb":"Upper Sorbian","kum":"Kumyk","sa":"Sanskrit","ba":"Bashkir","bin":"Bini","et":"Estonian","mgh":"Makhuwa-Meetto","aa":"Afar","ars":"Najdi Arabic","pcd":"Picard","sma":"Southern Sami","tog":"Nyasa Tonga","ce":"Chechen","naq":"Nama","bew":"Betawi","ee":"Ewe","jbo":"Lojban","kln":"Kalenjin","crs":"Seselwa Creole French","kbd":"Kabardian","ro-MD":"Moldavian","din":"Dinka","mga":"Middle Irish","ur":"Urdu","vun":"Vunjo","cv":"Chuvash","tet":"Tetum","und":"Unknown language","rar":"Rarotongan","jmc":"Machame","kk":"Kazakh","lzh":"Literary Chinese","mua":"Mundang","ae":"Avestan","gil":"Gilbertese","pcm":"Nigerian Pidgin","tg":"Tajik","udm":"Udmurt","war":"Waray","bua":"Buriat","efi":"Efik","fy":"Western Frisian","got":"Gothic","nyn":"Nyankole","arn":"Mapuche","egl":"Emilian","grc":"Ancient Greek","hai":"Haida","ibb":"Ibibio","mad":"Madurese","my":"Burmese","sdc":"Sassarese Sardinian","rap":"Rapanui","so":"Somali","tig":"Tigre","zbl":"Blissymbols","dua":"Duala","hak":"Hakka Chinese","lui":"Luiseno","mde":"Maba","se":"Northern Sami","tn":"Tswana","uz":"Uzbek","khw":"Khowar","mk":"Macedonian","ml":"Malayalam","rom":"Romany","scn":"Sicilian","umb":"Umbundu","ast":"Asturian","moh":"Mohawk","qu":"Quechua","sba":"Ngambay","tyv":"Tuvinian","xog":"Soga","ki":"Kikuyu","lmo":"Lombard","mni":"Manipuri","ug":"Uyghur","id":"Indonesian","lun":"Lunda","sk":"Slovak","ts":"Tsonga","gag":"Gagauz","sad":"Sandawe","tl":"Tagalog","ach":"Acoli","hi":"Hindi","pam":"Pampanga","smj":"Lule Sami","bm":"Bambara","bs":"Bosnian","krl":"Karelian","root":"Root","srr":"Serer","en-US":"American English","fat":"Fanti","gu":"Gujarati","ig":"Igbo","ken":"Kenyang","lua":"Luba-Lulua","vi":"Vietnamese","mnc":"Manchu","ms":"Malay","rtm":"Rotuman","su":"Sundanese","br":"Breton","is":"Icelandic","lij":"Ligurian","mdr":"Mandar","nyo":"Nyoro","or":"Odia","anp":"Angika","gl":"Galician","lo":"Lao","nl":"Dutch","rif":"Riffian","sn":"Shona","arw":"Arawak","bas":"Basaa","lad":"Ladino","lez":"Lezghian","gom":"Goan Konkani","tk":"Turkmen","tum":"Tumbuka","bal":"Baluchi","ie":"Interlingue","io":"Ido","sco":"Scots","dje":"Zarma","pt-PT":"European Portuguese","ty":"Tahitian","lb":"Luxembourgish","te":"Telugu","ve":"Venda","akz":"Alabama","bra":"Braj","eka":"Ekajuk","mwr":"Marwari","lu":"Luba-Katanga","njo":"Ao Naga","co":"Corsican","frc":"Cajun French","ky-alt-variant":"Kirghiz","lrc":"Northern Luri","en-GB-alt-short":"UK English","ja":"Japanese","nus":"Nuer","ar":"Arabic","bum":"Bulu","dak":"Dakota","nr":"South Ndebele","ltg":"Latgalian","sei":"Seri","shu":"Chadian Arabic","chk":"Chuukese","kut":"Kutenai","kw":"Cornish","li":"Limburgish","gay":"Gayo","gez":"Geez","rof":"Rombo","bbc":"Batak Toba","el":"Greek","fit":"Tornedalen Finnish","fro":"Old French","ky":"Kyrgyz","to":"Tongan","vls":"West Flemish","dz":"Dzongkha","fi":"Finnish","frm":"Middle French","fur":"Friulian","bho":"Bhojpuri","ewo":"Ewondo","ia":"Interlingua","na":"Nauru","dum":"Middle Dutch","hup":"Hupa","kho":"Khotanese","ro":"Romanian","hz":"Herero","mrj":"Western Mari","snk":"Soninke","zen":"Zenaga","bgn":"Western Balochi","dzg":"Dazaga","eu":"Basque","gv":"Manx","awa":"Awadhi","kaw":"Kawi","lg":"Ganda","ps":"Pashto","fo":"Faroese","krj":"Kinaray-a","sam":"Samaritan Aramaic","ses":"Koyraboro Senni","egy":"Ancient Egyptian","it":"Italian","pi":"Pali","uk":"Ukrainian","bi":"Bislama","es-ES":"European Spanish","nan":"Min Nan Chinese","del":"Delaware","iu":"Inuktitut","ca":"Catalan","hmn":"Hmong","rw":"Kinyarwanda","sl":"Slovenian","tmh":"Tamashek","alt":"Southern Altai","chy":"Cheyenne","gwi":"Gwichʼin","nv":"Navajo","krc":"Karachay-Balkar","smn":"Inari Sami","syc":"Classical Syriac","zh-Hant":"Traditional Chinese","tt":"Tatar","es-MX":"Mexican Spanish","mer":"Meru","mfe":"Morisyen","tiv":"Tiv","th":"Thai","kv":"Komi","oc":"Occitan","os":"Ossetic","osa":"Osage","nnh":"Ngiemboon","pt-BR":"Brazilian Portuguese","syr":"Syriac","bax":"Bamun","cay":"Cayuga","doi":"Dogri","liv":"Livonian","av":"Avaric","ccp":"Chakma","chm":"Mari","men":"Mende","kgp":"Kaingang","la":"Latin","nmg":"Kwasio","pro":"Old Provençal","afh":"Afrihili","bik":"Bikol","chr":"Cherokee","kam":"Kamba","tru":"Turoyo","cy":"Welsh","en-US-alt-short":"US English","kru":"Kurukh","pdt":"Plautdietsch","km":"Khmer","pap":"Papiamento","tcy":"Tulu","ng":"Ndonga","yrl":"Nheengatu","dav":"Taita","gaa":"Ga","hr":"Croatian","luo":"Luo","pa":"Punjabi","sli":"Lower Silesian","ch":"Chamorro","iba":"Iban","kfo":"Koro","sr":"Serbian","szl":"Silesian","elx":"Elamite","kg":"Kongo","ksh":"Colognian","seh":"Sena","sat":"Santali","stq":"Saterland Frisian","twq":"Tasawaq","avk":"Kotava","car":"Carib","mg":"Malagasy","mn":"Mongolian","jut":"Jutish","nb":"Norwegian Bokmål","non":"Old Norse","sah":"Sakha","vai":"Vai","arc":"Aramaic","izh":"Ingrian","nqo":"N’Ko","rwk":"Rwa","nl-BE":"Flemish","an":"Aragonese","frp":"Arpitan","ksb":"Shambala","maf":"Mafa","no":"Norwegian","ssy":"Saho","sux":"Sumerian","xh":"Xhosa","ang":"Old English","bem":"Bemba","hsn":"Xiang Chinese","mh":"Marshallese","teo":"Teso","bss":"Akoose","bug":"Buginese","gan":"Gan Chinese","kn":"Kannada","sm":"Samoan","xmf":"Mingrelian","bar":"Bavarian","cr":"Cree","gor":"Gorontalo","raj":"Rajasthani","dtp":"Central Dusun","en-AU":"Australian English","es-419":"Latin American Spanish","was":"Washo","kl":"Kalaallisut","wae":"Walser","wo":"Wolof","ar-001":"Modern Standard Arabic","gon":"Gondi","mwv":"Mentawai","shn":"Shan","ary":"Moroccan Arabic","az-alt-short":"Azeri","frs":"Eastern Frisian","myv":"Erzya","am":"Amharic","bej":"Beja","goh":"Old High German","tvl":"Tuvalu","cho":"Choctaw","en-GB":"British English","kiu":"Kirmanjki","sid":"Sidamo","ss":"Swati","za":"Zhuang","mye":"Myene","rue":"Rusyn","vep":"Veps","fr-CH":"Swiss French","lv":"Latvian","cps":"Capiznon","de-CH":"Swiss High German","fr":"French","pau":"Palauan","kr":"Kanuri","mzn":"Mazanderani","arq":"Algerian Arabic","ban":"Balinese","kac":"Kachin","kj":"Kuanyama","pal":"Pahlavi","pon":"Pohnpeian","tkr":"Tsakhur","wa":"Walloon","eo":"Esperanto","kri":"Krio","lol":"Mongo","nzi":"Nzima","az":"Azerbaijani","kha":"Khasi","saz":"Saurashtra","sw-CD":"Congo Swahili","oj":"Ojibwa","enm":"Middle English","ga":"Irish","jam":"Jamaican Creole English","kbl":"Kanembu","dsb":"Lower Sorbian","fon":"Fon","lou":"Louisiana Creole","mus":"Creek","mt":"Maltese","zza":"Zaza","bbj":"Ghomala","bjn":"Banjar","ha":"Hausa","mdf":"Moksha","nap":"Neapolitan","ps-alt-variant":"Pushto","rn":"Rundi","sog":"Sogdien","jpr":"Judeo-Persian","kaa":"Kara-Kalpak","ln":"Lingala","min":"Minangkabau","swb":"Comorian","tsd":"Tsakonian","sus":"Susu","crh":"Crimean Turkish","de":"German","den":"Slave","mos":"Mossi","bez":"Bena","ckb":"Central Kurdish","hif":"Fiji Hindi","sbp":"Sangu"}`},
		{"zh-Hans-CN", `{"bez":"贝纳语","ch":"查莫罗语","kmb":"金邦杜语","kru":"库鲁克语","arw":"阿拉瓦克语","cr":"克里族语","frm":"中古法语","hit":"赫梯语","mag":"摩揭陀语","gsw":"瑞士德语","hsb":"上索布语","mic":"密克马克语","sad":"桑达韦语","tn":"茨瓦纳语","gay":"迦约语","gor":"哥伦打洛语","lb":"卢森堡语","scn":"西西里语","vo":"沃拉普克语","ybb":"耶姆巴语","yue":"粤语","byn":"比林语","oj":"奥吉布瓦语","cad":"卡多语","rof":"兰博语","sm":"萨摩亚语","vep":"维普森语","de-AT":"奥地利德语","hz":"赫雷罗语","lv":"拉脱维亚语","efi":"埃菲克语","fil":"菲律宾语","kg":"刚果语","kw":"康沃尔语","nr":"南恩德贝勒语","es-419":"拉丁美洲西班牙语","lol":"蒙戈语","be":"白俄罗斯语","ga":"爱尔兰语","kho":"和田语","tig":"提格雷语","bgn":"西俾路支语","dv":"迪维西语","mdr":"曼达尔语","vai":"瓦伊语","yi":"意第绪语","bfd":"巴非特语","fat":"芳蒂语","guz":"古西语","saq":"桑布鲁语","mak":"望加锡语","pro":"古普罗文斯语","sam":"萨马利亚阿拉姆语","zh-Hant":"繁体中文","an":"阿拉贡语","cay":"卡尤加语","nds":"低地德语","ng":"恩东加语","pl":"波兰语","bin":"比尼语","da":"丹麦语","lrc":"北卢尔语","no":"挪威语","ce":"车臣语","es-ES":"欧洲西班牙语","lus":"米佐语","sr":"塞尔维亚语","tiv":"蒂夫语","gez":"吉兹语","lez":"列兹金语","nd":"北恩德贝勒语","prg":"普鲁士语","qu":"克丘亚语","sd":"信德语","zgh":"标准摩洛哥塔马塞特语","grc":"古希腊语","umb":"翁本杜语","dak":"达科他语","dsb":"下索布语","haw":"夏威夷语","it":"意大利语","ki":"吉库尤语","rwk":"罗瓦语","dz":"宗卡语","tem":"泰姆奈语","az":"阿塞拜疆语","ee":"埃维语","en":"英语","mdf":"莫克沙语","th":"泰语","ain":"阿伊努语","kln":"卡伦金语","pcm":"尼日利亚皮钦语","seh":"塞纳语","ssy":"萨霍语","chy":"夏延语","uk":"乌克兰语","gd":"苏格兰盖尔语","lua":"卢巴-卢拉语","awa":"阿瓦德语","got":"哥特语","gv":"马恩语","ha":"豪萨语","shu":"乍得阿拉伯语","el":"希腊语","gl":"加利西亚语","ksh":"科隆语","ro-MD":"摩尔多瓦语","vi":"越南语","war":"瓦瑞语","gag":"加告兹语","kde":"马孔德语","mgo":"梅塔语","pag":"邦阿西南语","he":"希伯来语","mzn":"马赞德兰语","rom":"吉普赛语","smn":"伊纳里萨米语","tli":"特林吉特语","yo":"约鲁巴语","asa":"帕雷语","mk":"马其顿语","car":"加勒比语","kbl":"加涅姆布语","kut":"库特奈语","or":"奥里亚语","chk":"楚克语","non":"古诺尔斯语","und":"未知语言","wal":"瓦拉莫语","luy":"卢雅语","ca":"加泰罗尼亚语","kkj":"卡库语","bej":"贝沙语","myv":"厄尔兹亚语","rap":"拉帕努伊语","ug":"维吾尔语","lg":"卢干达语","nv":"纳瓦霍语","sq":"阿尔巴尼亚语","tum":"通布卡语","ku":"库尔德语","ter":"特伦诺语","tzm":"塔马齐格特语","xh":"科萨语","ab":"阿布哈西亚语","bax":"巴姆穆语","bug":"布吉语","doi":"多格拉语","ve":"文达语","chr":"切罗基语","dar":"达尔格瓦语","mfe":"毛里求斯克里奥尔语","raj":"拉贾斯坦语","mgh":"马库阿语","tl":"他加禄语","bg":"保加利亚语","byv":"梅敦巴语","grb":"格列博语","kos":"科斯拉伊语","sdh":"南库尔德语","si":"僧伽罗语","swb":"科摩罗语","cy":"威尔士语","lah":"印度-雅利安语","mas":"马赛语","nan":"闽南语","sco":"苏格兰语","udm":"乌德穆尔特语","ff":"富拉语","gan":"赣语","pi":"巴利语","xog":"索加语","gu":"古吉拉特语","nog":"诺盖语","ckb":"中库尔德语","man":"曼丁哥语","kum":"库梅克语","sg":"桑戈语","tpi":"托克皮辛语","yap":"雅浦语","is":"冰岛语","li":"林堡语","mga":"中古爱尔兰语","mh":"马绍尔语","pau":"帕劳语","sus":"苏苏语","kha":"卡西语","niu":"纽埃语","nqo":"西非书面文字","en-GB":"英国英语","gil":"吉尔伯特语","hsn":"湘语","iba":"伊班语","ky":"柯尔克孜语","bs":"波斯尼亚语","ts":"聪加语","mt":"马耳他语","to":"汤加语","ii":"四川彝语","iu":"因纽特语","men":"门德语","trv":"赛德克语","ady":"阿迪格语","chg":"察合台语","din":"丁卡语","es-MX":"墨西哥西班牙语","frc":"卡真法语","en-US-alt-short":"美式英语","koi":"科米-彼尔米亚克语","kok":"孔卡尼语","mus":"克里克语","ba":"巴什基尔语","ig":"伊博语","ja":"日语","lad":"拉迪诺语","tk":"土库曼语","tkl":"托克劳语","akk":"阿卡德语","ho":"希里莫图语","kj":"宽亚玛语","nl-BE":"弗拉芒语","sga":"古爱尔兰语","gmh":"中古高地德语","kv":"科米语","so":"索马里语","jmc":"马切姆语","ka":"格鲁吉亚语","sux":"苏美尔语","zh":"中文","mye":"姆耶内语","rup":"阿罗马尼亚语","sv":"瑞典语","nus":"努埃尔语","sel":"塞尔库普语","zza":"扎扎语","ik":"伊努皮克语","mde":"马坝语","ne":"尼泊尔语","sah":"萨哈语","ty":"塔希提语","zen":"泽纳加语","agq":"亚罕语","ia":"国际语","sw":"斯瓦希里语","tsi":"钦西安语","ar-001":"现代标准阿拉伯语","arc":"阿拉米语","bkm":"科姆语","my":"缅甸语","ses":"东桑海语","su":"巽他语","tog":"尼亚萨汤加语","arp":"阿拉帕霍语","cv":"楚瓦什语","gba":"格巴亚语","kam":"卡姆巴语","nl":"荷兰语","ks":"克什米尔语","chm":"马里语","cho":"乔克托语","fr-CA":"加拿大法语","kr":"卡努里语","et":"爱沙尼亚语","khq":"西桑海语","lui":"卢伊塞诺语","mn":"蒙古语","rn":"隆迪语","del":"特拉华语","hr":"克罗地亚语","io":"伊多语","mni":"曼尼普尔语","ota":"奥斯曼土耳其语","bho":"博杰普尔语","chb":"奇布查语","dyo":"朱拉语","kpe":"克佩列语","root":"根语言","aa":"阿法尔语","ang":"古英语","new":"尼瓦尔语","nym":"尼扬韦齐语","pt-PT":"欧洲葡萄牙语","sh":"塞尔维亚-克罗地亚语","srr":"塞雷尔语","sw-CD":"刚果斯瓦希里语","ur":"乌尔都语","ast":"阿斯图里亚斯语","bm":"班巴拉语","eka":"艾卡朱克语","lu":"鲁巴加丹加语","syc":"古典叙利亚语","fan":"芳格语","tr":"土耳其语","sk":"斯洛伐克语","srn":"苏里南汤加语","fy":"西弗里西亚语","gaa":"加族语","ht":"海地克里奥尔语","ny":"齐切瓦语","quc":"基切语","ale":"阿留申语","en-US":"美国英语","bas":"巴萨语","egy":"古埃及语","jv":"爪哇语","kac":"克钦语","mer":"梅鲁语","min":"米南佳保语","mr":"马拉地语","oc":"奥克语","se":"北方萨米语","ae":"阿维斯塔语","zap":"萨波蒂克语","br":"布列塔尼语","ru":"俄语","ss":"斯瓦蒂语","ban":"巴厘语","cch":"阿灿语","en-CA":"加拿大英语","snk":"索宁克语","bum":"布鲁语","elx":"埃兰语","ksf":"巴菲亚语","mwr":"马尔瓦里语","tet":"德顿语","bss":"阿库色语","dua":"都阿拉语","hy":"亚美尼亚语","mwl":"米兰德斯语","suk":"苏库马语","tmh":"塔马奇克语","fi":"芬兰语","lun":"隆达语","nia":"尼亚斯语","az-Arab":"南阿塞拜疆语","hmn":"苗语","ps":"普什图语","ie":"国际文字（E）","nb":"书面挪威语","shi":"希尔哈语","sog":"粟特语","den":"史拉维语","en-AU":"澳大利亚英语","eu":"巴斯克语","kab":"卡拜尔语","kcg":"卡塔布语","twq":"北桑海语","wo":"沃洛夫语","zxx":"无语言内容","af":"南非荷兰语","cgg":"奇加语","fo":"法罗语","ksb":"香巴拉语","lag":"朗吉语","lt":"立陶宛语","ta":"泰米尔语","inh":"印古什语","bbj":"戈马拉语","bik":"比科尔语","bn":"孟加拉语","kfo":"克罗语","pt":"葡萄牙语","rar":"拉罗汤加语","sat":"桑塔利语","yav":"洋卞语","ace":"亚齐语","brx":"博多语","rw":"卢旺达语","sba":"甘拜语","st":"南索托语","kn":"卡纳达语","lou":"路易斯安那克里奥尔语","mg":"马拉加斯语","vun":"温旧语","mua":"蒙当语","nzi":"恩济马语","os":"奥塞梯语","xal":"卡尔梅克语","alt":"南阿尔泰语","chn":"奇努克混合语","gn":"瓜拉尼语","mad":"马都拉语","mi":"毛利语","naq":"纳马语","nds-NL":"低萨克森语","sas":"萨萨克文","tg":"塔吉克语","goh":"古高地德语","gwi":"哥威迅语","jrb":"犹太阿拉伯语","nmg":"夸西奥语","ada":"阿当梅语","ceb":"宿务语","cu":"教会斯拉夫语","nwc":"古典尼瓦尔语","syr":"叙利亚语","wa":"瓦隆语","ar":"阿拉伯语","bem":"本巴语","nyn":"尼昂科勒语","pal":"巴拉维语","av":"阿瓦尔语","fr-CH":"瑞士法语","kbd":"卡巴尔德语","nso":"北索托语","sid":"悉达摩语","za":"壮语","bra":"布拉杰语","bla":"西克西卡语","hup":"胡帕语","mos":"莫西语","om":"奥罗莫语","as":"阿萨姆语","de":"德语","krl":"卡累利阿语","luo":"卢欧语","ml":"马拉雅拉姆语","phn":"腓尼基语","bal":"俾路支语","hil":"希利盖农语","ibb":"伊比比奥语","sma":"南萨米语","sn":"绍纳语","zh-Hans":"简体中文","id":"印度尼西亚语","wae":"瓦尔瑟语","dum":"中古荷兰语","fon":"丰语","ln":"林加拉语","sl":"斯洛文尼亚语","gon":"冈德语","jbo":"逻辑语","la":"拉丁语","ay":"艾马拉语","fur":"弗留利语","kaj":"卡捷语","pam":"邦板牙语","see":"塞内卡语","tlh":"克林贡语","crh":"克里米亚土耳其语","crs":"塞舌尔克里奥尔语","ewo":"旺杜语","hai":"海达语","ro":"罗马尼亚语","fj":"斐济语","ilo":"伊洛卡诺语","km":"高棉语","pon":"波纳佩语","sc":"萨丁语","vot":"沃提克语","lo":"老挝语","nnh":"恩甘澎语","rm":"罗曼什语","shn":"掸语","tt":"鞑靼语","frr":"北弗里西亚语","ko":"韩语","mai":"迈蒂利语","nyo":"尼奥罗语","pa":"旁遮普语","bi":"比斯拉马语","hi":"印地语","kaa":"卡拉卡尔帕克语","anp":"昂加语","kl":"格陵兰语","osa":"奥塞治语","smj":"吕勒萨米语","zun":"祖尼语","ach":"阿乔利语","eo":"世界语","kk":"哈萨克语","maf":"马法语","na":"瑙鲁语","bua":"布里亚特语","dje":"哲尔马语","es":"西班牙语","lkt":"拉科塔语","tvl":"图瓦卢语","uz":"乌兹别克语","was":"瓦绍语","yao":"瑶族语","afh":"阿弗里希利语","ak":"阿肯语","fro":"古法语","loz":"洛齐语","te":"泰卢固语","tw":"契维语","wuu":"吴语","dyu":"迪尤拉语","krc":"卡拉恰伊巴尔卡尔语","mul":"多语种","pt-BR":"巴西葡萄牙语","bo":"藏语","cop":"科普特语","ebu":"恩布语","fa":"波斯语","frs":"东弗里西亚语","hak":"客家语","hu":"匈牙利语","nap":"那不勒斯语","nn":"挪威尼诺斯克语","de-CH":"瑞士高地德语","jpr":"犹太波斯语","mnc":"满语","pap":"帕皮阿门托语","peo":"古波斯语","sa":"梵语","sbp":"桑古语","sms":"斯科特萨米语","uga":"乌加里特语","co":"科西嘉语","arn":"马普切语","csb":"卡舒比语","jgo":"恩艮巴语","lam":"兰巴语","az-alt-short":"阿塞语","dzg":"达扎葛语","kea":"卡布佛得鲁语","teo":"特索语","ti":"提格利尼亚语","cs":"捷克语","dav":"台塔语","en-GB-alt-short":"英式英语","enm":"中古英语","fr":"法语","moh":"摩霍克语","zu":"祖鲁语","am":"阿姆哈拉语","chp":"奇佩维安语","dgr":"多格里布语","kaw":"卡威语","ms":"马来语","tyv":"图瓦语","wbp":"瓦尔皮瑞语","zbl":"布里斯符号"}`},
		{"zh-CN", `{"ewo":"旺杜语","frm":"中古法语","mua":"蒙当语","sux":"苏美尔语","bug":"布吉语","byn":"比林语","cch":"阿灿语","kl":"格陵兰语","bax":"巴姆穆语","lkt":"拉科塔语","ro":"罗马尼亚语","zgh":"标准摩洛哥塔马塞特语","af":"南非荷兰语","bho":"博杰普尔语","din":"丁卡语","hsn":"湘语","mr":"马拉地语","sh":"塞尔维亚-克罗地亚语","cho":"乔克托语","chp":"奇佩维安语","mad":"马都拉语","nzi":"恩济马语","bal":"俾路支语","en-US-alt-short":"美式英语","kos":"科斯拉伊语","sc":"萨丁语","sw-CD":"刚果斯瓦希里语","car":"加勒比语","fur":"弗留利语","hup":"胡帕语","ik":"伊努皮克语","mk":"马其顿语","tkl":"托克劳语","guz":"古西语","jmc":"马切姆语","ko":"韩语","nl-BE":"弗拉芒语","non":"古诺尔斯语","ss":"斯瓦蒂语","ty":"塔希提语","udm":"乌德穆尔特语","uga":"乌加里特语","zxx":"无语言内容","kj":"宽亚玛语","rwk":"罗瓦语","saq":"桑布鲁语","bss":"阿库色语","gl":"加利西亚语","krl":"卡累利阿语","nd":"北恩德贝勒语","pal":"巴拉维语","quc":"基切语","chk":"楚克语","min":"米南佳保语","ny":"齐切瓦语","or":"奥里亚语","shi":"希尔哈语","tw":"契维语","vi":"越南语","dje":"哲尔马语","egy":"古埃及语","ff":"富拉语","fr-CA":"加拿大法语","frr":"北弗里西亚语","na":"瑙鲁语","lua":"卢巴-卢拉语","sd":"信德语","be":"白俄罗斯语","cy":"威尔士语","dgr":"多格里布语","fil":"菲律宾语","kaw":"卡威语","lb":"卢森堡语","lui":"卢伊塞诺语","nym":"尼扬韦齐语","und":"未知语言","bez":"贝纳语","ceb":"宿务语","de-CH":"瑞士高地德语","ml":"马拉雅拉姆语","sas":"萨萨克文","yo":"约鲁巴语","bas":"巴萨语","gay":"迦约语","kea":"卡布佛得鲁语","lou":"路易斯安那克里奥尔语","nds":"低地德语","bej":"贝沙语","ms":"马来语","sm":"萨摩亚语","ur":"乌尔都语","fy":"西弗里西亚语","jpr":"犹太波斯语","pcm":"尼日利亚皮钦语","sah":"萨哈语","tig":"提格雷语","vo":"沃拉普克语","en":"英语","ksb":"香巴拉语","mul":"多语种","mus":"克里克语","ar":"阿拉伯语","tt":"鞑靼语","wa":"瓦隆语","kaa":"卡拉卡尔帕克语","nan":"闽南语","shu":"乍得阿拉伯语","afh":"阿弗里希利语","arn":"马普切语","kac":"克钦语","tzm":"塔马齐格特语","sga":"古爱尔兰语","co":"科西嘉语","es":"西班牙语","gd":"苏格兰盖尔语","gil":"吉尔伯特语","rup":"阿罗马尼亚语","sdh":"南库尔德语","chm":"马里语","gan":"赣语","zen":"泽纳加语","awa":"阿瓦德语","chb":"奇布查语","maf":"马法语","raj":"拉贾斯坦语","zh":"中文","dak":"达科他语","hmn":"苗语","luy":"卢雅语","moh":"摩霍克语","chr":"切罗基语","kln":"卡伦金语","mg":"马拉加斯语","pam":"邦板牙语","zbl":"布里斯符号","iba":"伊班语","jbo":"逻辑语","kv":"科米语","zh-Hant":"繁体中文","ro-MD":"摩尔多瓦语","ang":"古英语","ban":"巴厘语","br":"布列塔尼语","ig":"伊博语","lah":"印度-雅利安语","nus":"努埃尔语","ch":"查莫罗语","umb":"翁本杜语","de":"德语","gez":"吉兹语","hu":"匈牙利语","te":"泰卢固语","bla":"西克西卡语","io":"伊多语","iu":"因纽特语","ja":"日语","ks":"克什米尔语","mh":"马绍尔语","ee":"埃维语","hil":"希利盖农语","kbd":"卡巴尔德语","kho":"和田语","tem":"泰姆奈语","ay":"艾马拉语","men":"门德语","mer":"梅鲁语","vep":"维普森语","ca":"加泰罗尼亚语","inh":"印古什语","sus":"苏苏语","wo":"沃洛夫语","cu":"教会斯拉夫语","lad":"拉迪诺语","so":"索马里语","th":"泰语","teo":"特索语","tli":"特林吉特语","ain":"阿伊努语","arc":"阿拉米语","chn":"奇努克混合语","dar":"达尔格瓦语","dav":"台塔语","la":"拉丁语","twq":"北桑海语","ybb":"耶姆巴语","enm":"中古英语","lam":"兰巴语","fi":"芬兰语","ha":"豪萨语","ale":"阿留申语","root":"根语言","vun":"温旧语","ab":"阿布哈西亚语","as":"阿萨姆语","en-GB-alt-short":"英式英语","fan":"芳格语","kr":"卡努里语","shn":"掸语","az-Arab":"南阿塞拜疆语","trv":"赛德克语","yav":"洋卞语","es-419":"拉丁美洲西班牙语","kg":"刚果语","peo":"古波斯语","am":"阿姆哈拉语","nwc":"古典尼瓦尔语","tmh":"塔马奇克语","fj":"斐济语","hi":"印地语","mnc":"满语","sa":"梵语","wuu":"吴语","av":"阿瓦尔语","cop":"科普特语","lol":"蒙戈语","pro":"古普罗文斯语","bi":"比斯拉马语","cay":"卡尤加语","frs":"东弗里西亚语","ada":"阿当梅语","eka":"艾卡朱克语","it":"意大利语","ka":"格鲁吉亚语","tlh":"克林贡语","tn":"茨瓦纳语","bik":"比科尔语","hz":"赫雷罗语","pl":"波兰语","pon":"波纳佩语","zh-Hans":"简体中文","en-AU":"澳大利亚英语","gaa":"加族语","ne":"尼泊尔语","oc":"奥克语","sad":"桑达韦语","syr":"叙利亚语","del":"特拉华语","dyu":"迪尤拉语","gor":"哥伦打洛语","kw":"康沃尔语","see":"塞内卡语","frc":"卡真法语","nds-NL":"低萨克森语","sr":"塞尔维亚语","ie":"国际文字（E）","nog":"诺盖语","snk":"索宁克语","yi":"意第绪语","elx":"埃兰语","gba":"格巴亚语","kha":"卡西语","lo":"老挝语","mak":"望加锡语","uz":"乌兹别克语","anp":"昂加语","csb":"卡舒比语","mag":"摩揭陀语","myv":"厄尔兹亚语","got":"哥特语","jrb":"犹太阿拉伯语","mde":"马坝语","mt":"马耳他语","nn":"挪威尼诺斯克语","smj":"吕勒萨米语","ta":"泰米尔语","ti":"提格利尼亚语","nqo":"西非书面文字","rap":"拉帕努伊语","ast":"阿斯图里亚斯语","brx":"博多语","ga":"爱尔兰语","kn":"卡纳达语","mgo":"梅塔语","mic":"密克马克语","sg":"桑戈语","ksf":"巴菲亚语","bo":"藏语","ku":"库尔德语","mai":"迈蒂利语","zza":"扎扎语","fa":"波斯语","pap":"帕皮阿门托语","yue":"粤语","kcg":"卡塔布语","mfe":"毛里求斯克里奥尔语","sba":"甘拜语","jv":"爪哇语","pau":"帕劳语","rm":"罗曼什语","km":"高棉语","man":"曼丁哥语","ota":"奥斯曼土耳其语","tet":"德顿语","xal":"卡尔梅克语","fat":"芳蒂语","kab":"卡拜尔语","lv":"拉脱维亚语","prg":"普鲁士语","arp":"阿拉帕霍语","li":"林堡语","nnh":"恩甘澎语","syc":"古典叙利亚语","ar-001":"现代标准阿拉伯语","bg":"保加利亚语","gv":"马恩语","sms":"斯科特萨米语","ssy":"萨霍语","tl":"他加禄语","ace":"亚齐语","bin":"比尼语","da":"丹麦语","dv":"迪维西语","lu":"鲁巴加丹加语","mi":"毛利语","mn":"蒙古语","nl":"荷兰语","nyo":"尼奥罗语","yao":"瑶族语","dua":"都阿拉语","fro":"古法语","bgn":"西俾路支语","rw":"卢旺达语","srr":"塞雷尔语","kk":"哈萨克语","lrc":"北卢尔语","naq":"纳马语","sma":"南萨米语","dyo":"朱拉语","kum":"库梅克语","mzn":"马赞德兰语","was":"瓦绍语","ve":"文达语","den":"史拉维语","hit":"赫梯语","nso":"北索托语","bem":"本巴语","byv":"梅敦巴语","khq":"西桑海语","mgh":"马库阿语","tvl":"图瓦卢语","ckb":"中库尔德语","cv":"楚瓦什语","ht":"海地克里奥尔语","nyn":"尼昂科勒语","rof":"兰博语","sel":"塞尔库普语","grc":"古希腊语","kbl":"加涅姆布语","mni":"曼尼普尔语","ng":"恩东加语","tr":"土耳其语","ts":"聪加语","uk":"乌克兰语","ky":"柯尔克孜语","lez":"列兹金语","kpe":"克佩列语","os":"奥塞梯语","xog":"索加语","ba":"巴什基尔语","dz":"宗卡语","el":"希腊语","ksh":"科隆语","ter":"特伦诺语","es-MX":"墨西哥西班牙语","kok":"孔卡尼语","mwr":"马尔瓦里语","nia":"尼亚斯语","nv":"纳瓦霍语","suk":"苏库马语","gon":"冈德语","hak":"客家语","hy":"亚美尼亚语","luo":"卢欧语","ps":"普什图语","sat":"桑塔利语","bra":"布拉杰语","swb":"科摩罗语","kaj":"卡捷语","pt-PT":"欧洲葡萄牙语","asa":"帕雷语","bfd":"巴非特语","bkm":"科姆语","bs":"波斯尼亚语","crh":"克里米亚土耳其语","fo":"法罗语","rn":"隆迪语","smn":"伊纳里萨米语","ak":"阿肯语","ki":"吉库尤语","osa":"奥塞治语","dzg":"达扎葛语","hr":"克罗地亚语","ses":"东桑海语","tum":"通布卡语","ug":"维吾尔语","gwi":"哥威迅语","tog":"尼亚萨汤加语","vot":"沃提克语","wbp":"瓦尔皮瑞语","chy":"夏延语","he":"希伯来语","kru":"库鲁克语","pi":"巴利语","rar":"拉罗汤加语","sco":"苏格兰语","bbj":"戈马拉语","es-ES":"欧洲西班牙语","my":"缅甸语","nb":"书面挪威语","ru":"俄语","eo":"世界语","lus":"米佐语","mas":"马赛语","oj":"奥吉布瓦语","qu":"克丘亚语","crs":"塞舌尔克里奥尔语","en-GB":"英国英语","fon":"丰语","koi":"科米-彼尔米亚克语","pt-BR":"巴西葡萄牙语","sog":"粟特语","jgo":"恩艮巴语","scn":"西西里语","ady":"阿迪格语","agq":"亚罕语","bn":"孟加拉语","doi":"多格拉语","efi":"埃菲克语","gag":"加告兹语","sid":"悉达摩语","tk":"土库曼语","cs":"捷克语","gu":"古吉拉特语","haw":"夏威夷语","kut":"库特奈语","en-CA":"加拿大英语","war":"瓦瑞语","yap":"雅浦语","az":"阿塞拜疆语","id":"印度尼西亚语","lt":"立陶宛语","nap":"那不勒斯语","nmg":"夸西奥语","sam":"萨马利亚阿拉姆语","zun":"祖尼语","bm":"班巴拉语","dum":"中古荷兰语","gmh":"中古高地德语","rom":"吉普赛语","tyv":"图瓦语","bua":"布里亚特语","de-AT":"奥地利德语","mdr":"曼达尔语","tg":"塔吉克语","hai":"海达语","kkj":"卡库语","phn":"腓尼基语","st":"南索托语","sbp":"桑古语","wal":"瓦拉莫语","akk":"阿卡德语","cad":"卡多语","cgg":"奇加语","pa":"旁遮普语","ilo":"伊洛卡诺语","lag":"朗吉语","niu":"纽埃语","om":"奥罗莫语","su":"巽他语","az-alt-short":"阿塞语","ho":"希里莫图语","is":"冰岛语","zap":"萨波蒂克语","ln":"林加拉语","se":"北方萨米语","seh":"塞纳语","tpi":"托克皮辛语","xh":"科萨语","hsb":"上索布语","kam":"卡姆巴语","mga":"中古爱尔兰语","ibb":"伊比比奥语","kmb":"金邦杜语","sk":"斯洛伐克语","tsi":"钦西安语","mdf":"莫克沙语","si":"僧伽罗语","alt":"南阿尔泰语","bum":"布鲁语","ebu":"恩布语","lg":"卢干达语","sw":"斯瓦希里语","cr":"克里族语","goh":"古高地德语","no":"挪威语","sl":"斯洛文尼亚语","pag":"邦阿西南语","zu":"祖鲁语","ae":"阿维斯塔语","ii":"四川彝语","kde":"马孔德语","kfo":"克罗语","lun":"隆达语","mye":"姆耶内语","ia":"国际语","new":"尼瓦尔语","wae":"瓦尔瑟语","an":"阿拉贡语","en-US":"美国英语","pt":"葡萄牙语","sn":"绍纳语","ach":"阿乔利语","dsb":"下索布语","gsw":"瑞士德语","sq":"阿尔巴尼亚语","srn":"苏里南汤加语","vai":"瓦伊语","et":"爱沙尼亚语","nr":"南恩德贝勒语","mos":"莫西语","to":"汤加语","chg":"察合台语","krc":"卡拉恰伊巴尔卡尔语","tiv":"蒂夫语","za":"壮语","aa":"阿法尔语","arw":"阿拉瓦克语","eu":"巴斯克语","fr-CH":"瑞士法语","gn":"瓜拉尼语","sv":"瑞典语","ce":"车臣语","fr":"法语","grb":"格列博语","loz":"洛齐语","mwl":"米兰德斯语"} `},
	} {
		d := d
		t.Run(d.input, func(t *testing.T) {
			normalLocale := strings.ToLower(coreutil.GetCLDRLocale(d.input))
			data, err := localeutil.GetLocaleLanguages(context.TODO(), normalLocale)
			assert.Nil(t, err)

			var expected map[string]string
			json.UnmarshalFromString(d.wanted, &expected)
			assert.True(t, reflect.DeepEqual(expected, data))
		})
	}
}

func TestGetPathLocale(t *testing.T) {
	for _, d := range []struct{ input, wanted string }{
		{"en", "en"},
		{"en-US", ""},
		{"zh-Hans-CN", "zh-Hans"},
		{"zh-CN", "zh-Hans"},
	} {
		d := d
		t.Run(d.input, func(t *testing.T) {
			newLocale := coreutil.GetPathLocale(d.input)
			assert.Equal(t, d.wanted, newLocale)
		})
	}
}

func TestGetRegionsFunc(t *testing.T) {
	for _, d := range []struct{ input, wanted string }{
		{"en", "United Arab Emirates"},
		{"en-US", "United Arab Emirates"},
		{"zh-Hans-CN", "阿拉伯联合酋长国"},
		{"zh-CN", "阿拉伯联合酋长国"},
	} {
		d := d
		t.Run(d.input, func(t *testing.T) {
			data, err := localeutil.GetTerritoriesOfMultipleLocales(context.TODO(), []string{d.input})
			assert.Nil(t, err)
			bts, _ := json.Marshal(data)
			assert.Contains(t, string(bts), d.wanted)
		})
	}
}

func TestScopeFilter(t *testing.T) {
	for _, d := range []struct{ locale, scope, scopeFilter string }{
		{"en", "dates", "dates_dayPeriodsFormat,dates_dayPeriodsStandalone"},
		{"en", "dates", "^(dates_dayPeriodsFormat,dates_dayPeriodsStandalone)"},
	} {
		d := d
		t.Run(fmt.Sprintf("%v", d), func(t *testing.T) {
			result, cldrLocale, err := cldrservice.GetPatternByLocale(context.TODO(), d.locale, d.scope, d.scopeFilter)
			assert.Nil(t, err)
			assert.Equal(t, "en", cldrLocale)

			newFilter := strings.ReplaceAll(d.scopeFilter, "_", ".")
			newFilter = strings.TrimSuffix(strings.TrimPrefix(newFilter, "^("), ")")
			newMap := objx.Map(result)
			for _, f := range strings.Split(newFilter, common.ParamSep) {
				subNode := newMap.Get(f).Data()
				if strings.HasPrefix(d.scopeFilter, "^") {
					assert.Nil(t, subNode)
				} else {
					assert.NotNil(t, subNode)
				}
			}
		})
	}
}

func TestCLDRCache(t *testing.T) {
	if !localeutil.EnableCache {
		return
	}

	c, ok := cache.GetCache("cldr")
	assert.True(t, ok)
	c.Clear()

	locale, scope := "en", "dates"
	_, data, err := cldrservice.GetPatternByLocale(context.TODO(), locale, scope, "")
	assert.Nil(t, err)

	time.Sleep(time.Millisecond)
	cachedData, err := c.Get(scope + ":" + locale)
	assert.Nil(t, err)
	assert.NotNil(t, cachedData)

	// query again to verify Cache works properly
	_, data2, err := cldrservice.GetPatternByLocale(context.TODO(), locale, scope, "")
	assert.Nil(t, err)
	assert.True(t, assert.ObjectsAreEqual(data, data2))
}

// TestParallelGetPattern Test DoAndWait by getting pattern parallelly
func TestParallelGetPattern(t *testing.T) {
	// clear the cache to test querying parallelly
	c, _ := cache.GetCache("cldr")
	c.Clear()

	var wg sync.WaitGroup
	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func(i int) {
			defer wg.Done()
			_, _, err := cldrservice.GetPatternByLocale(
				logger.NewContext(context.TODO(), logger.Log.With(zap.Int("thread", i))),
				Locale,
				"dates",
				"")
			assert.Nil(t, err, "error is %+v", err)
		}(i)
	}

	wg.Wait()
}

func TestGetLocaleByLangRegFunc(t *testing.T) {
	tests := []struct {
		testName         string
		language, region string
		wanted           string
	}{
		{testName: "en, US", language: Language, region: Region, wanted: Locale},
		{testName: "en-GB, US", language: "en-GB", region: Region, wanted: Locale},
		{testName: "en_GB, US", language: "en_GB", region: Region, wanted: Locale},
		{testName: "en_GB, GB", language: "en_GB", region: "GB", wanted: "en-GB"},
		{testName: "en_Script_GB, GB", language: "en_Script_GB", region: "GB", wanted: "en-GB"},
		{testName: "zh-Hans, US", language: "zh-Hans", region: Region, wanted: ""},
		{testName: "zh-Hans, XXX", language: "zh-Hans", region: "XXX", wanted: ""},
		{testName: "XXX, CN", language: "XXX", region: "CN", wanted: ""},
		{testName: "zh-Hans-CN, US", language: "zh-Hans-CN", region: Region, wanted: ""},
		{testName: "zh-Hans-CN, HK", language: "zh-Hans-CN", region: "HK", wanted: "zh-Hans-HK"},
		{testName: "es-MX, US", language: "es-MX", region: "US", wanted: "es-US"},
	}
	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			actual := coreutil.GetLocaleByLangReg(tt.language, tt.region)
			assert.Equal(t, tt.wanted, actual)
		})
	}
}

func TestParseLocaleFunc(t *testing.T) {
	tests := []struct {
		testName string
		locale   string
		wanted   *coreutil.Locale
	}{
		{testName: "en-US", locale: "en-US", wanted: &coreutil.Locale{Language: Language, Region: Region, Scripts: ""}},
		{testName: "en_US", locale: "en_US", wanted: &coreutil.Locale{Language: Language, Region: Region, Scripts: ""}},
		{testName: "zh-Hans-XXX", locale: "zh-Hans-XXX", wanted: nil},
		{testName: "en_Script_GB", locale: "en_Script_GB", wanted: nil},
		{testName: "zh-Hans", locale: "zh-Hans", wanted: &coreutil.Locale{Language: "zh", Region: "", Scripts: "Hans"}},
		{testName: "zh-Hans-CN", locale: "zh-Hans-CN", wanted: &coreutil.Locale{Language: "zh", Region: "CN", Scripts: "Hans"}},
		{testName: "XXX-Hans", locale: "XXX-Hans", wanted: nil},
		{testName: "ES-419", locale: "ES-419", wanted: &coreutil.Locale{Language: "es", Region: "419", Scripts: ""}},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			actual := coreutil.ParseLocale(tt.locale)
			if diff := deep.Equal(tt.wanted, actual); diff != nil {
				t.Error(diff)
			}
		})
	}
}
