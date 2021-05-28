/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
enum TimeUnit {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    MONTH,
    YEAR
}
enum SecondsOfUnit {
    SECOND = 1, // 1
    MINUTE = 60, // 1 * 60
    HOUR = 3600, // 1 * 60 * 60
    DAY = 86400, // 1 * 60 * 60 * 24
    MONTH = 86400 * ( 146097 / 4800  ), // 1 * 60 * 60 * 24 * (days of month)
    YEAR = SecondsOfUnit.MONTH * 12
}

enum THRESHODS {
    SECOND = 45,
    MINUTE = 45,
    HOUR = 22,
    DAY = 26,
    MONTH = 11
}

export interface TimeOffset {
    unit: string;
    offset: number;
}

export class RelativeTimeFormatter {
    public getOffset( from: Date, to: Date ): TimeOffset {
        const start = from.getTime();
        const end = to.getTime();
        const ms = end - start;
        const offset = ms / 1000;
        const offsetOfSeconds = Math.floor( Math.abs( offset ) );

        const unit = this.getUnit( offsetOfSeconds );
        const offsetOfUnit = this.getOffsetOfUnit(offsetOfSeconds, unit);
        return {
            unit: TimeUnit[unit].toLocaleLowerCase(),
            offset: offset > 0 ? offsetOfUnit : -1 * offsetOfUnit
        };
    }

    // Calculate the best time unit based on the difference and use the threshold limit range
    private getUnit( offset: number ): TimeUnit {
        let unit: TimeUnit;
        if ( offset <  SecondsOfUnit.MINUTE && this.isOverThreshod(offset, TimeUnit.SECOND)) {
            unit = TimeUnit.SECOND;
        } else if ( offset <  SecondsOfUnit.HOUR && this.isOverThreshod(offset, TimeUnit.MINUTE)) {
            unit = TimeUnit.MINUTE;
        } else if ( offset <  SecondsOfUnit.DAY && this.isOverThreshod(offset, TimeUnit.HOUR)) {
            unit = TimeUnit.HOUR;
        } else if ( offset <  SecondsOfUnit.MONTH && this.isOverThreshod(offset, TimeUnit.DAY)) {
            unit = TimeUnit.DAY;
        } else if ( offset <  SecondsOfUnit.YEAR && this.isOverThreshod(offset, TimeUnit.MONTH)) {
            unit = TimeUnit.MONTH;
        } else {
            unit = TimeUnit.YEAR;
        }
        return unit;
    }

    // Check whether the offset of 'unit' is within the threshold range
    private isOverThreshod( offset: number, unit: TimeUnit): boolean {
        const offsetOfUnit = this.getOffsetOfUnit(offset, unit);
        return offsetOfUnit < THRESHODS[ TimeUnit[unit] ];
    }

    private getOffsetOfUnit( offset: number, unit: TimeUnit) {
        const offsetOfUnit = Math.round( offset / SecondsOfUnit[ TimeUnit[unit]] );
        return offsetOfUnit;
    }
}

