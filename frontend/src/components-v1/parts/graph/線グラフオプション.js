import moment from "moment";

export class LineGraphOptions {
    constructor() {
        this.subtitles = [];
        // * 仮の基本設定
        this.data = {
            flags: null,        // * 表示フラグのリスト
            labelFlag: null,    // * 表示フラグ
            interval: null,
            showIndex: 0,
            showMode: false,
            oldValue: null,
            firstCategory: null,
            zoomed: {
                max: 0,
                min: 0,
                diff: 0,
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
                            let zoomed = this.data.zoomed;
                            zoomed.max = xaxis.max;
                            zoomed.min = xaxis.min;
                            zoomed.diff = xaxis.max - xaxis.min;
                            this.setIntervalFlag(this.data.interval);
                            chartContext.updateOptions({
                                xaxis: {
                                    labels: {
                                        formatter: function (value) {
                                            if (this.checkFlag(value)) {
                                                return this.formatLabel(this.data.chartOptions.xaxis.categories[value]);
                                            }
                                            else return "";
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
                    categories: null,
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
                            if (value === undefined) {
                                this.showMode = true;
                                return "";
                            } else if (!this.showMode) {
                                return "";
                            } else {
                                if (this.data.firstCategory != null) {
                                    if (this.data.firstCategory !== value) {
                                        return "";
                                    } else {
                                        this.data.firstCategory = null;
                                    }
                                }
                                if (this.data.oldValue != null) {
                                    let returnValue = this.data.oldValue;
                                    this.data.oldValue = null;
                                    if (this.data.showIndex == this.data.chartOptions.xaxis.categories.length) {
                                        this.showMode = false;
                                    }
                                    return returnValue;
                                } else {
                                    if (this.checkFlag(this.data.showIndex++)) {
                                        this.data.oldValue = this.formatLabel(value);
                                    } else {
                                        this.data.oldValue = "";
                                    }
                                    return this.data.oldValue;
                                }

                            }
                        }.bind(this),
                    },
                },
                tooltip: {
                    enabled: true,  // ツールチップを有効にする
                    shared: true,  // ポイントごとにツールチップを表示
                    intersect: false,  // マウスオーバーしたポイントのみに表示
                    position: 'topRight',
                    offsetX: 10, // ツールチップをX軸方向に10pxシフト
                    offsetY: 10,  // ツールチップをY軸方向に20pxシフト
                    x: {
                        formatter: function (value) {
                            // ツールチップではすべての値を表示
                            return this.data.chartOptions.xaxis.categories[value - 1];
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
    // インターバルフラグを設定する
    //* ============================================
    setIntervalFlag(interval) {
        // * (注)生データ
        if (interval != null) {
            let termby12 = (this.data.zoomed.diff * interval) / 1440;
            this.data.labelFlag = this.setSensorlabelFlag(termby12);
        }
        // * モデルデータ
        else {
            this.data.labelFlag = this.setModellabelFlag(this.data.zoomed.diff);
        }
    }
    //* ============================================
    // インターバルを設定する
    //* ============================================ 
    setInterval(interval, min, max, labelFlag) {
        this.data.interval = interval;
        this.data.flags = labelFlag;
        this.data.showIndex = 0;
        this.data.zoomed.max = max;
        this.data.zoomed.min = min;
        this.data.zoomed.diff = max - min;
        this.setIntervalFlag(interval)
    }
    //* ============================================
    // センサーラベルフラグの設定
    //* ============================================
    setSensorlabelFlag(termby12) {
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
            return 256;
        }
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
    //* ============================================
    // センサーラベルフラグの設定
    //* ============================================
    setModellabelFlag(termby12) {
        // * インターバルが無い場合（モデルグラフ）
        if (termby12 >= 182) {
            return 2048;
        }
        //* 15日単位：６か月以下
        else if (termby12 > 121) {
            return 1024;
        }
        // * 10日単位：４か月以下
        else if (termby12 > 60) {
            return 512;
        }
        // * 5日単位：２か月以下
        else if (termby12 > 24) {
            return 256;
        }
        // ２日単位：２４日以下
        else if (termby12 > 12) {
            return 128;
        }
        // * 12日以下
        else return 64;

    }
    //* ============================================
    // X軸のラベルを表示するかどうかチェックする
    //* ============================================
    checkFlag(index) {
        if (index == -1) return false;
        return (this.data.labelFlag & this.data.flags[index]) > 0;
    }
    //* ============================================
    // X軸の表示ラベルの設定
    //* ============================================
    formatLabel(value) {
        // 生データの場合
        if (this.data.interval != null) {
            // 日付と時刻を分割して2段表示
            const date = value.split(' ')[0];  // 日付部分
            const time = value.split(' ')[1];  // 時刻部分
            return [date, time]

        }
        // モデルデータの場合
        else {
            let str = value.split('/')
            return [str[0], str[1] + "/" + str[2]]
        }
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
    setXCategory(category) {
        this.data.chartOptions.xaxis.categories = category;
        this.data.firstCategory = category[0];
    }
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