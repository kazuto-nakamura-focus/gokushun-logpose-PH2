export class LabelFlags {
    constructor() {
        this.TEN = 1;         // 10分おき
        this.THIRTY = 2;      // 30分おき
        this.HOUR = 4;        // 1時間おき
        this.HOUR2 = 8;       // 2時間おき
        this.HOUR4 = 16;      // 4時間おき
        this.HOUR6 = 32;      // 6時間おき
        this.HOUR12 = 64;     // 12時間おき
        this.HOUR_0 = this.HOUR | this.HOUR2 | this.HOUR4 | this.HOUR6 | this.HOUR12;
        this.DAY = 128;       // 1日おき
        this.DAYS5 = 256;     // 5日間隔（30日を除く）
        this.DAYS10 = 512;    // 10日間隔（30日を除く）
        this.DAYS15 = 1024;   // 15日間隔（30日を除く）
        this.MONTH = 2048;    // 1月間隔
        this.DAY_1 = this.DAY | this.DAYS5 | this.DAYS10 | this.DAYS15 | this.MONTH;
        this.flags = [];
        this.category = [];

    }

    // ある期間内のセンサーのデータを返す
    getSensorGraphDataByInterval(startDate, endDate, minutes) {
        // 検索の開始時刻の設定
        let startTime = new Date(startDate);
        startTime.setUTCHours(0, 0, 0, 0);  // HOUR_OF_DAY, MINUTE, SECOND, MILLISECONDを0にセット

        let seekTime = startTime.getTime();

        // 検索の終了時刻の設定
        let endTime = new Date(endDate);
        endTime.setUTCDate(endTime.getUTCDate() + 1);
        endTime.setUTCHours(0, 0, 0, 0);
        let endTimeMillis = endTime.getTime();

        // インターバルは分単位なので、ミリ秒に変換する
        let interval = 60000 * minutes;
        let cal = new Date();
        while (seekTime < endTimeMillis) {
            // 時刻フラグ
            cal.setTime(seekTime);
            // 日付を "MM/dd hh:mm" 形式で追加 (UTC)
            let formattedDate = this.formatDateUTC(cal);
            this.category.push(formattedDate);  // 日付をdaysに追加
            let flag = this.getFlag(cal);
            this.flags.push(flag);
            seekTime += interval;
        }
    }

    // 非公開関数
    getFlag(cal) {
        let minutes = cal.getUTCMinutes();
        if (minutes !== 0) {
            if (minutes === 30) {
                return this.THIRTY;
            } else {
                return 0;
            }
        }

        let flag = 6;  // THIRTY|HOUR
        let hour = cal.getUTCHours();
        if (hour === 0) {  // 0時
            flag |= this.HOUR_0;
        } else {
            flag |= this.HOUR;
            if (hour % 2 === 0) flag |= this.HOUR2;
            if (hour % 4 === 0) flag |= this.HOUR4;
            if (hour % 6 === 0) flag |= this.HOUR6;
            if (hour % 12 === 0) flag |= this.HOUR12;
            return flag;
        }

        let date = cal.getUTCDate();
        if (date === 1) {  // 1日
            return flag | this.DAY_1;
        } else {
            flag |= this.DAY;
            if (date !== 30) {
                if (date % 5 === 0) flag |= this.DAYS5;
                if (date % 10 === 0) flag |= this.DAYS10;
                if (date % 15 === 0) flag |= this.DAYS15;
            }
            return flag;
        }
    }
    formatDateUTC(cal) {
        let month = (cal.getUTCMonth() + 1).toString().padStart(2, '0');  // 月 (0埋め)
        let day = cal.getUTCDate().toString().padStart(2, '0');            // 日 (0埋め)
        let hours = cal.getUTCHours().toString().padStart(2, '0');         // 時 (0埋め)
        let minutes = cal.getUTCMinutes().toString().padStart(2, '0');     // 分 (0埋め)
        return `${month}/${day} ${hours}:${minutes}`;                      // "MM/dd hh:mm" 形式で返す
    }
}
