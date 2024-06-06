export class ModelLineGraphOptions {
    constructor() {
        this.series = [];
    }
    //* ============================================
    // データの設定
    //* ============================================
    addDataSeries(title, values) {
        this.series.push({
            name: title,
            data: values
        });
    }
}