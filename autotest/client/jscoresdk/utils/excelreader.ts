import xlsx from 'node-xlsx';


export class ExcelReader {
    private file: string;

    constructor(file: string) {
        this.file = file;
    }

    read(sheetname: string) {
        const workSheetsFromFile = xlsx.parse(this.file);
        for (const i in workSheetsFromFile) {
            let sheet = workSheetsFromFile[i];
            if (sheet.name == sheetname) {
                return sheet.data;
            }
        }
    }

    readsheet_with_sheetname(sheetname: string) {
        var data = null;
        const workSheetsFromFile = xlsx.parse(this.file);
        for (const i in workSheetsFromFile) {
            let sheet = workSheetsFromFile[i];
            if (sheet.name == sheetname) {
                data = sheet.data;
                break;
            }
        }

        return this.convertdata(data);
    }

    readsheet_with_sheetindex(i: number) {
        const workSheetsFromFile = xlsx.parse(this.file);
        return this.convertdata(workSheetsFromFile[i].data);
    }

    private convertdata(data) {
        var parseddata = [];
        if (data.length > 0) {
            var columnheader = data[0];
        }
        for (let i = 1; i < data.length; i++) {
            let item = data[i];
            if (item.length == 0) {
                continue;
            }

            var oneline = {};
            for (let j = 0; j < item.length && j < columnheader.length; j++) {
                oneline[columnheader[j]] = item[j];
            }

            parseddata.push(oneline);
        }

        return parseddata;
    }
}

