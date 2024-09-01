import moment from "moment";

export class LineGraphOptions {
    constructor() {
        this.subtitles = [];
        // * 仮の基本設定
        this.data = {
            interval: null,
            chartOptions: {
                chart: {
                    type: 'line',
                    stacked: false,
                    height: 500,
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
                            // * インターバルの設定がある場合（生データグラフである場合）
                            if (this.data.interval != null) {
                                chartContext.updateOptions({
                                    xaxis: {
                                        tickAmount: this.setTickamount(xaxis.min, xaxis.max),
                                        type: 'category',  // 追加: typeを再設定
                                        categories: chartContext.opts.xaxis.categories  // 追加: カテゴリーも再設定

                                    }
                                });
                            }
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
                    tickAmount: 12, // 365日の12分割
                    axisTicks: { show: true, },
                    title: {
                        text: '',
                        offsetY: -20,
                    },
                    labels: {
                        formatter: function (value) {
                            // * インターバルの設定がある場合（生データグラフである場合）
                            if (this.data.interval != null) {
                                if (typeof value === "undefined") {
                                    return "";
                                } else {
                                    // 日付と時刻を分割して2段表示
                                    const date = value.split(' ')[0];  // 日付部分
                                    const time = value.split(' ')[1];  // 時刻部分
                                    return [date, time]
                                }

                            } else
                                return value;
                        }.bind(this),
                        style: {
                            fontSize: '12px',
                            colors: [],  // 必要に応じて色を設定
                            whiteSpace: 'pre'  // 改行文字を適用するためにpreを使用
                        },
                        offsetY: 5  // ラベルを少し下にオフセット
                    },
                    /*    labels: {
                            formatter: function (val) {
                                return moment(val).format(this.dateFormat);
                            }.bind(this),
                        },*/
                },
                tooltip: {
                    shared: false,
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
    // グラフのタイトルを設定する
    //* ============================================
    setDateFormat(format) {
        this.dateFormat = format;
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
    setInterval(interval, min, max) {
        this.data.interval = interval;
        this.data.chartOptions.xaxis.tickAmount = this.setTickamount(min, max);
    }
    //* ============================================
    // tickamountを設定する
    //* ============================================
    setTickamount(min, max) {
        // * 表示されるX軸目盛の数
        let showNumber = max - min;
        // * 表示され目盛数が示す時間幅(分)
        let time = this.data.interval * showNumber;
        // * 一日以内
        if (time <= 1440) return 24;
        // * ２日以内
        else if (time <= 2880) return time / 120;
        // * ２週間以内
        else if (time <= 20160) return time / 720;
        // * ひと月以内
        else if (time <= 44640) return time / 1440;
        else {
            return time / 1440;
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