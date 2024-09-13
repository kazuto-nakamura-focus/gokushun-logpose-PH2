import moment from "moment";

export class LineGraphOptions {
    constructor() {
        this.subtitles = [];
        // * 仮の基本設定
        this.data = {
            dataIndex: 0,
            flags: null,
            labelFlag: null,

            interval: null,
            zoomed: {
                max: 0,
                min: 0,
                diff: 0,
                termby12: 0,
            },
            chartOptions: {
                chart: {
                    type: 'line',
                    stacked: false,
                    height: 500,
                    //      margin: 40,
                    animations: {
                        enabled: false,
                    },
                    zoom: {
                        type: 'x',
                        enabled: true,
                        autoScaleYaxis: false
                    },
                    events: {
                        zoomed: function (chartContext, { xaxis }) {
                            this.data.dataIndex = xaxis.min;
                            let zoomed = this.data.zoomed;
                            zoomed.max = xaxis.max;
                            zoomed.min = xaxis.min;
                            zoomed.diff = xaxis.max - xaxis.min;
                            // * (注)生データ
                            if (this.data.interval != null) {
                                zoomed.termby12 = (zoomed.diff * this.data.interval) / 1440;
                            }
                            // * モデルデータ
                            else {
                                zoomed.termby12 = zoomed.diff;
                            }
                            chartContext.updateOptions({
                                xaxis: {
                                    labels: {
                                        formatter: function (value) {
                                            let label = this.data.chartOptions.xaxis.categories[value];
                                            return this.setLabel(label);
                                        }.bind(this)
                                    }
                                }
                            });
                        }.bind(this)
                    },
                    toolbar: {
                        autoSelected: 'zoom'
                    },
                },
                dataLabels: {
                    enabled: false
                },
                colors: [],
                markers: {
                    size: 0,
                },
                stroke: {
                    width: 2,
                    curve: 'smooth',
                    //        dashArray: [0, 3]
                },
                title: {
                    text: undefined,
                    align: 'center',
                    style: {
                        fontSize: '14px',
                        fontWeight: 'bold',
                        fontFamily: "Helvetica, Arial, sans-serif",
                        color: '#263238'
                    },
                },
                subtitle: {
                    text: undefined,
                    align: 'center',
                    margin: 10,
                    offsetX: 0,
                    offsetY: 30,
                    floating: false,
                    style: {
                        fontSize: '10pt',
                        fontWeight: 'normal',
                        fontFamily: "Helvetica, Arial, sans-serif",
                        color: '#FF6666'
                    },
                },
                fill: {
                    colors: []
                },
                yaxis: {
                    labels: {
                        formatter: function (val) {
                            return Math.round(val * 100) / 100;
                        },
                    },
                    title: {
                        text: '累積F値'
                    },
                },
                xaxis: {
                    categories: [],
                    type: 'category',
                    //tickAmount: 12, // 365日の12分割
                    axisTicks: { show: true, },
                    title: {
                        text: '',
                        offsetY: -20,
                    },
                    labels: {
                        rotate: 0,
                        style: {
                            fontSize: '10pt',
                            colors: [],  // 必要に応じて色を設定
                        },
                        offsetY: 0,  // ラベルを少し下にオフセット
                        formatter: function (value) {
                            return this.setLabel(value);
                        }.bind(this),
                    },
                },
                tooltip: {
                    enabled: true,  // ツールチップを有効にする
                    shared: false,  // ポイントごとにツールチップを表示
                    intersect: false,  // マウスオーバーしたポイントのみに表示
                    x: {
                        formatter: function (value) {
                            // ツールチップではすべての値を表示
                            return this.data.chartOptions.xaxis.categories[value];
                        }.bind(this)
                    },
                    y: {
                        formatter: function (val) {
                            return val;
                        }
                    },

                },
                annotations: {
                    yaxis: [],
                    xaxis: []
                },
            }
        }
    }
    //* ============================================
    // オブジェクトリファレンス
    //* ============================================
    getChartOptions() { return this.data.chartOptions; }
    //* ============================================
    // グラフのタイトルを設定する
    //* ============================================
    setGraphTitle(title) {
        this.data.chartOptions.title.text = title;
    }
    //* ============================================
    // サブタイトルを追加する
    //* ============================================
    addSubTitleElement(name, value) {
        value = Math.round(value * 100) / 100;
        this.subtitles.push(name + " " + value);
    }
    //* ============================================
    // サブタイトルを作成する
    //* ============================================
    createSubtitle() {
        let text = "";
        for (const item of this.subtitles) {
            if (text.length > 0) text += " / ";
            text += item;
        }
        if (text.length > 0) {
            let yesterday = new moment().add(-1, 'day').format("(MM月DD日現在)");
            this.data.chartOptions.subtitle.text = text + " " + yesterday;
        }
    }
    //* ============================================
    // X軸のタイトルを作成する
    //* ============================================   
    setXScaleTitle(xtitle) { this.data.chartOptions.xaxis.title.text = xtitle; }
    //* ============================================
    // Y軸のタイトルを作成する
    //* ============================================ 
    setYScaleTitle(ytitle) { this.data.chartOptions.yaxis.title.text = ytitle; }
    //* ============================================
    // インターバルを設定する
    //* ============================================ 
    setInterval(interval, min, max, labelFlag) {
        this.data.flags = labelFlag;
        this.data.interval = interval;
        this.data.zoomed.max = max;
        this.data.zoomed.min = min;
        this.data.zoomed.diff = max - min;
        // * (注)生データ
        if (interval != null) {
            this.data.zoomed.termby12 = (this.data.zoomed.diff * interval) / 1440;
            this.data.labelFlag = this.#setlabelFlag(this.data.zoomed.termby12);
        }
        // * モデルデータ
        else {
            this.data.zoomed.termby12 = this.data.zoomed.diff;
        }
    }
    //* ============================================
    // ラベルフラグの設定
    //* ============================================
    #setlabelFlag(termby12) {
        // 一年以上
        if (termby12 > 182) {
            return 2048;
        }
        //* ６か月以上 15日間隔（３０日を除く）
        else if (termby12 > 121) {
            return 1024;
        }
        // * ２か月以上 10日間隔（３０日を除く）
        else if (termby12 > 60) {
            return 512;
        }
        // * 12日以上 ５日間隔（３０日を除く）
        else if (termby12 > 12) {
            return 256; s
        }
        // * 6日以上　一日おき
        else if (termby12 > 6) {
            return 128;
        }
        // * 3日以上 十二時間おき
        else if (termby12 > 3) {
            return 64;
        }
        // * ２日以上、六時間おき
        else if (termby12 > 2) {
            return 32;
        }
        // * 一日以上 四時間おき
        else if (termby12 > 1) {
            return 16;
        }
        // * 半日以上
        else if (termby12 > 0.5) {
            return 8;
        }
        // * １時間単位：12時間以下
        else return 2;
    }
}
//* ============================================
// X軸の表示ラベルの設定
//* ============================================
#pickupCategory(label) {
    let termby12 = this.data.zoomed.termby12;
    if (typeof label === "undefined") {
        return "";
    }
    let value = new String(label);
    // console.log("diff", diff, value);
    // * 共通・最初
    // if (min == 0) return value;
    // * インターバルが無い場合（モデルグラフ）
    if (null == this.data.interval) {
        // * 共通・月初
        if (value.endsWith("01")) return value;
        // * ５か月以上１０か月以内
        else {
            // * 30日単位
            if (termby12 >= 182) {
                return "";
            }
            //* 15日単位：６か月以下
            else if (termby12 > 121) {
                if (value.endsWith("15")) {
                    //  console.log("90p", value)
                    return value;
                }
            }
            // * 10日単位：４か月以下
            else if (termby12 > 60) {
                if (!value.endsWith("30") && value.endsWith("0")) {
                    return value;
                }
            }
            //
            // * 5日単位：２か月以下
            else if (termby12 > 24) {
                if (!value.endsWith("30") && (value.endsWith("5") || value.endsWith("0"))) {
                    return value;
                }
            }
            // ２日単位：２４日以下
            else if (termby12 > 12) {
                // 末尾の2文字を取得
                const dayString = value.substring(8, 10);  // "23"
                // 数値に変換して奇数かチェック
                if (parseInt(dayString, 10) % 2 !== 0) return value;
            }
            // * 12日以下
            else return value;
        }
        return "";
    } else {
        // * 共通・月初
        if (value.endsWith("01 00:00")) return value;
        else {
            // * 1か月単位：１年以下
            if (termby12 > 182) {
                return ""
            }
            //* 15日単位 : ６か月以下 
            else if (termby12 > 121) {
                if (value.endsWith("15 00:00")) return value;
            }
            // * 30日を除く10日単位：120日以下
            else if (termby12 > 60) {
                if (value.endsWith("0 00:00")) return value;
            }
            // * 30日を除く５日単位：６０日以下
            else if (termby12 > 12) {
                if (!value.endsWith("30 00:00") && (value.endsWith("5 00:00") || value.endsWith("0 00:00"))) return value;
            }
            // * 1日単位：12日以下
            else if (termby12 > 6) {
                if (value.endsWith("00:00") || value.endsWith("12:00")) return value;
            }
            // * 12時間単位：６日以下
            else if (termby12 > 3) {
                if (value.endsWith("00:00") || value.endsWith("12:00")) return value;
            }
            // * ６時間単位:３日以下
            else if (termby12 > 2) {
                const hourString = value.substring(11, 13);
                const hour = parseInt(hourString, 10);
                if (hour % 6 === 0) return value;
            }
            // * ４時間単位；2日以下
            else if (termby12 > 1) {
                const hourString = value.substring(11, 13);
                const hour = parseInt(hourString, 10);
                if (hour % 4 === 0) return value;
            }
            // * ２時間単位：２４時間以下
            else if (termby12 > 0.5) {
                const hourString = value.substring(11, 13);
                const hour = parseInt(hourString, 10);
                if (hour % 2 === 0) return value;
            }
            // * １時間単位：12時間以下
            else {
                if (value.endsWith(":00")) return value;
            }
        }
        return "";
    }
}
//* ============================================
// X軸の表示ラベルの設定
//* ============================================
setLabel(label) {
    let newValue = this.#pickupCategory(label);

    if (this.data.interval != null) {
        if (newValue.length > 0) {
            // 日付と時刻を分割して2段表示
            const date = newValue.split(' ')[0];  // 日付部分
            const time = newValue.split(' ')[1];  // 時刻部分
            return [date, time]
        }
    } else {
        if (newValue.length > 0) {
            // 日付と時刻を分割して2段表示
            let str = newValue.split('/')
            return [str[0], str[1] + "/" + str[2]]
        }
    }
    return newValue;
}
//* ============================================
// 複合線グラフの宣言をする
//* ============================================
declareMultiGraph() {
    this.data.chartOptions.yaxis = [];
}
//* ============================================
// 複合線グラフのY軸を設定する
//* ============================================
declareYScaleTitle(title, max, opposite) {
    let unit = this.#setStep(max);
    let step = this.#setMax(max, unit);
    const item = {
        opposite: opposite,
        //   seriseriesName: title,
        title: {
            text: title,
        },
        stepSize: unit,
        max: step,
        labels: {
            formatter: function (val) {
                return Math.round(val * 100) / 100;
            },
        },
    }
    this.data.chartOptions.yaxis.push(item);
}
//* ============================================
// 複合線グラフのY軸を追加する
//* ============================================
addYScaleTitle(title) {
    console.log(title);
    const item = {
        //          seriesName: title,
        show: false
    };
    this.data.chartOptions.yaxis.push(item);
}

//* ============================================
// Y軸の逆タイトルを作成する
//* ============================================ 
setYScaleOppositeTitle(nameA, titleA, maxA, nameB, titleB, maxB) {
    let unitA = this.#setStep(maxA);
    let stepA = this.#setMax(maxA, unitA);
    let unitB = this.#setStep(maxB);
    let stepB = this.#setMax(maxB, unitB);

    this.data.chartOptions.yaxis = [{
        //  seriesName: "nameA",
        title: {
            text: titleA,
        },
        stepSize: unitA,
        max: stepA,
        labels: {
            formatter: function (val) {
                return Math.round(val * 100) / 100;
            },
        },

    }, {
        opposite: true,
        //     seriesName: "nameB",
        title: {
            text: titleB,
        },
        stepSize: unitB,
        max: stepB,
        labels: {
            formatter: function (val) {
                return Math.round(val * 100) / 100;
            },
        },
    }];
}
// ======================================================
// X軸のカテゴリー設定
// ======================================================
setXCategory(category) { this.data.chartOptions.xaxis.categories = category; }
//* ============================================
// X軸アノテーションの設定 
//* ============================================
setXAnnotations(annotations) { this.data.chartOptions.annotations.xaxis = annotations; }
//* ============================================
// Y軸アノテーションの設定 
//* ============================================
setYAnnotations(annotations) { this.data.chartOptions.annotations.yaxis = annotations; }
// ======================================================
// Y軸の目盛りの値
// ======================================================
setYScale(max) {
    // this.data.chartOptions.yaxis.min = 0;
    let unit = this.#setStep(max);
    this.data.chartOptions.yaxis.stepSize = unit;
    this.data.chartOptions.yaxis.max = this.#setMax(max, unit);
}
// ======================================================
// 色の指定の追加
// ======================================================
addColor(color) {
    this.data.chartOptions.colors.push(color);
}
// ======================================================
// Y軸の目盛りの最大値設定する
// ======================================================
#setMax(max, unit) {
    let value = Math.trunc(max / unit) + 1;
    return unit * value + unit;
}
// ======================================================
// Y軸の目盛りのステップサイズを設定する
// ======================================================
#setStep(max) {
    if (max >= 0) {
        // 桁数の算出
        let numberDigit = String(Math.floor(max)).length;
        // 2桁に変更する
        let tmp = max / Math.pow(10, (numberDigit - 2));
        // 目盛単位の設定
        let scale = 0;
        if ((tmp >= 10) && (tmp < 20)) scale = 1;
        else if ((tmp >= 20) && (tmp < 40)) scale = 2;
        else scale = 5;
        // 単位を戻して返却
        return scale * Math.pow(10, (numberDigit - 2));

    } else {
        // 数値を文字列に変換
        let numStr = max.toString();
        // 小数点の位置を見つける
        let decimalIndex = numStr.indexOf('.');
        // 小数点以下の部分を取得
        let decimalPart = numStr.slice(decimalIndex + 1);
        let numberDigit = 0;
        for (; numberDigit < decimalPart.length; numberDigit++) {
            if (decimalPart[numberDigit] !== '0') numberDigit++;
        }
        // 2桁に変更する
        let tmp = max * Math.pow(10, (numberDigit + 1));
        // 目盛単位の設定
        let scale = 0;
        if ((tmp >= 10) && (tmp < 20)) scale = 1;
        else if ((tmp >= 20) && (tmp < 40)) scale = 2;
        else scale = 5;
        // 単位を戻して返却
        return scale / Math.pow(10, (numberDigit - 1));
    }
}
}