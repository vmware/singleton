/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net/http"
	"testing"

	_ "sgtnserver/api/v2/countryflag"

	"github.com/stretchr/testify/assert"
)

const (
	getFlagURL = BaseURL + "/image/countryFlag"
)

func TestCountryFlag(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		testName          string
		region, imageType string
		scale             int
		wantedHTTPCode    int
		wantedBody        string
	}{
		{testName: "Normal Successful. Default scale is 1, default type is json.", region: Region, wantedHTTPCode: http.StatusOK,
			wantedBody: `{"response":{"code":200,"message":"OK"},"data":{"image":"\u003c?xml version=\"1.0\" encoding=\"utf-8\"?\u003e\n\u003csvg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"59.85 0 342 342\"\u003e\n\u003crect y=\"0\" fill=\"#FFFFFF\" width=\"513\" height=\"342\"/\u003e\n\u003cg fill=\"#D80027\"\u003e\n\t\u003crect y=\"0\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"52.6\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"105.2\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"157.8\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"210.5\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"263.1\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"315.7\" width=\"513\" height=\"26.3\"/\u003e\n\u003c/g\u003e\n\u003crect fill=\"#2E52B2\" width=\"256.5\" height=\"184.1\"/\u003e\n\u003cg fill=\"#FFFFFF\"\u003e\n\t\u003cpolygon points=\"47.8,138.9 43.8,126.1 39.4,138.9 26.2,138.9 36.9,146.6 32.9,159.4 43.8,151.5 54.4,159.4\n\t\t50.3,146.6 61.2,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"104.1,138.9 100,126.1 95.8,138.9 82.6,138.9 93.3,146.6 89.3,159.4 100,151.5 110.8,159.4\n\t\t106.8,146.6 117.5,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"160.6,138.9 156.3,126.1 152.3,138.9 138.8,138.9 149.8,146.6 145.6,159.4 156.3,151.5 167.3,159.4\n\t\t163.1,146.6 173.8,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"216.8,138.9 212.8,126.1 208.6,138.9 195.3,138.9 206.1,146.6 202.1,159.4 212.8,151.5 223.6,159.4\n\t\t219.3,146.6 230.3,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"100,75.3 95.8,88.1 82.6,88.1 93.3,96 89.3,108.6 100,100.8 110.8,108.6 106.8,96 117.5,88.1\n\t\t104.1,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"43.8,75.3 39.4,88.1 26.2,88.1 36.9,96 32.9,108.6 43.8,100.8 54.4,108.6 50.3,96 61.2,88.1\n\t\t47.8,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"156.3,75.3 152.3,88.1 138.8,88.1 149.8,96 145.6,108.6 156.3,100.8 167.3,108.6 163.1,96 173.8,88.1\n\t\t160.6,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"212.8,75.3 208.6,88.1 195.3,88.1 206.1,96 202.1,108.6 212.8,100.8 223.6,108.6 219.3,96 230.3,88.1\n\t\t216.8,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"43.8,24.7 39.4,37.3 26.2,37.3 36.9,45.2 32.9,57.9 43.8,50 54.4,57.9 50.3,45.2 61.2,37.3 47.8,37.3\n\t\t\t\"/\u003e\n\t\u003cpolygon points=\"100,24.7 95.8,37.3 82.6,37.3 93.3,45.2 89.3,57.9 100,50 110.8,57.9 106.8,45.2 117.5,37.3\n\t\t104.1,37.3 \t\"/\u003e\n\t\u003cpolygon points=\"156.3,24.7 152.3,37.3 138.8,37.3 149.8,45.2 145.6,57.9 156.3,50 167.3,57.9 163.1,45.2 173.8,37.3\n\t\t160.6,37.3 \t\"/\u003e\n\t\u003cpolygon points=\"212.8,24.7 208.6,37.3 195.3,37.3 206.1,45.2 202.1,57.9 212.8,50 223.6,57.9 219.3,45.2 230.3,37.3\n\t\t216.8,37.3 \t\"/\u003e\n\u003c/g\u003e\n\u003c/svg\u003e\n","region":"US","type":"svg"}}`},
		{testName: "Normal Successful. to test cache", region: Region, wantedHTTPCode: http.StatusOK,
			wantedBody: `{"response":{"code":200,"message":"OK"},"data":{"image":"\u003c?xml version=\"1.0\" encoding=\"utf-8\"?\u003e\n\u003csvg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"59.85 0 342 342\"\u003e\n\u003crect y=\"0\" fill=\"#FFFFFF\" width=\"513\" height=\"342\"/\u003e\n\u003cg fill=\"#D80027\"\u003e\n\t\u003crect y=\"0\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"52.6\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"105.2\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"157.8\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"210.5\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"263.1\" width=\"513\" height=\"26.3\"/\u003e\n\t\u003crect y=\"315.7\" width=\"513\" height=\"26.3\"/\u003e\n\u003c/g\u003e\n\u003crect fill=\"#2E52B2\" width=\"256.5\" height=\"184.1\"/\u003e\n\u003cg fill=\"#FFFFFF\"\u003e\n\t\u003cpolygon points=\"47.8,138.9 43.8,126.1 39.4,138.9 26.2,138.9 36.9,146.6 32.9,159.4 43.8,151.5 54.4,159.4\n\t\t50.3,146.6 61.2,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"104.1,138.9 100,126.1 95.8,138.9 82.6,138.9 93.3,146.6 89.3,159.4 100,151.5 110.8,159.4\n\t\t106.8,146.6 117.5,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"160.6,138.9 156.3,126.1 152.3,138.9 138.8,138.9 149.8,146.6 145.6,159.4 156.3,151.5 167.3,159.4\n\t\t163.1,146.6 173.8,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"216.8,138.9 212.8,126.1 208.6,138.9 195.3,138.9 206.1,146.6 202.1,159.4 212.8,151.5 223.6,159.4\n\t\t219.3,146.6 230.3,138.9 \t\"/\u003e\n\t\u003cpolygon points=\"100,75.3 95.8,88.1 82.6,88.1 93.3,96 89.3,108.6 100,100.8 110.8,108.6 106.8,96 117.5,88.1\n\t\t104.1,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"43.8,75.3 39.4,88.1 26.2,88.1 36.9,96 32.9,108.6 43.8,100.8 54.4,108.6 50.3,96 61.2,88.1\n\t\t47.8,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"156.3,75.3 152.3,88.1 138.8,88.1 149.8,96 145.6,108.6 156.3,100.8 167.3,108.6 163.1,96 173.8,88.1\n\t\t160.6,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"212.8,75.3 208.6,88.1 195.3,88.1 206.1,96 202.1,108.6 212.8,100.8 223.6,108.6 219.3,96 230.3,88.1\n\t\t216.8,88.1 \t\"/\u003e\n\t\u003cpolygon points=\"43.8,24.7 39.4,37.3 26.2,37.3 36.9,45.2 32.9,57.9 43.8,50 54.4,57.9 50.3,45.2 61.2,37.3 47.8,37.3\n\t\t\t\"/\u003e\n\t\u003cpolygon points=\"100,24.7 95.8,37.3 82.6,37.3 93.3,45.2 89.3,57.9 100,50 110.8,57.9 106.8,45.2 117.5,37.3\n\t\t104.1,37.3 \t\"/\u003e\n\t\u003cpolygon points=\"156.3,24.7 152.3,37.3 138.8,37.3 149.8,45.2 145.6,57.9 156.3,50 167.3,57.9 163.1,45.2 173.8,37.3\n\t\t160.6,37.3 \t\"/\u003e\n\t\u003cpolygon points=\"212.8,24.7 208.6,37.3 195.3,37.3 206.1,45.2 202.1,57.9 212.8,50 223.6,57.9 219.3,45.2 230.3,37.3\n\t\t216.8,37.3 \t\"/\u003e\n\u003c/g\u003e\n\u003c/svg\u003e\n","region":"US","type":"svg"}}`},
		{testName: "Specify Values", region: "CN", scale: 2, imageType: "svg", wantedHTTPCode: http.StatusOK,
			wantedBody: "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 513 342\">\n<rect fill=\"#D80027\" width=\"513\" height=\"342\"/>\n<g fill=\"#FFDA44\">\n\t<polygon points=\"226.8,239.2 217.1,223.6 199.2,228 211.1,213.9 201.4,198.3 218.5,205.2 230.3,191.1 229,209.5\n\t\t246.1,216.4 228.2,220.8 \t\"/>\n\t<polygon points=\"290.6,82 280.5,97.4 292.1,111.7 274.4,106.9 264.3,122.4 263.3,104 245.6,99.2 262.8,92.6\n\t\t261.8,74.2 273.4,88.5 \t\"/>\n\t<polygon points=\"236.2,25.4 234.2,43.7 251,51.3 233,55.1 231,73.4 221.8,57.4 203.9,61.2 216.2,47.5 207,31.6\n\t\t223.8,39.1 \t\"/>\n\t<polygon points=\"292.8,161.8 277.9,172.7 283.7,190.2 268.8,179.4 253.9,190.4 259.5,172.8 244.6,162.1 263,162\n\t\t268.6,144.4 274.4,161.9 \t\"/>\n  <polygon points=\"115,46.3 132.3,99.8 188.5,99.8 143.1,132.7 160.4,186.2 115,153.2 69.5,186.2 86.9,132.7 41.4,99.8\n\t97.7,99.8 \"/>\n</g>\n</svg>\n"},
		{testName: "nonexistent region", region: "xxx", wantedHTTPCode: http.StatusOK,
			wantedBody: "{\"response\":{\"code\":400,\"message\":\"the flag for region 'xxx' and scale '1x1' is unavailable\"}}"},
		{testName: "invalid scale", region: Region, scale: 3, wantedHTTPCode: http.StatusOK,
			wantedBody: `{"response":{"code":400,"message":"Scale must be one of [1 2]"}}`},
		{testName: "invalid type", region: Region, imageType: "abc", wantedHTTPCode: http.StatusOK,
			wantedBody: `{"response":{"code":400,"message":"Type must be one of [json svg]"}}`},
	} {
		d := d
		t.Run(fmt.Sprintf("%s.Region:%s,scale:%v,type:%v", d.testName, d.region, d.scale, d.imageType), func(t *testing.T) {
			req := e.GET(getFlagURL).WithQuery("region", d.region)
			if d.scale != 0 {
				req = req.WithQuery("scale", d.scale)
			}
			if d.imageType != "" {
				req = req.WithQuery("type", d.imageType)
			}
			resp := req.Expect()
			resp.Status(d.wantedHTTPCode)
			assert.Equal(t, d.wantedBody, resp.Body().Raw())
		})
	}
}
