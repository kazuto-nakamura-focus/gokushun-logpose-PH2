export class GraphDataSeries {
    constructor() {
        this.series = [];
    }
    //* ============================================
    // オブジェクトリファレンス
    //* ============================================
    getSeries() { return this.series; }
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