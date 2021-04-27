/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"

	_ "sgtnserver/api/v2/cldr"
	"sgtnserver/internal/sgtnerror"
)

func TestGetPatternByLangReg(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	for _, d := range []struct {
		lang, reg, scope, wanted string
		wantedCode               int
	}{
		{lang: "en-US", reg: "US", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"en","language":"en-US","region":"US","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "a", "p" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"daysStandalone" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"eras" : {"narrow" : [ "B", "A" ],"abbreviated" : [ "BC", "AD" ],"wide" : [ "Before Christ", "Anno Domini" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "M/d/yy","medium" : "MMM d, y","long" : "MMMM d, y","full" : "EEEE, MMMM d, y"},"timeFormats" : {"short" : "h:mm a","medium" : "h:mm:ss a","long" : "h:mm:ss a z","full" : "h:mm:ss a zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'at' {0}","full" : "{1} 'at' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{0} {1}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{0} {1}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH – HH"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M – M"},"Md" : {"d" : "M/d – M/d","M" : "M/d – M/d"},"MEd" : {"d" : "E, M/d – E, M/d","M" : "E, M/d – E, M/d"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "MMM d – d","M" : "MMM d – MMM d"},"MMMEd" : {"d" : "E, MMM d – E, MMM d","M" : "E, MMM d – E, MMM d"},"y" : {"y" : "y – y"},"yM" : {"y" : "M/y – M/y","M" : "M/y – M/y"},"yMd" : {"d" : "M/d/y – M/d/y","y" : "M/d/y – M/d/y","M" : "M/d/y – M/d/y"},"yMEd" : {"d" : "E, M/d/y – E, M/d/y","y" : "E, M/d/y – E, M/d/y","M" : "E, M/d/y – E, M/d/y"},"yMMM" : {"y" : "MMM y – MMM y","M" : "MMM – MMM y"},"yMMMd" : {"d" : "MMM d – d, y","y" : "MMM d, y – MMM d, y","M" : "MMM d – MMM d, y"},"yMMMEd" : {"d" : "E, MMM d – E, MMM d, y","y" : "E, MMM d, y – E, MMM d, y","M" : "E, MMM d – E, MMM d, y"},"yMMMM" : {"y" : "MMMM y – MMMM y","M" : "MMMM – MMMM y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "d E","Ehm" : "E h:mm a","EHm" : "E HH:mm","Ehms" : "E h:mm:ss a","EHms" : "E HH:mm:ss","Gy" : "y G","GyMMM" : "MMM y G","GyMMMd" : "MMM d, y G","GyMMMEd" : "E, MMM d, y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "h:mm a zzzz","M" : "L","Md" : "M/d","MEd" : "E, M/d","MMM" : "LLL","MMMd" : "MMM d","MMMEd" : "E, MMM d","MMMMd" : "MMMM d","MMMMW-count-one" : "'week' W 'of' MMMM","MMMMW-count-other" : "'week' W 'of' MMMM","ms" : "mm:ss","y" : "y","yM" : "M/y","yMd" : "M/d/y","yMEd" : "E, M/d/y","yMMM" : "MMM y","yMMMd" : "MMM d, y","yMMMEd" : "E, MMM d, y","yMMMM" : "MMMM y","yQQQ" : "QQQ y","yQQQQ" : "QQQQ y","yw-count-one" : "'week' w 'of' Y","yw-count-other" : "'week' w 'of' Y"}}}}}}`},
		{lang: "en", reg: "US", scope: "numbers", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"en","language":"en","region":"US","categories":{"numbers":{"defaultNumberingSystem":"latn","numberFormats":{"currencyFormats":"¤#,##0.00","currencyFormats-short":{"standard":{"1000-count-one":"¤0K","1000-count-other":"¤0K","10000-count-one":"¤00K","10000-count-other":"¤00K","100000-count-one":"¤000K","100000-count-other":"¤000K","1000000-count-one":"¤0M","1000000-count-other":"¤0M","10000000-count-one":"¤00M","10000000-count-other":"¤00M","100000000-count-one":"¤000M","100000000-count-other":"¤000M","1000000000-count-one":"¤0B","1000000000-count-other":"¤0B","10000000000-count-one":"¤00B","10000000000-count-other":"¤00B","100000000000-count-one":"¤000B","100000000000-count-other":"¤000B","1000000000000-count-one":"¤0T","1000000000000-count-other":"¤0T","10000000000000-count-one":"¤00T","10000000000000-count-other":"¤00T","100000000000000-count-one":"¤000T","100000000000000-count-other":"¤000T"}},"decimalFormats":"#,##0.###","decimalFormats-long":{"decimalFormat":{"1000-count-one":"0 thousand","1000-count-other":"0 thousand","10000-count-one":"00 thousand","10000-count-other":"00 thousand","100000-count-one":"000 thousand","100000-count-other":"000 thousand","1000000-count-one":"0 million","1000000-count-other":"0 million","10000000-count-one":"00 million","10000000-count-other":"00 million","100000000-count-one":"000 million","100000000-count-other":"000 million","1000000000-count-one":"0 billion","1000000000-count-other":"0 billion","10000000000-count-one":"00 billion","10000000000-count-other":"00 billion","100000000000-count-one":"000 billion","100000000000-count-other":"000 billion","1000000000000-count-one":"0 trillion","1000000000000-count-other":"0 trillion","10000000000000-count-one":"00 trillion","10000000000000-count-other":"00 trillion","100000000000000-count-one":"000 trillion","100000000000000-count-other":"000 trillion"}},"decimalFormats-short":{"decimalFormat":{"1000-count-one":"0K","1000-count-other":"0K","10000-count-one":"00K","10000-count-other":"00K","100000-count-one":"000K","100000-count-other":"000K","1000000-count-one":"0M","1000000-count-other":"0M","10000000-count-one":"00M","10000000-count-other":"00M","100000000-count-one":"000M","100000000-count-other":"000M","1000000000-count-one":"0B","1000000000-count-other":"0B","10000000000-count-one":"00B","10000000000-count-other":"00B","100000000000-count-one":"000B","100000000000-count-other":"000B","1000000000000-count-one":"0T","1000000000000-count-other":"0T","10000000000000-count-one":"00T","10000000000000-count-other":"00T","100000000000000-count-one":"000T","100000000000000-count-other":"000T"}},"percentFormats":"#,##0%","scientificFormats":"#E0"},"numberSymbols":{"decimal":".","exponential":"E","group":",","infinity":"∞","list":";","minusSign":"-","nan":"NaN","perMille":"‰","percentSign":"%","plusSign":"+","superscriptingExponent":"×","timeSeparator":":"}},"supplemental":{"numbers":{"numberingSystems":{"adlm":{"_digits":"𞥐𞥑𞥒𞥓𞥔𞥕𞥖𞥗𞥘𞥙","_type":"numeric"},"ahom":{"_digits":"𑜰𑜱𑜲𑜳𑜴𑜵𑜶𑜷𑜸𑜹","_type":"numeric"},"arab":{"_digits":"٠١٢٣٤٥٦٧٨٩","_type":"numeric"},"arabext":{"_digits":"۰۱۲۳۴۵۶۷۸۹","_type":"numeric"},"armn":{"_rules":"armenian-upper","_type":"algorithmic"},"armnlow":{"_rules":"armenian-lower","_type":"algorithmic"},"bali":{"_digits":"᭐᭑᭒᭓᭔᭕᭖᭗᭘᭙","_type":"numeric"},"beng":{"_digits":"০১২৩৪৫৬৭৮৯","_type":"numeric"},"bhks":{"_digits":"𑱐𑱑𑱒𑱓𑱔𑱕𑱖𑱗𑱘𑱙","_type":"numeric"},"brah":{"_digits":"𑁦𑁧𑁨𑁩𑁪𑁫𑁬𑁭𑁮𑁯","_type":"numeric"},"cakm":{"_digits":"𑄶𑄷𑄸𑄹𑄺𑄻𑄼𑄽𑄾𑄿","_type":"numeric"},"cham":{"_digits":"꩐꩑꩒꩓꩔꩕꩖꩗꩘꩙","_type":"numeric"},"cyrl":{"_rules":"cyrillic-lower","_type":"algorithmic"},"deva":{"_digits":"०१२३४५६७८९","_type":"numeric"},"ethi":{"_rules":"ethiopic","_type":"algorithmic"},"fullwide":{"_digits":"０１２３４５６７８９","_type":"numeric"},"geor":{"_rules":"georgian","_type":"algorithmic"},"gonm":{"_digits":"𑵐𑵑𑵒𑵓𑵔𑵕𑵖𑵗𑵘𑵙","_type":"numeric"},"grek":{"_rules":"greek-upper","_type":"algorithmic"},"greklow":{"_rules":"greek-lower","_type":"algorithmic"},"gujr":{"_digits":"૦૧૨૩૪૫૬૭૮૯","_type":"numeric"},"guru":{"_digits":"੦੧੨੩੪੫੬੭੮੯","_type":"numeric"},"hanidays":{"_rules":"zh/SpelloutRules/spellout-numbering-days","_type":"algorithmic"},"hanidec":{"_digits":"〇一二三四五六七八九","_type":"numeric"},"hans":{"_rules":"zh/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"hansfin":{"_rules":"zh/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"hant":{"_rules":"zh_Hant/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"hantfin":{"_rules":"zh_Hant/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"hebr":{"_rules":"hebrew","_type":"algorithmic"},"hmng":{"_digits":"𖭐𖭑𖭒𖭓𖭔𖭕𖭖𖭗𖭘𖭙","_type":"numeric"},"java":{"_digits":"꧐꧑꧒꧓꧔꧕꧖꧗꧘꧙","_type":"numeric"},"jpan":{"_rules":"ja/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"jpanfin":{"_rules":"ja/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"kali":{"_digits":"꤀꤁꤂꤃꤄꤅꤆꤇꤈꤉","_type":"numeric"},"khmr":{"_digits":"០១២៣៤៥៦៧៨៩","_type":"numeric"},"knda":{"_digits":"೦೧೨೩೪೫೬೭೮೯","_type":"numeric"},"lana":{"_digits":"᪀᪁᪂᪃᪄᪅᪆᪇᪈᪉","_type":"numeric"},"lanatham":{"_digits":"᪐᪑᪒᪓᪔᪕᪖᪗᪘᪙","_type":"numeric"},"laoo":{"_digits":"໐໑໒໓໔໕໖໗໘໙","_type":"numeric"},"latn":{"_digits":"0123456789","_type":"numeric"},"lepc":{"_digits":"᱀᱁᱂᱃᱄᱅᱆᱇᱈᱉","_type":"numeric"},"limb":{"_digits":"᥆᥇᥈᥉᥊᥋᥌᥍᥎᥏","_type":"numeric"},"mathbold":{"_digits":"𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗","_type":"numeric"},"mathdbl":{"_digits":"𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡","_type":"numeric"},"mathmono":{"_digits":"𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿","_type":"numeric"},"mathsanb":{"_digits":"𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵","_type":"numeric"},"mathsans":{"_digits":"𝟢𝟣𝟤𝟥𝟦𝟧𝟨𝟩𝟪𝟫","_type":"numeric"},"mlym":{"_digits":"൦൧൨൩൪൫൬൭൮൯","_type":"numeric"},"modi":{"_digits":"𑙐𑙑𑙒𑙓𑙔𑙕𑙖𑙗𑙘𑙙","_type":"numeric"},"mong":{"_digits":"᠐᠑᠒᠓᠔᠕᠖᠗᠘᠙","_type":"numeric"},"mroo":{"_digits":"𖩠𖩡𖩢𖩣𖩤𖩥𖩦𖩧𖩨𖩩","_type":"numeric"},"mtei":{"_digits":"꯰꯱꯲꯳꯴꯵꯶꯷꯸꯹","_type":"numeric"},"mymr":{"_digits":"၀၁၂၃၄၅၆၇၈၉","_type":"numeric"},"mymrshan":{"_digits":"႐႑႒႓႔႕႖႗႘႙","_type":"numeric"},"mymrtlng":{"_digits":"꧰꧱꧲꧳꧴꧵꧶꧷꧸꧹","_type":"numeric"},"newa":{"_digits":"𑑐𑑑𑑒𑑓𑑔𑑕𑑖𑑗𑑘𑑙","_type":"numeric"},"nkoo":{"_digits":"߀߁߂߃߄߅߆߇߈߉","_type":"numeric"},"olck":{"_digits":"᱐᱑᱒᱓᱔᱕᱖᱗᱘᱙","_type":"numeric"},"orya":{"_digits":"୦୧୨୩୪୫୬୭୮୯","_type":"numeric"},"osma":{"_digits":"𐒠𐒡𐒢𐒣𐒤𐒥𐒦𐒧𐒨𐒩","_type":"numeric"},"roman":{"_rules":"roman-upper","_type":"algorithmic"},"romanlow":{"_rules":"roman-lower","_type":"algorithmic"},"saur":{"_digits":"꣐꣑꣒꣓꣔꣕꣖꣗꣘꣙","_type":"numeric"},"shrd":{"_digits":"𑇐𑇑𑇒𑇓𑇔𑇕𑇖𑇗𑇘𑇙","_type":"numeric"},"sind":{"_digits":"𑋰𑋱𑋲𑋳𑋴𑋵𑋶𑋷𑋸𑋹","_type":"numeric"},"sinh":{"_digits":"෦෧෨෩෪෫෬෭෮෯","_type":"numeric"},"sora":{"_digits":"𑃰𑃱𑃲𑃳𑃴𑃵𑃶𑃷𑃸𑃹","_type":"numeric"},"sund":{"_digits":"᮰᮱᮲᮳᮴᮵᮶᮷᮸᮹","_type":"numeric"},"takr":{"_digits":"𑛀𑛁𑛂𑛃𑛄𑛅𑛆𑛇𑛈𑛉","_type":"numeric"},"talu":{"_digits":"᧐᧑᧒᧓᧔᧕᧖᧗᧘᧙","_type":"numeric"},"taml":{"_rules":"tamil","_type":"algorithmic"},"tamldec":{"_digits":"௦௧௨௩௪௫௬௭௮௯","_type":"numeric"},"telu":{"_digits":"౦౧౨౩౪౫౬౭౮౯","_type":"numeric"},"thai":{"_digits":"๐๑๒๓๔๕๖๗๘๙","_type":"numeric"},"tibt":{"_digits":"༠༡༢༣༤༥༦༧༨༩","_type":"numeric"},"tirh":{"_digits":"𑓐𑓑𑓒𑓓𑓔𑓕𑓖𑓗𑓘𑓙","_type":"numeric"},"vaii":{"_digits":"꘠꘡꘢꘣꘤꘥꘦꘧꘨꘩","_type":"numeric"},"wara":{"_digits":"𑣠𑣡𑣢𑣣𑣤𑣥𑣦𑣧𑣨𑣩","_type":"numeric"}}}}}}}`},
		{lang: "zh-Hans", reg: "CN", scope: "measurements", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh-Hans","language":"zh-Hans","region":"CN","categories":{"measurements":{"durationUnit-type-hm":{"durationUnitPattern":"h:mm"},"durationUnit-type-hms":{"durationUnitPattern":"h:mm:ss"},"durationUnit-type-ms":{"durationUnitPattern":"m:ss"},"long":{"acceleration-g-force":{"displayName":"G力","unitPattern-count-other":"{0}G力"},"acceleration-meter-per-second-squared":{"displayName":"米/秒²","unitPattern-count-other":"每平方秒{0}米"},"angle-arc-minute":{"displayName":"弧分","unitPattern-count-other":"{0}弧分"},"angle-arc-second":{"displayName":"弧秒","unitPattern-count-other":"{0}弧秒"},"angle-degree":{"displayName":"度","unitPattern-count-other":"{0}度"},"angle-radian":{"displayName":"弧度","unitPattern-count-other":"{0}弧度"},"angle-revolution":{"displayName":"转","unitPattern-count-other":"{0}转"},"area-acre":{"displayName":"英亩","unitPattern-count-other":"{0}英亩"},"area-hectare":{"displayName":"公顷","unitPattern-count-other":"{0}公顷"},"area-square-centimeter":{"displayName":"平方厘米","perUnitPattern":"每平方厘米{0}","unitPattern-count-other":"{0}平方厘米"},"area-square-foot":{"displayName":"平方英尺","unitPattern-count-other":"{0}平方英尺"},"area-square-inch":{"displayName":"平方英寸","perUnitPattern":"每平方英寸{0}","unitPattern-count-other":"{0}平方英寸"},"area-square-kilometer":{"displayName":"平方公里","perUnitPattern":"每平方公里{0}","unitPattern-count-other":"{0}平方公里"},"area-square-meter":{"displayName":"平方米","perUnitPattern":"每平方米{0}","unitPattern-count-other":"{0}平方米"},"area-square-mile":{"displayName":"平方英里","perUnitPattern":"每平方英里{0}","unitPattern-count-other":"{0}平方英里"},"area-square-yard":{"displayName":"平方码","unitPattern-count-other":"{0}平方码"},"concentr-karat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"concentr-milligram-per-deciliter":{"displayName":"毫克/分升","unitPattern-count-other":"每分升{0}毫克"},"concentr-millimole-per-liter":{"displayName":"毫摩尔/升","unitPattern-count-other":"每升{0}毫摩尔"},"concentr-part-per-million":{"displayName":"ppm","unitPattern-count-other":"百万分之{0}"},"consumption-liter-per-100kilometers":{"displayName":"升/100千米","unitPattern-count-other":"{0}升/100千米"},"consumption-liter-per-kilometer":{"displayName":"升/公里","unitPattern-count-other":"每公里{0}升"},"consumption-mile-per-gallon":{"displayName":"英里/加仑","unitPattern-count-other":"每加仑{0}英里"},"consumption-mile-per-gallon-imperial":{"displayName":"英里/英制加仑","unitPattern-count-other":"每英制加仑{0}英里"},"coordinateUnit":{"east":"东经{0}","north":"北纬{0}","south":"南纬{0}","west":"西经{0}"},"digital-bit":{"displayName":"比特","unitPattern-count-other":"{0}比特"},"digital-byte":{"displayName":"字节","unitPattern-count-other":"{0}字节"},"digital-gigabit":{"displayName":"吉比特","unitPattern-count-other":"{0}吉比特"},"digital-gigabyte":{"displayName":"吉字节","unitPattern-count-other":"{0}吉字节"},"digital-kilobit":{"displayName":"千比特","unitPattern-count-other":"{0}千比特"},"digital-kilobyte":{"displayName":"千字节","unitPattern-count-other":"{0}千字节"},"digital-megabit":{"displayName":"兆比特","unitPattern-count-other":"{0}兆比特"},"digital-megabyte":{"displayName":"兆字节","unitPattern-count-other":"{0}兆字节"},"digital-terabit":{"displayName":"太比特","unitPattern-count-other":"{0}太比特"},"digital-terabyte":{"displayName":"太字节","unitPattern-count-other":"{0}太字节"},"duration-century":{"displayName":"个世纪","unitPattern-count-other":"{0}个世纪"},"duration-day":{"displayName":"天","perUnitPattern":"每天{0}","unitPattern-count-other":"{0}天"},"duration-hour":{"displayName":"小时","perUnitPattern":"每小时{0}","unitPattern-count-other":"{0}小时"},"duration-microsecond":{"displayName":"微秒","unitPattern-count-other":"{0}微秒"},"duration-millisecond":{"displayName":"毫秒","unitPattern-count-other":"{0}毫秒"},"duration-minute":{"displayName":"分钟","perUnitPattern":"每分钟{0}","unitPattern-count-other":"{0}分钟"},"duration-month":{"displayName":"个月","perUnitPattern":"每月{0}","unitPattern-count-other":"{0}个月"},"duration-nanosecond":{"displayName":"纳秒","unitPattern-count-other":"{0}纳秒"},"duration-second":{"displayName":"秒钟","perUnitPattern":"{0}/秒","unitPattern-count-other":"{0}秒钟"},"duration-week":{"displayName":"周","perUnitPattern":"每周{0}","unitPattern-count-other":"{0}周"},"duration-year":{"displayName":"年","perUnitPattern":"每年{0}","unitPattern-count-other":"{0}年"},"electric-ampere":{"displayName":"安培","unitPattern-count-other":"{0}安培"},"electric-milliampere":{"displayName":"毫安","unitPattern-count-other":"{0}毫安"},"electric-ohm":{"displayName":"欧姆","unitPattern-count-other":"{0}欧姆"},"electric-volt":{"displayName":"伏特","unitPattern-count-other":"{0}伏特"},"energy-calorie":{"displayName":"卡路里","unitPattern-count-other":"{0}卡路里"},"energy-foodcalorie":{"displayName":"卡路里","unitPattern-count-other":"{0}卡路里"},"energy-joule":{"displayName":"焦耳","unitPattern-count-other":"{0}焦耳"},"energy-kilocalorie":{"displayName":"千卡","unitPattern-count-other":"{0}千卡"},"energy-kilojoule":{"displayName":"千焦","unitPattern-count-other":"{0}千焦"},"energy-kilowatt-hour":{"displayName":"千瓦时","unitPattern-count-other":"{0}千瓦时"},"frequency-gigahertz":{"displayName":"吉赫","unitPattern-count-other":"{0}吉赫"},"frequency-hertz":{"displayName":"赫兹","unitPattern-count-other":"{0}赫兹"},"frequency-kilohertz":{"displayName":"千赫","unitPattern-count-other":"{0}千赫"},"frequency-megahertz":{"displayName":"兆赫","unitPattern-count-other":"{0}兆赫"},"length-astronomical-unit":{"displayName":"天文单位","unitPattern-count-other":"{0}天文单位"},"length-centimeter":{"displayName":"厘米","perUnitPattern":"每厘米{0}","unitPattern-count-other":"{0}厘米"},"length-decimeter":{"displayName":"分米","unitPattern-count-other":"{0}分米"},"length-fathom":{"displayName":"英寻","unitPattern-count-other":"{0}英寻"},"length-foot":{"displayName":"英尺","perUnitPattern":"每英尺{0}","unitPattern-count-other":"{0}英尺"},"length-furlong":{"displayName":"弗隆","unitPattern-count-other":"{0}弗隆"},"length-inch":{"displayName":"英寸","perUnitPattern":"每英寸{0}","unitPattern-count-other":"{0}英寸"},"length-kilometer":{"displayName":"公里","perUnitPattern":"每公里{0}","unitPattern-count-other":"{0}公里"},"length-light-year":{"displayName":"光年","unitPattern-count-other":"{0}光年"},"length-meter":{"displayName":"米","perUnitPattern":"每米{0}","unitPattern-count-other":"{0}米"},"length-micrometer":{"displayName":"微米","unitPattern-count-other":"{0}微米"},"length-mile":{"displayName":"英里","unitPattern-count-other":"{0}英里"},"length-mile-scandinavian":{"displayName":"斯堪的纳维亚英里","unitPattern-count-other":"{0}斯堪的纳维亚英里"},"length-millimeter":{"displayName":"毫米","unitPattern-count-other":"{0}毫米"},"length-nanometer":{"displayName":"纳米","unitPattern-count-other":"{0}纳米"},"length-nautical-mile":{"displayName":"海里","unitPattern-count-other":"{0}海里"},"length-parsec":{"displayName":"秒差距","unitPattern-count-other":"{0}秒差距"},"length-picometer":{"displayName":"皮米","unitPattern-count-other":"{0}皮米"},"length-point":{"displayName":"pt","unitPattern-count-other":"{0} pt"},"length-yard":{"displayName":"码","unitPattern-count-other":"{0}码"},"light-lux":{"displayName":"勒克斯","unitPattern-count-other":"{0}勒克斯"},"mass-carat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"mass-gram":{"displayName":"克","perUnitPattern":"每克{0}","unitPattern-count-other":"{0}克"},"mass-kilogram":{"displayName":"千克","perUnitPattern":"每千克{0}","unitPattern-count-other":"{0}千克"},"mass-metric-ton":{"displayName":"公吨","unitPattern-count-other":"{0}公吨"},"mass-microgram":{"displayName":"微克","unitPattern-count-other":"{0}微克"},"mass-milligram":{"displayName":"毫克","unitPattern-count-other":"{0}毫克"},"mass-ounce":{"displayName":"盎司","perUnitPattern":"每盎司{0}","unitPattern-count-other":"{0}盎司"},"mass-ounce-troy":{"displayName":"金衡制盎司","unitPattern-count-other":"{0}金衡制盎司"},"mass-pound":{"displayName":"磅","perUnitPattern":"每磅{0}","unitPattern-count-other":"{0}磅"},"mass-stone":{"displayName":"英石","unitPattern-count-other":"{0}英石"},"mass-ton":{"displayName":"吨","unitPattern-count-other":"{0}吨"},"per":{"compoundUnitPattern":"每{1}{0}"},"power-gigawatt":{"displayName":"吉瓦","unitPattern-count-other":"{0}吉瓦"},"power-horsepower":{"displayName":"马力","unitPattern-count-other":"{0}马力"},"power-kilowatt":{"displayName":"千瓦","unitPattern-count-other":"{0}千瓦"},"power-megawatt":{"displayName":"兆瓦","unitPattern-count-other":"{0}兆瓦"},"power-milliwatt":{"displayName":"毫瓦","unitPattern-count-other":"{0}毫瓦"},"power-watt":{"displayName":"瓦特","unitPattern-count-other":"{0}瓦特"},"pressure-hectopascal":{"displayName":"百帕斯卡","unitPattern-count-other":"{0}百帕斯卡"},"pressure-inch-hg":{"displayName":"英寸汞柱","unitPattern-count-other":"{0}英寸汞柱"},"pressure-millibar":{"displayName":"毫巴","unitPattern-count-other":"{0}毫巴"},"pressure-millimeter-of-mercury":{"displayName":"毫米汞柱","unitPattern-count-other":"{0}毫米汞柱"},"pressure-pound-per-square-inch":{"displayName":"磅/平方英寸","unitPattern-count-other":"每平方英寸{0}磅"},"speed-kilometer-per-hour":{"displayName":"公里/小时","unitPattern-count-other":"每小时{0}公里"},"speed-knot":{"displayName":"节","unitPattern-count-other":"{0}节"},"speed-meter-per-second":{"displayName":"米/秒","unitPattern-count-other":"每秒{0}米"},"speed-mile-per-hour":{"displayName":"英里/小时","unitPattern-count-other":"每小时{0}英里"},"temperature-celsius":{"displayName":"摄氏度","unitPattern-count-other":"{0}摄氏度"},"temperature-fahrenheit":{"displayName":"华氏度","unitPattern-count-other":"{0}华氏度"},"temperature-generic":{"displayName":"°","unitPattern-count-other":"{0}°"},"temperature-kelvin":{"displayName":"开尔文","unitPattern-count-other":"{0}开尔文"},"volume-acre-foot":{"displayName":"英亩英尺","unitPattern-count-other":"{0}英亩英尺"},"volume-bushel":{"displayName":"蒲式耳","unitPattern-count-other":"{0}蒲式耳"},"volume-centiliter":{"displayName":"厘升","unitPattern-count-other":"{0}厘升"},"volume-cubic-centimeter":{"displayName":"立方厘米","perUnitPattern":"每立方厘米{0}","unitPattern-count-other":"{0}立方厘米"},"volume-cubic-foot":{"displayName":"立方英尺","unitPattern-count-other":"{0}立方英尺"},"volume-cubic-inch":{"displayName":"立方英寸","unitPattern-count-other":"{0}立方英寸"},"volume-cubic-kilometer":{"displayName":"立方千米","unitPattern-count-other":"{0}立方千米"},"volume-cubic-meter":{"displayName":"立方米","perUnitPattern":"每立方米{0}","unitPattern-count-other":"{0}立方米"},"volume-cubic-mile":{"displayName":"立方英里","unitPattern-count-other":"{0}立方英里"},"volume-cubic-yard":{"displayName":"立方码","unitPattern-count-other":"{0}立方码"},"volume-cup":{"displayName":"杯","unitPattern-count-other":"{0}杯"},"volume-cup-metric":{"displayName":"公制杯","unitPattern-count-other":"{0}公制杯"},"volume-deciliter":{"displayName":"分升","unitPattern-count-other":"{0}分升"},"volume-fluid-ounce":{"displayName":"液盎司","unitPattern-count-other":"{0}液盎司"},"volume-gallon":{"displayName":"加仑","perUnitPattern":"每加仑{0}","unitPattern-count-other":"{0}加仑"},"volume-gallon-imperial":{"displayName":"英制加仑","perUnitPattern":"每英制加仑{0}","unitPattern-count-other":"{0}英制加仑"},"volume-hectoliter":{"displayName":"公石","unitPattern-count-other":"{0}公石"},"volume-liter":{"displayName":"升","perUnitPattern":"每升{0}","unitPattern-count-other":"{0}升"},"volume-megaliter":{"displayName":"兆升","unitPattern-count-other":"{0}兆升"},"volume-milliliter":{"displayName":"毫升","unitPattern-count-other":"{0}毫升"},"volume-pint":{"displayName":"品脱","unitPattern-count-other":"{0}品脱"},"volume-pint-metric":{"displayName":"公制品脱","unitPattern-count-other":"{0}公制品脱"},"volume-quart":{"displayName":"夸脱","unitPattern-count-other":"{0}夸脱"},"volume-tablespoon":{"displayName":"汤匙","unitPattern-count-other":"{0}汤匙"},"volume-teaspoon":{"displayName":"茶匙","unitPattern-count-other":"{0}茶匙"}},"narrow":{"acceleration-g-force":{"displayName":"G力","unitPattern-count-other":"{0}G"},"acceleration-meter-per-second-squared":{"displayName":"米/秒²","unitPattern-count-other":"{0}米/秒²"},"angle-arc-minute":{"displayName":"弧分","unitPattern-count-other":"{0}′"},"angle-arc-second":{"displayName":"弧秒","unitPattern-count-other":"{0}″"},"angle-degree":{"displayName":"度","unitPattern-count-other":"{0}°"},"angle-radian":{"displayName":"弧度","unitPattern-count-other":"{0}弧度"},"angle-revolution":{"displayName":"转","unitPattern-count-other":"{0}转"},"area-acre":{"displayName":"英亩","unitPattern-count-other":"{0}ac"},"area-hectare":{"displayName":"公顷","unitPattern-count-other":"{0}ha"},"area-square-centimeter":{"displayName":"平方厘米","perUnitPattern":"{0}/平方厘米","unitPattern-count-other":"{0}平方厘米"},"area-square-foot":{"displayName":"平方英尺","unitPattern-count-other":"{0}ft²"},"area-square-inch":{"displayName":"平方英寸","perUnitPattern":"{0}/平方英寸","unitPattern-count-other":"{0}平方英寸"},"area-square-kilometer":{"displayName":"平方公里","perUnitPattern":"{0}/平方公里","unitPattern-count-other":"{0}km²"},"area-square-meter":{"displayName":"平方米","perUnitPattern":"{0}/平方米","unitPattern-count-other":"{0}m²"},"area-square-mile":{"displayName":"平方英里","perUnitPattern":"{0}/平方英里","unitPattern-count-other":"{0}mi²"},"area-square-yard":{"displayName":"平方码","unitPattern-count-other":"{0}平方码"},"concentr-karat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"concentr-milligram-per-deciliter":{"displayName":"毫克/分升","unitPattern-count-other":"{0}毫克/分升"},"concentr-millimole-per-liter":{"displayName":"毫摩尔/升","unitPattern-count-other":"{0}毫摩尔/升"},"concentr-part-per-million":{"displayName":"ppm","unitPattern-count-other":"{0}ppm"},"consumption-liter-per-100kilometers":{"displayName":"升/100千米","unitPattern-count-other":"{0}L/100km"},"consumption-liter-per-kilometer":{"displayName":"升/公里","unitPattern-count-other":"{0}升/公里"},"consumption-mile-per-gallon":{"displayName":"英里/加仑","unitPattern-count-other":"{0}英里/加仑"},"consumption-mile-per-gallon-imperial":{"displayName":"英里/英制加仑","unitPattern-count-other":"{0}英里/英制加仑"},"coordinateUnit":{"east":"{0}E","north":"{0}N","south":"{0}S","west":"{0}W"},"digital-bit":{"displayName":"比特","unitPattern-count-other":"{0}比特"},"digital-byte":{"displayName":"字节","unitPattern-count-other":"{0}字节"},"digital-gigabit":{"displayName":"吉比特","unitPattern-count-other":"{0}吉比特"},"digital-gigabyte":{"displayName":"吉字节","unitPattern-count-other":"{0}吉字节"},"digital-kilobit":{"displayName":"千比特","unitPattern-count-other":"{0}千比特"},"digital-kilobyte":{"displayName":"千字节","unitPattern-count-other":"{0}千字节"},"digital-megabit":{"displayName":"兆比特","unitPattern-count-other":"{0}兆比特"},"digital-megabyte":{"displayName":"兆字节","unitPattern-count-other":"{0}兆字节"},"digital-terabit":{"displayName":"太比特","unitPattern-count-other":"{0}太比特"},"digital-terabyte":{"displayName":"太字节","unitPattern-count-other":"{0}太字节"},"duration-century":{"displayName":"世纪","unitPattern-count-other":"{0}个世纪"},"duration-day":{"displayName":"天","perUnitPattern":"{0}/天","unitPattern-count-other":"{0}天"},"duration-hour":{"displayName":"小时","perUnitPattern":"{0}/小时","unitPattern-count-other":"{0}小时"},"duration-microsecond":{"displayName":"微秒","unitPattern-count-other":"{0}微秒"},"duration-millisecond":{"displayName":"毫秒","unitPattern-count-other":"{0}毫秒"},"duration-minute":{"displayName":"分钟","perUnitPattern":"{0}/分钟","unitPattern-count-other":"{0}分钟"},"duration-month":{"displayName":"个月","perUnitPattern":"{0}/月","unitPattern-count-other":"{0}个月"},"duration-nanosecond":{"displayName":"纳秒","unitPattern-count-other":"{0}纳秒"},"duration-second":{"displayName":"秒","perUnitPattern":"{0}/秒","unitPattern-count-other":"{0}秒"},"duration-week":{"displayName":"周","perUnitPattern":"{0}/周","unitPattern-count-other":"{0}周"},"duration-year":{"displayName":"年","perUnitPattern":"{0}/年","unitPattern-count-other":"{0}年"},"electric-ampere":{"displayName":"安培","unitPattern-count-other":"{0}安"},"electric-milliampere":{"displayName":"毫安","unitPattern-count-other":"{0}毫安"},"electric-ohm":{"displayName":"欧姆","unitPattern-count-other":"{0}欧"},"electric-volt":{"displayName":"伏特","unitPattern-count-other":"{0}伏"},"energy-calorie":{"displayName":"卡","unitPattern-count-other":"{0}卡"},"energy-foodcalorie":{"displayName":"卡","unitPattern-count-other":"{0}卡"},"energy-joule":{"displayName":"焦耳","unitPattern-count-other":"{0}焦耳"},"energy-kilocalorie":{"displayName":"千卡","unitPattern-count-other":"{0}千卡"},"energy-kilojoule":{"displayName":"千焦","unitPattern-count-other":"{0}千焦"},"energy-kilowatt-hour":{"displayName":"千瓦时","unitPattern-count-other":"{0}千瓦时"},"frequency-gigahertz":{"displayName":"吉赫","unitPattern-count-other":"{0}吉赫"},"frequency-hertz":{"displayName":"赫兹","unitPattern-count-other":"{0}赫"},"frequency-kilohertz":{"displayName":"千赫","unitPattern-count-other":"{0}千赫"},"frequency-megahertz":{"displayName":"兆赫","unitPattern-count-other":"{0}兆赫"},"length-astronomical-unit":{"displayName":"天文单位","unitPattern-count-other":"{0}天文单位"},"length-centimeter":{"displayName":"厘米","perUnitPattern":"{0}/厘米","unitPattern-count-other":"{0}厘米"},"length-decimeter":{"displayName":"分米","unitPattern-count-other":"{0}分米"},"length-fathom":{"displayName":"英寻","unitPattern-count-other":"{0}英寻"},"length-foot":{"displayName":"英尺","perUnitPattern":"{0}/英尺","unitPattern-count-other":"{0}′"},"length-furlong":{"displayName":"弗隆","unitPattern-count-other":"{0}弗隆"},"length-inch":{"displayName":"英寸","perUnitPattern":"{0}/英寸","unitPattern-count-other":"{0}″"},"length-kilometer":{"displayName":"公里","perUnitPattern":"{0}/公里","unitPattern-count-other":"{0}公里"},"length-light-year":{"displayName":"光年","unitPattern-count-other":"{0}ly"},"length-meter":{"displayName":"米","perUnitPattern":"{0}/米","unitPattern-count-other":"{0}米"},"length-micrometer":{"displayName":"微米","unitPattern-count-other":"{0}微米"},"length-mile":{"displayName":"英里","unitPattern-count-other":"{0}mi"},"length-mile-scandinavian":{"displayName":"斯堪的纳维亚英里","unitPattern-count-other":"{0}斯堪的纳维亚英里"},"length-millimeter":{"displayName":"毫米","unitPattern-count-other":"{0}毫米"},"length-nanometer":{"displayName":"纳米","unitPattern-count-other":"{0}纳米"},"length-nautical-mile":{"displayName":"海里","unitPattern-count-other":"{0}海里"},"length-parsec":{"displayName":"秒差距","unitPattern-count-other":"{0}秒差距"},"length-picometer":{"displayName":"皮米","unitPattern-count-other":"{0}pm"},"length-point":{"displayName":"pt","unitPattern-count-other":"{0} pt"},"length-yard":{"displayName":"码","unitPattern-count-other":"{0}yd"},"light-lux":{"displayName":"勒克斯","unitPattern-count-other":"{0}勒克斯"},"mass-carat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"mass-gram":{"displayName":"克","perUnitPattern":"{0}/克","unitPattern-count-other":"{0}克"},"mass-kilogram":{"displayName":"千克","perUnitPattern":"{0}/千克","unitPattern-count-other":"{0}千克"},"mass-metric-ton":{"displayName":"公吨","unitPattern-count-other":"{0}公吨"},"mass-microgram":{"displayName":"微克","unitPattern-count-other":"{0}微克"},"mass-milligram":{"displayName":"毫克","unitPattern-count-other":"{0}毫克"},"mass-ounce":{"displayName":"盎司","perUnitPattern":"{0}/盎司","unitPattern-count-other":"{0}盎司"},"mass-ounce-troy":{"displayName":"金衡盎司","unitPattern-count-other":"{0}金衡盎司"},"mass-pound":{"displayName":"磅","perUnitPattern":"{0}/磅","unitPattern-count-other":"{0}磅"},"mass-stone":{"displayName":"英石","unitPattern-count-other":"{0}英石"},"mass-ton":{"displayName":"吨","unitPattern-count-other":"{0}吨"},"per":{"compoundUnitPattern":"{0}/{1}"},"power-gigawatt":{"displayName":"吉瓦","unitPattern-count-other":"{0}吉瓦"},"power-horsepower":{"displayName":"马力","unitPattern-count-other":"{0}hp"},"power-kilowatt":{"displayName":"千瓦","unitPattern-count-other":"{0}kW"},"power-megawatt":{"displayName":"兆瓦","unitPattern-count-other":"{0}兆瓦"},"power-milliwatt":{"displayName":"毫瓦","unitPattern-count-other":"{0}毫瓦"},"power-watt":{"displayName":"瓦特","unitPattern-count-other":"{0}W"},"pressure-hectopascal":{"displayName":"百帕","unitPattern-count-other":"{0}hPa"},"pressure-inch-hg":{"displayName":"英寸汞柱","unitPattern-count-other":"{0}\\\" Hg"},"pressure-millibar":{"displayName":"毫巴","unitPattern-count-other":"{0}mb"},"pressure-millimeter-of-mercury":{"displayName":"毫米汞柱","unitPattern-count-other":"{0}毫米汞柱"},"pressure-pound-per-square-inch":{"displayName":"磅/平方英寸","unitPattern-count-other":"每平方英寸{0}磅"},"speed-kilometer-per-hour":{"displayName":"公里/小时","unitPattern-count-other":"{0}公里/小时"},"speed-knot":{"displayName":"节","unitPattern-count-other":"{0}节"},"speed-meter-per-second":{"displayName":"米/秒","unitPattern-count-other":"{0}m/s"},"speed-mile-per-hour":{"displayName":"英里/小时","unitPattern-count-other":"{0}mi/h"},"temperature-celsius":{"displayName":"°C","unitPattern-count-other":"{0}°C"},"temperature-fahrenheit":{"displayName":"华氏度","unitPattern-count-other":"{0}°F"},"temperature-generic":{"displayName":"°","unitPattern-count-other":"{0}°"},"temperature-kelvin":{"displayName":"开","unitPattern-count-other":"{0}K"},"volume-acre-foot":{"displayName":"英亩英尺","unitPattern-count-other":"{0}英亩英尺"},"volume-bushel":{"displayName":"蒲式耳","unitPattern-count-other":"{0}蒲式耳"},"volume-centiliter":{"displayName":"厘升","unitPattern-count-other":"{0}厘升"},"volume-cubic-centimeter":{"displayName":"立方厘米","perUnitPattern":"{0}/立方厘米","unitPattern-count-other":"{0}立方厘米"},"volume-cubic-foot":{"displayName":"立方英尺","unitPattern-count-other":"{0}立方英尺"},"volume-cubic-inch":{"displayName":"立方英寸","unitPattern-count-other":"{0}立方英寸"},"volume-cubic-kilometer":{"displayName":"立方千米","unitPattern-count-other":"{0}km³"},"volume-cubic-meter":{"displayName":"立方米","perUnitPattern":"{0}/立方米","unitPattern-count-other":"{0}立方米"},"volume-cubic-mile":{"displayName":"立方英里","unitPattern-count-other":"{0}mi³"},"volume-cubic-yard":{"displayName":"立方码","unitPattern-count-other":"{0}立方码"},"volume-cup":{"displayName":"杯","unitPattern-count-other":"{0}杯"},"volume-cup-metric":{"displayName":"公制杯","unitPattern-count-other":"{0}公制杯"},"volume-deciliter":{"displayName":"分升","unitPattern-count-other":"{0}分升"},"volume-fluid-ounce":{"displayName":"液盎司","unitPattern-count-other":"{0}液盎司"},"volume-gallon":{"displayName":"加仑","perUnitPattern":"{0}/加仑","unitPattern-count-other":"{0}加仑"},"volume-gallon-imperial":{"displayName":"英制加仑","perUnitPattern":"{0}/英制加仑","unitPattern-count-other":"{0}英制加仑"},"volume-hectoliter":{"displayName":"公石","unitPattern-count-other":"{0}公石"},"volume-liter":{"displayName":"升","perUnitPattern":"{0}/升","unitPattern-count-other":"{0}升"},"volume-megaliter":{"displayName":"兆升","unitPattern-count-other":"{0}兆升"},"volume-milliliter":{"displayName":"毫升","unitPattern-count-other":"{0}毫升"},"volume-pint":{"displayName":"品脱","unitPattern-count-other":"{0}品脱"},"volume-pint-metric":{"displayName":"公制品脱","unitPattern-count-other":"{0}公制品脱"},"volume-quart":{"displayName":"夸脱","unitPattern-count-other":"{0}夸脱"},"volume-tablespoon":{"displayName":"汤匙","unitPattern-count-other":"{0}汤匙"},"volume-teaspoon":{"displayName":"茶匙","unitPattern-count-other":"{0}茶匙"}},"short":{"acceleration-g-force":{"displayName":"G力","unitPattern-count-other":"{0}G"},"acceleration-meter-per-second-squared":{"displayName":"米/秒²","unitPattern-count-other":"{0}米/秒²"},"angle-arc-minute":{"displayName":"弧分","unitPattern-count-other":"{0}弧分"},"angle-arc-second":{"displayName":"弧秒","unitPattern-count-other":"{0}弧秒"},"angle-degree":{"displayName":"度","unitPattern-count-other":"{0}°"},"angle-radian":{"displayName":"弧度","unitPattern-count-other":"{0}弧度"},"angle-revolution":{"displayName":"转","unitPattern-count-other":"{0}转"},"area-acre":{"displayName":"英亩","unitPattern-count-other":"{0}英亩"},"area-hectare":{"displayName":"公顷","unitPattern-count-other":"{0}公顷"},"area-square-centimeter":{"displayName":"平方厘米","perUnitPattern":"{0}/平方厘米","unitPattern-count-other":"{0}平方厘米"},"area-square-foot":{"displayName":"平方英尺","unitPattern-count-other":"{0}平方英尺"},"area-square-inch":{"displayName":"平方英寸","perUnitPattern":"{0}/平方英寸","unitPattern-count-other":"{0}平方英寸"},"area-square-kilometer":{"displayName":"平方公里","perUnitPattern":"{0}/平方公里","unitPattern-count-other":"{0}平方公里"},"area-square-meter":{"displayName":"平方米","perUnitPattern":"{0}/平方米","unitPattern-count-other":"{0}平方米"},"area-square-mile":{"displayName":"平方英里","perUnitPattern":"{0}/平方英里","unitPattern-count-other":"{0}平方英里"},"area-square-yard":{"displayName":"平方码","unitPattern-count-other":"{0}平方码"},"concentr-karat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"concentr-milligram-per-deciliter":{"displayName":"毫克/分升","unitPattern-count-other":"{0}毫克/分升"},"concentr-millimole-per-liter":{"displayName":"毫摩尔/升","unitPattern-count-other":"{0}毫摩尔/升"},"concentr-part-per-million":{"displayName":"ppm","unitPattern-count-other":"{0}ppm"},"consumption-liter-per-100kilometers":{"displayName":"升/100千米","unitPattern-count-other":"{0}升/100千米"},"consumption-liter-per-kilometer":{"displayName":"升/公里","unitPattern-count-other":"{0}升/公里"},"consumption-mile-per-gallon":{"displayName":"英里/加仑","unitPattern-count-other":"{0}英里/加仑"},"consumption-mile-per-gallon-imperial":{"displayName":"英里/英制加仑","unitPattern-count-other":"{0}英里/英制加仑"},"coordinateUnit":{"east":"东经{0}","north":"北纬{0}","south":"南纬{0}","west":"西经{0}"},"digital-bit":{"displayName":"比特","unitPattern-count-other":"{0}比特"},"digital-byte":{"displayName":"字节","unitPattern-count-other":"{0}字节"},"digital-gigabit":{"displayName":"吉比特","unitPattern-count-other":"{0}吉比特"},"digital-gigabyte":{"displayName":"吉字节","unitPattern-count-other":"{0}吉字节"},"digital-kilobit":{"displayName":"千比特","unitPattern-count-other":"{0}千比特"},"digital-kilobyte":{"displayName":"千字节","unitPattern-count-other":"{0}千字节"},"digital-megabit":{"displayName":"兆比特","unitPattern-count-other":"{0}兆比特"},"digital-megabyte":{"displayName":"兆字节","unitPattern-count-other":"{0}兆字节"},"digital-terabit":{"displayName":"太比特","unitPattern-count-other":"{0}太比特"},"digital-terabyte":{"displayName":"太字节","unitPattern-count-other":"{0}太字节"},"duration-century":{"displayName":"世纪","unitPattern-count-other":"{0}个世纪"},"duration-day":{"displayName":"天","perUnitPattern":"{0}/天","unitPattern-count-other":"{0}天"},"duration-hour":{"displayName":"小时","perUnitPattern":"{0}/小时","unitPattern-count-other":"{0}小时"},"duration-microsecond":{"displayName":"微秒","unitPattern-count-other":"{0}微秒"},"duration-millisecond":{"displayName":"毫秒","unitPattern-count-other":"{0}毫秒"},"duration-minute":{"displayName":"分钟","perUnitPattern":"{0}/分钟","unitPattern-count-other":"{0}分钟"},"duration-month":{"displayName":"个月","perUnitPattern":"{0}/月","unitPattern-count-other":"{0}个月"},"duration-nanosecond":{"displayName":"纳秒","unitPattern-count-other":"{0}纳秒"},"duration-second":{"displayName":"秒","perUnitPattern":"{0}/秒","unitPattern-count-other":"{0}秒"},"duration-week":{"displayName":"周","perUnitPattern":"{0}/周","unitPattern-count-other":"{0}周"},"duration-year":{"displayName":"年","perUnitPattern":"{0}/年","unitPattern-count-other":"{0}年"},"electric-ampere":{"displayName":"安培","unitPattern-count-other":"{0}安"},"electric-milliampere":{"displayName":"毫安","unitPattern-count-other":"{0}毫安"},"electric-ohm":{"displayName":"欧姆","unitPattern-count-other":"{0}欧"},"electric-volt":{"displayName":"伏特","unitPattern-count-other":"{0}伏"},"energy-calorie":{"displayName":"卡","unitPattern-count-other":"{0}卡"},"energy-foodcalorie":{"displayName":"卡","unitPattern-count-other":"{0}卡"},"energy-joule":{"displayName":"焦耳","unitPattern-count-other":"{0}焦耳"},"energy-kilocalorie":{"displayName":"千卡","unitPattern-count-other":"{0}千卡"},"energy-kilojoule":{"displayName":"千焦","unitPattern-count-other":"{0}千焦"},"energy-kilowatt-hour":{"displayName":"千瓦时","unitPattern-count-other":"{0}千瓦时"},"frequency-gigahertz":{"displayName":"吉赫","unitPattern-count-other":"{0}吉赫"},"frequency-hertz":{"displayName":"赫兹","unitPattern-count-other":"{0}赫"},"frequency-kilohertz":{"displayName":"千赫","unitPattern-count-other":"{0}千赫"},"frequency-megahertz":{"displayName":"兆赫","unitPattern-count-other":"{0}兆赫"},"length-astronomical-unit":{"displayName":"天文单位","unitPattern-count-other":"{0}天文单位"},"length-centimeter":{"displayName":"厘米","perUnitPattern":"{0}/厘米","unitPattern-count-other":"{0}厘米"},"length-decimeter":{"displayName":"分米","unitPattern-count-other":"{0}分米"},"length-fathom":{"displayName":"英寻","unitPattern-count-other":"{0}英寻"},"length-foot":{"displayName":"英尺","perUnitPattern":"{0}/英尺","unitPattern-count-other":"{0}英尺"},"length-furlong":{"displayName":"弗隆","unitPattern-count-other":"{0}弗隆"},"length-inch":{"displayName":"英寸","perUnitPattern":"{0}/英寸","unitPattern-count-other":"{0}英寸"},"length-kilometer":{"displayName":"公里","perUnitPattern":"{0}/公里","unitPattern-count-other":"{0}公里"},"length-light-year":{"displayName":"光年","unitPattern-count-other":"{0}光年"},"length-meter":{"displayName":"米","perUnitPattern":"{0}/米","unitPattern-count-other":"{0}米"},"length-micrometer":{"displayName":"微米","unitPattern-count-other":"{0}微米"},"length-mile":{"displayName":"英里","unitPattern-count-other":"{0}英里"},"length-mile-scandinavian":{"displayName":"斯堪的纳维亚英里","unitPattern-count-other":"{0}斯堪的纳维亚英里"},"length-millimeter":{"displayName":"毫米","unitPattern-count-other":"{0}毫米"},"length-nanometer":{"displayName":"纳米","unitPattern-count-other":"{0}纳米"},"length-nautical-mile":{"displayName":"海里","unitPattern-count-other":"{0}海里"},"length-parsec":{"displayName":"秒差距","unitPattern-count-other":"{0}秒差距"},"length-picometer":{"displayName":"皮米","unitPattern-count-other":"{0}皮米"},"length-point":{"displayName":"pt","unitPattern-count-other":"{0} pt"},"length-yard":{"displayName":"码","unitPattern-count-other":"{0}码"},"light-lux":{"displayName":"勒克斯","unitPattern-count-other":"{0}勒克斯"},"mass-carat":{"displayName":"克拉","unitPattern-count-other":"{0}克拉"},"mass-gram":{"displayName":"克","perUnitPattern":"{0}/克","unitPattern-count-other":"{0}克"},"mass-kilogram":{"displayName":"千克","perUnitPattern":"{0}/千克","unitPattern-count-other":"{0}千克"},"mass-metric-ton":{"displayName":"公吨","unitPattern-count-other":"{0}公吨"},"mass-microgram":{"displayName":"微克","unitPattern-count-other":"{0}微克"},"mass-milligram":{"displayName":"毫克","unitPattern-count-other":"{0}毫克"},"mass-ounce":{"displayName":"盎司","perUnitPattern":"{0}/盎司","unitPattern-count-other":"{0}盎司"},"mass-ounce-troy":{"displayName":"金衡盎司","unitPattern-count-other":"{0}金衡盎司"},"mass-pound":{"displayName":"磅","perUnitPattern":"{0}/磅","unitPattern-count-other":"{0}磅"},"mass-stone":{"displayName":"英石","unitPattern-count-other":"{0}英石"},"mass-ton":{"displayName":"吨","unitPattern-count-other":"{0}吨"},"per":{"compoundUnitPattern":"{0}/{1}"},"power-gigawatt":{"displayName":"吉瓦","unitPattern-count-other":"{0}吉瓦"},"power-horsepower":{"displayName":"马力","unitPattern-count-other":"{0}马力"},"power-kilowatt":{"displayName":"千瓦","unitPattern-count-other":"{0}千瓦"},"power-megawatt":{"displayName":"兆瓦","unitPattern-count-other":"{0}兆瓦"},"power-milliwatt":{"displayName":"毫瓦","unitPattern-count-other":"{0}毫瓦"},"power-watt":{"displayName":"瓦特","unitPattern-count-other":"{0}瓦"},"pressure-hectopascal":{"displayName":"百帕","unitPattern-count-other":"{0}百帕"},"pressure-inch-hg":{"displayName":"英寸汞柱","unitPattern-count-other":"{0}英寸汞柱"},"pressure-millibar":{"displayName":"毫巴","unitPattern-count-other":"{0}毫巴"},"pressure-millimeter-of-mercury":{"displayName":"毫米汞柱","unitPattern-count-other":"{0}毫米汞柱"},"pressure-pound-per-square-inch":{"displayName":"磅/平方英寸","unitPattern-count-other":"每平方英寸{0}磅"},"speed-kilometer-per-hour":{"displayName":"公里/小时","unitPattern-count-other":"每小时{0}公里"},"speed-knot":{"displayName":"节","unitPattern-count-other":"{0}节"},"speed-meter-per-second":{"displayName":"米/秒","unitPattern-count-other":"{0}米/秒"},"speed-mile-per-hour":{"displayName":"英里/小时","unitPattern-count-other":"{0}英里/小时"},"temperature-celsius":{"displayName":"摄氏度","unitPattern-count-other":"{0}°C"},"temperature-fahrenheit":{"displayName":"华氏度","unitPattern-count-other":"{0}°F"},"temperature-generic":{"displayName":"°","unitPattern-count-other":"{0}°"},"temperature-kelvin":{"displayName":"开","unitPattern-count-other":"{0}K"},"volume-acre-foot":{"displayName":"英亩英尺","unitPattern-count-other":"{0}英亩英尺"},"volume-bushel":{"displayName":"蒲式耳","unitPattern-count-other":"{0}蒲式耳"},"volume-centiliter":{"displayName":"厘升","unitPattern-count-other":"{0}厘升"},"volume-cubic-centimeter":{"displayName":"立方厘米","perUnitPattern":"{0}/立方厘米","unitPattern-count-other":"{0}立方厘米"},"volume-cubic-foot":{"displayName":"立方英尺","unitPattern-count-other":"{0}立方英尺"},"volume-cubic-inch":{"displayName":"立方英寸","unitPattern-count-other":"{0}立方英寸"},"volume-cubic-kilometer":{"displayName":"立方千米","unitPattern-count-other":"{0}立方千米"},"volume-cubic-meter":{"displayName":"立方米","perUnitPattern":"{0}/立方米","unitPattern-count-other":"{0}立方米"},"volume-cubic-mile":{"displayName":"立方英里","unitPattern-count-other":"{0}立方英里"},"volume-cubic-yard":{"displayName":"立方码","unitPattern-count-other":"{0}立方码"},"volume-cup":{"displayName":"杯","unitPattern-count-other":"{0}杯"},"volume-cup-metric":{"displayName":"公制杯","unitPattern-count-other":"{0}公制杯"},"volume-deciliter":{"displayName":"分升","unitPattern-count-other":"{0}分升"},"volume-fluid-ounce":{"displayName":"液盎司","unitPattern-count-other":"{0}液盎司"},"volume-gallon":{"displayName":"加仑","perUnitPattern":"{0}/加仑","unitPattern-count-other":"{0}加仑"},"volume-gallon-imperial":{"displayName":"英制加仑","perUnitPattern":"{0}/英制加仑","unitPattern-count-other":"{0}英制加仑"},"volume-hectoliter":{"displayName":"公石","unitPattern-count-other":"{0}公石"},"volume-liter":{"displayName":"升","perUnitPattern":"{0}/升","unitPattern-count-other":"{0}升"},"volume-megaliter":{"displayName":"兆升","unitPattern-count-other":"{0}兆升"},"volume-milliliter":{"displayName":"毫升","unitPattern-count-other":"{0}毫升"},"volume-pint":{"displayName":"品脱","unitPattern-count-other":"{0}品脱"},"volume-pint-metric":{"displayName":"公制品脱","unitPattern-count-other":"{0}公制品脱"},"volume-quart":{"displayName":"夸脱","unitPattern-count-other":"{0}夸脱"},"volume-tablespoon":{"displayName":"汤匙","unitPattern-count-other":"{0}汤匙"},"volume-teaspoon":{"displayName":"茶匙","unitPattern-count-other":"{0}茶匙"}}}}}}`},
		{lang: "es", reg: "US", scope: "currencies", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"es-US","language":"es","region":"US","categories":{"currencies":{"ADP":{"displayName":"peseta andorrana","displayName-count-one":"peseta andorrana","displayName-count-other":"pesetas andorranas","symbol":"ADP"},"AED":{"displayName":"dírham de los Emiratos Árabes Unidos","displayName-count-one":"dírham de los Emiratos Árabes Unidos","displayName-count-other":"dírhams de los Emiratos Árabes Unidos","symbol":"AED"},"AFA":{"displayName":"afgani (1927–2002)","symbol":"AFA"},"AFN":{"displayName":"afgani","displayName-count-one":"afgani","displayName-count-other":"afganis","symbol":"AFN"},"ALK":{"displayName":"ALK","symbol":"ALK"},"ALL":{"displayName":"lek","displayName-count-one":"lek","displayName-count-other":"lekes","symbol":"ALL"},"AMD":{"displayName":"dram armenio","displayName-count-one":"dram","displayName-count-other":"drams","symbol":"AMD"},"ANG":{"displayName":"florín de las Antillas Neerlandesas","displayName-count-one":"florín de las Antillas Neerlandesas","displayName-count-other":"florines de las Antillas Neerlandesas","symbol":"ANG"},"AOA":{"displayName":"kuanza","displayName-count-one":"kuanza","displayName-count-other":"kuanzas","symbol":"AOA","symbol-alt-narrow":"Kz"},"AOK":{"displayName":"kwanza angoleño (1977–1990)","symbol":"AOK"},"AON":{"displayName":"nuevo kwanza angoleño (1990–2000)","symbol":"AON"},"AOR":{"displayName":"kwanza reajustado angoleño (1995–1999)","symbol":"AOR"},"ARA":{"displayName":"austral argentino","displayName-count-one":"austral argentino","displayName-count-other":"australes argentinos","symbol":"ARA"},"ARL":{"displayName":"ARL","symbol":"ARL"},"ARM":{"displayName":"ARM","symbol":"ARM"},"ARP":{"displayName":"peso argentino (1983–1985)","displayName-count-one":"peso argentino (ARP)","displayName-count-other":"pesos argentinos (ARP)","symbol":"ARP"},"ARS":{"displayName":"peso argentino","displayName-count-one":"peso argentino","displayName-count-other":"pesos argentinos","symbol":"ARS","symbol-alt-narrow":"$"},"ATS":{"displayName":"chelín austriaco","displayName-count-one":"chelín austriaco","displayName-count-other":"chelines austriacos","symbol":"ATS"},"AUD":{"displayName":"dólar australiano","displayName-count-one":"dólar australiano","displayName-count-other":"dólares australianos","symbol":"AUD","symbol-alt-narrow":"$"},"AWG":{"displayName":"florín arubeño","displayName-count-one":"florín arubeño","displayName-count-other":"florines arubeños","symbol":"AWG"},"AZM":{"displayName":"manat azerí (1993–2006)","symbol":"AZM"},"AZN":{"displayName":"manat azerbaiyano","displayName-count-one":"manat azerbaiyano","displayName-count-other":"manat azerbaiyanos","symbol":"AZN"},"BAD":{"displayName":"dinar bosnio","displayName-count-one":"dinar bosnio","displayName-count-other":"dinares bosnios","symbol":"BAD"},"BAM":{"displayName":"marco convertible de Bosnia y Herzegovina","displayName-count-one":"marco convertible de Bosnia y Herzegovina","displayName-count-other":"marcos convertibles de Bosnia y Herzegovina","symbol":"BAM","symbol-alt-narrow":"KM"},"BAN":{"displayName":"BAN","symbol":"BAN"},"BBD":{"displayName":"dólar barbadense","displayName-count-one":"dólar barbadense","displayName-count-other":"dólares barbadenses","symbol":"BBD","symbol-alt-narrow":"$"},"BDT":{"displayName":"taka","displayName-count-one":"taka","displayName-count-other":"takas","symbol":"BDT","symbol-alt-narrow":"৳"},"BEC":{"displayName":"franco belga (convertible)","displayName-count-one":"franco belga (convertible)","displayName-count-other":"francos belgas (convertibles)","symbol":"BEC"},"BEF":{"displayName":"franco belga","displayName-count-one":"franco belga","displayName-count-other":"francos belgas","symbol":"BEF"},"BEL":{"displayName":"franco belga (financiero)","displayName-count-one":"franco belga (financiero)","displayName-count-other":"francos belgas (financieros)","symbol":"BEL"},"BGL":{"displayName":"lev fuerte búlgaro","displayName-count-one":"lev fuerte búlgaro","displayName-count-other":"leva fuertes búlgaros","symbol":"BGL"},"BGM":{"displayName":"BGM","symbol":"BGM"},"BGN":{"displayName":"lev búlgaro","displayName-count-one":"lev búlgaro","displayName-count-other":"leva búlgaros","symbol":"BGN"},"BGO":{"displayName":"BGO","symbol":"BGO"},"BHD":{"displayName":"dinar bahreiní","displayName-count-one":"dinar bahreiní","displayName-count-other":"dinares bahreiníes","symbol":"BHD"},"BIF":{"displayName":"franco burundés","displayName-count-one":"franco burundés","displayName-count-other":"francos burundeses","symbol":"BIF"},"BMD":{"displayName":"dólar de Bermudas","displayName-count-one":"dólar de Bermudas","displayName-count-other":"dólares de Bermudas","symbol":"BMD","symbol-alt-narrow":"$"},"BND":{"displayName":"dólar bruneano","displayName-count-one":"dólar bruneano","displayName-count-other":"dólares bruneanos","symbol":"BND","symbol-alt-narrow":"$"},"BOB":{"displayName":"boliviano","displayName-count-one":"boliviano","displayName-count-other":"bolivianos","symbol":"BOB","symbol-alt-narrow":"Bs"},"BOL":{"displayName":"BOL","symbol":"BOL"},"BOP":{"displayName":"peso boliviano","displayName-count-one":"peso boliviano","displayName-count-other":"pesos bolivianos","symbol":"BOP"},"BOV":{"displayName":"MVDOL boliviano","displayName-count-one":"MVDOL boliviano","displayName-count-other":"MVDOL bolivianos","symbol":"BOV"},"BRB":{"displayName":"nuevo cruceiro brasileño (1967–1986)","displayName-count-one":"nuevo cruzado brasileño (BRB)","displayName-count-other":"nuevos cruzados brasileños (BRB)","symbol":"BRB"},"BRC":{"displayName":"cruzado brasileño","displayName-count-one":"cruzado brasileño","displayName-count-other":"cruzados brasileños","symbol":"BRC"},"BRE":{"displayName":"cruceiro brasileño (1990–1993)","displayName-count-one":"cruceiro brasileño (BRE)","displayName-count-other":"cruceiros brasileños (BRE)","symbol":"BRE"},"BRL":{"displayName":"real brasileño","displayName-count-one":"real brasileño","displayName-count-other":"reales brasileños","symbol":"BRL","symbol-alt-narrow":"R$"},"BRN":{"displayName":"nuevo cruzado brasileño","displayName-count-one":"nuevo cruzado brasileño","displayName-count-other":"nuevos cruzados brasileños","symbol":"BRN"},"BRR":{"displayName":"cruceiro brasileño","displayName-count-one":"cruceiro brasileño","displayName-count-other":"cruceiros brasileños","symbol":"BRR"},"BRZ":{"displayName":"BRZ","symbol":"BRZ"},"BSD":{"displayName":"dólar bahameño","displayName-count-one":"dólar bahameño","displayName-count-other":"dólares bahameños","symbol":"BSD","symbol-alt-narrow":"$"},"BTN":{"displayName":"gultrum","displayName-count-one":"gultrum","displayName-count-other":"gultrums","symbol":"BTN"},"BUK":{"displayName":"kyat birmano","displayName-count-one":"kyat birmano","displayName-count-other":"kyat birmanos","symbol":"BUK"},"BWP":{"displayName":"pula","displayName-count-one":"pula","displayName-count-other":"pulas","symbol":"BWP","symbol-alt-narrow":"P"},"BYB":{"displayName":"nuevo rublo bielorruso (1994–1999)","displayName-count-one":"nuevo rublo bielorruso","displayName-count-other":"nuevos rublos bielorrusos","symbol":"BYB"},"BYN":{"displayName":"rublo bielorruso","displayName-count-one":"rublo bielorruso","displayName-count-other":"rublos bielorrusos","symbol":"BYN","symbol-alt-narrow":"р."},"BYR":{"displayName":"rublo bielorruso (2000–2016)","displayName-count-one":"rublo bielorruso (2000–2016)","displayName-count-other":"rublos bielorrusos (2000–2016)","symbol":"BYR"},"BZD":{"displayName":"dólar beliceño","displayName-count-one":"dólar beliceño","displayName-count-other":"dólares beliceños","symbol":"BZD","symbol-alt-narrow":"$"},"CAD":{"displayName":"dólar canadiense","displayName-count-one":"dólar canadiense","displayName-count-other":"dólares canadienses","symbol":"CAD","symbol-alt-narrow":"$"},"CDF":{"displayName":"franco congoleño","displayName-count-one":"franco congoleño","displayName-count-other":"francos congoleños","symbol":"CDF"},"CHE":{"displayName":"euro WIR","displayName-count-one":"euro WIR","displayName-count-other":"euros WIR","symbol":"CHE"},"CHF":{"displayName":"franco suizo","displayName-count-one":"franco suizo","displayName-count-other":"francos suizos","symbol":"CHF"},"CHW":{"displayName":"franco WIR","displayName-count-one":"franco WIR","displayName-count-other":"francos WIR","symbol":"CHW"},"CLE":{"displayName":"CLE","symbol":"CLE"},"CLF":{"displayName":"unidad de fomento chilena","displayName-count-one":"unidad de fomento chilena","displayName-count-other":"unidades de fomento chilenas","symbol":"CLF"},"CLP":{"displayName":"peso chileno","displayName-count-one":"peso chileno","displayName-count-other":"pesos chilenos","symbol":"CLP","symbol-alt-narrow":"$"},"CNH":{"displayName":"yuan chino (extracontinental)","displayName-count-one":"yuan chino (extracontinental)","displayName-count-other":"yuan chino (extracontinental)","symbol":"CNH"},"CNX":{"displayName":"CNX","symbol":"CNX"},"CNY":{"displayName":"yuan","displayName-count-one":"yuan","displayName-count-other":"yuanes","symbol":"CNY","symbol-alt-narrow":"¥"},"COP":{"displayName":"peso colombiano","displayName-count-one":"peso colombiano","displayName-count-other":"pesos colombianos","symbol":"COP","symbol-alt-narrow":"$"},"COU":{"displayName":"unidad de valor real colombiana","displayName-count-one":"unidad de valor real","displayName-count-other":"unidades de valor reales","symbol":"COU"},"CRC":{"displayName":"colón costarricense","displayName-count-one":"colón costarricense","displayName-count-other":"colones costarricenses","symbol":"CRC","symbol-alt-narrow":"₡"},"CSD":{"displayName":"antiguo dinar serbio","displayName-count-one":"antiguo dinar serbio","displayName-count-other":"antiguos dinares serbios","symbol":"CSD"},"CSK":{"displayName":"corona fuerte checoslovaca","displayName-count-one":"corona fuerte checoslovaca","displayName-count-other":"coronas fuertes checoslovacas","symbol":"CSK"},"CUC":{"displayName":"peso cubano convertible","displayName-count-one":"peso cubano convertible","displayName-count-other":"pesos cubanos convertibles","symbol":"CUC","symbol-alt-narrow":"$"},"CUP":{"displayName":"peso cubano","displayName-count-one":"peso cubano","displayName-count-other":"pesos cubanos","symbol":"CUP","symbol-alt-narrow":"$"},"CVE":{"displayName":"escudo de Cabo Verde","displayName-count-one":"escudo de Cabo Verde","displayName-count-other":"escudos de Cabo Verde","symbol":"CVE"},"CYP":{"displayName":"libra chipriota","displayName-count-one":"libra chipriota","displayName-count-other":"libras chipriotas","symbol":"CYP"},"CZK":{"displayName":"corona checa","displayName-count-one":"corona checa","displayName-count-other":"coronas checas","symbol":"CZK","symbol-alt-narrow":"Kč"},"DDM":{"displayName":"ostmark de Alemania del Este","displayName-count-one":"marco de la República Democrática Alemana","displayName-count-other":"marcos de la República Democrática Alemana","symbol":"DDM"},"DEM":{"displayName":"marco alemán","displayName-count-one":"marco alemán","displayName-count-other":"marcos alemanes","symbol":"DEM"},"DJF":{"displayName":"franco yibutiano","displayName-count-one":"franco yibutiano","displayName-count-other":"francos yibutianos","symbol":"DJF"},"DKK":{"displayName":"corona danesa","displayName-count-one":"corona danesa","displayName-count-other":"coronas danesas","symbol":"DKK","symbol-alt-narrow":"kr"},"DOP":{"displayName":"peso dominicano","displayName-count-one":"peso dominicano","displayName-count-other":"pesos dominicanos","symbol":"DOP","symbol-alt-narrow":"$"},"DZD":{"displayName":"dinar argelino","displayName-count-one":"dinar argelino","displayName-count-other":"dinares argelinos","symbol":"DZD"},"ECS":{"displayName":"sucre ecuatoriano","displayName-count-one":"sucre ecuatoriano","displayName-count-other":"sucres ecuatorianos","symbol":"ECS"},"ECV":{"displayName":"unidad de valor constante (UVC) ecuatoriana","displayName-count-one":"unidad de valor constante (UVC) ecuatoriana","displayName-count-other":"unidades de valor constante (UVC) ecuatorianas","symbol":"ECV"},"EEK":{"displayName":"corona estonia","displayName-count-one":"corona estonia","displayName-count-other":"coronas estonias","symbol":"EEK"},"EGP":{"displayName":"libra egipcia","displayName-count-one":"libra egipcia","displayName-count-other":"libras egipcias","symbol":"EGP","symbol-alt-narrow":"E£"},"ERN":{"displayName":"nafka","displayName-count-one":"nakfa","displayName-count-other":"nakfas","symbol":"ERN"},"ESA":{"displayName":"peseta española (cuenta A)","displayName-count-one":"peseta española (cuenta A)","displayName-count-other":"pesetas españolas (cuenta A)","symbol":"ESA"},"ESB":{"displayName":"peseta española (cuenta convertible)","displayName-count-one":"peseta española (cuenta convertible)","displayName-count-other":"pesetas españolas (cuenta convertible)","symbol":"ESB"},"ESP":{"displayName":"peseta española","displayName-count-one":"peseta española","displayName-count-other":"pesetas españolas","symbol":"₧","symbol-alt-narrow":"₧"},"ETB":{"displayName":"bir","displayName-count-one":"bir","displayName-count-other":"bires","symbol":"ETB"},"EUR":{"displayName":"euro","displayName-count-one":"euro","displayName-count-other":"euros","symbol":"EUR","symbol-alt-narrow":"€"},"FIM":{"displayName":"marco finlandés","displayName-count-one":"marco finlandés","displayName-count-other":"marcos finlandeses","symbol":"FIM"},"FJD":{"displayName":"dólar fiyiano","displayName-count-one":"dólar fiyiano","displayName-count-other":"dólares fiyianos","symbol":"FJD","symbol-alt-narrow":"$"},"FKP":{"displayName":"libra malvinense","displayName-count-one":"libra malvinense","displayName-count-other":"libras malvinenses","symbol":"FKP","symbol-alt-narrow":"£"},"FRF":{"displayName":"franco francés","displayName-count-one":"franco francés","displayName-count-other":"francos franceses","symbol":"FRF"},"GBP":{"displayName":"libra esterlina","displayName-count-one":"libra esterlina","displayName-count-other":"libras esterlinas","symbol":"GBP","symbol-alt-narrow":"£"},"GEK":{"displayName":"kupon larit georgiano","symbol":"GEK"},"GEL":{"displayName":"lari","displayName-count-one":"lari","displayName-count-other":"laris","symbol":"GEL","symbol-alt-narrow":"₾","symbol-alt-variant":"₾"},"GHC":{"displayName":"cedi ghanés (1979–2007)","symbol":"GHC"},"GHS":{"displayName":"cedi","displayName-count-one":"cedi","displayName-count-other":"cedis","symbol":"GHS"},"GIP":{"displayName":"libra gibraltareña","displayName-count-one":"libra gibraltareña","displayName-count-other":"libras gibraltareñas","symbol":"GIP","symbol-alt-narrow":"£"},"GMD":{"displayName":"dalasi","displayName-count-one":"dalasi","displayName-count-other":"dalasis","symbol":"GMD"},"GNF":{"displayName":"franco guineano","displayName-count-one":"franco guineano","displayName-count-other":"francos guineanos","symbol":"GNF","symbol-alt-narrow":"FG"},"GNS":{"displayName":"syli guineano","symbol":"GNS"},"GQE":{"displayName":"ekuele de Guinea Ecuatorial","displayName-count-one":"ekuele de Guinea Ecuatorial","displayName-count-other":"ekueles de Guinea Ecuatorial","symbol":"GQE"},"GRD":{"displayName":"dracma griego","displayName-count-one":"dracma griego","displayName-count-other":"dracmas griegos","symbol":"GRD"},"GTQ":{"displayName":"quetzal guatemalteco","displayName-count-one":"quetzal guatemalteco","displayName-count-other":"quetzales guatemaltecos","symbol":"GTQ","symbol-alt-narrow":"Q"},"GWE":{"displayName":"escudo de Guinea Portuguesa","symbol":"GWE"},"GWP":{"displayName":"peso de Guinea-Bissáu","symbol":"GWP"},"GYD":{"displayName":"dólar guyanés","displayName-count-one":"dólar guyanés","displayName-count-other":"dólares guyaneses","symbol":"GYD","symbol-alt-narrow":"$"},"HKD":{"displayName":"dólar hongkonés","displayName-count-one":"dólar hongkonés","displayName-count-other":"dólares hongkoneses","symbol":"HKD","symbol-alt-narrow":"$"},"HNL":{"displayName":"lempira hondureño","displayName-count-one":"lempira hondureño","displayName-count-other":"lempiras hondureños","symbol":"HNL","symbol-alt-narrow":"L"},"HRD":{"displayName":"dinar croata","displayName-count-one":"dinar croata","displayName-count-other":"dinares croatas","symbol":"HRD"},"HRK":{"displayName":"kuna","displayName-count-one":"kuna","displayName-count-other":"kunas","symbol":"HRK","symbol-alt-narrow":"kn"},"HTG":{"displayName":"gourde haitiano","displayName-count-one":"gourde haitiano","displayName-count-other":"gourdes haitianos","symbol":"HTG"},"HUF":{"displayName":"forinto húngaro","displayName-count-one":"forinto húngaro","displayName-count-other":"forintos húngaros","symbol":"HUF","symbol-alt-narrow":"Ft"},"IDR":{"displayName":"rupia indonesia","displayName-count-one":"rupia indonesia","displayName-count-other":"rupias indonesias","symbol":"IDR","symbol-alt-narrow":"Rp"},"IEP":{"displayName":"libra irlandesa","displayName-count-one":"libra irlandesa","displayName-count-other":"libras irlandesas","symbol":"IEP"},"ILP":{"displayName":"libra israelí","displayName-count-one":"libra israelí","displayName-count-other":"libras israelíes","symbol":"ILP"},"ILR":{"displayName":"ILR","symbol":"ILR"},"ILS":{"displayName":"nuevo séquel israelí","displayName-count-one":"nuevo séquel israelí","displayName-count-other":"nuevos séqueles israelíes","symbol":"ILS","symbol-alt-narrow":"₪"},"INR":{"displayName":"rupia india","displayName-count-one":"rupia india","displayName-count-other":"rupias indias","symbol":"INR","symbol-alt-narrow":"₹"},"IQD":{"displayName":"dinar iraquí","displayName-count-one":"dinar iraquí","displayName-count-other":"dinares iraquíes","symbol":"IQD"},"IRR":{"displayName":"rial iraní","displayName-count-one":"rial iraní","displayName-count-other":"riales iraníes","symbol":"IRR"},"ISJ":{"displayName":"ISJ","symbol":"ISJ"},"ISK":{"displayName":"corona islandesa","displayName-count-one":"corona islandesa","displayName-count-other":"coronas islandesas","symbol":"ISK","symbol-alt-narrow":"kr"},"ITL":{"displayName":"lira italiana","displayName-count-one":"lira italiana","displayName-count-other":"liras italianas","symbol":"ITL"},"JMD":{"displayName":"dólar jamaicano","displayName-count-one":"dólar jamaicano","displayName-count-other":"dólares jamaicanos","symbol":"JMD","symbol-alt-narrow":"$"},"JOD":{"displayName":"dinar jordano","displayName-count-one":"dinar jordano","displayName-count-other":"dinares jordanos","symbol":"JOD"},"JPY":{"displayName":"yen","displayName-count-one":"yen","displayName-count-other":"yenes","symbol":"¥","symbol-alt-narrow":"¥"},"KES":{"displayName":"chelín keniano","displayName-count-one":"chelín keniano","displayName-count-other":"chelines kenianos","symbol":"KES"},"KGS":{"displayName":"som","displayName-count-one":"som","displayName-count-other":"soms","symbol":"KGS"},"KHR":{"displayName":"riel","displayName-count-one":"riel","displayName-count-other":"rieles","symbol":"KHR","symbol-alt-narrow":"៛"},"KMF":{"displayName":"franco comorense","displayName-count-one":"franco comorense","displayName-count-other":"francos comorenses","symbol":"KMF","symbol-alt-narrow":"CF"},"KPW":{"displayName":"won norcoreano","displayName-count-one":"won norcoreano","displayName-count-other":"wons norcoreanos","symbol":"KPW","symbol-alt-narrow":"₩"},"KRH":{"displayName":"KRH","symbol":"KRH"},"KRO":{"displayName":"KRO","symbol":"KRO"},"KRW":{"displayName":"won surcoreano","displayName-count-one":"won surcoreano","displayName-count-other":"wons surcoreanos","symbol":"KRW","symbol-alt-narrow":"₩"},"KWD":{"displayName":"dinar kuwaití","displayName-count-one":"dinar kuwaití","displayName-count-other":"dinares kuwaitíes","symbol":"KWD"},"KYD":{"displayName":"dólar de las Islas Caimán","displayName-count-one":"dólar de las Islas Caimán","displayName-count-other":"dólares de las Islas Caimán","symbol":"KYD","symbol-alt-narrow":"$"},"KZT":{"displayName":"tenge kazako","displayName-count-one":"tenge kazako","displayName-count-other":"tenges kazakos","symbol":"KZT","symbol-alt-narrow":"₸"},"LAK":{"displayName":"kip","displayName-count-one":"kip","displayName-count-other":"kips","symbol":"LAK","symbol-alt-narrow":"₭"},"LBP":{"displayName":"libra libanesa","displayName-count-one":"libra libanesa","displayName-count-other":"libras libanesas","symbol":"LBP","symbol-alt-narrow":"L£"},"LKR":{"displayName":"rupia esrilanquesa","displayName-count-one":"rupia esrilanquesa","displayName-count-other":"rupias esrilanquesas","symbol":"LKR","symbol-alt-narrow":"Rs"},"LRD":{"displayName":"dólar liberiano","displayName-count-one":"dólar liberiano","displayName-count-other":"dólares liberianos","symbol":"LRD","symbol-alt-narrow":"$"},"LSL":{"displayName":"loti lesothense","symbol":"LSL"},"LTL":{"displayName":"litas lituano","displayName-count-one":"litas lituana","displayName-count-other":"litas lituanas","symbol":"LTL","symbol-alt-narrow":"Lt"},"LTT":{"displayName":"talonas lituano","displayName-count-one":"talonas lituana","displayName-count-other":"talonas lituanas","symbol":"LTT"},"LUC":{"displayName":"franco convertible luxemburgués","displayName-count-one":"franco convertible luxemburgués","displayName-count-other":"francos convertibles luxemburgueses","symbol":"LUC"},"LUF":{"displayName":"franco luxemburgués","displayName-count-one":"franco luxemburgués","displayName-count-other":"francos luxemburgueses","symbol":"LUF"},"LUL":{"displayName":"franco financiero luxemburgués","displayName-count-one":"franco financiero luxemburgués","displayName-count-other":"francos financieros luxemburgueses","symbol":"LUL"},"LVL":{"displayName":"lats letón","displayName-count-one":"lats letón","displayName-count-other":"lats letónes","symbol":"LVL","symbol-alt-narrow":"Ls"},"LVR":{"displayName":"rublo letón","displayName-count-one":"rublo letón","displayName-count-other":"rublos letones","symbol":"LVR"},"LYD":{"displayName":"dinar libio","displayName-count-one":"dinar libio","displayName-count-other":"dinares libios","symbol":"LYD"},"MAD":{"displayName":"dírham marroquí","displayName-count-one":"dírham marroquí","displayName-count-other":"dírhams marroquíes","symbol":"MAD"},"MAF":{"displayName":"franco marroquí","displayName-count-one":"franco marroquí","displayName-count-other":"francos marroquíes","symbol":"MAF"},"MCF":{"displayName":"MCF","symbol":"MCF"},"MDC":{"displayName":"MDC","symbol":"MDC"},"MDL":{"displayName":"leu moldavo","displayName-count-one":"leu moldavo","displayName-count-other":"lei moldavos","symbol":"MDL"},"MGA":{"displayName":"ariari","displayName-count-one":"ariari","displayName-count-other":"ariaris","symbol":"MGA","symbol-alt-narrow":"Ar"},"MGF":{"displayName":"franco malgache","symbol":"MGF"},"MKD":{"displayName":"dinar macedonio","displayName-count-one":"dinar macedonio","displayName-count-other":"dinares macedonios","symbol":"MKD"},"MKN":{"displayName":"MKN","symbol":"MKN"},"MLF":{"displayName":"franco malí","symbol":"MLF"},"MMK":{"displayName":"kiat","displayName-count-one":"kiat","displayName-count-other":"kiats","symbol":"MMK","symbol-alt-narrow":"K"},"MNT":{"displayName":"tugrik","displayName-count-one":"tugrik","displayName-count-other":"tugriks","symbol":"MNT","symbol-alt-narrow":"₮"},"MOP":{"displayName":"pataca de Macao","displayName-count-one":"pataca de Macao","displayName-count-other":"patacas de Macao","symbol":"MOP"},"MRO":{"displayName":"uguiya","displayName-count-one":"uguiya","displayName-count-other":"uguiyas","symbol":"MRO"},"MTL":{"displayName":"lira maltesa","displayName-count-one":"lira maltesa","displayName-count-other":"liras maltesas","symbol":"MTL"},"MTP":{"displayName":"libra maltesa","displayName-count-one":"libra maltesa","displayName-count-other":"libras maltesas","symbol":"MTP"},"MUR":{"displayName":"rupia mauriciana","displayName-count-one":"rupia mauriciana","displayName-count-other":"rupias mauricianas","symbol":"MUR","symbol-alt-narrow":"Rs"},"MVP":{"displayName":"MVP","symbol":"MVP"},"MVR":{"displayName":"rufiya","displayName-count-one":"rufiya","displayName-count-other":"rufiyas","symbol":"MVR"},"MWK":{"displayName":"kwacha malauí","displayName-count-one":"kwacha malauí","displayName-count-other":"kwachas malauís","symbol":"MWK"},"MXN":{"displayName":"peso mexicano","displayName-count-one":"peso mexicano","displayName-count-other":"pesos mexicanos","symbol":"MXN","symbol-alt-narrow":"$"},"MXP":{"displayName":"peso de plata mexicano (1861–1992)","displayName-count-one":"peso de plata mexicano (MXP)","displayName-count-other":"pesos de plata mexicanos (MXP)","symbol":"MXP"},"MXV":{"displayName":"unidad de inversión (UDI) mexicana","displayName-count-one":"unidad de inversión (UDI) mexicana","displayName-count-other":"unidades de inversión (UDI) mexicanas","symbol":"MXV"},"MYR":{"displayName":"ringit","displayName-count-one":"ringit","displayName-count-other":"ringits","symbol":"MYR","symbol-alt-narrow":"RM"},"MZE":{"displayName":"escudo mozambiqueño","displayName-count-one":"escudo mozambiqueño","displayName-count-other":"escudos mozambiqueños","symbol":"MZE"},"MZM":{"displayName":"antiguo metical mozambiqueño","symbol":"MZM"},"MZN":{"displayName":"metical","displayName-count-one":"metical","displayName-count-other":"meticales","symbol":"MZN"},"NAD":{"displayName":"dólar namibio","displayName-count-one":"dólar namibio","displayName-count-other":"dólares namibios","symbol":"NAD","symbol-alt-narrow":"$"},"NGN":{"displayName":"naira","displayName-count-one":"naira","displayName-count-other":"nairas","symbol":"NGN","symbol-alt-narrow":"₦"},"NIC":{"displayName":"córdoba nicaragüense (1988–1991)","displayName-count-one":"córdoba nicaragüense (1988–1991)","displayName-count-other":"córdobas nicaragüenses (1988–1991)","symbol":"NIC"},"NIO":{"displayName":"córdoba nicaragüense","displayName-count-one":"córdoba nicaragüense","displayName-count-other":"córdobas nicaragüenses","symbol":"NIO","symbol-alt-narrow":"C$"},"NLG":{"displayName":"florín neerlandés","displayName-count-one":"florín neerlandés","displayName-count-other":"florines neerlandeses","symbol":"NLG"},"NOK":{"displayName":"corona noruega","displayName-count-one":"corona noruega","displayName-count-other":"coronas noruegas","symbol":"NOK","symbol-alt-narrow":"kr"},"NPR":{"displayName":"rupia nepalí","displayName-count-one":"rupia nepalí","displayName-count-other":"rupias nepalíes","symbol":"NPR","symbol-alt-narrow":"Rs"},"NZD":{"displayName":"dólar neozelandés","displayName-count-one":"dólar neozelandés","displayName-count-other":"dólares neozelandeses","symbol":"NZD","symbol-alt-narrow":"$"},"OMR":{"displayName":"rial omaní","displayName-count-one":"rial omaní","displayName-count-other":"riales omaníes","symbol":"OMR"},"PAB":{"displayName":"balboa panameño","displayName-count-one":"balboa panameño","displayName-count-other":"balboas panameños","symbol":"PAB"},"PEI":{"displayName":"inti peruano","displayName-count-one":"inti peruano","displayName-count-other":"intis peruanos","symbol":"PEI"},"PEN":{"displayName":"sol peruano","displayName-count-one":"sol peruano","displayName-count-other":"soles peruanos","symbol":"PEN"},"PES":{"displayName":"sol peruano (1863–1965)","displayName-count-one":"sol peruano (1863–1965)","displayName-count-other":"soles peruanos (1863–1965)","symbol":"PES"},"PGK":{"displayName":"kina","displayName-count-one":"kina","displayName-count-other":"kinas","symbol":"PGK"},"PHP":{"displayName":"peso filipino","displayName-count-one":"peso filipino","displayName-count-other":"pesos filipinos","symbol":"PHP","symbol-alt-narrow":"₱"},"PKR":{"displayName":"rupia pakistaní","displayName-count-one":"rupia pakistaní","displayName-count-other":"rupias pakistaníes","symbol":"PKR","symbol-alt-narrow":"Rs"},"PLN":{"displayName":"esloti","displayName-count-one":"esloti","displayName-count-other":"eslotis","symbol":"PLN","symbol-alt-narrow":"zł"},"PLZ":{"displayName":"zloty polaco (1950–1995)","displayName-count-one":"zloty polaco (PLZ)","displayName-count-other":"zlotys polacos (PLZ)","symbol":"PLZ"},"PTE":{"displayName":"escudo portugués","displayName-count-one":"escudo portugués","displayName-count-other":"escudos portugueses","symbol":"PTE"},"PYG":{"displayName":"guaraní paraguayo","displayName-count-one":"guaraní paraguayo","displayName-count-other":"guaraníes paraguayos","symbol":"PYG","symbol-alt-narrow":"₲"},"QAR":{"displayName":"rial catarí","displayName-count-one":"rial catarí","displayName-count-other":"riales cataríes","symbol":"QAR"},"RHD":{"displayName":"dólar rodesiano","symbol":"RHD"},"ROL":{"displayName":"antiguo leu rumano","displayName-count-one":"antiguo leu rumano","displayName-count-other":"antiguos lei rumanos","symbol":"ROL"},"RON":{"displayName":"leu rumano","displayName-count-one":"leu rumano","displayName-count-other":"lei rumanos","symbol":"RON","symbol-alt-narrow":"lei"},"RSD":{"displayName":"dinar serbio","displayName-count-one":"dinar serbio","displayName-count-other":"dinares serbios","symbol":"RSD"},"RUB":{"displayName":"rublo ruso","displayName-count-one":"rublo ruso","displayName-count-other":"rublos rusos","symbol":"RUB","symbol-alt-narrow":"₽"},"RUR":{"displayName":"rublo ruso (1991–1998)","displayName-count-one":"rublo ruso (RUR)","displayName-count-other":"rublos rusos (RUR)","symbol":"RUR","symbol-alt-narrow":"р."},"RWF":{"displayName":"franco ruandés","displayName-count-one":"franco ruandés","displayName-count-other":"francos ruandeses","symbol":"RWF","symbol-alt-narrow":"RF"},"SAR":{"displayName":"rial saudí","displayName-count-one":"rial saudí","displayName-count-other":"riales saudíes","symbol":"SAR"},"SBD":{"displayName":"dólar salomonense","displayName-count-one":"dólar salomonense","displayName-count-other":"dólares salomonenses","symbol":"SBD","symbol-alt-narrow":"$"},"SCR":{"displayName":"rupia seychellense","displayName-count-one":"rupia seychellense","displayName-count-other":"rupias seychellenses","symbol":"SCR"},"SDD":{"displayName":"dinar sudanés","displayName-count-one":"dinar sudanés","displayName-count-other":"dinares sudaneses","symbol":"SDD"},"SDG":{"displayName":"libra sudanesa","displayName-count-one":"libra sudanesa","displayName-count-other":"libras sudanesas","symbol":"SDG"},"SDP":{"displayName":"libra sudanesa antigua","displayName-count-one":"libra sudanesa antigua","displayName-count-other":"libras sudanesas antiguas","symbol":"SDP"},"SEK":{"displayName":"corona sueca","displayName-count-one":"corona sueca","displayName-count-other":"coronas suecas","symbol":"SEK","symbol-alt-narrow":"kr"},"SGD":{"displayName":"dólar singapurense","displayName-count-one":"dólar singapurense","displayName-count-other":"dólares singapurenses","symbol":"SGD","symbol-alt-narrow":"$"},"SHP":{"displayName":"libra de Santa Elena","displayName-count-one":"libra de Santa Elena","displayName-count-other":"libras de Santa Elena","symbol":"SHP","symbol-alt-narrow":"£"},"SIT":{"displayName":"tólar esloveno","displayName-count-one":"tólar esloveno","displayName-count-other":"tólares eslovenos","symbol":"SIT"},"SKK":{"displayName":"corona eslovaca","displayName-count-one":"corona eslovaca","displayName-count-other":"coronas eslovacas","symbol":"SKK"},"SLL":{"displayName":"leona","displayName-count-one":"leona","displayName-count-other":"leonas","symbol":"SLL"},"SOS":{"displayName":"chelín somalí","displayName-count-one":"chelín somalí","displayName-count-other":"chelines somalíes","symbol":"SOS"},"SRD":{"displayName":"dólar surinamés","displayName-count-one":"dólar surinamés","displayName-count-other":"dólares surinameses","symbol":"SRD","symbol-alt-narrow":"$"},"SRG":{"displayName":"florín surinamés","symbol":"SRG"},"SSP":{"displayName":"libra sursudanesa","displayName-count-one":"libra sursudanesa","displayName-count-other":"libras sursudanesas","symbol":"SSP","symbol-alt-narrow":"£"},"STD":{"displayName":"dobra","displayName-count-one":"dobra","displayName-count-other":"dobras","symbol":"STD","symbol-alt-narrow":"Db"},"STN":{"displayName":"STN","symbol":"STN"},"SUR":{"displayName":"rublo soviético","displayName-count-one":"rublo soviético","displayName-count-other":"rublos soviéticos","symbol":"SUR"},"SVC":{"displayName":"colón salvadoreño","displayName-count-one":"colón salvadoreño","displayName-count-other":"colones salvadoreños","symbol":"SVC"},"SYP":{"displayName":"libra siria","displayName-count-one":"libra siria","displayName-count-other":"libras sirias","symbol":"SYP","symbol-alt-narrow":"£"},"SZL":{"displayName":"lilangeni","displayName-count-one":"lilangeni","displayName-count-other":"lilangenis","symbol":"SZL"},"THB":{"displayName":"bat","displayName-count-one":"bat","displayName-count-other":"bats","symbol":"THB","symbol-alt-narrow":"฿"},"TJR":{"displayName":"rublo tayiko","symbol":"TJR"},"TJS":{"displayName":"somoni tayiko","displayName-count-one":"somoni tayiko","displayName-count-other":"somonis tayikos","symbol":"TJS"},"TMM":{"displayName":"manat turcomano (1993–2009)","displayName-count-one":"manat turcomano (1993–2009)","displayName-count-other":"manats turcomanos (1993–2009)","symbol":"TMM"},"TMT":{"displayName":"manat turcomano","displayName-count-one":"manat turcomano","displayName-count-other":"manat turcomanos","symbol":"TMT"},"TND":{"displayName":"dinar tunecino","displayName-count-one":"dinar tunecino","displayName-count-other":"dinares tunecinos","symbol":"TND"},"TOP":{"displayName":"paanga","displayName-count-one":"paanga","displayName-count-other":"paangas","symbol":"TOP","symbol-alt-narrow":"T$"},"TPE":{"displayName":"escudo timorense","symbol":"TPE"},"TRL":{"displayName":"lira turca (1922–2005)","displayName-count-one":"lira turca (1922–2005)","displayName-count-other":"liras turcas (1922–2005)","symbol":"TRL"},"TRY":{"displayName":"lira turca","displayName-count-one":"lira turca","displayName-count-other":"liras turcas","symbol":"TRY","symbol-alt-narrow":"₺","symbol-alt-variant":"TL"},"TTD":{"displayName":"dólar de Trinidad y Tobago","displayName-count-one":"dólar de Trinidad y Tobago","displayName-count-other":"dólares de Trinidad y Tobago","symbol":"TTD","symbol-alt-narrow":"$"},"TWD":{"displayName":"nuevo dólar taiwanés","displayName-count-one":"nuevo dólar taiwanés","displayName-count-other":"nuevos dólares taiwaneses","symbol":"TWD","symbol-alt-narrow":"NT$"},"TZS":{"displayName":"chelín tanzano","displayName-count-one":"chelín tanzano","displayName-count-other":"chelines tanzanos","symbol":"TZS"},"UAH":{"displayName":"grivna","displayName-count-one":"grivna","displayName-count-other":"grivnas","symbol":"UAH","symbol-alt-narrow":"₴"},"UAK":{"displayName":"karbovanet ucraniano","displayName-count-one":"karbovanet ucraniano","displayName-count-other":"karbovanets ucranianos","symbol":"UAK"},"UGS":{"displayName":"chelín ugandés (1966–1987)","symbol":"UGS"},"UGX":{"displayName":"chelín ugandés","displayName-count-one":"chelín ugandés","displayName-count-other":"chelines ugandeses","symbol":"UGX"},"USD":{"displayName":"dólar estadounidense","displayName-count-one":"dólar estadounidense","displayName-count-other":"dólares estadounidenses","symbol":"$","symbol-alt-narrow":"$"},"USN":{"displayName":"dólar estadounidense (día siguiente)","displayName-count-one":"dólar estadounidense (día siguiente)","displayName-count-other":"dólares estadounidenses (día siguiente)","symbol":"USN"},"USS":{"displayName":"dólar estadounidense (mismo día)","displayName-count-one":"dólar estadounidense (mismo día)","displayName-count-other":"dólares estadounidenses (mismo día)","symbol":"USS"},"UYI":{"displayName":"peso uruguayo en unidades indexadas","displayName-count-one":"peso uruguayo en unidades indexadas","displayName-count-other":"pesos uruguayos en unidades indexadas","symbol":"UYI"},"UYP":{"displayName":"peso uruguayo (1975–1993)","displayName-count-one":"peso uruguayo (UYP)","displayName-count-other":"pesos uruguayos (UYP)","symbol":"UYP"},"UYU":{"displayName":"peso uruguayo","displayName-count-one":"peso uruguayo","displayName-count-other":"pesos uruguayos","symbol":"UYU","symbol-alt-narrow":"$"},"UZS":{"displayName":"sum","displayName-count-one":"sum","displayName-count-other":"sums","symbol":"UZS"},"VEB":{"displayName":"bolívar venezolano (1871–2008)","displayName-count-one":"bolívar venezolano (1871–2008)","displayName-count-other":"bolívares venezolanos (1871–2008)","symbol":"VEB"},"VEF":{"displayName":"bolívar venezolano","displayName-count-one":"bolívar venezolano","displayName-count-other":"bolívares venezolanos","symbol":"VEF","symbol-alt-narrow":"BsF"},"VND":{"displayName":"dong","displayName-count-one":"dong","displayName-count-other":"dongs","symbol":"VND","symbol-alt-narrow":"₫"},"VNN":{"displayName":"VNN","symbol":"VNN"},"VUV":{"displayName":"vatu","displayName-count-one":"vatu","displayName-count-other":"vatus","symbol":"VUV"},"WST":{"displayName":"tala","displayName-count-one":"tala","displayName-count-other":"talas","symbol":"WST"},"XAF":{"displayName":"franco CFA de África central","displayName-count-one":"franco CFA de África central","displayName-count-other":"francos CFA de África central","symbol":"XAF"},"XAG":{"displayName":"plata","displayName-count-one":"plata","displayName-count-other":"plata","symbol":"XAG"},"XAU":{"displayName":"oro","displayName-count-one":"oro","displayName-count-other":"oro","symbol":"XAU"},"XBA":{"displayName":"unidad compuesta europea","displayName-count-one":"unidad compuesta europea","displayName-count-other":"unidades compuestas europeas","symbol":"XBA"},"XBB":{"displayName":"unidad monetaria europea","displayName-count-one":"unidad monetaria europea","displayName-count-other":"unidades monetarias europeas","symbol":"XBB"},"XBC":{"displayName":"unidad de cuenta europea (XBC)","displayName-count-one":"unidad de cuenta europea (XBC)","displayName-count-other":"unidades de cuenta europeas (XBC)","symbol":"XBC"},"XBD":{"displayName":"unidad de cuenta europea (XBD)","displayName-count-one":"unidad de cuenta europea (XBD)","displayName-count-other":"unidades de cuenta europeas (XBD)","symbol":"XBD"},"XCD":{"displayName":"dólar del Caribe Oriental","displayName-count-one":"dólar del Caribe Oriental","displayName-count-other":"dólares del Caribe Oriental","symbol":"XCD","symbol-alt-narrow":"$"},"XDR":{"displayName":"derechos especiales de giro","symbol":"XDR"},"XEU":{"displayName":"unidad de moneda europea","displayName-count-one":"unidad de moneda europea","displayName-count-other":"unidades de moneda europeas","symbol":"XEU"},"XFO":{"displayName":"franco oro francés","displayName-count-one":"franco oro francés","displayName-count-other":"francos oro franceses","symbol":"XFO"},"XFU":{"displayName":"franco UIC francés","displayName-count-one":"franco UIC francés","displayName-count-other":"francos UIC franceses","symbol":"XFU"},"XOF":{"displayName":"franco CFA de África Occidental","displayName-count-one":"franco CFA de África Occidental","displayName-count-other":"francos CFA de África Occidental","symbol":"XOF"},"XPD":{"displayName":"paladio","displayName-count-one":"paladio","displayName-count-other":"paladio","symbol":"XPD"},"XPF":{"displayName":"franco CFP","displayName-count-one":"franco CFP","displayName-count-other":"francos CFP","symbol":"CFPF"},"XPT":{"displayName":"platino","displayName-count-one":"platino","displayName-count-other":"platino","symbol":"XPT"},"XRE":{"displayName":"fondos RINET","symbol":"XRE"},"XSU":{"displayName":"XSU","symbol":"XSU"},"XTS":{"displayName":"código reservado para pruebas","symbol":"XTS"},"XUA":{"displayName":"XUA","symbol":"XUA"},"XXX":{"displayName":"moneda desconocida","displayName-count-one":"(unidad de moneda desconocida)","displayName-count-other":"(moneda desconocida)","symbol":"XXX"},"YDD":{"displayName":"dinar yemení","symbol":"YDD"},"YER":{"displayName":"rial yemení","displayName-count-one":"rial yemení","displayName-count-other":"riales yemeníes","symbol":"YER"},"YUD":{"displayName":"dinar fuerte yugoslavo","symbol":"YUD"},"YUM":{"displayName":"super dinar yugoslavo","symbol":"YUM"},"YUN":{"displayName":"dinar convertible yugoslavo","displayName-count-one":"dinar convertible yugoslavo","displayName-count-other":"dinares convertibles yugoslavos","symbol":"YUN"},"YUR":{"displayName":"YUR","symbol":"YUR"},"ZAL":{"displayName":"rand sudafricano (financiero)","symbol":"ZAL"},"ZAR":{"displayName":"rand","displayName-count-one":"rand","displayName-count-other":"rands","symbol":"ZAR","symbol-alt-narrow":"R"},"ZMK":{"displayName":"kwacha zambiano (1968–2012)","displayName-count-one":"kwacha zambiano (1968–2012)","displayName-count-other":"kwachas zambianos (1968–2012)","symbol":"ZMK"},"ZMW":{"displayName":"kwacha zambiano","displayName-count-one":"kwacha zambiano","displayName-count-other":"kwachas zambianos","symbol":"ZMW","symbol-alt-narrow":"ZK"},"ZRN":{"displayName":"nuevo zaire zaireño","symbol":"ZRN"},"ZRZ":{"displayName":"zaire zaireño","symbol":"ZRZ"},"ZWD":{"displayName":"dólar de Zimbabue","symbol":"ZWD"},"ZWL":{"displayName":"dólar zimbabuense","symbol":"ZWL"},"ZWR":{"displayName":"ZWR","symbol":"ZWR"}},"numbers":{"defaultNumberingSystem":"latn","numberFormats":{"currencyFormats":"¤#,##0.00","currencyFormats-short":{"standard":{"1000-count-one":"0 K ¤","1000-count-other":"0 K ¤","10000-count-one":"00 K ¤","10000-count-other":"00 K ¤","100000-count-one":"000 K ¤","100000-count-other":"000 K ¤","1000000-count-one":"0 M ¤","1000000-count-other":"0 M ¤","10000000-count-one":"00 M ¤","10000000-count-other":"00 M ¤","100000000-count-one":"000 M ¤","100000000-count-other":"000 M ¤","1000000000-count-one":"0000 M ¤","1000000000-count-other":"0000 M ¤","10000000000-count-one":"00 mil M¤","10000000000-count-other":"00 mil M¤","100000000000-count-one":"000 mil M¤","100000000000-count-other":"000 mil M¤","1000000000000-count-one":"0 B¤","1000000000000-count-other":"0 B¤","10000000000000-count-one":"00 B¤","10000000000000-count-other":"00 B¤","100000000000000-count-one":"000 B¤","100000000000000-count-other":"000 B¤"}},"decimalFormats":"#,##0.###","decimalFormats-long":{"decimalFormat":{"1000-count-one":"0 mil","1000-count-other":"0 mil","10000-count-one":"00 mil","10000-count-other":"00 mil","100000-count-one":"000 mil","100000-count-other":"000 mil","1000000-count-one":"0 millón","1000000-count-other":"0 millones","10000000-count-one":"00 millones","10000000-count-other":"00 millones","100000000-count-one":"000 millones","100000000-count-other":"000 millones","1000000000-count-one":"0 mil millones","1000000000-count-other":"0 mil millones","10000000000-count-one":"00 mil millones","10000000000-count-other":"00 mil millones","100000000000-count-one":"000 mil millones","100000000000-count-other":"000 mil millones","1000000000000-count-one":"0 trillón","1000000000000-count-other":"0 trillones","10000000000000-count-one":"00 trillones","10000000000000-count-other":"00 trillones","100000000000000-count-one":"000 trillones","100000000000000-count-other":"000 trillones"}},"decimalFormats-short":{"decimalFormat":{"1000-count-one":"0","1000-count-other":"0","10000-count-one":"00 K","10000-count-other":"00 K","100000-count-one":"000 K","100000-count-other":"000 K","1000000-count-one":"0 M","1000000-count-other":"0 M","10000000-count-one":"00 M","10000000-count-other":"00 M","100000000-count-one":"000 M","100000000-count-other":"000 M","1000000000-count-one":"0k M","1000000000-count-other":"0k M","10000000000-count-one":"00k M","10000000000-count-other":"00k M","100000000000-count-one":"000k M","100000000000-count-other":"000k M","1000000000000-count-one":"0 T","1000000000000-count-other":"0 T","10000000000000-count-one":"00 T","10000000000000-count-other":"00 T","100000000000000-count-one":"000 T","100000000000000-count-other":"000 T"}},"percentFormats":"#,##0 %","scientificFormats":"#E0"},"numberSymbols":{"decimal":".","exponential":"E","group":",","infinity":"∞","list":";","minusSign":"-","nan":"NaN","perMille":"‰","percentSign":"%","plusSign":"+","superscriptingExponent":"×","timeSeparator":":"}},"supplemental":{"currencies":{"fractions":{"ADP":{"_digits":"0","_rounding":"0"},"AFN":{"_digits":"0","_rounding":"0"},"ALL":{"_digits":"0","_rounding":"0"},"AMD":{"_digits":"0","_rounding":"0"},"BHD":{"_digits":"3","_rounding":"0"},"BIF":{"_digits":"0","_rounding":"0"},"BYN":{"_digits":"2","_rounding":"0"},"BYR":{"_digits":"0","_rounding":"0"},"CAD":{"_cashRounding":"5","_digits":"2","_rounding":"0"},"CHF":{"_cashRounding":"5","_digits":"2","_rounding":"0"},"CLF":{"_digits":"4","_rounding":"0"},"CLP":{"_digits":"0","_rounding":"0"},"COP":{"_digits":"0","_rounding":"0"},"CRC":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"CZK":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"DEFAULT":{"_digits":"2","_rounding":"0"},"DJF":{"_digits":"0","_rounding":"0"},"DKK":{"_cashRounding":"50","_digits":"2","_rounding":"0"},"ESP":{"_digits":"0","_rounding":"0"},"GNF":{"_digits":"0","_rounding":"0"},"GYD":{"_digits":"0","_rounding":"0"},"HUF":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"IDR":{"_digits":"0","_rounding":"0"},"IQD":{"_digits":"0","_rounding":"0"},"IRR":{"_digits":"0","_rounding":"0"},"ISK":{"_digits":"0","_rounding":"0"},"ITL":{"_digits":"0","_rounding":"0"},"JOD":{"_digits":"3","_rounding":"0"},"JPY":{"_digits":"0","_rounding":"0"},"KMF":{"_digits":"0","_rounding":"0"},"KPW":{"_digits":"0","_rounding":"0"},"KRW":{"_digits":"0","_rounding":"0"},"KWD":{"_digits":"3","_rounding":"0"},"LAK":{"_digits":"0","_rounding":"0"},"LBP":{"_digits":"0","_rounding":"0"},"LUF":{"_digits":"0","_rounding":"0"},"LYD":{"_digits":"3","_rounding":"0"},"MGA":{"_digits":"0","_rounding":"0"},"MGF":{"_digits":"0","_rounding":"0"},"MMK":{"_digits":"0","_rounding":"0"},"MNT":{"_digits":"0","_rounding":"0"},"MRO":{"_digits":"0","_rounding":"0"},"MUR":{"_digits":"0","_rounding":"0"},"NOK":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"OMR":{"_digits":"3","_rounding":"0"},"PKR":{"_digits":"0","_rounding":"0"},"PYG":{"_digits":"0","_rounding":"0"},"RSD":{"_digits":"0","_rounding":"0"},"RWF":{"_digits":"0","_rounding":"0"},"SEK":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"SLL":{"_digits":"0","_rounding":"0"},"SOS":{"_digits":"0","_rounding":"0"},"STD":{"_digits":"0","_rounding":"0"},"SYP":{"_digits":"0","_rounding":"0"},"TMM":{"_digits":"0","_rounding":"0"},"TND":{"_digits":"3","_rounding":"0"},"TRL":{"_digits":"0","_rounding":"0"},"TWD":{"_cashDigits":"0","_cashRounding":"0","_digits":"2","_rounding":"0"},"TZS":{"_digits":"0","_rounding":"0"},"UGX":{"_digits":"0","_rounding":"0"},"UYI":{"_digits":"0","_rounding":"0"},"UZS":{"_digits":"0","_rounding":"0"},"VND":{"_digits":"0","_rounding":"0"},"VUV":{"_digits":"0","_rounding":"0"},"XAF":{"_digits":"0","_rounding":"0"},"XOF":{"_digits":"0","_rounding":"0"},"XPF":{"_digits":"0","_rounding":"0"},"YER":{"_digits":"0","_rounding":"0"},"ZMK":{"_digits":"0","_rounding":"0"},"ZWD":{"_digits":"0","_rounding":"0"}},"region":{"AC":"SHP","AD":"EUR","AE":"AED","AF":"AFN","AG":"XCD","AI":"XCD","AL":"ALL","AM":"AMD","AO":"AOA","AR":"ARS","AS":"USD","AT":"EUR","AU":"AUD","AW":"AWG","AX":"EUR","AZ":"AZN","BA":"BAM","BB":"BBD","BD":"BDT","BE":"EUR","BF":"XOF","BG":"BGN","BH":"BHD","BI":"BIF","BJ":"XOF","BL":"EUR","BM":"BMD","BN":"BND","BO":"BOB","BQ":"USD","BR":"BRL","BS":"BSD","BT":"BTN","BV":"NOK","BW":"BWP","BY":"BYN","BZ":"BZD","CA":"CAD","CC":"AUD","CD":"CDF","CF":"XAF","CG":"XAF","CH":"CHF","CI":"XOF","CK":"NZD","CL":"CLP","CM":"XAF","CN":"CNH","CO":"COP","CR":"CRC","CU":"CUC","CV":"CVE","CW":"ANG","CX":"AUD","CY":"EUR","CZ":"CZK","DE":"EUR","DG":"USD","DJ":"DJF","DK":"DKK","DM":"XCD","DO":"DOP","DZ":"DZD","EA":"EUR","EC":"USD","EE":"EUR","EG":"EGP","EH":"MAD","ER":"ERN","ES":"EUR","ET":"ETB","EU":"EUR","FI":"EUR","FJ":"FJD","FK":"FKP","FM":"USD","FO":"DKK","FR":"EUR","GA":"XAF","GB":"GBP","GD":"XCD","GE":"GEL","GF":"EUR","GG":"GBP","GH":"GHS","GI":"GIP","GL":"DKK","GM":"GMD","GN":"GNF","GP":"EUR","GQ":"XAF","GR":"EUR","GS":"GBP","GT":"GTQ","GU":"USD","GW":"XOF","GY":"GYD","HK":"HKD","HM":"AUD","HN":"HNL","HR":"HRK","HT":"USD","HU":"HUF","IC":"EUR","ID":"IDR","IE":"EUR","IL":"ILS","IM":"GBP","IN":"INR","IO":"USD","IQ":"IQD","IR":"IRR","IS":"ISK","IT":"EUR","JE":"GBP","JM":"JMD","JO":"JOD","JP":"JPY","KE":"KES","KG":"KGS","KH":"KHR","KI":"AUD","KM":"KMF","KN":"XCD","KP":"KPW","KR":"KRW","KW":"KWD","KY":"KYD","KZ":"KZT","LA":"LAK","LB":"LBP","LC":"XCD","LI":"CHF","LK":"LKR","LR":"LRD","LS":"LSL","LT":"EUR","LU":"EUR","LV":"EUR","LY":"LYD","MA":"MAD","MC":"EUR","MD":"MDL","ME":"EUR","MF":"EUR","MG":"MGA","MH":"USD","MK":"MKD","ML":"XOF","MM":"MMK","MN":"MNT","MO":"MOP","MP":"USD","MQ":"EUR","MR":"MRO","MS":"XCD","MT":"EUR","MU":"MUR","MV":"MVR","MW":"MWK","MX":"MXN","MY":"MYR","MZ":"MZN","NA":"NAD","NC":"XPF","NE":"XOF","NF":"AUD","NG":"NGN","NI":"NIO","NL":"EUR","NO":"NOK","NP":"NPR","NR":"AUD","NU":"NZD","NZ":"NZD","OM":"OMR","PA":"USD","PE":"PEN","PF":"XPF","PG":"PGK","PH":"PHP","PK":"PKR","PL":"PLN","PM":"EUR","PN":"NZD","PR":"USD","PS":"JOD","PT":"EUR","PW":"USD","PY":"PYG","QA":"QAR","RE":"EUR","RO":"RON","RS":"RSD","RU":"RUB","RW":"RWF","SA":"SAR","SB":"SBD","SC":"SCR","SD":"SDG","SE":"SEK","SG":"SGD","SH":"SHP","SI":"EUR","SJ":"NOK","SK":"EUR","SL":"SLL","SM":"EUR","SN":"XOF","SO":"SOS","SR":"SRD","SS":"SSP","ST":"STN","SV":"USD","SX":"ANG","SY":"SYP","SZ":"SZL","TA":"GBP","TC":"USD","TD":"XAF","TF":"EUR","TG":"XOF","TH":"THB","TJ":"TJS","TK":"NZD","TL":"USD","TM":"TMT","TN":"TND","TO":"TOP","TR":"TRY","TT":"TTD","TV":"AUD","TW":"TWD","TZ":"TZS","UA":"UAH","UG":"UGX","UM":"USD","US":"USD","UY":"UYU","UZ":"UZS","VA":"EUR","VC":"XCD","VE":"VEF","VG":"USD","VI":"USD","VN":"VND","VU":"VUV","WF":"XPF","WS":"WST","XK":"EUR","YE":"YER","YT":"EUR","ZA":"ZAR","ZM":"ZMW","ZW":"USD"}},"numbers":{"numberingSystems":{"adlm":{"_digits":"𞥐𞥑𞥒𞥓𞥔𞥕𞥖𞥗𞥘𞥙","_type":"numeric"},"ahom":{"_digits":"𑜰𑜱𑜲𑜳𑜴𑜵𑜶𑜷𑜸𑜹","_type":"numeric"},"arab":{"_digits":"٠١٢٣٤٥٦٧٨٩","_type":"numeric"},"arabext":{"_digits":"۰۱۲۳۴۵۶۷۸۹","_type":"numeric"},"armn":{"_rules":"armenian-upper","_type":"algorithmic"},"armnlow":{"_rules":"armenian-lower","_type":"algorithmic"},"bali":{"_digits":"᭐᭑᭒᭓᭔᭕᭖᭗᭘᭙","_type":"numeric"},"beng":{"_digits":"০১২৩৪৫৬৭৮৯","_type":"numeric"},"bhks":{"_digits":"𑱐𑱑𑱒𑱓𑱔𑱕𑱖𑱗𑱘𑱙","_type":"numeric"},"brah":{"_digits":"𑁦𑁧𑁨𑁩𑁪𑁫𑁬𑁭𑁮𑁯","_type":"numeric"},"cakm":{"_digits":"𑄶𑄷𑄸𑄹𑄺𑄻𑄼𑄽𑄾𑄿","_type":"numeric"},"cham":{"_digits":"꩐꩑꩒꩓꩔꩕꩖꩗꩘꩙","_type":"numeric"},"cyrl":{"_rules":"cyrillic-lower","_type":"algorithmic"},"deva":{"_digits":"०१२३४५६७८९","_type":"numeric"},"ethi":{"_rules":"ethiopic","_type":"algorithmic"},"fullwide":{"_digits":"０１２３４５６７８９","_type":"numeric"},"geor":{"_rules":"georgian","_type":"algorithmic"},"gonm":{"_digits":"𑵐𑵑𑵒𑵓𑵔𑵕𑵖𑵗𑵘𑵙","_type":"numeric"},"grek":{"_rules":"greek-upper","_type":"algorithmic"},"greklow":{"_rules":"greek-lower","_type":"algorithmic"},"gujr":{"_digits":"૦૧૨૩૪૫૬૭૮૯","_type":"numeric"},"guru":{"_digits":"੦੧੨੩੪੫੬੭੮੯","_type":"numeric"},"hanidays":{"_rules":"zh/SpelloutRules/spellout-numbering-days","_type":"algorithmic"},"hanidec":{"_digits":"〇一二三四五六七八九","_type":"numeric"},"hans":{"_rules":"zh/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"hansfin":{"_rules":"zh/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"hant":{"_rules":"zh_Hant/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"hantfin":{"_rules":"zh_Hant/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"hebr":{"_rules":"hebrew","_type":"algorithmic"},"hmng":{"_digits":"𖭐𖭑𖭒𖭓𖭔𖭕𖭖𖭗𖭘𖭙","_type":"numeric"},"java":{"_digits":"꧐꧑꧒꧓꧔꧕꧖꧗꧘꧙","_type":"numeric"},"jpan":{"_rules":"ja/SpelloutRules/spellout-cardinal","_type":"algorithmic"},"jpanfin":{"_rules":"ja/SpelloutRules/spellout-cardinal-financial","_type":"algorithmic"},"kali":{"_digits":"꤀꤁꤂꤃꤄꤅꤆꤇꤈꤉","_type":"numeric"},"khmr":{"_digits":"០១២៣៤៥៦៧៨៩","_type":"numeric"},"knda":{"_digits":"೦೧೨೩೪೫೬೭೮೯","_type":"numeric"},"lana":{"_digits":"᪀᪁᪂᪃᪄᪅᪆᪇᪈᪉","_type":"numeric"},"lanatham":{"_digits":"᪐᪑᪒᪓᪔᪕᪖᪗᪘᪙","_type":"numeric"},"laoo":{"_digits":"໐໑໒໓໔໕໖໗໘໙","_type":"numeric"},"latn":{"_digits":"0123456789","_type":"numeric"},"lepc":{"_digits":"᱀᱁᱂᱃᱄᱅᱆᱇᱈᱉","_type":"numeric"},"limb":{"_digits":"᥆᥇᥈᥉᥊᥋᥌᥍᥎᥏","_type":"numeric"},"mathbold":{"_digits":"𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗","_type":"numeric"},"mathdbl":{"_digits":"𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡","_type":"numeric"},"mathmono":{"_digits":"𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿","_type":"numeric"},"mathsanb":{"_digits":"𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵","_type":"numeric"},"mathsans":{"_digits":"𝟢𝟣𝟤𝟥𝟦𝟧𝟨𝟩𝟪𝟫","_type":"numeric"},"mlym":{"_digits":"൦൧൨൩൪൫൬൭൮൯","_type":"numeric"},"modi":{"_digits":"𑙐𑙑𑙒𑙓𑙔𑙕𑙖𑙗𑙘𑙙","_type":"numeric"},"mong":{"_digits":"᠐᠑᠒᠓᠔᠕᠖᠗᠘᠙","_type":"numeric"},"mroo":{"_digits":"𖩠𖩡𖩢𖩣𖩤𖩥𖩦𖩧𖩨𖩩","_type":"numeric"},"mtei":{"_digits":"꯰꯱꯲꯳꯴꯵꯶꯷꯸꯹","_type":"numeric"},"mymr":{"_digits":"၀၁၂၃၄၅၆၇၈၉","_type":"numeric"},"mymrshan":{"_digits":"႐႑႒႓႔႕႖႗႘႙","_type":"numeric"},"mymrtlng":{"_digits":"꧰꧱꧲꧳꧴꧵꧶꧷꧸꧹","_type":"numeric"},"newa":{"_digits":"𑑐𑑑𑑒𑑓𑑔𑑕𑑖𑑗𑑘𑑙","_type":"numeric"},"nkoo":{"_digits":"߀߁߂߃߄߅߆߇߈߉","_type":"numeric"},"olck":{"_digits":"᱐᱑᱒᱓᱔᱕᱖᱗᱘᱙","_type":"numeric"},"orya":{"_digits":"୦୧୨୩୪୫୬୭୮୯","_type":"numeric"},"osma":{"_digits":"𐒠𐒡𐒢𐒣𐒤𐒥𐒦𐒧𐒨𐒩","_type":"numeric"},"roman":{"_rules":"roman-upper","_type":"algorithmic"},"romanlow":{"_rules":"roman-lower","_type":"algorithmic"},"saur":{"_digits":"꣐꣑꣒꣓꣔꣕꣖꣗꣘꣙","_type":"numeric"},"shrd":{"_digits":"𑇐𑇑𑇒𑇓𑇔𑇕𑇖𑇗𑇘𑇙","_type":"numeric"},"sind":{"_digits":"𑋰𑋱𑋲𑋳𑋴𑋵𑋶𑋷𑋸𑋹","_type":"numeric"},"sinh":{"_digits":"෦෧෨෩෪෫෬෭෮෯","_type":"numeric"},"sora":{"_digits":"𑃰𑃱𑃲𑃳𑃴𑃵𑃶𑃷𑃸𑃹","_type":"numeric"},"sund":{"_digits":"᮰᮱᮲᮳᮴᮵᮶᮷᮸᮹","_type":"numeric"},"takr":{"_digits":"𑛀𑛁𑛂𑛃𑛄𑛅𑛆𑛇𑛈𑛉","_type":"numeric"},"talu":{"_digits":"᧐᧑᧒᧓᧔᧕᧖᧗᧘᧙","_type":"numeric"},"taml":{"_rules":"tamil","_type":"algorithmic"},"tamldec":{"_digits":"௦௧௨௩௪௫௬௭௮௯","_type":"numeric"},"telu":{"_digits":"౦౧౨౩౪౫౬౭౮౯","_type":"numeric"},"thai":{"_digits":"๐๑๒๓๔๕๖๗๘๙","_type":"numeric"},"tibt":{"_digits":"༠༡༢༣༤༥༦༧༨༩","_type":"numeric"},"tirh":{"_digits":"𑓐𑓑𑓒𑓓𑓔𑓕𑓖𑓗𑓘𑓙","_type":"numeric"},"vaii":{"_digits":"꘠꘡꘢꘣꘤꘥꘦꘧꘨꘩","_type":"numeric"},"wara":{"_digits":"𑣠𑣡𑣢𑣣𑣤𑣥𑣦𑣧𑣨𑣩","_type":"numeric"}}}}}}}`},
		{lang: "es-MX", reg: "US", scope: "dateFields", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"es-MX","language":"es-MX","region":"US","categories":{"dateFields":{"day":{"displayName":"día","relative-type--1":"ayer","relative-type--2":"anteayer","relative-type-0":"hoy","relative-type-1":"mañana","relative-type-2":"pasado mañana","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} día","relativeTimePattern-count-other":"dentro de {0} días"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} día","relativeTimePattern-count-other":"hace {0} días"}},"hour":{"displayName":"hora","relative-type-0":"esta hora","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} hora","relativeTimePattern-count-other":"dentro de {0} horas"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} hora","relativeTimePattern-count-other":"hace {0} horas"}},"minute":{"displayName":"minuto","relative-type-0":"este minuto","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} minuto","relativeTimePattern-count-other":"dentro de {0} minutos"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} minuto","relativeTimePattern-count-other":"hace {0} minutos"}},"month":{"displayName":"mes","relative-type--1":"el mes pasado","relative-type-0":"este mes","relative-type-1":"el mes próximo","relativeTime-type-future":{"relativeTimePattern-count-one":"en {0} mes","relativeTimePattern-count-other":"en {0} meses"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} mes","relativeTimePattern-count-other":"hace {0} meses"}},"second":{"displayName":"segundo","relative-type-0":"ahora","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} segundo","relativeTimePattern-count-other":"dentro de {0} segundos"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} segundo","relativeTimePattern-count-other":"hace {0} segundos"}},"year":{"displayName":"año","relative-type--1":"el año pasado","relative-type-0":"este año","relative-type-1":"el año próximo","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} año","relativeTimePattern-count-other":"dentro de {0} años"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} año","relativeTimePattern-count-other":"hace {0} años"}}}}}}`},
		{lang: "en", reg: "US", scope: "dates,dateFields", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"en","language":"en","region":"US","categories":{"dateFields":{"year" : {"displayName" : "year","relative-type-0" : "this year","relative-type-1" : "next year","relative-type--1" : "last year","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} years","relativeTimePattern-count-one" : "in {0} year"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} years ago","relativeTimePattern-count-one" : "{0} year ago"}},"month" : {"displayName" : "month","relative-type-0" : "this month","relative-type-1" : "next month","relative-type--1" : "last month","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} months","relativeTimePattern-count-one" : "in {0} month"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} months ago","relativeTimePattern-count-one" : "{0} month ago"}},"day" : {"displayName" : "day","relative-type-0" : "today","relative-type-1" : "tomorrow","relative-type--1" : "yesterday","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} days","relativeTimePattern-count-one" : "in {0} day"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} days ago","relativeTimePattern-count-one" : "{0} day ago"}},"hour" : {"displayName" : "hour","relative-type-0" : "this hour","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} hours","relativeTimePattern-count-one" : "in {0} hour"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} hours ago","relativeTimePattern-count-one" : "{0} hour ago"}},"minute" : {"displayName" : "minute","relative-type-0" : "this minute","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} minutes","relativeTimePattern-count-one" : "in {0} minute"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} minutes ago","relativeTimePattern-count-one" : "{0} minute ago"}},"second" : {"displayName" : "second","relative-type-0" : "now","relativeTime-type-future" : {"relativeTimePattern-count-other" : "in {0} seconds","relativeTimePattern-count-one" : "in {0} second"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0} seconds ago","relativeTimePattern-count-one" : "{0} second ago"}}},"dates":{"dayPeriodsFormat" : {"narrow" : [ "a", "p" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"daysStandalone" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"eras" : {"narrow" : [ "B", "A" ],"abbreviated" : [ "BC", "AD" ],"wide" : [ "Before Christ", "Anno Domini" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "M/d/yy","medium" : "MMM d, y","long" : "MMMM d, y","full" : "EEEE, MMMM d, y"},"timeFormats" : {"short" : "h:mm a","medium" : "h:mm:ss a","long" : "h:mm:ss a z","full" : "h:mm:ss a zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'at' {0}","full" : "{1} 'at' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{0} {1}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{0} {1}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH – HH"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M – M"},"Md" : {"d" : "M/d – M/d","M" : "M/d – M/d"},"MEd" : {"d" : "E, M/d – E, M/d","M" : "E, M/d – E, M/d"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "MMM d – d","M" : "MMM d – MMM d"},"MMMEd" : {"d" : "E, MMM d – E, MMM d","M" : "E, MMM d – E, MMM d"},"y" : {"y" : "y – y"},"yM" : {"y" : "M/y – M/y","M" : "M/y – M/y"},"yMd" : {"d" : "M/d/y – M/d/y","y" : "M/d/y – M/d/y","M" : "M/d/y – M/d/y"},"yMEd" : {"d" : "E, M/d/y – E, M/d/y","y" : "E, M/d/y – E, M/d/y","M" : "E, M/d/y – E, M/d/y"},"yMMM" : {"y" : "MMM y – MMM y","M" : "MMM – MMM y"},"yMMMd" : {"d" : "MMM d – d, y","y" : "MMM d, y – MMM d, y","M" : "MMM d – MMM d, y"},"yMMMEd" : {"d" : "E, MMM d – E, MMM d, y","y" : "E, MMM d, y – E, MMM d, y","M" : "E, MMM d – E, MMM d, y"},"yMMMM" : {"y" : "MMMM y – MMMM y","M" : "MMMM – MMMM y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "d E","Ehm" : "E h:mm a","EHm" : "E HH:mm","Ehms" : "E h:mm:ss a","EHms" : "E HH:mm:ss","Gy" : "y G","GyMMM" : "MMM y G","GyMMMd" : "MMM d, y G","GyMMMEd" : "E, MMM d, y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "h:mm a zzzz","M" : "L","Md" : "M/d","MEd" : "E, M/d","MMM" : "LLL","MMMd" : "MMM d","MMMEd" : "E, MMM d","MMMMd" : "MMMM d","MMMMW-count-one" : "'week' W 'of' MMMM","MMMMW-count-other" : "'week' W 'of' MMMM","ms" : "mm:ss","y" : "y","yM" : "M/y","yMd" : "M/d/y","yMEd" : "E, M/d/y","yMMM" : "MMM y","yMMMd" : "MMM d, y","yMMMEd" : "E, MMM d, y","yMMMM" : "MMMM y","yQQQ" : "QQQ y","yQQQQ" : "QQQQ y","yw-count-one" : "'week' w 'of' Y","yw-count-other" : "'week' w 'of' Y"}}}}}}`},

		{lang: "zh-cn", reg: "TW", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh-Hans","language":"zh-cn","region":"TW","categories":{"plurals":{"pluralRules" : {"pluralRule-count-other" : " @integer 0~15, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},

		{lang: "zh-xxx", reg: "TW", scope: "plurals", wantedCode: http.StatusNotFound,
			wanted: `{"response":{"code":404,"message":"1 error occurred. Invalid locale 'zh-xxx'"}}`},
		{lang: "zh-cn", reg: "xxx", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh-Hans","language":"zh-cn","region":"xxx","categories":{"plurals":{"pluralRules" : {"pluralRule-count-other" : " @integer 0~15, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},

		{lang: "zh-xxx", reg: "TW", scope: "dates,dateFields", wantedCode: sgtnerror.StatusPartialSuccess.Code(),
			wanted: `{"response":{"code":207, "message":"Successful Partially"},"data":{"localeID":"zh-Hant","language":"zh-xxx","region":"TW","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"dayPeriodsStandalone" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"daysFormat" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"daysStandalone" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"monthsFormat" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"monthsStandalone" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"eras" : {"narrow" : [ "西元前", "西元" ],"abbreviated" : [ "西元前", "西元" ],"wide" : [ "西元前", "西元" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "y/M/d","medium" : "y年M月d日","long" : "y年M月d日","full" : "y年M月d日 EEEE"},"timeFormats" : {"short" : "ah:mm","medium" : "ah:mm:ss","long" : "ah:mm:ss [z]","full" : "ah:mm:ss [zzzz]"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d日至d日"},"H" : {"H" : "HH – HH"},"h" : {"a" : "ah時至ah時","h" : "ah時至h時"},"hm" : {"a" : "ah:mm至ah:mm","h" : "ah:mm至h:mm","m" : "ah:mm至h:mm"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "ah:mm至ah:mm [v]","h" : "ah:mm至h:mm [v]","m" : "ah:mm至h:mm [v]"},"Hmv" : {"H" : "HH:mm – HH:mm [v]","m" : "HH:mm – HH:mm [v]"},"hv" : {"a" : "ah時至ah時 [v]","h" : "ah時至h時 [v]"},"Hv" : {"H" : "HH – HH [v]"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M月至M月"},"Md" : {"d" : "M/d至M/d","M" : "M/d至M/d"},"MEd" : {"d" : "M/dE至M/dE","M" : "M/dE至M/dE"},"MMM" : {"M" : "LLL至LLL"},"MMMd" : {"d" : "M月d日至d日","M" : "M月d日至M月d日"},"MMMEd" : {"d" : "M月d日E至d日E","M" : "M月d日E至M月d日E"},"MMMM" : {"M" : "LLLL至LLLL"},"y" : {"y" : "y至y"},"yM" : {"y" : "y/M至y/M","M" : "y/M至y/M"},"yMd" : {"d" : "y/M/d至y/M/d","y" : "y/M/d至y/M/d","M" : "y/M/d至y/M/d"},"yMEd" : {"d" : "y/M/dE至y/M/dE","y" : "y/M/dE至y/M/dE","M" : "y/M/dE至y/M/dE"},"yMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"},"yMMMd" : {"d" : "y年M月d日至d日","y" : "y年M月d日至y年M月d日","M" : "y年M月d日至M月d日"},"yMMMEd" : {"d" : "y年M月d日E至M月d日E","y" : "y年M月d日E至y年M月d日E","M" : "y年M月d日E至M月d日E"},"yMMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"}},"availableFormats" : {"Bh" : "Bh時","Bhm" : "Bh:mm","Bhms" : "Bh:mm:ss","d" : "d日","E" : "ccc","EBhm" : "E Bh:mm","EBhms" : "E Bh:mm:ss","Ed" : "d E","Ehm" : "E ah:mm","EHm" : "E HH:mm","Ehms" : "E ah:mm:ss","EHms" : "E HH:mm:ss","Gy" : "Gy年","GyMMM" : "Gy年M月","GyMMMd" : "Gy年M月d日","GyMMMEd" : "Gy年M月d日 E","H" : "H時","h" : "ah時","hm" : "ah:mm","Hm" : "HH:mm","hms" : "ah:mm:ss","Hms" : "HH:mm:ss","hmsv" : "ah:mm:ss [v]","Hmsv" : "HH:mm:ss [v]","hmv" : "ah:mm [v]","Hmv" : "HH:mm [v]","hmz" : "ah:mm [zzzz]","M" : "M月","Md" : "M/d","MEd" : "M/d（E）","MMdd" : "MM/dd","MMM" : "LLL","MMMd" : "M月d日","MMMEd" : "M月d日 E","MMMMd" : "M月d日","MMMMW-count-other" : "MMM的第W週","ms" : "mm:ss","y" : "y年","yM" : "y/M","yMd" : "y/M/d","yMEd" : "y/M/d（E）","yMM" : "y/MM","yMMM" : "y年M月","yMMMd" : "y年M月d日","yMMMEd" : "y年M月d日 E","yMMMM" : "y年M月","yQQQ" : "y年QQQ","yQQQQ" : "y年QQQQ","yw-count-other" : "Y年的第w週"}}}}}}`},
		{lang: "zh-cn", reg: "xxx", scope: "dates,dateFields", wantedCode: sgtnerror.StatusPartialSuccess.Code(),
			wanted: `{"response":{"code":207, "message":"Successful Partially"},"data":{"localeID":"zh-Hans","language":"zh-cn","region":"xxx","categories":{"dateFields":{"year" : {"displayName" : "年","relative-type-0" : "今年","relative-type-1" : "明年","relative-type--1" : "去年","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}年后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}年前"}},"month" : {"displayName" : "月","relative-type-0" : "本月","relative-type-1" : "下个月","relative-type--1" : "上个月","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}个月后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}个月前"}},"day" : {"displayName" : "日","relative-type-2" : "后天","relative-type-0" : "今天","relative-type-1" : "明天","relative-type--2" : "前天","relative-type--1" : "昨天","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}天后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}天前"}},"hour" : {"displayName" : "小时","relative-type-0" : "这一时间 / 此时","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}小时后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}小时前"}},"minute" : {"displayName" : "分钟","relative-type-0" : "此刻","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}分钟后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}分钟前"}},"second" : {"displayName" : "秒","relative-type-0" : "现在","relativeTime-type-future" : {"relativeTimePattern-count-other" : "{0}秒钟后"},"relativeTime-type-past" : {"relativeTimePattern-count-other" : "{0}秒钟前"}}}}}}`},

		{lang: "zh-xxx", reg: "TW", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh-Hant","language":"zh-xxx","region":"TW","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"dayPeriodsStandalone" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"daysFormat" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"daysStandalone" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"monthsFormat" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"monthsStandalone" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"eras" : {"narrow" : [ "西元前", "西元" ],"abbreviated" : [ "西元前", "西元" ],"wide" : [ "西元前", "西元" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "y/M/d","medium" : "y年M月d日","long" : "y年M月d日","full" : "y年M月d日 EEEE"},"timeFormats" : {"short" : "ah:mm","medium" : "ah:mm:ss","long" : "ah:mm:ss [z]","full" : "ah:mm:ss [zzzz]"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d日至d日"},"H" : {"H" : "HH – HH"},"h" : {"a" : "ah時至ah時","h" : "ah時至h時"},"hm" : {"a" : "ah:mm至ah:mm","h" : "ah:mm至h:mm","m" : "ah:mm至h:mm"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "ah:mm至ah:mm [v]","h" : "ah:mm至h:mm [v]","m" : "ah:mm至h:mm [v]"},"Hmv" : {"H" : "HH:mm – HH:mm [v]","m" : "HH:mm – HH:mm [v]"},"hv" : {"a" : "ah時至ah時 [v]","h" : "ah時至h時 [v]"},"Hv" : {"H" : "HH – HH [v]"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M月至M月"},"Md" : {"d" : "M/d至M/d","M" : "M/d至M/d"},"MEd" : {"d" : "M/dE至M/dE","M" : "M/dE至M/dE"},"MMM" : {"M" : "LLL至LLL"},"MMMd" : {"d" : "M月d日至d日","M" : "M月d日至M月d日"},"MMMEd" : {"d" : "M月d日E至d日E","M" : "M月d日E至M月d日E"},"MMMM" : {"M" : "LLLL至LLLL"},"y" : {"y" : "y至y"},"yM" : {"y" : "y/M至y/M","M" : "y/M至y/M"},"yMd" : {"d" : "y/M/d至y/M/d","y" : "y/M/d至y/M/d","M" : "y/M/d至y/M/d"},"yMEd" : {"d" : "y/M/dE至y/M/dE","y" : "y/M/dE至y/M/dE","M" : "y/M/dE至y/M/dE"},"yMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"},"yMMMd" : {"d" : "y年M月d日至d日","y" : "y年M月d日至y年M月d日","M" : "y年M月d日至M月d日"},"yMMMEd" : {"d" : "y年M月d日E至M月d日E","y" : "y年M月d日E至y年M月d日E","M" : "y年M月d日E至M月d日E"},"yMMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"}},"availableFormats" : {"Bh" : "Bh時","Bhm" : "Bh:mm","Bhms" : "Bh:mm:ss","d" : "d日","E" : "ccc","EBhm" : "E Bh:mm","EBhms" : "E Bh:mm:ss","Ed" : "d E","Ehm" : "E ah:mm","EHm" : "E HH:mm","Ehms" : "E ah:mm:ss","EHms" : "E HH:mm:ss","Gy" : "Gy年","GyMMM" : "Gy年M月","GyMMMd" : "Gy年M月d日","GyMMMEd" : "Gy年M月d日 E","H" : "H時","h" : "ah時","hm" : "ah:mm","Hm" : "HH:mm","hms" : "ah:mm:ss","Hms" : "HH:mm:ss","hmsv" : "ah:mm:ss [v]","Hmsv" : "HH:mm:ss [v]","hmv" : "ah:mm [v]","Hmv" : "HH:mm [v]","hmz" : "ah:mm [zzzz]","M" : "M月","Md" : "M/d","MEd" : "M/d（E）","MMdd" : "MM/dd","MMM" : "LLL","MMMd" : "M月d日","MMMEd" : "M月d日 E","MMMMd" : "M月d日","MMMMW-count-other" : "MMM的第W週","ms" : "mm:ss","y" : "y年","yM" : "y/M","yMd" : "y/M/d","yMEd" : "y/M/d（E）","yMM" : "y/MM","yMMM" : "y年M月","yMMMd" : "y年M月d日","yMMMEd" : "y年M月d日 E","yMMMM" : "y年M月","yQQQ" : "y年QQQ","yQQQQ" : "y年QQQQ","yw-count-other" : "Y年的第w週"}}}}}}`},
		{lang: "zh-cn", reg: "xxx", scope: "dates", wantedCode: http.StatusNotFound,
			wanted: `{"response":{"code":404,"message":"1 error occurred. Can't get a locale ID with 'zh-cn' and 'xxx'"}}`},

		{lang: "zh-cn", reg: "TW", scope: "invalid", wantedCode: http.StatusBadRequest,
			wanted: `{"response":{"code":400,"message":"1 error occurred. 'invalid' is invalid"}}`},
		{lang: "zh-cn", reg: "TW", scope: "dates,invalid", wantedCode: sgtnerror.StatusPartialSuccess.Code(),
			wanted: `{"response":{"code":207, "message":"Successful Partially"},"data":{"localeID":"zh-Hant","language":"zh-cn","region":"TW","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"dayPeriodsStandalone" : {"narrow" : [ "上午", "下午" ],"abbreviated" : [ "上午", "下午" ],"wide" : [ "上午", "下午" ]},"daysFormat" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"daysStandalone" : {"narrow" : [ "日", "一", "二", "三", "四", "五", "六" ],"abbreviated" : [ "週日", "週一", "週二", "週三", "週四", "週五", "週六" ],"wide" : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" ],"short" : [ "日", "一", "二", "三", "四", "五", "六" ]},"monthsFormat" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"monthsStandalone" : {"narrow" : [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],"abbreviated" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ],"wide" : [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ]},"eras" : {"narrow" : [ "西元前", "西元" ],"abbreviated" : [ "西元前", "西元" ],"wide" : [ "西元前", "西元" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "y/M/d","medium" : "y年M月d日","long" : "y年M月d日","full" : "y年M月d日 EEEE"},"timeFormats" : {"short" : "ah:mm","medium" : "ah:mm:ss","long" : "ah:mm:ss [z]","full" : "ah:mm:ss [zzzz]"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d日至d日"},"H" : {"H" : "HH – HH"},"h" : {"a" : "ah時至ah時","h" : "ah時至h時"},"hm" : {"a" : "ah:mm至ah:mm","h" : "ah:mm至h:mm","m" : "ah:mm至h:mm"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "ah:mm至ah:mm [v]","h" : "ah:mm至h:mm [v]","m" : "ah:mm至h:mm [v]"},"Hmv" : {"H" : "HH:mm – HH:mm [v]","m" : "HH:mm – HH:mm [v]"},"hv" : {"a" : "ah時至ah時 [v]","h" : "ah時至h時 [v]"},"Hv" : {"H" : "HH – HH [v]"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M月至M月"},"Md" : {"d" : "M/d至M/d","M" : "M/d至M/d"},"MEd" : {"d" : "M/dE至M/dE","M" : "M/dE至M/dE"},"MMM" : {"M" : "LLL至LLL"},"MMMd" : {"d" : "M月d日至d日","M" : "M月d日至M月d日"},"MMMEd" : {"d" : "M月d日E至d日E","M" : "M月d日E至M月d日E"},"MMMM" : {"M" : "LLLL至LLLL"},"y" : {"y" : "y至y"},"yM" : {"y" : "y/M至y/M","M" : "y/M至y/M"},"yMd" : {"d" : "y/M/d至y/M/d","y" : "y/M/d至y/M/d","M" : "y/M/d至y/M/d"},"yMEd" : {"d" : "y/M/dE至y/M/dE","y" : "y/M/dE至y/M/dE","M" : "y/M/dE至y/M/dE"},"yMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"},"yMMMd" : {"d" : "y年M月d日至d日","y" : "y年M月d日至y年M月d日","M" : "y年M月d日至M月d日"},"yMMMEd" : {"d" : "y年M月d日E至M月d日E","y" : "y年M月d日E至y年M月d日E","M" : "y年M月d日E至M月d日E"},"yMMMM" : {"y" : "y年M月至y年M月","M" : "y年M月至M月"}},"availableFormats" : {"Bh" : "Bh時","Bhm" : "Bh:mm","Bhms" : "Bh:mm:ss","d" : "d日","E" : "ccc","EBhm" : "E Bh:mm","EBhms" : "E Bh:mm:ss","Ed" : "d E","Ehm" : "E ah:mm","EHm" : "E HH:mm","Ehms" : "E ah:mm:ss","EHms" : "E HH:mm:ss","Gy" : "Gy年","GyMMM" : "Gy年M月","GyMMMd" : "Gy年M月d日","GyMMMEd" : "Gy年M月d日 E","H" : "H時","h" : "ah時","hm" : "ah:mm","Hm" : "HH:mm","hms" : "ah:mm:ss","Hms" : "HH:mm:ss","hmsv" : "ah:mm:ss [v]","Hmsv" : "HH:mm:ss [v]","hmv" : "ah:mm [v]","Hmv" : "HH:mm [v]","hmz" : "ah:mm [zzzz]","M" : "M月","Md" : "M/d","MEd" : "M/d（E）","MMdd" : "MM/dd","MMM" : "LLL","MMMd" : "M月d日","MMMEd" : "M月d日 E","MMMMd" : "M月d日","MMMMW-count-other" : "MMM的第W週","ms" : "mm:ss","y" : "y年","yM" : "y/M","yMd" : "y/M/d","yMEd" : "y/M/d（E）","yMM" : "y/MM","yMMM" : "y年M月","yMMMd" : "y年M月d日","yMMMEd" : "y年M月d日 E","yMMMM" : "y年M月","yQQQ" : "y年QQQ","yQQQQ" : "y年QQQQ","yw-count-other" : "Y年的第w週"}}}}}}`},

		{lang: "pt", reg: "pt", scope: "dates,plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"","language":"pt","region":"pt","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "da manhã", "da tarde" ]},"dayPeriodsStandalone" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "manhã", "tarde" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 1,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/yy","medium" : "dd/MM/y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'às' {0}","full" : "{1} 'às' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d–d"},"H" : {"H" : "HH–HH"},"h" : {"a" : "h a – h a","h" : "h–h a"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h–h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M–M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "ccc, dd/MM – ccc, dd/MM","M" : "ccc, dd/MM – ccc, dd/MM"},"MMM" : {"M" : "MMM–MMM"},"MMMd" : {"d" : "d–d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "ccc, dd/MM – ccc, dd/MM","M" : "ccc, dd/MM – ccc, dd/MM"},"MMMMEd" : {"d" : "ccc, d 'de' MMMM – ccc, d 'de' MMMM","M" : "ccc, d 'de' MMMM – ccc, d 'de' MMMM"},"y" : {"y" : "y–y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "ccc, dd/MM/y – ccc, dd/MM/y","y" : "ccc, dd/MM/y – ccc, dd/MM/y","M" : "ccc, dd/MM/y – ccc, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM–MMM 'de' y"},"yMMMd" : {"d" : "d–d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, dd/MM – E, dd/MM/y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"},"yMMMMEd" : {"d" : "E, d 'de' MMMM – E, d 'de' MMMM 'de' y","y" : "E, d 'de' MMMM 'de' y – E, d 'de' MMMM 'de' y","M" : "E, d 'de' MMMM – E, d 'de' MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "dd/MM","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d/MM","MMMEd" : "E, d/MM","MMMMd" : "d 'de' MMMM","MMMMEd" : "ccc, d 'de' MMMM","MMMMW-count-one" : "W.'ª' 'semana' 'de' MMM","MMMMW-count-other" : "W.'ª' 'semana' 'de' MMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MM/y","yMMMd" : "d/MM/y","yMMMEd" : "E, d/MM/y","yMMMEEEEd" : "EEEE, d/MM/y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "ccc, d 'de' MMMM 'de' y","yQQQ" : "QQQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w.'ª' 'semana' 'de' Y","yw-count-other" : "w.'ª' 'semana' 'de' Y"}}},"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 0..1 @integer 0, 1 @decimal 0.0~1.5","pluralRule-count-other" : " @integer 2~17, 100, 1000, 10000, 100000, 1000000, … @decimal 2.0~3.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "pt", reg: "pt", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt","language":"pt","region":"pt","categories":{"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 0..1 @integer 0, 1 @decimal 0.0~1.5","pluralRule-count-other" : " @integer 2~17, 100, 1000, 10000, 100000, 1000000, … @decimal 2.0~3.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "pt", reg: "pt", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt-PT","language":"pt","region":"pt","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "da manhã", "da tarde" ]},"dayPeriodsStandalone" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "manhã", "tarde" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 1,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/yy","medium" : "dd/MM/y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'às' {0}","full" : "{1} 'às' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d–d"},"H" : {"H" : "HH–HH"},"h" : {"a" : "h a – h a","h" : "h–h a"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h–h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M–M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "ccc, dd/MM – ccc, dd/MM","M" : "ccc, dd/MM – ccc, dd/MM"},"MMM" : {"M" : "MMM–MMM"},"MMMd" : {"d" : "d–d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "ccc, dd/MM – ccc, dd/MM","M" : "ccc, dd/MM – ccc, dd/MM"},"MMMMEd" : {"d" : "ccc, d 'de' MMMM – ccc, d 'de' MMMM","M" : "ccc, d 'de' MMMM – ccc, d 'de' MMMM"},"y" : {"y" : "y–y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "ccc, dd/MM/y – ccc, dd/MM/y","y" : "ccc, dd/MM/y – ccc, dd/MM/y","M" : "ccc, dd/MM/y – ccc, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM–MMM 'de' y"},"yMMMd" : {"d" : "d–d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, dd/MM – E, dd/MM/y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"},"yMMMMEd" : {"d" : "E, d 'de' MMMM – E, d 'de' MMMM 'de' y","y" : "E, d 'de' MMMM 'de' y – E, d 'de' MMMM 'de' y","M" : "E, d 'de' MMMM – E, d 'de' MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "dd/MM","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d/MM","MMMEd" : "E, d/MM","MMMMd" : "d 'de' MMMM","MMMMEd" : "ccc, d 'de' MMMM","MMMMW-count-one" : "W.'ª' 'semana' 'de' MMM","MMMMW-count-other" : "W.'ª' 'semana' 'de' MMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MM/y","yMMMd" : "d/MM/y","yMMMEd" : "E, d/MM/y","yMMMEEEEd" : "EEEE, d/MM/y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "ccc, d 'de' MMMM 'de' y","yQQQ" : "QQQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w.'ª' 'semana' 'de' Y","yw-count-other" : "w.'ª' 'semana' 'de' Y"}}}}}}`},
		{lang: "pt", reg: "br", scope: "dates,plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt","language":"pt","region":"br","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/y","medium" : "d 'de' MMM 'de' y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH'h' - HH'h'"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M – M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "E, dd/MM – E, dd/MM","M" : "E, dd/MM – E, dd/MM"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "d – d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "E, d – E, d 'de' MMM","M" : "E, d 'de' MMM – E, d 'de' MMM"},"y" : {"y" : "y – y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "E, dd/MM/y – E, dd/MM/y","y" : "E, dd/MM/y – E, dd/MM/y","M" : "E, dd/MM/y – E, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM – MMM 'de' y"},"yMMMd" : {"d" : "d – d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, d – E, d 'de' MMM 'de' y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "d/M","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d 'de' MMM","MMMEd" : "E, d 'de' MMM","MMMMd" : "d 'de' MMMM","MMMMEd" : "E, d 'de' MMMM","MMMMW-count-one" : "W'ª' 'semana' 'de' MMMM","MMMMW-count-other" : "W'ª' 'semana' 'de' MMMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MMM 'de' y","yMMMd" : "d 'de' MMM 'de' y","yMMMEd" : "E, d 'de' MMM 'de' y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "E, d 'de' MMMM 'de' y","yQQQ" : "QQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w'ª' 'semana' 'de' Y","yw-count-other" : "w'ª' 'semana' 'de' Y"}}},"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 0..1 @integer 0, 1 @decimal 0.0~1.5","pluralRule-count-other" : " @integer 2~17, 100, 1000, 10000, 100000, 1000000, … @decimal 2.0~3.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "pt", reg: "br", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt","language":"pt","region":"br","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/y","medium" : "d 'de' MMM 'de' y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH'h' - HH'h'"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M – M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "E, dd/MM – E, dd/MM","M" : "E, dd/MM – E, dd/MM"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "d – d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "E, d – E, d 'de' MMM","M" : "E, d 'de' MMM – E, d 'de' MMM"},"y" : {"y" : "y – y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "E, dd/MM/y – E, dd/MM/y","y" : "E, dd/MM/y – E, dd/MM/y","M" : "E, dd/MM/y – E, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM – MMM 'de' y"},"yMMMd" : {"d" : "d – d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, d – E, d 'de' MMM 'de' y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "d/M","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d 'de' MMM","MMMEd" : "E, d 'de' MMM","MMMMd" : "d 'de' MMMM","MMMMEd" : "E, d 'de' MMMM","MMMMW-count-one" : "W'ª' 'semana' 'de' MMMM","MMMMW-count-other" : "W'ª' 'semana' 'de' MMMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MMM 'de' y","yMMMd" : "d 'de' MMM 'de' y","yMMMEd" : "E, d 'de' MMM 'de' y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "E, d 'de' MMMM 'de' y","yQQQ" : "QQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w'ª' 'semana' 'de' Y","yw-count-other" : "w'ª' 'semana' 'de' Y"}}}}}}`},
		{lang: "pt", reg: "br", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt","language":"pt","region":"br","categories":{"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 0..1 @integer 0, 1 @decimal 0.0~1.5","pluralRule-count-other" : " @integer 2~17, 100, 1000, 10000, 100000, 1000000, … @decimal 2.0~3.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "pt-pt", reg: "br", scope: "dates,plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"","language":"pt-pt","region":"br","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/y","medium" : "d 'de' MMM 'de' y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH'h' - HH'h'"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M – M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "E, dd/MM – E, dd/MM","M" : "E, dd/MM – E, dd/MM"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "d – d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "E, d – E, d 'de' MMM","M" : "E, d 'de' MMM – E, d 'de' MMM"},"y" : {"y" : "y – y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "E, dd/MM/y – E, dd/MM/y","y" : "E, dd/MM/y – E, dd/MM/y","M" : "E, dd/MM/y – E, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM – MMM 'de' y"},"yMMMd" : {"d" : "d – d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, d – E, d 'de' MMM 'de' y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "d/M","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d 'de' MMM","MMMEd" : "E, d 'de' MMM","MMMMd" : "d 'de' MMMM","MMMMEd" : "E, d 'de' MMMM","MMMMW-count-one" : "W'ª' 'semana' 'de' MMMM","MMMMW-count-other" : "W'ª' 'semana' 'de' MMMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MMM 'de' y","yMMMd" : "d 'de' MMM 'de' y","yMMMEd" : "E, d 'de' MMM 'de' y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "E, d 'de' MMMM 'de' y","yQQQ" : "QQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w'ª' 'semana' 'de' Y","yw-count-other" : "w'ª' 'semana' 'de' Y"}}},"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 1 and v = 0 @integer 1","pluralRule-count-other" : " @integer 0, 2~16, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "pt-pt", reg: "br", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt","language":"pt-pt","region":"br","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"dayPeriodsStandalone" : {"narrow" : [ "AM", "PM" ],"abbreviated" : [ "AM", "PM" ],"wide" : [ "AM", "PM" ]},"daysFormat" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"daysStandalone" : {"narrow" : [ "D", "S", "T", "Q", "Q", "S", "S" ],"abbreviated" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ],"wide" : [ "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" ],"short" : [ "dom", "seg", "ter", "qua", "qui", "sex", "sáb" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez" ],"wide" : [ "janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro" ]},"eras" : {"narrow" : [ "a.C.", "d.C." ],"abbreviated" : [ "a.C.", "d.C." ],"wide" : [ "antes de Cristo", "depois de Cristo" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short" : "dd/MM/y","medium" : "d 'de' MMM 'de' y","long" : "d 'de' MMMM 'de' y","full" : "EEEE, d 'de' MMMM 'de' y"},"timeFormats" : {"short" : "HH:mm","medium" : "HH:mm:ss","long" : "HH:mm:ss z","full" : "HH:mm:ss zzzz"},"dateTimeFormats" : {"short" : "{1} {0}","medium" : "{1} {0}","long" : "{1} {0}","full" : "{1} {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{1} {0}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{1} {0}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH'h' - HH'h'"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} - {1}","M" : {"M" : "M – M"},"Md" : {"d" : "dd/MM – dd/MM","M" : "dd/MM – dd/MM"},"MEd" : {"d" : "E, dd/MM – E, dd/MM","M" : "E, dd/MM – E, dd/MM"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "d – d 'de' MMM","M" : "d 'de' MMM – d 'de' MMM"},"MMMEd" : {"d" : "E, d – E, d 'de' MMM","M" : "E, d 'de' MMM – E, d 'de' MMM"},"y" : {"y" : "y – y"},"yM" : {"y" : "MM/y – MM/y","M" : "MM/y – MM/y"},"yMd" : {"d" : "dd/MM/y – dd/MM/y","y" : "dd/MM/y – dd/MM/y","M" : "dd/MM/y – dd/MM/y"},"yMEd" : {"d" : "E, dd/MM/y – E, dd/MM/y","y" : "E, dd/MM/y – E, dd/MM/y","M" : "E, dd/MM/y – E, dd/MM/y"},"yMMM" : {"y" : "MMM 'de' y – MMM 'de' y","M" : "MMM – MMM 'de' y"},"yMMMd" : {"d" : "d – d 'de' MMM 'de' y","y" : "d 'de' MMM 'de' y – d 'de' MMM 'de' y","M" : "d 'de' MMM – d 'de' MMM 'de' y"},"yMMMEd" : {"d" : "E, d – E, d 'de' MMM 'de' y","y" : "E, d 'de' MMM 'de' y – E, d 'de' MMM 'de' y","M" : "E, d 'de' MMM – E, d 'de' MMM 'de' y"},"yMMMM" : {"y" : "MMMM 'de' y – MMMM 'de' y","M" : "MMMM – MMMM 'de' y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E, d","Ehm" : "E, h:mm a","EHm" : "E, HH:mm","Ehms" : "E, h:mm:ss a","EHms" : "E, HH:mm:ss","Gy" : "y G","GyMMM" : "MMM 'de' y G","GyMMMd" : "d 'de' MMM 'de' y G","GyMMMEd" : "E, d 'de' MMM 'de' y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "HH:mm zzzz","M" : "L","Md" : "d/M","MEd" : "E, dd/MM","MMdd" : "dd/MM","MMM" : "LLL","MMMd" : "d 'de' MMM","MMMEd" : "E, d 'de' MMM","MMMMd" : "d 'de' MMMM","MMMMEd" : "E, d 'de' MMMM","MMMMW-count-one" : "W'ª' 'semana' 'de' MMMM","MMMMW-count-other" : "W'ª' 'semana' 'de' MMMM","ms" : "mm:ss","y" : "y","yM" : "MM/y","yMd" : "dd/MM/y","yMEd" : "E, dd/MM/y","yMM" : "MM/y","yMMM" : "MMM 'de' y","yMMMd" : "d 'de' MMM 'de' y","yMMMEd" : "E, d 'de' MMM 'de' y","yMMMM" : "MMMM 'de' y","yMMMMd" : "d 'de' MMMM 'de' y","yMMMMEd" : "E, d 'de' MMMM 'de' y","yQQQ" : "QQQ 'de' y","yQQQQ" : "QQQQ 'de' y","yw-count-one" : "w'ª' 'semana' 'de' Y","yw-count-other" : "w'ª' 'semana' 'de' Y"}}}}}}`},
		{lang: "pt-pt", reg: "br", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"pt-PT","language":"pt-pt","region":"br","categories":{"plurals":{"pluralRules" : {"pluralRule-count-one" : "i = 1 and v = 0 @integer 1","pluralRule-count-other" : " @integer 0, 2~16, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "zh", reg: "ca", scope: "dates,plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"","language":"zh","region":"ca","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "a", "p" ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "a.m.", "p.m." ]},"dayPeriodsStandalone" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "a.m.", "p.m." ]},"daysFormat" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat." ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"daysStandalone" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat." ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"eras" : {"narrow" : [ "B", "A" ],"abbreviated" : [ "BC", "AD" ],"wide" : [ "Before Christ", "Anno Domini" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short-alt-variant" : "d/M/yy","short" : "y-MM-dd","medium" : "MMM d, y","long" : "MMMM d, y","full" : "EEEE, MMMM d, y"},"timeFormats" : {"short" : "h:mm a","medium" : "h:mm:ss a","long" : "h:mm:ss a z","full" : "h:mm:ss a zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'at' {0}","full" : "{1} 'at' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{0} {1}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{0} {1}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH – HH"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M – M"},"Md" : {"d" : "MM-dd – MM-dd","d-alt-variant" : "d/M – d/M","M-alt-variant" : "d/M – d/M","M" : "MM-dd – MM-dd"},"MEd" : {"d" : "E, MM-dd – E, MM-dd","d-alt-variant" : "E, d/M – E, d/M","M-alt-variant" : "E, d/M – E, d/M","M" : "E, MM-dd – E, MM-dd"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "MMM d – d","M" : "MMM d – MMM d"},"MMMEd" : {"d" : "E, MMM d – E, MMM d","M" : "E, MMM d – E, MMM d"},"y" : {"y" : "y – y"},"yM" : {"y-alt-variant" : "M/y – M/y","y" : "y-MM – y-MM","M-alt-variant" : "M/y – M/y","M" : "y-MM – y-MM"},"yMd" : {"y-alt-variant" : "d/M/y – d/M/y","d" : "y-MM-dd – y-MM-dd","d-alt-variant" : "d/M/y – d/M/y","y" : "y-MM-dd – y-MM-dd","M-alt-variant" : "d/M/y – d/M/y","M" : "y-MM-dd – y-MM-dd"},"yMEd" : {"y-alt-variant" : "E, d/M/y – E, d/M/y","d" : "E, y-MM-dd – E, y-MM-dd","d-alt-variant" : "E, d/M/y – E, d/M/y","y" : "E, y-MM-dd – E, y-MM-dd","M-alt-variant" : "E, d/M/y – E, d/M/y","M" : "E, y-MM-dd – E, y-MM-dd"},"yMMM" : {"y" : "MMM y – MMM y","M" : "MMM – MMM y"},"yMMMd" : {"d" : "MMM d – d, y","y" : "MMM d, y – MMM d, y","M" : "MMM d – MMM d, y"},"yMMMEd" : {"d" : "E, MMM d – E, MMM d, y","y" : "E, MMM d, y – E, MMM d, y","M" : "E, MMM d – E, MMM d, y"},"yMMMM" : {"y" : "MMMM y – MMMM y","M" : "MMMM – MMMM y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E d","Ehm" : "E h:mm a","EHm" : "E HH:mm","Ehms" : "E h:mm:ss a","EHms" : "E HH:mm:ss","Gy" : "y G","GyMMM" : "MMM y G","GyMMMd" : "MMM d, y G","GyMMMEd" : "E, MMM d, y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "h:mm a zzzz","M" : "L","Md" : "MM-dd","Md-alt-variant" : "d/M","MEd" : "E, MM-dd","MEd-alt-variant" : "E, d/M","MMdd" : "MM-dd","MMdd-alt-variant" : "dd/MM","MMM" : "LLL","MMMd" : "MMM d","MMMEd" : "E, MMM d","MMMMd" : "MMMM d","MMMMW-count-one" : "'week' W 'of' MMMM","MMMMW-count-other" : "'week' W 'of' MMMM","ms" : "mm:ss","y" : "y","yM" : "y-MM","yM-alt-variant" : "M/y","yMd" : "y-MM-dd","yMd-alt-variant" : "d/M/y","yMEd" : "E, y-MM-dd","yMEd-alt-variant" : "E, d/M/y","yMMM" : "MMM y","yMMMd" : "MMM d, y","yMMMEd" : "E, MMM d, y","yMMMM" : "MMMM y","yQQQ" : "QQQ y","yQQQQ" : "QQQQ y","yw-count-one" : "'week' w 'of' Y","yw-count-other" : "'week' w 'of' Y"}}},"plurals":{"pluralRules" : {"pluralRule-count-other" : " @integer 0~15, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{lang: "zh", reg: "ca", scope: "dates", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"en-CA","language":"zh","region":"ca","categories":{"dates":{"dayPeriodsFormat" : {"narrow" : [ "a", "p" ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "a.m.", "p.m." ]},"dayPeriodsStandalone" : {"narrow" : [ "a.m.", "p.m." ],"abbreviated" : [ "a.m.", "p.m." ],"wide" : [ "a.m.", "p.m." ]},"daysFormat" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat." ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"daysStandalone" : {"narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],"abbreviated" : [ "Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat." ],"wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],"short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]},"monthsFormat" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"monthsStandalone" : {"narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],"abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],"wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]},"eras" : {"narrow" : [ "B", "A" ],"abbreviated" : [ "BC", "AD" ],"wide" : [ "Before Christ", "Anno Domini" ]},"firstDayOfWeek" : 0,"weekendRange" : [ 6, 0 ],"dateFormats" : {"short-alt-variant" : "d/M/yy","short" : "y-MM-dd","medium" : "MMM d, y","long" : "MMMM d, y","full" : "EEEE, MMMM d, y"},"timeFormats" : {"short" : "h:mm a","medium" : "h:mm:ss a","long" : "h:mm:ss a z","full" : "h:mm:ss a zzzz"},"dateTimeFormats" : {"short" : "{1}, {0}","medium" : "{1}, {0}","long" : "{1} 'at' {0}","full" : "{1} 'at' {0}","appendItems" : {"Day" : "{0} ({2}: {1})","Day-Of-Week" : "{0} {1}","Era" : "{0} {1}","Hour" : "{0} ({2}: {1})","Minute" : "{0} ({2}: {1})","Month" : "{0} ({2}: {1})","Quarter" : "{0} ({2}: {1})","Second" : "{0} ({2}: {1})","Timezone" : "{0} {1}","Week" : "{0} ({2}: {1})","Year" : "{0} {1}"},"intervalFormats" : {"d" : {"d" : "d – d"},"h" : {"a" : "h a – h a","h" : "h – h a"},"H" : {"H" : "HH – HH"},"hm" : {"a" : "h:mm a – h:mm a","h" : "h:mm – h:mm a","m" : "h:mm – h:mm a"},"Hm" : {"H" : "HH:mm – HH:mm","m" : "HH:mm – HH:mm"},"hmv" : {"a" : "h:mm a – h:mm a v","h" : "h:mm – h:mm a v","m" : "h:mm – h:mm a v"},"Hmv" : {"H" : "HH:mm – HH:mm v","m" : "HH:mm – HH:mm v"},"hv" : {"a" : "h a – h a v","h" : "h – h a v"},"Hv" : {"H" : "HH – HH v"},"intervalFormatFallback" : "{0} – {1}","M" : {"M" : "M – M"},"Md" : {"d" : "MM-dd – MM-dd","d-alt-variant" : "d/M – d/M","M-alt-variant" : "d/M – d/M","M" : "MM-dd – MM-dd"},"MEd" : {"d" : "E, MM-dd – E, MM-dd","d-alt-variant" : "E, d/M – E, d/M","M-alt-variant" : "E, d/M – E, d/M","M" : "E, MM-dd – E, MM-dd"},"MMM" : {"M" : "MMM – MMM"},"MMMd" : {"d" : "MMM d – d","M" : "MMM d – MMM d"},"MMMEd" : {"d" : "E, MMM d – E, MMM d","M" : "E, MMM d – E, MMM d"},"y" : {"y" : "y – y"},"yM" : {"y-alt-variant" : "M/y – M/y","y" : "y-MM – y-MM","M-alt-variant" : "M/y – M/y","M" : "y-MM – y-MM"},"yMd" : {"y-alt-variant" : "d/M/y – d/M/y","d" : "y-MM-dd – y-MM-dd","d-alt-variant" : "d/M/y – d/M/y","y" : "y-MM-dd – y-MM-dd","M-alt-variant" : "d/M/y – d/M/y","M" : "y-MM-dd – y-MM-dd"},"yMEd" : {"y-alt-variant" : "E, d/M/y – E, d/M/y","d" : "E, y-MM-dd – E, y-MM-dd","d-alt-variant" : "E, d/M/y – E, d/M/y","y" : "E, y-MM-dd – E, y-MM-dd","M-alt-variant" : "E, d/M/y – E, d/M/y","M" : "E, y-MM-dd – E, y-MM-dd"},"yMMM" : {"y" : "MMM y – MMM y","M" : "MMM – MMM y"},"yMMMd" : {"d" : "MMM d – d, y","y" : "MMM d, y – MMM d, y","M" : "MMM d – MMM d, y"},"yMMMEd" : {"d" : "E, MMM d – E, MMM d, y","y" : "E, MMM d, y – E, MMM d, y","M" : "E, MMM d – E, MMM d, y"},"yMMMM" : {"y" : "MMMM y – MMMM y","M" : "MMMM – MMMM y"}},"availableFormats" : {"Bh" : "h B","Bhm" : "h:mm B","Bhms" : "h:mm:ss B","d" : "d","E" : "ccc","EBhm" : "E h:mm B","EBhms" : "E h:mm:ss B","Ed" : "E d","Ehm" : "E h:mm a","EHm" : "E HH:mm","Ehms" : "E h:mm:ss a","EHms" : "E HH:mm:ss","Gy" : "y G","GyMMM" : "MMM y G","GyMMMd" : "MMM d, y G","GyMMMEd" : "E, MMM d, y G","H" : "HH","h" : "h a","hm" : "h:mm a","Hm" : "HH:mm","hms" : "h:mm:ss a","Hms" : "HH:mm:ss","hmsv" : "h:mm:ss a v","Hmsv" : "HH:mm:ss v","hmv" : "h:mm a v","Hmv" : "HH:mm v","hmz" : "h:mm a zzzz","M" : "L","Md" : "MM-dd","Md-alt-variant" : "d/M","MEd" : "E, MM-dd","MEd-alt-variant" : "E, d/M","MMdd" : "MM-dd","MMdd-alt-variant" : "dd/MM","MMM" : "LLL","MMMd" : "MMM d","MMMEd" : "E, MMM d","MMMMd" : "MMMM d","MMMMW-count-one" : "'week' W 'of' MMMM","MMMMW-count-other" : "'week' W 'of' MMMM","ms" : "mm:ss","y" : "y","yM" : "y-MM","yM-alt-variant" : "M/y","yMd" : "y-MM-dd","yMd-alt-variant" : "d/M/y","yMEd" : "E, y-MM-dd","yMEd-alt-variant" : "E, d/M/y","yMMM" : "MMM y","yMMMd" : "MMM d, y","yMMMEd" : "E, MMM d, y","yMMMM" : "MMMM y","yQQQ" : "QQQ y","yQQQQ" : "QQQQ y","yw-count-one" : "'week' w 'of' Y","yw-count-other" : "'week' w 'of' Y"}}}}}}`},
		{lang: "zh", reg: "ca", scope: "plurals", wantedCode: http.StatusOK,
			wanted: `{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh","language":"zh","region":"ca","categories":{"plurals":{"pluralRules" : {"pluralRule-count-other" : " @integer 0~15, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
	} {
		d := d

		t.Run(d.lang+":"+d.reg+":"+d.scope, func(t *testing.T) {
			req := e.GET(GetPatternByLangRegURL)
			if d.lang != "" {
				req.WithQuery("language", d.lang)
			}
			if d.reg != "" {
				req.WithQuery("region", d.reg)
			}
			if d.scope != "" {
				req.WithQuery("scope", d.scope)
			}
			resp := req.Expect()

			resp.Status(d.wantedCode)

			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}

func TestGetPatternByLocale(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct{ locale, scope, wanted string }{
		{"en", "dates",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"en","language":"en","region":"US","categories":{"dates":{
				 "dayPeriodsFormat" : {
				   "narrow" : [ "a", "p" ],
				   "abbreviated" : [ "AM", "PM" ],
				   "wide" : [ "AM", "PM" ]
				 },
				 "dayPeriodsStandalone" : {
				   "narrow" : [ "AM", "PM" ],
				   "abbreviated" : [ "AM", "PM" ],
				   "wide" : [ "AM", "PM" ]
				 },
				 "daysFormat" : {
				   "narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],
				   "abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],
				   "wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],
				   "short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]
				 },
				 "daysStandalone" : {
				   "narrow" : [ "S", "M", "T", "W", "T", "F", "S" ],
				   "abbreviated" : [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ],
				   "wide" : [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ],
				   "short" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ]
				 },
				 "monthsFormat" : {
				   "narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],
				   "abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],
				   "wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]
				 },
				 "monthsStandalone" : {
				   "narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],
				   "abbreviated" : [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],
				   "wide" : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]
				 },
				 "eras" : {
				   "narrow" : [ "B", "A" ],
				   "abbreviated" : [ "BC", "AD" ],
				   "wide" : [ "Before Christ", "Anno Domini" ]
				 },
				 "firstDayOfWeek" : 0,
				 "weekendRange" : [ 6, 0 ],
				 "dateFormats" : {
				   "short" : "M/d/yy",
				   "medium" : "MMM d, y",
				   "long" : "MMMM d, y",
				   "full" : "EEEE, MMMM d, y"
				 },
				 "timeFormats" : {
				   "short" : "h:mm a",
				   "medium" : "h:mm:ss a",
				   "long" : "h:mm:ss a z",
				   "full" : "h:mm:ss a zzzz"
				 },
				 "dateTimeFormats" : {
				   "short" : "{1}, {0}",
				   "medium" : "{1}, {0}",
				   "long" : "{1} 'at' {0}",
				   "full" : "{1} 'at' {0}",
				   "appendItems" : {
					 "Day" : "{0} ({2}: {1})",
					 "Day-Of-Week" : "{0} {1}",
					 "Era" : "{0} {1}",
					 "Hour" : "{0} ({2}: {1})",
					 "Minute" : "{0} ({2}: {1})",
					 "Month" : "{0} ({2}: {1})",
					 "Quarter" : "{0} ({2}: {1})",
					 "Second" : "{0} ({2}: {1})",
					 "Timezone" : "{0} {1}",
					 "Week" : "{0} ({2}: {1})",
					 "Year" : "{0} {1}"
				   },
				   "intervalFormats" : {
					 "d" : {
					   "d" : "d – d"
					 },
					 "h" : {
					   "a" : "h a – h a",
					   "h" : "h – h a"
					 },
					 "H" : {
					   "H" : "HH – HH"
					 },
					 "hm" : {
					   "a" : "h:mm a – h:mm a",
					   "h" : "h:mm – h:mm a",
					   "m" : "h:mm – h:mm a"
					 },
					 "Hm" : {
					   "H" : "HH:mm – HH:mm",
					   "m" : "HH:mm – HH:mm"
					 },
					 "hmv" : {
					   "a" : "h:mm a – h:mm a v",
					   "h" : "h:mm – h:mm a v",
					   "m" : "h:mm – h:mm a v"
					 },
					 "Hmv" : {
					   "H" : "HH:mm – HH:mm v",
					   "m" : "HH:mm – HH:mm v"
					 },
					 "hv" : {
					   "a" : "h a – h a v",
					   "h" : "h – h a v"
					 },
					 "Hv" : {
					   "H" : "HH – HH v"
					 },
					 "intervalFormatFallback" : "{0} – {1}",
					 "M" : {
					   "M" : "M – M"
					 },
					 "Md" : {
					   "d" : "M/d – M/d",
					   "M" : "M/d – M/d"
					 },
					 "MEd" : {
					   "d" : "E, M/d – E, M/d",
					   "M" : "E, M/d – E, M/d"
					 },
					 "MMM" : {
					   "M" : "MMM – MMM"
					 },
					 "MMMd" : {
					   "d" : "MMM d – d",
					   "M" : "MMM d – MMM d"
					 },
					 "MMMEd" : {
					   "d" : "E, MMM d – E, MMM d",
					   "M" : "E, MMM d – E, MMM d"
					 },
					 "y" : {
					   "y" : "y – y"
					 },
					 "yM" : {
					   "y" : "M/y – M/y",
					   "M" : "M/y – M/y"
					 },
					 "yMd" : {
					   "d" : "M/d/y – M/d/y",
					   "y" : "M/d/y – M/d/y",
					   "M" : "M/d/y – M/d/y"
					 },
					 "yMEd" : {
					   "d" : "E, M/d/y – E, M/d/y",
					   "y" : "E, M/d/y – E, M/d/y",
					   "M" : "E, M/d/y – E, M/d/y"
					 },
					 "yMMM" : {
					   "y" : "MMM y – MMM y",
					   "M" : "MMM – MMM y"
					 },
					 "yMMMd" : {
					   "d" : "MMM d – d, y",
					   "y" : "MMM d, y – MMM d, y",
					   "M" : "MMM d – MMM d, y"
					 },
					 "yMMMEd" : {
					   "d" : "E, MMM d – E, MMM d, y",
					   "y" : "E, MMM d, y – E, MMM d, y",
					   "M" : "E, MMM d – E, MMM d, y"
					 },
					 "yMMMM" : {
					   "y" : "MMMM y – MMMM y",
					   "M" : "MMMM – MMMM y"
					 }
				   },
				   "availableFormats" : {
					 "Bh" : "h B",
					 "Bhm" : "h:mm B",
					 "Bhms" : "h:mm:ss B",
					 "d" : "d",
					 "E" : "ccc",
					 "EBhm" : "E h:mm B",
					 "EBhms" : "E h:mm:ss B",
					 "Ed" : "d E",
					 "Ehm" : "E h:mm a",
					 "EHm" : "E HH:mm",
					 "Ehms" : "E h:mm:ss a",
					 "EHms" : "E HH:mm:ss",
					 "Gy" : "y G",
					 "GyMMM" : "MMM y G",
					 "GyMMMd" : "MMM d, y G",
					 "GyMMMEd" : "E, MMM d, y G",
					 "H" : "HH",
					 "h" : "h a",
					 "hm" : "h:mm a",
					 "Hm" : "HH:mm",
					 "hms" : "h:mm:ss a",
					 "Hms" : "HH:mm:ss",
					 "hmsv" : "h:mm:ss a v",
					 "Hmsv" : "HH:mm:ss v",
					 "hmv" : "h:mm a v",
					 "Hmv" : "HH:mm v",
					 "hmz" : "h:mm a zzzz",
					 "M" : "L",
					 "Md" : "M/d",
					 "MEd" : "E, M/d",
					 "MMM" : "LLL",
					 "MMMd" : "MMM d",
					 "MMMEd" : "E, MMM d",
					 "MMMMd" : "MMMM d",
					 "MMMMW-count-one" : "'week' W 'of' MMMM",
					 "MMMMW-count-other" : "'week' W 'of' MMMM",
					 "ms" : "mm:ss",
					 "y" : "y",
					 "yM" : "M/y",
					 "yMd" : "M/d/y",
					 "yMEd" : "E, M/d/y",
					 "yMMM" : "MMM y",
					 "yMMMd" : "MMM d, y",
					 "yMMMEd" : "E, MMM d, y",
					 "yMMMM" : "MMMM y",
					 "yQQQ" : "QQQ y",
					 "yQQQQ" : "QQQQ y",
					 "yw-count-one" : "'week' w 'of' Y",
					 "yw-count-other" : "'week' w 'of' Y"
				   }
				 }
			   }}}}`},
		{"zh-Hans", "numbers",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"zh-Hans","language":"zh","region":"CN","categories":{"numbers":{
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
			   },"supplemental":{"numbers":{
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
		   }}}}}`},
		{"es-US", "plurals",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"es-US","language":"es","region":"US","categories":{"plurals":{"pluralRules":{"pluralRule-count-one":"n = 1 @integer 1 @decimal 1.0, 1.00, 1.000, 1.0000","pluralRule-count-other":" @integer 0, 2~16, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~0.9, 1.1~1.6, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"}}}}}`},
		{"de", "currencies",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"de","language":"de","region":"DE","categories":{"currencies":{
				 "ADP" : {
				   "symbol" : "ADP",
				   "displayName" : "Andorranische Pesete",
				   "displayName-count-one" : "Andorranische Pesete",
				   "displayName-count-other" : "Andorranische Peseten"
				 },
				 "AED" : {
				   "symbol" : "AED",
				   "displayName" : "VAE-Dirham",
				   "displayName-count-one" : "VAE-Dirham",
				   "displayName-count-other" : "VAE-Dirham"
				 },
				 "AFA" : {
				   "symbol" : "AFA",
				   "displayName" : "Afghanische Afghani (1927–2002)",
				   "displayName-count-one" : "Afghanische Afghani (1927–2002)",
				   "displayName-count-other" : "Afghanische Afghani (1927–2002)"
				 },
				 "AFN" : {
				   "symbol" : "AFN",
				   "displayName" : "Afghanischer Afghani",
				   "displayName-count-one" : "Afghanischer Afghani",
				   "displayName-count-other" : "Afghanische Afghani"
				 },
				 "ALK" : {
				   "symbol" : "ALK",
				   "displayName" : "Albanischer Lek (1946–1965)",
				   "displayName-count-one" : "Albanischer Lek (1946–1965)",
				   "displayName-count-other" : "Albanische Lek (1946–1965)"
				 },
				 "ALL" : {
				   "symbol" : "ALL",
				   "displayName" : "Albanischer Lek",
				   "displayName-count-one" : "Albanischer Lek",
				   "displayName-count-other" : "Albanische Lek"
				 },
				 "AMD" : {
				   "symbol" : "AMD",
				   "displayName" : "Armenischer Dram",
				   "displayName-count-one" : "Armenischer Dram",
				   "displayName-count-other" : "Armenische Dram"
				 },
				 "ANG" : {
				   "symbol" : "ANG",
				   "displayName" : "Niederländische-Antillen-Gulden",
				   "displayName-count-one" : "Niederländische-Antillen-Gulden",
				   "displayName-count-other" : "Niederländische-Antillen-Gulden"
				 },
				 "AOA" : {
				   "symbol" : "AOA",
				   "displayName" : "Angolanischer Kwanza",
				   "displayName-count-one" : "Angolanischer Kwanza",
				   "symbol-alt-narrow" : "Kz",
				   "displayName-count-other" : "Angolanische Kwanza"
				 },
				 "AOK" : {
				   "symbol" : "AOK",
				   "displayName" : "Angolanischer Kwanza (1977–1990)",
				   "displayName-count-one" : "Angolanischer Kwanza (1977–1990)",
				   "displayName-count-other" : "Angolanische Kwanza (1977–1990)"
				 },
				 "AON" : {
				   "symbol" : "AON",
				   "displayName" : "Angolanischer Neuer Kwanza (1990–2000)",
				   "displayName-count-one" : "Angolanischer Neuer Kwanza (1990–2000)",
				   "displayName-count-other" : "Angolanische Neue Kwanza (1990–2000)"
				 },
				 "AOR" : {
				   "symbol" : "AOR",
				   "displayName" : "Angolanischer Kwanza Reajustado (1995–1999)",
				   "displayName-count-one" : "Angolanischer Kwanza Reajustado (1995–1999)",
				   "displayName-count-other" : "Angolanische Kwanza Reajustado (1995–1999)"
				 },
				 "ARA" : {
				   "symbol" : "ARA",
				   "displayName" : "Argentinischer Austral",
				   "displayName-count-one" : "Argentinischer Austral",
				   "displayName-count-other" : "Argentinische Austral"
				 },
				 "ARL" : {
				   "symbol" : "ARL",
				   "displayName" : "Argentinischer Peso Ley (1970–1983)",
				   "displayName-count-one" : "Argentinischer Peso Ley (1970–1983)",
				   "displayName-count-other" : "Argentinische Pesos Ley (1970–1983)"
				 },
				 "ARM" : {
				   "symbol" : "ARM",
				   "displayName" : "Argentinischer Peso (1881–1970)",
				   "displayName-count-one" : "Argentinischer Peso (1881–1970)",
				   "displayName-count-other" : "Argentinische Pesos (1881–1970)"
				 },
				 "ARP" : {
				   "symbol" : "ARP",
				   "displayName" : "Argentinischer Peso (1983–1985)",
				   "displayName-count-one" : "Argentinischer Peso (1983–1985)",
				   "displayName-count-other" : "Argentinische Peso (1983–1985)"
				 },
				 "ARS" : {
				   "symbol" : "ARS",
				   "displayName" : "Argentinischer Peso",
				   "displayName-count-one" : "Argentinischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Argentinische Pesos"
				 },
				 "ATS" : {
				   "symbol" : "öS",
				   "displayName" : "Österreichischer Schilling",
				   "displayName-count-one" : "Österreichischer Schilling",
				   "displayName-count-other" : "Österreichische Schilling"
				 },
				 "AUD" : {
				   "symbol" : "AU$",
				   "displayName" : "Australischer Dollar",
				   "displayName-count-one" : "Australischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Australische Dollar"
				 },
				 "AWG" : {
				   "symbol" : "AWG",
				   "displayName" : "Aruba-Florin",
				   "displayName-count-one" : "Aruba-Florin",
				   "displayName-count-other" : "Aruba-Florin"
				 },
				 "AZM" : {
				   "symbol" : "AZM",
				   "displayName" : "Aserbaidschan-Manat (1993–2006)",
				   "displayName-count-one" : "Aserbaidschan-Manat (1993–2006)",
				   "displayName-count-other" : "Aserbaidschan-Manat (1993–2006)"
				 },
				 "AZN" : {
				   "symbol" : "AZN",
				   "displayName" : "Aserbaidschan-Manat",
				   "displayName-count-one" : "Aserbaidschan-Manat",
				   "displayName-count-other" : "Aserbaidschan-Manat"
				 },
				 "BAD" : {
				   "symbol" : "BAD",
				   "displayName" : "Bosnien und Herzegowina Dinar (1992–1994)",
				   "displayName-count-one" : "Bosnien und Herzegowina Dinar (1992–1994)",
				   "displayName-count-other" : "Bosnien und Herzegowina Dinar (1992–1994)"
				 },
				 "BAM" : {
				   "symbol" : "BAM",
				   "displayName" : "Bosnien und Herzegowina Konvertierbare Mark",
				   "displayName-count-one" : "Bosnien und Herzegowina Konvertierbare Mark",
				   "symbol-alt-narrow" : "KM",
				   "displayName-count-other" : "Bosnien und Herzegowina Konvertierbare Mark"
				 },
				 "BAN" : {
				   "symbol" : "BAN",
				   "displayName" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)",
				   "displayName-count-one" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)",
				   "displayName-count-other" : "Bosnien und Herzegowina Neue Dinar (1994–1997)"
				 },
				 "BBD" : {
				   "symbol" : "BBD",
				   "displayName" : "Barbados-Dollar",
				   "displayName-count-one" : "Barbados-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Barbados-Dollar"
				 },
				 "BDT" : {
				   "symbol" : "BDT",
				   "displayName" : "Bangladesch-Taka",
				   "displayName-count-one" : "Bangladesch-Taka",
				   "symbol-alt-narrow" : "৳",
				   "displayName-count-other" : "Bangladesch-Taka"
				 },
				 "BEC" : {
				   "symbol" : "BEC",
				   "displayName" : "Belgischer Franc (konvertibel)",
				   "displayName-count-one" : "Belgischer Franc (konvertibel)",
				   "displayName-count-other" : "Belgische Franc (konvertibel)"
				 },
				 "BEF" : {
				   "symbol" : "BEF",
				   "displayName" : "Belgischer Franc",
				   "displayName-count-one" : "Belgischer Franc",
				   "displayName-count-other" : "Belgische Franc"
				 },
				 "BEL" : {
				   "symbol" : "BEL",
				   "displayName" : "Belgischer Finanz-Franc",
				   "displayName-count-one" : "Belgischer Finanz-Franc",
				   "displayName-count-other" : "Belgische Finanz-Franc"
				 },
				 "BGL" : {
				   "symbol" : "BGL",
				   "displayName" : "Bulgarische Lew (1962–1999)",
				   "displayName-count-one" : "Bulgarische Lew (1962–1999)",
				   "displayName-count-other" : "Bulgarische Lew (1962–1999)"
				 },
				 "BGM" : {
				   "symbol" : "BGK",
				   "displayName" : "Bulgarischer Lew (1952–1962)",
				   "displayName-count-one" : "Bulgarischer Lew (1952–1962)",
				   "displayName-count-other" : "Bulgarische Lew (1952–1962)"
				 },
				 "BGN" : {
				   "symbol" : "BGN",
				   "displayName" : "Bulgarischer Lew",
				   "displayName-count-one" : "Bulgarischer Lew",
				   "displayName-count-other" : "Bulgarische Lew"
				 },
				 "BGO" : {
				   "symbol" : "BGJ",
				   "displayName" : "Bulgarischer Lew (1879–1952)",
				   "displayName-count-one" : "Bulgarischer Lew (1879–1952)",
				   "displayName-count-other" : "Bulgarische Lew (1879–1952)"
				 },
				 "BHD" : {
				   "symbol" : "BHD",
				   "displayName" : "Bahrain-Dinar",
				   "displayName-count-one" : "Bahrain-Dinar",
				   "displayName-count-other" : "Bahrain-Dinar"
				 },
				 "BIF" : {
				   "symbol" : "BIF",
				   "displayName" : "Burundi-Franc",
				   "displayName-count-one" : "Burundi-Franc",
				   "displayName-count-other" : "Burundi-Francs"
				 },
				 "BMD" : {
				   "symbol" : "BMD",
				   "displayName" : "Bermuda-Dollar",
				   "displayName-count-one" : "Bermuda-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Bermuda-Dollar"
				 },
				 "BND" : {
				   "symbol" : "BND",
				   "displayName" : "Brunei-Dollar",
				   "displayName-count-one" : "Brunei-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Brunei-Dollar"
				 },
				 "BOB" : {
				   "symbol" : "BOB",
				   "displayName" : "Bolivianischer Boliviano",
				   "displayName-count-one" : "Bolivianischer Boliviano",
				   "symbol-alt-narrow" : "Bs",
				   "displayName-count-other" : "Bolivianische Bolivianos"
				 },
				 "BOL" : {
				   "symbol" : "BOL",
				   "displayName" : "Bolivianischer Boliviano (1863–1963)",
				   "displayName-count-one" : "Bolivianischer Boliviano (1863–1963)",
				   "displayName-count-other" : "Bolivianische Bolivianos (1863–1963)"
				 },
				 "BOP" : {
				   "symbol" : "BOP",
				   "displayName" : "Bolivianischer Peso",
				   "displayName-count-one" : "Bolivianischer Peso",
				   "displayName-count-other" : "Bolivianische Peso"
				 },
				 "BOV" : {
				   "symbol" : "BOV",
				   "displayName" : "Boliviansiche Mvdol",
				   "displayName-count-one" : "Boliviansiche Mvdol",
				   "displayName-count-other" : "Bolivianische Mvdol"
				 },
				 "BRB" : {
				   "symbol" : "BRB",
				   "displayName" : "Brasilianischer Cruzeiro Novo (1967–1986)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro Novo (1967–1986)",
				   "displayName-count-other" : "Brasilianische Cruzeiro Novo (1967–1986)"
				 },
				 "BRC" : {
				   "symbol" : "BRC",
				   "displayName" : "Brasilianischer Cruzado (1986–1989)",
				   "displayName-count-one" : "Brasilianischer Cruzado (1986–1989)",
				   "displayName-count-other" : "Brasilianische Cruzado (1986–1989)"
				 },
				 "BRE" : {
				   "symbol" : "BRE",
				   "displayName" : "Brasilianischer Cruzeiro (1990–1993)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1990–1993)",
				   "displayName-count-other" : "Brasilianische Cruzeiro (1990–1993)"
				 },
				 "BRL" : {
				   "symbol" : "R$",
				   "displayName" : "Brasilianischer Real",
				   "displayName-count-one" : "Brasilianischer Real",
				   "symbol-alt-narrow" : "R$",
				   "displayName-count-other" : "Brasilianische Real"
				 },
				 "BRN" : {
				   "symbol" : "BRN",
				   "displayName" : "Brasilianischer Cruzado Novo (1989–1990)",
				   "displayName-count-one" : "Brasilianischer Cruzado Novo (1989–1990)",
				   "displayName-count-other" : "Brasilianische Cruzado Novo (1989–1990)"
				 },
				 "BRR" : {
				   "symbol" : "BRR",
				   "displayName" : "Brasilianischer Cruzeiro (1993–1994)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1993–1994)",
				   "displayName-count-other" : "Brasilianische Cruzeiro (1993–1994)"
				 },
				 "BRZ" : {
				   "symbol" : "BRZ",
				   "displayName" : "Brasilianischer Cruzeiro (1942–1967)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1942–1967)",
				   "displayName-count-other" : "Brasilianischer Cruzeiro (1942–1967)"
				 },
				 "BSD" : {
				   "symbol" : "BSD",
				   "displayName" : "Bahamas-Dollar",
				   "displayName-count-one" : "Bahamas-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Bahamas-Dollar"
				 },
				 "BTN" : {
				   "symbol" : "BTN",
				   "displayName" : "Bhutan-Ngultrum",
				   "displayName-count-one" : "Bhutan-Ngultrum",
				   "displayName-count-other" : "Bhutan-Ngultrum"
				 },
				 "BUK" : {
				   "symbol" : "BUK",
				   "displayName" : "Birmanischer Kyat",
				   "displayName-count-one" : "Birmanischer Kyat",
				   "displayName-count-other" : "Birmanische Kyat"
				 },
				 "BWP" : {
				   "symbol" : "BWP",
				   "displayName" : "Botswanischer Pula",
				   "displayName-count-one" : "Botswanischer Pula",
				   "symbol-alt-narrow" : "P",
				   "displayName-count-other" : "Botswanische Pula"
				 },
				 "BYB" : {
				   "symbol" : "BYB",
				   "displayName" : "Belarus-Rubel (1994–1999)",
				   "displayName-count-one" : "Belarus-Rubel (1994–1999)",
				   "displayName-count-other" : "Belarus-Rubel (1994–1999)"
				 },
				 "BYN" : {
				   "symbol" : "BYN",
				   "displayName" : "Weißrussischer Rubel",
				   "displayName-count-one" : "Weißrussischer Rubel",
				   "symbol-alt-narrow" : "р.",
				   "displayName-count-other" : "Weißrussische Rubel"
				 },
				 "BYR" : {
				   "symbol" : "BYR",
				   "displayName" : "Weißrussischer Rubel (2000–2016)",
				   "displayName-count-one" : "Weißrussischer Rubel (2000–2016)",
				   "displayName-count-other" : "Weißrussische Rubel (2000–2016)"
				 },
				 "BZD" : {
				   "symbol" : "BZD",
				   "displayName" : "Belize-Dollar",
				   "displayName-count-one" : "Belize-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Belize-Dollar"
				 },
				 "CAD" : {
				   "symbol" : "CA$",
				   "displayName" : "Kanadischer Dollar",
				   "displayName-count-one" : "Kanadischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kanadische Dollar"
				 },
				 "CDF" : {
				   "symbol" : "CDF",
				   "displayName" : "Kongo-Franc",
				   "displayName-count-one" : "Kongo-Franc",
				   "displayName-count-other" : "Kongo-Francs"
				 },
				 "CHE" : {
				   "symbol" : "CHE",
				   "displayName" : "WIR-Euro",
				   "displayName-count-one" : "WIR-Euro",
				   "displayName-count-other" : "WIR-Euro"
				 },
				 "CHF" : {
				   "symbol" : "CHF",
				   "displayName" : "Schweizer Franken",
				   "displayName-count-one" : "Schweizer Franken",
				   "displayName-count-other" : "Schweizer Franken"
				 },
				 "CHW" : {
				   "symbol" : "CHW",
				   "displayName" : "WIR Franken",
				   "displayName-count-one" : "WIR Franken",
				   "displayName-count-other" : "WIR Franken"
				 },
				 "CLE" : {
				   "symbol" : "CLE",
				   "displayName" : "Chilenischer Escudo",
				   "displayName-count-one" : "Chilenischer Escudo",
				   "displayName-count-other" : "Chilenische Escudo"
				 },
				 "CLF" : {
				   "symbol" : "CLF",
				   "displayName" : "Chilenische Unidades de Fomento",
				   "displayName-count-one" : "Chilenische Unidades de Fomento",
				   "displayName-count-other" : "Chilenische Unidades de Fomento"
				 },
				 "CLP" : {
				   "symbol" : "CLP",
				   "displayName" : "Chilenischer Peso",
				   "displayName-count-one" : "Chilenischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Chilenische Pesos"
				 },
				 "CNH" : {
				   "symbol" : "CNH",
				   "displayName" : "Renminbi Yuan (Off–Shore)",
				   "displayName-count-one" : "Renminbi Yuan (Off–Shore)",
				   "displayName-count-other" : "Renminbi Yuan (Off–Shore)"
				 },
				 "CNX" : {
				   "symbol" : "CNX",
				   "displayName" : "Dollar der Chinesischen Volksbank",
				   "displayName-count-one" : "Dollar der Chinesischen Volksbank",
				   "displayName-count-other" : "Dollar der Chinesischen Volksbank"
				 },
				 "CNY" : {
				   "symbol" : "CN¥",
				   "displayName" : "Renminbi Yuan",
				   "displayName-count-one" : "Chinesischer Yuan",
				   "symbol-alt-narrow" : "¥",
				   "displayName-count-other" : "Renminbi Yuan"
				 },
				 "COP" : {
				   "symbol" : "COP",
				   "displayName" : "Kolumbianischer Peso",
				   "displayName-count-one" : "Kolumbianischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kolumbianische Pesos"
				 },
				 "COU" : {
				   "symbol" : "COU",
				   "displayName" : "Kolumbianische Unidades de valor real",
				   "displayName-count-one" : "Kolumbianische Unidad de valor real",
				   "displayName-count-other" : "Kolumbianische Unidades de valor real"
				 },
				 "CRC" : {
				   "symbol" : "CRC",
				   "displayName" : "Costa-Rica-Colón",
				   "displayName-count-one" : "Costa-Rica-Colón",
				   "symbol-alt-narrow" : "₡",
				   "displayName-count-other" : "Costa-Rica-Colón"
				 },
				 "CSD" : {
				   "symbol" : "CSD",
				   "displayName" : "Serbischer Dinar (2002–2006)",
				   "displayName-count-one" : "Serbischer Dinar (2002–2006)",
				   "displayName-count-other" : "Serbische Dinar (2002–2006)"
				 },
				 "CSK" : {
				   "symbol" : "CSK",
				   "displayName" : "Tschechoslowakische Krone",
				   "displayName-count-one" : "Tschechoslowakische Kronen",
				   "displayName-count-other" : "Tschechoslowakische Kronen"
				 },
				 "CUC" : {
				   "symbol" : "CUC",
				   "displayName" : "Kubanischer Peso (konvertibel)",
				   "displayName-count-one" : "Kubanischer Peso (konvertibel)",
				   "symbol-alt-narrow" : "Cub$",
				   "displayName-count-other" : "Kubanische Pesos (konvertibel)"
				 },
				 "CUP" : {
				   "symbol" : "CUP",
				   "displayName" : "Kubanischer Peso",
				   "displayName-count-one" : "Kubanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kubanische Pesos"
				 },
				 "CVE" : {
				   "symbol" : "CVE",
				   "displayName" : "Cabo-Verde-Escudo",
				   "displayName-count-one" : "Cabo-Verde-Escudo",
				   "displayName-count-other" : "Cabo-Verde-Escudos"
				 },
				 "CYP" : {
				   "symbol" : "CYP",
				   "displayName" : "Zypern-Pfund",
				   "displayName-count-one" : "Zypern Pfund",
				   "displayName-count-other" : "Zypern Pfund"
				 },
				 "CZK" : {
				   "symbol" : "CZK",
				   "displayName" : "Tschechische Krone",
				   "displayName-count-one" : "Tschechische Krone",
				   "symbol-alt-narrow" : "Kč",
				   "displayName-count-other" : "Tschechische Kronen"
				 },
				 "DDM" : {
				   "symbol" : "DDM",
				   "displayName" : "Mark der DDR",
				   "displayName-count-one" : "Mark der DDR",
				   "displayName-count-other" : "Mark der DDR"
				 },
				 "DEM" : {
				   "symbol" : "DM",
				   "displayName" : "Deutsche Mark",
				   "displayName-count-one" : "Deutsche Mark",
				   "displayName-count-other" : "Deutsche Mark"
				 },
				 "DJF" : {
				   "symbol" : "DJF",
				   "displayName" : "Dschibuti-Franc",
				   "displayName-count-one" : "Dschibuti-Franc",
				   "displayName-count-other" : "Dschibuti-Franc"
				 },
				 "DKK" : {
				   "symbol" : "DKK",
				   "displayName" : "Dänische Krone",
				   "displayName-count-one" : "Dänische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Dänische Kronen"
				 },
				 "DOP" : {
				   "symbol" : "DOP",
				   "displayName" : "Dominikanischer Peso",
				   "displayName-count-one" : "Dominikanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Dominikanische Pesos"
				 },
				 "DZD" : {
				   "symbol" : "DZD",
				   "displayName" : "Algerischer Dinar",
				   "displayName-count-one" : "Algerischer Dinar",
				   "displayName-count-other" : "Algerische Dinar"
				 },
				 "ECS" : {
				   "symbol" : "ECS",
				   "displayName" : "Ecuadorianischer Sucre",
				   "displayName-count-one" : "Ecuadorianischer Sucre",
				   "displayName-count-other" : "Ecuadorianische Sucre"
				 },
				 "ECV" : {
				   "symbol" : "ECV",
				   "displayName" : "Verrechnungseinheit für Ecuador",
				   "displayName-count-one" : "Verrechnungseinheiten für Ecuador",
				   "displayName-count-other" : "Verrechnungseinheiten für Ecuador"
				 },
				 "EEK" : {
				   "symbol" : "EEK",
				   "displayName" : "Estnische Krone",
				   "displayName-count-one" : "Estnische Krone",
				   "displayName-count-other" : "Estnische Kronen"
				 },
				 "EGP" : {
				   "symbol" : "EGP",
				   "displayName" : "Ägyptisches Pfund",
				   "displayName-count-one" : "Ägyptisches Pfund",
				   "symbol-alt-narrow" : "E£",
				   "displayName-count-other" : "Ägyptische Pfund"
				 },
				 "ERN" : {
				   "symbol" : "ERN",
				   "displayName" : "Eritreischer Nakfa",
				   "displayName-count-one" : "Eritreischer Nakfa",
				   "displayName-count-other" : "Eritreische Nakfa"
				 },
				 "ESA" : {
				   "symbol" : "ESA",
				   "displayName" : "Spanische Peseta (A–Konten)",
				   "displayName-count-one" : "Spanische Peseta (A–Konten)",
				   "displayName-count-other" : "Spanische Peseten (A–Konten)"
				 },
				 "ESB" : {
				   "symbol" : "ESB",
				   "displayName" : "Spanische Peseta (konvertibel)",
				   "displayName-count-one" : "Spanische Peseta (konvertibel)",
				   "displayName-count-other" : "Spanische Peseten (konvertibel)"
				 },
				 "ESP" : {
				   "symbol" : "ESP",
				   "displayName" : "Spanische Peseta",
				   "displayName-count-one" : "Spanische Peseta",
				   "symbol-alt-narrow" : "₧",
				   "displayName-count-other" : "Spanische Peseten"
				 },
				 "ETB" : {
				   "symbol" : "ETB",
				   "displayName" : "Äthiopischer Birr",
				   "displayName-count-one" : "Äthiopischer Birr",
				   "displayName-count-other" : "Äthiopische Birr"
				 },
				 "EUR" : {
				   "symbol" : "€",
				   "displayName" : "Euro",
				   "displayName-count-one" : "Euro",
				   "symbol-alt-narrow" : "€",
				   "displayName-count-other" : "Euro"
				 },
				 "FIM" : {
				   "symbol" : "FIM",
				   "displayName" : "Finnische Mark",
				   "displayName-count-one" : "Finnische Mark",
				   "displayName-count-other" : "Finnische Mark"
				 },
				 "FJD" : {
				   "symbol" : "FJD",
				   "displayName" : "Fidschi-Dollar",
				   "displayName-count-one" : "Fidschi-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Fidschi-Dollar"
				 },
				 "FKP" : {
				   "symbol" : "FKP",
				   "displayName" : "Falkland-Pfund",
				   "displayName-count-one" : "Falkland-Pfund",
				   "symbol-alt-narrow" : "Fl£",
				   "displayName-count-other" : "Falkland-Pfund"
				 },
				 "FRF" : {
				   "symbol" : "FRF",
				   "displayName" : "Französischer Franc",
				   "displayName-count-one" : "Französischer Franc",
				   "displayName-count-other" : "Französische Franc"
				 },
				 "GBP" : {
				   "symbol" : "£",
				   "displayName" : "Britisches Pfund",
				   "displayName-count-one" : "Britisches Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Britische Pfund"
				 },
				 "GEK" : {
				   "symbol" : "GEK",
				   "displayName" : "Georgischer Kupon Larit",
				   "displayName-count-one" : "Georgischer Kupon Larit",
				   "displayName-count-other" : "Georgische Kupon Larit"
				 },
				 "GEL" : {
				   "symbol" : "GEL",
				   "symbol-alt-variant" : "₾",
				   "displayName" : "Georgischer Lari",
				   "displayName-count-one" : "Georgischer Lari",
				   "symbol-alt-narrow" : "₾",
				   "displayName-count-other" : "Georgische Lari"
				 },
				 "GHC" : {
				   "symbol" : "GHC",
				   "displayName" : "Ghanaischer Cedi (1979–2007)",
				   "displayName-count-one" : "Ghanaischer Cedi (1979–2007)",
				   "displayName-count-other" : "Ghanaische Cedi (1979–2007)"
				 },
				 "GHS" : {
				   "symbol" : "GHS",
				   "displayName" : "Ghanaischer Cedi",
				   "displayName-count-one" : "Ghanaischer Cedi",
				   "displayName-count-other" : "Ghanaische Cedi"
				 },
				 "GIP" : {
				   "symbol" : "GIP",
				   "displayName" : "Gibraltar-Pfund",
				   "displayName-count-one" : "Gibraltar-Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Gibraltar-Pfund"
				 },
				 "GMD" : {
				   "symbol" : "GMD",
				   "displayName" : "Gambia-Dalasi",
				   "displayName-count-one" : "Gambia-Dalasi",
				   "displayName-count-other" : "Gambia-Dalasi"
				 },
				 "GNF" : {
				   "symbol" : "GNF",
				   "displayName" : "Guinea-Franc",
				   "displayName-count-one" : "Guinea-Franc",
				   "symbol-alt-narrow" : "F.G.",
				   "displayName-count-other" : "Guinea-Franc"
				 },
				 "GNS" : {
				   "symbol" : "GNS",
				   "displayName" : "Guineischer Syli",
				   "displayName-count-one" : "Guineischer Syli",
				   "displayName-count-other" : "Guineische Syli"
				 },
				 "GQE" : {
				   "symbol" : "GQE",
				   "displayName" : "Äquatorialguinea-Ekwele",
				   "displayName-count-one" : "Äquatorialguinea-Ekwele",
				   "displayName-count-other" : "Äquatorialguinea-Ekwele"
				 },
				 "GRD" : {
				   "symbol" : "GRD",
				   "displayName" : "Griechische Drachme",
				   "displayName-count-one" : "Griechische Drachme",
				   "displayName-count-other" : "Griechische Drachmen"
				 },
				 "GTQ" : {
				   "symbol" : "GTQ",
				   "displayName" : "Guatemaltekischer Quetzal",
				   "displayName-count-one" : "Guatemaltekischer Quetzal",
				   "symbol-alt-narrow" : "Q",
				   "displayName-count-other" : "Guatemaltekische Quetzales"
				 },
				 "GWE" : {
				   "symbol" : "GWE",
				   "displayName" : "Portugiesisch Guinea Escudo",
				   "displayName-count-one" : "Portugiesisch Guinea Escudo",
				   "displayName-count-other" : "Portugiesisch Guinea Escudo"
				 },
				 "GWP" : {
				   "symbol" : "GWP",
				   "displayName" : "Guinea-Bissau Peso",
				   "displayName-count-one" : "Guinea-Bissau Peso",
				   "displayName-count-other" : "Guinea-Bissau Pesos"
				 },
				 "GYD" : {
				   "symbol" : "GYD",
				   "displayName" : "Guyana-Dollar",
				   "displayName-count-one" : "Guyana-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Guyana-Dollar"
				 },
				 "HKD" : {
				   "symbol" : "HK$",
				   "displayName" : "Hongkong-Dollar",
				   "displayName-count-one" : "Hongkong-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Hongkong-Dollar"
				 },
				 "HNL" : {
				   "symbol" : "HNL",
				   "displayName" : "Honduras-Lempira",
				   "displayName-count-one" : "Honduras-Lempira",
				   "symbol-alt-narrow" : "L",
				   "displayName-count-other" : "Honduras-Lempira"
				 },
				 "HRD" : {
				   "symbol" : "HRD",
				   "displayName" : "Kroatischer Dinar",
				   "displayName-count-one" : "Kroatischer Dinar",
				   "displayName-count-other" : "Kroatische Dinar"
				 },
				 "HRK" : {
				   "symbol" : "HRK",
				   "displayName" : "Kroatischer Kuna",
				   "displayName-count-one" : "Kroatischer Kuna",
				   "symbol-alt-narrow" : "kn",
				   "displayName-count-other" : "Kroatische Kuna"
				 },
				 "HTG" : {
				   "symbol" : "HTG",
				   "displayName" : "Haitianische Gourde",
				   "displayName-count-one" : "Haitianische Gourde",
				   "displayName-count-other" : "Haitianische Gourdes"
				 },
				 "HUF" : {
				   "symbol" : "HUF",
				   "displayName" : "Ungarischer Forint",
				   "displayName-count-one" : "Ungarischer Forint",
				   "symbol-alt-narrow" : "Ft",
				   "displayName-count-other" : "Ungarische Forint"
				 },
				 "IDR" : {
				   "symbol" : "IDR",
				   "displayName" : "Indonesische Rupiah",
				   "displayName-count-one" : "Indonesische Rupiah",
				   "symbol-alt-narrow" : "Rp",
				   "displayName-count-other" : "Indonesische Rupiah"
				 },
				 "IEP" : {
				   "symbol" : "IEP",
				   "displayName" : "Irisches Pfund",
				   "displayName-count-one" : "Irisches Pfund",
				   "displayName-count-other" : "Irische Pfund"
				 },
				 "ILP" : {
				   "symbol" : "ILP",
				   "displayName" : "Israelisches Pfund",
				   "displayName-count-one" : "Israelisches Pfund",
				   "displayName-count-other" : "Israelische Pfund"
				 },
				 "ILR" : {
				   "symbol" : "ILR",
				   "displayName" : "Israelischer Schekel (1980–1985)",
				   "displayName-count-one" : "Israelischer Schekel (1980–1985)",
				   "displayName-count-other" : "Israelische Schekel (1980–1985)"
				 },
				 "ILS" : {
				   "symbol" : "₪",
				   "displayName" : "Israelischer Neuer Schekel",
				   "displayName-count-one" : "Israelischer Neuer Schekel",
				   "symbol-alt-narrow" : "₪",
				   "displayName-count-other" : "Israelische Neue Schekel"
				 },
				 "INR" : {
				   "symbol" : "₹",
				   "displayName" : "Indische Rupie",
				   "displayName-count-one" : "Indische Rupie",
				   "symbol-alt-narrow" : "₹",
				   "displayName-count-other" : "Indische Rupien"
				 },
				 "IQD" : {
				   "symbol" : "IQD",
				   "displayName" : "Irakischer Dinar",
				   "displayName-count-one" : "Irakischer Dinar",
				   "displayName-count-other" : "Irakische Dinar"
				 },
				 "IRR" : {
				   "symbol" : "IRR",
				   "displayName" : "Iranischer Rial",
				   "displayName-count-one" : "Iranischer Rial",
				   "displayName-count-other" : "Iranische Rial"
				 },
				 "ISJ" : {
				   "symbol" : "ISJ",
				   "displayName" : "Isländische Krone (1918–1981)",
				   "displayName-count-one" : "Isländische Krone (1918–1981)",
				   "displayName-count-other" : "Isländische Kronen (1918–1981)"
				 },
				 "ISK" : {
				   "symbol" : "ISK",
				   "displayName" : "Isländische Krone",
				   "displayName-count-one" : "Isländische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Isländische Kronen"
				 },
				 "ITL" : {
				   "symbol" : "ITL",
				   "displayName" : "Italienische Lira",
				   "displayName-count-one" : "Italienische Lira",
				   "displayName-count-other" : "Italienische Lire"
				 },
				 "JMD" : {
				   "symbol" : "JMD",
				   "displayName" : "Jamaika-Dollar",
				   "displayName-count-one" : "Jamaika-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Jamaika-Dollar"
				 },
				 "JOD" : {
				   "symbol" : "JOD",
				   "displayName" : "Jordanischer Dinar",
				   "displayName-count-one" : "Jordanischer Dinar",
				   "displayName-count-other" : "Jordanische Dinar"
				 },
				 "JPY" : {
				   "symbol" : "¥",
				   "displayName" : "Japanischer Yen",
				   "displayName-count-one" : "Japanischer Yen",
				   "symbol-alt-narrow" : "¥",
				   "displayName-count-other" : "Japanische Yen"
				 },
				 "KES" : {
				   "symbol" : "KES",
				   "displayName" : "Kenia-Schilling",
				   "displayName-count-one" : "Kenia-Schilling",
				   "displayName-count-other" : "Kenia-Schilling"
				 },
				 "KGS" : {
				   "symbol" : "KGS",
				   "displayName" : "Kirgisischer Som",
				   "displayName-count-one" : "Kirgisischer Som",
				   "displayName-count-other" : "Kirgisische Som"
				 },
				 "KHR" : {
				   "symbol" : "KHR",
				   "displayName" : "Kambodschanischer Riel",
				   "displayName-count-one" : "Kambodschanischer Riel",
				   "symbol-alt-narrow" : "៛",
				   "displayName-count-other" : "Kambodschanische Riel"
				 },
				 "KMF" : {
				   "symbol" : "KMF",
				   "displayName" : "Komoren-Franc",
				   "displayName-count-one" : "Komoren-Franc",
				   "symbol-alt-narrow" : "FC",
				   "displayName-count-other" : "Komoren-Francs"
				 },
				 "KPW" : {
				   "symbol" : "KPW",
				   "displayName" : "Nordkoreanischer Won",
				   "displayName-count-one" : "Nordkoreanischer Won",
				   "symbol-alt-narrow" : "₩",
				   "displayName-count-other" : "Nordkoreanische Won"
				 },
				 "KRH" : {
				   "symbol" : "KRH",
				   "displayName" : "Südkoreanischer Hwan (1953–1962)",
				   "displayName-count-one" : "Südkoreanischer Hwan (1953–1962)",
				   "displayName-count-other" : "Südkoreanischer Hwan (1953–1962)"
				 },
				 "KRO" : {
				   "symbol" : "KRO",
				   "displayName" : "Südkoreanischer Won (1945–1953)",
				   "displayName-count-one" : "Südkoreanischer Won (1945–1953)",
				   "displayName-count-other" : "Südkoreanischer Won (1945–1953)"
				 },
				 "KRW" : {
				   "symbol" : "₩",
				   "displayName" : "Südkoreanischer Won",
				   "displayName-count-one" : "Südkoreanischer Won",
				   "symbol-alt-narrow" : "₩",
				   "displayName-count-other" : "Südkoreanische Won"
				 },
				 "KWD" : {
				   "symbol" : "KWD",
				   "displayName" : "Kuwait-Dinar",
				   "displayName-count-one" : "Kuwait-Dinar",
				   "displayName-count-other" : "Kuwait-Dinar"
				 },
				 "KYD" : {
				   "symbol" : "KYD",
				   "displayName" : "Kaiman-Dollar",
				   "displayName-count-one" : "Kaiman-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kaiman-Dollar"
				 },
				 "KZT" : {
				   "symbol" : "KZT",
				   "displayName" : "Kasachischer Tenge",
				   "displayName-count-one" : "Kasachischer Tenge",
				   "symbol-alt-narrow" : "₸",
				   "displayName-count-other" : "Kasachische Tenge"
				 },
				 "LAK" : {
				   "symbol" : "LAK",
				   "displayName" : "Laotischer Kip",
				   "displayName-count-one" : "Laotischer Kip",
				   "symbol-alt-narrow" : "₭",
				   "displayName-count-other" : "Laotische Kip"
				 },
				 "LBP" : {
				   "symbol" : "LBP",
				   "displayName" : "Libanesisches Pfund",
				   "displayName-count-one" : "Libanesisches Pfund",
				   "symbol-alt-narrow" : "L£",
				   "displayName-count-other" : "Libanesische Pfund"
				 },
				 "LKR" : {
				   "symbol" : "LKR",
				   "displayName" : "Sri-Lanka-Rupie",
				   "displayName-count-one" : "Sri-Lanka-Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Sri-Lanka-Rupien"
				 },
				 "LRD" : {
				   "symbol" : "LRD",
				   "displayName" : "Liberianischer Dollar",
				   "displayName-count-one" : "Liberianischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Liberianische Dollar"
				 },
				 "LSL" : {
				   "symbol" : "LSL",
				   "displayName" : "Loti",
				   "displayName-count-one" : "Loti",
				   "displayName-count-other" : "Loti"
				 },
				 "LTL" : {
				   "symbol" : "LTL",
				   "displayName" : "Litauischer Litas",
				   "displayName-count-one" : "Litauischer Litas",
				   "symbol-alt-narrow" : "Lt",
				   "displayName-count-other" : "Litauische Litas"
				 },
				 "LTT" : {
				   "symbol" : "LTT",
				   "displayName" : "Litauischer Talonas",
				   "displayName-count-one" : "Litauische Talonas",
				   "displayName-count-other" : "Litauische Talonas"
				 },
				 "LUC" : {
				   "symbol" : "LUC",
				   "displayName" : "Luxemburgischer Franc (konvertibel)",
				   "displayName-count-one" : "Luxemburgische Franc (konvertibel)",
				   "displayName-count-other" : "Luxemburgische Franc (konvertibel)"
				 },
				 "LUF" : {
				   "symbol" : "LUF",
				   "displayName" : "Luxemburgischer Franc",
				   "displayName-count-one" : "Luxemburgische Franc",
				   "displayName-count-other" : "Luxemburgische Franc"
				 },
				 "LUL" : {
				   "symbol" : "LUL",
				   "displayName" : "Luxemburgischer Finanz-Franc",
				   "displayName-count-one" : "Luxemburgische Finanz-Franc",
				   "displayName-count-other" : "Luxemburgische Finanz-Franc"
				 },
				 "LVL" : {
				   "symbol" : "LVL",
				   "displayName" : "Lettischer Lats",
				   "displayName-count-one" : "Lettischer Lats",
				   "symbol-alt-narrow" : "Ls",
				   "displayName-count-other" : "Lettische Lats"
				 },
				 "LVR" : {
				   "symbol" : "LVR",
				   "displayName" : "Lettischer Rubel",
				   "displayName-count-one" : "Lettische Rubel",
				   "displayName-count-other" : "Lettische Rubel"
				 },
				 "LYD" : {
				   "symbol" : "LYD",
				   "displayName" : "Libyscher Dinar",
				   "displayName-count-one" : "Libyscher Dinar",
				   "displayName-count-other" : "Libysche Dinar"
				 },
				 "MAD" : {
				   "symbol" : "MAD",
				   "displayName" : "Marokkanischer Dirham",
				   "displayName-count-one" : "Marokkanischer Dirham",
				   "displayName-count-other" : "Marokkanische Dirham"
				 },
				 "MAF" : {
				   "symbol" : "MAF",
				   "displayName" : "Marokkanischer Franc",
				   "displayName-count-one" : "Marokkanische Franc",
				   "displayName-count-other" : "Marokkanische Franc"
				 },
				 "MCF" : {
				   "symbol" : "MCF",
				   "displayName" : "Monegassischer Franc",
				   "displayName-count-one" : "Monegassischer Franc",
				   "displayName-count-other" : "Monegassische Franc"
				 },
				 "MDC" : {
				   "symbol" : "MDC",
				   "displayName" : "Moldau-Cupon",
				   "displayName-count-one" : "Moldau-Cupon",
				   "displayName-count-other" : "Moldau-Cupon"
				 },
				 "MDL" : {
				   "symbol" : "MDL",
				   "displayName" : "Moldau-Leu",
				   "displayName-count-one" : "Moldau-Leu",
				   "displayName-count-other" : "Moldau-Leu"
				 },
				 "MGA" : {
				   "symbol" : "MGA",
				   "displayName" : "Madagaskar-Ariary",
				   "displayName-count-one" : "Madagaskar-Ariary",
				   "symbol-alt-narrow" : "Ar",
				   "displayName-count-other" : "Madagaskar-Ariary"
				 },
				 "MGF" : {
				   "symbol" : "MGF",
				   "displayName" : "Madagaskar-Franc",
				   "displayName-count-one" : "Madagaskar-Franc",
				   "displayName-count-other" : "Madagaskar-Franc"
				 },
				 "MKD" : {
				   "symbol" : "MKD",
				   "displayName" : "Mazedonischer Denar",
				   "displayName-count-one" : "Mazedonischer Denar",
				   "displayName-count-other" : "Mazedonische Denari"
				 },
				 "MKN" : {
				   "symbol" : "MKN",
				   "displayName" : "Mazedonischer Denar (1992–1993)",
				   "displayName-count-one" : "Mazedonischer Denar (1992–1993)",
				   "displayName-count-other" : "Mazedonische Denar (1992–1993)"
				 },
				 "MLF" : {
				   "symbol" : "MLF",
				   "displayName" : "Malischer Franc",
				   "displayName-count-one" : "Malische Franc",
				   "displayName-count-other" : "Malische Franc"
				 },
				 "MMK" : {
				   "symbol" : "MMK",
				   "displayName" : "Myanmarischer Kyat",
				   "displayName-count-one" : "Myanmarischer Kyat",
				   "symbol-alt-narrow" : "K",
				   "displayName-count-other" : "Myanmarische Kyat"
				 },
				 "MNT" : {
				   "symbol" : "MNT",
				   "displayName" : "Mongolischer Tögrög",
				   "displayName-count-one" : "Mongolischer Tögrög",
				   "symbol-alt-narrow" : "₮",
				   "displayName-count-other" : "Mongolische Tögrög"
				 },
				 "MOP" : {
				   "symbol" : "MOP",
				   "displayName" : "Macao-Pataca",
				   "displayName-count-one" : "Macao-Pataca",
				   "displayName-count-other" : "Macao-Pataca"
				 },
				 "MRO" : {
				   "symbol" : "MRO",
				   "displayName" : "Mauretanischer Ouguiya",
				   "displayName-count-one" : "Mauretanischer Ouguiya",
				   "displayName-count-other" : "Mauretanische Ouguiya"
				 },
				 "MTL" : {
				   "symbol" : "MTL",
				   "displayName" : "Maltesische Lira",
				   "displayName-count-one" : "Maltesische Lira",
				   "displayName-count-other" : "Maltesische Lira"
				 },
				 "MTP" : {
				   "symbol" : "MTP",
				   "displayName" : "Maltesisches Pfund",
				   "displayName-count-one" : "Maltesische Pfund",
				   "displayName-count-other" : "Maltesische Pfund"
				 },
				 "MUR" : {
				   "symbol" : "MUR",
				   "displayName" : "Mauritius-Rupie",
				   "displayName-count-one" : "Mauritius-Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Mauritius-Rupien"
				 },
				 "MVP" : {
				   "symbol" : "MVP",
				   "displayName" : "Malediven-Rupie (alt)",
				   "displayName-count-one" : "Malediven-Rupie (alt)",
				   "displayName-count-other" : "Malediven-Rupien (alt)"
				 },
				 "MVR" : {
				   "symbol" : "MVR",
				   "displayName" : "Malediven-Rufiyaa",
				   "displayName-count-one" : "Malediven-Rufiyaa",
				   "displayName-count-other" : "Malediven-Rupien"
				 },
				 "MWK" : {
				   "symbol" : "MWK",
				   "displayName" : "Malawi-Kwacha",
				   "displayName-count-one" : "Malawi-Kwacha",
				   "displayName-count-other" : "Malawi-Kwacha"
				 },
				 "MXN" : {
				   "symbol" : "MX$",
				   "displayName" : "Mexikanischer Peso",
				   "displayName-count-one" : "Mexikanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Mexikanische Pesos"
				 },
				 "MXP" : {
				   "symbol" : "MXP",
				   "displayName" : "Mexikanischer Silber-Peso (1861–1992)",
				   "displayName-count-one" : "Mexikanische Silber-Peso (1861–1992)",
				   "displayName-count-other" : "Mexikanische Silber-Pesos (1861–1992)"
				 },
				 "MXV" : {
				   "symbol" : "MXV",
				   "displayName" : "Mexicanischer Unidad de Inversion (UDI)",
				   "displayName-count-one" : "Mexicanischer Unidad de Inversion (UDI)",
				   "displayName-count-other" : "Mexikanische Unidad de Inversion (UDI)"
				 },
				 "MYR" : {
				   "symbol" : "MYR",
				   "displayName" : "Malaysischer Ringgit",
				   "displayName-count-one" : "Malaysischer Ringgit",
				   "symbol-alt-narrow" : "RM",
				   "displayName-count-other" : "Malaysische Ringgit"
				 },
				 "MZE" : {
				   "symbol" : "MZE",
				   "displayName" : "Mosambikanischer Escudo",
				   "displayName-count-one" : "Mozambikanische Escudo",
				   "displayName-count-other" : "Mozambikanische Escudo"
				 },
				 "MZM" : {
				   "symbol" : "MZM",
				   "displayName" : "Mosambikanischer Metical (1980–2006)",
				   "displayName-count-one" : "Mosambikanischer Metical (1980–2006)",
				   "displayName-count-other" : "Mosambikanische Meticais (1980–2006)"
				 },
				 "MZN" : {
				   "symbol" : "MZN",
				   "displayName" : "Mosambikanischer Metical",
				   "displayName-count-one" : "Mosambikanischer Metical",
				   "displayName-count-other" : "Mosambikanische Meticais"
				 },
				 "NAD" : {
				   "symbol" : "NAD",
				   "displayName" : "Namibia-Dollar",
				   "displayName-count-one" : "Namibia-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Namibia-Dollar"
				 },
				 "NGN" : {
				   "symbol" : "NGN",
				   "displayName" : "Nigerianischer Naira",
				   "displayName-count-one" : "Nigerianischer Naira",
				   "symbol-alt-narrow" : "₦",
				   "displayName-count-other" : "Nigerianische Naira"
				 },
				 "NIC" : {
				   "symbol" : "NIC",
				   "displayName" : "Nicaraguanischer Córdoba (1988–1991)",
				   "displayName-count-one" : "Nicaraguanischer Córdoba (1988–1991)",
				   "displayName-count-other" : "Nicaraguanische Córdoba (1988–1991)"
				 },
				 "NIO" : {
				   "symbol" : "NIO",
				   "displayName" : "Nicaragua-Córdoba",
				   "displayName-count-one" : "Nicaragua-Córdoba",
				   "symbol-alt-narrow" : "C$",
				   "displayName-count-other" : "Nicaragua-Córdobas"
				 },
				 "NLG" : {
				   "symbol" : "NLG",
				   "displayName" : "Niederländischer Gulden",
				   "displayName-count-one" : "Niederländischer Gulden",
				   "displayName-count-other" : "Niederländische Gulden"
				 },
				 "NOK" : {
				   "symbol" : "NOK",
				   "displayName" : "Norwegische Krone",
				   "displayName-count-one" : "Norwegische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Norwegische Kronen"
				 },
				 "NPR" : {
				   "symbol" : "NPR",
				   "displayName" : "Nepalesische Rupie",
				   "displayName-count-one" : "Nepalesische Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Nepalesische Rupien"
				 },
				 "NZD" : {
				   "symbol" : "NZ$",
				   "displayName" : "Neuseeland-Dollar",
				   "displayName-count-one" : "Neuseeland-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Neuseeland-Dollar"
				 },
				 "OMR" : {
				   "symbol" : "OMR",
				   "displayName" : "Omanischer Rial",
				   "displayName-count-one" : "Omanischer Rial",
				   "displayName-count-other" : "Omanische Rials"
				 },
				 "PAB" : {
				   "symbol" : "PAB",
				   "displayName" : "Panamaischer Balboa",
				   "displayName-count-one" : "Panamaischer Balboa",
				   "displayName-count-other" : "Panamaische Balboas"
				 },
				 "PEI" : {
				   "symbol" : "PEI",
				   "displayName" : "Peruanischer Inti",
				   "displayName-count-one" : "Peruanische Inti",
				   "displayName-count-other" : "Peruanische Inti"
				 },
				 "PEN" : {
				   "symbol" : "PEN",
				   "displayName" : "Peruanischer Sol",
				   "displayName-count-one" : "Peruanischer Sol",
				   "displayName-count-other" : "Peruanische Sol"
				 },
				 "PES" : {
				   "symbol" : "PES",
				   "displayName" : "Peruanischer Sol (1863–1965)",
				   "displayName-count-one" : "Peruanischer Sol (1863–1965)",
				   "displayName-count-other" : "Peruanische Sol (1863–1965)"
				 },
				 "PGK" : {
				   "symbol" : "PGK",
				   "displayName" : "Papua-Neuguineischer Kina",
				   "displayName-count-one" : "Papua-Neuguineischer Kina",
				   "displayName-count-other" : "Papua-Neuguineische Kina"
				 },
				 "PHP" : {
				   "symbol" : "PHP",
				   "displayName" : "Philippinischer Peso",
				   "displayName-count-one" : "Philippinischer Peso",
				   "symbol-alt-narrow" : "₱",
				   "displayName-count-other" : "Philippinische Pesos"
				 },
				 "PKR" : {
				   "symbol" : "PKR",
				   "displayName" : "Pakistanische Rupie",
				   "displayName-count-one" : "Pakistanische Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Pakistanische Rupien"
				 },
				 "PLN" : {
				   "symbol" : "PLN",
				   "displayName" : "Polnischer Złoty",
				   "displayName-count-one" : "Polnischer Złoty",
				   "symbol-alt-narrow" : "zł",
				   "displayName-count-other" : "Polnische Złoty"
				 },
				 "PLZ" : {
				   "symbol" : "PLZ",
				   "displayName" : "Polnischer Zloty (1950–1995)",
				   "displayName-count-one" : "Polnischer Zloty (1950–1995)",
				   "displayName-count-other" : "Polnische Zloty (1950–1995)"
				 },
				 "PTE" : {
				   "symbol" : "PTE",
				   "displayName" : "Portugiesischer Escudo",
				   "displayName-count-one" : "Portugiesische Escudo",
				   "displayName-count-other" : "Portugiesische Escudo"
				 },
				 "PYG" : {
				   "symbol" : "PYG",
				   "displayName" : "Paraguayischer Guaraní",
				   "displayName-count-one" : "Paraguayischer Guaraní",
				   "symbol-alt-narrow" : "₲",
				   "displayName-count-other" : "Paraguayische Guaraníes"
				 },
				 "QAR" : {
				   "symbol" : "QAR",
				   "displayName" : "Katar-Riyal",
				   "displayName-count-one" : "Katar-Riyal",
				   "displayName-count-other" : "Katar-Riyal"
				 },
				 "RHD" : {
				   "symbol" : "RHD",
				   "displayName" : "Rhodesischer Dollar",
				   "displayName-count-one" : "Rhodesische Dollar",
				   "displayName-count-other" : "Rhodesische Dollar"
				 },
				 "ROL" : {
				   "symbol" : "ROL",
				   "displayName" : "Rumänischer Leu (1952–2006)",
				   "displayName-count-one" : "Rumänischer Leu (1952–2006)",
				   "displayName-count-other" : "Rumänische Leu (1952–2006)"
				 },
				 "RON" : {
				   "symbol" : "RON",
				   "displayName" : "Rumänischer Leu",
				   "displayName-count-one" : "Rumänischer Leu",
				   "symbol-alt-narrow" : "L",
				   "displayName-count-other" : "Rumänische Leu"
				 },
				 "RSD" : {
				   "symbol" : "RSD",
				   "displayName" : "Serbischer Dinar",
				   "displayName-count-one" : "Serbischer Dinar",
				   "displayName-count-other" : "Serbische Dinaren"
				 },
				 "RUB" : {
				   "symbol" : "RUB",
				   "displayName" : "Russischer Rubel",
				   "displayName-count-one" : "Russischer Rubel",
				   "symbol-alt-narrow" : "₽",
				   "displayName-count-other" : "Russische Rubel"
				 },
				 "RUR" : {
				   "symbol" : "RUR",
				   "displayName" : "Russischer Rubel (1991–1998)",
				   "displayName-count-one" : "Russischer Rubel (1991–1998)",
				   "symbol-alt-narrow" : "р.",
				   "displayName-count-other" : "Russische Rubel (1991–1998)"
				 },
				 "RWF" : {
				   "symbol" : "RWF",
				   "displayName" : "Ruanda-Franc",
				   "displayName-count-one" : "Ruanda-Franc",
				   "symbol-alt-narrow" : "F.Rw",
				   "displayName-count-other" : "Ruanda-Francs"
				 },
				 "SAR" : {
				   "symbol" : "SAR",
				   "displayName" : "Saudi-Rial",
				   "displayName-count-one" : "Saudi-Rial",
				   "displayName-count-other" : "Saudi-Rial"
				 },
				 "SBD" : {
				   "symbol" : "SBD",
				   "displayName" : "Salomonen-Dollar",
				   "displayName-count-one" : "Salomonen-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Salomonen-Dollar"
				 },
				 "SCR" : {
				   "symbol" : "SCR",
				   "displayName" : "Seychellen-Rupie",
				   "displayName-count-one" : "Seychellen-Rupie",
				   "displayName-count-other" : "Seychellen-Rupien"
				 },
				 "SDD" : {
				   "symbol" : "SDD",
				   "displayName" : "Sudanesischer Dinar (1992–2007)",
				   "displayName-count-one" : "Sudanesischer Dinar (1992–2007)",
				   "displayName-count-other" : "Sudanesische Dinar (1992–2007)"
				 },
				 "SDG" : {
				   "symbol" : "SDG",
				   "displayName" : "Sudanesisches Pfund",
				   "displayName-count-one" : "Sudanesisches Pfund",
				   "displayName-count-other" : "Sudanesische Pfund"
				 },
				 "SDP" : {
				   "symbol" : "SDP",
				   "displayName" : "Sudanesisches Pfund (1957–1998)",
				   "displayName-count-one" : "Sudanesisches Pfund (1957–1998)",
				   "displayName-count-other" : "Sudanesische Pfund (1957–1998)"
				 },
				 "SEK" : {
				   "symbol" : "SEK",
				   "displayName" : "Schwedische Krone",
				   "displayName-count-one" : "Schwedische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Schwedische Kronen"
				 },
				 "SGD" : {
				   "symbol" : "SGD",
				   "displayName" : "Singapur-Dollar",
				   "displayName-count-one" : "Singapur-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Singapur-Dollar"
				 },
				 "SHP" : {
				   "symbol" : "SHP",
				   "displayName" : "St. Helena-Pfund",
				   "displayName-count-one" : "St. Helena-Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "St. Helena-Pfund"
				 },
				 "SIT" : {
				   "symbol" : "SIT",
				   "displayName" : "Slowenischer Tolar",
				   "displayName-count-one" : "Slowenischer Tolar",
				   "displayName-count-other" : "Slowenische Tolar"
				 },
				 "SKK" : {
				   "symbol" : "SKK",
				   "displayName" : "Slowakische Krone",
				   "displayName-count-one" : "Slowakische Kronen",
				   "displayName-count-other" : "Slowakische Kronen"
				 },
				 "SLL" : {
				   "symbol" : "SLL",
				   "displayName" : "Sierra-leonischer Leone",
				   "displayName-count-one" : "Sierra-leonischer Leone",
				   "displayName-count-other" : "Sierra-leonische Leones"
				 },
				 "SOS" : {
				   "symbol" : "SOS",
				   "displayName" : "Somalia-Schilling",
				   "displayName-count-one" : "Somalia-Schilling",
				   "displayName-count-other" : "Somalia-Schilling"
				 },
				 "SRD" : {
				   "symbol" : "SRD",
				   "displayName" : "Suriname-Dollar",
				   "displayName-count-one" : "Suriname-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Suriname-Dollar"
				 },
				 "SRG" : {
				   "symbol" : "SRG",
				   "displayName" : "Suriname Gulden",
				   "displayName-count-one" : "Suriname-Gulden",
				   "displayName-count-other" : "Suriname-Gulden"
				 },
				 "SSP" : {
				   "symbol" : "SSP",
				   "displayName" : "Südsudanesisches Pfund",
				   "displayName-count-one" : "Südsudanesisches Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Südsudanesische Pfund"
				 },
				 "STD" : {
				   "symbol" : "STD",
				   "displayName" : "São-toméischer Dobra",
				   "displayName-count-one" : "São-toméischer Dobra",
				   "symbol-alt-narrow" : "Db",
				   "displayName-count-other" : "São-toméische Dobra"
				 },
				 "STN" : {
				   "symbol" : "STN",
				   "displayName" : "STN"
				 },
				 "SUR" : {
				   "symbol" : "SUR",
				   "displayName" : "Sowjetischer Rubel",
				   "displayName-count-one" : "Sowjetische Rubel",
				   "displayName-count-other" : "Sowjetische Rubel"
				 },
				 "SVC" : {
				   "symbol" : "SVC",
				   "displayName" : "El Salvador Colon",
				   "displayName-count-one" : "El Salvador-Colon",
				   "displayName-count-other" : "El Salvador-Colon"
				 },
				 "SYP" : {
				   "symbol" : "SYP",
				   "displayName" : "Syrisches Pfund",
				   "displayName-count-one" : "Syrisches Pfund",
				   "symbol-alt-narrow" : "SYP",
				   "displayName-count-other" : "Syrische Pfund"
				 },
				 "SZL" : {
				   "symbol" : "SZL",
				   "displayName" : "Swasiländischer Lilangeni",
				   "displayName-count-one" : "Swasiländischer Lilangeni",
				   "displayName-count-other" : "Swasiländische Emalangeni"
				 },
				 "THB" : {
				   "symbol" : "฿",
				   "displayName" : "Thailändischer Baht",
				   "displayName-count-one" : "Thailändischer Baht",
				   "symbol-alt-narrow" : "฿",
				   "displayName-count-other" : "Thailändische Baht"
				 },
				 "TJR" : {
				   "symbol" : "TJR",
				   "displayName" : "Tadschikistan Rubel",
				   "displayName-count-one" : "Tadschikistan-Rubel",
				   "displayName-count-other" : "Tadschikistan-Rubel"
				 },
				 "TJS" : {
				   "symbol" : "TJS",
				   "displayName" : "Tadschikistan-Somoni",
				   "displayName-count-one" : "Tadschikistan-Somoni",
				   "displayName-count-other" : "Tadschikistan-Somoni"
				 },
				 "TMM" : {
				   "symbol" : "TMM",
				   "displayName" : "Turkmenistan-Manat (1993–2009)",
				   "displayName-count-one" : "Turkmenistan-Manat (1993–2009)",
				   "displayName-count-other" : "Turkmenistan-Manat (1993–2009)"
				 },
				 "TMT" : {
				   "symbol" : "TMT",
				   "displayName" : "Turkmenistan-Manat",
				   "displayName-count-one" : "Turkmenistan-Manat",
				   "displayName-count-other" : "Turkmenistan-Manat"
				 },
				 "TND" : {
				   "symbol" : "TND",
				   "displayName" : "Tunesischer Dinar",
				   "displayName-count-one" : "Tunesischer Dinar",
				   "displayName-count-other" : "Tunesische Dinar"
				 },
				 "TOP" : {
				   "symbol" : "TOP",
				   "displayName" : "Tongaischer Paʻanga",
				   "displayName-count-one" : "Tongaischer Paʻanga",
				   "symbol-alt-narrow" : "T$",
				   "displayName-count-other" : "Tongaische Paʻanga"
				 },
				 "TPE" : {
				   "symbol" : "TPE",
				   "displayName" : "Timor-Escudo",
				   "displayName-count-one" : "Timor-Escudo",
				   "displayName-count-other" : "Timor-Escudo"
				 },
				 "TRL" : {
				   "symbol" : "TRL",
				   "displayName" : "Türkische Lira (1922–2005)",
				   "displayName-count-one" : "Türkische Lira (1922–2005)",
				   "displayName-count-other" : "Türkische Lira (1922–2005)"
				 },
				 "TRY" : {
				   "symbol" : "TRY",
				   "symbol-alt-variant" : "TL",
				   "displayName" : "Türkische Lira",
				   "displayName-count-one" : "Türkische Lira",
				   "symbol-alt-narrow" : "₺",
				   "displayName-count-other" : "Türkische Lira"
				 },
				 "TTD" : {
				   "symbol" : "TTD",
				   "displayName" : "Trinidad und Tobago-Dollar",
				   "displayName-count-one" : "Trinidad und Tobago-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Trinidad und Tobago-Dollar"
				 },
				 "TWD" : {
				   "symbol" : "NT$",
				   "displayName" : "Neuer Taiwan-Dollar",
				   "displayName-count-one" : "Neuer Taiwan-Dollar",
				   "symbol-alt-narrow" : "NT$",
				   "displayName-count-other" : "Neue Taiwan-Dollar"
				 },
				 "TZS" : {
				   "symbol" : "TZS",
				   "displayName" : "Tansania-Schilling",
				   "displayName-count-one" : "Tansania-Schilling",
				   "displayName-count-other" : "Tansania-Schilling"
				 },
				 "UAH" : {
				   "symbol" : "UAH",
				   "displayName" : "Ukrainische Hrywnja",
				   "displayName-count-one" : "Ukrainische Hrywnja",
				   "symbol-alt-narrow" : "₴",
				   "displayName-count-other" : "Ukrainische Hrywen"
				 },
				 "UAK" : {
				   "symbol" : "UAK",
				   "displayName" : "Ukrainischer Karbovanetz",
				   "displayName-count-one" : "Ukrainische Karbovanetz",
				   "displayName-count-other" : "Ukrainische Karbovanetz"
				 },
				 "UGS" : {
				   "symbol" : "UGS",
				   "displayName" : "Uganda-Schilling (1966–1987)",
				   "displayName-count-one" : "Uganda-Schilling (1966–1987)",
				   "displayName-count-other" : "Uganda-Schilling (1966–1987)"
				 },
				 "UGX" : {
				   "symbol" : "UGX",
				   "displayName" : "Uganda-Schilling",
				   "displayName-count-one" : "Uganda-Schilling",
				   "displayName-count-other" : "Uganda-Schilling"
				 },
				 "USD" : {
				   "symbol" : "$",
				   "displayName" : "US-Dollar",
				   "displayName-count-one" : "US-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "US-Dollar"
				 },
				 "USN" : {
				   "symbol" : "USN",
				   "displayName" : "US Dollar (Nächster Tag)",
				   "displayName-count-one" : "US-Dollar (Nächster Tag)",
				   "displayName-count-other" : "US-Dollar (Nächster Tag)"
				 },
				 "USS" : {
				   "symbol" : "USS",
				   "displayName" : "US Dollar (Gleicher Tag)",
				   "displayName-count-one" : "US-Dollar (Gleicher Tag)",
				   "displayName-count-other" : "US-Dollar (Gleicher Tag)"
				 },
				 "UYI" : {
				   "symbol" : "UYI",
				   "displayName" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)",
				   "displayName-count-one" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)",
				   "displayName-count-other" : "Uruguayische Pesos (Indexierte Rechnungseinheiten)"
				 },
				 "UYP" : {
				   "symbol" : "UYP",
				   "displayName" : "Uruguayischer Peso (1975–1993)",
				   "displayName-count-one" : "Uruguayischer Peso (1975–1993)",
				   "displayName-count-other" : "Uruguayische Pesos (1975–1993)"
				 },
				 "UYU" : {
				   "symbol" : "UYU",
				   "displayName" : "Uruguayischer Peso",
				   "displayName-count-one" : "Uruguayischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Uruguayische Pesos"
				 },
				 "UZS" : {
				   "symbol" : "UZS",
				   "displayName" : "Usbekistan-Sum",
				   "displayName-count-one" : "Usbekistan-Sum",
				   "displayName-count-other" : "Usbekistan-Sum"
				 },
				 "VEB" : {
				   "symbol" : "VEB",
				   "displayName" : "Venezolanischer Bolívar (1871–2008)",
				   "displayName-count-one" : "Venezolanischer Bolívar (1871–2008)",
				   "displayName-count-other" : "Venezolanische Bolívares (1871–2008)"
				 },
				 "VEF" : {
				   "symbol" : "VEF",
				   "displayName" : "Venezolanischer Bolívar",
				   "displayName-count-one" : "Venezolanischer Bolívar",
				   "symbol-alt-narrow" : "Bs",
				   "displayName-count-other" : "Venezolanische Bolívares"
				 },
				 "VND" : {
				   "symbol" : "₫",
				   "displayName" : "Vietnamesischer Dong",
				   "displayName-count-one" : "Vietnamesischer Dong",
				   "symbol-alt-narrow" : "₫",
				   "displayName-count-other" : "Vietnamesische Dong"
				 },
				 "VNN" : {
				   "symbol" : "VNN",
				   "displayName" : "Vietnamesischer Dong(1978–1985)",
				   "displayName-count-one" : "Vietnamesischer Dong(1978–1985)",
				   "displayName-count-other" : "Vietnamesische Dong(1978–1985)"
				 },
				 "VUV" : {
				   "symbol" : "VUV",
				   "displayName" : "Vanuatu-Vatu",
				   "displayName-count-one" : "Vanuatu-Vatu",
				   "displayName-count-other" : "Vanuatu-Vatu"
				 },
				 "WST" : {
				   "symbol" : "WST",
				   "displayName" : "Samoanischer Tala",
				   "displayName-count-one" : "Samoanischer Tala",
				   "displayName-count-other" : "Samoanische Tala"
				 },
				 "XAF" : {
				   "symbol" : "FCFA",
				   "displayName" : "CFA-Franc (BEAC)",
				   "displayName-count-one" : "CFA-Franc (BEAC)",
				   "displayName-count-other" : "CFA-Franc (BEAC)"
				 },
				 "XAG" : {
				   "symbol" : "XAG",
				   "displayName" : "Unze Silber",
				   "displayName-count-one" : "Unze Silber",
				   "displayName-count-other" : "Unzen Silber"
				 },
				 "XAU" : {
				   "symbol" : "XAU",
				   "displayName" : "Unze Gold",
				   "displayName-count-one" : "Unze Gold",
				   "displayName-count-other" : "Unzen Gold"
				 },
				 "XBA" : {
				   "symbol" : "XBA",
				   "displayName" : "Europäische Rechnungseinheit",
				   "displayName-count-one" : "Europäische Rechnungseinheiten",
				   "displayName-count-other" : "Europäische Rechnungseinheiten"
				 },
				 "XBB" : {
				   "symbol" : "XBB",
				   "displayName" : "Europäische Währungseinheit (XBB)",
				   "displayName-count-one" : "Europäische Währungseinheiten (XBB)",
				   "displayName-count-other" : "Europäische Währungseinheiten (XBB)"
				 },
				 "XBC" : {
				   "symbol" : "XBC",
				   "displayName" : "Europäische Rechnungseinheit (XBC)",
				   "displayName-count-one" : "Europäische Rechnungseinheiten (XBC)",
				   "displayName-count-other" : "Europäische Rechnungseinheiten (XBC)"
				 },
				 "XBD" : {
				   "symbol" : "XBD",
				   "displayName" : "Europäische Rechnungseinheit (XBD)",
				   "displayName-count-one" : "Europäische Rechnungseinheiten (XBD)",
				   "displayName-count-other" : "Europäische Rechnungseinheiten (XBD)"
				 },
				 "XCD" : {
				   "symbol" : "EC$",
				   "displayName" : "Ostkaribischer Dollar",
				   "displayName-count-one" : "Ostkaribischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Ostkaribische Dollar"
				 },
				 "XDR" : {
				   "symbol" : "XDR",
				   "displayName" : "Sonderziehungsrechte",
				   "displayName-count-one" : "Sonderziehungsrechte",
				   "displayName-count-other" : "Sonderziehungsrechte"
				 },
				 "XEU" : {
				   "symbol" : "XEU",
				   "displayName" : "Europäische Währungseinheit (XEU)",
				   "displayName-count-one" : "Europäische Währungseinheiten (XEU)",
				   "displayName-count-other" : "Europäische Währungseinheiten (XEU)"
				 },
				 "XFO" : {
				   "symbol" : "XFO",
				   "displayName" : "Französischer Gold-Franc",
				   "displayName-count-one" : "Französische Gold-Franc",
				   "displayName-count-other" : "Französische Gold-Franc"
				 },
				 "XFU" : {
				   "symbol" : "XFU",
				   "displayName" : "Französischer UIC-Franc",
				   "displayName-count-one" : "Französische UIC-Franc",
				   "displayName-count-other" : "Französische UIC-Franc"
				 },
				 "XOF" : {
				   "symbol" : "CFA",
				   "displayName" : "CFA-Franc (BCEAO)",
				   "displayName-count-one" : "CFA-Franc (BCEAO)",
				   "displayName-count-other" : "CFA-Francs (BCEAO)"
				 },
				 "XPD" : {
				   "symbol" : "XPD",
				   "displayName" : "Unze Palladium",
				   "displayName-count-one" : "Unze Palladium",
				   "displayName-count-other" : "Unzen Palladium"
				 },
				 "XPF" : {
				   "symbol" : "CFPF",
				   "displayName" : "CFP-Franc",
				   "displayName-count-one" : "CFP-Franc",
				   "displayName-count-other" : "CFP-Franc"
				 },
				 "XPT" : {
				   "symbol" : "XPT",
				   "displayName" : "Unze Platin",
				   "displayName-count-one" : "Unze Platin",
				   "displayName-count-other" : "Unzen Platin"
				 },
				 "XRE" : {
				   "symbol" : "XRE",
				   "displayName" : "RINET Funds",
				   "displayName-count-one" : "RINET Funds",
				   "displayName-count-other" : "RINET Funds"
				 },
				 "XSU" : {
				   "symbol" : "XSU",
				   "displayName" : "SUCRE",
				   "displayName-count-one" : "SUCRE",
				   "displayName-count-other" : "SUCRE"
				 },
				 "XTS" : {
				   "symbol" : "XTS",
				   "displayName" : "Testwährung",
				   "displayName-count-one" : "Testwährung",
				   "displayName-count-other" : "Testwährung"
				 },
				 "XUA" : {
				   "symbol" : "XUA",
				   "displayName" : "Rechnungseinheit der AfEB",
				   "displayName-count-one" : "Rechnungseinheit der AfEB",
				   "displayName-count-other" : "Rechnungseinheiten der AfEB"
				 },
				 "XXX" : {
				   "symbol" : "XXX",
				   "displayName" : "Unbekannte Währung",
				   "displayName-count-one" : "(unbekannte Währung)",
				   "displayName-count-other" : "(unbekannte Währung)"
				 },
				 "YDD" : {
				   "symbol" : "YDD",
				   "displayName" : "Jemen-Dinar",
				   "displayName-count-one" : "Jemen-Dinar",
				   "displayName-count-other" : "Jemen-Dinar"
				 },
				 "YER" : {
				   "symbol" : "YER",
				   "displayName" : "Jemen-Rial",
				   "displayName-count-one" : "Jemen-Rial",
				   "displayName-count-other" : "Jemen-Rial"
				 },
				 "YUD" : {
				   "symbol" : "YUD",
				   "displayName" : "Jugoslawischer Dinar (1966–1990)",
				   "displayName-count-one" : "Jugoslawischer Dinar (1966–1990)",
				   "displayName-count-other" : "Jugoslawische Dinar (1966–1990)"
				 },
				 "YUM" : {
				   "symbol" : "YUM",
				   "displayName" : "Jugoslawischer Neuer Dinar (1994–2002)",
				   "displayName-count-one" : "Jugoslawischer Neuer Dinar (1994–2002)",
				   "displayName-count-other" : "Jugoslawische Neue Dinar (1994–2002)"
				 },
				 "YUN" : {
				   "symbol" : "YUN",
				   "displayName" : "Jugoslawischer Dinar (konvertibel)",
				   "displayName-count-one" : "Jugoslawische Dinar (konvertibel)",
				   "displayName-count-other" : "Jugoslawische Dinar (konvertibel)"
				 },
				 "YUR" : {
				   "symbol" : "YUR",
				   "displayName" : "Jugoslawischer reformierter Dinar (1992–1993)",
				   "displayName-count-one" : "Jugoslawischer reformierter Dinar (1992–1993)",
				   "displayName-count-other" : "Jugoslawische reformierte Dinar (1992–1993)"
				 },
				 "ZAL" : {
				   "symbol" : "ZAL",
				   "displayName" : "Südafrikanischer Rand (Finanz)",
				   "displayName-count-one" : "Südafrikanischer Rand (Finanz)",
				   "displayName-count-other" : "Südafrikanischer Rand (Finanz)"
				 },
				 "ZAR" : {
				   "symbol" : "ZAR",
				   "displayName" : "Südafrikanischer Rand",
				   "displayName-count-one" : "Südafrikanischer Rand",
				   "symbol-alt-narrow" : "R",
				   "displayName-count-other" : "Südafrikanische Rand"
				 },
				 "ZMK" : {
				   "symbol" : "ZMK",
				   "displayName" : "Kwacha (1968–2012)",
				   "displayName-count-one" : "Kwacha (1968–2012)",
				   "displayName-count-other" : "Kwacha (1968–2012)"
				 },
				 "ZMW" : {
				   "symbol" : "ZMW",
				   "displayName" : "Kwacha",
				   "displayName-count-one" : "Kwacha",
				   "symbol-alt-narrow" : "K",
				   "displayName-count-other" : "Kwacha"
				 },
				 "ZRN" : {
				   "symbol" : "ZRN",
				   "displayName" : "Zaire-Neuer Zaïre (1993–1998)",
				   "displayName-count-one" : "Zaire-Neuer Zaïre (1993–1998)",
				   "displayName-count-other" : "Zaire-Neue Zaïre (1993–1998)"
				 },
				 "ZRZ" : {
				   "symbol" : "ZRZ",
				   "displayName" : "Zaire-Zaïre (1971–1993)",
				   "displayName-count-one" : "Zaire-Zaïre (1971–1993)",
				   "displayName-count-other" : "Zaire-Zaïre (1971–1993)"
				 },
				 "ZWD" : {
				   "symbol" : "ZWD",
				   "displayName" : "Simbabwe-Dollar (1980–2008)",
				   "displayName-count-one" : "Simbabwe-Dollar (1980–2008)",
				   "displayName-count-other" : "Simbabwe-Dollar (1980–2008)"
				 },
				 "ZWL" : {
				   "symbol" : "ZWL",
				   "displayName" : "Simbabwe-Dollar (2009)",
				   "displayName-count-one" : "Simbabwe-Dollar (2009)",
				   "displayName-count-other" : "Simbabwe-Dollar (2009)"
				 },
				 "ZWR" : {
				   "symbol" : "ZWR",
				   "displayName" : "Simbabwe-Dollar (2008)",
				   "displayName-count-one" : "Simbabwe-Dollar (2008)",
				   "displayName-count-other" : "Simbabwe-Dollar (2008)"
				 }
			   },"numbers":{
				 "defaultNumberingSystem" : "latn",
				 "numberSymbols" : {
				   "decimal" : ",",
				   "group" : ".",
				   "list" : ";",
				   "percentSign" : "%",
				   "plusSign" : "+",
				   "minusSign" : "-",
				   "exponential" : "E",
				   "superscriptingExponent" : "·",
				   "perMille" : "‰",
				   "infinity" : "∞",
				   "nan" : "NaN",
				   "timeSeparator" : ":"
				 },
				 "numberFormats" : {
				   "decimalFormats" : "#,##0.###",
				   "percentFormats" : "#,##0 %",
				   "currencyFormats" : "#,##0.00 ¤",
				   "scientificFormats" : "#E0",
				   "decimalFormats-long" : {
					 "decimalFormat" : {
					   "1000-count-one" : "0 Tausend",
					   "1000-count-other" : "0 Tausend",
					   "10000-count-one" : "00 Tausend",
					   "10000-count-other" : "00 Tausend",
					   "100000-count-one" : "000 Tausend",
					   "100000-count-other" : "000 Tausend",
					   "1000000-count-one" : "0 Million",
					   "1000000-count-other" : "0 Millionen",
					   "10000000-count-one" : "00 Millionen",
					   "10000000-count-other" : "00 Millionen",
					   "100000000-count-one" : "000 Millionen",
					   "100000000-count-other" : "000 Millionen",
					   "1000000000-count-one" : "0 Milliarde",
					   "1000000000-count-other" : "0 Milliarden",
					   "10000000000-count-one" : "00 Milliarden",
					   "10000000000-count-other" : "00 Milliarden",
					   "100000000000-count-one" : "000 Milliarden",
					   "100000000000-count-other" : "000 Milliarden",
					   "1000000000000-count-one" : "0 Billion",
					   "1000000000000-count-other" : "0 Billionen",
					   "10000000000000-count-one" : "00 Billionen",
					   "10000000000000-count-other" : "00 Billionen",
					   "100000000000000-count-one" : "000 Billionen",
					   "100000000000000-count-other" : "000 Billionen"
					 }
				   },
				   "decimalFormats-short" : {
					 "decimalFormat" : {
					   "1000-count-one" : "0 Tsd'.'",
					   "1000-count-other" : "0 Tsd'.'",
					   "10000-count-one" : "00 Tsd'.'",
					   "10000-count-other" : "00 Tsd'.'",
					   "100000-count-one" : "000 Tsd'.'",
					   "100000-count-other" : "000 Tsd'.'",
					   "1000000-count-one" : "0 Mio'.'",
					   "1000000-count-other" : "0 Mio'.'",
					   "10000000-count-one" : "00 Mio'.'",
					   "10000000-count-other" : "00 Mio'.'",
					   "100000000-count-one" : "000 Mio'.'",
					   "100000000-count-other" : "000 Mio'.'",
					   "1000000000-count-one" : "0 Mrd'.'",
					   "1000000000-count-other" : "0 Mrd'.'",
					   "10000000000-count-one" : "00 Mrd'.'",
					   "10000000000-count-other" : "00 Mrd'.'",
					   "100000000000-count-one" : "000 Mrd'.'",
					   "100000000000-count-other" : "000 Mrd'.'",
					   "1000000000000-count-one" : "0 Bio'.'",
					   "1000000000000-count-other" : "0 Bio'.'",
					   "10000000000000-count-one" : "00 Bio'.'",
					   "10000000000000-count-other" : "00 Bio'.'",
					   "100000000000000-count-one" : "000 Bio'.'",
					   "100000000000000-count-other" : "000 Bio'.'"
					 }
				   },
				   "currencyFormats-short" : {
					 "standard" : {
					   "1000-count-one" : "0 Tsd'.' ¤",
					   "1000-count-other" : "0 Tsd'.' ¤",
					   "10000-count-one" : "00 Tsd'.' ¤",
					   "10000-count-other" : "00 Tsd'.' ¤",
					   "100000-count-one" : "000 Tsd'.' ¤",
					   "100000-count-other" : "000 Tsd'.' ¤",
					   "1000000-count-one" : "0 Mio'.' ¤",
					   "1000000-count-other" : "0 Mio'.' ¤",
					   "10000000-count-one" : "00 Mio'.' ¤",
					   "10000000-count-other" : "00 Mio'.' ¤",
					   "100000000-count-one" : "000 Mio'.' ¤",
					   "100000000-count-other" : "000 Mio'.' ¤",
					   "1000000000-count-one" : "0 Mrd'.' ¤",
					   "1000000000-count-other" : "0 Mrd'.' ¤",
					   "10000000000-count-one" : "00 Mrd'.' ¤",
					   "10000000000-count-other" : "00 Mrd'.' ¤",
					   "100000000000-count-one" : "000 Mrd'.' ¤",
					   "100000000000-count-other" : "000 Mrd'.' ¤",
					   "1000000000000-count-one" : "0 Bio'.' ¤",
					   "1000000000000-count-other" : "0 Bio'.' ¤",
					   "10000000000000-count-one" : "00 Bio'.' ¤",
					   "10000000000000-count-other" : "00 Bio'.' ¤",
					   "100000000000000-count-one" : "000 Bio'.' ¤",
					   "100000000000000-count-other" : "000 Bio'.' ¤"
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
		   }}}}}`},
		{"de", "dates,currencies,numbers,plurals",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"de","language":"de","region":"DE","categories":{"currencies":{
				 "ADP" : {
				   "symbol" : "ADP",
				   "displayName" : "Andorranische Pesete",
				   "displayName-count-one" : "Andorranische Pesete",
				   "displayName-count-other" : "Andorranische Peseten"
				 },
				 "AED" : {
				   "symbol" : "AED",
				   "displayName" : "VAE-Dirham",
				   "displayName-count-one" : "VAE-Dirham",
				   "displayName-count-other" : "VAE-Dirham"
				 },
				 "AFA" : {
				   "symbol" : "AFA",
				   "displayName" : "Afghanische Afghani (1927–2002)",
				   "displayName-count-one" : "Afghanische Afghani (1927–2002)",
				   "displayName-count-other" : "Afghanische Afghani (1927–2002)"
				 },
				 "AFN" : {
				   "symbol" : "AFN",
				   "displayName" : "Afghanischer Afghani",
				   "displayName-count-one" : "Afghanischer Afghani",
				   "displayName-count-other" : "Afghanische Afghani"
				 },
				 "ALK" : {
				   "symbol" : "ALK",
				   "displayName" : "Albanischer Lek (1946–1965)",
				   "displayName-count-one" : "Albanischer Lek (1946–1965)",
				   "displayName-count-other" : "Albanische Lek (1946–1965)"
				 },
				 "ALL" : {
				   "symbol" : "ALL",
				   "displayName" : "Albanischer Lek",
				   "displayName-count-one" : "Albanischer Lek",
				   "displayName-count-other" : "Albanische Lek"
				 },
				 "AMD" : {
				   "symbol" : "AMD",
				   "displayName" : "Armenischer Dram",
				   "displayName-count-one" : "Armenischer Dram",
				   "displayName-count-other" : "Armenische Dram"
				 },
				 "ANG" : {
				   "symbol" : "ANG",
				   "displayName" : "Niederländische-Antillen-Gulden",
				   "displayName-count-one" : "Niederländische-Antillen-Gulden",
				   "displayName-count-other" : "Niederländische-Antillen-Gulden"
				 },
				 "AOA" : {
				   "symbol" : "AOA",
				   "displayName" : "Angolanischer Kwanza",
				   "displayName-count-one" : "Angolanischer Kwanza",
				   "symbol-alt-narrow" : "Kz",
				   "displayName-count-other" : "Angolanische Kwanza"
				 },
				 "AOK" : {
				   "symbol" : "AOK",
				   "displayName" : "Angolanischer Kwanza (1977–1990)",
				   "displayName-count-one" : "Angolanischer Kwanza (1977–1990)",
				   "displayName-count-other" : "Angolanische Kwanza (1977–1990)"
				 },
				 "AON" : {
				   "symbol" : "AON",
				   "displayName" : "Angolanischer Neuer Kwanza (1990–2000)",
				   "displayName-count-one" : "Angolanischer Neuer Kwanza (1990–2000)",
				   "displayName-count-other" : "Angolanische Neue Kwanza (1990–2000)"
				 },
				 "AOR" : {
				   "symbol" : "AOR",
				   "displayName" : "Angolanischer Kwanza Reajustado (1995–1999)",
				   "displayName-count-one" : "Angolanischer Kwanza Reajustado (1995–1999)",
				   "displayName-count-other" : "Angolanische Kwanza Reajustado (1995–1999)"
				 },
				 "ARA" : {
				   "symbol" : "ARA",
				   "displayName" : "Argentinischer Austral",
				   "displayName-count-one" : "Argentinischer Austral",
				   "displayName-count-other" : "Argentinische Austral"
				 },
				 "ARL" : {
				   "symbol" : "ARL",
				   "displayName" : "Argentinischer Peso Ley (1970–1983)",
				   "displayName-count-one" : "Argentinischer Peso Ley (1970–1983)",
				   "displayName-count-other" : "Argentinische Pesos Ley (1970–1983)"
				 },
				 "ARM" : {
				   "symbol" : "ARM",
				   "displayName" : "Argentinischer Peso (1881–1970)",
				   "displayName-count-one" : "Argentinischer Peso (1881–1970)",
				   "displayName-count-other" : "Argentinische Pesos (1881–1970)"
				 },
				 "ARP" : {
				   "symbol" : "ARP",
				   "displayName" : "Argentinischer Peso (1983–1985)",
				   "displayName-count-one" : "Argentinischer Peso (1983–1985)",
				   "displayName-count-other" : "Argentinische Peso (1983–1985)"
				 },
				 "ARS" : {
				   "symbol" : "ARS",
				   "displayName" : "Argentinischer Peso",
				   "displayName-count-one" : "Argentinischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Argentinische Pesos"
				 },
				 "ATS" : {
				   "symbol" : "öS",
				   "displayName" : "Österreichischer Schilling",
				   "displayName-count-one" : "Österreichischer Schilling",
				   "displayName-count-other" : "Österreichische Schilling"
				 },
				 "AUD" : {
				   "symbol" : "AU$",
				   "displayName" : "Australischer Dollar",
				   "displayName-count-one" : "Australischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Australische Dollar"
				 },
				 "AWG" : {
				   "symbol" : "AWG",
				   "displayName" : "Aruba-Florin",
				   "displayName-count-one" : "Aruba-Florin",
				   "displayName-count-other" : "Aruba-Florin"
				 },
				 "AZM" : {
				   "symbol" : "AZM",
				   "displayName" : "Aserbaidschan-Manat (1993–2006)",
				   "displayName-count-one" : "Aserbaidschan-Manat (1993–2006)",
				   "displayName-count-other" : "Aserbaidschan-Manat (1993–2006)"
				 },
				 "AZN" : {
				   "symbol" : "AZN",
				   "displayName" : "Aserbaidschan-Manat",
				   "displayName-count-one" : "Aserbaidschan-Manat",
				   "displayName-count-other" : "Aserbaidschan-Manat"
				 },
				 "BAD" : {
				   "symbol" : "BAD",
				   "displayName" : "Bosnien und Herzegowina Dinar (1992–1994)",
				   "displayName-count-one" : "Bosnien und Herzegowina Dinar (1992–1994)",
				   "displayName-count-other" : "Bosnien und Herzegowina Dinar (1992–1994)"
				 },
				 "BAM" : {
				   "symbol" : "BAM",
				   "displayName" : "Bosnien und Herzegowina Konvertierbare Mark",
				   "displayName-count-one" : "Bosnien und Herzegowina Konvertierbare Mark",
				   "symbol-alt-narrow" : "KM",
				   "displayName-count-other" : "Bosnien und Herzegowina Konvertierbare Mark"
				 },
				 "BAN" : {
				   "symbol" : "BAN",
				   "displayName" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)",
				   "displayName-count-one" : "Bosnien und Herzegowina Neuer Dinar (1994–1997)",
				   "displayName-count-other" : "Bosnien und Herzegowina Neue Dinar (1994–1997)"
				 },
				 "BBD" : {
				   "symbol" : "BBD",
				   "displayName" : "Barbados-Dollar",
				   "displayName-count-one" : "Barbados-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Barbados-Dollar"
				 },
				 "BDT" : {
				   "symbol" : "BDT",
				   "displayName" : "Bangladesch-Taka",
				   "displayName-count-one" : "Bangladesch-Taka",
				   "symbol-alt-narrow" : "৳",
				   "displayName-count-other" : "Bangladesch-Taka"
				 },
				 "BEC" : {
				   "symbol" : "BEC",
				   "displayName" : "Belgischer Franc (konvertibel)",
				   "displayName-count-one" : "Belgischer Franc (konvertibel)",
				   "displayName-count-other" : "Belgische Franc (konvertibel)"
				 },
				 "BEF" : {
				   "symbol" : "BEF",
				   "displayName" : "Belgischer Franc",
				   "displayName-count-one" : "Belgischer Franc",
				   "displayName-count-other" : "Belgische Franc"
				 },
				 "BEL" : {
				   "symbol" : "BEL",
				   "displayName" : "Belgischer Finanz-Franc",
				   "displayName-count-one" : "Belgischer Finanz-Franc",
				   "displayName-count-other" : "Belgische Finanz-Franc"
				 },
				 "BGL" : {
				   "symbol" : "BGL",
				   "displayName" : "Bulgarische Lew (1962–1999)",
				   "displayName-count-one" : "Bulgarische Lew (1962–1999)",
				   "displayName-count-other" : "Bulgarische Lew (1962–1999)"
				 },
				 "BGM" : {
				   "symbol" : "BGK",
				   "displayName" : "Bulgarischer Lew (1952–1962)",
				   "displayName-count-one" : "Bulgarischer Lew (1952–1962)",
				   "displayName-count-other" : "Bulgarische Lew (1952–1962)"
				 },
				 "BGN" : {
				   "symbol" : "BGN",
				   "displayName" : "Bulgarischer Lew",
				   "displayName-count-one" : "Bulgarischer Lew",
				   "displayName-count-other" : "Bulgarische Lew"
				 },
				 "BGO" : {
				   "symbol" : "BGJ",
				   "displayName" : "Bulgarischer Lew (1879–1952)",
				   "displayName-count-one" : "Bulgarischer Lew (1879–1952)",
				   "displayName-count-other" : "Bulgarische Lew (1879–1952)"
				 },
				 "BHD" : {
				   "symbol" : "BHD",
				   "displayName" : "Bahrain-Dinar",
				   "displayName-count-one" : "Bahrain-Dinar",
				   "displayName-count-other" : "Bahrain-Dinar"
				 },
				 "BIF" : {
				   "symbol" : "BIF",
				   "displayName" : "Burundi-Franc",
				   "displayName-count-one" : "Burundi-Franc",
				   "displayName-count-other" : "Burundi-Francs"
				 },
				 "BMD" : {
				   "symbol" : "BMD",
				   "displayName" : "Bermuda-Dollar",
				   "displayName-count-one" : "Bermuda-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Bermuda-Dollar"
				 },
				 "BND" : {
				   "symbol" : "BND",
				   "displayName" : "Brunei-Dollar",
				   "displayName-count-one" : "Brunei-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Brunei-Dollar"
				 },
				 "BOB" : {
				   "symbol" : "BOB",
				   "displayName" : "Bolivianischer Boliviano",
				   "displayName-count-one" : "Bolivianischer Boliviano",
				   "symbol-alt-narrow" : "Bs",
				   "displayName-count-other" : "Bolivianische Bolivianos"
				 },
				 "BOL" : {
				   "symbol" : "BOL",
				   "displayName" : "Bolivianischer Boliviano (1863–1963)",
				   "displayName-count-one" : "Bolivianischer Boliviano (1863–1963)",
				   "displayName-count-other" : "Bolivianische Bolivianos (1863–1963)"
				 },
				 "BOP" : {
				   "symbol" : "BOP",
				   "displayName" : "Bolivianischer Peso",
				   "displayName-count-one" : "Bolivianischer Peso",
				   "displayName-count-other" : "Bolivianische Peso"
				 },
				 "BOV" : {
				   "symbol" : "BOV",
				   "displayName" : "Boliviansiche Mvdol",
				   "displayName-count-one" : "Boliviansiche Mvdol",
				   "displayName-count-other" : "Bolivianische Mvdol"
				 },
				 "BRB" : {
				   "symbol" : "BRB",
				   "displayName" : "Brasilianischer Cruzeiro Novo (1967–1986)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro Novo (1967–1986)",
				   "displayName-count-other" : "Brasilianische Cruzeiro Novo (1967–1986)"
				 },
				 "BRC" : {
				   "symbol" : "BRC",
				   "displayName" : "Brasilianischer Cruzado (1986–1989)",
				   "displayName-count-one" : "Brasilianischer Cruzado (1986–1989)",
				   "displayName-count-other" : "Brasilianische Cruzado (1986–1989)"
				 },
				 "BRE" : {
				   "symbol" : "BRE",
				   "displayName" : "Brasilianischer Cruzeiro (1990–1993)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1990–1993)",
				   "displayName-count-other" : "Brasilianische Cruzeiro (1990–1993)"
				 },
				 "BRL" : {
				   "symbol" : "R$",
				   "displayName" : "Brasilianischer Real",
				   "displayName-count-one" : "Brasilianischer Real",
				   "symbol-alt-narrow" : "R$",
				   "displayName-count-other" : "Brasilianische Real"
				 },
				 "BRN" : {
				   "symbol" : "BRN",
				   "displayName" : "Brasilianischer Cruzado Novo (1989–1990)",
				   "displayName-count-one" : "Brasilianischer Cruzado Novo (1989–1990)",
				   "displayName-count-other" : "Brasilianische Cruzado Novo (1989–1990)"
				 },
				 "BRR" : {
				   "symbol" : "BRR",
				   "displayName" : "Brasilianischer Cruzeiro (1993–1994)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1993–1994)",
				   "displayName-count-other" : "Brasilianische Cruzeiro (1993–1994)"
				 },
				 "BRZ" : {
				   "symbol" : "BRZ",
				   "displayName" : "Brasilianischer Cruzeiro (1942–1967)",
				   "displayName-count-one" : "Brasilianischer Cruzeiro (1942–1967)",
				   "displayName-count-other" : "Brasilianischer Cruzeiro (1942–1967)"
				 },
				 "BSD" : {
				   "symbol" : "BSD",
				   "displayName" : "Bahamas-Dollar",
				   "displayName-count-one" : "Bahamas-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Bahamas-Dollar"
				 },
				 "BTN" : {
				   "symbol" : "BTN",
				   "displayName" : "Bhutan-Ngultrum",
				   "displayName-count-one" : "Bhutan-Ngultrum",
				   "displayName-count-other" : "Bhutan-Ngultrum"
				 },
				 "BUK" : {
				   "symbol" : "BUK",
				   "displayName" : "Birmanischer Kyat",
				   "displayName-count-one" : "Birmanischer Kyat",
				   "displayName-count-other" : "Birmanische Kyat"
				 },
				 "BWP" : {
				   "symbol" : "BWP",
				   "displayName" : "Botswanischer Pula",
				   "displayName-count-one" : "Botswanischer Pula",
				   "symbol-alt-narrow" : "P",
				   "displayName-count-other" : "Botswanische Pula"
				 },
				 "BYB" : {
				   "symbol" : "BYB",
				   "displayName" : "Belarus-Rubel (1994–1999)",
				   "displayName-count-one" : "Belarus-Rubel (1994–1999)",
				   "displayName-count-other" : "Belarus-Rubel (1994–1999)"
				 },
				 "BYN" : {
				   "symbol" : "BYN",
				   "displayName" : "Weißrussischer Rubel",
				   "displayName-count-one" : "Weißrussischer Rubel",
				   "symbol-alt-narrow" : "р.",
				   "displayName-count-other" : "Weißrussische Rubel"
				 },
				 "BYR" : {
				   "symbol" : "BYR",
				   "displayName" : "Weißrussischer Rubel (2000–2016)",
				   "displayName-count-one" : "Weißrussischer Rubel (2000–2016)",
				   "displayName-count-other" : "Weißrussische Rubel (2000–2016)"
				 },
				 "BZD" : {
				   "symbol" : "BZD",
				   "displayName" : "Belize-Dollar",
				   "displayName-count-one" : "Belize-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Belize-Dollar"
				 },
				 "CAD" : {
				   "symbol" : "CA$",
				   "displayName" : "Kanadischer Dollar",
				   "displayName-count-one" : "Kanadischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kanadische Dollar"
				 },
				 "CDF" : {
				   "symbol" : "CDF",
				   "displayName" : "Kongo-Franc",
				   "displayName-count-one" : "Kongo-Franc",
				   "displayName-count-other" : "Kongo-Francs"
				 },
				 "CHE" : {
				   "symbol" : "CHE",
				   "displayName" : "WIR-Euro",
				   "displayName-count-one" : "WIR-Euro",
				   "displayName-count-other" : "WIR-Euro"
				 },
				 "CHF" : {
				   "symbol" : "CHF",
				   "displayName" : "Schweizer Franken",
				   "displayName-count-one" : "Schweizer Franken",
				   "displayName-count-other" : "Schweizer Franken"
				 },
				 "CHW" : {
				   "symbol" : "CHW",
				   "displayName" : "WIR Franken",
				   "displayName-count-one" : "WIR Franken",
				   "displayName-count-other" : "WIR Franken"
				 },
				 "CLE" : {
				   "symbol" : "CLE",
				   "displayName" : "Chilenischer Escudo",
				   "displayName-count-one" : "Chilenischer Escudo",
				   "displayName-count-other" : "Chilenische Escudo"
				 },
				 "CLF" : {
				   "symbol" : "CLF",
				   "displayName" : "Chilenische Unidades de Fomento",
				   "displayName-count-one" : "Chilenische Unidades de Fomento",
				   "displayName-count-other" : "Chilenische Unidades de Fomento"
				 },
				 "CLP" : {
				   "symbol" : "CLP",
				   "displayName" : "Chilenischer Peso",
				   "displayName-count-one" : "Chilenischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Chilenische Pesos"
				 },
				 "CNH" : {
				   "symbol" : "CNH",
				   "displayName" : "Renminbi Yuan (Off–Shore)",
				   "displayName-count-one" : "Renminbi Yuan (Off–Shore)",
				   "displayName-count-other" : "Renminbi Yuan (Off–Shore)"
				 },
				 "CNX" : {
				   "symbol" : "CNX",
				   "displayName" : "Dollar der Chinesischen Volksbank",
				   "displayName-count-one" : "Dollar der Chinesischen Volksbank",
				   "displayName-count-other" : "Dollar der Chinesischen Volksbank"
				 },
				 "CNY" : {
				   "symbol" : "CN¥",
				   "displayName" : "Renminbi Yuan",
				   "displayName-count-one" : "Chinesischer Yuan",
				   "symbol-alt-narrow" : "¥",
				   "displayName-count-other" : "Renminbi Yuan"
				 },
				 "COP" : {
				   "symbol" : "COP",
				   "displayName" : "Kolumbianischer Peso",
				   "displayName-count-one" : "Kolumbianischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kolumbianische Pesos"
				 },
				 "COU" : {
				   "symbol" : "COU",
				   "displayName" : "Kolumbianische Unidades de valor real",
				   "displayName-count-one" : "Kolumbianische Unidad de valor real",
				   "displayName-count-other" : "Kolumbianische Unidades de valor real"
				 },
				 "CRC" : {
				   "symbol" : "CRC",
				   "displayName" : "Costa-Rica-Colón",
				   "displayName-count-one" : "Costa-Rica-Colón",
				   "symbol-alt-narrow" : "₡",
				   "displayName-count-other" : "Costa-Rica-Colón"
				 },
				 "CSD" : {
				   "symbol" : "CSD",
				   "displayName" : "Serbischer Dinar (2002–2006)",
				   "displayName-count-one" : "Serbischer Dinar (2002–2006)",
				   "displayName-count-other" : "Serbische Dinar (2002–2006)"
				 },
				 "CSK" : {
				   "symbol" : "CSK",
				   "displayName" : "Tschechoslowakische Krone",
				   "displayName-count-one" : "Tschechoslowakische Kronen",
				   "displayName-count-other" : "Tschechoslowakische Kronen"
				 },
				 "CUC" : {
				   "symbol" : "CUC",
				   "displayName" : "Kubanischer Peso (konvertibel)",
				   "displayName-count-one" : "Kubanischer Peso (konvertibel)",
				   "symbol-alt-narrow" : "Cub$",
				   "displayName-count-other" : "Kubanische Pesos (konvertibel)"
				 },
				 "CUP" : {
				   "symbol" : "CUP",
				   "displayName" : "Kubanischer Peso",
				   "displayName-count-one" : "Kubanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kubanische Pesos"
				 },
				 "CVE" : {
				   "symbol" : "CVE",
				   "displayName" : "Cabo-Verde-Escudo",
				   "displayName-count-one" : "Cabo-Verde-Escudo",
				   "displayName-count-other" : "Cabo-Verde-Escudos"
				 },
				 "CYP" : {
				   "symbol" : "CYP",
				   "displayName" : "Zypern-Pfund",
				   "displayName-count-one" : "Zypern Pfund",
				   "displayName-count-other" : "Zypern Pfund"
				 },
				 "CZK" : {
				   "symbol" : "CZK",
				   "displayName" : "Tschechische Krone",
				   "displayName-count-one" : "Tschechische Krone",
				   "symbol-alt-narrow" : "Kč",
				   "displayName-count-other" : "Tschechische Kronen"
				 },
				 "DDM" : {
				   "symbol" : "DDM",
				   "displayName" : "Mark der DDR",
				   "displayName-count-one" : "Mark der DDR",
				   "displayName-count-other" : "Mark der DDR"
				 },
				 "DEM" : {
				   "symbol" : "DM",
				   "displayName" : "Deutsche Mark",
				   "displayName-count-one" : "Deutsche Mark",
				   "displayName-count-other" : "Deutsche Mark"
				 },
				 "DJF" : {
				   "symbol" : "DJF",
				   "displayName" : "Dschibuti-Franc",
				   "displayName-count-one" : "Dschibuti-Franc",
				   "displayName-count-other" : "Dschibuti-Franc"
				 },
				 "DKK" : {
				   "symbol" : "DKK",
				   "displayName" : "Dänische Krone",
				   "displayName-count-one" : "Dänische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Dänische Kronen"
				 },
				 "DOP" : {
				   "symbol" : "DOP",
				   "displayName" : "Dominikanischer Peso",
				   "displayName-count-one" : "Dominikanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Dominikanische Pesos"
				 },
				 "DZD" : {
				   "symbol" : "DZD",
				   "displayName" : "Algerischer Dinar",
				   "displayName-count-one" : "Algerischer Dinar",
				   "displayName-count-other" : "Algerische Dinar"
				 },
				 "ECS" : {
				   "symbol" : "ECS",
				   "displayName" : "Ecuadorianischer Sucre",
				   "displayName-count-one" : "Ecuadorianischer Sucre",
				   "displayName-count-other" : "Ecuadorianische Sucre"
				 },
				 "ECV" : {
				   "symbol" : "ECV",
				   "displayName" : "Verrechnungseinheit für Ecuador",
				   "displayName-count-one" : "Verrechnungseinheiten für Ecuador",
				   "displayName-count-other" : "Verrechnungseinheiten für Ecuador"
				 },
				 "EEK" : {
				   "symbol" : "EEK",
				   "displayName" : "Estnische Krone",
				   "displayName-count-one" : "Estnische Krone",
				   "displayName-count-other" : "Estnische Kronen"
				 },
				 "EGP" : {
				   "symbol" : "EGP",
				   "displayName" : "Ägyptisches Pfund",
				   "displayName-count-one" : "Ägyptisches Pfund",
				   "symbol-alt-narrow" : "E£",
				   "displayName-count-other" : "Ägyptische Pfund"
				 },
				 "ERN" : {
				   "symbol" : "ERN",
				   "displayName" : "Eritreischer Nakfa",
				   "displayName-count-one" : "Eritreischer Nakfa",
				   "displayName-count-other" : "Eritreische Nakfa"
				 },
				 "ESA" : {
				   "symbol" : "ESA",
				   "displayName" : "Spanische Peseta (A–Konten)",
				   "displayName-count-one" : "Spanische Peseta (A–Konten)",
				   "displayName-count-other" : "Spanische Peseten (A–Konten)"
				 },
				 "ESB" : {
				   "symbol" : "ESB",
				   "displayName" : "Spanische Peseta (konvertibel)",
				   "displayName-count-one" : "Spanische Peseta (konvertibel)",
				   "displayName-count-other" : "Spanische Peseten (konvertibel)"
				 },
				 "ESP" : {
				   "symbol" : "ESP",
				   "displayName" : "Spanische Peseta",
				   "displayName-count-one" : "Spanische Peseta",
				   "symbol-alt-narrow" : "₧",
				   "displayName-count-other" : "Spanische Peseten"
				 },
				 "ETB" : {
				   "symbol" : "ETB",
				   "displayName" : "Äthiopischer Birr",
				   "displayName-count-one" : "Äthiopischer Birr",
				   "displayName-count-other" : "Äthiopische Birr"
				 },
				 "EUR" : {
				   "symbol" : "€",
				   "displayName" : "Euro",
				   "displayName-count-one" : "Euro",
				   "symbol-alt-narrow" : "€",
				   "displayName-count-other" : "Euro"
				 },
				 "FIM" : {
				   "symbol" : "FIM",
				   "displayName" : "Finnische Mark",
				   "displayName-count-one" : "Finnische Mark",
				   "displayName-count-other" : "Finnische Mark"
				 },
				 "FJD" : {
				   "symbol" : "FJD",
				   "displayName" : "Fidschi-Dollar",
				   "displayName-count-one" : "Fidschi-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Fidschi-Dollar"
				 },
				 "FKP" : {
				   "symbol" : "FKP",
				   "displayName" : "Falkland-Pfund",
				   "displayName-count-one" : "Falkland-Pfund",
				   "symbol-alt-narrow" : "Fl£",
				   "displayName-count-other" : "Falkland-Pfund"
				 },
				 "FRF" : {
				   "symbol" : "FRF",
				   "displayName" : "Französischer Franc",
				   "displayName-count-one" : "Französischer Franc",
				   "displayName-count-other" : "Französische Franc"
				 },
				 "GBP" : {
				   "symbol" : "£",
				   "displayName" : "Britisches Pfund",
				   "displayName-count-one" : "Britisches Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Britische Pfund"
				 },
				 "GEK" : {
				   "symbol" : "GEK",
				   "displayName" : "Georgischer Kupon Larit",
				   "displayName-count-one" : "Georgischer Kupon Larit",
				   "displayName-count-other" : "Georgische Kupon Larit"
				 },
				 "GEL" : {
				   "symbol" : "GEL",
				   "symbol-alt-variant" : "₾",
				   "displayName" : "Georgischer Lari",
				   "displayName-count-one" : "Georgischer Lari",
				   "symbol-alt-narrow" : "₾",
				   "displayName-count-other" : "Georgische Lari"
				 },
				 "GHC" : {
				   "symbol" : "GHC",
				   "displayName" : "Ghanaischer Cedi (1979–2007)",
				   "displayName-count-one" : "Ghanaischer Cedi (1979–2007)",
				   "displayName-count-other" : "Ghanaische Cedi (1979–2007)"
				 },
				 "GHS" : {
				   "symbol" : "GHS",
				   "displayName" : "Ghanaischer Cedi",
				   "displayName-count-one" : "Ghanaischer Cedi",
				   "displayName-count-other" : "Ghanaische Cedi"
				 },
				 "GIP" : {
				   "symbol" : "GIP",
				   "displayName" : "Gibraltar-Pfund",
				   "displayName-count-one" : "Gibraltar-Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Gibraltar-Pfund"
				 },
				 "GMD" : {
				   "symbol" : "GMD",
				   "displayName" : "Gambia-Dalasi",
				   "displayName-count-one" : "Gambia-Dalasi",
				   "displayName-count-other" : "Gambia-Dalasi"
				 },
				 "GNF" : {
				   "symbol" : "GNF",
				   "displayName" : "Guinea-Franc",
				   "displayName-count-one" : "Guinea-Franc",
				   "symbol-alt-narrow" : "F.G.",
				   "displayName-count-other" : "Guinea-Franc"
				 },
				 "GNS" : {
				   "symbol" : "GNS",
				   "displayName" : "Guineischer Syli",
				   "displayName-count-one" : "Guineischer Syli",
				   "displayName-count-other" : "Guineische Syli"
				 },
				 "GQE" : {
				   "symbol" : "GQE",
				   "displayName" : "Äquatorialguinea-Ekwele",
				   "displayName-count-one" : "Äquatorialguinea-Ekwele",
				   "displayName-count-other" : "Äquatorialguinea-Ekwele"
				 },
				 "GRD" : {
				   "symbol" : "GRD",
				   "displayName" : "Griechische Drachme",
				   "displayName-count-one" : "Griechische Drachme",
				   "displayName-count-other" : "Griechische Drachmen"
				 },
				 "GTQ" : {
				   "symbol" : "GTQ",
				   "displayName" : "Guatemaltekischer Quetzal",
				   "displayName-count-one" : "Guatemaltekischer Quetzal",
				   "symbol-alt-narrow" : "Q",
				   "displayName-count-other" : "Guatemaltekische Quetzales"
				 },
				 "GWE" : {
				   "symbol" : "GWE",
				   "displayName" : "Portugiesisch Guinea Escudo",
				   "displayName-count-one" : "Portugiesisch Guinea Escudo",
				   "displayName-count-other" : "Portugiesisch Guinea Escudo"
				 },
				 "GWP" : {
				   "symbol" : "GWP",
				   "displayName" : "Guinea-Bissau Peso",
				   "displayName-count-one" : "Guinea-Bissau Peso",
				   "displayName-count-other" : "Guinea-Bissau Pesos"
				 },
				 "GYD" : {
				   "symbol" : "GYD",
				   "displayName" : "Guyana-Dollar",
				   "displayName-count-one" : "Guyana-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Guyana-Dollar"
				 },
				 "HKD" : {
				   "symbol" : "HK$",
				   "displayName" : "Hongkong-Dollar",
				   "displayName-count-one" : "Hongkong-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Hongkong-Dollar"
				 },
				 "HNL" : {
				   "symbol" : "HNL",
				   "displayName" : "Honduras-Lempira",
				   "displayName-count-one" : "Honduras-Lempira",
				   "symbol-alt-narrow" : "L",
				   "displayName-count-other" : "Honduras-Lempira"
				 },
				 "HRD" : {
				   "symbol" : "HRD",
				   "displayName" : "Kroatischer Dinar",
				   "displayName-count-one" : "Kroatischer Dinar",
				   "displayName-count-other" : "Kroatische Dinar"
				 },
				 "HRK" : {
				   "symbol" : "HRK",
				   "displayName" : "Kroatischer Kuna",
				   "displayName-count-one" : "Kroatischer Kuna",
				   "symbol-alt-narrow" : "kn",
				   "displayName-count-other" : "Kroatische Kuna"
				 },
				 "HTG" : {
				   "symbol" : "HTG",
				   "displayName" : "Haitianische Gourde",
				   "displayName-count-one" : "Haitianische Gourde",
				   "displayName-count-other" : "Haitianische Gourdes"
				 },
				 "HUF" : {
				   "symbol" : "HUF",
				   "displayName" : "Ungarischer Forint",
				   "displayName-count-one" : "Ungarischer Forint",
				   "symbol-alt-narrow" : "Ft",
				   "displayName-count-other" : "Ungarische Forint"
				 },
				 "IDR" : {
				   "symbol" : "IDR",
				   "displayName" : "Indonesische Rupiah",
				   "displayName-count-one" : "Indonesische Rupiah",
				   "symbol-alt-narrow" : "Rp",
				   "displayName-count-other" : "Indonesische Rupiah"
				 },
				 "IEP" : {
				   "symbol" : "IEP",
				   "displayName" : "Irisches Pfund",
				   "displayName-count-one" : "Irisches Pfund",
				   "displayName-count-other" : "Irische Pfund"
				 },
				 "ILP" : {
				   "symbol" : "ILP",
				   "displayName" : "Israelisches Pfund",
				   "displayName-count-one" : "Israelisches Pfund",
				   "displayName-count-other" : "Israelische Pfund"
				 },
				 "ILR" : {
				   "symbol" : "ILR",
				   "displayName" : "Israelischer Schekel (1980–1985)",
				   "displayName-count-one" : "Israelischer Schekel (1980–1985)",
				   "displayName-count-other" : "Israelische Schekel (1980–1985)"
				 },
				 "ILS" : {
				   "symbol" : "₪",
				   "displayName" : "Israelischer Neuer Schekel",
				   "displayName-count-one" : "Israelischer Neuer Schekel",
				   "symbol-alt-narrow" : "₪",
				   "displayName-count-other" : "Israelische Neue Schekel"
				 },
				 "INR" : {
				   "symbol" : "₹",
				   "displayName" : "Indische Rupie",
				   "displayName-count-one" : "Indische Rupie",
				   "symbol-alt-narrow" : "₹",
				   "displayName-count-other" : "Indische Rupien"
				 },
				 "IQD" : {
				   "symbol" : "IQD",
				   "displayName" : "Irakischer Dinar",
				   "displayName-count-one" : "Irakischer Dinar",
				   "displayName-count-other" : "Irakische Dinar"
				 },
				 "IRR" : {
				   "symbol" : "IRR",
				   "displayName" : "Iranischer Rial",
				   "displayName-count-one" : "Iranischer Rial",
				   "displayName-count-other" : "Iranische Rial"
				 },
				 "ISJ" : {
				   "symbol" : "ISJ",
				   "displayName" : "Isländische Krone (1918–1981)",
				   "displayName-count-one" : "Isländische Krone (1918–1981)",
				   "displayName-count-other" : "Isländische Kronen (1918–1981)"
				 },
				 "ISK" : {
				   "symbol" : "ISK",
				   "displayName" : "Isländische Krone",
				   "displayName-count-one" : "Isländische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Isländische Kronen"
				 },
				 "ITL" : {
				   "symbol" : "ITL",
				   "displayName" : "Italienische Lira",
				   "displayName-count-one" : "Italienische Lira",
				   "displayName-count-other" : "Italienische Lire"
				 },
				 "JMD" : {
				   "symbol" : "JMD",
				   "displayName" : "Jamaika-Dollar",
				   "displayName-count-one" : "Jamaika-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Jamaika-Dollar"
				 },
				 "JOD" : {
				   "symbol" : "JOD",
				   "displayName" : "Jordanischer Dinar",
				   "displayName-count-one" : "Jordanischer Dinar",
				   "displayName-count-other" : "Jordanische Dinar"
				 },
				 "JPY" : {
				   "symbol" : "¥",
				   "displayName" : "Japanischer Yen",
				   "displayName-count-one" : "Japanischer Yen",
				   "symbol-alt-narrow" : "¥",
				   "displayName-count-other" : "Japanische Yen"
				 },
				 "KES" : {
				   "symbol" : "KES",
				   "displayName" : "Kenia-Schilling",
				   "displayName-count-one" : "Kenia-Schilling",
				   "displayName-count-other" : "Kenia-Schilling"
				 },
				 "KGS" : {
				   "symbol" : "KGS",
				   "displayName" : "Kirgisischer Som",
				   "displayName-count-one" : "Kirgisischer Som",
				   "displayName-count-other" : "Kirgisische Som"
				 },
				 "KHR" : {
				   "symbol" : "KHR",
				   "displayName" : "Kambodschanischer Riel",
				   "displayName-count-one" : "Kambodschanischer Riel",
				   "symbol-alt-narrow" : "៛",
				   "displayName-count-other" : "Kambodschanische Riel"
				 },
				 "KMF" : {
				   "symbol" : "KMF",
				   "displayName" : "Komoren-Franc",
				   "displayName-count-one" : "Komoren-Franc",
				   "symbol-alt-narrow" : "FC",
				   "displayName-count-other" : "Komoren-Francs"
				 },
				 "KPW" : {
				   "symbol" : "KPW",
				   "displayName" : "Nordkoreanischer Won",
				   "displayName-count-one" : "Nordkoreanischer Won",
				   "symbol-alt-narrow" : "₩",
				   "displayName-count-other" : "Nordkoreanische Won"
				 },
				 "KRH" : {
				   "symbol" : "KRH",
				   "displayName" : "Südkoreanischer Hwan (1953–1962)",
				   "displayName-count-one" : "Südkoreanischer Hwan (1953–1962)",
				   "displayName-count-other" : "Südkoreanischer Hwan (1953–1962)"
				 },
				 "KRO" : {
				   "symbol" : "KRO",
				   "displayName" : "Südkoreanischer Won (1945–1953)",
				   "displayName-count-one" : "Südkoreanischer Won (1945–1953)",
				   "displayName-count-other" : "Südkoreanischer Won (1945–1953)"
				 },
				 "KRW" : {
				   "symbol" : "₩",
				   "displayName" : "Südkoreanischer Won",
				   "displayName-count-one" : "Südkoreanischer Won",
				   "symbol-alt-narrow" : "₩",
				   "displayName-count-other" : "Südkoreanische Won"
				 },
				 "KWD" : {
				   "symbol" : "KWD",
				   "displayName" : "Kuwait-Dinar",
				   "displayName-count-one" : "Kuwait-Dinar",
				   "displayName-count-other" : "Kuwait-Dinar"
				 },
				 "KYD" : {
				   "symbol" : "KYD",
				   "displayName" : "Kaiman-Dollar",
				   "displayName-count-one" : "Kaiman-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Kaiman-Dollar"
				 },
				 "KZT" : {
				   "symbol" : "KZT",
				   "displayName" : "Kasachischer Tenge",
				   "displayName-count-one" : "Kasachischer Tenge",
				   "symbol-alt-narrow" : "₸",
				   "displayName-count-other" : "Kasachische Tenge"
				 },
				 "LAK" : {
				   "symbol" : "LAK",
				   "displayName" : "Laotischer Kip",
				   "displayName-count-one" : "Laotischer Kip",
				   "symbol-alt-narrow" : "₭",
				   "displayName-count-other" : "Laotische Kip"
				 },
				 "LBP" : {
				   "symbol" : "LBP",
				   "displayName" : "Libanesisches Pfund",
				   "displayName-count-one" : "Libanesisches Pfund",
				   "symbol-alt-narrow" : "L£",
				   "displayName-count-other" : "Libanesische Pfund"
				 },
				 "LKR" : {
				   "symbol" : "LKR",
				   "displayName" : "Sri-Lanka-Rupie",
				   "displayName-count-one" : "Sri-Lanka-Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Sri-Lanka-Rupien"
				 },
				 "LRD" : {
				   "symbol" : "LRD",
				   "displayName" : "Liberianischer Dollar",
				   "displayName-count-one" : "Liberianischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Liberianische Dollar"
				 },
				 "LSL" : {
				   "symbol" : "LSL",
				   "displayName" : "Loti",
				   "displayName-count-one" : "Loti",
				   "displayName-count-other" : "Loti"
				 },
				 "LTL" : {
				   "symbol" : "LTL",
				   "displayName" : "Litauischer Litas",
				   "displayName-count-one" : "Litauischer Litas",
				   "symbol-alt-narrow" : "Lt",
				   "displayName-count-other" : "Litauische Litas"
				 },
				 "LTT" : {
				   "symbol" : "LTT",
				   "displayName" : "Litauischer Talonas",
				   "displayName-count-one" : "Litauische Talonas",
				   "displayName-count-other" : "Litauische Talonas"
				 },
				 "LUC" : {
				   "symbol" : "LUC",
				   "displayName" : "Luxemburgischer Franc (konvertibel)",
				   "displayName-count-one" : "Luxemburgische Franc (konvertibel)",
				   "displayName-count-other" : "Luxemburgische Franc (konvertibel)"
				 },
				 "LUF" : {
				   "symbol" : "LUF",
				   "displayName" : "Luxemburgischer Franc",
				   "displayName-count-one" : "Luxemburgische Franc",
				   "displayName-count-other" : "Luxemburgische Franc"
				 },
				 "LUL" : {
				   "symbol" : "LUL",
				   "displayName" : "Luxemburgischer Finanz-Franc",
				   "displayName-count-one" : "Luxemburgische Finanz-Franc",
				   "displayName-count-other" : "Luxemburgische Finanz-Franc"
				 },
				 "LVL" : {
				   "symbol" : "LVL",
				   "displayName" : "Lettischer Lats",
				   "displayName-count-one" : "Lettischer Lats",
				   "symbol-alt-narrow" : "Ls",
				   "displayName-count-other" : "Lettische Lats"
				 },
				 "LVR" : {
				   "symbol" : "LVR",
				   "displayName" : "Lettischer Rubel",
				   "displayName-count-one" : "Lettische Rubel",
				   "displayName-count-other" : "Lettische Rubel"
				 },
				 "LYD" : {
				   "symbol" : "LYD",
				   "displayName" : "Libyscher Dinar",
				   "displayName-count-one" : "Libyscher Dinar",
				   "displayName-count-other" : "Libysche Dinar"
				 },
				 "MAD" : {
				   "symbol" : "MAD",
				   "displayName" : "Marokkanischer Dirham",
				   "displayName-count-one" : "Marokkanischer Dirham",
				   "displayName-count-other" : "Marokkanische Dirham"
				 },
				 "MAF" : {
				   "symbol" : "MAF",
				   "displayName" : "Marokkanischer Franc",
				   "displayName-count-one" : "Marokkanische Franc",
				   "displayName-count-other" : "Marokkanische Franc"
				 },
				 "MCF" : {
				   "symbol" : "MCF",
				   "displayName" : "Monegassischer Franc",
				   "displayName-count-one" : "Monegassischer Franc",
				   "displayName-count-other" : "Monegassische Franc"
				 },
				 "MDC" : {
				   "symbol" : "MDC",
				   "displayName" : "Moldau-Cupon",
				   "displayName-count-one" : "Moldau-Cupon",
				   "displayName-count-other" : "Moldau-Cupon"
				 },
				 "MDL" : {
				   "symbol" : "MDL",
				   "displayName" : "Moldau-Leu",
				   "displayName-count-one" : "Moldau-Leu",
				   "displayName-count-other" : "Moldau-Leu"
				 },
				 "MGA" : {
				   "symbol" : "MGA",
				   "displayName" : "Madagaskar-Ariary",
				   "displayName-count-one" : "Madagaskar-Ariary",
				   "symbol-alt-narrow" : "Ar",
				   "displayName-count-other" : "Madagaskar-Ariary"
				 },
				 "MGF" : {
				   "symbol" : "MGF",
				   "displayName" : "Madagaskar-Franc",
				   "displayName-count-one" : "Madagaskar-Franc",
				   "displayName-count-other" : "Madagaskar-Franc"
				 },
				 "MKD" : {
				   "symbol" : "MKD",
				   "displayName" : "Mazedonischer Denar",
				   "displayName-count-one" : "Mazedonischer Denar",
				   "displayName-count-other" : "Mazedonische Denari"
				 },
				 "MKN" : {
				   "symbol" : "MKN",
				   "displayName" : "Mazedonischer Denar (1992–1993)",
				   "displayName-count-one" : "Mazedonischer Denar (1992–1993)",
				   "displayName-count-other" : "Mazedonische Denar (1992–1993)"
				 },
				 "MLF" : {
				   "symbol" : "MLF",
				   "displayName" : "Malischer Franc",
				   "displayName-count-one" : "Malische Franc",
				   "displayName-count-other" : "Malische Franc"
				 },
				 "MMK" : {
				   "symbol" : "MMK",
				   "displayName" : "Myanmarischer Kyat",
				   "displayName-count-one" : "Myanmarischer Kyat",
				   "symbol-alt-narrow" : "K",
				   "displayName-count-other" : "Myanmarische Kyat"
				 },
				 "MNT" : {
				   "symbol" : "MNT",
				   "displayName" : "Mongolischer Tögrög",
				   "displayName-count-one" : "Mongolischer Tögrög",
				   "symbol-alt-narrow" : "₮",
				   "displayName-count-other" : "Mongolische Tögrög"
				 },
				 "MOP" : {
				   "symbol" : "MOP",
				   "displayName" : "Macao-Pataca",
				   "displayName-count-one" : "Macao-Pataca",
				   "displayName-count-other" : "Macao-Pataca"
				 },
				 "MRO" : {
				   "symbol" : "MRO",
				   "displayName" : "Mauretanischer Ouguiya",
				   "displayName-count-one" : "Mauretanischer Ouguiya",
				   "displayName-count-other" : "Mauretanische Ouguiya"
				 },
				 "MTL" : {
				   "symbol" : "MTL",
				   "displayName" : "Maltesische Lira",
				   "displayName-count-one" : "Maltesische Lira",
				   "displayName-count-other" : "Maltesische Lira"
				 },
				 "MTP" : {
				   "symbol" : "MTP",
				   "displayName" : "Maltesisches Pfund",
				   "displayName-count-one" : "Maltesische Pfund",
				   "displayName-count-other" : "Maltesische Pfund"
				 },
				 "MUR" : {
				   "symbol" : "MUR",
				   "displayName" : "Mauritius-Rupie",
				   "displayName-count-one" : "Mauritius-Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Mauritius-Rupien"
				 },
				 "MVP" : {
				   "symbol" : "MVP",
				   "displayName" : "Malediven-Rupie (alt)",
				   "displayName-count-one" : "Malediven-Rupie (alt)",
				   "displayName-count-other" : "Malediven-Rupien (alt)"
				 },
				 "MVR" : {
				   "symbol" : "MVR",
				   "displayName" : "Malediven-Rufiyaa",
				   "displayName-count-one" : "Malediven-Rufiyaa",
				   "displayName-count-other" : "Malediven-Rupien"
				 },
				 "MWK" : {
				   "symbol" : "MWK",
				   "displayName" : "Malawi-Kwacha",
				   "displayName-count-one" : "Malawi-Kwacha",
				   "displayName-count-other" : "Malawi-Kwacha"
				 },
				 "MXN" : {
				   "symbol" : "MX$",
				   "displayName" : "Mexikanischer Peso",
				   "displayName-count-one" : "Mexikanischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Mexikanische Pesos"
				 },
				 "MXP" : {
				   "symbol" : "MXP",
				   "displayName" : "Mexikanischer Silber-Peso (1861–1992)",
				   "displayName-count-one" : "Mexikanische Silber-Peso (1861–1992)",
				   "displayName-count-other" : "Mexikanische Silber-Pesos (1861–1992)"
				 },
				 "MXV" : {
				   "symbol" : "MXV",
				   "displayName" : "Mexicanischer Unidad de Inversion (UDI)",
				   "displayName-count-one" : "Mexicanischer Unidad de Inversion (UDI)",
				   "displayName-count-other" : "Mexikanische Unidad de Inversion (UDI)"
				 },
				 "MYR" : {
				   "symbol" : "MYR",
				   "displayName" : "Malaysischer Ringgit",
				   "displayName-count-one" : "Malaysischer Ringgit",
				   "symbol-alt-narrow" : "RM",
				   "displayName-count-other" : "Malaysische Ringgit"
				 },
				 "MZE" : {
				   "symbol" : "MZE",
				   "displayName" : "Mosambikanischer Escudo",
				   "displayName-count-one" : "Mozambikanische Escudo",
				   "displayName-count-other" : "Mozambikanische Escudo"
				 },
				 "MZM" : {
				   "symbol" : "MZM",
				   "displayName" : "Mosambikanischer Metical (1980–2006)",
				   "displayName-count-one" : "Mosambikanischer Metical (1980–2006)",
				   "displayName-count-other" : "Mosambikanische Meticais (1980–2006)"
				 },
				 "MZN" : {
				   "symbol" : "MZN",
				   "displayName" : "Mosambikanischer Metical",
				   "displayName-count-one" : "Mosambikanischer Metical",
				   "displayName-count-other" : "Mosambikanische Meticais"
				 },
				 "NAD" : {
				   "symbol" : "NAD",
				   "displayName" : "Namibia-Dollar",
				   "displayName-count-one" : "Namibia-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Namibia-Dollar"
				 },
				 "NGN" : {
				   "symbol" : "NGN",
				   "displayName" : "Nigerianischer Naira",
				   "displayName-count-one" : "Nigerianischer Naira",
				   "symbol-alt-narrow" : "₦",
				   "displayName-count-other" : "Nigerianische Naira"
				 },
				 "NIC" : {
				   "symbol" : "NIC",
				   "displayName" : "Nicaraguanischer Córdoba (1988–1991)",
				   "displayName-count-one" : "Nicaraguanischer Córdoba (1988–1991)",
				   "displayName-count-other" : "Nicaraguanische Córdoba (1988–1991)"
				 },
				 "NIO" : {
				   "symbol" : "NIO",
				   "displayName" : "Nicaragua-Córdoba",
				   "displayName-count-one" : "Nicaragua-Córdoba",
				   "symbol-alt-narrow" : "C$",
				   "displayName-count-other" : "Nicaragua-Córdobas"
				 },
				 "NLG" : {
				   "symbol" : "NLG",
				   "displayName" : "Niederländischer Gulden",
				   "displayName-count-one" : "Niederländischer Gulden",
				   "displayName-count-other" : "Niederländische Gulden"
				 },
				 "NOK" : {
				   "symbol" : "NOK",
				   "displayName" : "Norwegische Krone",
				   "displayName-count-one" : "Norwegische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Norwegische Kronen"
				 },
				 "NPR" : {
				   "symbol" : "NPR",
				   "displayName" : "Nepalesische Rupie",
				   "displayName-count-one" : "Nepalesische Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Nepalesische Rupien"
				 },
				 "NZD" : {
				   "symbol" : "NZ$",
				   "displayName" : "Neuseeland-Dollar",
				   "displayName-count-one" : "Neuseeland-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Neuseeland-Dollar"
				 },
				 "OMR" : {
				   "symbol" : "OMR",
				   "displayName" : "Omanischer Rial",
				   "displayName-count-one" : "Omanischer Rial",
				   "displayName-count-other" : "Omanische Rials"
				 },
				 "PAB" : {
				   "symbol" : "PAB",
				   "displayName" : "Panamaischer Balboa",
				   "displayName-count-one" : "Panamaischer Balboa",
				   "displayName-count-other" : "Panamaische Balboas"
				 },
				 "PEI" : {
				   "symbol" : "PEI",
				   "displayName" : "Peruanischer Inti",
				   "displayName-count-one" : "Peruanische Inti",
				   "displayName-count-other" : "Peruanische Inti"
				 },
				 "PEN" : {
				   "symbol" : "PEN",
				   "displayName" : "Peruanischer Sol",
				   "displayName-count-one" : "Peruanischer Sol",
				   "displayName-count-other" : "Peruanische Sol"
				 },
				 "PES" : {
				   "symbol" : "PES",
				   "displayName" : "Peruanischer Sol (1863–1965)",
				   "displayName-count-one" : "Peruanischer Sol (1863–1965)",
				   "displayName-count-other" : "Peruanische Sol (1863–1965)"
				 },
				 "PGK" : {
				   "symbol" : "PGK",
				   "displayName" : "Papua-Neuguineischer Kina",
				   "displayName-count-one" : "Papua-Neuguineischer Kina",
				   "displayName-count-other" : "Papua-Neuguineische Kina"
				 },
				 "PHP" : {
				   "symbol" : "PHP",
				   "displayName" : "Philippinischer Peso",
				   "displayName-count-one" : "Philippinischer Peso",
				   "symbol-alt-narrow" : "₱",
				   "displayName-count-other" : "Philippinische Pesos"
				 },
				 "PKR" : {
				   "symbol" : "PKR",
				   "displayName" : "Pakistanische Rupie",
				   "displayName-count-one" : "Pakistanische Rupie",
				   "symbol-alt-narrow" : "Rs",
				   "displayName-count-other" : "Pakistanische Rupien"
				 },
				 "PLN" : {
				   "symbol" : "PLN",
				   "displayName" : "Polnischer Złoty",
				   "displayName-count-one" : "Polnischer Złoty",
				   "symbol-alt-narrow" : "zł",
				   "displayName-count-other" : "Polnische Złoty"
				 },
				 "PLZ" : {
				   "symbol" : "PLZ",
				   "displayName" : "Polnischer Zloty (1950–1995)",
				   "displayName-count-one" : "Polnischer Zloty (1950–1995)",
				   "displayName-count-other" : "Polnische Zloty (1950–1995)"
				 },
				 "PTE" : {
				   "symbol" : "PTE",
				   "displayName" : "Portugiesischer Escudo",
				   "displayName-count-one" : "Portugiesische Escudo",
				   "displayName-count-other" : "Portugiesische Escudo"
				 },
				 "PYG" : {
				   "symbol" : "PYG",
				   "displayName" : "Paraguayischer Guaraní",
				   "displayName-count-one" : "Paraguayischer Guaraní",
				   "symbol-alt-narrow" : "₲",
				   "displayName-count-other" : "Paraguayische Guaraníes"
				 },
				 "QAR" : {
				   "symbol" : "QAR",
				   "displayName" : "Katar-Riyal",
				   "displayName-count-one" : "Katar-Riyal",
				   "displayName-count-other" : "Katar-Riyal"
				 },
				 "RHD" : {
				   "symbol" : "RHD",
				   "displayName" : "Rhodesischer Dollar",
				   "displayName-count-one" : "Rhodesische Dollar",
				   "displayName-count-other" : "Rhodesische Dollar"
				 },
				 "ROL" : {
				   "symbol" : "ROL",
				   "displayName" : "Rumänischer Leu (1952–2006)",
				   "displayName-count-one" : "Rumänischer Leu (1952–2006)",
				   "displayName-count-other" : "Rumänische Leu (1952–2006)"
				 },
				 "RON" : {
				   "symbol" : "RON",
				   "displayName" : "Rumänischer Leu",
				   "displayName-count-one" : "Rumänischer Leu",
				   "symbol-alt-narrow" : "L",
				   "displayName-count-other" : "Rumänische Leu"
				 },
				 "RSD" : {
				   "symbol" : "RSD",
				   "displayName" : "Serbischer Dinar",
				   "displayName-count-one" : "Serbischer Dinar",
				   "displayName-count-other" : "Serbische Dinaren"
				 },
				 "RUB" : {
				   "symbol" : "RUB",
				   "displayName" : "Russischer Rubel",
				   "displayName-count-one" : "Russischer Rubel",
				   "symbol-alt-narrow" : "₽",
				   "displayName-count-other" : "Russische Rubel"
				 },
				 "RUR" : {
				   "symbol" : "RUR",
				   "displayName" : "Russischer Rubel (1991–1998)",
				   "displayName-count-one" : "Russischer Rubel (1991–1998)",
				   "symbol-alt-narrow" : "р.",
				   "displayName-count-other" : "Russische Rubel (1991–1998)"
				 },
				 "RWF" : {
				   "symbol" : "RWF",
				   "displayName" : "Ruanda-Franc",
				   "displayName-count-one" : "Ruanda-Franc",
				   "symbol-alt-narrow" : "F.Rw",
				   "displayName-count-other" : "Ruanda-Francs"
				 },
				 "SAR" : {
				   "symbol" : "SAR",
				   "displayName" : "Saudi-Rial",
				   "displayName-count-one" : "Saudi-Rial",
				   "displayName-count-other" : "Saudi-Rial"
				 },
				 "SBD" : {
				   "symbol" : "SBD",
				   "displayName" : "Salomonen-Dollar",
				   "displayName-count-one" : "Salomonen-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Salomonen-Dollar"
				 },
				 "SCR" : {
				   "symbol" : "SCR",
				   "displayName" : "Seychellen-Rupie",
				   "displayName-count-one" : "Seychellen-Rupie",
				   "displayName-count-other" : "Seychellen-Rupien"
				 },
				 "SDD" : {
				   "symbol" : "SDD",
				   "displayName" : "Sudanesischer Dinar (1992–2007)",
				   "displayName-count-one" : "Sudanesischer Dinar (1992–2007)",
				   "displayName-count-other" : "Sudanesische Dinar (1992–2007)"
				 },
				 "SDG" : {
				   "symbol" : "SDG",
				   "displayName" : "Sudanesisches Pfund",
				   "displayName-count-one" : "Sudanesisches Pfund",
				   "displayName-count-other" : "Sudanesische Pfund"
				 },
				 "SDP" : {
				   "symbol" : "SDP",
				   "displayName" : "Sudanesisches Pfund (1957–1998)",
				   "displayName-count-one" : "Sudanesisches Pfund (1957–1998)",
				   "displayName-count-other" : "Sudanesische Pfund (1957–1998)"
				 },
				 "SEK" : {
				   "symbol" : "SEK",
				   "displayName" : "Schwedische Krone",
				   "displayName-count-one" : "Schwedische Krone",
				   "symbol-alt-narrow" : "kr",
				   "displayName-count-other" : "Schwedische Kronen"
				 },
				 "SGD" : {
				   "symbol" : "SGD",
				   "displayName" : "Singapur-Dollar",
				   "displayName-count-one" : "Singapur-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Singapur-Dollar"
				 },
				 "SHP" : {
				   "symbol" : "SHP",
				   "displayName" : "St. Helena-Pfund",
				   "displayName-count-one" : "St. Helena-Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "St. Helena-Pfund"
				 },
				 "SIT" : {
				   "symbol" : "SIT",
				   "displayName" : "Slowenischer Tolar",
				   "displayName-count-one" : "Slowenischer Tolar",
				   "displayName-count-other" : "Slowenische Tolar"
				 },
				 "SKK" : {
				   "symbol" : "SKK",
				   "displayName" : "Slowakische Krone",
				   "displayName-count-one" : "Slowakische Kronen",
				   "displayName-count-other" : "Slowakische Kronen"
				 },
				 "SLL" : {
				   "symbol" : "SLL",
				   "displayName" : "Sierra-leonischer Leone",
				   "displayName-count-one" : "Sierra-leonischer Leone",
				   "displayName-count-other" : "Sierra-leonische Leones"
				 },
				 "SOS" : {
				   "symbol" : "SOS",
				   "displayName" : "Somalia-Schilling",
				   "displayName-count-one" : "Somalia-Schilling",
				   "displayName-count-other" : "Somalia-Schilling"
				 },
				 "SRD" : {
				   "symbol" : "SRD",
				   "displayName" : "Suriname-Dollar",
				   "displayName-count-one" : "Suriname-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Suriname-Dollar"
				 },
				 "SRG" : {
				   "symbol" : "SRG",
				   "displayName" : "Suriname Gulden",
				   "displayName-count-one" : "Suriname-Gulden",
				   "displayName-count-other" : "Suriname-Gulden"
				 },
				 "SSP" : {
				   "symbol" : "SSP",
				   "displayName" : "Südsudanesisches Pfund",
				   "displayName-count-one" : "Südsudanesisches Pfund",
				   "symbol-alt-narrow" : "£",
				   "displayName-count-other" : "Südsudanesische Pfund"
				 },
				 "STD" : {
				   "symbol" : "STD",
				   "displayName" : "São-toméischer Dobra",
				   "displayName-count-one" : "São-toméischer Dobra",
				   "symbol-alt-narrow" : "Db",
				   "displayName-count-other" : "São-toméische Dobra"
				 },
				 "STN" : {
				   "symbol" : "STN",
				   "displayName" : "STN"
				 },
				 "SUR" : {
				   "symbol" : "SUR",
				   "displayName" : "Sowjetischer Rubel",
				   "displayName-count-one" : "Sowjetische Rubel",
				   "displayName-count-other" : "Sowjetische Rubel"
				 },
				 "SVC" : {
				   "symbol" : "SVC",
				   "displayName" : "El Salvador Colon",
				   "displayName-count-one" : "El Salvador-Colon",
				   "displayName-count-other" : "El Salvador-Colon"
				 },
				 "SYP" : {
				   "symbol" : "SYP",
				   "displayName" : "Syrisches Pfund",
				   "displayName-count-one" : "Syrisches Pfund",
				   "symbol-alt-narrow" : "SYP",
				   "displayName-count-other" : "Syrische Pfund"
				 },
				 "SZL" : {
				   "symbol" : "SZL",
				   "displayName" : "Swasiländischer Lilangeni",
				   "displayName-count-one" : "Swasiländischer Lilangeni",
				   "displayName-count-other" : "Swasiländische Emalangeni"
				 },
				 "THB" : {
				   "symbol" : "฿",
				   "displayName" : "Thailändischer Baht",
				   "displayName-count-one" : "Thailändischer Baht",
				   "symbol-alt-narrow" : "฿",
				   "displayName-count-other" : "Thailändische Baht"
				 },
				 "TJR" : {
				   "symbol" : "TJR",
				   "displayName" : "Tadschikistan Rubel",
				   "displayName-count-one" : "Tadschikistan-Rubel",
				   "displayName-count-other" : "Tadschikistan-Rubel"
				 },
				 "TJS" : {
				   "symbol" : "TJS",
				   "displayName" : "Tadschikistan-Somoni",
				   "displayName-count-one" : "Tadschikistan-Somoni",
				   "displayName-count-other" : "Tadschikistan-Somoni"
				 },
				 "TMM" : {
				   "symbol" : "TMM",
				   "displayName" : "Turkmenistan-Manat (1993–2009)",
				   "displayName-count-one" : "Turkmenistan-Manat (1993–2009)",
				   "displayName-count-other" : "Turkmenistan-Manat (1993–2009)"
				 },
				 "TMT" : {
				   "symbol" : "TMT",
				   "displayName" : "Turkmenistan-Manat",
				   "displayName-count-one" : "Turkmenistan-Manat",
				   "displayName-count-other" : "Turkmenistan-Manat"
				 },
				 "TND" : {
				   "symbol" : "TND",
				   "displayName" : "Tunesischer Dinar",
				   "displayName-count-one" : "Tunesischer Dinar",
				   "displayName-count-other" : "Tunesische Dinar"
				 },
				 "TOP" : {
				   "symbol" : "TOP",
				   "displayName" : "Tongaischer Paʻanga",
				   "displayName-count-one" : "Tongaischer Paʻanga",
				   "symbol-alt-narrow" : "T$",
				   "displayName-count-other" : "Tongaische Paʻanga"
				 },
				 "TPE" : {
				   "symbol" : "TPE",
				   "displayName" : "Timor-Escudo",
				   "displayName-count-one" : "Timor-Escudo",
				   "displayName-count-other" : "Timor-Escudo"
				 },
				 "TRL" : {
				   "symbol" : "TRL",
				   "displayName" : "Türkische Lira (1922–2005)",
				   "displayName-count-one" : "Türkische Lira (1922–2005)",
				   "displayName-count-other" : "Türkische Lira (1922–2005)"
				 },
				 "TRY" : {
				   "symbol" : "TRY",
				   "symbol-alt-variant" : "TL",
				   "displayName" : "Türkische Lira",
				   "displayName-count-one" : "Türkische Lira",
				   "symbol-alt-narrow" : "₺",
				   "displayName-count-other" : "Türkische Lira"
				 },
				 "TTD" : {
				   "symbol" : "TTD",
				   "displayName" : "Trinidad und Tobago-Dollar",
				   "displayName-count-one" : "Trinidad und Tobago-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Trinidad und Tobago-Dollar"
				 },
				 "TWD" : {
				   "symbol" : "NT$",
				   "displayName" : "Neuer Taiwan-Dollar",
				   "displayName-count-one" : "Neuer Taiwan-Dollar",
				   "symbol-alt-narrow" : "NT$",
				   "displayName-count-other" : "Neue Taiwan-Dollar"
				 },
				 "TZS" : {
				   "symbol" : "TZS",
				   "displayName" : "Tansania-Schilling",
				   "displayName-count-one" : "Tansania-Schilling",
				   "displayName-count-other" : "Tansania-Schilling"
				 },
				 "UAH" : {
				   "symbol" : "UAH",
				   "displayName" : "Ukrainische Hrywnja",
				   "displayName-count-one" : "Ukrainische Hrywnja",
				   "symbol-alt-narrow" : "₴",
				   "displayName-count-other" : "Ukrainische Hrywen"
				 },
				 "UAK" : {
				   "symbol" : "UAK",
				   "displayName" : "Ukrainischer Karbovanetz",
				   "displayName-count-one" : "Ukrainische Karbovanetz",
				   "displayName-count-other" : "Ukrainische Karbovanetz"
				 },
				 "UGS" : {
				   "symbol" : "UGS",
				   "displayName" : "Uganda-Schilling (1966–1987)",
				   "displayName-count-one" : "Uganda-Schilling (1966–1987)",
				   "displayName-count-other" : "Uganda-Schilling (1966–1987)"
				 },
				 "UGX" : {
				   "symbol" : "UGX",
				   "displayName" : "Uganda-Schilling",
				   "displayName-count-one" : "Uganda-Schilling",
				   "displayName-count-other" : "Uganda-Schilling"
				 },
				 "USD" : {
				   "symbol" : "$",
				   "displayName" : "US-Dollar",
				   "displayName-count-one" : "US-Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "US-Dollar"
				 },
				 "USN" : {
				   "symbol" : "USN",
				   "displayName" : "US Dollar (Nächster Tag)",
				   "displayName-count-one" : "US-Dollar (Nächster Tag)",
				   "displayName-count-other" : "US-Dollar (Nächster Tag)"
				 },
				 "USS" : {
				   "symbol" : "USS",
				   "displayName" : "US Dollar (Gleicher Tag)",
				   "displayName-count-one" : "US-Dollar (Gleicher Tag)",
				   "displayName-count-other" : "US-Dollar (Gleicher Tag)"
				 },
				 "UYI" : {
				   "symbol" : "UYI",
				   "displayName" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)",
				   "displayName-count-one" : "Uruguayischer Peso (Indexierte Rechnungseinheiten)",
				   "displayName-count-other" : "Uruguayische Pesos (Indexierte Rechnungseinheiten)"
				 },
				 "UYP" : {
				   "symbol" : "UYP",
				   "displayName" : "Uruguayischer Peso (1975–1993)",
				   "displayName-count-one" : "Uruguayischer Peso (1975–1993)",
				   "displayName-count-other" : "Uruguayische Pesos (1975–1993)"
				 },
				 "UYU" : {
				   "symbol" : "UYU",
				   "displayName" : "Uruguayischer Peso",
				   "displayName-count-one" : "Uruguayischer Peso",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Uruguayische Pesos"
				 },
				 "UZS" : {
				   "symbol" : "UZS",
				   "displayName" : "Usbekistan-Sum",
				   "displayName-count-one" : "Usbekistan-Sum",
				   "displayName-count-other" : "Usbekistan-Sum"
				 },
				 "VEB" : {
				   "symbol" : "VEB",
				   "displayName" : "Venezolanischer Bolívar (1871–2008)",
				   "displayName-count-one" : "Venezolanischer Bolívar (1871–2008)",
				   "displayName-count-other" : "Venezolanische Bolívares (1871–2008)"
				 },
				 "VEF" : {
				   "symbol" : "VEF",
				   "displayName" : "Venezolanischer Bolívar",
				   "displayName-count-one" : "Venezolanischer Bolívar",
				   "symbol-alt-narrow" : "Bs",
				   "displayName-count-other" : "Venezolanische Bolívares"
				 },
				 "VND" : {
				   "symbol" : "₫",
				   "displayName" : "Vietnamesischer Dong",
				   "displayName-count-one" : "Vietnamesischer Dong",
				   "symbol-alt-narrow" : "₫",
				   "displayName-count-other" : "Vietnamesische Dong"
				 },
				 "VNN" : {
				   "symbol" : "VNN",
				   "displayName" : "Vietnamesischer Dong(1978–1985)",
				   "displayName-count-one" : "Vietnamesischer Dong(1978–1985)",
				   "displayName-count-other" : "Vietnamesische Dong(1978–1985)"
				 },
				 "VUV" : {
				   "symbol" : "VUV",
				   "displayName" : "Vanuatu-Vatu",
				   "displayName-count-one" : "Vanuatu-Vatu",
				   "displayName-count-other" : "Vanuatu-Vatu"
				 },
				 "WST" : {
				   "symbol" : "WST",
				   "displayName" : "Samoanischer Tala",
				   "displayName-count-one" : "Samoanischer Tala",
				   "displayName-count-other" : "Samoanische Tala"
				 },
				 "XAF" : {
				   "symbol" : "FCFA",
				   "displayName" : "CFA-Franc (BEAC)",
				   "displayName-count-one" : "CFA-Franc (BEAC)",
				   "displayName-count-other" : "CFA-Franc (BEAC)"
				 },
				 "XAG" : {
				   "symbol" : "XAG",
				   "displayName" : "Unze Silber",
				   "displayName-count-one" : "Unze Silber",
				   "displayName-count-other" : "Unzen Silber"
				 },
				 "XAU" : {
				   "symbol" : "XAU",
				   "displayName" : "Unze Gold",
				   "displayName-count-one" : "Unze Gold",
				   "displayName-count-other" : "Unzen Gold"
				 },
				 "XBA" : {
				   "symbol" : "XBA",
				   "displayName" : "Europäische Rechnungseinheit",
				   "displayName-count-one" : "Europäische Rechnungseinheiten",
				   "displayName-count-other" : "Europäische Rechnungseinheiten"
				 },
				 "XBB" : {
				   "symbol" : "XBB",
				   "displayName" : "Europäische Währungseinheit (XBB)",
				   "displayName-count-one" : "Europäische Währungseinheiten (XBB)",
				   "displayName-count-other" : "Europäische Währungseinheiten (XBB)"
				 },
				 "XBC" : {
				   "symbol" : "XBC",
				   "displayName" : "Europäische Rechnungseinheit (XBC)",
				   "displayName-count-one" : "Europäische Rechnungseinheiten (XBC)",
				   "displayName-count-other" : "Europäische Rechnungseinheiten (XBC)"
				 },
				 "XBD" : {
				   "symbol" : "XBD",
				   "displayName" : "Europäische Rechnungseinheit (XBD)",
				   "displayName-count-one" : "Europäische Rechnungseinheiten (XBD)",
				   "displayName-count-other" : "Europäische Rechnungseinheiten (XBD)"
				 },
				 "XCD" : {
				   "symbol" : "EC$",
				   "displayName" : "Ostkaribischer Dollar",
				   "displayName-count-one" : "Ostkaribischer Dollar",
				   "symbol-alt-narrow" : "$",
				   "displayName-count-other" : "Ostkaribische Dollar"
				 },
				 "XDR" : {
				   "symbol" : "XDR",
				   "displayName" : "Sonderziehungsrechte",
				   "displayName-count-one" : "Sonderziehungsrechte",
				   "displayName-count-other" : "Sonderziehungsrechte"
				 },
				 "XEU" : {
				   "symbol" : "XEU",
				   "displayName" : "Europäische Währungseinheit (XEU)",
				   "displayName-count-one" : "Europäische Währungseinheiten (XEU)",
				   "displayName-count-other" : "Europäische Währungseinheiten (XEU)"
				 },
				 "XFO" : {
				   "symbol" : "XFO",
				   "displayName" : "Französischer Gold-Franc",
				   "displayName-count-one" : "Französische Gold-Franc",
				   "displayName-count-other" : "Französische Gold-Franc"
				 },
				 "XFU" : {
				   "symbol" : "XFU",
				   "displayName" : "Französischer UIC-Franc",
				   "displayName-count-one" : "Französische UIC-Franc",
				   "displayName-count-other" : "Französische UIC-Franc"
				 },
				 "XOF" : {
				   "symbol" : "CFA",
				   "displayName" : "CFA-Franc (BCEAO)",
				   "displayName-count-one" : "CFA-Franc (BCEAO)",
				   "displayName-count-other" : "CFA-Francs (BCEAO)"
				 },
				 "XPD" : {
				   "symbol" : "XPD",
				   "displayName" : "Unze Palladium",
				   "displayName-count-one" : "Unze Palladium",
				   "displayName-count-other" : "Unzen Palladium"
				 },
				 "XPF" : {
				   "symbol" : "CFPF",
				   "displayName" : "CFP-Franc",
				   "displayName-count-one" : "CFP-Franc",
				   "displayName-count-other" : "CFP-Franc"
				 },
				 "XPT" : {
				   "symbol" : "XPT",
				   "displayName" : "Unze Platin",
				   "displayName-count-one" : "Unze Platin",
				   "displayName-count-other" : "Unzen Platin"
				 },
				 "XRE" : {
				   "symbol" : "XRE",
				   "displayName" : "RINET Funds",
				   "displayName-count-one" : "RINET Funds",
				   "displayName-count-other" : "RINET Funds"
				 },
				 "XSU" : {
				   "symbol" : "XSU",
				   "displayName" : "SUCRE",
				   "displayName-count-one" : "SUCRE",
				   "displayName-count-other" : "SUCRE"
				 },
				 "XTS" : {
				   "symbol" : "XTS",
				   "displayName" : "Testwährung",
				   "displayName-count-one" : "Testwährung",
				   "displayName-count-other" : "Testwährung"
				 },
				 "XUA" : {
				   "symbol" : "XUA",
				   "displayName" : "Rechnungseinheit der AfEB",
				   "displayName-count-one" : "Rechnungseinheit der AfEB",
				   "displayName-count-other" : "Rechnungseinheiten der AfEB"
				 },
				 "XXX" : {
				   "symbol" : "XXX",
				   "displayName" : "Unbekannte Währung",
				   "displayName-count-one" : "(unbekannte Währung)",
				   "displayName-count-other" : "(unbekannte Währung)"
				 },
				 "YDD" : {
				   "symbol" : "YDD",
				   "displayName" : "Jemen-Dinar",
				   "displayName-count-one" : "Jemen-Dinar",
				   "displayName-count-other" : "Jemen-Dinar"
				 },
				 "YER" : {
				   "symbol" : "YER",
				   "displayName" : "Jemen-Rial",
				   "displayName-count-one" : "Jemen-Rial",
				   "displayName-count-other" : "Jemen-Rial"
				 },
				 "YUD" : {
				   "symbol" : "YUD",
				   "displayName" : "Jugoslawischer Dinar (1966–1990)",
				   "displayName-count-one" : "Jugoslawischer Dinar (1966–1990)",
				   "displayName-count-other" : "Jugoslawische Dinar (1966–1990)"
				 },
				 "YUM" : {
				   "symbol" : "YUM",
				   "displayName" : "Jugoslawischer Neuer Dinar (1994–2002)",
				   "displayName-count-one" : "Jugoslawischer Neuer Dinar (1994–2002)",
				   "displayName-count-other" : "Jugoslawische Neue Dinar (1994–2002)"
				 },
				 "YUN" : {
				   "symbol" : "YUN",
				   "displayName" : "Jugoslawischer Dinar (konvertibel)",
				   "displayName-count-one" : "Jugoslawische Dinar (konvertibel)",
				   "displayName-count-other" : "Jugoslawische Dinar (konvertibel)"
				 },
				 "YUR" : {
				   "symbol" : "YUR",
				   "displayName" : "Jugoslawischer reformierter Dinar (1992–1993)",
				   "displayName-count-one" : "Jugoslawischer reformierter Dinar (1992–1993)",
				   "displayName-count-other" : "Jugoslawische reformierte Dinar (1992–1993)"
				 },
				 "ZAL" : {
				   "symbol" : "ZAL",
				   "displayName" : "Südafrikanischer Rand (Finanz)",
				   "displayName-count-one" : "Südafrikanischer Rand (Finanz)",
				   "displayName-count-other" : "Südafrikanischer Rand (Finanz)"
				 },
				 "ZAR" : {
				   "symbol" : "ZAR",
				   "displayName" : "Südafrikanischer Rand",
				   "displayName-count-one" : "Südafrikanischer Rand",
				   "symbol-alt-narrow" : "R",
				   "displayName-count-other" : "Südafrikanische Rand"
				 },
				 "ZMK" : {
				   "symbol" : "ZMK",
				   "displayName" : "Kwacha (1968–2012)",
				   "displayName-count-one" : "Kwacha (1968–2012)",
				   "displayName-count-other" : "Kwacha (1968–2012)"
				 },
				 "ZMW" : {
				   "symbol" : "ZMW",
				   "displayName" : "Kwacha",
				   "displayName-count-one" : "Kwacha",
				   "symbol-alt-narrow" : "K",
				   "displayName-count-other" : "Kwacha"
				 },
				 "ZRN" : {
				   "symbol" : "ZRN",
				   "displayName" : "Zaire-Neuer Zaïre (1993–1998)",
				   "displayName-count-one" : "Zaire-Neuer Zaïre (1993–1998)",
				   "displayName-count-other" : "Zaire-Neue Zaïre (1993–1998)"
				 },
				 "ZRZ" : {
				   "symbol" : "ZRZ",
				   "displayName" : "Zaire-Zaïre (1971–1993)",
				   "displayName-count-one" : "Zaire-Zaïre (1971–1993)",
				   "displayName-count-other" : "Zaire-Zaïre (1971–1993)"
				 },
				 "ZWD" : {
				   "symbol" : "ZWD",
				   "displayName" : "Simbabwe-Dollar (1980–2008)",
				   "displayName-count-one" : "Simbabwe-Dollar (1980–2008)",
				   "displayName-count-other" : "Simbabwe-Dollar (1980–2008)"
				 },
				 "ZWL" : {
				   "symbol" : "ZWL",
				   "displayName" : "Simbabwe-Dollar (2009)",
				   "displayName-count-one" : "Simbabwe-Dollar (2009)",
				   "displayName-count-other" : "Simbabwe-Dollar (2009)"
				 },
				 "ZWR" : {
				   "symbol" : "ZWR",
				   "displayName" : "Simbabwe-Dollar (2008)",
				   "displayName-count-one" : "Simbabwe-Dollar (2008)",
				   "displayName-count-other" : "Simbabwe-Dollar (2008)"
				 }
			   },"dates":{
				 "dayPeriodsFormat" : {
				   "narrow" : [ "vm.", "nm." ],
				   "abbreviated" : [ "AM", "PM" ],
				   "wide" : [ "AM", "PM" ]
				 },
				 "dayPeriodsStandalone" : {
				   "narrow" : [ "vorm.", "nachm." ],
				   "abbreviated" : [ "vorm.", "nachm." ],
				   "wide" : [ "vorm.", "nachm." ]
				 },
				 "daysFormat" : {
				   "narrow" : [ "S", "M", "D", "M", "D", "F", "S" ],
				   "abbreviated" : [ "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa." ],
				   "wide" : [ "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag" ],
				   "short" : [ "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa." ]
				 },
				 "daysStandalone" : {
				   "narrow" : [ "S", "M", "D", "M", "D", "F", "S" ],
				   "abbreviated" : [ "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa" ],
				   "wide" : [ "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag" ],
				   "short" : [ "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa." ]
				 },
				 "monthsFormat" : {
				   "narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],
				   "abbreviated" : [ "Jan.", "Feb.", "März", "Apr.", "Mai", "Juni", "Juli", "Aug.", "Sep.", "Okt.", "Nov.", "Dez." ],
				   "wide" : [ "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" ]
				 },
				 "monthsStandalone" : {
				   "narrow" : [ "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" ],
				   "abbreviated" : [ "Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez" ],
				   "wide" : [ "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" ]
				 },
				 "eras" : {
				   "narrow" : [ "v. Chr.", "n. Chr." ],
				   "abbreviated" : [ "v. Chr.", "n. Chr." ],
				   "wide" : [ "v. Chr.", "n. Chr." ]
				 },
				 "firstDayOfWeek" : 1,
				 "weekendRange" : [ 6, 0 ],
				 "dateFormats" : {
				   "short" : "dd.MM.yy",
				   "medium" : "dd.MM.y",
				   "long" : "d. MMMM y",
				   "full" : "EEEE, d. MMMM y"
				 },
				 "timeFormats" : {
				   "short" : "HH:mm",
				   "medium" : "HH:mm:ss",
				   "long" : "HH:mm:ss z",
				   "full" : "HH:mm:ss zzzz"
				 },
				 "dateTimeFormats" : {
				   "short" : "{1}, {0}",
				   "medium" : "{1}, {0}",
				   "long" : "{1} 'um' {0}",
				   "full" : "{1} 'um' {0}",
				   "appendItems" : {
					 "Day" : "{0} ({2}: {1})",
					 "Day-Of-Week" : "{0} {1}",
					 "Era" : "{1} {0}",
					 "Hour" : "{0} ({2}: {1})",
					 "Minute" : "{0} ({2}: {1})",
					 "Month" : "{0} ({2}: {1})",
					 "Quarter" : "{0} ({2}: {1})",
					 "Second" : "{0} ({2}: {1})",
					 "Timezone" : "{0} {1}",
					 "Week" : "{0} ({2}: {1})",
					 "Year" : "{1} {0}"
				   },
				   "intervalFormats" : {
					 "d" : {
					   "d" : "d.–d."
					 },
					 "H" : {
					   "H" : "HH–HH 'Uhr'"
					 },
					 "h" : {
					   "a" : "h 'Uhr' a – h 'Uhr' a",
					   "h" : "h – h 'Uhr' a"
					 },
					 "hm" : {
					   "a" : "h:mm a – h:mm a",
					   "h" : "h:mm–h:mm a",
					   "m" : "h:mm–h:mm a"
					 },
					 "Hm" : {
					   "H" : "HH:mm–HH:mm 'Uhr'",
					   "m" : "HH:mm–HH:mm 'Uhr'"
					 },
					 "hmv" : {
					   "a" : "h:mm a – h:mm a v",
					   "h" : "h:mm–h:mm a v",
					   "m" : "h:mm–h:mm a v"
					 },
					 "Hmv" : {
					   "H" : "HH:mm–HH:mm 'Uhr' v",
					   "m" : "HH:mm–HH:mm 'Uhr' v"
					 },
					 "hv" : {
					   "a" : "h a – h a v",
					   "h" : "h–h a v"
					 },
					 "Hv" : {
					   "H" : "HH–HH 'Uhr' v"
					 },
					 "intervalFormatFallback" : "{0} – {1}",
					 "M" : {
					   "M" : "M.–M."
					 },
					 "Md" : {
					   "d" : "dd.MM. – dd.MM.",
					   "M" : "dd.MM. – dd.MM."
					 },
					 "MEd" : {
					   "d" : "E, dd.MM. – E, dd.MM.",
					   "M" : "E, dd.MM. – E, dd.MM."
					 },
					 "MMM" : {
					   "M" : "MMM–MMM"
					 },
					 "MMMd" : {
					   "d" : "d.–d. MMM",
					   "M" : "d. MMM – d. MMM"
					 },
					 "MMMEd" : {
					   "d" : "E, d. – E, d. MMM",
					   "M" : "E, d. MMM – E, d. MMM"
					 },
					 "MMMM" : {
					   "M" : "LLLL–LLLL"
					 },
					 "y" : {
					   "y" : "y–y"
					 },
					 "yM" : {
					   "y" : "MM.y – MM.y",
					   "M" : "MM.y – MM.y"
					 },
					 "yMd" : {
					   "d" : "dd.MM.y – dd.MM.y",
					   "y" : "dd.MM.y – dd.MM.y",
					   "M" : "dd.MM.y – dd.MM.y"
					 },
					 "yMEd" : {
					   "d" : "E, dd.MM.y – E, dd.MM.y",
					   "y" : "E, dd.MM.y – E, dd.MM.y",
					   "M" : "E, dd.MM.y – E, dd.MM.y"
					 },
					 "yMMM" : {
					   "y" : "MMM y – MMM y",
					   "M" : "MMM–MMM y"
					 },
					 "yMMMd" : {
					   "d" : "d.–d. MMM y",
					   "y" : "d. MMM y – d. MMM y",
					   "M" : "d. MMM – d. MMM y"
					 },
					 "yMMMEd" : {
					   "d" : "E, d. – E, d. MMM y",
					   "y" : "E, d. MMM y – E, d. MMM y",
					   "M" : "E, d. MMM – E, d. MMM y"
					 },
					 "yMMMM" : {
					   "y" : "MMMM y – MMMM y",
					   "M" : "MMMM–MMMM y"
					 }
				   },
				   "availableFormats" : {
					 "Bh" : "h B",
					 "Bhm" : "h:mm B",
					 "Bhms" : "h:mm:ss B",
					 "d" : "d",
					 "E" : "ccc",
					 "EBhm" : "E h:mm B",
					 "EBhms" : "E h:mm:ss B",
					 "Ed" : "E, d.",
					 "Ehm" : "E h:mm a",
					 "EHm" : "E, HH:mm",
					 "Ehms" : "E, h:mm:ss a",
					 "EHms" : "E, HH:mm:ss",
					 "Gy" : "y G",
					 "GyMMM" : "MMM y G",
					 "GyMMMd" : "d. MMM y G",
					 "GyMMMEd" : "E, d. MMM y G",
					 "H" : "HH 'Uhr'",
					 "h" : "h 'Uhr' a",
					 "hm" : "h:mm a",
					 "Hm" : "HH:mm",
					 "hms" : "h:mm:ss a",
					 "Hms" : "HH:mm:ss",
					 "hmsv" : "h:mm:ss a v",
					 "Hmsv" : "HH:mm:ss v",
					 "hmv" : "h:mm a v",
					 "Hmv" : "HH:mm v",
					 "hmz" : "HH:mm zzzz",
					 "M" : "L",
					 "Md" : "d.M.",
					 "MEd" : "E, d.M.",
					 "MMd" : "d.MM.",
					 "MMdd" : "dd.MM.",
					 "MMM" : "LLL",
					 "MMMd" : "d. MMM",
					 "MMMEd" : "E, d. MMM",
					 "MMMMd" : "d. MMMM",
					 "MMMMEd" : "E, d. MMMM",
					 "MMMMW-count-one" : "'Woche' W 'im' MMM",
					 "MMMMW-count-other" : "'Woche' W 'im' MMM",
					 "ms" : "mm:ss",
					 "y" : "y",
					 "yM" : "M.y",
					 "yMd" : "d.M.y",
					 "yMEd" : "E, d.M.y",
					 "yMM" : "MM.y",
					 "yMMdd" : "dd.MM.y",
					 "yMMM" : "MMM y",
					 "yMMMd" : "d. MMM y",
					 "yMMMEd" : "E, d. MMM y",
					 "yMMMM" : "MMMM y",
					 "yQQQ" : "QQQ y",
					 "yQQQQ" : "QQQQ y",
					 "yw-count-one" : "'Woche' w 'des' 'Jahres' Y",
					 "yw-count-other" : "'Woche' w 'des' 'Jahres' Y"
				   }
				 }
			   },"numbers":{
				 "defaultNumberingSystem" : "latn",
				 "numberSymbols" : {
				   "decimal" : ",",
				   "group" : ".",
				   "list" : ";",
				   "percentSign" : "%",
				   "plusSign" : "+",
				   "minusSign" : "-",
				   "exponential" : "E",
				   "superscriptingExponent" : "·",
				   "perMille" : "‰",
				   "infinity" : "∞",
				   "nan" : "NaN",
				   "timeSeparator" : ":"
				 },
				 "numberFormats" : {
				   "decimalFormats" : "#,##0.###",
				   "percentFormats" : "#,##0 %",
				   "currencyFormats" : "#,##0.00 ¤",
				   "scientificFormats" : "#E0",
				   "decimalFormats-long" : {
					 "decimalFormat" : {
					   "1000-count-one" : "0 Tausend",
					   "1000-count-other" : "0 Tausend",
					   "10000-count-one" : "00 Tausend",
					   "10000-count-other" : "00 Tausend",
					   "100000-count-one" : "000 Tausend",
					   "100000-count-other" : "000 Tausend",
					   "1000000-count-one" : "0 Million",
					   "1000000-count-other" : "0 Millionen",
					   "10000000-count-one" : "00 Millionen",
					   "10000000-count-other" : "00 Millionen",
					   "100000000-count-one" : "000 Millionen",
					   "100000000-count-other" : "000 Millionen",
					   "1000000000-count-one" : "0 Milliarde",
					   "1000000000-count-other" : "0 Milliarden",
					   "10000000000-count-one" : "00 Milliarden",
					   "10000000000-count-other" : "00 Milliarden",
					   "100000000000-count-one" : "000 Milliarden",
					   "100000000000-count-other" : "000 Milliarden",
					   "1000000000000-count-one" : "0 Billion",
					   "1000000000000-count-other" : "0 Billionen",
					   "10000000000000-count-one" : "00 Billionen",
					   "10000000000000-count-other" : "00 Billionen",
					   "100000000000000-count-one" : "000 Billionen",
					   "100000000000000-count-other" : "000 Billionen"
					 }
				   },
				   "decimalFormats-short" : {
					 "decimalFormat" : {
					   "1000-count-one" : "0 Tsd'.'",
					   "1000-count-other" : "0 Tsd'.'",
					   "10000-count-one" : "00 Tsd'.'",
					   "10000-count-other" : "00 Tsd'.'",
					   "100000-count-one" : "000 Tsd'.'",
					   "100000-count-other" : "000 Tsd'.'",
					   "1000000-count-one" : "0 Mio'.'",
					   "1000000-count-other" : "0 Mio'.'",
					   "10000000-count-one" : "00 Mio'.'",
					   "10000000-count-other" : "00 Mio'.'",
					   "100000000-count-one" : "000 Mio'.'",
					   "100000000-count-other" : "000 Mio'.'",
					   "1000000000-count-one" : "0 Mrd'.'",
					   "1000000000-count-other" : "0 Mrd'.'",
					   "10000000000-count-one" : "00 Mrd'.'",
					   "10000000000-count-other" : "00 Mrd'.'",
					   "100000000000-count-one" : "000 Mrd'.'",
					   "100000000000-count-other" : "000 Mrd'.'",
					   "1000000000000-count-one" : "0 Bio'.'",
					   "1000000000000-count-other" : "0 Bio'.'",
					   "10000000000000-count-one" : "00 Bio'.'",
					   "10000000000000-count-other" : "00 Bio'.'",
					   "100000000000000-count-one" : "000 Bio'.'",
					   "100000000000000-count-other" : "000 Bio'.'"
					 }
				   },
				   "currencyFormats-short" : {
					 "standard" : {
					   "1000-count-one" : "0 Tsd'.' ¤",
					   "1000-count-other" : "0 Tsd'.' ¤",
					   "10000-count-one" : "00 Tsd'.' ¤",
					   "10000-count-other" : "00 Tsd'.' ¤",
					   "100000-count-one" : "000 Tsd'.' ¤",
					   "100000-count-other" : "000 Tsd'.' ¤",
					   "1000000-count-one" : "0 Mio'.' ¤",
					   "1000000-count-other" : "0 Mio'.' ¤",
					   "10000000-count-one" : "00 Mio'.' ¤",
					   "10000000-count-other" : "00 Mio'.' ¤",
					   "100000000-count-one" : "000 Mio'.' ¤",
					   "100000000-count-other" : "000 Mio'.' ¤",
					   "1000000000-count-one" : "0 Mrd'.' ¤",
					   "1000000000-count-other" : "0 Mrd'.' ¤",
					   "10000000000-count-one" : "00 Mrd'.' ¤",
					   "10000000000-count-other" : "00 Mrd'.' ¤",
					   "100000000000-count-one" : "000 Mrd'.' ¤",
					   "100000000000-count-other" : "000 Mrd'.' ¤",
					   "1000000000000-count-one" : "0 Bio'.' ¤",
					   "1000000000000-count-other" : "0 Bio'.' ¤",
					   "10000000000000-count-one" : "00 Bio'.' ¤",
					   "10000000000000-count-other" : "00 Bio'.' ¤",
					   "100000000000000-count-one" : "000 Bio'.' ¤",
					   "100000000000000-count-other" : "000 Bio'.' ¤"
					 }
				   }
				 }
			   },"plurals":{
				 "pluralRules" : {
				   "pluralRule-count-one" : "i = 1 and v = 0 @integer 1",
				   "pluralRule-count-other" : " @integer 0, 2~16, 100, 1000, 10000, 100000, 1000000, … @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …"
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
		   }}}}}`},
		{"es-MX", "dateFields",
			`{"response":{"code":200,"message":"OK"},"data":{"localeID":"es-MX","language":"es","region":"MX","categories":{"dateFields":{"day":{"displayName":"día","relative-type--1":"ayer","relative-type--2":"anteayer","relative-type-0":"hoy","relative-type-1":"mañana","relative-type-2":"pasado mañana","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} día","relativeTimePattern-count-other":"dentro de {0} días"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} día","relativeTimePattern-count-other":"hace {0} días"}},"hour":{"displayName":"hora","relative-type-0":"esta hora","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} hora","relativeTimePattern-count-other":"dentro de {0} horas"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} hora","relativeTimePattern-count-other":"hace {0} horas"}},"minute":{"displayName":"minuto","relative-type-0":"este minuto","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} minuto","relativeTimePattern-count-other":"dentro de {0} minutos"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} minuto","relativeTimePattern-count-other":"hace {0} minutos"}},"month":{"displayName":"mes","relative-type--1":"el mes pasado","relative-type-0":"este mes","relative-type-1":"el mes próximo","relativeTime-type-future":{"relativeTimePattern-count-one":"en {0} mes","relativeTimePattern-count-other":"en {0} meses"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} mes","relativeTimePattern-count-other":"hace {0} meses"}},"second":{"displayName":"segundo","relative-type-0":"ahora","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} segundo","relativeTimePattern-count-other":"dentro de {0} segundos"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} segundo","relativeTimePattern-count-other":"hace {0} segundos"}},"year":{"displayName":"año","relative-type--1":"el año pasado","relative-type-0":"este año","relative-type-1":"el año próximo","relativeTime-type-future":{"relativeTimePattern-count-one":"dentro de {0} año","relativeTimePattern-count-other":"dentro de {0} años"},"relativeTime-type-past":{"relativeTimePattern-count-one":"hace {0} año","relativeTimePattern-count-other":"hace {0} años"}}}}}}`},
	} {
		d := d
		t.Run(d.locale+":"+d.scope, func(t *testing.T) {
			req := e.GET(GetPatternByLocaleURL, d.locale)
			if d.scope != "" {
				req.WithQuery("scope", d.scope)
			}
			resp := req.Expect()

			resp.Status(http.StatusOK)

			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}

func TestGetRegions(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	for _, d := range []struct{ input, wanted string }{
		{"en", `{"response":{"code":200,"message":"OK"},"data":[{"language":"en","defaultRegionCode":"US","territories":{"AC":"Ascension Island","AD":"Andorra","AE":"United Arab Emirates","AF":"Afghanistan","AG":"Antigua \u0026 Barbuda","AI":"Anguilla","AL":"Albania","AM":"Armenia","AO":"Angola","AQ":"Antarctica","AR":"Argentina","AS":"American Samoa","AT":"Austria","AU":"Australia","AW":"Aruba","AX":"Åland Islands","AZ":"Azerbaijan","BA":"Bosnia \u0026 Herzegovina","BB":"Barbados","BD":"Bangladesh","BE":"Belgium","BF":"Burkina Faso","BG":"Bulgaria","BH":"Bahrain","BI":"Burundi","BJ":"Benin","BL":"St. Barthélemy","BM":"Bermuda","BN":"Brunei","BO":"Bolivia","BQ":"Caribbean Netherlands","BR":"Brazil","BS":"Bahamas","BT":"Bhutan","BV":"Bouvet Island","BW":"Botswana","BY":"Belarus","BZ":"Belize","CA":"Canada","CC":"Cocos (Keeling) Islands","CD":"Congo - Kinshasa","CF":"Central African Republic","CG":"Congo - Brazzaville","CH":"Switzerland","CI":"Côte d’Ivoire","CK":"Cook Islands","CL":"Chile","CM":"Cameroon","CN":"China","CO":"Colombia","CP":"Clipperton Island","CR":"Costa Rica","CU":"Cuba","CV":"Cape Verde","CW":"Curaçao","CX":"Christmas Island","CY":"Cyprus","CZ":"Czechia","DE":"Germany","DG":"Diego Garcia","DJ":"Djibouti","DK":"Denmark","DM":"Dominica","DO":"Dominican Republic","DZ":"Algeria","EA":"Ceuta \u0026 Melilla","EC":"Ecuador","EE":"Estonia","EG":"Egypt","EH":"Western Sahara","ER":"Eritrea","ES":"Spain","ET":"Ethiopia","FI":"Finland","FJ":"Fiji","FK":"Falkland Islands","FM":"Micronesia","FO":"Faroe Islands","FR":"France","GA":"Gabon","GB":"United Kingdom","GD":"Grenada","GE":"Georgia","GF":"French Guiana","GG":"Guernsey","GH":"Ghana","GI":"Gibraltar","GL":"Greenland","GM":"Gambia","GN":"Guinea","GP":"Guadeloupe","GQ":"Equatorial Guinea","GR":"Greece","GS":"South Georgia \u0026 South Sandwich Islands","GT":"Guatemala","GU":"Guam","GW":"Guinea-Bissau","GY":"Guyana","HK":"Hong Kong SAR China","HM":"Heard \u0026 McDonald Islands","HN":"Honduras","HR":"Croatia","HT":"Haiti","HU":"Hungary","IC":"Canary Islands","ID":"Indonesia","IE":"Ireland","IL":"Israel","IM":"Isle of Man","IN":"India","IO":"British Indian Ocean Territory","IQ":"Iraq","IR":"Iran","IS":"Iceland","IT":"Italy","JE":"Jersey","JM":"Jamaica","JO":"Jordan","JP":"Japan","KE":"Kenya","KG":"Kyrgyzstan","KH":"Cambodia","KI":"Kiribati","KM":"Comoros","KN":"St. Kitts \u0026 Nevis","KP":"North Korea","KR":"South Korea","KW":"Kuwait","KY":"Cayman Islands","KZ":"Kazakhstan","LA":"Laos","LB":"Lebanon","LC":"St. Lucia","LI":"Liechtenstein","LK":"Sri Lanka","LR":"Liberia","LS":"Lesotho","LT":"Lithuania","LU":"Luxembourg","LV":"Latvia","LY":"Libya","MA":"Morocco","MC":"Monaco","MD":"Moldova","ME":"Montenegro","MF":"St. Martin","MG":"Madagascar","MH":"Marshall Islands","MK":"Macedonia","ML":"Mali","MM":"Myanmar (Burma)","MN":"Mongolia","MO":"Macau SAR China","MP":"Northern Mariana Islands","MQ":"Martinique","MR":"Mauritania","MS":"Montserrat","MT":"Malta","MU":"Mauritius","MV":"Maldives","MW":"Malawi","MX":"Mexico","MY":"Malaysia","MZ":"Mozambique","NA":"Namibia","NC":"New Caledonia","NE":"Niger","NF":"Norfolk Island","NG":"Nigeria","NI":"Nicaragua","NL":"Netherlands","NO":"Norway","NP":"Nepal","NR":"Nauru","NU":"Niue","NZ":"New Zealand","OM":"Oman","PA":"Panama","PE":"Peru","PF":"French Polynesia","PG":"Papua New Guinea","PH":"Philippines","PK":"Pakistan","PL":"Poland","PM":"St. Pierre \u0026 Miquelon","PN":"Pitcairn Islands","PR":"Puerto Rico","PS":"Palestinian Territories","PT":"Portugal","PW":"Palau","PY":"Paraguay","QA":"Qatar","RE":"Réunion","RO":"Romania","RS":"Serbia","RU":"Russia","RW":"Rwanda","SA":"Saudi Arabia","SB":"Solomon Islands","SC":"Seychelles","SD":"Sudan","SE":"Sweden","SG":"Singapore","SH":"St. Helena","SI":"Slovenia","SJ":"Svalbard \u0026 Jan Mayen","SK":"Slovakia","SL":"Sierra Leone","SM":"San Marino","SN":"Senegal","SO":"Somalia","SR":"Suriname","SS":"South Sudan","ST":"São Tomé \u0026 Príncipe","SV":"El Salvador","SX":"Sint Maarten","SY":"Syria","SZ":"Swaziland","TA":"Tristan da Cunha","TC":"Turks \u0026 Caicos Islands","TD":"Chad","TF":"French Southern Territories","TG":"Togo","TH":"Thailand","TJ":"Tajikistan","TK":"Tokelau","TL":"Timor-Leste","TM":"Turkmenistan","TN":"Tunisia","TO":"Tonga","TR":"Turkey","TT":"Trinidad \u0026 Tobago","TV":"Tuvalu","TW":"Taiwan","TZ":"Tanzania","UA":"Ukraine","UG":"Uganda","UM":"U.S. Outlying Islands","US":"United States","UY":"Uruguay","UZ":"Uzbekistan","VA":"Vatican City","VC":"St. Vincent \u0026 Grenadines","VE":"Venezuela","VG":"British Virgin Islands","VI":"U.S. Virgin Islands","VN":"Vietnam","VU":"Vanuatu","WF":"Wallis \u0026 Futuna","WS":"Samoa","XK":"Kosovo","YE":"Yemen","YT":"Mayotte","ZA":"South Africa","ZM":"Zambia","ZW":"Zimbabwe","ZZ":"Unknown Region"}}]}`},
		{"en-US", `{"response":{"code":200,"message":"OK"},"data":[{"language":"en","defaultRegionCode":"US","territories":{"AC":"Ascension Island","AD":"Andorra","AE":"United Arab Emirates","AF":"Afghanistan","AG":"Antigua \u0026 Barbuda","AI":"Anguilla","AL":"Albania","AM":"Armenia","AO":"Angola","AQ":"Antarctica","AR":"Argentina","AS":"American Samoa","AT":"Austria","AU":"Australia","AW":"Aruba","AX":"Åland Islands","AZ":"Azerbaijan","BA":"Bosnia \u0026 Herzegovina","BB":"Barbados","BD":"Bangladesh","BE":"Belgium","BF":"Burkina Faso","BG":"Bulgaria","BH":"Bahrain","BI":"Burundi","BJ":"Benin","BL":"St. Barthélemy","BM":"Bermuda","BN":"Brunei","BO":"Bolivia","BQ":"Caribbean Netherlands","BR":"Brazil","BS":"Bahamas","BT":"Bhutan","BV":"Bouvet Island","BW":"Botswana","BY":"Belarus","BZ":"Belize","CA":"Canada","CC":"Cocos (Keeling) Islands","CD":"Congo - Kinshasa","CF":"Central African Republic","CG":"Congo - Brazzaville","CH":"Switzerland","CI":"Côte d’Ivoire","CK":"Cook Islands","CL":"Chile","CM":"Cameroon","CN":"China","CO":"Colombia","CP":"Clipperton Island","CR":"Costa Rica","CU":"Cuba","CV":"Cape Verde","CW":"Curaçao","CX":"Christmas Island","CY":"Cyprus","CZ":"Czechia","DE":"Germany","DG":"Diego Garcia","DJ":"Djibouti","DK":"Denmark","DM":"Dominica","DO":"Dominican Republic","DZ":"Algeria","EA":"Ceuta \u0026 Melilla","EC":"Ecuador","EE":"Estonia","EG":"Egypt","EH":"Western Sahara","ER":"Eritrea","ES":"Spain","ET":"Ethiopia","FI":"Finland","FJ":"Fiji","FK":"Falkland Islands","FM":"Micronesia","FO":"Faroe Islands","FR":"France","GA":"Gabon","GB":"United Kingdom","GD":"Grenada","GE":"Georgia","GF":"French Guiana","GG":"Guernsey","GH":"Ghana","GI":"Gibraltar","GL":"Greenland","GM":"Gambia","GN":"Guinea","GP":"Guadeloupe","GQ":"Equatorial Guinea","GR":"Greece","GS":"South Georgia \u0026 South Sandwich Islands","GT":"Guatemala","GU":"Guam","GW":"Guinea-Bissau","GY":"Guyana","HK":"Hong Kong SAR China","HM":"Heard \u0026 McDonald Islands","HN":"Honduras","HR":"Croatia","HT":"Haiti","HU":"Hungary","IC":"Canary Islands","ID":"Indonesia","IE":"Ireland","IL":"Israel","IM":"Isle of Man","IN":"India","IO":"British Indian Ocean Territory","IQ":"Iraq","IR":"Iran","IS":"Iceland","IT":"Italy","JE":"Jersey","JM":"Jamaica","JO":"Jordan","JP":"Japan","KE":"Kenya","KG":"Kyrgyzstan","KH":"Cambodia","KI":"Kiribati","KM":"Comoros","KN":"St. Kitts \u0026 Nevis","KP":"North Korea","KR":"South Korea","KW":"Kuwait","KY":"Cayman Islands","KZ":"Kazakhstan","LA":"Laos","LB":"Lebanon","LC":"St. Lucia","LI":"Liechtenstein","LK":"Sri Lanka","LR":"Liberia","LS":"Lesotho","LT":"Lithuania","LU":"Luxembourg","LV":"Latvia","LY":"Libya","MA":"Morocco","MC":"Monaco","MD":"Moldova","ME":"Montenegro","MF":"St. Martin","MG":"Madagascar","MH":"Marshall Islands","MK":"Macedonia","ML":"Mali","MM":"Myanmar (Burma)","MN":"Mongolia","MO":"Macau SAR China","MP":"Northern Mariana Islands","MQ":"Martinique","MR":"Mauritania","MS":"Montserrat","MT":"Malta","MU":"Mauritius","MV":"Maldives","MW":"Malawi","MX":"Mexico","MY":"Malaysia","MZ":"Mozambique","NA":"Namibia","NC":"New Caledonia","NE":"Niger","NF":"Norfolk Island","NG":"Nigeria","NI":"Nicaragua","NL":"Netherlands","NO":"Norway","NP":"Nepal","NR":"Nauru","NU":"Niue","NZ":"New Zealand","OM":"Oman","PA":"Panama","PE":"Peru","PF":"French Polynesia","PG":"Papua New Guinea","PH":"Philippines","PK":"Pakistan","PL":"Poland","PM":"St. Pierre \u0026 Miquelon","PN":"Pitcairn Islands","PR":"Puerto Rico","PS":"Palestinian Territories","PT":"Portugal","PW":"Palau","PY":"Paraguay","QA":"Qatar","RE":"Réunion","RO":"Romania","RS":"Serbia","RU":"Russia","RW":"Rwanda","SA":"Saudi Arabia","SB":"Solomon Islands","SC":"Seychelles","SD":"Sudan","SE":"Sweden","SG":"Singapore","SH":"St. Helena","SI":"Slovenia","SJ":"Svalbard \u0026 Jan Mayen","SK":"Slovakia","SL":"Sierra Leone","SM":"San Marino","SN":"Senegal","SO":"Somalia","SR":"Suriname","SS":"South Sudan","ST":"São Tomé \u0026 Príncipe","SV":"El Salvador","SX":"Sint Maarten","SY":"Syria","SZ":"Swaziland","TA":"Tristan da Cunha","TC":"Turks \u0026 Caicos Islands","TD":"Chad","TF":"French Southern Territories","TG":"Togo","TH":"Thailand","TJ":"Tajikistan","TK":"Tokelau","TL":"Timor-Leste","TM":"Turkmenistan","TN":"Tunisia","TO":"Tonga","TR":"Turkey","TT":"Trinidad \u0026 Tobago","TV":"Tuvalu","TW":"Taiwan","TZ":"Tanzania","UA":"Ukraine","UG":"Uganda","UM":"U.S. Outlying Islands","US":"United States","UY":"Uruguay","UZ":"Uzbekistan","VA":"Vatican City","VC":"St. Vincent \u0026 Grenadines","VE":"Venezuela","VG":"British Virgin Islands","VI":"U.S. Virgin Islands","VN":"Vietnam","VU":"Vanuatu","WF":"Wallis \u0026 Futuna","WS":"Samoa","XK":"Kosovo","YE":"Yemen","YT":"Mayotte","ZA":"South Africa","ZM":"Zambia","ZW":"Zimbabwe","ZZ":"Unknown Region"}}]}`},
		{"zh-Hans-CN", `{"response":{"code":200,"message":"OK"},"data":[{"language":"zh-Hans","defaultRegionCode":"CN","territories":{"AC":"阿森松岛","AD":"安道尔","AE":"阿拉伯联合酋长国","AF":"阿富汗","AG":"安提瓜和巴布达","AI":"安圭拉","AL":"阿尔巴尼亚","AM":"亚美尼亚","AO":"安哥拉","AQ":"南极洲","AR":"阿根廷","AS":"美属萨摩亚","AT":"奥地利","AU":"澳大利亚","AW":"阿鲁巴","AX":"奥兰群岛","AZ":"阿塞拜疆","BA":"波斯尼亚和黑塞哥维那","BB":"巴巴多斯","BD":"孟加拉国","BE":"比利时","BF":"布基纳法索","BG":"保加利亚","BH":"巴林","BI":"布隆迪","BJ":"贝宁","BL":"圣巴泰勒米","BM":"百慕大","BN":"文莱","BO":"玻利维亚","BQ":"荷属加勒比区","BR":"巴西","BS":"巴哈马","BT":"不丹","BV":"布韦岛","BW":"博茨瓦纳","BY":"白俄罗斯","BZ":"伯利兹","CA":"加拿大","CC":"科科斯（基林）群岛","CD":"刚果（金）","CF":"中非共和国","CG":"刚果（布）","CH":"瑞士","CI":"科特迪瓦","CK":"库克群岛","CL":"智利","CM":"喀麦隆","CN":"中国","CO":"哥伦比亚","CP":"克利珀顿岛","CR":"哥斯达黎加","CU":"古巴","CV":"佛得角","CW":"库拉索","CX":"圣诞岛","CY":"塞浦路斯","CZ":"捷克","DE":"德国","DG":"迪戈加西亚岛","DJ":"吉布提","DK":"丹麦","DM":"多米尼克","DO":"多米尼加共和国","DZ":"阿尔及利亚","EA":"休达及梅利利亚","EC":"厄瓜多尔","EE":"爱沙尼亚","EG":"埃及","EH":"西撒哈拉","ER":"厄立特里亚","ES":"西班牙","ET":"埃塞俄比亚","FI":"芬兰","FJ":"斐济","FK":"福克兰群岛","FM":"密克罗尼西亚","FO":"法罗群岛","FR":"法国","GA":"加蓬","GB":"英国","GD":"格林纳达","GE":"格鲁吉亚","GF":"法属圭亚那","GG":"根西岛","GH":"加纳","GI":"直布罗陀","GL":"格陵兰","GM":"冈比亚","GN":"几内亚","GP":"瓜德罗普","GQ":"赤道几内亚","GR":"希腊","GS":"南乔治亚和南桑威奇群岛","GT":"危地马拉","GU":"关岛","GW":"几内亚比绍","GY":"圭亚那","HK":"中国香港特别行政区","HM":"赫德岛和麦克唐纳群岛","HN":"洪都拉斯","HR":"克罗地亚","HT":"海地","HU":"匈牙利","IC":"加纳利群岛","ID":"印度尼西亚","IE":"爱尔兰","IL":"以色列","IM":"马恩岛","IN":"印度","IO":"英属印度洋领地","IQ":"伊拉克","IR":"伊朗","IS":"冰岛","IT":"意大利","JE":"泽西岛","JM":"牙买加","JO":"约旦","JP":"日本","KE":"肯尼亚","KG":"吉尔吉斯斯坦","KH":"柬埔寨","KI":"基里巴斯","KM":"科摩罗","KN":"圣基茨和尼维斯","KP":"朝鲜","KR":"韩国","KW":"科威特","KY":"开曼群岛","KZ":"哈萨克斯坦","LA":"老挝","LB":"黎巴嫩","LC":"圣卢西亚","LI":"列支敦士登","LK":"斯里兰卡","LR":"利比里亚","LS":"莱索托","LT":"立陶宛","LU":"卢森堡","LV":"拉脱维亚","LY":"利比亚","MA":"摩洛哥","MC":"摩纳哥","MD":"摩尔多瓦","ME":"黑山","MF":"法属圣马丁","MG":"马达加斯加","MH":"马绍尔群岛","MK":"马其顿","ML":"马里","MM":"缅甸","MN":"蒙古","MO":"中国澳门特别行政区","MP":"北马里亚纳群岛","MQ":"马提尼克","MR":"毛里塔尼亚","MS":"蒙特塞拉特","MT":"马耳他","MU":"毛里求斯","MV":"马尔代夫","MW":"马拉维","MX":"墨西哥","MY":"马来西亚","MZ":"莫桑比克","NA":"纳米比亚","NC":"新喀里多尼亚","NE":"尼日尔","NF":"诺福克岛","NG":"尼日利亚","NI":"尼加拉瓜","NL":"荷兰","NO":"挪威","NP":"尼泊尔","NR":"瑙鲁","NU":"纽埃","NZ":"新西兰","OM":"阿曼","PA":"巴拿马","PE":"秘鲁","PF":"法属波利尼西亚","PG":"巴布亚新几内亚","PH":"菲律宾","PK":"巴基斯坦","PL":"波兰","PM":"圣皮埃尔和密克隆群岛","PN":"皮特凯恩群岛","PR":"波多黎各","PS":"巴勒斯坦领土","PT":"葡萄牙","PW":"帕劳","PY":"巴拉圭","QA":"卡塔尔","RE":"留尼汪","RO":"罗马尼亚","RS":"塞尔维亚","RU":"俄罗斯","RW":"卢旺达","SA":"沙特阿拉伯","SB":"所罗门群岛","SC":"塞舌尔","SD":"苏丹","SE":"瑞典","SG":"新加坡","SH":"圣赫勒拿","SI":"斯洛文尼亚","SJ":"斯瓦尔巴和扬马延","SK":"斯洛伐克","SL":"塞拉利昂","SM":"圣马力诺","SN":"塞内加尔","SO":"索马里","SR":"苏里南","SS":"南苏丹","ST":"圣多美和普林西比","SV":"萨尔瓦多","SX":"荷属圣马丁","SY":"叙利亚","SZ":"斯威士兰","TA":"特里斯坦-达库尼亚群岛","TC":"特克斯和凯科斯群岛","TD":"乍得","TF":"法属南部领地","TG":"多哥","TH":"泰国","TJ":"塔吉克斯坦","TK":"托克劳","TL":"东帝汶","TM":"土库曼斯坦","TN":"突尼斯","TO":"汤加","TR":"土耳其","TT":"特立尼达和多巴哥","TV":"图瓦卢","TW":"台湾","TZ":"坦桑尼亚","UA":"乌克兰","UG":"乌干达","UM":"美国本土外小岛屿","US":"美国","UY":"乌拉圭","UZ":"乌兹别克斯坦","VA":"梵蒂冈","VC":"圣文森特和格林纳丁斯","VE":"委内瑞拉","VG":"英属维尔京群岛","VI":"美属维尔京群岛","VN":"越南","VU":"瓦努阿图","WF":"瓦利斯和富图纳","WS":"萨摩亚","XK":"科索沃","YE":"也门","YT":"马约特","ZA":"南非","ZM":"赞比亚","ZW":"津巴布韦","ZZ":"未知地区"}}]}`},
		{"zh-Hans", `{"response":{"code":200,"message":"OK"},"data":[{"language":"zh-Hans","defaultRegionCode":"CN","territories":{"AC":"阿森松岛","AD":"安道尔","AE":"阿拉伯联合酋长国","AF":"阿富汗","AG":"安提瓜和巴布达","AI":"安圭拉","AL":"阿尔巴尼亚","AM":"亚美尼亚","AO":"安哥拉","AQ":"南极洲","AR":"阿根廷","AS":"美属萨摩亚","AT":"奥地利","AU":"澳大利亚","AW":"阿鲁巴","AX":"奥兰群岛","AZ":"阿塞拜疆","BA":"波斯尼亚和黑塞哥维那","BB":"巴巴多斯","BD":"孟加拉国","BE":"比利时","BF":"布基纳法索","BG":"保加利亚","BH":"巴林","BI":"布隆迪","BJ":"贝宁","BL":"圣巴泰勒米","BM":"百慕大","BN":"文莱","BO":"玻利维亚","BQ":"荷属加勒比区","BR":"巴西","BS":"巴哈马","BT":"不丹","BV":"布韦岛","BW":"博茨瓦纳","BY":"白俄罗斯","BZ":"伯利兹","CA":"加拿大","CC":"科科斯（基林）群岛","CD":"刚果（金）","CF":"中非共和国","CG":"刚果（布）","CH":"瑞士","CI":"科特迪瓦","CK":"库克群岛","CL":"智利","CM":"喀麦隆","CN":"中国","CO":"哥伦比亚","CP":"克利珀顿岛","CR":"哥斯达黎加","CU":"古巴","CV":"佛得角","CW":"库拉索","CX":"圣诞岛","CY":"塞浦路斯","CZ":"捷克","DE":"德国","DG":"迪戈加西亚岛","DJ":"吉布提","DK":"丹麦","DM":"多米尼克","DO":"多米尼加共和国","DZ":"阿尔及利亚","EA":"休达及梅利利亚","EC":"厄瓜多尔","EE":"爱沙尼亚","EG":"埃及","EH":"西撒哈拉","ER":"厄立特里亚","ES":"西班牙","ET":"埃塞俄比亚","FI":"芬兰","FJ":"斐济","FK":"福克兰群岛","FM":"密克罗尼西亚","FO":"法罗群岛","FR":"法国","GA":"加蓬","GB":"英国","GD":"格林纳达","GE":"格鲁吉亚","GF":"法属圭亚那","GG":"根西岛","GH":"加纳","GI":"直布罗陀","GL":"格陵兰","GM":"冈比亚","GN":"几内亚","GP":"瓜德罗普","GQ":"赤道几内亚","GR":"希腊","GS":"南乔治亚和南桑威奇群岛","GT":"危地马拉","GU":"关岛","GW":"几内亚比绍","GY":"圭亚那","HK":"中国香港特别行政区","HM":"赫德岛和麦克唐纳群岛","HN":"洪都拉斯","HR":"克罗地亚","HT":"海地","HU":"匈牙利","IC":"加纳利群岛","ID":"印度尼西亚","IE":"爱尔兰","IL":"以色列","IM":"马恩岛","IN":"印度","IO":"英属印度洋领地","IQ":"伊拉克","IR":"伊朗","IS":"冰岛","IT":"意大利","JE":"泽西岛","JM":"牙买加","JO":"约旦","JP":"日本","KE":"肯尼亚","KG":"吉尔吉斯斯坦","KH":"柬埔寨","KI":"基里巴斯","KM":"科摩罗","KN":"圣基茨和尼维斯","KP":"朝鲜","KR":"韩国","KW":"科威特","KY":"开曼群岛","KZ":"哈萨克斯坦","LA":"老挝","LB":"黎巴嫩","LC":"圣卢西亚","LI":"列支敦士登","LK":"斯里兰卡","LR":"利比里亚","LS":"莱索托","LT":"立陶宛","LU":"卢森堡","LV":"拉脱维亚","LY":"利比亚","MA":"摩洛哥","MC":"摩纳哥","MD":"摩尔多瓦","ME":"黑山","MF":"法属圣马丁","MG":"马达加斯加","MH":"马绍尔群岛","MK":"马其顿","ML":"马里","MM":"缅甸","MN":"蒙古","MO":"中国澳门特别行政区","MP":"北马里亚纳群岛","MQ":"马提尼克","MR":"毛里塔尼亚","MS":"蒙特塞拉特","MT":"马耳他","MU":"毛里求斯","MV":"马尔代夫","MW":"马拉维","MX":"墨西哥","MY":"马来西亚","MZ":"莫桑比克","NA":"纳米比亚","NC":"新喀里多尼亚","NE":"尼日尔","NF":"诺福克岛","NG":"尼日利亚","NI":"尼加拉瓜","NL":"荷兰","NO":"挪威","NP":"尼泊尔","NR":"瑙鲁","NU":"纽埃","NZ":"新西兰","OM":"阿曼","PA":"巴拿马","PE":"秘鲁","PF":"法属波利尼西亚","PG":"巴布亚新几内亚","PH":"菲律宾","PK":"巴基斯坦","PL":"波兰","PM":"圣皮埃尔和密克隆群岛","PN":"皮特凯恩群岛","PR":"波多黎各","PS":"巴勒斯坦领土","PT":"葡萄牙","PW":"帕劳","PY":"巴拉圭","QA":"卡塔尔","RE":"留尼汪","RO":"罗马尼亚","RS":"塞尔维亚","RU":"俄罗斯","RW":"卢旺达","SA":"沙特阿拉伯","SB":"所罗门群岛","SC":"塞舌尔","SD":"苏丹","SE":"瑞典","SG":"新加坡","SH":"圣赫勒拿","SI":"斯洛文尼亚","SJ":"斯瓦尔巴和扬马延","SK":"斯洛伐克","SL":"塞拉利昂","SM":"圣马力诺","SN":"塞内加尔","SO":"索马里","SR":"苏里南","SS":"南苏丹","ST":"圣多美和普林西比","SV":"萨尔瓦多","SX":"荷属圣马丁","SY":"叙利亚","SZ":"斯威士兰","TA":"特里斯坦-达库尼亚群岛","TC":"特克斯和凯科斯群岛","TD":"乍得","TF":"法属南部领地","TG":"多哥","TH":"泰国","TJ":"塔吉克斯坦","TK":"托克劳","TL":"东帝汶","TM":"土库曼斯坦","TN":"突尼斯","TO":"汤加","TR":"土耳其","TT":"特立尼达和多巴哥","TV":"图瓦卢","TW":"台湾","TZ":"坦桑尼亚","UA":"乌克兰","UG":"乌干达","UM":"美国本土外小岛屿","US":"美国","UY":"乌拉圭","UZ":"乌兹别克斯坦","VA":"梵蒂冈","VC":"圣文森特和格林纳丁斯","VE":"委内瑞拉","VG":"英属维尔京群岛","VI":"美属维尔京群岛","VN":"越南","VU":"瓦努阿图","WF":"瓦利斯和富图纳","WS":"萨摩亚","XK":"科索沃","YE":"也门","YT":"马约特","ZA":"南非","ZM":"赞比亚","ZW":"津巴布韦","ZZ":"未知地区"}}]}`},
		{"zh-CN", `{"response":{"code":200,"message":"OK"},"data":[{"language":"zh-Hans","defaultRegionCode":"CN","territories":{"AC":"阿森松岛","AD":"安道尔","AE":"阿拉伯联合酋长国","AF":"阿富汗","AG":"安提瓜和巴布达","AI":"安圭拉","AL":"阿尔巴尼亚","AM":"亚美尼亚","AO":"安哥拉","AQ":"南极洲","AR":"阿根廷","AS":"美属萨摩亚","AT":"奥地利","AU":"澳大利亚","AW":"阿鲁巴","AX":"奥兰群岛","AZ":"阿塞拜疆","BA":"波斯尼亚和黑塞哥维那","BB":"巴巴多斯","BD":"孟加拉国","BE":"比利时","BF":"布基纳法索","BG":"保加利亚","BH":"巴林","BI":"布隆迪","BJ":"贝宁","BL":"圣巴泰勒米","BM":"百慕大","BN":"文莱","BO":"玻利维亚","BQ":"荷属加勒比区","BR":"巴西","BS":"巴哈马","BT":"不丹","BV":"布韦岛","BW":"博茨瓦纳","BY":"白俄罗斯","BZ":"伯利兹","CA":"加拿大","CC":"科科斯（基林）群岛","CD":"刚果（金）","CF":"中非共和国","CG":"刚果（布）","CH":"瑞士","CI":"科特迪瓦","CK":"库克群岛","CL":"智利","CM":"喀麦隆","CN":"中国","CO":"哥伦比亚","CP":"克利珀顿岛","CR":"哥斯达黎加","CU":"古巴","CV":"佛得角","CW":"库拉索","CX":"圣诞岛","CY":"塞浦路斯","CZ":"捷克","DE":"德国","DG":"迪戈加西亚岛","DJ":"吉布提","DK":"丹麦","DM":"多米尼克","DO":"多米尼加共和国","DZ":"阿尔及利亚","EA":"休达及梅利利亚","EC":"厄瓜多尔","EE":"爱沙尼亚","EG":"埃及","EH":"西撒哈拉","ER":"厄立特里亚","ES":"西班牙","ET":"埃塞俄比亚","FI":"芬兰","FJ":"斐济","FK":"福克兰群岛","FM":"密克罗尼西亚","FO":"法罗群岛","FR":"法国","GA":"加蓬","GB":"英国","GD":"格林纳达","GE":"格鲁吉亚","GF":"法属圭亚那","GG":"根西岛","GH":"加纳","GI":"直布罗陀","GL":"格陵兰","GM":"冈比亚","GN":"几内亚","GP":"瓜德罗普","GQ":"赤道几内亚","GR":"希腊","GS":"南乔治亚和南桑威奇群岛","GT":"危地马拉","GU":"关岛","GW":"几内亚比绍","GY":"圭亚那","HK":"中国香港特别行政区","HM":"赫德岛和麦克唐纳群岛","HN":"洪都拉斯","HR":"克罗地亚","HT":"海地","HU":"匈牙利","IC":"加纳利群岛","ID":"印度尼西亚","IE":"爱尔兰","IL":"以色列","IM":"马恩岛","IN":"印度","IO":"英属印度洋领地","IQ":"伊拉克","IR":"伊朗","IS":"冰岛","IT":"意大利","JE":"泽西岛","JM":"牙买加","JO":"约旦","JP":"日本","KE":"肯尼亚","KG":"吉尔吉斯斯坦","KH":"柬埔寨","KI":"基里巴斯","KM":"科摩罗","KN":"圣基茨和尼维斯","KP":"朝鲜","KR":"韩国","KW":"科威特","KY":"开曼群岛","KZ":"哈萨克斯坦","LA":"老挝","LB":"黎巴嫩","LC":"圣卢西亚","LI":"列支敦士登","LK":"斯里兰卡","LR":"利比里亚","LS":"莱索托","LT":"立陶宛","LU":"卢森堡","LV":"拉脱维亚","LY":"利比亚","MA":"摩洛哥","MC":"摩纳哥","MD":"摩尔多瓦","ME":"黑山","MF":"法属圣马丁","MG":"马达加斯加","MH":"马绍尔群岛","MK":"马其顿","ML":"马里","MM":"缅甸","MN":"蒙古","MO":"中国澳门特别行政区","MP":"北马里亚纳群岛","MQ":"马提尼克","MR":"毛里塔尼亚","MS":"蒙特塞拉特","MT":"马耳他","MU":"毛里求斯","MV":"马尔代夫","MW":"马拉维","MX":"墨西哥","MY":"马来西亚","MZ":"莫桑比克","NA":"纳米比亚","NC":"新喀里多尼亚","NE":"尼日尔","NF":"诺福克岛","NG":"尼日利亚","NI":"尼加拉瓜","NL":"荷兰","NO":"挪威","NP":"尼泊尔","NR":"瑙鲁","NU":"纽埃","NZ":"新西兰","OM":"阿曼","PA":"巴拿马","PE":"秘鲁","PF":"法属波利尼西亚","PG":"巴布亚新几内亚","PH":"菲律宾","PK":"巴基斯坦","PL":"波兰","PM":"圣皮埃尔和密克隆群岛","PN":"皮特凯恩群岛","PR":"波多黎各","PS":"巴勒斯坦领土","PT":"葡萄牙","PW":"帕劳","PY":"巴拉圭","QA":"卡塔尔","RE":"留尼汪","RO":"罗马尼亚","RS":"塞尔维亚","RU":"俄罗斯","RW":"卢旺达","SA":"沙特阿拉伯","SB":"所罗门群岛","SC":"塞舌尔","SD":"苏丹","SE":"瑞典","SG":"新加坡","SH":"圣赫勒拿","SI":"斯洛文尼亚","SJ":"斯瓦尔巴和扬马延","SK":"斯洛伐克","SL":"塞拉利昂","SM":"圣马力诺","SN":"塞内加尔","SO":"索马里","SR":"苏里南","SS":"南苏丹","ST":"圣多美和普林西比","SV":"萨尔瓦多","SX":"荷属圣马丁","SY":"叙利亚","SZ":"斯威士兰","TA":"特里斯坦-达库尼亚群岛","TC":"特克斯和凯科斯群岛","TD":"乍得","TF":"法属南部领地","TG":"多哥","TH":"泰国","TJ":"塔吉克斯坦","TK":"托克劳","TL":"东帝汶","TM":"土库曼斯坦","TN":"突尼斯","TO":"汤加","TR":"土耳其","TT":"特立尼达和多巴哥","TV":"图瓦卢","TW":"台湾","TZ":"坦桑尼亚","UA":"乌克兰","UG":"乌干达","UM":"美国本土外小岛屿","US":"美国","UY":"乌拉圭","UZ":"乌兹别克斯坦","VA":"梵蒂冈","VC":"圣文森特和格林纳丁斯","VE":"委内瑞拉","VG":"英属维尔京群岛","VI":"美属维尔京群岛","VN":"越南","VU":"瓦努阿图","WF":"瓦利斯和富图纳","WS":"萨摩亚","XK":"科索沃","YE":"也门","YT":"马约特","ZA":"南非","ZM":"赞比亚","ZW":"津巴布韦","ZZ":"未知地区"}}]}`},
	} {
		d := d
		t.Run(d.input, func(t *testing.T) {
			resp := e.GET(GetRegionsOfLanguagesURL).WithQuery("supportedLanguageList", d.input).Expect()

			resp.Status(http.StatusOK)

			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}

func TestGetPatternByLangRegExcep(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		testName, lang, reg, scope, scopeFilter string
		wantedCode                              int
	}{
		{testName: "notFoundError", lang: "en-US", reg: "AB", scope: "dates", scopeFilter: "", wantedCode: http.StatusNotFound},
		{testName: "invalidLang", lang: "€", reg: Region, scope: "dates", scopeFilter: "", wantedCode: http.StatusBadRequest},
		{testName: "invalidRegion", lang: Language, reg: "€", scope: "dates", scopeFilter: "", wantedCode: http.StatusBadRequest},
		{testName: "invalidScope", lang: Language, reg: Region, scope: "€", scopeFilter: "", wantedCode: http.StatusBadRequest},
		{testName: "invalidScopeFilter", lang: Language, reg: Region, scope: "dates", scopeFilter: "€", wantedCode: http.StatusOK},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			req := e.GET(GetPatternByLangRegURL).WithQueryObject(
				map[string]interface{}{
					"language":    tt.lang,
					"region":      tt.reg,
					"scope":       tt.scope,
					"scopeFilter": tt.scopeFilter})
			resp := req.Expect()

			resp.Status(tt.wantedCode)
		})
	}
}

func TestGetPatternByLocaleExcep(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		testName, locale, scope, scopeFilter string
		wantedCode                           int
	}{
		{testName: "Locale_en-AB", locale: "en-AB", scope: "dates", scopeFilter: "", wantedCode: http.StatusNotFound},
		{testName: "invalidLocale", locale: "€", scope: "dates", scopeFilter: "", wantedCode: http.StatusBadRequest},
		{testName: "invalidScope", locale: Language, scope: "€", scopeFilter: "", wantedCode: http.StatusBadRequest},
		{testName: "invalidScopeFilter", locale: Language, scope: "dates", scopeFilter: "€", wantedCode: http.StatusOK},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			req := e.GET(GetPatternByLocaleURL, tt.locale).WithQueryObject(
				map[string]interface{}{
					"scope":       tt.scope,
					"scopeFilter": tt.scopeFilter})
			resp := req.Expect()

			resp.Status(tt.wantedCode)
		})
	}
}

func TestGetRegionsExcep(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, tt := range []struct {
		testName, input string
		wantedCode      int
	}{
		{testName: "invalidLanguage-1", input: "€", wantedCode: http.StatusBadRequest},
		{testName: "invalidLanguage-2", input: "zh,€,jp", wantedCode: http.StatusBadRequest},
		{testName: "notFoundLanguage", input: "ABC,EDF", wantedCode: http.StatusNotFound},
	} {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			resp := e.GET(GetRegionsOfLanguagesURL).WithQuery("supportedLanguageList", tt.input).Expect()
			resp.Status(tt.wantedCode)
		})
	}
}
